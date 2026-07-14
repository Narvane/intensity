# Agent: Code

Intensity development agent. Use when implementing features, fixing bugs, or evolving the system from a work prompt.

Before executing the task, read this agent fully and apply the skills referenced below.

## Mission

Implement the requested change so that the code remains comprehensible, navigable, and aligned with the domain — not merely “working.”

## Required skills

| Order | Skill | Use |
|-------|--------|-----|
| 1 | @ref:skill-architectural-philosophy | Criterion for every structural decision |
| 2 | @ref:skill-referencer | Cite and resolve artifacts via `docs/refs.yaml` |
| 3 | @ref:skill-documenter | At the end, create/update docs in all 3 languages |

Read the philosophy **before** writing code. Use the referencer whenever you cite docs, contracts, or folders. Run the documenter **after** the implementation is stable.

## Context to consult

Resolve through map ids (@ref:refs):

* Product and intent: @ref:en-overview-what-is-it, @ref:en-how-it-works, @ref:en-principles-why-it-works-this-way
* Behavior and data: @ref:en-functional-components, @ref:en-data-model, @ref:en-experience-and-identity
* Shape of the system: @ref:en-architectural-decisions, @ref:en-artifacts, @ref:en-integrations-and-communication
* How to build: @ref:en-technical-decisions, @ref:en-development-process, @ref:en-tools
* HTTP contract: @ref:openapi
* Pending work (if relevant): @ref:backlog

Do not load everything at once. Load the minimum the task requires, starting with the doc closest to the delta.

## Workflow

```text
1. Clarify the request and expected delta.
2. Read the architectural philosophy.
3. Read relevant docs/contracts via @ref.
4. Inspect the code and tree around the change.
5. Plan the insertion with context saturation in mind.
6. Implement (reorganize before saturating).
7. Verify behavior (relevant tests/builds).
8. Document with the documenter skill (en + pt-br + it).
9. Validate refs if the map or citations changed.
```

## How to decide structure (derived from the philosophy)

Every code organization answers first:

> Does this organization make the system easier to understand?

Then apply in practice:

### Before adding

1. Does the element belong to an existing concept? → incorporate it there.
2. Is it a new concept? → create a new context.
3. Does the current level exceed ~5 main elements? → reorganize and raise abstraction **before** growing the list.

> Before you add, reorganize.

### While naming and positioning

* The tree should explain **what** the system is; the code explains **how**.
* Names high in the hierarchy are broader; names deep in the tree are more specific.
* Descending the tree must refine context, never widen it.
* Position is meaning: the same term under different parents communicates different things.
* Neighbors and contrasts among concepts also communicate the domain.

### Known patterns

Clean Architecture, DDD, microservices, and similar approaches are tools, not goals. Use them only when they reduce cognitive load in the current context. Ignore ritualistic fidelity to a pattern if it confuses reading.

## Implementation criteria

* Change only what the request needs; avoid opportunistic refactors outside scope (those belong to @ref:agent-refactor).
* If the feature truly saturates a context you are touching, reorganize that context — that is part of development, not “extra scope.”
* Preserve public contracts or version them compatibly (@ref:en-technical-decisions, @ref:openapi).
* Follow the local and testing process described in @ref:en-development-process.
* Cite artifacts with `@ref:<id>` in any text you write (docs, backlog, notes).

## Closing with documentation

When implementation is done, apply @ref:skill-documenter:

1. Identify which docs across the four layers became stale.
2. Update or create in `en`, then `pt-br` and `it`.
3. Register new artifacts in @ref:refs.
4. Run `python3 scripts/validate-refs.py` if refs or citations changed.

If the change is internal with no material documentation impact, say so explicitly and do not force cosmetic docs.

## Exit checklist

* [ ] Philosophy applied (saturation + structure as language).
* [ ] Code in the semantically correct place in the tree.
* [ ] Behavior verified where relevant.
* [ ] Trilingual docs updated **or** justified “no documentation delta.”
* [ ] Refs valid when the map was touched.
