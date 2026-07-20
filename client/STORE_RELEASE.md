# Store release checklist (DT-09)

Manual release path for Google Play (AAB) and Apple App Store (IPA). Run **after** the production API is deployed.

## 1. Pre-release configuration

- [ ] `client/.env.production` — `VITE_API_URL` points to live HTTPS API; `VITE_INVITE_BASE_URL` points to the invite host (e.g. `https://app.example/join`)
- [ ] Launcher icons — after changing `client/assets/icon.png` (launcher) or `client/assets/icon-translucid.png` (splash), run `npm run generate:native-assets` in `client/` (Android adaptive + iOS AppIcon)
- [ ] `deploy/.env` on VPS — domains, JWT secret, Postgres password set
- [ ] Deep link files published and verified:
  - [ ] `client/deep-link/.well-known/assetlinks.json` — release keystore SHA256 fingerprint
  - [ ] `client/deep-link/.well-known/apple-app-site-association` — Apple Team ID
  - [ ] `curl` both URLs over HTTPS on `app.<domain>`
- [ ] Bump version in `client/package.json`, Android `versionCode` / `versionName`, iOS `MARKETING_VERSION` / `CURRENT_PROJECT_VERSION`

## 2. Build web assets + sync native projects

From `client/`:

```bash
npm install
npm test
npm run build:store
```

This runs `vite build --mode production` (bakes `VITE_API_URL`) and `cap sync` with `STORE_BUILD=true` (HTTPS WebView scheme for production).

## 3. Android (Google Play)

1. Open `client/android` in Android Studio
2. **Build → Generate Signed Bundle / APK** → Android App Bundle (AAB)
3. Upload to Play Console (internal testing track first)
4. Confirm App Links: **Digital Asset Links** verified for `app.<domain>/join`
5. Store listing: screenshots, privacy policy URL (`https://app.<domain>/privacy`), data deletion URL (`https://app.<domain>/delete-account`), content rating

## 4. iOS (App Store Connect)

1. Open `client/ios/App/App.xcworkspace` in Xcode
2. Select release signing team + provisioning profile
3. **Product → Archive** → upload to App Store Connect
4. Confirm Associated Domains entitlement: `applinks:app.<domain>`
5. TestFlight smoke test on device

## 5. Manual QA (happy path)

Run on **production API** with test allowlist accounts:

1. Register / login (Experiences + Experience Box dual login)
2. Create box (Experience Box session and Experiences mode create route)
3. Generate invite → accept on second device (code + deep link)
4. Contribute experience (Experiences session)
5. Shared moment: draw → reveal (aria-live announcements)
6. Delete secondary box → confirm cascade dialog
7. Leave group → logout → unknown session flow
8. Offline banner visible when airplane mode; destructive actions blocked

## 6. Submit for review

- [ ] Release notes (EN minimum; PT-BR / IT if localized listing)
- [ ] API deployed **before** store rollout completes
- [ ] Monitor VPS logs after first installs: `docker compose -f deploy/docker-compose.prod.yml logs -f api`

## Rollback

- **Client:** halt staged rollout in Play Console / App Store Connect; previous build remains available
- **API:** pin previous GHCR SHA in `deploy/.env` and run `./deploy.sh` (see @ref:deploy-readme)
