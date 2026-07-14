# Referencer

Skill for citing, resolving, and maintaining the repository's central reference map.

## When to use

Whenever an agent or skill needs to:

* cite a project artifact in Markdown;
* discover the canonical path of a document, contract, or folder;
* create or move a file that other texts must reference;
* validate that `@ref:` citations still point to existing paths.

## Source of truth

The central map is @ref:refs (`docs/refs.yaml`).

Map conventions:

* Each artifact has a stable kebab-case `id`.
* The `path` field is the only field that changes when a file moves.
* Prose citations use `@ref:<id>` (backticks optional).
* Human-clickable links remain standard `[label](path)`, and the path must match the registered `path`.

English (`docs/en`) is the canonical specification. `docs/pt-br` and `docs/it` are translations with structural parity.

## How to cite

### Preferred form

```markdown
See @ref:en-architectural-decisions.
```

### Form with a human link

```markdown
See @ref:en-architectural-decisions — [Architectural Decisions](docs/en/solution-architecture/architectural-decisions.md).
```

### What to avoid

* Citing only a hardcoded path when an `id` already exists in the map.
* Inventing `@ref:` ids that are not in `docs/refs.yaml`.
* Updating only the `[text](path)` link while leaving the map `path` stale.
* Using ids in other formats (camelCase, underscore, spaces).

## Resolution flow

1. Open @ref:refs (`docs/refs.yaml`).
2. Locate the `id` by artifact role (`role`), layer (`layer`), or locale.
3. Use the resolved `path` to read or edit the file.
4. In written outputs (docs, backlog, READMEs, agent notes), cite via `@ref:<id>`.

If the needed artifact is missing from the map, register it before citing.

## When to register a new entry

Add an entry in `docs/refs.yaml` when you:

* create a new document under `docs/`;
* create an agent or skill under `ia/` that other texts should cite;
* promote a one-off file into a project reference artifact.

Template:

```yaml
- id: en-document-name
  path: docs/en/layer/document-name.md
  label: Human-readable label
  locale: en
  layer: solution-architecture
```

Rules:

* An `id` never changes after publication; change only `path` if the file moves.
* For product/engineering docs, register all three locales (`en`, `pt-br`, `it`) with matching ids (`en-…`, `pt-br-…`, `it-…`).
* For unique artifacts (agents, skills, contracts), a single `id` is enough.

## When a file moves

1. Update only the corresponding entry's `path` in `docs/refs.yaml`.
2. Update Markdown links `[label](path)` that pointed to the old path.
3. Do not rewrite `@ref:<id>` citations — they remain valid.

## Validation

After creating, moving, or citing artifacts:

```bash
python3 scripts/validate-refs.py
```

The script checks:

* that every map `path` exists;
* that every `@ref:<id>` in `validate.scan_files` resolves to a known `id`.

If an important file starts containing `@ref:` citations often, consider adding it to `validate.scan_files` in `docs/refs.yaml`.

## Quick checklist

* [ ] I cited via `@ref:<id>`, not only via a bare path.
* [ ] The `id` exists in `docs/refs.yaml`.
* [ ] If I created a new artifact, I registered the entry (and translations, if it is a doc).
* [ ] If I moved a file, I updated the map `path`.
* [ ] I ran `python3 scripts/validate-refs.py` when refs or citations changed.
