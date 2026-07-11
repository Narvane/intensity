import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { Capacitor } from '@capacitor/core';
import { SafeArea, SystemBarsStyle } from '@capacitor-community/safe-area';
import { AppProviders } from '@app/providers';
import { AppRouter } from '@app/routes';
import { getBrandIconUrl } from './content/brandAssets';
import '@presentation/styles/global.css';

function applyBrandFavicon() {
  const iconUrl = getBrandIconUrl();
  if (!iconUrl) {
    return;
  }

  const existing = document.querySelector<HTMLLinkElement>('link[rel="icon"]');
  const link = existing ?? document.createElement('link');
  link.rel = 'icon';
  link.type = 'image/png';
  link.href = iconUrl;
  if (!existing) {
    document.head.appendChild(link);
  }
}

async function initNativeChrome() {
  if (!Capacitor.isNativePlatform()) {
    return;
  }

  // Edge-to-edge + CSS env(safe-area-inset-*) is the single inset contract.
  // @capacitor-community/safe-area polyfills broken Android WebViews.
  await SafeArea.setSystemBarsStyle({ style: SystemBarsStyle.Light });
}

void initNativeChrome();
applyBrandFavicon();

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AppProviders>
      <AppRouter />
    </AppProviders>
  </StrictMode>,
);
