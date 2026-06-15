# Intensity

Mobile app for collecting experiences and running shared draw rituals in groups.

**Canonical specification:** [`newdocs/en/`](newdocs/en/)  
**Development plan (slices, DoR/DoD):** [`newdocs/plano-desenvolvimento-ia.md`](newdocs/plano-desenvolvimento-ia.md)

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
├── openapi/      Contract-first OpenAPI v1
└── newdocs/      Product and engineering documentation
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

### Client

```bash
cd client
npm install
npm run dev
```

- Dev server: http://localhost:5173
- API URL: `VITE_API_URL=http://localhost:8080` (see `client/.env.development`)

### Mobile (optional)

```bash
cd client
npm run build
npx cap sync
```

Open the native project in Android Studio or Xcode for emulator/device runs.  
Android emulator API host: `10.0.2.2:8080`; physical device: your machine LAN IP.

## CI / deploy (API)

GitHub Actions runs `./mvnw test`, builds the Docker image, and pushes to GHCR on push to `master`.

**Required repository secrets:**

| Secret | Purpose |
|--------|---------|
| `GHCR_TOKEN` | Push container image to GitHub Container Registry |
| `DEPLOY_WEBHOOK_URL` | POST URL on VPS to pull and restart API after image push |
| `DEPLOY_WEBHOOK_SECRET` | Optional shared secret for webhook authentication |

Production deploy is triggered by the CI webhook; VPS runs `docker compose pull && up -d` (manual setup documented in `newdocs/en/engineering-and-operations/development-process.md`).

## Stack

- **API:** Java 21, Spring Boot 3.5, Maven, Hibernate, Flyway, PostgreSQL 16
- **Client:** Node 22, TypeScript 5.7, React 19, Vite 6, Capacitor 7
- **Infra:** Docker Compose, GitHub Actions → GHCR → VPS webhook

See [`newdocs/en/engineering-and-operations/tools.md`](newdocs/en/engineering-and-operations/tools.md) for the full inventory.
