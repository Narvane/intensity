import { useEffect, useMemo, useState } from 'react';
import { GitFork, Trash2 } from 'lucide-react';
import { createApiClient } from '@adapters/api/ApiClient';
import { useToast } from '@app/ToastProvider';
import type { BoxType } from '@domain/box/boxTypes';
import type { Experience, ExperienceInput } from '@domain/experience/experienceTypes';
import { suggestIntensity } from '@domain/experience/experienceTypes';
import {
  isValidIntensity,
  validateExperienceParameters,
} from '@domain/experience/intensityTokens';
import { resolveExperienceError } from '@domain/experience/experienceErrors';
import {
  CreateExperienceUseCase,
  CreateExperiencesBatchUseCase,
  UpdateExperienceUseCase,
} from '@domain/experience/experienceUseCases';
import type { ExperienceSuggestion } from '../../content/suggestion-packs';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import { NavButton } from '../components/NavButton';
import { ScreenHeader } from '../components/ScreenHeader';
import { ExperienceTypePicker } from '../components/ExperienceTypePicker';
import { ParameterStarField } from '../components/ParameterStarField';
import { RatingScale } from '../components/RatingScale';
import { SuggestionExplorer } from '../suggestions/SuggestionExplorer';
import { DraftPaginator } from './DraftPaginator';
import {
  MAX_DRAFTS,
  createEmptyDraft,
  draftFromExperience,
  useCreationDrafts,
  type CreationDraft,
} from './useCreationDrafts';
import styles from './CreationAssistant.module.css';

interface CreationAssistantProps {
  open: boolean;
  boxId: string;
  boxType: BoxType;
  token: string;
  editing?: Experience | null;
  onClose: () => void;
  onSaved: (saved: Experience[]) => void;
}

const STEP_COUNT = 5;
const PARAMETER_ORDER = ['effort', 'unpredictability', 'novelty'] as const;
const STEP_TITLE_KEYS = [
  'assistant.steps.suggestion.title',
  'assistant.steps.parameters.title',
  'assistant.steps.classification.title',
  'assistant.steps.type.title',
  'assistant.steps.interest.title',
];

function buildInput(draft: CreationDraft): ExperienceInput {
  return {
    description: draft.description.trim(),
    reflection: draft.reflection.trim(),
    intensity: draft.intensity,
    parameters: draft.parameters,
    type: draft.type,
  };
}

