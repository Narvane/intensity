# Data Model

This document describes the functional data model of Intensity — the domain entities, their relationships, taxonomies, parameters, and default content that structure how the product works. It specifies *what exists* and *how concepts relate*, without implementation detail.

**Audience:** analysts, product owners, designers, and functional QA — people who need to understand the solution's domain without knowing how it was built.

---

## Short

Intensity's domain revolves around **participants** who form **groups**, collect **experiences** inside themed **boxes**, and live a **draw-and-reveal** moment together. **Pre-defined suggestions by box type** guide experience creation — they function as an implicit tutorial and change with the active box type. Each experience carries a description, an overall **intensity** level (1–5), three **parameters** (effort, openness, novelty), and a proposer's **reflection**. Eleven **box types** organize ideas by context; two **access modes** (Experiences and Experience Box) scope who can do what. Registration is gated by an **allowlist**; draw results are transient and not stored.

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
- **Pre-defined suggestions:** embedded packs per box type, shown in the Suggestion wizard step; they function as an implicit creation tutorial

### Parameters and defaults

- Registration requires an allowlisted email
- Default box type when unspecified: **Outings with friends**
- Default wizard intensity and draw-filter level: **3**
- Suggested intensity: rounded average of the three parameter ratings (proposer may override)
- **165 pre-defined suggestions by box type** (11 types × 5 levels × 3 each), with localized texts in Portuguese, English, and Italian
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

#### Pre-defined suggestions by box type

Pre-defined suggestions are **essential embedded content** in Intensity. They are not optional decoration: they function as an **implicit tutorial** for how to create good experiences — tone, granularity, boldness at each intensity level, and fit with the box theme. Each suggestion must be carefully crafted; weak examples would teach the wrong patterns to proposers.

**How they work in the product:**

- In the **Suggestion** step of the creation wizard, the proponent sees examples grouped by **intensity level** (1–5).
- The example set comes from the **active box type** — the type of the box where the experience will be saved (session context in Experiences mode).
- Creating a box with a given type does not copy suggestions into the box; instead, that type **selects which suggestion pack** is offered when creating experiences.
- Tapping a suggestion populates the description field (editable at any wizard stage).
- Structure: **11 box types × 5 intensity levels × 3 suggestions = 165** embedded examples.

**Source in the system:**

- Pack structure and i18n keys: `client/mobile-app/src/commonMain/kotlin/com/intensity/mobile/app/ui/experience/ExperienceBoxSuggestionPacks.kt`
- Canonical texts by language:
  - Portuguese: `client/mobile-app/src/commonMain/kotlin/com/intensity/mobile/app/platform/i18n/dictionaries/SuggestionPacksPt.kt`
  - English: `client/mobile-app/src/commonMain/kotlin/com/intensity/mobile/app/platform/i18n/dictionaries/SuggestionPacksEn.kt`
  - Italian: `client/mobile-app/src/commonMain/kotlin/com/intensity/mobile/app/platform/i18n/dictionaries/SuggestionPacksIt.kt`

**Localization:** suggestion texts are served in the selected UI language (Portuguese, English, or Italian). The catalog below lists the **English** embedded values.

##### Outings with friends (`outings_friends`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | Order food together and eat while watching something at someone's place |
| 1 - Light | 2 | Try a simple group activity together on a calm evening |
| 1 - Light | 3 | Get together just to talk at home with no rush |
| 2 - Uncomfortable | 1 | Go to a different bar or cafe at night |
| 2 - Uncomfortable | 2 | Have a barbecue at someone's house on a Saturday afternoon |
| 2 - Uncomfortable | 3 | Go out for dinner somewhere no one in the group has tried yet |
| 3 - Courage | 1 | Go to karaoke or bowling as a group |
| 3 - Courage | 2 | Spend a full day together somewhere in the city |
| 3 - Courage | 3 | Do an outing that is not the group's usual style |
| 4 - Bold | 1 | Organize a themed night (e.g., Mexican food, Italian food, etc.) |
| 4 - Bold | 2 | Plan a full day with several different activities |
| 4 - Bold | 3 | Set up a meetup with more people beyond the usual group |
| 5 - Adrenaline | 1 | Take a group trip to a nearby city for the weekend |
| 5 - Adrenaline | 2 | Rent a place to spend a weekend together |
| 5 - Adrenaline | 3 | Do a totally out-of-the-ordinary outing for the group |

