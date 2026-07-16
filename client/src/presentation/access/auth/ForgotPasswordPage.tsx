import { useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { createApiClient } from '@app/SessionProvider';
import { resolveAuthError } from '@domain/auth/authErrors';
import { RequestPasswordResetUseCase } from '@domain/auth/authUseCases';
import { useI18n } from '../../../i18n/I18nContext';
import { Button } from '../../components/controls/Button';
import { NavButton } from '../../components/controls/NavButton';
import { ScreenHeader } from '../../components/chrome/ScreenHeader';
import styles from './AuthPage.module.css';

export function ForgotPasswordPage() {
  const { t } = useI18n();
  const navigate = useNavigate();
  const api = useMemo(() => createApiClient(), []);
  const requestReset = useMemo(() => new RequestPasswordResetUseCase(api), [api]);

  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [submitted, setSubmitted] = useState(false);

  const submit = async () => {
    setError(null);
    if (!email.trim()) {
      setError(t('auth.errors.requiredFields'));
      return;
    }

    setLoading(true);
    try {
      await requestReset.execute(email);
      setSubmitted(true);
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
        <h1>{t('auth.forgotPassword.title')}</h1>
        {submitted ? (
          <p className={styles.hint}>{t('auth.forgotPassword.sent')}</p>
        ) : (
          <>
            <p className={styles.hint}>{t('auth.forgotPassword.subtitle')}</p>
            <label className={styles.field}>
              <span>{t('auth.fields.email')}</span>
              <input
                type="email"
                autoComplete="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
              />
            </label>
            {error && <p className={styles.error}>{error}</p>}
            <Button fullWidth disabled={loading} onClick={() => void submit()}>
              {loading ? t('auth.forgotPassword.sending') : t('auth.forgotPassword.submit')}
            </Button>
          </>
        )}
        <Link className={styles.signOutButton} to="/auth">
          {t('auth.forgotPassword.backToSignIn')}
        </Link>
      </section>
    </main>
  );
}
