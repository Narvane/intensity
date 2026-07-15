# Big Refactoring — Progress

Track execution against @ref:task-big-refactoring-exec. Mark items only when the corresponding safety gate is green (@ref:task-big-refactoring-safety).

## Phases

- [x] **Phase 0** — Whole-project diagnosis recorded in @ref:task-big-refactoring-diagnosis
- [x] **Phase 1** — Target trees written in @ref:task-big-refactoring-target-tree
- [x] **Phase 2** — Repository root & product knowledge surface
- [x] **Phase 3** — Documentation tree (`docs/` en + pt-br + it) + refs
- [x] **Phase 4** — Contracts, deploy, tooling, IA (`.github` only if needed)
- [x] **Phase 5** — API refactor + `./mvnw test` green
- [x] **Phase 6** — Client refactor + test/build green
- [x] **Phase 7** — Deep passes / leftover saturations
- [x] **Phase 8** — Trilingual docs + README + refs validation final

## Verification log

| Date | Phase / batch | Command / check | Result | Notes |
|------|---------------|-----------------|--------|-------|
| 2026-07-15 | Phase 0 baseline | refs validation (Node port of `scripts/validate-refs.py`; host has no Python) | RED (pre-existing) | orphan `@ref:backlog` in `ia/agents/code.md` — fix scheduled in Phase 2 |
| 2026-07-15 | Phase 0 | diagnosis covers root → docs, ia, api, client, deploy, .github | done | @ref:task-big-refactoring-diagnosis |
| 2026-07-15 | Phase 1 | target trees + per-batch risks + execution order | done | @ref:task-big-refactoring-target-tree |
| 2026-07-15 | Phase 2 | refs validation after `backlog.md` + refs entry + README layout rewrite | GREEN (66 refs, 15 files, 142 citations) | fixes pre-existing red; no folder moves at root (documented exception) |
| 2026-07-15 | Phase 3 | locale parity (file-by-file diff en/pt-br/it) + 25 relative doc links + refs | GREEN | docs tree already healthy — verification-only, no moves (per target tree) |
| 2026-07-15 | Phase 4 | docs-ci.yml trigger paths fixed (`agents/**`→ removed, `../../ia/**`→`ia/**`, +`backlog.md`); deploy/README folder map added; refs | GREEN | openapi/scripts/assets untouched; deploy: no moves (red zone, documented exception) |
| 2026-07-15 | Phase 5 baseline | `mvnw test` before moves | GREEN (66 tests) | |
| 2026-07-15 | Phase 5a | `mvnw test` after `config/common/demo` → `platform/{security,web,common,demo}` | GREEN (66 tests, BUILD SUCCESS) | mechanical package moves + imports only; REST/Flyway untouched |
| 2026-07-15 | Phase 5b | `mvnw test` after test classes regrouped by domain (`box/experience/group/invite/participant`) | GREEN (66 tests, BUILD SUCCESS) | `AbstractMockMvcIntegrationTest` made `public` (+`protected` lifecycle) for cross-package subclasses |
| 2026-07-15 | Phase 6 baseline | `npm test` + `npm run build` before moves | GREEN (67 tests, build OK) | |
| 2026-07-15 | Phase 6a | `npm test` + `npm run build` after `presentation/components` split into `brand/controls/feedback/chrome/experience/rating/collection` | GREEN (67 tests, build OK) | 33 flat files → 7 concept groups; imports rewritten mechanically |
| 2026-07-15 | Phase 6b | `npm test` + `npm run build` after presentation regrouped into `access/ collection/ moment/ invite/ components/ hooks/ styles/`; `box-home` → `moment/experience-box` | GREEN (67 tests, build OK; **identical bundle hash** to baseline) | `quick-guide` placed under `access/` (used by auth+onboarding), diverging from target-tree draft — recorded; no route URL changes; Capacitor untouched |
| 2026-07-15 | Phase 7 | repo-wide sweep for stale paths (docs, scripts, workflows, STORE_RELEASE, Dockerfile) | GREEN | accepted leftovers recorded in diagnosis (domain 10 folders, suggestion-pack twins, BoxHomePage file names, root peers, deploy interleaving) |
| 2026-07-15 | Phase 8 | trilingual doc updates (artifacts, technical-decisions, tools × en/pt-br/it); final gates: refs (66/15/142), 25 doc links, `mvnw test` 66 GREEN, `npm test` 67 + build GREEN | ALL GREEN | README layout already aligned in Phase 2 |

## Blockers

| Blocker | Phase | Decision / next step |
|---------|-------|----------------------|
| | | |
