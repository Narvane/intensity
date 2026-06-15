import { useNavigate } from 'react-router-dom';
import { useSession } from '@app/SessionProvider';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import styles from './UnknownSessionPage.module.css';

export function UnknownSessionPage() {
  const { t } = useI18n();
  const { logout } = useSession();
  const navigate = useNavigate();

  return (
    <main className={styles.page}>
      <h1>{t('unknownSession.title')}</h1>
      <p>{t('unknownSession.body')}</p>
      <div className={styles.actions}>
        <Button
          fullWidth
          onClick={async () => {
            await logout();
            navigate('/auth', { replace: true });
          }}
        >
          {t('unknownSession.logout')}
        </Button>
        <Button
          variant="secondary"
          fullWidth
          onClick={async () => {
            await logout();
            navigate('/auth', { replace: true, state: { panel: 'experienceBox' } });
          }}
        >
          {t('unknownSession.enterExperienceBox')}
        </Button>
      </div>
    </main>
  );
}
