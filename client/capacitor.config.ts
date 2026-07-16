/// <reference types="@capacitor-community/safe-area" />
import type { CapacitorConfig } from '@capacitor/cli';

// Local dev uses an HTTP API (e.g. http://192.168.x.x:8080). androidScheme must be
// 'http' or the WebView blocks those requests as mixed content (https page → http API).
// Store builds against HTTPS production API can switch this back to 'https'.
const isStoreBuild = process.env.STORE_BUILD === 'true';

const config: CapacitorConfig = {
  appId: 'com.intensity.app',
  appName: 'Intensity',
  webDir: 'dist',
  server: isStoreBuild
    ? {
        androidScheme: 'https',
      }
    : {
        androidScheme: 'http',
        cleartext: true,
      },
  android: {
    // Safe-area plugin owns inset handling; avoid Capacitordouble-margining.
    adjustMarginsForEdgeToEdge: 'disable',
    ...(isStoreBuild ? {} : { allowMixedContent: true }),
  },
  plugins: {
    // Route fetch/XHR through native HTTP so Android WebView CORS / flaky
    // cross-origin failures do not look like "lost API connection".
    CapacitorHttp: {
      enabled: true,
    },
    SafeArea: {
      statusBarStyle: 'LIGHT',
      navigationBarStyle: 'LIGHT',
      initialViewportFitCover: true,
      detectViewportFitCoverChanges: true,
    },
  },
};

export default config;
