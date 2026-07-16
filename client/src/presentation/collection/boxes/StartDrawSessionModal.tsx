import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createApiClient } from '@adapters/api/ApiClient';
import { createDefaultSessionAdapter, useSession } from '@app/SessionProvider';
import { useNavigation } from '@app/NavigationProvider';
import {
  isValidAuthPasswordLength,
  looksLikeMaskedPassword,
  resolveAuthError,
} from '@domain/auth/authErrors';
import { LoginExperienceBoxUseCase } from '@domain/auth/authUseCases';
import type { Box, GroupMember } from '@domain/box/boxTypes';
import { ListGroupsUseCase } from '@domain/box/boxUseCases';
import { useModalDialog } from '@presentation/hooks/useModalDialog';
import { useI18n } from '../../../i18n/I18nContext';
import { Button } from '../../components/controls/Button';
import {
  JointLoginEmailInput,
  JointLoginSecretInput,
} from '../../components/controls/JointLoginSecretInput';
import styles from './StartDrawSessionModal.module.css';

interface CredentialForm {
  email: string;
  password: string;
  participantId?: string;
  displayName?: string;
}

const emptyCredential = (): CredentialForm => ({ email: '', password: '' });

export type GroupJointLoginMode = 'play' | 'manage';

interface StartDrawSessionModalProps {
  open: boolean;
  mode: GroupJointLoginMode;
  box: Box | null;
  groupId: string;
  members: GroupMember[];
  onClose: () => void;
}

function buildInitialCredentials(
  members: GroupMember[],
  options: {
    experiencesEmail: string;
    hasExperiencesSession: boolean;
    currentParticipantId?: string;
  },
): CredentialForm[] {
  const { experiencesEmail, hasExperiencesSession, currentParticipantId } = options;
  const normalizedSessionEmail = experiencesEmail.trim().toLowerCase();

  if (members.length === 0) {
    return [
      hasExperiencesSession && experiencesEmail
        ? { email: experiencesEmail, password: '' }
        : emptyCredential(),
    ];
  }

  const isCurrentMember = (member: GroupMember) => {
    if (!hasExperiencesSession) {
      return false;
    }
    if (currentParticipantId && member.participantId === currentParticipantId) {
      return true;
    }
    const memberEmail = (member.email ?? '').trim().toLowerCase();
    return Boolean(memberEmail && memberEmail === normalizedSessionEmail);
  };

  const ordered = [...members].sort((left, right) => {
    const leftIsYou = isCurrentMember(left);
    const rightIsYou = isCurrentMember(right);
    if (leftIsYou === rightIsYou) {
      return left.displayName.localeCompare(right.displayName, undefined, { sensitivity: 'base' });
    }
    return leftIsYou ? -1 : 1;
  });

  return ordered.map((member) => {
    const isYou = isCurrentMember(member);
    const email = (member.email ?? (isYou ? experiencesEmail : '')).trim();
    return {
      email,
      password: '',
      participantId: member.participantId,
      displayName: member.displayName,
    };
  });
}

