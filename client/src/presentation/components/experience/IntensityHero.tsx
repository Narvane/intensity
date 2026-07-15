import { useI18n } from '../../../i18n/I18nContext';
import styles from './IntensityHero.module.css';

interface IntensityHeroProps {
  level: number;
  showName?: boolean;
}

export function IntensityHero({ level, showName = true }: IntensityHeroProps) {
  const { t } = useI18n();
  const name = t(`intensity.levels.${level}`);

  return (
    <div className={styles.hero} data-intensity={level}>
      <p className={styles.label}>{t('sharedMoment.intensityLabel')}</p>
      <p
        className={styles.value}
        aria-label={t('intensity.levelNamed', { level, name })}
      >
        {level}
      </p>
      {showName && <p className={styles.name}>{name}</p>}
    </div>
  );
}
