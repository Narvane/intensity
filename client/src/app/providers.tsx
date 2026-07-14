import type { PropsWithChildren } from 'react';
import { BrowserRouter } from 'react-router-dom';
import { isDemoMode } from '../content/demoCredentials';
import { DemoDeviceShell } from '../presentation/components/DemoDeviceShell';
import { SessionProvider } from './SessionProvider';
import { NavigationProvider } from './NavigationProvider';
import { ToastProvider } from './ToastProvider';
import { I18nProvider } from '../i18n/I18nProvider';

export function AppProviders({ children }: PropsWithChildren) {
  const content = (
    <ToastProvider>
      <SessionProvider>
        <NavigationProvider>{children}</NavigationProvider>
      </SessionProvider>
    </ToastProvider>
  );

  return (
    <BrowserRouter>
      <I18nProvider>
        {isDemoMode() ? <DemoDeviceShell>{content}</DemoDeviceShell> : content}
      </I18nProvider>
    </BrowserRouter>
  );
}
