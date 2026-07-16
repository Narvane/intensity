import { useI18n } from '../../../i18n/I18nContext';
import { DestructiveConfirmDialog } from './DestructiveConfirmDialog';

interface LogoutConfirmDialogProps {
  open: boolean;
  confirming: boolean;
  error: string | null;
  onConfirm: () => void;
  onCancel: () => void;
}

export function LogoutConfirmDialog({
  open,
  confirming,
  error,
  onConfirm,
  onCancel,
}: LogoutConfirmDialogProps) {
  const { t } = useI18n();

  return (
    <DestructiveConfirmDialog
      open={open}
      titleId="logout-confirm-title"
      title={t('session.logoutDialog.title')}
      message={t('session.logoutDialog.message')}
      confirmLabel={t('session.logoutDialog.confirm')}
      cancelLabel={t('session.logoutDialog.cancel')}
      confirming={confirming}
      error={error}
      onConfirm={onConfirm}
      onCancel={onCancel}
    />
  );
}
