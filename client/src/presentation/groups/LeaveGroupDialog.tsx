import { useEffect, useRef } from 'react';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import styles from './LeaveGroupDialog.module.css';

interface LeaveGroupDialogProps {
  open: boolean;
  memberCount: number;
  leavingCount: number;
  leaving: boolean;
  error: string | null;
  onConfirm: () => void;
  onCancel: () => void;
}

export function LeaveGroupDialog({
  open,
  memberCount,
  leavingCount,
  leaving,
  error,
  onConfirm,
  onCancel,
}: LeaveGroupDialogProps) {
  const { t } = useI18n();
  const cancelButtonRef = useRef<HTMLButtonElement>(null);
  const deletesGroup = memberCount <= leavingCount;

  useEffect(() => {
    if (!open) {
      return;
    }

    cancelButtonRef.current?.focus();
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';

    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && !leaving) {
        onCancel();
      }
    };

    window.addEventListener('keydown', onKeyDown);
    return () => {
      document.body.style.overflow = previousOverflow;
      window.removeEventListener('keydown', onKeyDown);
    };
  }, [leaving, onCancel, open]);

  if (!open) {
    return null;
  }

  const messageKey = deletesGroup
    ? 'groups.leaveDialog.lastMemberMessage'
    : leavingCount > 1
      ? 'groups.leaveDialog.sessionMessage'
      : 'groups.leaveDialog.message';

  return (
    <div className={styles.backdrop} role="presentation" onClick={leaving ? undefined : onCancel}>
      <section
        className={styles.dialog}
        role="dialog"
        aria-modal="true"
        aria-labelledby="leave-group-title"
        onClick={(event) => event.stopPropagation()}
      >
        <h2 id="leave-group-title">{t('groups.leaveDialog.title')}</h2>
        <p className={styles.message}>{t(messageKey)}</p>

        {error && (
          <p className={styles.error} role="alert">
            {error}
          </p>
        )}

        <div className={styles.actions}>
          <Button ref={cancelButtonRef} variant="secondary" fullWidth disabled={leaving} onClick={onCancel}>
            {t('groups.leaveDialog.cancel')}
          </Button>
          <Button fullWidth disabled={leaving} className={styles.danger} onClick={onConfirm}>
            {leaving ? t('common.loading') : t('groups.leaveDialog.confirm')}
          </Button>
        </div>
      </section>
    </div>
  );
}
