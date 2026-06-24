import { useI18n } from '../../i18n/I18nContext';
import { BadgeCheck } from 'lucide-react';
import styles from './IntegritySeal.module.css';

interface IntegritySealProps {
  seal: string;
  compact?: boolean;
  variant?: 'default' | 'minimal';
}

export function IntegritySeal({
  seal,
  compact = false,
  variant = 'default',
}: IntegritySealProps) {
  const { t } = useI18n();
  const isMinimal = variant === 'minimal';
  const displaySeal = isMinimal && seal.length > 6 ? seal.slice(-4) : seal;
  const className = isMinimal
    ? styles.minimal
    : compact
      ? styles.compact
      : styles.seal;

  return (
    <div
      className={className}
      title={isMinimal ? `${t('seal.hint')} ${seal}` : t('seal.hint')}
      aria-label={`${t('seal.label')}: ${seal}`}
    >
      <BadgeCheck className={styles.icon} aria-hidden="true" />
      {!isMinimal && <span className={styles.label}>{t('seal.label')}</span>}
      <span className={styles.value}>{displaySeal}</span>
    </div>
  );
}