##### Outings as a couple (`outings_couple`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | Have coffee together somewhere new |
| 1 - Light | 2 | Go for a walk and talk without using your phones |
| 1 - Light | 3 | Watch a movie together picked in the moment |
| 2 - Uncomfortable | 1 | Have dinner at a restaurant you've never been to |
| 2 - Uncomfortable | 2 | Have a picnic in a city park |
| 2 - Uncomfortable | 3 | Go out at night without planning the destination |
| 3 - Courage | 1 | Spend a full day together outside the house |
| 3 - Courage | 2 | Do an activity one of you likes and the other would not usually choose |
| 3 - Courage | 3 | Go together to an event (concert, fair, etc.) |
| 4 - Bold | 1 | Spend a weekend outside the city |
| 4 - Bold | 2 | Plan a surprise day for your partner |
| 4 - Bold | 3 | Host a themed night at home |
| 5 - Adrenaline | 1 | Travel together to a destination you've talked about before |
| 5 - Adrenaline | 2 | Do a memorable experience together (trail, different kind of outing) |
| 5 - Adrenaline | 3 | Spend a full day unplugged (no phone) |

##### Trips with friends (`trips_friends`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | Talk about a trip you always mention |
| 1 - Light | 2 | Choose a destination everyone finds interesting |
| 1 - Light | 3 | Put together a simple itinerary as a group |
| 2 - Uncomfortable | 1 | Take a day trip to a nearby city |
| 2 - Uncomfortable | 2 | Spend a full sightseeing day in another city |
| 2 - Uncomfortable | 3 | Explore a new place in your region |
| 3 - Courage | 1 | Plan a real group trip |
| 3 - Courage | 2 | Spend a weekend away together |
| 3 - Courage | 3 | Split tasks to organize the trip |
| 4 - Bold | 1 | Travel to a destination no one in the group knows |
| 4 - Bold | 2 | Plan a trip with varied activities (nature, culture, etc.) |
| 4 - Bold | 3 | Do a more structured group trip |
| 5 - Adrenaline | 1 | Take a long trip together |
| 5 - Adrenaline | 2 | Travel abroad as a group |
| 5 - Adrenaline | 3 | Plan a memorable experience during the trip |

##### Trips as a couple (`trips_couple`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | List three destinations you would both be up for |
| 1 - Light | 2 | Look at photos from an old trip together and reminisce |
| 1 - Light | 3 | Browse flight options out of curiosity, no commitment |
| 2 - Uncomfortable | 1 | Book a weekend at a nearby inn |
| 2 - Uncomfortable | 2 | Create a one-day itinerary with stops chosen by both of you |
| 2 - Uncomfortable | 3 | Visit a museum or neighboring city together for the first time |
| 3 - Courage | 1 | Plan a 3- to 5-day trip with an agreed budget |
| 3 - Courage | 2 | Try a type of accommodation different from your usual one |
| 3 - Courage | 3 | Travel without a fixed itinerary, only the destination set |
| 4 - Bold | 1 | Take a trip that includes a light challenge (trail, road trip, etc.) |
| 4 - Bold | 2 | Set up a partially surprise trip (only part is secret) |
| 4 - Bold | 3 | Return to a favorite destination but with a new itinerary |
| 5 - Adrenaline | 1 | Plan an international trip together |
| 5 - Adrenaline | 2 | Take a long trip with near-total disconnection |
| 5 - Adrenaline | 3 | Schedule an experience outside your comfort zone, agreed by both |

