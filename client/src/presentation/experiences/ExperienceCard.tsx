import { useState } from 'react';
import type { Experience } from '@domain/experience/experienceTypes';
import {
  canManageExperience,
  isSummaryOnlyView,
  shouldShowDescription,
  shouldShowReflection,
} from '@domain/experience/experienceVisibility';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import { ExperienceContentBlock } from '../components/ExperienceContentBlock';
import { ExperienceSummaryMeta } from '../components/ExperienceSummaryMeta';
import styles from './ExperienceCard.module.css';

interface ExperienceCardProps {
  experience: Experience;
  participantId?: string;
  onEdit?: () => void;
  onDelete?: () => void;
}

export function ExperienceCard({
  experience,
  participantId,
  onEdit,
  onDelete,
}: ExperienceCardProps) {
  const { t } = useI18n();
  const isAuthor = canManageExperience(experience, participantId);
  const [previewAsOthers, setPreviewAsOthers] = useState(false);

  const listOptions = { isAuthor, previewAsOthers };
  const summaryOnly = isSummaryOnlyView(experience, 'EXPERIENCES_LIST', listOptions);
  const showDescription = shouldShowDescription(experience, 'EXPERIENCES_LIST', listOptions);
  const showReflection = shouldShowReflection(experience, 'EXPERIENCES_LIST', listOptions);

  return (
    <article className={styles.card} data-intensity={experience.intensity}>
      <ExperienceSummaryMeta experience={experience} />

      {summaryOnly && (
        <p className={styles.summary}>
          {isAuthor && previewAsOthers
            ? t('experiences.previewAsOthers')
            : t('experiences.otherSummary', {
                author: experience.authorDisplayName ?? t('experiences.anonymous'),
              })}
        </p>
      )}

      {(showDescription || showReflection) && (
        <ExperienceContentBlock
          experience={{
            ...experience,
            description: showDescription ? experience.description : undefined,
            reflection: showReflection ? experience.reflection : undefined,
          }}
        />
      )}

      {isAuthor && (
        <div className={styles.actions}>
          <Button variant="ghost" onClick={() => setPreviewAsOthers((current) => !current)}>
            {previewAsOthers ? t('experiences.showFull') : t('experiences.previewToggle')}
          </Button>
          <Button variant="ghost" onClick={onEdit}>
            {t('experiences.edit')}
          </Button>
          <Button variant="ghost" onClick={onDelete}>
            {t('experiences.delete')}
          </Button>
        </div>
      )}
    </article>
  );
}
