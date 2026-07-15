import { useI18n } from '../../../i18n/I18nContext';
import styles from './DraftPaginator.module.css';

interface DraftPaginatorProps {
  total: number;
  activeIndex: number;
  onSelect: (index: number) => void;
}

export function DraftPaginator({ total, activeIndex, onSelect }: DraftPaginatorProps) {
  const { t } = useI18n();

  if (total <= 1) {
    return null;
  }

  return (
    <div className={styles.paginator}>
      <p className={styles.label}>
        {t('assistant.fork.paginator', { current: activeIndex + 1, total })}
      </p>
      <div className={styles.dots} role="tablist" aria-label={t('assistant.fork.paginatorAria')}>
        {Array.from({ length: total }, (_, index) => (
          <button
            key={index}
            type="button"
            role="tab"
            aria-selected={index === activeIndex}
            aria-label={t('assistant.fork.dot', { number: index + 1 })}
            className={index === activeIndex ? styles.dotActive : styles.dot}
            onClick={() => onSelect(index)}
          >
            {index + 1}
          </button>
        ))}
      </div>
    </div>
  );
}
