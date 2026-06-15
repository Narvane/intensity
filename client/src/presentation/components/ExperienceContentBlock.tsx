import type { Experience } from '@domain/experience/experienceTypes';
import { useI18n } from '../../i18n/I18nContext';
import styles from './ExperienceContentBlock.module.css';

interface ExperienceContentBlockProps {
  experience: Experience;
  showAuthor?: boolean;
}

export function ExperienceContentBlock({
  experience,
  showAuthor = false,
}: ExperienceContentBlockProps) {
  const { t } = useI18n();

  return (
    <div className={styles.block}>
      {showAuthor && (
        <p className={styles.author}>
          {t('sharedMoment.byAuthor', {
            author: experience.authorDisplayName ?? t('experiences.anonymous'),
          })}
        </p>
      )}
      {experience.description && <p className={styles.description}>{experience.description}</p>}
      {experience.reflection && <p className={styles.reflection}>{experience.reflection}</p>}
    </div>
  );
}
