import { useSession } from '@app/SessionProvider';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import styles from './BoxHomePage.module.css';

export function BoxHomePage() {
  const { t } = useI18n();
  const { session, logout } = useSession();

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <p className={styles.mode}>{t('session.experienceBoxMode')}</p>
          <h1>{t('boxHome.title')}</h1>
        </div>
        <Button variant="ghost" onClick={() => void logout()}>
          {t('session.logout')}
        </Button>
      </header>
      <section className={styles.card}>
        <p>{t('boxHome.placeholder')}</p>
        {session?.members && (
          <ul className={styles.members}>
            {session.members.map((member) => (
              <li key={member.participantId}>{member.displayName}</li>
            ))}
          </ul>
        )}
      </section>
    </main>
  );
}
