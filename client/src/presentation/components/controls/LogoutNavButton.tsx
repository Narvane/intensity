import { useState } from 'react';
import { useAppLogout } from '@app/useAppLogout';
import { useI18n } from '../../../i18n/I18nContext';
import { LogoutConfirmDialog } from '../feedback/LogoutConfirmDialog';
import { NavButton } from './NavButton';

interface LogoutNavButtonProps {
  mode: 'EXPERIENCES' | 'EXPERIENCE_BOX';
  className?: string;
}

export function LogoutNavButton({ mode, className }: LogoutNavButtonProps) {
  const { t } = useI18n();
  const logout = useAppLogout(mode);
  const [open, setOpen] = useState(false);
  const [loggingOut, setLoggingOut] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const requestLogout = () => {
    setError(null);
    setOpen(true);
  };

  const confirmLogout = async () => {
    setLoggingOut(true);
    setError(null);
    try {
      await logout();
    } catch {
      setError(t('common.error'));
      setLoggingOut(false);
    }
  };

  return (
    <>
      <NavButton action="logout" className={className} onClick={requestLogout} />
      <LogoutConfirmDialog
        open={open}
        confirming={loggingOut}
        error={error}
        onConfirm={() => void confirmLogout()}
        onCancel={() => {
          if (!loggingOut) {
            setOpen(false);
          }
        }}
      />
    </>
  );
}
