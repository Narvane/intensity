import { useState } from 'react';
import { useI18n } from '../../i18n/I18nContext';
import { BrandMark } from '../components/BrandMark';
import { Button } from '../components/Button';
import { QuickGuideOverlay } from '../quick-guide/QuickGuideOverlay';
import styles from './AuthPage.module.css';

export function AuthPage() {
  const { t } = useI18n();
  const [quickGuideOpen, setQuickGuideOpen] = useState(false);

  return (
    <>
      <main className={styles.page}>
        <header className={styles.header}>
          <BrandMark />
          <Button
            variant="ghost"
            aria-label={t('auth.helpLabel')}
            onClick={() => setQuickGuideOpen(true)}
          >
            ?
          </Button>
        </header>

        <section className={styles.panel}>
          <h1>{t('auth.title')}</h1>
          <p>{t('auth.placeholder')}</p>
        </section>
      </main>

      <QuickGuideOverlay open={quickGuideOpen} onClose={() => setQuickGuideOpen(false)} />
    </>
  );
}
