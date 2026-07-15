import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Pencil, UsersRound } from 'lucide-react';
import { ApiError, createApiClient } from '@adapters/api/ApiClient';
import { useAppLogout } from '@app/useAppLogout';
import { useToast } from '@app/ToastProvider';
import { useNavigation } from '@app/NavigationProvider';
import { useSession } from '@app/SessionProvider';
import type { Group, GroupAccent } from '@domain/box/boxTypes';
import { resolveGroupDisplayName } from '@domain/box/resolveGroupDisplayName';
import {
  CreateGroupUseCase,
  ListGroupsUseCase,
  UpdateGroupUseCase,
} from '@domain/box/boxUseCases';
import { useI18n } from '../../../i18n/I18nContext';
import { StartDrawSessionModal } from '../boxes/StartDrawSessionModal';
import { Button } from '../../components/controls/Button';
import { AppLoader } from '../../components/feedback/AppLoader';
import { NavButton } from '../../components/controls/NavButton';
import { ScreenHeader } from '../../components/chrome/ScreenHeader';
import { ScreenTitle } from '../../components/chrome/ScreenTitle';
import { SessionModeFooter } from '../../components/chrome/SessionModeFooter';
import { resolveGroupAccent } from '../../components/collection/groupVisuals';
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
  const updateGroup = useMemo(() => new UpdateGroupUseCase(api), [api]);

  const [groups, setGroups] = useState<Group[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [createOpen, setCreateOpen] = useState(false);
  const [creating, setCreating] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);
  const [groupToEdit, setGroupToEdit] = useState<Group | null>(null);
  const [groupToManage, setGroupToManage] = useState<Group | null>(null);
  const [savingGroup, setSavingGroup] = useState(false);
  const [editError, setEditError] = useState<string | null>(null);

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

  const confirmEdit = async (input: { name: string; color: GroupAccent }) => {
    if (!experiencesSession?.token || !groupToEdit) {
      return;
    }

    setSavingGroup(true);
    setEditError(null);

    try {
      const updated = await updateGroup.execute(
        groupToEdit.id,
        experiencesSession.token,
        input,
      );
      setGroups((current) =>
        current.map((group) => (group.id === updated.id ? updated : group)),
      );
      setGroupToEdit(null);
      showToast(t('groups.editSuccess'));
    } catch (err) {
      setEditError(err instanceof ApiError ? err.message : t('common.error'));
    } finally {
      setSavingGroup(false);
    }
  };

  return (
    <>
      <main className={styles.page}>
        <ScreenHeader
          leading={<NavButton action="back" onClick={() => navigate('/auth')} />}
          trailing={<NavButton action="logout" onClick={() => void logout()} />}
        >
          <ScreenTitle>{t('groups.title')}</ScreenTitle>
        </ScreenHeader>

        {!loading && !error && (
          <div className={styles.toolbar}>
            <Button
              onClick={() => {
                setCreateError(null);
                setCreateOpen(true);
              }}
            >
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
                  <div className={styles.card} data-accent={accent}>
                    <button
                      type="button"
                      className={styles.row}
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
                    <div className={styles.cardActions}>
                      <button
                        type="button"
                        className={styles.actionButton}
                        aria-label={t('groups.manage')}
                        onClick={() => setGroupToManage(group)}
                      >
                        <UsersRound size={18} strokeWidth={2.25} aria-hidden />
                      </button>
                      <button
                        type="button"
                        className={styles.actionButton}
                        aria-label={t('groups.editDialog.title')}
                        onClick={() => {
                          setEditError(null);
                          setGroupToEdit(group);
                        }}
                      >
                        <Pencil size={18} strokeWidth={2.25} aria-hidden />
                      </button>
                    </div>
                  </div>
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

        {groupToEdit && (
          <GroupFormDialog
            open
            mode="edit"
            initialName={groupToEdit.name}
            initialColor={resolveGroupAccent(groupToEdit)}
            saving={savingGroup}
            error={editError}
            onConfirm={(input) => void confirmEdit(input)}
            onCancel={() => {
              if (!savingGroup) {
                setGroupToEdit(null);
                setEditError(null);
              }
            }}
          />
        )}

        <StartDrawSessionModal
          open={groupToManage !== null}
          mode="manage"
          box={null}
          groupId={groupToManage?.id ?? ''}
          members={groupToManage?.members ?? []}
          onClose={() => setGroupToManage(null)}
        />
      </main>
      <SessionModeFooter
        mode="EXPERIENCES"
        participantDisplayName={experiencesSession?.displayName}
      />
    </>
  );
}
