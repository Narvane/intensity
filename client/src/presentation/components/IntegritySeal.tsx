import { useI18n } from '../../i18n/I18nContext';
import styles from './IntegritySeal.module.css';

interface IntegritySealProps {
  seal: string;
  compact?: boolean;
}

export function IntegritySeal({ seal, compact = false }: IntegritySealProps) {
  const { t } = useI18n();

  return (
    <div
      className={compact ? styles.compact : styles.seal}
      title={t('seal.hint')}
      aria-label={`${t('seal.label')}: ${seal}`}
    >
      <span className={styles.label}>{t('seal.label')}</span>
      <code className={styles.value}>{seal}</code>
    </div>
  );
}
