# Documenter

Skill for creating and updating Intensity's official documentation under `docs/`, always in all three languages, aligned with the real state of the code and domain.

## When to use

Use at the end of development or refactoring tasks — and whenever the user asks for documentation — when the change affects:

* product conception;
* solution specification;
* solution architecture;
* engineering and operations;
* contracts, artifacts, or processes described in `docs/`.

Do not document cosmetic noise. Document changes that alter understanding, behavior, structure, or decisions.

## Required skills

Before and during documentation work:

1. Follow @ref:skill-referencer to cite and register artifacts.
2. When the change is structural or cognitive, align the text with @ref:skill-architectural-philosophy.

## Languages and canonicity

Documentation lives in three trees with structural parity:

| Locale | Folder | Role |
|--------|--------|------|
| `en` | @ref:docs-en | Canonical — write this first |
| `pt-br` | @ref:docs-pt-br | Faithful translation |
| `it` | @ref:docs-it | Faithful translation |

Rules:

* Always update **all three** languages in the same task.
* Write or revise English first; then propagate to `pt-br` and `it`.
* Keep the same section titles, the same order, and the same information density.
* Do not leave a locale stale “for later.”

## Layer map

Choose the document by the nature of the change:

### Product conception

* @ref:en-overview-what-is-it — what the product is
* @ref:en-how-it-works — how it works in the user experience
* @ref:en-principles-why-it-works-this-way — why it works this way

### Solution specification

* @ref:en-data-model — entities and relationships
* @ref:en-experience-and-identity — experience and identity
* @ref:en-functional-components — functional components
* @ref:en-design-system — design system

### Solution architecture

* @ref:en-architectural-decisions — ADRs
* @ref:en-artifacts — system artifacts
* @ref:en-integrations-and-communication — integrations and communication
* @ref:en-platforms-and-environments — platforms and environments

### Engineering and operations

* @ref:en-development-process — development process
* @ref:en-technical-decisions — technical decisions (DTs)
* @ref:en-team-and-responsibilities — team and responsibilities
* @ref:en-tools — tools inventory

Always mirror the equivalent `pt-br-…` and `it-…` ids.

## Standard document structure

Preserve the Short / Medium / Detailed format already used in `docs/`:

| Locale | Sections |
|--------|----------|
| `en` | `## Short` → `## Medium` → `## Detailed` |
| `pt-br` | `## Curta` → `## Média` → `## Detalhada` |
| `it` | the locale equivalent already present in the file |

Content guidelines:

* **Short / Curta:** compressed view of the current state; fits in a few paragraphs.
* **Medium / Média:** tables, decision indexes, flows, and operational rules.
* **Detailed / Detalhada:** deepening per item (AD-xx, DT-xx, entities, flows).

When updating:

* adjust Short so a quick reading reflects the new state;
* update Medium with indexes, tables, and consequences;
* detail in Detailed only what materially changed;
* remove claims that the code or architecture made false.

## Workflow

```text
1. Identify the real delta (code, structure, decision, contract).
2. Choose which docs across the four layers are affected.
3. Update or create the en version.
4. Propagate the same change to pt-br and it.
5. Cite artifacts with @ref:<id> (referencer skill).
6. If a new document was created: register en/pt-br/it in docs/refs.yaml.
7. Validate: python3 scripts/validate-refs.py
```

### Create new documentation

1. Create the file under `docs/en/<layer>/`.
2. Use the Short / Medium / Detailed section template.
3. Create equivalents under `docs/pt-br/` and `docs/it/` with the same filename.
4. Register the three ids in @ref:refs.
5. Cite the new doc from related docs when it helps.

### Update existing documentation

1. Read the current `en` version.
2. Rewrite only what is needed to match the new state — without inflating the text.
3. Ensure Short remains true after the change.
4. Propagate to `pt-br` and `it` with semantic equivalence, not careless machine translation.
5. Update `@ref:` citations if new artifacts enter the narrative.

## What to document by change type

| Change type | Typical docs |
|-------------|--------------|
| New product concept / UX flow | overview, how-it-works, principles, experience-and-identity, functional-components |
| Entity, field, relationship, migration | data-model, artifacts, openapi if the contract changes |
| Structural system decision | architectural-decisions, artifacts, integrations, platforms |
| Stack, lib, pipeline, folder organization | technical-decisions, development-process, tools |
| REST contract | @ref:openapi + integrations-and-communication + artifacts as needed |
| Cognitive code reorganization | technical-decisions, artifacts, development-process — explain the cognitive “why” without becoming a refactor tutorial |

## Quality

* Document the system's intended/current state, not the history of the AI conversation.
* Prefer precision over marketing.
* Use `@ref:<id>` to point to artifacts; do not duplicate fragile paths.
* If a new decision deserves an identifier (AD-xx / DT-xx), add it to the index and detail in all three languages.
* Do not invent behavior the code does not implement.

## Final checklist

* [ ] `en` version updated or created.
* [ ] `pt-br` and `it` versions in parity.
* [ ] Correct docs per layer; no irrelevant files touched.
* [ ] Citations via @ref:skill-referencer.
* [ ] New artifacts registered in @ref:refs.
* [ ] `python3 scripts/validate-refs.py` ok when refs changed.