##### Intimate as a couple (`intimate_couple`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | Have a deeper conversation about something light |
| 1 - Light | 2 | Recall important moments in your relationship |
| 1 - Light | 3 | Share something never said before (light) |
| 2 - Uncomfortable | 1 | Create a planned romantic moment at home |
| 2 - Uncomfortable | 2 | Write something meaningful to each other |
| 2 - Uncomfortable | 3 | Create a special moment outside your routine |
| 3 - Courage | 1 | Have a conversation that has been avoided |
| 3 - Courage | 2 | Try something new together in the relationship |
| 3 - Courage | 3 | Break out of your emotional routine as a couple |
| 4 - Bold | 1 | Open up emotionally about something important |
| 4 - Bold | 2 | Explore something new in the relationship more deeply |
| 4 - Bold | 3 | Create a moment that is totally outside your usual pattern |
| 5 - Adrenaline | 1 | Have a transformative conversation about your relationship |
| 5 - Adrenaline | 2 | Do something that requires high vulnerability |
| 5 - Adrenaline | 3 | Create a memorable experience for the couple |

##### Experiences with friends (`experiences_friends`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | Try a snack together from a new place |
| 1 - Light | 2 | Take a short outing no one has done before |
| 1 - Light | 3 | Exchange ideas for experiences outside your routine |
| 2 - Uncomfortable | 1 | Go to a workshop or short class as a group (cooking, dance, etc.) |
| 2 - Uncomfortable | 2 | Take a guided tour or a different cultural visit |
| 2 - Uncomfortable | 3 | Try a light outdoor activity |
| 3 - Courage | 1 | Book an experience that requires a bit more courage as a group |
| 3 - Courage | 2 | Plan a day with two new experiences back to back |
| 3 - Courage | 3 | Invite someone from outside for an experience with the group |
| 4 - Bold | 1 | Do an intense group activity (rappelling, rafting, etc.) if everyone agrees |
| 4 - Bold | 2 | Organize a themed event with brand-new experiences |
| 4 - Bold | 3 | Spend a full day trying new things around the city |
| 5 - Adrenaline | 1 | Plan a memorable group experience outside the city |
| 5 - Adrenaline | 2 | Commit to something the group would never do alone |
| 5 - Adrenaline | 3 | Create a group ritual to repeat after a powerful experience |

##### Break routine (`break_routine`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | Change where you have breakfast or lunch during the week |
| 1 - Light | 2 | Walk through a neighborhood you rarely visit |
| 1 - Light | 3 | Do a routine task at a different time or with different music |
| 2 - Uncomfortable | 1 | Go to a movie theater or play alone or with someone on an unusual day |
| 2 - Uncomfortable | 2 | Try a low-cost hobby for an afternoon |
| 2 - Uncomfortable | 3 | Change your home-to-work route for one day |
| 3 - Courage | 1 | Schedule a date with yourself: museum, park, bookstore |
| 3 - Courage | 2 | Do something you've postponed for months out of laziness |
| 3 - Courage | 3 | Invite someone to break routine together |
| 4 - Bold | 1 | Plan an off-grid day or almost screen-free day |
| 4 - Bold | 2 | Take a short solo round trip on the same day |
| 4 - Bold | 3 | Take on a personal public experience (run, class, etc.) |
| 5 - Adrenaline | 1 | Change a structural part of your routine for a week (sleep, work, etc.) |
| 5 - Adrenaline | 2 | Do an experience that scares you a little but still attracts you |
| 5 - Adrenaline | 3 | Share with the group a plan to step out of your comfort zone |

##### First times (`first_times`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | Write down three small firsts for the week |
| 1 - Light | 2 | Try an ingredient or recipe you've never made |
| 1 - Light | 3 | Listen to a music genre you usually avoid |
| 2 - Uncomfortable | 1 | Go alone to an event where you know no one |
| 2 - Uncomfortable | 2 | Try a new exercise modality |
| 2 - Uncomfortable | 3 | Try a sport or dynamic the group has never done |
| 3 - Courage | 1 | Do something artistic for the first time (class, open mic, etc.) |
| 3 - Courage | 2 | Drive or take public transport to a place new to you |
| 3 - Courage | 3 | Ask for help with something you always do alone |
| 4 - Bold | 1 | Schedule a first time that involves light vulnerability |
| 4 - Bold | 2 | Do a first-ever overnight stay or short trip |
| 4 - Bold | 3 | Share with the group a first time that scared you |
| 5 - Adrenaline | 1 | Plan a first time that touches identity or a real fear |
| 5 - Adrenaline | 2 | Arrange with the group a brand-new experience for everyone |
| 5 - Adrenaline | 3 | Record and celebrate a remarkable first time |

