import { useEffect, useState } from 'react';
import type { GroupAccent } from '@domain/box/boxTypes';
import { useModalDialog } from '@presentation/hooks/useModalDialog';
import { useI18n } from '../../i18n/I18nContext';
import { Button } from '../components/Button';
import { GroupColorPicker } from './GroupColorPicker';
import styles from './GroupFormDialog.module.css';

interface GroupFormDialogProps {
  open: boolean;
  mode: 'create' | 'edit';
  initialName?: string;
  initialColor?: GroupAccent;
  saving: boolean;
  error: string | null;
  onConfirm: (input: { name: string; color: GroupAccent }) => void;
  onCancel: () => void;
}

export function GroupFormDialog({
  open,
  mode,
  initialName = '',
  initialColor = 'coral',
  saving,
  error,
  onConfirm,
  onCancel,
}: GroupFormDialogProps) {
  const { t } = useI18n();
  const { dialogRef, cancelRef } = useModalDialog(open, onCancel, saving);
  const [name, setName] = useState(initialName);
  const [color, setColor] = useState<GroupAccent>(initialColor);

  useEffect(() => {
    if (!open) {
      return;
    }
    setName(initialName);
    setColor(initialColor);
  }, [initialColor, initialName, open]);

  if (!open) {
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
        aria-labelledby="group-form-title"
        onClick={(event) => event.stopPropagation()}
      >
        <h2 id="group-form-title">
          {mode === 'create' ? t('groups.createDialog.title') : t('groups.editDialog.title')}
        </h2>
        {mode === 'create' && (
          <p className={styles.message}>{t('groups.createDialog.message')}</p>
        )}

        <label className={styles.field}>
          <span>{t('groups.form.nameLabel')}</span>
          <input
            value={name}
            maxLength={120}
            placeholder={t('groups.form.namePlaceholder')}
            onChange={(event) => setName(event.target.value)}
          />
        </label>

        <div className={styles.field}>
          <span>{t('groups.form.colorLabel')}</span>
          <GroupColorPicker
            value={color}
            label={t('groups.form.colorLabel')}
            onChange={setColor}
          />
        </div>

        {error && (
          <p className={styles.error} role="alert">
            {error}
          </p>
        )}

        <div className={styles.actions}>
          <Button ref={cancelRef} variant="secondary" fullWidth disabled={saving} onClick={onCancel}>
            {t('groups.createDialog.cancel')}
          </Button>
          <Button
            fullWidth
            disabled={!canSubmit}
            onClick={() => onConfirm({ name: trimmedName, color })}
          >
            {saving
              ? t('common.loading')
              : mode === 'create'
                ? t('groups.createDialog.confirm')
                : t('groups.editDialog.confirm')}
          </Button>
        </div>
      </section>
    </div>
  );
}
