import type { Experience } from '@domain/experience/experienceTypes';
import { IntegritySeal } from '../../components/experience/IntegritySeal';
import { IntensityHero } from '../../components/experience/IntensityHero';
import { ExperienceTypePill } from '../../components/experience/ExperienceTypePill';
import { ParameterStarsGroup } from '../../components/rating/ParameterStarField';
import styles from './DrawCardCover.module.css';

interface DrawCardCoverProps {
  experience: Experience;
}

export function DrawCardCover({ experience }: DrawCardCoverProps) {
  const hasType = experience.type !== 'none';

  return (
    <div className={`${styles.cover} ${hasType ? styles.coverWithType : ''}`}>
      {hasType && (
        <div className={styles.typeSlot}>
          <ExperienceTypePill type={experience.type} />
        </div>
      )}
      <IntensityHero level={experience.intensity} />
      <ParameterStarsGroup parameters={experience.parameters} layout="drawCover" />
      <div className={styles.sealSlot}>
        <IntegritySeal seal={experience.seal} variant="minimal" />
      </div>
    </div>
  );
}
