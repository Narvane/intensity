# Big Refactoring — Execution Plan

This file is the **work order** for a whole-repository cognitive refactor of Intensity.

## How to run

1. Read and follow @ref:agent-refactor end to end.
2. Apply @ref:skill-architectural-philosophy as the specification of the refactor — not only inside `api/` and `client/`, but across the **entire product project**.
3. Use @ref:skill-referencer for every move that touches registered paths.
4. Close each phase (and the program as a whole) with @ref:skill-documenter when structure or architecture narrative changed.

Supporting files in this folder:

| File | Role |
|------|------|
| @ref:task-big-refactoring-safety | Non-negotiable safety gates — read before every move |
| @ref:task-big-refactoring-progress | Phase checklist — update as work proceeds |
| @ref:task-big-refactoring-diagnosis | Living diagnosis notes (fill during Phase 0) |
| @ref:task-big-refactoring-target-tree | Proposed target trees (fill before moving) |

Do not start moving files until Phase 0 diagnosis and Phase 1 target trees are written and coherent.

---

## Scope (entire repository)

**In scope:** every first-class folder and meaningful file at the repository root, then recursively every subtree until leaf modules feel cognitively healthy.

Current root concepts to treat as one architectural language (not “code vs non-code”):

```text
Intensity (product project)
├── README.md          — product entry
├── backlog.md         — what to build next
├── docs/              — product & engineering knowledge
├── openapi/           — public contract
├── api/               — persistence & REST backend
├── client/            — product experience (mobile/web)
├── deploy/            — how it runs in production
├── assets/            — brand
├── ia/                — how humans+AI evolve the project
├── scripts/           — repo tooling
└── .github/           — automation (treat carefully)
```

The philosophy must be felt **from the first directory listing**. Opening this repository should feel like opening **Intensity the product**, not a dump of engineering folders. Documentation, contracts, agents, deploy, and code are all knowledge fragments of the same product.

**Out of scope for rewrite:** product behavior, API contracts (`/v1`), database semantics, UX flows, copy/i18n meaning. Structural moves only, unless a rename is required for progressive specificity and has a safe mechanical update path.

---

## North-star questions

At every level, keep asking:

1. Does this organization make Intensity easier to understand?
2. Could a newcomer infer **what this product is** before reading implementation?
3. Does descending the tree **refine** context instead of widening it?
4. Are there ~5-or-fewer main concepts at this level, or is it a growing list?
5. Is position part of meaning (same name under a different parent ≠ same idea)?

Apply that from repository root → packages → modules → files → and (where cognitively useful) classes/components.

---

## Safety doctrine (non-negotiable)

Full checklist: @ref:task-big-refactoring-safety.

Absolute rules:

* **Do not break the product.** Prefer no move over a risky move.
* One phase (or one coherent subtree) at a time. No “big bang” rename of the monorepo in a single commit.
* Preserve observable behavior and public contracts (@ref:openapi).
* Prefer mechanical moves + import/path updates over logic rewrites.
* After every structural batch: run the relevant verification gate before continuing.
* If uncertain about impact (native shells, CI paths, Docker, Flyway, Capacitor), stop and record the risk in @ref:task-big-refactoring-diagnosis instead of guessing.
* Do not expand scope mid-flight into features. Feature work belongs to @ref:agent-code.
* Update @ref:refs `path` fields when registered artifacts move; keep `id`s stable.

---

## Program of work

### Phase 0 — Whole-project diagnosis (no moves)

Operate as @ref:agent-refactor diagnosis section.

1. Inventory the repository root as a cognitive context.
2. Walk depth-first: for each directory level, list main concepts and mark:
   * saturation (> ~5 main peers),
   * progressive-specificity violations,
   * wrong position / misleading neighbors,
   * abstractions that increase cognitive load,
   * tech-stack noise that hides product meaning.
3. Explicitly diagnose non-code knowledge:
   * `docs/` layers vs how a product story should unfold,
   * `ia/` discoverability (agents/skills/tasks),
   * root README vs tree language,
   * `openapi/`, `deploy/`, `scripts/`, `assets/` as first-class product concepts.
4. Explicitly diagnose code:
   * `api/` domain modules and packaging,
   * `client/src` layers (`domain`, `presentation`, `adapters`, `app`, `content`, …),
   * saturated buckets (e.g. large unsorted `presentation/components`),
   * native projects (`android/`, `ios/`) — note constraints; do not casually rearrange generated shells.
5. Write findings into @ref:task-big-refactoring-diagnosis.
6. Do **not** move anything in this phase.

Gate: diagnosis document exists and covers root → major subtrees.

---

### Phase 1 — Target trees (design only)

Propose the target shape **before** executing moves. Write it in @ref:task-big-refactoring-target-tree.

Requirements for the proposal:

* Root tells a product story (Intensity as a product project).
* Each level stays near the saturation heuristic.
* Names follow progressive specificity.
* Code, docs, contract, deploy, and IA remain clearly related parts of one product.
* Migration notes for high-risk areas (CI paths, Docker context, Capacitor, Maven layout, Vite aliases).

Do not invent hierarchy for its own sake. Only create levels that reduce simultaneous concepts or clarify meaning.

Gate: target trees reviewed against the philosophy; safety risks noted per risky move.

---

### Phase 2 — Repository root & product knowledge surface

Goal: the first listing of the repo already communicates Intensity as a product.

Typical work (driven by diagnosis, not by this list blindly):

