import type { ExperienceType } from '@domain/experience/experienceType';
import { EXPERIENCE_TYPE_EMOJI } from '@domain/experience/experienceType';
import { useI18n } from '../../../i18n/I18nContext';
import styles from './ExperienceTypePill.module.css';

interface ExperienceTypePillProps {
  type: ExperienceType;
  size?: 'sm' | 'md';
}

export function ExperienceTypePill({ type, size = 'md' }: ExperienceTypePillProps) {
  const { t } = useI18n();

  if (type === 'none') {
    return null;
  }

  return (
    <span className={size === 'sm' ? styles.pillSm : styles.pill}>
      <span className={styles.emoji} aria-hidden>
        {EXPERIENCE_TYPE_EMOJI[type]}
      </span>
      <span className={styles.label}>{t(`experienceType.options.${type}.name`)}</span>
    </span>
  );
}
