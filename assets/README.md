# Brand assets

Official Intensity logos for the client app. The Vite build imports them from this folder via `client/src/content/brandAssets.ts` (`import.meta.glob` on `logo-icon` / `logo-wordmark`).

| File | Format | Usage |
|------|--------|--------|
| `logo-icon.png` | Square PNG | Favicon, bootstrap/brand mark contexts; kept in sync with `client/assets/icon.png` by `npm run generate:native-assets` |
| `logo-wordmark.png` | Horizontal PNG | Authentication header and other horizontal brand contexts |
| `logo-transparent.png` | Square PNG (transparent) | Source-aligned with the translucent splash logo; not imported by `brandAssets.ts` |

SVG variants (`logo-icon.svg`, `logo-wordmark.svg`) are supported by the import glob if added later; PNG is the current canonical format. If a required logo file is missing, the client shows a design-system placeholder until the asset is added.

Native launcher and splash generation uses `client/assets/icon.png` and `client/assets/icon-translucid.png` (see `client/STORE_RELEASE.md`).
