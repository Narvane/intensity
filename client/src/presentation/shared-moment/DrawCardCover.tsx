import type { Experience } from '@domain/experience/experienceTypes';
import { IntegritySeal } from '../components/IntegritySeal';
import { IntensityHero } from '../components/IntensityHero';
import { ExperienceTypePill } from '../components/ExperienceTypePill';
import { ParameterStarsGroup } from '../components/ParameterStarField';
import styles from './DrawCardCover.module.css';

interface DrawCardCoverProps {
  experience: Experience;
}

export function DrawCardCover({ experience }: DrawCardCoverProps) {
  return (
    <div className={styles.cover}>
      {experience.type !== 'none' && (
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
