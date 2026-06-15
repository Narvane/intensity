import { useSession } from '@app/SessionProvider';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import styles from './GroupSelectionPage.module.css';

export function GroupSelectionPage() {
  const { t } = useI18n();
  const { session, logout } = useSession();

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <p className={styles.mode}>{t('session.experiencesMode')}</p>
          <h1>{t('groups.title')}</h1>
        </div>
        <Button variant="ghost" onClick={() => void logout()}>
          {t('session.logout')}
        </Button>
      </header>
      <section className={styles.card}>
        <p>{t('groups.placeholder')}</p>
        <p className={styles.meta}>
          {t('groups.signedInAs', { name: session?.displayName ?? '—' })}
        </p>
      </section>
    </main>
  );
}
