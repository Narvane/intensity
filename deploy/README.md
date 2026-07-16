# Production deploy (VPS)

Production stack per **DT-07** and **DT-08**: Caddy (TLS) + Spring Boot API + PostgreSQL on a single VPS.

Public **demo** stack (same VPS, isolated DB): see [Public demo](#public-demo) below.

## Folder map (two stacks, one folder)

This folder holds **two** deployable stacks side by side. File paths are referenced by the VPS (crontab, webhook, systemd) — do not move or rename them.

| Stack | Files |
|-------|-------|
| **Production** | `docker-compose.prod.yml`, `Caddyfile`, `deploy.sh`, `.env.example`, `webhook/` (CI-triggered redeploy), `vps.md` |
| **Public demo** | `docker-compose.demo.yml`, `deploy-demo.sh`, `reset-demo.sh`, `cron-reset-demo.sh`, `publish-demo-client.sh`, `.env.demo.example`, `demo-nginx.conf`, `demo-web/` (published SPA build) |
| **Shared** | `caddy-snippets/` (demo writes its snippet here; Caddy from prod compose loads it) |

## Prerequisites

- Linux VPS with Docker 24+ and Compose v2
- DNS `A` records for `API_DOMAIN` and `APP_DOMAIN` pointing to the VPS
- GitHub repo with API CI pushing to GHCR (`ghcr.io/<owner>/<repo>/api`, e.g. `ghcr.io/<owner>/intesity-2/api`)
- Repository secrets: `DEPLOY_WEBHOOK_URL`, `DEPLOY_WEBHOOK_SECRET` (optional)

## First-time setup

1. Clone this repo on the VPS (e.g. `/opt/intensity`).

2. Configure secrets:

   ```bash
   cd deploy
   cp .env.example .env
   # Edit .env — strong passwords, real domains, GHCR image path,
   # INTENSITY_RESEND_* and INTENSITY_APP_BASE_URL for password-reset email
   ```

3. Make scripts executable:

   ```bash
   chmod +x deploy.sh deploy-demo.sh reset-demo.sh cron-reset-demo.sh publish-demo-client.sh webhook/receive.sh
   ```

4. Log in to GHCR on the VPS (once):

   ```bash
   echo "$GITHUB_PAT" | docker login ghcr.io -u YOUR_GITHUB_USER --password-stdin
   ```

5. Start the stack:

   ```bash
   ./deploy.sh
   ```

   If upgrading an older install: the Docker network is now fixed as `intensity` (required for demo). One-time recreate if Compose complains about the network:

   ```bash
   docker compose -f docker-compose.prod.yml --env-file .env down
   ./deploy.sh
   ```

6. Verify:

   ```bash
   curl -fsS "https://$API_DOMAIN/actuator/health"
   curl -fsS "https://$APP_DOMAIN/.well-known/assetlinks.json"
   curl -fsS "https://$APP_DOMAIN/.well-known/apple-app-site-association"
   ```

   Update placeholder values in `client/deep-link/.well-known/` (Android SHA256 fingerprint, Apple Team ID) **before** store submission.

## Automated deploy (CI webhook)

On push to `master`, GitHub Actions builds the Docker image, pushes to GHCR, and POSTs to `DEPLOY_WEBHOOK_URL`:

```json
{ "image": "ghcr.io/<owner>/intesity-2/api", "sha": "<commit-sha>" }
```

### Option A — Manual pull (simplest)

After CI succeeds, SSH to the VPS and run:

```bash
cd /opt/intensity/deploy
./deploy.sh
```

Or pin a specific SHA:

```bash
./deploy.sh ghcr.io/<owner>/intesity-2/api abc123def456
```

### Option B — Webhook listener

Install [webhook](https://github.com/adnanh/webhook) on the VPS, point it at `deploy/webhook/hooks.json`, and set:

- `DEPLOY_WEBHOOK_URL` → `https://your-vps:9000/hooks/intensity-api-deploy`
- `DEPLOY_WEBHOOK_SECRET` → same value as in `deploy/.env`

Example systemd unit:

```ini
[Unit]
Description=Intensity deploy webhook
After=network.target

[Service]
ExecStart=/usr/bin/webhook -hooks /opt/intensity/deploy/webhook/hooks.json -port 9000 -verbose
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

Restrict port 9000 to GitHub Actions egress or protect with a reverse proxy + secret header.

## Rollback

Pin the previous image tag in `.env`:

```env
API_IMAGE=ghcr.io/<owner>/intesity-2/api:PREVIOUS_SHA
```

Then run `./deploy.sh`.

One-time VPS migration notes (previous stack → Intensity) live in [`vps.md`](vps.md).

## Stack layout

```
Internet :443
    ↓
  Caddy (proxy, production compose)
    ├── api.<domain>       → intensity-api:8080
    ├── app.<domain>       → /.well-known/* (deep links)
    ├── demo-intensity-api.<domain>  → intensity-demo-api:8080   (optional)
    └── demo-intensity.<domain>      → intensity-demo-web:80     (optional)
Postgres prod + Postgres demo (separate compose projects / volumes)
```

Compose project names: production network `intensity` (fixed); demo project `intensity-demo`.

## Public demo

Same GHCR API image as production, profile `demo` (seed Leo / Maya / Nico). **Does not** share the production database or JWT secret.

### First-time demo setup

1. DNS `A` records for `DEMO_API_DOMAIN` and `DEMO_APP_DOMAIN` → VPS.

2. Configure demo secrets:

   ```bash
   cd /opt/intensity/deploy
   cp .env.demo.example .env.demo
   # Edit — use a different POSTGRES_PASSWORD and INTENSITY_JWT_SECRET than production
   # API_IMAGE should match the production image you want to show
   ```

3. Production must already be up (`./deploy.sh`) so network `intensity` and Caddy exist.

4. Build and publish the demo SPA (Node 22+ on the VPS or your machine):

   ```bash
   chmod +x publish-demo-client.sh
   ./publish-demo-client.sh
   ```

5. Start demo (writes Caddy snippet + reloads proxy):

   ```bash
   chmod +x deploy-demo.sh reset-demo.sh cron-reset-demo.sh
   ./deploy-demo.sh
   ```

6. Verify:

   ```bash
   curl -fsS "https://$DEMO_API_DOMAIN/actuator/health"
   curl -fsSI "https://$DEMO_APP_DOMAIN/"
   curl -fsSI "https://$DEMO_APP_DOMAIN/join"
   # OpenAPI UI (demo profile)
   curl -fsSI "https://$DEMO_API_DOMAIN/swagger-ui/index.html"
   ```

Interactive SPA with banner + sample-account shortcuts. Sample login: `leo@demo.intensity.app` / `demo1234`.

Pin a SHA like production:

```bash
./deploy-demo.sh ghcr.io/<owner>/intesity-2/api abc123def456
```

### Daily reset

```bash
./reset-demo.sh
```

Cron (03:00 UTC):

```cron
0 3 * * * /opt/intensity/deploy/cron-reset-demo.sh >> /var/log/intensity-demo-reset.log 2>&1
```

### Updating demo after CI

Production webhook does **not** restart the demo stack. After a new API image is on GHCR:

```bash
./deploy-demo.sh          # same SHA/tag as prod if desired
```

After client demo UI changes:

```bash
./publish-demo-client.sh
docker compose -f docker-compose.demo.yml --env-file .env.demo up -d web
```

## Order of release (DT-10)

1. Deploy API to VPS (`./deploy.sh` or CI webhook)
2. Build and submit client store release (`client/STORE_RELEASE.md`)

Never ship a client build that calls new API endpoints before the API is live.
