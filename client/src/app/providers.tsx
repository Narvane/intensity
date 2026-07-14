import type { PropsWithChildren } from 'react';
import { BrowserRouter } from 'react-router-dom';
import { isDemoMode } from '../content/demoCredentials';
import { DemoBanner } from '../presentation/components/DemoBanner';
import { SessionProvider } from './SessionProvider';
import { NavigationProvider } from './NavigationProvider';
import { ToastProvider } from './ToastProvider';
import { I18nProvider } from '../i18n/I18nProvider';

export function AppProviders({ children }: PropsWithChildren) {
  return (
    <BrowserRouter>
      <I18nProvider>
        {isDemoMode() ? <DemoBanner /> : null}
        <ToastProvider>
          <SessionProvider>
            <NavigationProvider>{children}</NavigationProvider>
          </SessionProvider>
        </ToastProvider>
      </I18nProvider>
    </BrowserRouter>
  );
}
