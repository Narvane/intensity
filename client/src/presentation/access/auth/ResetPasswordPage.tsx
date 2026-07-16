import { useMemo, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { createApiClient } from '@app/SessionProvider';
import {
  isValidAuthPasswordLength,
  resolveAuthError,
} from '@domain/auth/authErrors';
import { ResetPasswordUseCase } from '@domain/auth/authUseCases';
import { useI18n } from '../../../i18n/I18nContext';
import { Button } from '../../components/controls/Button';
import { NavButton } from '../../components/controls/NavButton';
import { ScreenHeader } from '../../components/chrome/ScreenHeader';
import styles from './AuthPage.module.css';

export function ResetPasswordPage() {
  const { t } = useI18n();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get('t')?.trim() ?? '';
  const api = useMemo(() => createApiClient(), []);
  const resetPassword = useMemo(() => new ResetPasswordUseCase(api), [api]);

  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState(false);

  const submit = async () => {
    setError(null);
    if (!token) {
      setError(t('auth.errors.invalidResetToken'));
      return;
    }
    if (!password || !confirmPassword) {
      setError(t('auth.errors.requiredFields'));
      return;
    }
    if (!isValidAuthPasswordLength(password)) {
      setError(t('auth.errors.passwordLength'));
      return;
    }
    if (password !== confirmPassword) {
      setError(t('auth.errors.passwordMismatch'));
      return;
    }

    setLoading(true);
    try {
      await resetPassword.execute(token, password);
      setDone(true);
    } catch (err) {
      setError(resolveAuthError(err, t));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className={styles.page}>
      <ScreenHeader
        leading={<NavButton action="back" onClick={() => navigate('/auth')} />}
      />
      <section className={`${styles.panel} ${styles.panelNeutral}`}>
        <h1>{t('auth.resetPassword.title')}</h1>
        {done ? (
          <>
            <p className={styles.hint}>{t('auth.resetPassword.success')}</p>
            <Button fullWidth onClick={() => navigate('/auth', { replace: true })}>
              {t('auth.resetPassword.goToSignIn')}
            </Button>
          </>
        ) : (
          <>
            <p className={styles.hint}>{t('auth.resetPassword.subtitle')}</p>
            <label className={styles.field}>
              <span>{t('auth.fields.password')}</span>
              <input
                type="password"
                autoComplete="new-password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
              />
            </label>
            <label className={styles.field}>
              <span>{t('auth.fields.confirmPassword')}</span>
              <input
                type="password"
                autoComplete="new-password"
                value={confirmPassword}
                onChange={(event) => setConfirmPassword(event.target.value)}
              />
            </label>
            {error && <p className={styles.error}>{error}</p>}
            <Button fullWidth disabled={loading || !token} onClick={() => void submit()}>
              {loading ? t('auth.resetPassword.saving') : t('auth.resetPassword.submit')}
            </Button>
          </>
        )}
      </section>
    </main>
  );
}
