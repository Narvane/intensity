# Data Model

This document describes the functional data model of Intensity — the domain entities, their relationships, taxonomies, parameters, and default content that structure how the product works. It specifies *what exists* and *how concepts relate*, without implementation detail.

**Audience:** analysts, product owners, designers, and functional QA — people who need to understand the solution's domain without knowing how it was built.

---

## Short

Intensity's domain revolves around **participants** who form **groups**, collect **experiences** inside themed **boxes**, and live a **draw-and-reveal** moment together. Each experience carries a description, an overall **intensity** level (1–5), three **parameters** (effort, openness, novelty), and a proposer's **reflection**. Eleven **box types** organize ideas by context; two **access modes** (Experiences and Experience Box) scope who can do what. Registration is gated by an **allowlist**; draw results are transient and not stored.

---

## Medium

### Core entities

| Entity | What it represents |
|--------|---------------------|
| **Participant** | A registered person (display name, email, credentials) who can contribute and join groups |
| **Registration allowlist entry** | A pre-approved email permitted to sign up |
| **Group** | The set of participants who entered Experience Box mode together — identified by that exact combination |
| **Box** | A named, themed container where a group's experiences are collected |
| **Experience** | A concrete idea to do together, authored by one participant, belonging to one box |
| **Session context** | The operational scope of the current use: access mode, active group, active box |

### How they connect

```
Allowlist  →  permits  →  Participant
Participant  ↔  Group  (many-to-many, via who logs in together)
Group  →  owns  →  Box  (one-to-many)
Box  →  contains  →  Experience  (one-to-many)
Participant  →  authors  →  Experience  (one-to-many)
```

A **group** is not manually named — it emerges from the unique combination of participants who authenticate together in Experience Box mode. **Boxes** are created only in that mode; **experiences** are registered primarily through Experiences mode.

### What each experience carries

| Attribute | Meaning |
|-----------|---------|
| Description | The experience text (up to 1,000 characters) |
| Intensity | Overall boldness, levels 1–5 |
| Parameters | Three 1–5 ratings: effort, openness, novelty |
| Reflection | Proposer's justification about group acceptance |
| Author | Who registered it |
| Registration moment | When it was saved |
| Integrity seal | A fingerprint shown on cards as "Seal" |
| Parent box | Which box it belongs to |

### Taxonomies at a glance

- **Access modes:** Experiences (individual contribution) and Experience Box (group ritual)
- **Box types:** 11 thematic categories (outings, trips, intimacy, routine-breaking, novelty, discomfort, connection, and more — for couples and friends)
- **Intensity levels:** 1 Light → 5 Adrenaline
- **Draw filters:** Any, fixed intensity, or max intensity
- **Creation wizard steps:** Suggestion → Reflection → Parameters → Classification → Bifurcation

### Parameters and defaults

- Registration requires an allowlisted email
- Default box type when unspecified: **Outings with friends**
- Default wizard intensity and draw-filter level: **3**
- Suggested intensity: rounded average of the three parameter ratings (proposer may override)
- **165 embedded example suggestions** (11 box types × 5 levels × 3 each), currently in Portuguese for all UI languages
- **Consequences, swaps, and gradual progression** exist only as social guidance — not as tracked data

---

## Detailed

### Participant and registration

A **participant** is anyone who completed registration. They have a **display name** (shown in group lists), an **email** (login identity), and **credentials** (email + password).

Before becoming a participant, an email must appear on the **registration allowlist**. This is an administrative gate — not a concept users manage in the app, but it defines who may join. Example seed entries include `proponente@intensity.app`, `membro1@intensity.app`, and `membro2@intensity.app`.

Registered participants appear in the Experience Box login UI so groups can select who is present.

**Not modeled:** profile photos, notification preferences, or per-user settings beyond what the client stores locally (such as UI language).

### Group

A **group** is the set of people who entered **Experience Box** mode together. It has:

| Attribute | Meaning |
|-----------|---------|
| Participants | Members of this group |
| Creation moment | When this combination was first formed |

**Identity rule:** the same combination of participants always maps to the same group. If Alice and Bob log in together, they form one group; if Alice, Bob, and Carol log in, that is a different group. A participant may belong to multiple groups depending on session combinations.

