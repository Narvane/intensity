import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { FlipHorizontal2 } from 'lucide-react';
import { createApiClient } from '@adapters/api/ApiClient';
import { useAppLogout } from '@app/useAppLogout';
import { useToast } from '@app/ToastProvider';
import { useNavigation } from '@app/NavigationProvider';
import { useSession } from '@app/SessionProvider';
import { DEFAULT_BOX_TYPE } from '@domain/box/boxTypes';
import type { Experience } from '@domain/experience/experienceTypes';
import { resolveExperienceError } from '@domain/experience/experienceErrors';
import {
  canManageExperience,
  hasRevealableAuthorContent,
} from '@domain/experience/experienceVisibility';
import {
  DeleteExperienceUseCase,
  ListExperiencesUseCase,
} from '@domain/experience/experienceUseCases';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import { AppLoader } from '../components/AppLoader';
import { NavButton } from '../components/NavButton';
import { ScreenHeader } from '../components/ScreenHeader';
import { ScreenTitle } from '../components/ScreenTitle';
import { SessionModeFooter } from '../components/SessionModeFooter';
import { CreationAssistant } from './CreationAssistant';
import { DeleteExperienceDialog } from './DeleteExperienceDialog';
import { ExperienceCard } from './ExperienceCard';
import styles from './ExperienceListPage.module.css';

export function ExperienceListPage() {
  const { groupId = '', boxId = '' } = useParams();
  const { t } = useI18n();
  const { experiencesSession } = useSession();
  const { navigation } = useNavigation();
  const { showToast } = useToast();
  const logout = useAppLogout('EXPERIENCES');
  const navigate = useNavigate();
  const api = useMemo(() => createApiClient(), []);
  const listExperiences = useMemo(() => new ListExperiencesUseCase(api), [api]);
  const deleteExperience = useMemo(() => new DeleteExperienceUseCase(api), [api]);

  const [experiences, setExperiences] = useState<Experience[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [assistantOpen, setAssistantOpen] = useState(false);
  const [editing, setEditing] = useState<Experience | null>(null);
  const [experienceToDelete, setExperienceToDelete] = useState<Experience | null>(null);
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [flippedIds, setFlippedIds] = useState<Set<string>>(new Set());

  const boxName = navigation.boxName ?? t('experiences.defaultBoxName');
  const boxType = navigation.boxType ?? DEFAULT_BOX_TYPE;

  const flippableIds = useMemo(
    () =>
      experiences
        .filter(
          (experience) =>
            canManageExperience(experience, experiencesSession?.participantId) &&
            hasRevealableAuthorContent(experience),
        )
        .map((experience) => experience.id),
    [experiences, experiencesSession?.participantId],
  );

  const allFlipped =
    flippableIds.length > 0 && flippableIds.every((id) => flippedIds.has(id));

  useEffect(() => {
    setFlippedIds((current) => {
      const next = new Set([...current].filter((id) => flippableIds.includes(id)));
      return next.size === current.size ? current : next;
    });
  }, [flippableIds]);

  const toggleCardFlip = useCallback((experienceId: string) => {
    setFlippedIds((current) => {
      const next = new Set(current);
      if (next.has(experienceId)) {
        next.delete(experienceId);
      } else {
        next.add(experienceId);
      }
      return next;
    });
  }, []);

  const toggleFlipAll = useCallback(() => {
    if (allFlipped) {
      setFlippedIds(new Set());
      return;
    }

    setFlippedIds(new Set(flippableIds));
  }, [allFlipped, flippableIds]);

  const loadExperiences = useCallback(async () => {
    if (!experiencesSession?.token || !boxId) {
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const items = await listExperiences.execute(boxId, experiencesSession.token);
      setExperiences(items);
    } catch (err) {
      setError(resolveExperienceError(err, t));
    } finally {
      setLoading(false);
    }
  }, [boxId, listExperiences, experiencesSession?.token, t]);

  useEffect(() => {
    void loadExperiences();
  }, [loadExperiences]);

  const handleDelete = async () => {
    if (!experienceToDelete || !experiencesSession?.token) {
      return;
    }

    setDeleting(true);
    setDeleteError(null);

    try {
      await deleteExperience.execute(experienceToDelete.id, experiencesSession.token);
      setExperiences((current) => current.filter((item) => item.id !== experienceToDelete.id));
      setExperienceToDelete(null);
      showToast(t('experiences.deleteSuccess'));
    } catch (err) {
      setDeleteError(resolveExperienceError(err, t));
    } finally {
      setDeleting(false);
    }
  };

  const openCreateAssistant = () => {
    setEditing(null);
    setAssistantOpen(true);
  };

  const openEditAssistant = (experience: Experience) => {
    setEditing(experience);
    setAssistantOpen(true);
  };

  return (
    <>
    <main className={styles.page}>
      <ScreenHeader
        leading={
          <NavButton
            action="back"
            onClick={() => navigate(`/groups/${groupId}/boxes`)}
          />
        }
        trailing={<NavButton action="logout" onClick={() => void logout()} />}
      >
        <ScreenTitle>{boxName}</ScreenTitle>
      </ScreenHeader>

      <p className={styles.transparency}>{t('experiences.transparency')}</p>

      <div className={styles.toolbar}>
        <Button onClick={openCreateAssistant}>{t('experiences.create')}</Button>
        {flippableIds.length > 0 && (
          <>
            <span className={styles.toolbarSpacer} aria-hidden />
            <button
              type="button"
              className={styles.flipAllButton}
              aria-pressed={allFlipped}
              aria-label={allFlipped ? t('experiences.unflipAll') : t('experiences.flipAll')}
              onClick={toggleFlipAll}
            >
              <FlipHorizontal2 size={20} strokeWidth={2.25} aria-hidden />
            </button>
          </>
        )}
      </div>

      {loading && <AppLoader label={t('common.loading')} />}
      {error && (
        <p className={styles.error} role="alert">
          {error}
        </p>
      )}

      {!loading && !error && experiences.length === 0 && (
        <section className={styles.empty} aria-live="polite">
          <h2>{t('experiences.title')}</h2>
          <p>{t('experiences.empty')}</p>
          <Button onClick={openCreateAssistant}>{t('experiences.createFirst')}</Button>
        </section>
      )}

      {!loading && !error && experiences.length > 0 && (
        <div className={styles.list}>
          {experiences.map((experience) => (
            <ExperienceCard
              key={experience.id}
              experience={experience}
              participantId={experiencesSession?.participantId}
              flipped={flippedIds.has(experience.id)}
              onFlipToggle={() => toggleCardFlip(experience.id)}
              onEdit={() => openEditAssistant(experience)}
              onDelete={() => {
                setDeleteError(null);
                setExperienceToDelete(experience);
              }}
            />
          ))}
        </div>
      )}

      {experiencesSession?.token && (
        <CreationAssistant
          open={assistantOpen}
          boxId={boxId}
          boxType={boxType}
          token={experiencesSession.token}
          editing={editing}
          onClose={() => {
            setAssistantOpen(false);
            setEditing(null);
          }}
          onSaved={(saved) => {
            const savedIds = new Set(saved.map((item) => item.id));
            setExperiences((current) => [
              ...saved,
              ...current.filter((item) => !savedIds.has(item.id)),
            ]);
            setEditing(null);
          }}
        />
      )}

      <DeleteExperienceDialog
        experience={experienceToDelete}
        deleting={deleting}
        error={deleteError}
        onConfirm={() => void handleDelete()}
        onCancel={() => {
          if (!deleting) {
            setExperienceToDelete(null);
            setDeleteError(null);
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
