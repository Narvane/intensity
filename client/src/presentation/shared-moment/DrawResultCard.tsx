import type { Experience } from '@domain/experience/experienceTypes';
import { ExperienceContentBlock } from '../components/ExperienceContentBlock';
import { DrawCardCover } from './DrawCardCover';
import styles from './DrawResultCard.module.css';

interface DrawResultCardProps {
  experience: Experience;
  revealed: boolean;
}

export function DrawResultCard({ experience, revealed }: DrawResultCardProps) {
  return (
    <div className={styles.wrapper}>
      <div className={`${styles.card} ${revealed ? styles.revealed : ''}`}>
        <div className={styles.inner}>
          <div className={styles.cover} data-intensity={experience.intensity}>
            <DrawCardCover experience={experience} />
          </div>

          <div className={styles.face}>
            <ExperienceContentBlock experience={experience} />
          </div>
        </div>
      </div>
    </div>
  );
}