* Reorganize or regroup root concepts so neighbors make sense.
* Align `README.md` layout section with the new tree language.
* Ensure `docs/`, `openapi/`, `ia/`, `deploy/`, product apps, and brand sit in a story that zooms from product → delivery → implementation.
* Keep tool-only concerns from dominating the first glance when possible (or nest them under a clearly named higher concept if diagnosis warrants it).

Verification:

* Root listing is narratively coherent.
* Links in README and entry docs still work.
* @ref:refs paths updated if anything registered moved.
* `python3 scripts/validate-refs.py` (or equivalent) passes.

Update @ref:task-big-refactoring-progress.

---

### Phase 3 — Documentation tree as product language

Goal: navigating @ref:docs-en (and parity locales) feels like zooming into the product, not into a doc CMS.

* Reassess `docs/{en,pt-br,it}/` layers for saturation and specificity.
* Ensure layer names and document grouping tell a story from conception → specification → architecture → engineering.
* Preserve **structural parity** across the three locales in the same task when paths change.
* Update every registered `path` in @ref:refs; never change published `id`s casually.

Verification:

* Locale parity intact.
* Refs validation passes.
* Cross-links and `@ref:` citations still resolve.

Then apply @ref:skill-documenter for narrative updates in all three languages.

---

### Phase 4 — Contracts, deploy, tooling, IA

Goal: non-app knowledge also reads as part of Intensity.

Sub-areas (run as separate batches if risk differs):

1. `openapi/` — keep contract truth stable; reorganize only surrounding packaging if needed.
2. `deploy/` — preserve production scripts/paths; rename only with gate checks.
3. `scripts/` — tooling remains discoverable without polluting product root meaning.
4. `ia/` — agents, skills, and tasks should form a clear “how this product is evolved” language.
5. `.github/` — extreme caution; CI path breakage is a release risk.

Verification per batch: whatever that area owns still runs (deploy dry-read, workflow path sanity, script execution).

---

### Phase 5 — API (`api/`)

Goal: backend tree explains persistence/domain boundaries before file contents.

Follow @ref:agent-refactor inside `api/`:

* Diagnose package saturation under `com.intensity.*`.
* Prefer domain story over framework ceremony when they conflict cognitively.
* Mechanical package moves only; keep REST routes and JSON shapes unchanged.
* Respect Flyway/history — **do not rewrite applied migrations**; structural code moves must not imply schema rewrites.

Verification gate (mandatory):

```text
cd api
./mvnw test
```

(adjust wrapper command for the host OS)

Also smoke health/docs endpoints if local stack is available (@ref:en-development-process).

---

### Phase 6 — Client product app (`client/`)

Goal: opening `client/` and especially `client/src` feels like opening the Intensity experience, refined step by step.

Follow @ref:agent-refactor inside `client/`:

* Treat `domain/`, `presentation/`, `adapters/`, `app/`, `content/`, `i18n/` as cognitive contexts subject to saturation.
* Pay special attention to saturated “drawers” (e.g. large flat `presentation/components`).
* Align naming/position with domain language (group, box, experience, draw, invite, …) where that improves understanding.
* Prefer Clean Architecture / layer names **only when they reduce cognitive load**; they are tools, not goals (@ref:skill-architectural-philosophy).
* Native shells (`android/`, `ios/`): do not gratuitously rearrange Cap-generated trees. If Capacitor config paths move, update configs and run sync carefully.

Verification gate (mandatory):

```text
cd client
npm test   # or project’s vitest script if aliased
npm run build
```

If native paths/config changed:

```text
npx cap sync
```

---

### Phase 7 — Deep passes & leftover saturations

After coarse trees are healthy, re-walk the repo:

* Any level still > ~5 main peers without meaning?
* Any name that is generic too deep, or specific too high?
* Any “misc”, “common”, “utils”, “components” buckets that hide concepts?
* Any docs still describing the old tree?

Fix leftovers in small batches with the same safety gates.

---

### Phase 8 — Close with documentation & map

Apply @ref:skill-documenter across impacted docs (at minimum: @ref:en-artifacts, @ref:en-technical-decisions, @ref:en-development-process, @ref:en-tools, and any others that mention structure). Propagate `en` → `pt-br` → `it`.

Update README repository layout to match reality.

Finalize:

* @ref:task-big-refactoring-progress all phases checked
* @ref:refs paths accurate
* refs validation green
* API tests green
* client build (and relevant tests) green

---

## Execution rhythm (per batch)

```text
1. Read philosophy + this phase goal
2. Touch only the agreed subtree
3. Move/rename mechanically
4. Fix imports, aliases, CI/docs paths
5. Run the phase safety gate
6. Update diagnosis/progress notes
7. Update refs if registered paths changed
8. Only then start the next batch
```

If a gate fails: revert or fix before continuing. Never accumulate broken states across phases.

---

## Definition of done

The program is done when:

1. A newcomer can navigate from repo root toward a concrete module and feel continuous zoom into Intensity.
2. Root and major subtrees communicate product meaning before implementation detail.
3. Contexts are generally within cognitive saturation heuristics (or documented exceptions with rationale).
4. Observable product behavior and `/v1` contracts are unchanged.
5. Trilingual docs + refs map describe the new structure.
6. Safety gates for API and client are green.

---

## Agent reminder

You are not “cleaning folders.” You are reorganizing the **knowledge of a product** — code, documentation, contracts, operations, and evolution practices — so human minds can understand Intensity with less simultaneous load.

When in doubt: **do not break anything**; record the dilemma; prefer the smaller move that preserves meaning.
