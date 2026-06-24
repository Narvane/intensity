import type { Experience } from '@domain/experience/experienceTypes';
import { IntegritySeal } from './IntegritySeal';
import { IntensityBadge } from './IntensityBadge';
import { ParameterStarsGroup } from './ParameterStarField';
import styles from './ExperienceSummaryMeta.module.css';

interface ExperienceSummaryMetaProps {
  experience: Experience;
  compact?: boolean;
}

export function ExperienceSummaryMeta({ experience, compact = false }: ExperienceSummaryMetaProps) {
  return (
    <div className={compact ? styles.compact : styles.meta}>
      <IntensityBadge level={experience.intensity} />
      <ParameterStarsGroup
        parameters={experience.parameters}
        layout={compact ? 'cover' : 'inline'}
      />
      <IntegritySeal seal={experience.seal} compact={compact} />
    </div>
  );
}
