# Platforms and Environments

This document describes where Intensity runs — execution platforms, deployment environments, and device usage patterns. It is written for architects and senior engineers planning infrastructure and client distribution.

---

## Short

Intensity runs on **two product platforms**: a **mobile client** (iOS and Android via Capacitor) and a **centralized server** (API + PostgreSQL). The **canonical product distribution** is store apps; there is no general-purpose web/PWA product. **Local** development pairs a localhost API with Vite dev server or emulator builds; **production** runs API and database in Docker on a VPS while store clients call the public HTTPS API. A separate **public demo** stack on the same VPS serves the same React UI in the browser against an isolated demo database (portfolio / recruiter preview — not a release staging environment).

---

## Medium

### Execution platforms

| Platform | Role | Instances |
|----------|------|-----------|
| **Mobile client** | Full product UI, flows, draw ritual, local session | One install per participant device |
| **Server** | REST API + co-located PostgreSQL | Production (+ optional public demo) on one VPS |

**Topology:** many mobile clients → one REST API → one database. No peer-to-peer sync, no CDN, no message broker. The public demo adds a second API+DB pair and a static SPA host behind the same reverse proxy — still not a second product topology.

### Device usage patterns

| Mode | Device pattern |
|------|----------------|
| **Experiences** | Each participant uses their own phone to register ideas |
| **Experience Box** | Group ritual (navigate boxes, invite, delete, draw, reveal) on **one shared phone**; contributions may come from separate devices |

Invite acceptance and individual contribution happen on personal devices; the draw ritual assumes co-presence on a shared screen. The public demo lets a single browser session approximate multi-account flows by switching credentials.

### Environments

| Environment | Client | API | Database |
|-------------|--------|-----|----------|
| **Local** | Vite dev server or Capacitor debug build | `localhost:8080` | PostgreSQL via Docker Compose |
| **Production** | Store builds (AAB/IPA) | HTTPS on VPS (`api.` / deep-link `app.`) | PostgreSQL container on same VPS |
| **Public demo** | Static Vite SPA (`demo-intensity.`) | HTTPS (`demo-intensity-api.`), Spring profile `demo` | Isolated Postgres + daily reset seed |

There is **no** dedicated staging / pre-production promotion environment. Demo is sample data for product preview only; it must never share production JWT secrets or database volumes. Ops: @ref:demo-plan — [`demo-plan.md`](../../../demo-plan.md); @ref:deploy-readme — [deploy/README.md](../../../deploy/README.md).

### Runtime requirements

- Mobile: iOS and Android current minus two major versions
- Server: Linux VPS, Docker 24+, Docker Compose v2
- Network required for all persisted operations (no offline baseline)

---

## Detailed

### Mobile platform

The client is a **hybrid app**: React UI in a Capacitor WebView shell with embedded static assets after build. Native capabilities used minimally: app lifecycle, status bar, splash screen, local preferences (language, onboarding flag).

**Product** distribution is exclusively through **Google Play** (AAB) and **Apple App Store** (IPA). Sideload and general PWA distribution are out of scope.

Deep links for **invite URLs** on production resolve into the installed app (Universal Links / App Links) or prompt install from store if missing. Demo invite links use the demo SPA host (`/join`) and must not be registered in native association files for the production app domain.

### Server platform

Single JVM process (Spring Boot) behind reverse proxy (Caddy or equivalent) terminating TLS. PostgreSQL 16 co-located in Compose stack on one VPS. Production and demo use separate Compose projects, volumes, and JWT secrets; demo reuses the same GHCR API image with `SPRING_PROFILES_ACTIVE=demo`.

Horizontal scaling is not baseline — architecture accepts single-instance API with future evolution path documented in architectural decisions.

### Local development topology

```
Developer machine
├── client/     npm run dev → browser :5173
├── api/        spring-boot:run → :8080
└── docker      postgres → :5432

Optional: Capacitor copy → Android emulator (10.0.2.2:8080) or device (LAN IP)
Optional: SPRING_PROFILES_ACTIVE=demo against intensity_demo DB
```

Environment variables:

| Variable | Role |
|----------|------|
| `VITE_API_URL` | API base URL baked at client build time |
| `VITE_INVITE_BASE_URL` | Invite link host (production or demo) |
| `VITE_API_PROXY_TARGET` | Optional Vite `/v1` proxy target in local dev |
| `VITE_DEMO` | When `true`, shows demo banner and auth shortcuts |

API JWT lifetimes (defaults in `application.yml`, overridable in production/demo):

| Session | Property | Default |
|---------|----------|---------|
| Experiences | `intensity.jwt.expiration-seconds` | 2_592_000 (30 days) |
| Experience Box | `intensity.jwt.experience-box-expiration-seconds` | 14_400 (4 hours) |

### Production topology

```
App stores → Mobile clients
                ↓ HTTPS REST
           VPS (Docker Compose)
             ├── reverse proxy :443
             ├── API container :8080
             └── PostgreSQL container
```

Deploy triggered by webhook after CI pushes image to registry.

### Public demo topology (same VPS)

```
Browser → demo-intensity.<domain> (nginx static SPA)
                ↓ HTTPS REST
         demo-intensity-api.<domain> → intensity-demo-api (profile demo)
                          → intensity-demo postgres (seed + daily reset)
```

Caddy (production compose) terminates TLS for demo hosts and reverse-proxies to demo containers on the shared Docker network `intensity`.

### What is explicitly absent

General web/PWA product distribution, BaaS, Kubernetes, staging VPS for release promotion, CDN, WebSockets, gRPC, GraphQL, real-time multi-device sync during draw.

## Decisions assumed in this rewrite

- Invite deep links for the **store product** are a **mobile platform concern** (App Links / Universal Links).
- Public demo web hosting is a **portfolio/preview** concern, not a second product channel.
- Box deletion and invite flows require network; no offline queue in baseline.