**Not modeled:** a user-facing group name, group editing, or explicit group creation outside the login combination.

### Box

A **box** is a themed container where experiences for one group are collected.

| Attribute | Meaning |
|-----------|---------|
| Name | User-chosen label (e.g. "Saturday party") |
| Type | One of 11 thematic categories |
| Creation moment | When the box was created |
| Parent group | Which group owns this box |

Boxes are created in **Experience Box** mode, not during individual contribution. Each type carries presentation metadata (accent color, icon, subtitle hint) that orients the kind of ideas expected.

**Not modeled:** box rename, edit, or delete.

### Experience

An **experience** is a concrete idea to do together.

| Attribute | Constraint / rule |
|-----------|-------------------|
| Description | Required; max 1,000 characters |
| Intensity | Required; integer 1–5 |
| Effort | Required; 1–5 stars |
| Openness | Required; 1–5 stars |
| Novelty | Required; 1–5 stars |
| Reflection | Required in current flow; max 2,000 characters per reflection field |
| Author | Recorded at registration; only the author may edit or delete |
| Integrity seal | Derived from description; shown on cards |

#### Visibility rules

| Context | What others see |
|---------|-----------------|
| **Experiences mode** (author's list) | Full description for own experiences; others see summary only (intensity + seal, no text) |
| **Experience Box mode** (draw) | Intensity and parameters first; full description only after **Reveal** |

The interface also informs users that **experiences are not encrypted** — a transparency notice about how text is handled.

#### Reflection

The functional model supports three reflection fields:

| Field | Current behavior |
|-------|------------------|
| "Would everyone accept this, however unusual?" | **Collected** in the creation wizard |
| "Does it involve everyone?" | Supported in the data model; **not collected** in the current UI |
| "Is there mild discomfort?" | Supported in the data model; **not collected** in the current UI |

When only one field is populated, cards show that single question; when multiple are populated, all blocks appear.

### Session context

While not a user-managed entity, **session context** scopes every operation:

| Element | Values |
|---------|--------|
| Access mode | **Experiences** or **Experience Box** |
| Active group | Selected or formed at login |
| Active box | Selected box (in Experiences mode) |
| Box type | Type of the active box (drives suggestions and theming) |

| Mode | Who enters | Domain operations |
|------|------------|-------------------|
| **Experiences** | One participant | Register, edit, delete experiences; choose group and box |
| **Experience Box** | Multiple participants together | Form group, create boxes, browse, draw, reveal |

### Draw result (transient)

A **draw** randomly selects one experience from a box. It is **not persisted** — each activation creates a new selection.

| Element | Meaning |
|---------|---------|
| Selected experience | One experience from the box, filtered if requested |
| Filter applied | Any, fixed intensity, or max intensity |
| Reveal state | Whether the full description has been shown |

**Not modeled:** draw history, reveal events, completion status, or social practices (consequences, swaps).

---

### Entity relationship overview

```
                    ┌─────────────────────┐
                    │  Allowlist entry    │
                    └──────────┬──────────┘
                               │ permits
                               ▼
┌──────────────┐      ┌────────────────┐      ┌──────────────┐
│ Participant  │◄────►│     Group      │─────►│     Box      │
└──────┬───────┘      └────────────────┘      └──────┬───────┘
       │                                              │
       │ authors                                      │ contains
       ▼                                              ▼
              ┌──────────────────────────────────────────┐
              │              Experience                  │
              └──────────────────────────────────────────┘
```

---

### Taxonomies

#### Access modes

| User-facing label | Functional scope |
|-------------------|------------------|
| **Experiences** | Individual contribution: register and manage experiences |
| **Experience Box** | Group ritual: create boxes, draw, reveal |

#### Box types (11 categories)

Default: **Outings with friends**

| Type | Subtitle hint (EN) |
|------|-------------------|
| Outings with friends | Light to intense group hangouts |
| Outings as a couple | Cafes, walks, and dates for two |
| Trips as a couple | Getaways and destinations for two |
| Intimate as a couple | Connection and deeper conversations |
| Trips with friends | Day trips, weekends, or planned travel |
| Experiences with friends | Classes, tours, and group experiences |
| Break routine | Small routine breaks in daily life |
| First times | Try new things calmly |
| Light discomfort | Step a bit out of comfort zone with care |
| Connection moments | Presence, listening, and group bonding |
| Different experiences | Uncommon things for the group |

The catalog code groups types into presentation sections (friends, couple, personal, social), but the creation UI shows a flat list without section labels.

#### Intensity levels (1–5)

| Level | Label |
|-------|-------|
| 1 | Light |
| 2 | Uncomfortable |
| 3 | Courage |
| 4 | Bold |
| 5 | Adrenaline |

#### Experience parameters

Each dimension is rated 1–5 with defined verbal levels:

| Dimension | Question to the proposer |
|-----------|--------------------------|
| **Effort** | How demanding is this to do? |
| **Openness** | How much gentle exposure or sincerity does it ask? |
| **Novelty** | How different from what you usually do together? |

**Suggested intensity:** the system proposes a level based on the rounded average of the three ratings; the proposer may override in the Classification step.

#### Draw filters

| Filter | Behavior |
|--------|----------|
| **Any** | Draw from all experiences in the box |
| **Fixed intensity** | Only experiences at exactly level N |
| **Max intensity** | Experiences at level N or below |

Default filter level in the UI: **3**.

#### Creation wizard steps

| Step | Purpose |
|------|---------|
| 1 – Suggestion | Describe an idea or pick an example |
| 2 – Reflection | Justify whether everyone would accept it |
| 3 – Parameters | Rate effort, openness, novelty |
| 4 – Classification | Set final intensity (with suggestion) |
| 5 – Bifurcation | Review, save, optionally create another |

#### UI language (client preference)

| Code | Language |
|------|----------|
| `pt` | Portuguese (default) |
| `en` | English |
| `it` | Italian |

Stored on the client; not part of the persisted domain model.

---

### Parameters, constraints, and configurations

| Parameter | Value / rule |
|-----------|--------------|
| Description max length | 1,000 characters |
| Reflection max length | 2,000 characters per field |
| Intensity range | 1–5 (required) |
| Parameter ratings | 1–5 each, all three required |
| Default box type | Outings with friends |
| Default intensity (wizard and draw filter) | 3 |
| Author-only edit/delete | Only the experience author may modify or remove it |
| Allowlist-gated registration | Email must be on allowlist before signup |

---

### Default and embedded content

#### Registration allowlist (seed examples)

- `proponente@intensity.app`
- `membro1@intensity.app`
- `membro2@intensity.app`

#### Suggestion packs

The client embeds **165 example experiences**: 11 box types × 5 intensity levels × 3 suggestions each. Tapping an example populates the description field (editable at any stage).

**Localization gap:** suggestion text is currently **Portuguese only**, regardless of the selected UI language.

#### Onboarding and quick guide (client-only)

- Four-step onboarding narrative (problem → unusual moments → act → collect/draw/live)
- Quick guide sections: core rule, recommended flow, intensity tips, social practices

These are presentation content, not domain entities.

#### Social practices (guidance only)

The quick guide and principles document recommend practices that **have no corresponding entities**:

- Defining a **consequence** before revealing
- **Swapping** experiences of different intensity levels
- **Gradual progression** through intensity over time

---

### Canonical terminology

| Use in Layer 2 | Avoid |
|----------------|-------|
| Participant | User table, entity class names |
| Registration allowlist entry | Allowed emails table |
| Group | Fingerprint, group ID |
| Box | Experience box table |
| Experience | Description cipher, row |
| Parameters (effort / openness / novelty) | Star column names |
| Reflection | Additional info cipher |
| Integrity seal | Description hash field |
| Experiences mode / Experience Box mode | Internal mode codes |
| Proposer | Internal role labels |

---

## Gaps and limitations

| Topic | Status |
|-------|--------|
| Reflection model vs UI | Three fields supported; only one collected today |
| Box lifecycle | No rename, edit, or delete observed |
| Group naming | No user-facing name — participant list only |
| Draw persistence | Draws and reveal events are not stored |
| Social practices | Consequences, swaps, progression — guidance only |
| Suggestion localization | Portuguese text served for all UI languages |
| Participant profile | No avatar, preferences, or notifications beyond name/email |
| Box type sections | Catalog sections exist in code; UI shows flat list |
