<p>
  <img src="assets/logo-icon.png" alt="Intensity icon" width="120" />
  <img src="assets/logo-wordmark.png" alt="Intensity" width="180" />
</p>

# Intensity

Intensity helps couples and friends create deeper connection through shared experiences. Collect unusual ideas in themed **boxes**, rate how intense each one feels, and when you're together **draw** one at random and **reveal** it deliberately — after aligning on mood and limits. Groups form by playing together and grow through **invites**. The rhythm is simple: collect → draw → live the moment. Not a task list or a social network — **connection, intensity, and discovery**, lived with presence.

## Live demo

Try the product in the browser (no app install). Sample data resets daily.

| | |
|---|---|
| **App** | https://demo-intensity.narvane.com.br |
| **API** | https://demo-intensity-api.narvane.com.br |
| **Accounts** | `leo@demo.intensity.app` · `maya@demo.intensity.app` · `nico@demo.intensity.app` |
| **Password** | `demo1234` |

Tour tip: Experiences as Leo → open both groups; Experience Box as Leo+Maya (couple) or Leo+Maya+Nico (trip). Ops: @ref:demo-plan — [`demo-plan.md`](demo-plan.md).

**Product documentation**

| | Locale | Spec |
|---|--------|------|
| 🇧🇷🇵🇹 | Portuguese (Brazil / Portugal) | @ref:docs-pt-br — [docs/pt-br/](docs/pt-br/) |
| 🇬🇧 | English (UK) | @ref:docs-en — [docs/en/](docs/en/) *(canonical)* |
| 🇮🇹 | Italian | @ref:docs-it — [docs/it/](docs/it/) |

**Reference map:** @ref:refs — [`docs/refs.yaml`](docs/refs.yaml)

## Prerequisites

| Tool | Version |
|------|---------|
| JDK | 21 |
| Node.js | 22 LTS |
| npm | 10+ |
| Docker | 24+ with Compose v2 |

## Repository layout

```
├── api/          Spring Boot REST API (Java 21)
├── client/       React + Capacitor mobile app
├── deploy/       Production + public demo VPS stack (Compose + Caddy)
├── openapi/      Contract-first OpenAPI v1
├── assets/       Brand logos imported by the Vite client
├── agents/       Agent prompts for backlog tasks
├── scripts/      Doc reference validation (`validate-refs.py`)
├── docs/         Product & engineering docs (en / pt-br / it)
├── demo-plan.md  Public demo environment plan
└── backlog.md    Product backlog
```

## Local development

### API

```bash
cd api
docker compose up -d
./mvnw spring-boot:run
```

- Health: http://localhost:8080/actuator/health
- OpenAPI (dev): http://localhost:8080/v3/api-docs

**Demo profile (sample world):** create/reset DB `intensity_demo` via `api/scripts/reset-demo-db.sh` (or first-time compose init), then:

```bash
SPRING_PROFILES_ACTIVE=demo ./mvnw spring-boot:run
```

Accounts: `leo@demo.intensity.app` / `maya@demo.intensity.app` / `nico@demo.intensity.app` — password `demo1234`. See @ref:demo-plan.

### Client

```bash
cd client
npm install
npm run dev
```

- Dev server: http://localhost:5173
- API URL: `VITE_API_URL=http://localhost:8080` (see `client/.env.development`)
- Optional: `VITE_API_PROXY_TARGET` proxies `/v1` in Vite; production also uses `VITE_INVITE_BASE_URL` for invite links
- **Demo SPA build:** `npm run build:demo` (uses `client/.env.demo`); publish with `deploy/publish-demo-client.sh`

### Mobile (optional)

```bash
cd client
npm run build
npx cap sync
```

Open the native project in Android Studio or Xcode for emulator/device runs.  
Android emulator API host: `10.0.2.2:8080`; physical device: your machine LAN IP.

## Production deploy

### API (automated CI → VPS)

GitHub Actions runs `./mvnw test`, builds the Docker image, and pushes to GHCR on push to `master`.

**Required repository secrets:**

| Secret | Purpose |
|--------|---------|
| `DEPLOY_WEBHOOK_URL` | POST URL on VPS to pull and restart API after image push |
| `DEPLOY_WEBHOOK_SECRET` | Shared secret sent as `X-Deploy-Secret` header |

GHCR push uses the built-in `GITHUB_TOKEN` (no extra PAT required for public repos).

**VPS setup:** see @ref:deploy-readme — [deploy/README.md](deploy/README.md) — copy `deploy/.env.example` → `.env`, run `./deploy.sh`.

**Public demo (optional):** same VPS, isolated DB — `deploy/.env.demo.example` → `.env.demo`, then `./deploy-demo.sh`. See @ref:demo-plan.

Order: **deploy API first**, then store client release.

### Client (manual store release)

1. Set `client/.env.production` → `VITE_API_URL` and `VITE_INVITE_BASE_URL` for your domains
2. `npm run build:store` (production Vite build + Capacitor sync with HTTPS scheme)
3. Sign and upload in Android Studio / Xcode

Full checklist: @ref:store-release — [client/STORE_RELEASE.md](client/STORE_RELEASE.md)

## Stack

- **API:** Java 21, Spring Boot 3.5, Maven, Hibernate, Flyway, PostgreSQL 16
- **Client:** Node 22, TypeScript 5.7, React 19, Vite 6, Capacitor 7
- **Infra:** Docker Compose, GitHub Actions → GHCR → VPS webhook, Caddy TLS; optional public demo stack on the same VPS

See @ref:en-tools — [tools inventory](docs/en/engineering-and-operations/tools.md) for the full stack.

Validate doc references: `python3 scripts/validate-refs.py`
