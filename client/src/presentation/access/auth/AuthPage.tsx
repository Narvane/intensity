import { useEffect, useMemo, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { createDefaultPendingInviteAdapter } from '@adapters/invite/PendingInvitePreferencesAdapter';
import { createApiClient, createDefaultSessionAdapter, useSession } from '@app/SessionProvider';
import {
  DEMO_PASSWORD,
  isDemoMode,
  type DemoPersona,
} from '@content/demoCredentials';
import {
  isValidAuthPasswordLength,
  resolveAuthError,
} from '@domain/auth/authErrors';
import {
  LoginExperienceBoxUseCase,
  LoginExperiencesUseCase,
  RegisterParticipantUseCase,
  ValidateInviteCodeFormatUseCase,
} from '@domain/auth/authUseCases';
import { resolveExperiencesSessionContinuePath } from '@domain/auth/guestRouteRedirect';
import {
  getMemoryPendingReturnPath,
  hydratePendingInvite,
  resolvePostAuthDestination,
} from '@domain/invite/pendingInvite';
import { consumeExperienceBoxSessionEndReason } from '@domain/session/experienceBoxSessionEnd';
import { useI18n } from '../../../i18n/I18nContext';
import { AuthModeIntro } from '../../components/chrome/AuthModeIntro';
import { Button } from '../../components/controls/Button';
import { DemoAuthShortcuts } from '../../components/feedback/DemoAuthShortcuts';
import {
  JointLoginEmailInput,
  JointLoginSecretInput,
} from '../../components/controls/JointLoginSecretInput';
import { NavButton } from '../../components/controls/NavButton';
import { ScreenHeader } from '../../components/chrome/ScreenHeader';
import { QuickGuideOverlay } from '../quick-guide/QuickGuideOverlay';
import styles from './AuthPage.module.css';

type AuthPanel = 'experiences' | 'experienceBox' | 'register' | 'invite';

interface AuthLocationState {
  returnTo?: string;
  panel?: AuthPanel;
}

interface CredentialForm {
  email: string;
  password: string;
}

const emptyCredential = (): CredentialForm => ({ email: '', password: '' });
const MASKED_PASSWORD = '••••••••';

export function AuthPage() {
  const { t } = useI18n();
  const navigate = useNavigate();
  const location = useLocation();
  const authState = (location.state as AuthLocationState | null) ?? {};
  const { experiencesSession, refresh, logoutExperiences } = useSession();
  const api = useMemo(() => createApiClient(), []);
  const sessionPort = useMemo(() => createDefaultSessionAdapter(), []);
  const pendingInvitePort = useMemo(() => createDefaultPendingInviteAdapter(), []);

  const registerUseCase = useMemo(
    () => new RegisterParticipantUseCase(api, sessionPort),
    [api, sessionPort],
  );
  const loginExperiencesUseCase = useMemo(
    () => new LoginExperiencesUseCase(api, sessionPort),
    [api, sessionPort],
  );
  const loginExperienceBoxUseCase = useMemo(
    () => new LoginExperienceBoxUseCase(api, sessionPort),
    [api, sessionPort],
  );
  const validateInviteCode = useMemo(() => new ValidateInviteCodeFormatUseCase(), []);

  const [panel, setPanel] = useState<AuthPanel>(authState.panel ?? 'experiences');
  const [quickGuideOpen, setQuickGuideOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [sessionNotice, setSessionNotice] = useState<string | null>(null);

  const [experiencesForm, setExperiencesForm] = useState<CredentialForm>(emptyCredential);
  const [boxCredentials, setBoxCredentials] = useState<CredentialForm[]>([emptyCredential()]);
  const [registerForm, setRegisterForm] = useState({
    displayName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [inviteCode, setInviteCode] = useState('');
  const [pendingReturnPath, setPendingReturnPath] = useState<string | null>(null);

  const hasExperiencesSession = experiencesSession?.accessMode === 'EXPERIENCES';
  const experiencesEmail = experiencesSession?.email ?? '';
  const showDemoShortcuts = isDemoMode();

  useEffect(() => {
    if (authState.panel) {
      setPanel(authState.panel);
    }
  }, [authState.panel]);

  useEffect(() => {
    const endReason = consumeExperienceBoxSessionEndReason();
    if (endReason === 'draw_limit') {
      setSessionNotice(t('session.experienceBoxEnded'));
      setPanel('experienceBox');
    }
  }, [t]);

  useEffect(() => {
    if (authState.returnTo) {
      setPendingReturnPath(authState.returnTo);
      return;
    }

    void hydratePendingInvite(pendingInvitePort).then((pending) => {
      if (pending?.returnPath) {
        setPendingReturnPath(pending.returnPath);
      }
    });
  }, [authState.returnTo, pendingInvitePort]);

  useEffect(() => {
    if (!hasExperiencesSession) {
      return;
    }

    setExperiencesForm({
      email: experiencesEmail,
      password: MASKED_PASSWORD,
    });

    setBoxCredentials((current) => {
      const next = current.length > 0 ? [...current] : [emptyCredential()];
      next[0] = {
        email: experiencesEmail,
        password: MASKED_PASSWORD,
      };
      return next;
    });
  }, [experiencesEmail, hasExperiencesSession]);

  const continueExperiencesPath = () =>
    resolveExperiencesSessionContinuePath({
      returnTo: authState.returnTo,
      pendingReturnPath: pendingReturnPath ?? getMemoryPendingReturnPath(),
    });

  const resolveExperiencesDestination = () =>
    resolvePostAuthDestination(authState.returnTo, pendingReturnPath ?? getMemoryPendingReturnPath());

  const afterAuthNavigate = () => {
    navigate(resolveExperiencesDestination(), { replace: true });
  };

  const handleError = (err: unknown) => {
    setError(resolveAuthError(err, t));
  };

  const passwordsHaveValidLength = (passwords: string[]) =>
    passwords.every((password) => isValidAuthPasswordLength(password));

  const signOutExperiencesSession = async () => {
    setLoading(true);
    setError(null);
    try {
      await logoutExperiences();
      setExperiencesForm(emptyCredential());
      setBoxCredentials([emptyCredential()]);
    } finally {
      setLoading(false);
    }
  };

  const continueExperiences = () => {
    navigate(continueExperiencesPath(), { replace: true });
  };

  const applyDemoExperiencesPersona = (persona: DemoPersona) => {
    if (hasExperiencesSession) {
      return;
    }
    setError(null);
    setExperiencesForm({ email: persona.email, password: DEMO_PASSWORD });
  };

  const applyDemoJointPersonas = (personas: DemoPersona[]) => {
    setError(null);
    const sessionEmail = experiencesEmail.trim().toLowerCase();
    if (hasExperiencesSession && sessionEmail) {
      const others = personas.filter((persona) => persona.email !== sessionEmail);
      setBoxCredentials([
        { email: experiencesEmail, password: MASKED_PASSWORD },
        ...others.map((persona) => ({ email: persona.email, password: DEMO_PASSWORD })),
      ]);
      return;
    }
    setBoxCredentials(
      personas.map((persona) => ({ email: persona.email, password: DEMO_PASSWORD })),
    );
  };

  const submitExperiences = async () => {
    if (hasExperiencesSession) {
      continueExperiences();
      return;
    }

    setError(null);
    if (!experiencesForm.email.trim() || !experiencesForm.password) {
      setError(t('auth.errors.requiredFields'));
      return;
    }
    if (!isValidAuthPasswordLength(experiencesForm.password)) {
      setError(t('auth.errors.passwordLength'));
      return;
    }

    setLoading(true);
    try {
      await loginExperiencesUseCase.execute(experiencesForm);
      await refresh();
      navigate(resolveExperiencesDestination(), { replace: true });
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  const submitExperienceBox = async () => {
    setError(null);
    try {
      const reuseSessionToken =
        hasExperiencesSession && experiencesSession?.token ? experiencesSession.token : undefined;
      const additionalCredentials = hasExperiencesSession
        ? boxCredentials.slice(1).filter((credential) => credential.email.trim().length > 0)
        : boxCredentials.filter((credential) => credential.email.trim().length > 0);

      if (!reuseSessionToken && additionalCredentials.length === 0) {
        setError(t('auth.errors.credentialsRequired'));
        return;
      }

      if (!passwordsHaveValidLength(additionalCredentials.map((credential) => credential.password))) {
        setError(t('auth.errors.passwordLength'));
        return;
      }

      setLoading(true);
      await loginExperienceBoxUseCase.execute({
        credentials: additionalCredentials,
        reuseSessionToken,
      });
      await refresh();
      navigate('/box-home');
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  const submitRegister = async () => {
    setError(null);
    if (
      !registerForm.displayName.trim() ||
      !registerForm.email.trim() ||
      !registerForm.password ||
      !registerForm.confirmPassword
    ) {
      setError(t('auth.errors.requiredFields'));
      return;
    }
    if (!isValidAuthPasswordLength(registerForm.password)) {
      setError(t('auth.errors.passwordLength'));
      return;
    }
    if (registerForm.password !== registerForm.confirmPassword) {
      setError(t('auth.errors.passwordMismatch'));
      return;
    }

    setLoading(true);
    try {
      await registerUseCase.execute({
        displayName: registerForm.displayName,
        email: registerForm.email,
        password: registerForm.password,
      });
      await refresh();
      afterAuthNavigate();
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  const submitInviteCode = () => {
    setError(null);
    if (!validateInviteCode.execute(inviteCode)) {
      setError(t('auth.errors.invalidInviteCode'));
      return;
    }
    navigate(`/join?code=${encodeURIComponent(inviteCode.trim().toUpperCase())}`);
  };

  const panelClass =
    panel === 'experiences'
      ? styles.panelExperiences
      : panel === 'experienceBox'
        ? styles.panelExperienceBox
        : panel === 'invite'
          ? styles.panelInvite
          : styles.panelNeutral;

  return (
    <>
      <main className={styles.page}>
        <ScreenHeader
          leading={<span aria-hidden="true" />}
          trailing={
            <NavButton
              action="help"
              iconOnly
              onClick={() => setQuickGuideOpen(true)}
            />
          }
        />

        <nav className={styles.tabs} aria-label={t('auth.tabsLabel')}>
          {(['experiences', 'experienceBox', 'register', 'invite'] as AuthPanel[]).map((tab) => (
            <button
              key={tab}
              type="button"
              className={
                panel === tab
                  ? tab === 'experiences'
                    ? `${styles.tabActive} ${styles.tabActiveExperiences}`
                    : tab === 'experienceBox'
                      ? `${styles.tabActive} ${styles.tabActiveExperienceBox}`
                      : tab === 'invite'
                        ? `${styles.tabActive} ${styles.tabActiveInvite}`
                        : styles.tabActive
                  : styles.tab
              }
              onClick={() => {
                setPanel(tab);
                setError(null);
              }}
            >
              {t(`auth.tabs.${tab}`)}
            </button>
          ))}
        </nav>

        <section className={`${styles.panel} ${panelClass}`}>
          {panel === 'experiences' && (
            <>
              <AuthModeIntro mode="EXPERIENCES" />
              {showDemoShortcuts && !hasExperiencesSession && (
                <DemoAuthShortcuts
                  mode="experiences"
                  onPickExperiences={applyDemoExperiencesPersona}
                  onPickJoint={applyDemoJointPersonas}
                />
              )}
              {hasExperiencesSession && (
                <p className={styles.sessionActive}>{t('auth.experiences.sessionActive')}</p>
              )}
              <label className={styles.field}>
                <span>{t('auth.fields.email')}</span>
                <input
                  type="email"
                  autoComplete="email"
                  disabled={hasExperiencesSession}
                  value={hasExperiencesSession ? experiencesEmail : experiencesForm.email}
                  onChange={(event) =>
                    setExperiencesForm((current) => ({
                      ...current,
                      email: event.target.value,
                    }))
                  }
                />
              </label>
              <label className={styles.field}>
                <span>{t('auth.fields.password')}</span>
                <input
                  type="password"
                  autoComplete={hasExperiencesSession ? 'off' : 'current-password'}
                  disabled={hasExperiencesSession}
                  value={hasExperiencesSession ? MASKED_PASSWORD : experiencesForm.password}
                  aria-label={
                    hasExperiencesSession ? t('auth.experiences.passwordSaved') : undefined
                  }
                  onChange={(event) =>
                    setExperiencesForm((current) => ({
                      ...current,
                      password: event.target.value,
                    }))
                  }
                />
              </label>
              {!hasExperiencesSession && (
                <Link className={styles.signOutButton} to="/auth/forgot-password">
                  {t('auth.forgotPassword.link')}
                </Link>
              )}
              {hasExperiencesSession && (
                <button
                  type="button"
                  className={styles.signOutButton}
                  disabled={loading}
                  onClick={() => void signOutExperiencesSession()}
                >
                  {t('auth.experiences.signOut')}
                </button>
              )}
              <Button fullWidth disabled={loading} onClick={() => void submitExperiences()}>
                {loading
                  ? t('auth.loading')
                  : hasExperiencesSession
                    ? t('auth.experiences.continue')
                    : t('auth.experiences.submit')}
              </Button>
            </>
          )}

          {panel === 'experienceBox' && (
            <>
              <AuthModeIntro mode="EXPERIENCE_BOX" />
              <p className={styles.hint}>{t('auth.experienceBox.hint')}</p>
              {showDemoShortcuts && (
                <DemoAuthShortcuts
                  mode="experienceBox"
                  onPickExperiences={applyDemoExperiencesPersona}
                  onPickJoint={applyDemoJointPersonas}
                />
              )}
              {boxCredentials.map((credential, index) => {
                const isPrefilledSlot = hasExperiencesSession && index === 0;
                return (
                  <div
                    key={index}
                    className={`${styles.credentialCard} ${
                      isPrefilledSlot ? styles.credentialCardPrefilled : ''
                    }`}
                  >
                    <div className={styles.credentialCardHeader}>
                      <p className={styles.cardTitle}>
                        {isPrefilledSlot
                          ? t('auth.experienceBox.prefilledYou')
                          : t('auth.experienceBox.participant', { number: index + 1 })}
                      </p>
                      {isPrefilledSlot && (
                        <button
                          type="button"
                          className={styles.signOutButtonInline}
                          disabled={loading}
                          onClick={() => void signOutExperiencesSession()}
                        >
                          {t('auth.experiences.signOut')}
                        </button>
                      )}
                    </div>
                    <label className={styles.field}>
                      <span>{t('auth.fields.email')}</span>
                      <JointLoginEmailInput
                        name={`joint-email-${index}`}
                        disabled={isPrefilledSlot}
                        value={credential.email}
                        onChange={(event) =>
                          setBoxCredentials((current) =>
                            current.map((item, itemIndex) =>
                              itemIndex === index
                                ? { ...item, email: event.target.value }
                                : item,
                            ),
                          )
                        }
                      />
                    </label>
                    <label className={styles.field}>
                      <span>{t('auth.fields.password')}</span>
                      <JointLoginSecretInput
                        name={`joint-secret-${index}`}
                        disabled={isPrefilledSlot}
                        value={isPrefilledSlot ? MASKED_PASSWORD : credential.password}
                        aria-label={
                          isPrefilledSlot ? t('auth.experiences.passwordSaved') : undefined
                        }
                        onChange={(event) =>
                          setBoxCredentials((current) =>
                            current.map((item, itemIndex) =>
                              itemIndex === index
                                ? { ...item, password: event.target.value }
                                : item,
                            ),
                          )
                        }
                      />
                    </label>
                  </div>
                );
              })}
              <Button
                variant="secondary"
                fullWidth
                onClick={() => setBoxCredentials((current) => [...current, emptyCredential()])}
              >
                {t('auth.experienceBox.addParticipant')}
              </Button>
              <Button fullWidth disabled={loading} onClick={() => void submitExperienceBox()}>
                {loading ? t('auth.loading') : t('auth.experienceBox.submit')}
              </Button>
            </>
          )}

          {panel === 'register' && (
            <>
              <h1>{t('auth.register.title')}</h1>
              <label className={styles.field}>
                <span>{t('auth.fields.displayName')}</span>
                <input
                  type="text"
                  value={registerForm.displayName}
                  onChange={(event) =>
                    setRegisterForm((current) => ({
                      ...current,
                      displayName: event.target.value,
                    }))
                  }
                />
              </label>
              <label className={styles.field}>
                <span>{t('auth.fields.email')}</span>
                <input
                  type="email"
                  autoComplete="email"
                  value={registerForm.email}
                  onChange={(event) =>
                    setRegisterForm((current) => ({ ...current, email: event.target.value }))
                  }
                />
              </label>
              <label className={styles.field}>
                <span>{t('auth.fields.password')}</span>
                <input
                  type="password"
                  autoComplete="new-password"
                  value={registerForm.password}
                  onChange={(event) =>
                    setRegisterForm((current) => ({ ...current, password: event.target.value }))
                  }
                />
              </label>
              <label className={styles.field}>
                <span>{t('auth.fields.confirmPassword')}</span>
                <input
                  type="password"
                  autoComplete="new-password"
                  value={registerForm.confirmPassword}
                  onChange={(event) =>
                    setRegisterForm((current) => ({
                      ...current,
                      confirmPassword: event.target.value,
                    }))
                  }
                />
              </label>
              <Button fullWidth disabled={loading} onClick={() => void submitRegister()}>
                {loading ? t('auth.loading') : t('auth.register.submit')}
              </Button>
            </>
          )}

          {panel === 'invite' && (
            <>
              <h1>{t('auth.invite.title')}</h1>
              <label className={styles.field}>
                <span>{t('auth.invite.codeLabel')}</span>
                <input
                  type="text"
                  maxLength={6}
                  className={styles.inviteCode}
                  value={inviteCode}
                  onChange={(event) => setInviteCode(event.target.value.toUpperCase())}
                />
              </label>
              <Button fullWidth onClick={submitInviteCode}>
                {t('auth.invite.submit')}
              </Button>
            </>
          )}

          {sessionNotice && (
            <p className={styles.notice} role="status">
              {sessionNotice}
            </p>
          )}

          {error && (
            <p className={styles.error} role="alert">
              {error}
            </p>
          )}
        </section>
      </main>
      <div className={styles.bottomSafePad} aria-hidden="true" />

      <QuickGuideOverlay open={quickGuideOpen} onClose={() => setQuickGuideOpen(false)} />
    </>
  );
}
