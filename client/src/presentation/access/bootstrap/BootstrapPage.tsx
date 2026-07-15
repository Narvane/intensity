import { useI18n } from '../../../i18n/I18nContext';
import { AppLoader } from '../../components/feedback/AppLoader';
import { BrandMark } from '../../components/brand/BrandMark';
import { Button } from '../../components/controls/Button';
import { useBootstrapFlow } from '@app/useBootstrapFlow';
import styles from './BootstrapPage.module.css';

export function BootstrapPage() {
  const { t } = useI18n();
  const { state, retry } = useBootstrapFlow();

  return (
    <main className={styles.page} aria-live="polite">
      <BrandMark variant="icon" size="lg" />
      <h1 className="srOnly">{t('app.name')}</h1>

      {state.status === 'loading' && (
        <AppLoader label={t('bootstrap.loading')} size="lg" />
      )}

      {state.status === 'error' && (
        <div className={styles.errorBlock}>
          <p className={styles.message} role="alert">
            {t(state.errorMessage ?? 'bootstrap.error')}
          </p>
          <Button onClick={() => void retry()}>{t('bootstrap.retry')}</Button>
        </div>
      )}
    </main>
  );
}
