import { copyFileSync, mkdirSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { spawnSync } from 'node:child_process';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const sourceIcon = resolve(root, '..', 'assets', 'logo-icon.png');
const targetDir = resolve(root, 'assets');
const targetIcon = resolve(targetDir, 'icon.png');

mkdirSync(targetDir, { recursive: true });
copyFileSync(sourceIcon, targetIcon);

const args = [
  '@capacitor/assets',
  'generate',
  '--android',
  '--ios',
  '--iconBackgroundColor',
  '#fff7ed',
  '--splashBackgroundColor',
  '#fff7ed',
];

const result = spawnSync(process.platform === 'win32' ? 'npx.cmd' : 'npx', args, {
  cwd: root,
  stdio: 'inherit',
});

if (result.status !== 0) {
  process.exit(result.status ?? 1);
}