export function StartDrawSessionModal({
  open,
  mode,
  box,
  groupId,
  members,
  onClose,
}: StartDrawSessionModalProps) {
  const { t } = useI18n();
  const navigate = useNavigate();
  const { experiencesSession, saveExperienceBoxSession } = useSession();
  const { setNavigation } = useNavigation();

  const api = useMemo(() => createApiClient(), []);
  const sessionPort = useMemo(() => createDefaultSessionAdapter(), []);
  const loginExperienceBox = useMemo(
    () => new LoginExperienceBoxUseCase(api, sessionPort),
    [api, sessionPort],
  );
  const listGroups = useMemo(() => new ListGroupsUseCase(api), [api]);

  const [credentials, setCredentials] = useState<CredentialForm[]>([emptyCredential()]);
  const [roster, setRoster] = useState<GroupMember[]>(members);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { dialogRef, cancelRef } = useModalDialog(open, onClose, loading);

  const experiencesEmail = experiencesSession?.email ?? '';
  const hasExperiencesSession = experiencesSession?.accessMode === 'EXPERIENCES';
  const currentParticipantId = experiencesSession?.participantId;
  const requireAllParticipants =
    mode === 'manage' ? true : Boolean(box?.requireAllParticipants);
  const canEditRoster = !requireAllParticipants;

  useEffect(() => {
    if (!open) {
      return;
    }

    setError(null);
    setLoading(false);
    setRoster(members);
    setCredentials(
      buildInitialCredentials(members, {
        experiencesEmail,
        hasExperiencesSession,
        currentParticipantId,
      }),
    );

    const token = experiencesSession?.token;
    if (!token || !groupId) {
      return;
    }

    let cancelled = false;
    void listGroups
      .execute(token)
      .then((groups) => {
        if (cancelled) {
          return;
        }
        const freshMembers = groups.find((group) => group.id === groupId)?.members ?? members;
        setRoster(freshMembers);
        setCredentials(
          buildInitialCredentials(freshMembers, {
            experiencesEmail,
            hasExperiencesSession,
            currentParticipantId,
          }),
        );
      })
      .catch(() => {
        /* Keep prop-based roster if refresh fails. */
      });

    return () => {
      cancelled = true;
    };
  }, [
    open,
    members,
    groupId,
    listGroups,
    experiencesSession?.token,
    hasExperiencesSession,
    experiencesEmail,
    currentParticipantId,
  ]);

  if (!open || (mode === 'play' && !box)) {
    return null;
  }

  const handleError = (err: unknown) => {
    setError(resolveAuthError(err, t));
  };

  const submit = async () => {
    setError(null);

    const filled = credentials.filter((credential) => credential.email.trim().length > 0);
    const reuseSessionToken =
      hasExperiencesSession &&
      experiencesSession?.token &&
      filled.some(
        (credential) =>
          credential.email.trim().toLowerCase() === experiencesEmail.trim().toLowerCase(),
      )
        ? experiencesSession.token
        : undefined;
    const additionalCredentials = (
      reuseSessionToken
        ? filled.filter(
            (credential) =>
              credential.email.trim().toLowerCase() !== experiencesEmail.trim().toLowerCase(),
          )
        : filled
    ).map((credential) => ({
      email: credential.email.trim(),
      password: credential.password,
    }));

    if (!reuseSessionToken && additionalCredentials.length === 0) {
      setError(t('auth.errors.credentialsRequired'));
      return;
    }

    if (requireAllParticipants && filled.length < roster.length) {
      setError(t('auth.errors.groupRequiresAllMembers'));
      return;
    }

    if (
      additionalCredentials.some(
        (credential) => !credential.password || looksLikeMaskedPassword(credential.password),
      )
    ) {
      setError(t('auth.errors.requiredFields'));
      return;
    }

    if (
      !additionalCredentials.every((credential) => isValidAuthPasswordLength(credential.password))
    ) {
      setError(t('auth.errors.passwordLength'));
      return;
    }

    setLoading(true);

    try {
      const session = await loginExperienceBox.execute({
        credentials: additionalCredentials,
        reuseSessionToken,
        targetGroupId: groupId,
        requireAllMembers: requireAllParticipants,
      });
      await saveExperienceBoxSession(session);

      if (mode === 'play' && box) {
        await setNavigation({
          groupId,
          boxId: box.id,
          boxName: box.name,
          boxType: box.type,
        });
        onClose();
        navigate(`/box-home/${box.id}/moment`);
      } else {
        await setNavigation({ groupId });
        onClose();
        navigate('/box-home');
      }
    } catch (err) {
      handleError(err);
    } finally {
      setLoading(false);
    }
  };

  const title =
    mode === 'manage' ? t('groups.manageModal.title') : t('boxes.playModal.title');
  const submitLabel =
    mode === 'manage' ? t('groups.manageModal.submit') : t('boxes.playModal.submit');

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
          <h2 id="start-draw-title">{title}</h2>
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

        <p className={styles.hostNote}>
          {mode === 'manage'
            ? t('groups.manageModal.hostExplanation')
            : t('boxes.playModal.hostExplanation')}
        </p>
        <p className={styles.hint}>
          {requireAllParticipants
            ? t('boxes.playModal.participantsHintRequired')
            : t('boxes.playModal.participantsHintOptional')}
        </p>

        <div className={styles.credentials}>
          {credentials.map((credential, index) => {
            const isPrefilledSlot =
              hasExperiencesSession &&
              credential.email.trim().toLowerCase() === experiencesEmail.trim().toLowerCase();
            return (
              <div
                key={credential.participantId ?? `slot-${index}`}
                className={`${styles.credentialCard} ${
                  isPrefilledSlot ? styles.credentialCardPrefilled : ''
                }`}
              >
                <div className={styles.cardHeader}>
                  <p className={styles.cardTitle}>
                    {isPrefilledSlot
                      ? t('auth.experienceBox.prefilledYou')
                      : credential.displayName
                        ? credential.displayName
                        : t('auth.experienceBox.participant', { number: index + 1 })}
                  </p>
                  {canEditRoster && credentials.length > 1 && (
                    <button
                      type="button"
                      className={styles.removeParticipant}
                      disabled={loading}
                      onClick={() =>
                        setCredentials((current) => current.filter((_, itemIndex) => itemIndex !== index))
                      }
                    >
                      {t('auth.experienceBox.removeParticipant')}
                    </button>
                  )}
                </div>
                <label className={styles.field}>
                  <span>{t('auth.fields.email')}</span>
                  <JointLoginEmailInput
                    name={`joint-email-${index}`}
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
                {!isPrefilledSlot && (
                  <label className={styles.field}>
                    <span>{t('auth.fields.password')}</span>
                    <JointLoginSecretInput
                      name={`joint-secret-${index}`}
                      disabled={loading}
                      value={credential.password}
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
                )}
              </div>
            );
          })}
        </div>

        {canEditRoster && (
          <Button
            variant="secondary"
            fullWidth
            disabled={loading}
            onClick={() => setCredentials((current) => [...current, emptyCredential()])}
          >
            {t('auth.experienceBox.addParticipant')}
          </Button>
        )}

        {error && (
          <p className={styles.error} role="alert">
            {error}
          </p>
        )}

        <div className={styles.actions}>
          <Button fullWidth disabled={loading} onClick={() => void submit()}>
            {loading ? t('auth.loading') : submitLabel}
          </Button>
          <Button variant="secondary" fullWidth disabled={loading} onClick={onClose}>
            {t('boxes.playModal.cancel')}
          </Button>
        </div>
      </section>
    </div>
  );
}
