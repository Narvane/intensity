import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Pencil } from 'lucide-react';
import { ApiError, createApiClient } from '@adapters/api/ApiClient';
import { useAppLogout } from '@app/useAppLogout';
import { useToast } from '@app/ToastProvider';
import { useNavigation } from '@app/NavigationProvider';
import { useSession } from '@app/SessionProvider';
import type { Box, Group, GroupAccent } from '@domain/box/boxTypes';
import { resolveGroupDisplayName } from '@domain/box/resolveGroupDisplayName';
import { resolveSessionGroupIds } from '@domain/box/sessionGroups';
import {
  DeleteBoxUseCase,
  LeaveGroupUseCase,
  ListBoxesUseCase,
  ListGroupsUseCase,
  UpdateBoxUseCase,
  UpdateGroupUseCase,
} from '@domain/box/boxUseCases';
import { useI18n } from '../../../i18n/I18nContext';
import { ShareInviteSheet } from '../../invite/ShareInviteSheet';
import { LeaveGroupDialog } from '../../collection/groups/LeaveGroupDialog';
import { GroupBoxesSection } from '../../collection/groups/GroupBoxesSection';
import { GroupFormDialog } from '../../collection/groups/GroupFormDialog';
import { GroupHeading } from '../../collection/groups/GroupHeading';
import { GroupSelector } from '../../collection/groups/GroupSelector';
import { BoxCard } from '../../components/collection/BoxCard';
import { AppLoader } from '../../components/feedback/AppLoader';
import { Button } from '../../components/controls/Button';
import { NavButton } from '../../components/controls/NavButton';
import { ScreenHeader } from '../../components/chrome/ScreenHeader';
import { SessionModeFooter } from '../../components/chrome/SessionModeFooter';
import { resolveGroupAccent } from '../../components/collection/groupVisuals';
import { DeleteBoxDialog } from './DeleteBoxDialog';
import { EditBoxDialog } from './EditBoxDialog';
import styles from './BoxHomePage.module.css';

