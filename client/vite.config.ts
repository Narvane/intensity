/// <reference types="vitest/config" />
import fs from 'node:fs';
import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'node:path';

function copyPrivacyPolicyPlugin() {
  return {
    name: 'copy-privacy-policy',
    closeBundle() {
      const source = path.resolve(__dirname, 'deep-link/privacy/index.html');
      const destination = path.resolve(__dirname, 'dist/privacy/index.html');
      fs.mkdirSync(path.dirname(destination), { recursive: true });
      fs.copyFileSync(source, destination);
    },
  };
}

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const apiProxyTarget = env.VITE_API_PROXY_TARGET?.trim();

  return {
    plugins: [react(), copyPrivacyPolicyPlugin()],
    resolve: {
      alias: {
        '@app': path.resolve(__dirname, 'src/app'),
        '@domain': path.resolve(__dirname, 'src/domain'),
        '@adapters': path.resolve(__dirname, 'src/adapters'),
        '@presentation': path.resolve(__dirname, 'src/presentation'),
        '@i18n': path.resolve(__dirname, 'src/i18n'),
        '@content': path.resolve(__dirname, 'src/content'),
      },
    },
    server: {
      port: 5173,
      fs: {
        allow: [path.resolve(__dirname, '..')],
      },
      proxy: apiProxyTarget
        ? {
            '/v1': {
              target: apiProxyTarget,
              changeOrigin: true,
              secure: true,
            },
          }
        : undefined,
    },
    test: {
      environment: 'node',
      env: {
        VITE_API_URL: 'http://localhost:8080',
      },
    },
  };
});
