# Big Refactoring — Diagnosis

Living notes for Phase 0 of @ref:task-big-refactoring-exec.  
Fill this **before** moving files. Guided by @ref:agent-refactor and @ref:skill-architectural-philosophy.

## Snapshot (start)

Date: 2026-07-15 (branch `refactor`, clean tree at `07a065c`)

Root listing (as observed):

```text
Intensity/
├── README.md
├── api/          Spring Boot REST backend
├── assets/       brand images (consumed by root README AND client Vite build)
├── client/       React + Capacitor app
├── deploy/       VPS prod + demo stack (Compose, Caddy, webhook, built demo SPA)
├── docs/         refs.yaml + en / pt-br / it spec (4 layers each)
├── ia/           agents / skills / tasks
├── openapi/      openapi.yaml (single file)
├── scripts/      validate-refs.py (single file)
└── .github/      workflows: api-ci.yml, docs-ci.yml
```

Note: `backlog.md` is listed in the exec-plan scope but **does not exist** in the repository.

First-impression narrative (what a newcomer thinks this repo is after 10 seconds):

> "A conventional engineering monorepo: an API, a client, deploy stuff, docs." The root already names the two apps and the knowledge folders, which is decent — but nothing at first glance says *what Intensity is* except the README prose. The tree is tech-first (`api`, `client`, `deploy`, `scripts`), not product-first. Still, at 9 visible peers it is close to readable; the bigger problems live one or two levels down.

## Baseline verification state

| Check | Result | Notes |
|-------|--------|-------|
| refs validation | **RED (pre-existing)** | orphan `@ref:backlog` in `ia/agents/code.md` — `backlog.md` doesn't exist and has no refs entry |
| Host tooling | Python not installed on this Windows host | gate run via a Node port of `scripts/validate-refs.py` (same checks); CI still runs the Python original |
| `api` tests / `client` build | not yet run | will be run as phase gates before touching those trees |

## Saturation findings

| Location | Peer count (approx.) | Main concepts | Problem | Severity (H/M/L) |
|----------|----------------------|---------------|---------|------------------|
| repo root | 9 (+.github) | 2 apps, contract, docs, deploy, brand, ia, scripts | Above ~5, but each peer is meaningful; grouping (e.g. `tooling/`) would cost CI/docs churn for little cognitive gain | L |
| docs/{locale} | 4 layers × 3–4 docs | conception → specification → architecture → engineering | Healthy; layers tell the product story | — |
| ia/ | 3 (agents, skills, tasks) | how the project is evolved | Healthy | — |
| api/.../com.intensity | 9 packages | box, experience, group, invite, participant + common, config, demo | Domain story (5 packages) diluted by 3–4 infra packages at the same level | M |
| api/.../com.intensity.config | 8 files | JWT/security (4), CORS (2), OpenAPI, error handling | "config" is a drawer hiding 4 distinct concepts; JWT auth is a domain-relevant concept buried as config | M |
| api test root (com.intensity) | 16 flat test classes | auth, box, experience, group, invite, health… | Flat list; domain grouping exists in main but not mirrored in tests | M |
| client/src | 7 + main.tsx | adapters, app, assets, content, domain, i18n, presentation | Slightly above 5; names are layer-ish but each earns its place via Vite aliases | L |
| client/src/domain | 10 folders | auth, bootstrap, box, draw, experience, invite, navigation, preferences, session | Above heuristic; 3 of them (navigation, preferences) are single-port folders — ports mixed with domain concepts | M |
| client/src/presentation | 16 folders | pages by feature + `components` + `hooks` + `styles` | Saturated; feature folders (good) sit next to generic drawers (bad) | **H** |
| client/src/presentation/components | 30 components (60 files) | brand, buttons, experience widgets, session chrome, demo, dialogs… | Classic saturated flat drawer; concepts invisible | **H** |
| deploy/ | 13 files + 3 dirs | production stack AND demo stack interleaved | Two products of knowledge (prod vs demo) shuffled in one list | M |

## Architecture-as-language findings

| Location | What it currently communicates | What it should communicate | Notes |
|----------|--------------------------------|----------------------------|-------|
| repo root | generic engineering monorepo | Intensity the product project | README prose carries all the product meaning; tree carries almost none. Cheapest wins: fix stale README layout section (`agents/` ghost, missing `ia/`), not necessarily move folders |
| docs layers | product zoom: conception → spec → architecture → ops | same | Already good; keep |
| client/src top | Clean-Architecture-ish layers | the Intensity experience | Acceptable: layers are few and aliased; the *feature* language lives one level down and mostly works |
| client/presentation | mixed: features (boxes, groups, invite…) vs drawers (components, hooks, styles) | product surfaces, refined | `box-home` vs `boxes` is the worst naming pair — same noun, unclear split (home dashboard vs draw-session flow) |
| api domain folders | box, experience, group, invite, participant — good domain story | persisted domain boundaries | The 5 domain packages already read like the product; infra packages blur the first glance |
| deploy/ | one bucket of ops files | "how prod runs" vs "how the public demo runs" | prod/demo split would clarify, but this is a red zone (webhook + VPS paths) |

## Progressive specificity / position issues

