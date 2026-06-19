import type { CapacitorConfig } from '@capacitor/cli';

// Local dev uses an HTTP API (e.g. http://192.168.x.x:8080). androidScheme must be
// 'http' or the WebView blocks those requests as mixed content (https page → http API).
// Store builds against HTTPS production API can switch this back to 'https'.
const config: CapacitorConfig = {
  appId: 'com.intensity.app',
  appName: 'Intensity',
  webDir: 'dist',
  server: {
    androidScheme: 'http',
    cleartext: true,
  },
  android: {
    allowMixedContent: true,
  },
  plugins: {
    StatusBar: {
      overlaysWebView: false,
      style: 'DARK',
      backgroundColor: '#fff7ed',
    },
  },
};

export default config;
