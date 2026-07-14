# Big Refactoring — Safety Gates

Use this checklist before and after every structural batch. Prefer stopping over shipping a broken tree.

## Absolute prohibitions

* Do not change REST paths, request/response shapes, or status semantics of @ref:openapi `/v1`.
* Do not rewrite or squash already-applied Flyway migrations.
* Do not change draw/reveal product behavior, auth semantics, invite rules, or cascade delete behavior “while you’re here.”
* Do not mix feature delivery into this refactor (use @ref:agent-code for features).
* Do not rearrange Capacitor `android/` / `ios/` trees casually; treat them as generated/native shells.
* Do not force-push, rewrite shared history, or delete production deploy secrets/config.

## Before each batch

* [ ] Batch scope written (paths included / excluded).
* [ ] Target shape for this batch documented in @ref:task-big-refactoring-target-tree (or diagnosis notes).
* [ ] Known risky dependents listed (CI, Docker, Vite aliases, Maven packages, deep links, imports).
* [ ] Rollback plan known (`git checkout -- path` / revert commit / restore imports).

## After each batch

* [ ] Project still opens / indexes without obvious missing entrypoints.
* [ ] Imports and path aliases updated.
* [ ] Registered artifact `path`s in @ref:refs updated if those files moved.
* [ ] No intentional behavior delta in the diff (structure-only review).

## Gate matrix

| Area touched | Minimum verification |
|--------------|----------------------|
| `docs/` paths / locales | Locale parity; links; `python3 scripts/validate-refs.py` |
| `ia/` agents/skills/tasks | Refs entries; citations still resolve |
| Root README / entry files | Links open; layout section matches tree |
| `openapi/` | Contract unchanged unless explicitly postponed; consumers still point correctly |
| `deploy/` | Compose/Caddy/webhook paths still coherent; README paths fixed |
| `.github/` workflows | Workflow file paths match repo; no broken `working-directory` |
| `api/` Java packages | `cd api && ./mvnw test` |
| `client/src` structure | `cd client && npm test` (or vitest script) && `npm run build` |
| Capacitor config / web dir | `npx cap sync` after build if paths changed |
| `scripts/` | Script still runnable from documented command |

## High-caution zones

Treat these as red zones — smaller batches, more verification:

1. `api/src/main/resources/db/migration/`
2. `client/android/`, `client/ios/`, `client/capacitor.config.ts`
3. `.github/workflows/`
4. `deploy/` production compose and webhook
5. Public package exports and Vite path aliases consumed across many files
6. Deep-link / invite URL construction

## Failure protocol

If a gate fails:

1. Stop the program of work.
2. Restore green by fix or revert.
3. Note cause in @ref:task-big-refactoring-diagnosis.
4. Only then resume.

Never start the next phase while the previous gate is red.
