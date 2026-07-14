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
(paste)
```

### Proposed

```text
(paste)
```

### Why this communicates Intensity better

### Migration notes / risks

---

## 2. docs/

### Proposed (en shown; pt-br/it must stay in parity)

```text
(paste)
```

### Refs impact

* ids stay stable; list `path` updates required:

---

## 3. ia/

### Proposed

```text
(paste)
```

---

## 4. api/

### Proposed package / folder story

```text
(paste)
```

### Behavior preservation notes

* REST mappings unchanged:
* Tests to run: `./mvnw test`

---

## 5. client/

### Proposed `client/src` story

```text
(paste)
```

### Native / Capacitor notes

### Behavior preservation notes

* Tests/build: `npm test`, `npm run build`

---

## 6. Other surfaces

### deploy /

### openapi /

### scripts /

### assets /

### .github / (only if justified)

---

## Execution order (after design sign-off)

Ordered batches from lowest risk to highest (customize after diagnosis):

1.
2.
3.
4.
…
