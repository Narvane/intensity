import type { Experience } from '@domain/experience/experienceTypes';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import styles from './ExperienceCard.module.css';

interface ExperienceCardProps {
  experience: Experience;
  isOwn: boolean;
  onEdit?: () => void;
  onDelete?: () => void;
}

export function ExperienceCard({ experience, isOwn, onEdit, onDelete }: ExperienceCardProps) {
  const { t } = useI18n();

  return (
    <article className={styles.card} data-intensity={experience.intensity}>
      <header className={styles.header}>
        <span className={styles.intensity}>
          {t('experiences.intensityLabel', { level: experience.intensity })}
        </span>
        <span className={styles.seal} title={t('experiences.sealLabel')}>
          {experience.seal}
        </span>
      </header>

      <p className={styles.params}>
        {t('experiences.paramsSummary', {
          effort: experience.parameters.effort,
          openness: experience.parameters.openness,
          novelty: experience.parameters.novelty,
        })}
      </p>

      {isOwn && experience.description && (
        <>
          <p className={styles.description}>{experience.description}</p>
          {experience.reflection && (
            <p className={styles.reflection}>{experience.reflection}</p>
          )}
        </>
      )}

      {!isOwn && (
        <p className={styles.summary}>
          {t('experiences.otherSummary', { author: experience.authorDisplayName ?? t('experiences.anonymous') })}
        </p>
      )}

      {isOwn && (
        <div className={styles.actions}>
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
