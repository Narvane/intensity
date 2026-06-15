import type { Experience } from '@domain/experience/experienceTypes';
import { useI18n } from '../../i18n/I18nContext';
import styles from './ExperienceSummaryMeta.module.css';

interface ExperienceSummaryMetaProps {
  experience: Experience;
  compact?: boolean;
}

export function ExperienceSummaryMeta({ experience, compact = false }: ExperienceSummaryMetaProps) {
  const { t } = useI18n();

  return (
    <div className={compact ? styles.compact : styles.meta}>
      <span className={styles.intensity} data-intensity={experience.intensity}>
        {t('experiences.intensityLabel', { level: experience.intensity })}
      </span>
      <span className={styles.params}>
        {t('experiences.paramsSummary', {
          effort: experience.parameters.effort,
          openness: experience.parameters.openness,
          novelty: experience.parameters.novelty,
        })}
      </span>
      <span className={styles.seal} title={t('experiences.sealLabel')}>
        {experience.seal}
      </span>
    </div>
  );
}
