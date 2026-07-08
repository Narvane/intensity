import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { UsersRound } from 'lucide-react';
import { ApiError, createApiClient } from '@adapters/api/ApiClient';
import { useAppLogout } from '@app/useAppLogout';
import { useToast } from '@app/ToastProvider';
import { useNavigation } from '@app/NavigationProvider';
import { useSession } from '@app/SessionProvider';
import type { Group, GroupAccent } from '@domain/box/boxTypes';
import { resolveGroupDisplayName } from '@domain/box/resolveGroupDisplayName';
import { CreateGroupUseCase, ListGroupsUseCase } from '@domain/box/boxUseCases';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import { AppLoader } from '../components/AppLoader';
import { NavButton } from '../components/NavButton';
import { ScreenHeader } from '../components/ScreenHeader';
import { ScreenTitle } from '../components/ScreenTitle';
import { SessionModeFooter } from '../components/SessionModeFooter';
import { resolveGroupAccent } from '../components/groupVisuals';
import { GroupFormDialog } from './GroupFormDialog';
import styles from './GroupSelectionPage.module.css';

export function GroupSelectionPage() {
  const { t } = useI18n();
  const { experiencesSession } = useSession();
  const { showToast } = useToast();
  const logout = useAppLogout('EXPERIENCES');
  const navigate = useNavigate();
  const { setNavigation } = useNavigation();
  const api = useMemo(() => createApiClient(), []);
  const listGroups = useMemo(() => new ListGroupsUseCase(api), [api]);
  const createGroup = useMemo(() => new CreateGroupUseCase(api), [api]);

  const [groups, setGroups] = useState<Group[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [createOpen, setCreateOpen] = useState(false);
  const [creating, setCreating] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);

  const loadGroups = useCallback(async () => {
    if (!experiencesSession?.token) {
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const items = await listGroups.execute(experiencesSession.token);
      setGroups(items);
    } catch (err: unknown) {
      setError(err instanceof ApiError ? err.message : t('common.error'));
    } finally {
      setLoading(false);
    }
  }, [listGroups, experiencesSession?.token, t]);

  useEffect(() => {
    void loadGroups();
  }, [loadGroups]);

  const confirmCreate = async (input: { name: string; color: GroupAccent }) => {
    if (!experiencesSession?.token) {
      return;
    }

    setCreating(true);
    setCreateError(null);

    try {
      const created = await createGroup.execute(experiencesSession.token, input);
      setGroups((current) => [created, ...current.filter((item) => item.id !== created.id)]);
      setCreateOpen(false);
      showToast(t('groups.createSuccess'));
    } catch (err) {
      setCreateError(err instanceof ApiError ? err.message : t('common.error'));
    } finally {
      setCreating(false);
    }
  };

  return (
    <>
    <main className={styles.page}>
      <ScreenHeader
        leading={
          <NavButton action="back" onClick={() => navigate('/auth')} />
        }
        trailing={<NavButton action="logout" onClick={() => void logout()} />}
      >
        <ScreenTitle>{t('groups.title')}</ScreenTitle>
      </ScreenHeader>

      {!loading && !error && (
        <div className={styles.toolbar}>
          <Button onClick={() => {
            setCreateError(null);
            setCreateOpen(true);
          }}>
            {t('groups.create')}
          </Button>
        </div>
      )}

      {loading && <AppLoader label={t('common.loading')} />}

      {error && (
        <p className={styles.error} role="alert">
          {error}
        </p>
      )}

      {!loading && !error && groups.length === 0 && (
        <section className={styles.empty}>
          <p>{t('groups.empty')}</p>
          <p className={styles.emptyHint}>{t('groups.emptyHint')}</p>
        </section>
      )}

      {!loading && !error && groups.length > 0 && (
        <ul className={styles.list}>
          {groups.map((group) => {
            const accent = resolveGroupAccent(group);
            const displayName = resolveGroupDisplayName(group, t);

            return (
              <li key={group.id} className={styles.item}>
                <button
                  type="button"
                  className={styles.row}
                  data-accent={accent}
                  onClick={() => {
                    void setNavigation({ groupId: group.id }).then(() => {
                      navigate(`/groups/${group.id}/boxes`);
                    });
                  }}
                >
                  <span className={styles.rowIcon} aria-hidden="true">
                    <UsersRound size={26} strokeWidth={2.25} />
                  </span>
                  <span className={styles.rowCopy}>
                    <span className={styles.rowTitle}>{displayName}</span>
                    <span className={styles.rowMeta}>
                      {t('groups.memberCount', { count: group.memberCount })}
                      {' · '}
                      {t('groups.openBoxes')}
                    </span>
                  </span>
                </button>
              </li>
            );
          })}
        </ul>
      )}

      <GroupFormDialog
        open={createOpen}
        mode="create"
        saving={creating}
        error={createError}
        onConfirm={(input) => void confirmCreate(input)}
        onCancel={() => {
          if (!creating) {
            setCreateOpen(false);
            setCreateError(null);
          }
        }}
      />
    </main>
      <SessionModeFooter
        mode="EXPERIENCES"
        participantDisplayName={experiencesSession?.displayName}
      />
    </>
  );
}
