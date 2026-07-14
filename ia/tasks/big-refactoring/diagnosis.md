# Big Refactoring — Diagnosis

Living notes for Phase 0 of @ref:task-big-refactoring-exec.  
Fill this **before** moving files. Guided by @ref:agent-refactor and @ref:skill-architectural-philosophy.

## Snapshot (start)

Date:

Root listing (as observed):

```text
(paste tree)
```

First-impression narrative (what a newcomer thinks this repo is after 10 seconds):

## Saturation findings

| Location | Peer count (approx.) | Main concepts | Problem | Severity (H/M/L) |
|----------|----------------------|---------------|---------|------------------|
| repo root | | | | |
| docs/ | | | | |
| ia/ | | | | |
| api/.../com.intensity | | | | |
| client/src | | | | |
| client/src/domain | | | | |
| client/src/presentation | | | | |
| client/src/presentation/components | | | | |
| … | | | | |

## Architecture-as-language findings

| Location | What it currently communicates | What it should communicate | Notes |
|----------|--------------------------------|----------------------------|-------|
| repo root | | Intensity the product project | |
| docs layers | | product zoom: conception → ops | |
| client/src top | | experience architecture | |
| api domain folders | | persisted domain boundaries | |

## Progressive specificity / position issues

| Path | Issue | Suggested direction |
|------|-------|---------------------|
| | | |

## Useless or costly abstractions

| Path | Why it hurts cognition | Keep / reshape / remove (structurally) |
|------|------------------------|----------------------------------------|
| | | |

## Non-code knowledge gaps

| Area | Gap | Impact on onboarding |
|------|-----|----------------------|
| README ↔ tree | | |
| docs ↔ code layout | | |
| ia discoverability | | |
| deploy/openapi placement | | |

## Hard constraints (do not casually move)

| Path / system | Constraint |
|---------------|------------|
| Flyway migrations | history immutable for applied versions |
| Capacitor native projects | generated shell sensitivity |
| GHCR / deploy webhook | production path coupling |
| OpenAPI `/v1` | public contract stability |

## Open questions for the human

1.
2.
3.
