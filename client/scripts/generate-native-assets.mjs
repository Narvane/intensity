import { copyFileSync, mkdirSync, rmSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import { spawnSync } from 'node:child_process';
import sharp from 'sharp';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const assetsDir = resolve(root, 'assets');
const buildDir = resolve(assetsDir, '.native-build');
const launcherSource = resolve(assetsDir, 'icon.png');
const splashSource = resolve(assetsDir, 'icon-translucid.png');
const brandIconTarget = resolve(root, '..', 'assets', 'logo-icon.png');

const BACKGROUND = '#fff7ed';
const SPLASH_SIZE = 2732;
const SPLASH_LOGO_SCALE = 0.32;

async function buildSplash(outputPath) {
  const logoSize = Math.round(SPLASH_SIZE * SPLASH_LOGO_SCALE);
  const logo = await sharp(splashSource)
    .resize(logoSize, logoSize, { fit: 'contain', background: { r: 0, g: 0, b: 0, alpha: 0 } })
    .png()
    .toBuffer();

  await sharp({
    create: {
      width: SPLASH_SIZE,
      height: SPLASH_SIZE,
      channels: 4,
      background: BACKGROUND,
    },
  })
    .composite([{ input: logo, gravity: 'center' }])
    .png()
    .toFile(outputPath);
}

rmSync(buildDir, { recursive: true, force: true });
mkdirSync(buildDir, { recursive: true });

copyFileSync(launcherSource, resolve(buildDir, 'icon.png'));
copyFileSync(launcherSource, brandIconTarget);
await buildSplash(resolve(buildDir, 'splash.png'));

const args = [
  '@capacitor/assets',
  'generate',
  '--android',
  '--ios',
  '--assetPath',
  'assets/.native-build',
  '--iconBackgroundColor',
  BACKGROUND,
  '--splashBackgroundColor',
  BACKGROUND,
];

const result = spawnSync('npx', args, {
  cwd: root,
  stdio: 'inherit',
  shell: true,
});

if (result.status !== 0) {
  process.exit(result.status ?? 1);
}
