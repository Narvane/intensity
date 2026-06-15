import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.intensity.app',
  appName: 'Intensity',
  webDir: 'dist',
  server: {
    androidScheme: 'https',
  },
};

export default config;