export function CreationAssistant({
  open,
  boxId,
  boxType,
  token,
  editing,
  onClose,
  onSaved,
}: CreationAssistantProps) {
  const { t } = useI18n();
  const { showToast } = useToast();
  const api = useMemo(() => createApiClient(), []);
  const createExperience = useMemo(() => new CreateExperienceUseCase(api), [api]);
  const createBatch = useMemo(() => new CreateExperiencesBatchUseCase(api), [api]);
  const updateExperience = useMemo(() => new UpdateExperienceUseCase(api), [api]);

  const {
    drafts: draftList,
    activeDraft,
    activeIndex,
    isForked,
    canFork,
    setActiveIndex,
    updateDraft,
    updateActiveDraft,
    forkFromDraft,
    removeDraft,
    reset,
  } = useCreationDrafts(() => [createEmptyDraft()]);

  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!open) {
      return;
    }
    reset(editing ? [draftFromExperience(editing)] : [createEmptyDraft()]);
    setStep(1);
    setError(null);
  }, [open, editing, reset]);

  if (!open) {
    return null;
  }

  const isLastDraft = activeIndex === draftList.length - 1;
  const isLastStep = step === STEP_COUNT;
  const suggestedIntensity = suggestIntensity(activeDraft.parameters);

  const applySuggestion = (suggestion: ExperienceSuggestion) => {
    updateDraft(0, {
      description: suggestion.description,
      reflection: suggestion.reflection,
      parameters: suggestion.parameters,
      intensity: suggestion.intensity,
      intensityTouched: true,
    });
    setActiveIndex(0);
  };

  const changeParameter = (key: (typeof PARAMETER_ORDER)[number], value: number) => {
    const nextParameters = { ...activeDraft.parameters, [key]: value };
    updateActiveDraft({
      parameters: nextParameters,
      ...(activeDraft.intensityTouched
        ? {}
        : { intensity: suggestIntensity(nextParameters) }),
    });
  };

  const canAdvance =
    step !== 1 || draftList.every((draft) => draft.description.trim().length > 0);

  const goNext = () => {
    setError(null);
    if (step === 1) {
      setStep(2);
      setActiveIndex(0);
      return;
    }
    if (!isLastDraft) {
      setActiveIndex(activeIndex + 1);
      return;
    }
    setStep((current) => current + 1);
    setActiveIndex(0);
  };

  const goBack = () => {
    setError(null);
    if (step === 1) {
      return;
    }
    if (activeIndex > 0) {
      setActiveIndex(activeIndex - 1);
      return;
    }
    const previousStep = step - 1;
    setStep(previousStep);
    setActiveIndex(previousStep === 1 ? 0 : draftList.length - 1);
  };

  const save = async () => {
    setLoading(true);
    setError(null);

    const invalid = draftList.some(
      (draft) =>
        draft.description.trim().length === 0 ||
        validateExperienceParameters(draft.parameters) !== null ||
        !isValidIntensity(draft.intensity),
    );
    if (invalid) {
      setError(t('experiences.validationError'));
      setLoading(false);
      return;
    }

    try {
      const inputs = draftList.map(buildInput);
      let saved: Experience[];

      if (editing) {
        saved = [await updateExperience.execute(editing.id, token, inputs[0])];
      } else if (inputs.length === 1) {
        saved = [await createExperience.execute(boxId, token, inputs[0])];
      } else {
        saved = await createBatch.execute(boxId, token, inputs);
      }

      onSaved(saved);
      onClose();

      if (!editing) {
        showToast(
          saved.length > 1
            ? t('assistant.createdMany', { count: saved.length })
            : t('assistant.createdOne'),
        );
      }
    } catch (err) {
      setError(resolveExperienceError(err, t));
    } finally {
      setLoading(false);
    }
  };

  const primaryLabel = !isLastDraft ? t('assistant.nextExperience') : t('assistant.next');

  return (
    <div className={styles.backdrop} role="dialog" aria-modal="true">
      <section className={styles.panel}>
        <ScreenHeader trailing={<NavButton action="close" onClick={onClose} />}>
          <div className={styles.headerBody}>
            <h2>{editing ? t('assistant.editTitle') : t('assistant.title')}</h2>
            <p className={styles.stepLabel}>
              {t('assistant.stepIndicator', { current: step, total: STEP_COUNT })}
              {' · '}
              {t(STEP_TITLE_KEYS[step - 1])}
            </p>
          </div>
        </ScreenHeader>

        <div className={styles.progress} aria-hidden="true">
          {Array.from({ length: STEP_COUNT }, (_, index) => (
            <span
              key={index}
              className={index + 1 <= step ? styles.progressActive : styles.progressIdle}
            />
          ))}
        </div>

        {step > 1 && (
          <div className={styles.stickyDescription}>
            <DraftPaginator
              total={draftList.length}
              activeIndex={activeIndex}
              onSelect={setActiveIndex}
            />
            <label className={styles.descriptionField}>
              <span>{t('assistant.fields.description')}</span>
              <textarea
                value={activeDraft.description}
                maxLength={1000}
                rows={2}
                placeholder={t('assistant.descriptionPlaceholder')}
                onChange={(event) =>
                  updateActiveDraft({ description: event.target.value })
                }
              />
            </label>
          </div>
        )}

        {step === 1 && (
          <section className={styles.step}>
            <h3>{t('assistant.steps.suggestion.title')}</h3>
            <p>{t('assistant.steps.suggestion.body')}</p>

            <div className={styles.draftList}>
              {draftList.map((draft, index) => (
                <div key={draft.uid} className={styles.draftItem}>
                  {isForked && (
                    <div className={styles.draftItemHeader}>
                      <span className={styles.draftBadge}>
                        {t('assistant.experienceLabel', { number: index + 1 })}
                      </span>
                      <button
                        type="button"
                        className={styles.removeDraft}
                        aria-label={t('assistant.fork.remove')}
                        onClick={() => removeDraft(index)}
                      >
                        <Trash2 size={15} aria-hidden />
                      </button>
                    </div>
                  )}
                  <label className={styles.descriptionField}>
                    {!isForked && <span>{t('assistant.fields.description')}</span>}
                    <textarea
                      value={draft.description}
                      maxLength={1000}
                      rows={3}
                      placeholder={t('assistant.descriptionPlaceholder')}
                      onChange={(event) =>
                        updateDraft(index, { description: event.target.value })
                      }
                    />
                  </label>
                </div>
              ))}
            </div>

            {!editing && (
              <div className={styles.forkArea}>
                <button
                  type="button"
                  className={styles.forkButton}
                  disabled={!canFork}
                  onClick={() => forkFromDraft(draftList.length - 1)}
                >
                  <GitFork size={16} aria-hidden />
                  {t('assistant.fork.button')}
                </button>
                <p className={styles.forkHint}>
                  {canFork
                    ? t('assistant.fork.hint')
                    : t('assistant.fork.limit', { max: MAX_DRAFTS })}
                </p>
              </div>
            )}

            <SuggestionExplorer boxType={boxType} onAccept={applySuggestion} />
          </section>
        )}

        {step === 2 && (
          <section className={styles.step}>
            <h3>{t('assistant.steps.parameters.title')}</h3>
            <p>{t('assistant.steps.parameters.body')}</p>
            <div className={styles.parameterFields}>
              {PARAMETER_ORDER.map((key) => (
                <ParameterStarField
                  key={key}
                  parameterKey={key}
                  value={activeDraft.parameters[key]}
                  layout="wizard"
                  showHint
                  onChange={(value) => changeParameter(key, value)}
                />
              ))}
            </div>
          </section>
        )}

        {step === 3 && (
          <section className={styles.step}>
            <h3>{t('assistant.steps.classification.title')}</h3>
            <p>{t('assistant.steps.classification.body')}</p>
            <p className={styles.classificationNote}>
              {t('assistant.steps.classification.averageHint')}
            </p>
            {!activeDraft.intensityTouched && (
              <p className={styles.hint}>
                {t('assistant.suggestedIntensity', {
                  level: suggestedIntensity,
                  name: t(`intensity.levels.${suggestedIntensity}`),
                })}
              </p>
            )}
            <RatingScale
              label={t('assistant.fields.intensity')}
              value={activeDraft.intensity}
              tone="intensity"
              onChange={(value) =>
                updateActiveDraft({ intensity: value, intensityTouched: true })
              }
            />
          </section>
        )}

        {step === 4 && (
          <section className={styles.step}>
            <h3>{t('assistant.steps.type.title')}</h3>
            <p>{t('assistant.steps.type.body')}</p>
            <ExperienceTypePicker
              value={activeDraft.type}
              onChange={(type) => updateActiveDraft({ type })}
            />
          </section>
        )}

        {step === 5 && (
          <section className={styles.step}>
            <h3>{t('assistant.steps.interest.title')}</h3>
            <p>{t('assistant.steps.interest.body')}</p>
            <label className={styles.field}>
              <span>{t('assistant.fields.interest')}</span>
              <textarea
                value={activeDraft.reflection}
                maxLength={2000}
                rows={5}
                placeholder={t('assistant.interestPlaceholder')}
                onChange={(event) =>
                  updateActiveDraft({ reflection: event.target.value })
                }
              />
            </label>
          </section>
        )}

        {error && (
          <p className={styles.error} role="alert">
            {error}
          </p>
        )}

        <footer className={styles.footer}>
          {step > 1 && <NavButton action="back" onClick={goBack} />}

          {(!isLastStep || !isLastDraft) && (
            <Button fullWidth disabled={!canAdvance} onClick={goNext}>
              {primaryLabel}
            </Button>
          )}

          {isLastStep && isLastDraft && (
            <Button fullWidth disabled={loading} onClick={() => void save()}>
              {loading
                ? t('common.loading')
                : editing
                  ? t('assistant.saveChanges')
                  : t('assistant.saveFinish')}
            </Button>
          )}
        </footer>
      </section>
    </div>
  );
}
