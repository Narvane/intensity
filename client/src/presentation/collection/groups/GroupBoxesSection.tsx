import type { ReactNode } from 'react';
import { useI18n } from '../../../i18n/I18nContext';
import styles from './GroupBoxesSection.module.css';

interface GroupBoxesSectionProps {
  description: string;
  children: ReactNode;
}

export function GroupBoxesSection({ description, children }: GroupBoxesSectionProps) {
  const { t } = useI18n();

  return (
    <section className={styles.section}>
      <h2 className={styles.title}>{t('groups.boxesSectionTitle')}</h2>
      <p className={styles.description}>{description}</p>
      <div className={styles.content}>{children}</div>
    </section>
  );
}
