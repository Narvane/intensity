import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ApiError, createApiClient } from '@adapters/api/ApiClient';
import { createDefaultSessionAdapter, useSession } from '@app/SessionProvider';
import { useNavigation } from '@app/NavigationProvider';
import { LoginExperienceBoxUseCase } from '@domain/auth/authUseCases';
import type { Box } from '@domain/box/boxTypes';
import { useModalDialog } from '@presentation/hooks/useModalDialog';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import styles from './StartDrawSessionModal.module.css';

interface CredentialForm {
  email: string;
  password: string;
}

const emptyCredential = (): CredentialForm => ({ email: '', password: '' });
const MASKED_PASSWORD = '••••••••';

interface StartDrawSessionModalProps {
  open: boolean;
  box: Box | null;
  groupId: string;
  onClose: () => void;
}

export function StartDrawSessionModal({
  open,
  box,
  groupId,
  onClose,
}: StartDrawSessionModalProps) {
  const { t } = useI18n();
  const navigate = useNavigate();
  const { experiencesSession, refresh } = useSession();
  const { setNavigation } = useNavigation();

  const api = useMemo(() => createApiClient(), []);
  const sessionPort = useMemo(() => createDefaultSessionAdapter(), []);
  const loginExperienceBox = useMemo(
    () => new LoginExperienceBoxUseCase(api, sessionPort),
    [api, sessionPort],
  );

  const [credentials, setCredentials] = useState<CredentialForm[]>([emptyCredential()]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { dialogRef, cancelRef } = useModalDialog(open, onClose, loading);

  const experiencesEmail = experiencesSession?.email ?? '';
  const hasExperiencesSession = experiencesSession?.accessMode === 'EXPERIENCES';

  useEffect(() => {
    if (!open) {
      return;
    }

    setError(null);
    setLoading(false);
    setCredentials([
      hasExperiencesSession
        ? { email: experiencesEmail, password: MASKED_PASSWORD }
        : emptyCredential(),
      emptyCredential(),
    ]);
  }, [open, hasExperiencesSession, experiencesEmail]);

  if (!open || !box) {
    return null;
  }

  const handleError = (err: unknown) => {
    if (err instanceof ApiError) {
      if (err.code === 'GROUP_MEMBERSHIP_CONFLICT') {
        setError(t('auth.errors.groupMembershipConflict'));
        return;
      }
      setError(err.message);
      return;
    }
    setError(t('auth.errors.network'));
  };

  const submit = async () => {
    setLoading(true);
    setError(null);

    try {
      const reuseSessionToken =
        hasExperiencesSession && experiencesSession?.token ? experiencesSession.token : undefined;
      const additionalCredentials = hasExperiencesSession
        ? credentials.slice(1).filter((credential) => credential.email.trim().length > 0)
        : credentials.filter((credential) => credential.email.trim().length > 0);

      if (!reuseSessionToken && additionalCredentials.length === 0) {
        setError(t('auth.errors.network'));
        return;
      }

      await loginExperienceBox.execute({
        credentials: additionalCredentials,
        reuseSessionToken,
      });
      await refresh();
      await setNavigation({
        groupId,
        boxId: box.id,
        boxName: box.name,
        boxType: box.type,
      });
      onClose();
      navigate(`/box-home/${box.id}/moment`);
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className={styles.backdrop}
      role="presentation"
      onClick={loading ? undefined : onClose}
    >
      <section
        ref={dialogRef}
        className={styles.dialog}
        role="dialog"
        aria-modal="true"
        aria-labelledby="start-draw-title"
        onClick={(event) => event.stopPropagation()}
      >
        <header className={styles.header}>
          <h2 id="start-draw-title">{t('boxes.playModal.title')}</h2>
          <button
            ref={cancelRef}
            type="button"
            className={styles.close}
            aria-label={t('boxes.playModal.close')}
            disabled={loading}
            onClick={onClose}
          >
            ×
          </button>
        </header>

        <p className={styles.hostNote}>{t('boxes.playModal.hostExplanation')}</p>
        <p className={styles.hint}>{t('boxes.playModal.participantsHint')}</p>

        <div className={styles.credentials}>
          {credentials.map((credential, index) => {
            const isPrefilledSlot = hasExperiencesSession && index === 0;
            return (
              <div
                key={index}
                className={`${styles.credentialCard} ${
                  isPrefilledSlot ? styles.credentialCardPrefilled : ''
                }`}
              >
                <p className={styles.cardTitle}>
                  {isPrefilledSlot
                    ? t('auth.experienceBox.prefilledYou')
                    : t('auth.experienceBox.participant', { number: index + 1 })}
                </p>
                <label className={styles.field}>
                  <span>{t('auth.fields.email')}</span>
                  <input
                    type="email"
                    autoComplete="email"
                    disabled={isPrefilledSlot || loading}
                    value={credential.email}
                    onChange={(event) =>
                      setCredentials((current) =>
                        current.map((item, itemIndex) =>
                          itemIndex === index ? { ...item, email: event.target.value } : item,
                        ),
                      )
                    }
                  />
                </label>
                <label className={styles.field}>
                  <span>{t('auth.fields.password')}</span>
                  <input
                    type="password"
                    autoComplete={isPrefilledSlot ? 'off' : 'current-password'}
                    disabled={isPrefilledSlot || loading}
                    value={isPrefilledSlot ? MASKED_PASSWORD : credential.password}
                    aria-label={
                      isPrefilledSlot ? t('auth.experiences.passwordSaved') : undefined
                    }
                    onChange={(event) =>
                      setCredentials((current) =>
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
        </div>

        <Button
          variant="secondary"
          fullWidth
          disabled={loading}
          onClick={() => setCredentials((current) => [...current, emptyCredential()])}
        >
          {t('auth.experienceBox.addParticipant')}
        </Button>

        {error && (
          <p className={styles.error} role="alert">
            {error}
          </p>
        )}

        <div className={styles.actions}>
          <Button fullWidth disabled={loading} onClick={() => void submit()}>
            {loading ? t('auth.loading') : t('boxes.playModal.submit')}
          </Button>
          <Button variant="secondary" fullWidth disabled={loading} onClick={onClose}>
            {t('boxes.playModal.cancel')}
          </Button>
        </div>
      </section>
    </div>
  );
}