##### Light discomfort (`light_discomfort`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | Say no to something small you always accept out of politeness |
| 1 - Light | 2 | Take a slightly longer walk than usual |
| 1 - Light | 3 | Try clothing or a style outside your usual pattern |
| 2 - Uncomfortable | 1 | Join an honest conversation circle about a slightly uncomfortable topic |
| 2 - Uncomfortable | 2 | Do a group activity where you are not the best |
| 2 - Uncomfortable | 3 | Ask someone close to you for sincere feedback |
| 3 - Courage | 1 | Propose to the group something that brings healthy awkwardness |
| 3 - Courage | 2 | Stay in silence or guided meditation longer than feels comfortable |
| 3 - Courage | 3 | Take on a different role in a group dynamic |
| 4 - Bold | 1 | Arrange a light physical or social experience outside your comfort zone |
| 4 - Bold | 2 | Talk calmly about a personal boundary in the group |
| 4 - Bold | 3 | Do an improv or theater activity with the group |
| 5 - Adrenaline | 1 | Plan an experience that mixes fun and agreed light discomfort |
| 5 - Adrenaline | 2 | Revisit an avoided topic with group support |
| 5 - Adrenaline | 3 | Celebrate courage after a well-lived uncomfortable moment |

##### Connection moments (`connection_moments`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | Do a quick round: one sincere compliment for each person |
| 1 - Light | 2 | Share a good memory involving the group |
| 1 - Light | 3 | Ask how you are and truly listen |
| 2 - Uncomfortable | 1 | Prepare coffee or a shared snack with no rush |
| 2 - Uncomfortable | 2 | Do a group gratitude dynamic |
| 2 - Uncomfortable | 3 | Share one small personal goal for the next few months |
| 3 - Courage | 1 | Create a simple connection ritual (weekly check-in, etc.) |
| 3 - Courage | 2 | Take a silent walk followed by an open conversation |
| 3 - Courage | 3 | Write anonymous support cards within the group |
| 4 - Bold | 1 | Facilitate a deeper conversation with agreed respect rules |
| 4 - Bold | 2 | Plan a meetup just to listen to one another |
| 4 - Bold | 3 | Do an activity that requires real cooperation (cook together, etc.) |
| 5 - Adrenaline | 1 | Plan a short retreat or long meetup focused on connection |
| 5 - Adrenaline | 2 | Create a group commitment to care for one another |
| 5 - Adrenaline | 3 | Live a shared vulnerability moment with safety |

##### Different experiences (`different_experiences`)

| Intensity | # | Suggestion |
|-------------|---|----------|
| 1 - Light | 1 | Swap roles in a simple dynamic (who organizes, who cooks, etc.) |
| 1 - Light | 2 | Try food from a country no one really knows well |
| 1 - Light | 3 | Watch a type of show the group has never attended |
| 2 - Uncomfortable | 1 | Take an alternative city tour (alleys, markets, street art) |
| 2 - Uncomfortable | 2 | Build a shared playlist and comment on each track |
| 2 - Uncomfortable | 3 | Go to a cultural space outside the group's radar |
| 3 - Courage | 1 | Arrange a sensory experience (blind dinner, etc.) |
| 3 - Courage | 2 | Do an improv dynamic with new rules |
| 3 - Courage | 3 | Plan a meetup with an unusual theme |
| 4 - Bold | 1 | Join an experience led by an outsider or specialist |
| 4 - Bold | 2 | Take a night route or go to an unusual location |
| 4 - Bold | 3 | Create a creative group experience with a final presentation |
| 5 - Adrenaline | 1 | Schedule an unusual experience that requires shared planning |
| 5 - Adrenaline | 2 | Travel or camp with an experimental itinerary |
| 5 - Adrenaline | 3 | Document and share learnings after the experience |

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
| Suggestion localization | Localized packs available for Portuguese, English, and Italian |
| Participant profile | No avatar, preferences, or notifications beyond name/email |
| Box type sections | Catalog sections exist in code; UI shows flat list |
