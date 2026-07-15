import type { Experience } from '@domain/experience/experienceTypes';
import { IntegritySeal } from './IntegritySeal';
import { IntensityBadge } from './IntensityBadge';
import { ParameterStarsGroup } from '../rating/ParameterStarField';
import styles from './ExperienceSummaryMeta.module.css';

interface ExperienceSummaryMetaProps {
  experience: Experience;
  compact?: boolean;
  variant?: 'default' | 'experienceList';
  hideIntensity?: boolean;
}

export function ExperienceSummaryMeta({
  experience,
  compact = false,
  variant = 'default',
  hideIntensity = false,
}: ExperienceSummaryMetaProps) {
  const isExperienceList = variant === 'experienceList';

  return (
    <div
      className={
        compact ? styles.compact : isExperienceList ? styles.experienceList : styles.meta
      }
    >
      {!hideIntensity && <IntensityBadge level={experience.intensity} />}
      <ParameterStarsGroup
        parameters={experience.parameters}
        layout={compact ? 'cover' : isExperienceList ? 'listCompact' : 'list'}
      />
      <IntegritySeal
        seal={experience.seal}
        compact={compact}
        variant={isExperienceList ? 'minimal' : 'default'}
      />
    </div>
  );
}