export function BoxHomePage() {
  const { t } = useI18n();
  const { experienceBoxSession } = useSession();
  const { navigation, setNavigation, clearNavigation } = useNavigation();
  const { showToast } = useToast();
  const logout = useAppLogout('EXPERIENCE_BOX');
  const navigate = useNavigate();
  const api = useMemo(() => createApiClient(), []);
  const listBoxes = useMemo(() => new ListBoxesUseCase(api), [api]);
  const listGroups = useMemo(() => new ListGroupsUseCase(api), [api]);
  const deleteBox = useMemo(() => new DeleteBoxUseCase(api), [api]);
  const updateBox = useMemo(() => new UpdateBoxUseCase(api), [api]);
  const leaveGroup = useMemo(() => new LeaveGroupUseCase(api), [api]);
  const updateGroup = useMemo(() => new UpdateGroupUseCase(api), [api]);

  const [boxes, setBoxes] = useState<Box[]>([]);
  const [activeGroups, setActiveGroups] = useState<Group[]>([]);
  const [selectedGroupId, setSelectedGroupId] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [shareOpen, setShareOpen] = useState(false);
  const [leaveOpen, setLeaveOpen] = useState(false);
  const [leaving, setLeaving] = useState(false);
  const [leaveError, setLeaveError] = useState<string | null>(null);
  const [editOpen, setEditOpen] = useState(false);
  const [savingGroup, setSavingGroup] = useState(false);
  const [editError, setEditError] = useState<string | null>(null);
  const [boxToDelete, setBoxToDelete] = useState<Box | null>(null);
  const [boxToEdit, setBoxToEdit] = useState<Box | null>(null);
  const [savingBox, setSavingBox] = useState(false);
  const [editBoxError, setEditBoxError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  const sessionGroupIds = useMemo(
    () => (experienceBoxSession ? resolveSessionGroupIds(experienceBoxSession) : []),
    [experienceBoxSession],
  );

  const selectedGroup = useMemo(
    () => activeGroups.find((group) => group.id === selectedGroupId) ?? null,
    [activeGroups, selectedGroupId],
  );

  const visibleBoxes = useMemo(
    () => (selectedGroupId ? boxes.filter((box) => box.groupId === selectedGroupId) : []),
    [boxes, selectedGroupId],
  );

  const loadBoxes = useCallback(async () => {
    if (!experienceBoxSession?.token || sessionGroupIds.length === 0) {
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const [groups, ...boxLists] = await Promise.all([
        listGroups.execute(experienceBoxSession.token),
        ...sessionGroupIds.map((groupId) =>
          listBoxes.execute(groupId, experienceBoxSession.token),
        ),
      ]);
      setActiveGroups(groups);
      setBoxes([...new Map(boxLists.flat().map((box) => [box.id, box])).values()]);
    } catch (err: unknown) {
      setError(err instanceof ApiError ? err.message : t('common.error'));
    } finally {
      setLoading(false);
    }
  }, [listBoxes, listGroups, experienceBoxSession?.token, sessionGroupIds, t]);

  useEffect(() => {
    void loadBoxes();
  }, [loadBoxes]);

  useEffect(() => {
    if (activeGroups.length === 0) {
      setSelectedGroupId(null);
      return;
    }

    setSelectedGroupId((current) =>
      current && activeGroups.some((group) => group.id === current)
        ? current
        : activeGroups[0].id,
    );
  }, [activeGroups]);

  const openBox = (box: Box) => {
    void setNavigation({
      groupId: box.groupId,
      boxId: box.id,
      boxName: box.name,
      boxType: box.type,
    }).then(() => {
      navigate(`/box-home/${box.id}/moment`);
    });
  };

  const openCreate = () => {
    if (!selectedGroupId) {
      return;
    }

    navigate('/box-home/create', { state: { groupId: selectedGroupId } });
  };

  const confirmDelete = async () => {
    if (!boxToDelete || !experienceBoxSession?.token) {
      return;
    }

    setDeleting(true);
    setDeleteError(null);

    try {
      await deleteBox.execute(boxToDelete.id, experienceBoxSession.token);
      setBoxes((current) => current.filter((item) => item.id !== boxToDelete.id));

      if (navigation.boxId === boxToDelete.id && selectedGroupId) {
        await setNavigation({ groupId: selectedGroupId });
      }

      setBoxToDelete(null);
      showToast(t('boxHome.deleteSuccess', { name: boxToDelete.name }));
    } catch (err) {
      setDeleteError(err instanceof ApiError ? err.message : t('common.error'));
    } finally {
      setDeleting(false);
    }
  };

  const confirmEditBox = async (input: {
    name: string;
    requireAllParticipants: boolean;
  }) => {
    if (!boxToEdit || !experienceBoxSession?.token) {
      return;
    }

    setSavingBox(true);
    setEditBoxError(null);

    try {
      const updated = await updateBox.execute(
        boxToEdit.id,
        experienceBoxSession.token,
        input,
      );
      setBoxes((current) =>
        current.map((item) => (item.id === updated.id ? updated : item)),
      );
      setBoxToEdit(null);
      showToast(t('boxHome.editSuccess', { name: updated.name }));
    } catch (err) {
      setEditBoxError(err instanceof ApiError ? err.message : t('common.error'));
    } finally {
      setSavingBox(false);
    }
  };

  const confirmLeave = async () => {
    if (!experienceBoxSession?.token || !selectedGroupId) {
      return;
    }

    setLeaving(true);
    setLeaveError(null);

    try {
      await leaveGroup.execute(selectedGroupId, experienceBoxSession.token);

      const remainingGroups = activeGroups.filter((group) => group.id !== selectedGroupId);
      setActiveGroups(remainingGroups);
      setBoxes((current) => current.filter((box) => box.groupId !== selectedGroupId));
      setLeaveOpen(false);

      if (remainingGroups.length === 0) {
        await clearNavigation();
        await logout();
        navigate('/auth', { replace: true });
        showToast(t('groups.leaveSuccess'));
        return;
      }

      showToast(t('groups.leaveSuccess'));
    } catch (err) {
      setLeaveError(err instanceof ApiError ? err.message : t('common.error'));
    } finally {
      setLeaving(false);
    }
  };

  const confirmEdit = async (input: { name: string; color: GroupAccent }) => {
    if (!experienceBoxSession?.token || !selectedGroup) {
      return;
    }

    setSavingGroup(true);
    setEditError(null);

    try {
      const updated = await updateGroup.execute(
        selectedGroup.id,
        experienceBoxSession.token,
        input,
      );
      setActiveGroups((current) =>
        current.map((group) => (group.id === updated.id ? updated : group)),
      );
      setEditOpen(false);
      showToast(t('groups.editSuccess'));
    } catch (err) {
      setEditError(err instanceof ApiError ? err.message : t('common.error'));
    } finally {
      setSavingGroup(false);
    }
  };

  const headingGroups = selectedGroup
    ? [
        {
          name: resolveGroupDisplayName(selectedGroup, t),
          accent: resolveGroupAccent(selectedGroup),
        },
      ]
    : [];
  const leavingCount = experienceBoxSession?.members?.length ?? 1;

  return (
    <>
    <main className={styles.page}>
      <ScreenHeader
        trailing={
          <div className={styles.headerActions}>
            {selectedGroup && (
              <button
                type="button"
                className={styles.editButton}
                aria-label={t('groups.editDialog.title')}
                onClick={() => {
                  setEditError(null);
                  setEditOpen(true);
                }}
              >
                <Pencil size={18} strokeWidth={2.25} aria-hidden />
              </button>
            )}
            <NavButton action="logout" onClick={() => void logout()} />
          </div>
        }
      >
        {headingGroups.length > 0 && <GroupHeading groups={headingGroups} />}
      </ScreenHeader>

      {activeGroups.length > 1 && selectedGroupId && (
        <GroupSelector
          groups={activeGroups}
          value={selectedGroupId}
          onChange={setSelectedGroupId}
        />
      )}

      <div className={styles.toolbar}>
        <Button onClick={openCreate}>{t('boxHome.create')}</Button>
        {selectedGroupId && experienceBoxSession?.token && (
          <Button variant="secondary" onClick={() => setShareOpen(true)}>
            {t('groups.inviteToGroup')}
          </Button>
        )}
        {selectedGroupId && experienceBoxSession?.token && (
          <NavButton
            action="leave"
            onClick={() => {
              setLeaveError(null);
              setLeaveOpen(true);
            }}
          />
        )}
      </div>

      {loading && <AppLoader label={t('common.loading')} />}
      {error && (
        <p className={styles.error} role="alert">
          {error}
        </p>
      )}

      {!loading && !error && (
        <GroupBoxesSection description={t('boxHome.boxesSectionDescription')}>
          {visibleBoxes.length === 0 ? (
            <section className={styles.empty}>
              <p>{t('boxHome.empty')}</p>
              <Button onClick={openCreate}>{t('boxHome.createFirst')}</Button>
            </section>
          ) : (
            <div className={styles.grid}>
              {visibleBoxes.map((box) => (
                <BoxCard
                  key={box.id}
                  name={box.name}
                  type={box.type}
                  typeLabel={t(`boxTypes.${box.type}.title`)}
                  typeHint={t(`boxTypes.${box.type}.hint`)}
                  experienceCount={box.experienceCount}
                  playLabel={t('boxes.actions.play')}
                  editLabel={t('boxHome.edit')}
                  deleteLabel={t('boxHome.delete')}
                  onPlay={() => openBox(box)}
                  onEdit={() => {
                    setEditBoxError(null);
                    setBoxToEdit(box);
                  }}
                  onDelete={() => {
                    setDeleteError(null);
                    setBoxToDelete(box);
                  }}
                />
              ))}
            </div>
          )}
        </GroupBoxesSection>
      )}

      {selectedGroupId && experienceBoxSession?.token && (
        <ShareInviteSheet
          open={shareOpen}
          groupId={selectedGroupId}
          token={experienceBoxSession.token}
          onClose={() => setShareOpen(false)}
        />
      )}

      <DeleteBoxDialog
        box={boxToDelete}
        deleting={deleting}
        error={deleteError}
        onConfirm={() => void confirmDelete()}
        onCancel={() => {
          if (!deleting) {
            setBoxToDelete(null);
            setDeleteError(null);
          }
        }}
      />

      <EditBoxDialog
        box={boxToEdit}
        saving={savingBox}
        error={editBoxError}
        onConfirm={(input) => void confirmEditBox(input)}
        onCancel={() => {
          if (!savingBox) {
            setBoxToEdit(null);
            setEditBoxError(null);
          }
        }}
      />

      <LeaveGroupDialog
        open={leaveOpen}
        memberCount={selectedGroup?.memberCount ?? 0}
        leavingCount={leavingCount}
        leaving={leaving}
        error={leaveError}
        onConfirm={() => void confirmLeave()}
        onCancel={() => {
          if (!leaving) {
            setLeaveOpen(false);
            setLeaveError(null);
          }
        }}
      />

      {selectedGroup && (
        <GroupFormDialog
          open={editOpen}
          mode="edit"
          initialName={selectedGroup.name}
          initialColor={resolveGroupAccent(selectedGroup)}
          saving={savingGroup}
          error={editError}
          onConfirm={(input) => void confirmEdit(input)}
          onCancel={() => {
            if (!savingGroup) {
              setEditOpen(false);
              setEditError(null);
            }
          }}
        />
      )}
    </main>
      <SessionModeFooter mode="EXPERIENCE_BOX" members={experienceBoxSession?.members} />
    </>
  );
}

