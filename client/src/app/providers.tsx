import type { PropsWithChildren } from 'react';
import { BrowserRouter } from 'react-router-dom';
import { I18nProvider } from '../i18n/I18nProvider';

export function AppProviders({ children }: PropsWithChildren) {
  return (
    <BrowserRouter>
      <I18nProvider>{children}</I18nProvider>
    </BrowserRouter>
  );
}
