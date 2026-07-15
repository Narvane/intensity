# Big Refactoring — Target Trees

Design-only workspace for Phase 1 of @ref:task-big-refactoring-exec.  
Do not execute moves until this document is consistent with @ref:task-big-refactoring-diagnosis and @ref:task-big-refactoring-safety.

Propose trees that a newcomer can read as a zoom into Intensity.

## Principles for proposals

* ~5 main peers per level when possible (heuristic, not dogma).
* Higher names broader; deeper names more specific.
* Descend = refine.
* Product knowledge (docs, contract, IA, deploy) is part of the same story as code.
* Every proposed move needs a migration note if CI, Docker, Vite, Maven, Capacitor, or refs depend on the path.

---

## 1. Repository root (product project)

### Current

```text
README.md  api/  assets/  client/  deploy/  docs/  ia/  openapi/  scripts/  .github/
```

### Proposed

```text
README.md      product entry (layout section rewritten to match reality)
backlog.md     what to build next (NEW stub — restores @ref:backlog)
docs/          product & engineering knowledge        (unchanged)
openapi/       public contract                        (unchanged)
api/           persistence & REST backend             (internal refactor, Phase 5)
client/        product experience                     (internal refactor, Phase 6)
deploy/        how it runs in production              (no moves — red zone)
assets/        brand                                  (unchanged — client build depends on path)
ia/            how humans+AI evolve the project       (unchanged)
scripts/       repo tooling                           (unchanged)
.github/       automation                             (path fixes only)
```

### Why this communicates Intensity better

At 9–10 peers the root is over the heuristic, but every peer is a real, distinct product concept and three of them (`assets`, `deploy`, `.github`, `scripts`) are pinned by build/CI/production paths. Nesting them under an artificial parent (e.g. `tooling/`) would trade real breakage risk for one line less of listing. **Decision: documented exception.** The product story is carried by (a) README narrative aligned with the tree, (b) `backlog.md` restoring the "what to build next" concept the exec plan already treats as part of the root language.

### Migration notes / risks

* `backlog.md`: new file + refs entry `backlog` → fixes the pre-existing red validation (orphan `@ref:backlog` in `ia/agents/code.md`).
* README layout block: remove ghost `agents/`, add `ia/`, `backlog.md`.
* No folder moves at root ⇒ no CI/Docker/Capacitor impact.

---

## 2. docs/

### Proposed (en shown; pt-br/it must stay in parity)

```text
docs/
├── refs.yaml
└── {en, pt-br, it}/
    ├── product-conception/          (overview, how-it-works, principles)
    ├── solution-specification/      (data-model, design-system, experience-and-identity, functional-components)
    ├── solution-architecture/       (architectural-decisions, artifacts, integrations, platforms)
    └── engineering-and-operations/  (development-process, team, technical-decisions, tools)
```

**No structural change.** The four layers already read conception → specification → architecture → operations, 3–4 docs per layer, full trilingual parity. This is the healthiest tree in the repo; Phase 3 is verification-only (parity + links + refs), narrative updates deferred to Phase 8.

### Refs impact

* ids stable; **no `path` updates required** (no moves).

---

## 3. ia/

### Proposed

```text
ia/
├── agents/   code.md, refactor.md
├── skills/   architectural-philosophy.md, documenter.md, referencer.md
└── tasks/    big-refactoring/ (exec, safety, progress, diagnosis, target-tree)
```

**No change.** 3 peers, names already tell "how this product is evolved". Only fix: `.github/workflows/docs-ci.yml` must actually trigger on `ia/**` (currently the invalid pattern `../../ia/**` — see §6).

---

## 4. api/

### Proposed package / folder story

```text
com.intensity
├── IntensityApiApplication.java
├── box/  experience/  group/  invite/  participant/     ← domain story, unchanged inside
└── platform/                                            ← everything that serves the domain
    ├── security/   JwtAuthenticationFilter, JwtProperties, JwtService, SecurityConfig
    ├── web/        CorsConfig, CorsProperties, OpenApiConfig, RestExceptionHandler
    ├── common/     AccessMode, AuthPrincipal, dto/ErrorResponse, exception/ApiException
    └── demo/       DemoProperties, DemoSeedRunner, DemoSeedService
```

First glance becomes: **five domain concepts + one platform** (6 peers instead of 9). `config` — a drawer hiding four concepts — dissolves into `security` and `web`, both progressively specific under `platform`.

Test tree mirrors main:

```text
com.intensity (test)
├── AbstractMockMvcIntegrationTest.java   (shared base, stays at root)
├── IntensityApiApplicationTests.java, HealthIntegrationTest.java, CorsIntegrationTest.java (root/platform-level)
├── box/          BoxIntegrationTest
├── experience/   ExperienceIntegrationTest, ExperienceVisibilityPolicyTest, SealServiceTest
├── group/        GroupIntegrationTest, CreateGroupIntegrationTest
├── invite/       InviteIntegrationTest, InvitePersistenceIntegrationTest, InviteTest,
│                 InviteCodeGeneratorTest, InviteExpirationPolicyTest
├── participant/  AuthIntegrationTest
└── platform/
    ├── security/ JwtServiceTest
    └── demo/     DemoSeedIntegrationTest
```

### Behavior preservation notes

* REST mappings unchanged: only package declarations + imports move; no class renames, no route/DTO changes.
* Flyway untouched; `application*.yml` untouched (no package-qualified config values present — verify `@ConfigurationProperties` prefixes remain `intensity.*`/`demo.*` before moving).
* Component scan safe: everything stays under `com.intensity`.
* Tests to run: `./mvnw test` (from `api/`), after each batch (batch 1: platform extraction; batch 2: test regrouping).

