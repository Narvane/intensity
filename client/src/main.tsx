import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { Capacitor } from '@capacitor/core';
import { StatusBar, Style } from '@capacitor/status-bar';
import { AppProviders } from '@app/providers';
import { AppRouter } from '@app/routes';
import '@presentation/styles/global.css';

const APP_BACKGROUND = '#fff7ed';

async function initNativeChrome() {
  if (!Capacitor.isNativePlatform()) {
    return;
  }

  if (Capacitor.getPlatform() === 'android') {
    await StatusBar.setOverlaysWebView({ overlay: false });
    await StatusBar.setBackgroundColor({ color: APP_BACKGROUND });
  }

  await StatusBar.setStyle({ style: Style.Dark });
}

void initNativeChrome();

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AppProviders>
      <AppRouter />
    </AppProviders>
  </StrictMode>,
);
