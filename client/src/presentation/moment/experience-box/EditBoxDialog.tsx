import { useEffect, useState } from 'react';
import type { Box } from '@domain/box/boxTypes';
import { useModalDialog } from '@presentation/hooks/useModalDialog';
import { useI18n } from '../../../i18n/I18nContext';
import { Button } from '../../components/controls/Button';
import { Checkbox } from '../../components/controls/Checkbox';
import styles from './EditBoxDialog.module.css';

interface EditBoxDialogProps {
  box: Box | null;
  saving: boolean;
  error: string | null;
  onConfirm: (input: { name: string; requireAllParticipants: boolean }) => void;
  onCancel: () => void;
}

export function EditBoxDialog({
  box,
  saving,
  error,
  onConfirm,
  onCancel,
}: EditBoxDialogProps) {
  const { t } = useI18n();
  const open = Boolean(box);
  const { dialogRef, cancelRef } = useModalDialog(open, onCancel, saving);
  const [name, setName] = useState('');
  const [requireAllParticipants, setRequireAllParticipants] = useState(false);

  useEffect(() => {
    if (!box) {
      return;
    }
    setName(box.name);
    setRequireAllParticipants(box.requireAllParticipants);
  }, [box]);

  if (!box) {
    return null;
  }

  const trimmedName = name.trim();
  const canSubmit = trimmedName.length > 0 && !saving;

  return (
    <div
      className={styles.backdrop}
      role="presentation"
      onClick={saving ? undefined : onCancel}
    >
      <section
        ref={dialogRef}
        className={styles.dialog}
        role="dialog"
        aria-modal="true"
        aria-labelledby="edit-box-title"
        onClick={(event) => event.stopPropagation()}
      >
        <h2 id="edit-box-title">{t('boxHome.editDialog.title')}</h2>

        <label className={styles.field}>
          <span>{t('createBox.nameLabel')}</span>
          <input
            value={name}
            maxLength={80}
            disabled={saving}
            placeholder={t('createBox.namePlaceholder')}
            onChange={(event) => setName(event.target.value)}
          />
        </label>

        <label className={styles.checkRow}>
          <Checkbox
            checked={requireAllParticipants}
            disabled={saving}
            onChange={(event) => setRequireAllParticipants(event.target.checked)}
          />
          <span>
            <strong>{t('createBox.requireAllParticipants')}</strong>
            <span className={styles.checkHint}>{t('createBox.requireAllParticipantsHint')}</span>
          </span>
        </label>

        {error && (
          <p className={styles.error} role="alert">
            {error}
          </p>
        )}

        <div className={styles.actions}>
          <Button
            fullWidth
            disabled={!canSubmit}
            onClick={() =>
              onConfirm({
                name: trimmedName,
                requireAllParticipants,
              })
            }
          >
            {t('boxHome.editDialog.confirm')}
          </Button>
          <Button
            ref={cancelRef}
            variant="secondary"
            fullWidth
            disabled={saving}
            onClick={onCancel}
          >
            {t('boxHome.editDialog.cancel')}
          </Button>
        </div>
      </section>
    </div>
  );
}