| Path | Issue | Suggested direction |
|------|-------|---------------------|
| `client/src/presentation/box-home` vs `boxes` | Same concept name at same level with hidden distinction (dashboard vs selection/draw-session flow) | Rename to communicate the actual split (e.g. home vs draw-session), or merge under one `box` context with two children |
| `client/src/presentation/components` | 30 peers; name says nothing | Split by concept: brand/, session-mode/, experience widgets/, form controls/, feedback (banners/dialogs)/ |
| `client/src/domain/navigation`, `preferences` | One-port folders sitting as peers of rich domain concepts | Consider a `ports/`-style grouping or fold into their consumer concept — decide in Phase 1 |
| `api/.../config` | Security/JWT concept hidden under generic "config" | Extract `security` (or `auth`) package; keep true wiring config small |
| `api` tests flat root | Position doesn't mirror the domain story of main | Group test classes by domain package |
| README layout section | Mentions `agents/` (gone), omits `ia/` | Rewrite in Phase 2 to match the real tree |
| `.github/workflows/docs-ci.yml` | Trigger paths list `agents/**` (stale) and `../../ia/**` (invalid pattern — never matches; ia changes don't trigger docs CI) | Fix to `ia/**` in Phase 4 (.github batch) |

## Useless or costly abstractions

| Path | Why it hurts cognition | Keep / reshape / remove (structurally) |
|------|------------------------|----------------------------------------|
| `api` per-domain `controller/dto/entity/repository/service` | Ceremony, but consistent and small (≤5 files each); removing would churn every import for no gain | Keep |
| `client/src/adapters` split by 6 tiny folders | Borderline: 6 folders for 6 files; but names map 1:1 to ports | Keep (low cost) |
| `deploy/demo-web` committed build artifacts (hashed js/css) | Generated output in source tree; confuses "source of truth" | Note only — publishing flow depends on it; do not remove in this program without owner decision |
| `client/src/content/suggestion-packs/by-type` + `text` twin trees | Two parallel 11-file lists keyed by the same pack names | Reassess in Phase 6; may be intentional data/text split |

## Non-code knowledge gaps

| Area | Gap | Impact on onboarding |
|------|-----|----------------------|
| README ↔ tree | Layout block stale (`agents/`, no `ia/`, no `backlog.md` which is fine since it doesn't exist) | Newcomer trusts README and looks for folders that don't exist |
| refs map ↔ reality | `@ref:backlog` cited but unregistered/nonexistent → baseline validation red | CI docs gate presumably red already; must fix early (Phase 2) |
| docs ↔ code layout | Docs describe architecture; will need updates after Phases 5–6 moves | Deferred to Phase 8 (documenter) |
| ia discoverability | Good: agents/skills/tasks with refs entries | — |
| deploy/openapi placement | Both fine at root; deploy internal mix is the issue | — |

## Hard constraints (do not casually move)

| Path / system | Constraint |
|---------------|------------|
| Flyway migrations (`api/src/main/resources/db/migration`) | history immutable for applied versions V1–V9 |
| Capacitor native projects (`client/android`, `client/ios`) | generated shell sensitivity; `capacitor.config.ts` webDir=`dist` |
| GHCR / deploy webhook | `api-ci.yml` builds with context `.` + `api/Dockerfile`, posts to VPS webhook; `deploy/webhook/receive.sh` and VPS paths are production-coupled |
| OpenAPI `/v1` | public contract stability |
| Root `assets/` | consumed by client at build time via `import.meta.glob('../../../assets/…')` in `client/src/content/brandAssets.ts` (plus Vite `server.fs.allow` on repo root) — moving/renaming root assets breaks the client build silently (globs return empty) |
| CI trigger paths | `api-ci.yml` keyed on `api/**`; `docs-ci.yml` keyed on `docs/**`, `scripts/validate-refs.py`, `README.md`, `openapi/openapi.yaml` — any rename of these roots must update workflows in the same batch |
| Vite aliases | `@app @domain @adapters @presentation @i18n @content` → renaming `client/src` top folders requires `vite.config.ts` + `tsconfig` sync |
| `deploy/publish-demo-client.sh`, `cron-reset-demo.sh` | reference in-repo paths and VPS paths; verify with dry-read before/after any deploy/ move |

## Phase 7 sweep — accepted leftovers (with rationale)

| Path | Leftover | Rationale |
|------|----------|-----------|
| `client/src/domain` (10 folders) | Above ~5 heuristic | Each folder is a real product concept; ports consistently live beside their concept. Documented exception |
| `client/src/content/suggestion-packs/{by-type,text}` | Twin 11-file trees | Intentional data/text separation keyed by pack name; merging would couple content authoring to pack logic |
| `moment/experience-box/BoxHomePage.tsx` (+CreateBoxPage) | File names keep old "box-home" vocabulary | Route URLs (`/box-home/...`) are public product paths and stay; position (`moment/experience-box/`) now carries the meaning; renaming components adds churn without route alignment |
| repo root (9–10 peers) | Above ~5 heuristic | All peers pinned by build/CI/production paths; README narrative carries the story. Documented exception |
| `deploy/` prod/demo interleaving | Flat mixed list | Production red zone; mitigated with a folder-map section in `deploy/README.md` |
| `api/target/**` stale package names | Old `com.intensity.config` in surefire reports | Generated build output; git-ignored |

## Open questions for the human

1. `backlog.md` is referenced (exec scope, `@ref:backlog` in agent-code) but absent. Create a stub + refs entry, or remove the citation? **Working assumption: create a minimal `backlog.md` + refs entry (smaller, restores green, matches exec scope description).**
2. `deploy/demo-web` holds committed build artifacts. Out of scope for removal; OK to leave untouched? **Assumed yes.**
3. Root regrouping (e.g. nesting `scripts`/`assets` under a broader concept) costs CI/README/refs churn for modest gain at 9 peers. **Working assumption: keep root peers in place; invest in README narrative + deeper trees instead.**