---

## 5. client/

### Proposed `client/src` story

Top level: **keep** `adapters / app / assets / content / domain / i18n / presentation` (documented exception at 7 peers — each is a load-bearing Vite alias; renaming buys little and costs alias+tsconfig churn across every import).

`domain/`: **keep** the ten concept folders (auth, bootstrap, box, draw, experience, invite, navigation, preferences, session). Ports consistently live beside their concept; `navigation`/`preferences` are single-port folders but coherent. Documented exception.

`presentation/` — the real work. Current: 16 peers, including a 30-component flat `components/` drawer and the misleading `box-home` vs `boxes` pair.

**Batch A (low risk, high gain) — split the `components/` drawer by concept:**

```text
presentation/components/
├── brand/        BrandMark, ToolbarBrand, IntensityHero, OnboardingIllustration
├── controls/     Button, Checkbox, NavButton, RatingScale, StarRating, JointLoginSecretInput
├── feedback/     DemoBanner, OfflineBanner, DestructiveConfirmDialog, AppLoader
├── experience/   ExperienceContentBlock, ExperienceSummaryMeta, ExperienceTypePicker,
│                 ExperienceTypePill, IntensityBadge, IntegritySeal, ParameterStarField,
│                 parameterVisuals, RatingScale? (no — stays in controls)
├── box/          BoxCard, boxVisuals, GroupMemberPills, groupVisuals
├── session/      SessionModeChrome, SessionModeFooter, sessionModeVisuals, ScreenHeader, ScreenTitle
└── demo? → fold DemoAuthShortcuts, AuthModeIntro into feedback/ or access pages (decide at move time; keep ≤6 groups)
```

(Exact membership finalized at move time; rule: ≤ ~6 files per group, group name = product concept.)

**Batch B (moderate risk) — clarify the session-mode split at `presentation/` level:**

```text
presentation/
├── access/         auth/, bootstrap/, onboarding/, unknown-session/, quick-guide/
├── collection/     groups/, boxes/, experiences/, suggestions/     ← "collect" phase (experiences session)
├── moment/         box-home/ → experience-box/, shared-moment/     ← "draw & live it" phase
├── invite/         InvitePreviewPage, ShareInviteSheet
├── components/     (split per Batch A)
├── hooks/  styles/
```

*(As executed: `quick-guide/` landed under `access/` — it is consumed by auth and onboarding, not by the moment flow.)*

This makes `presentation/` read as the product rhythm — access → collect → invite → moment — and resolves `box-home` vs `boxes` by renaming `box-home` to `experience-box` (the domain's own term: `useExperienceBoxSessionEnd`, `RequireExperienceBoxSessionRoute`).

Batch B only executes if Batch A's gate is green and the import churn proves manageable; otherwise record as follow-up.

`content/suggestion-packs/` twin trees (`by-type/` + `text/`, 11 mirrored files each): intentional data/text separation keyed by pack name; **keep**, note in Phase 7 sweep.

### Native / Capacitor notes

* No changes to `android/`, `ios/`, `capacitor.config.ts`, `webDir`, or `index.html`. `npx cap sync` not required.
* Root `assets/` glob dependency (`content/brandAssets.ts`) untouched.

### Behavior preservation notes

* Route paths (`/box-home`, `/groups/...`) are URL strings in `app/routes.tsx` — **they do not change**; only import paths and folder names change.
* Tests/build gate after each batch: `npm test`, `npm run build` (from `client/`).

---

## 6. Other surfaces

### deploy/

**No moves.** Production red zone: VPS crontab → `cron-reset-demo.sh`, webhook → `webhook/receive.sh`, publish script paths, committed `demo-web/` build output. Internal prod/demo interleaving is real but the cure (folder split) risks production paths for cosmetic gain. Mitigation: a short "map" section at the top of `deploy/README.md` naming the two stacks (prod vs demo) and which files belong to each. Documented exception.

### openapi/

**No change.** Single-file contract at root communicates "contract-first" clearly.

### scripts/

**No change.** One file, discoverable, referenced by CI and docs.

### assets/

**No change.** Client build consumes these paths via `import.meta.glob`.

### .github/ (justified fixes only)

* `docs-ci.yml`: replace stale trigger paths — remove `agents/**`, replace invalid `../../ia/**` with `ia/**`. Today changes under `ia/` do not trigger docs CI even though `ia/*` files are refs-scanned; this is a correctness fix, not a reorganization.
* `api-ci.yml`: untouched.

---

## Execution order (after design sign-off)

Ordered batches from lowest risk to highest:

1. **Phase 2** — root knowledge surface: create `backlog.md` + refs entry; rewrite README layout section. Gate: refs validation green (fixes pre-existing red).
2. **Phase 3** — docs: verification only (parity + links + refs). No moves.
3. **Phase 4** — `.github/docs-ci.yml` trigger-path fix; `deploy/README.md` prod/demo map section. Gate: workflow paths sane, refs green.
4. **Phase 5a** — api: extract `platform/{security,web,common,demo}`. Gate: `./mvnw test`.
5. **Phase 5b** — api: regroup tests by domain. Gate: `./mvnw test`.
6. **Phase 6a** — client: split `presentation/components` by concept. Gate: `npm test` + `npm run build`.
7. **Phase 6b** — client: session-mode grouping + `box-home` → `experience-box` rename (only if 6a green and churn acceptable). Gate: same.
8. **Phase 7** — sweep for leftovers.
9. **Phase 8** — documenter (en → pt-br → it), README final pass, refs validation, all gates re-run.
