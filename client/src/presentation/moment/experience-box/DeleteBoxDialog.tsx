import type { Box } from '@domain/box/boxTypes';
import { useI18n } from '../../../i18n/I18nContext';
import { useOnlineStatus } from '@presentation/hooks/useOnlineStatus';
import { DestructiveConfirmDialog } from '../../components/feedback/DestructiveConfirmDialog';

interface DeleteBoxDialogProps {
  box: Box | null;
  deleting: boolean;
  error: string | null;
  onConfirm: () => void;
  onCancel: () => void;
}

export function DeleteBoxDialog({
  box,
  deleting,
  error,
  onConfirm,
  onCancel,
}: DeleteBoxDialogProps) {
  const { t } = useI18n();
  const online = useOnlineStatus();

  return (
    <DestructiveConfirmDialog
      open={Boolean(box)}
      titleId="delete-box-title"
      title={box ? t('boxHome.deleteDialog.title', { name: box.name }) : ''}
      message={
        box
          ? t('boxHome.deleteDialog.message', {
              count: String(box.experienceCount),
            })
          : ''
      }
      confirmLabel={t('boxHome.deleteDialog.confirm')}
      cancelLabel={t('boxHome.deleteDialog.cancel')}
      confirming={deleting}
      error={error}
      offlineBlocked={!online}
      offlineMessage={t('common.offlineDestructiveBlocked')}
      onConfirm={onConfirm}
      onCancel={onCancel}
    />
  );
}
