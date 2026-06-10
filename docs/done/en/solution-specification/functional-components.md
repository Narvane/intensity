# Functional Components

This document catalogs the functional modules, screens, user flows, and interface behaviors of Intensity — what the user can do, where, and under which conditions. It specifies *what exists functionally* in the interface, without implementation detail.

**Audience:** analysts, product owners, designers, and functional QA — people who need to map features, journeys, and screen behaviors without knowing how the app was built.

---

## Curta

Intensity is a **mobile application** organized around **eleven primary views** plus overlays. After bootstrap and optional onboarding, the user authenticates in one of three modes (**Experiences**, **Experience Box**, or **Register**). The **Experiences** path flows through group selection → box selection → experience list → creation wizard. The **Experience Box** path flows through box list → optional box creation → shared moment (draw and reveal). Each screen handles **loading**, **empty**, and **error** states explicitly. A five-step **creation wizard** guides experience registration. The **shared moment** supports intensity filters and a flip-card reveal ritual.

---

## Média

### Functional modules

| Module | Purpose |
|--------|---------|
| **Bootstrap** | Load language preference and first-run state before showing content |
| **Onboarding** | Four-step illustrated introduction to the product story |
| **Quick guide** | Reusable manual with core rules, flow, and tips |
| **Authentication** | Login (Experiences or Experience Box), registration, help access |
| **Group selection** | Pick which participant group to contribute to (Experiences mode) |
| **Box selection** | Pick which box within the group (Experiences mode) |
| **Experience list** | View, reveal, and delete own experiences in the active box |
| **Creation wizard** | Five-step guided flow to register a new experience |
| **Box home** | List and create boxes (Experience Box mode) |
| **Shared moment** | Random draw with filters, alignment hint, and card reveal |
| **Error recovery** | Screen for unrecognized session state with exit options |

### Screen catalog

| # | Screen | When shown |
|---|--------|------------|
| 1 | **Bootstrap loading** | Language/onboarding preferences not yet ready |
| 2 | **Onboarding** (4 steps) | First launch |
| 3 | **Quick guide** | From onboarding or auth help; overlay |
| 4 | **Authentication** | No active session; onboarding completed |
| 5 | **Unknown session** | Session access mode not recognized |
| 6 | **Group selection** | Experiences mode; no group chosen |
| 7 | **Box selection** | Experiences mode; group set, no box chosen |
| 8 | **Experience list** | Experiences mode; group and box set |
| 9 | **Creation wizard** | Overlay from experience list |
| 10 | **Box home** | Experience Box mode |
| 11 | **Create box** | Sub-view from box home |
| 12 | **Shared moment** | Experience Box mode; box opened |

Authentication also contains three **sub-panes** (not separate routes): Experiences login, Experience Box multi-user login, and Register.

### Primary user flows

```
Flow A — First launch
  Loading → Onboarding (4 steps) → [optional Quick guide] → Authentication

Flow B — Experiences (individual contribution)
  Authentication → Group selection → Box selection → Experience list
    → [+ Create experience] → Wizard overlay → back to list
  Back: list → box selection → group selection
  Exit: logout from any authenticated screen

Flow C — Experience Box (group ritual)
  Authentication (multi-user) → Box home → [Create box] → Box home
    → Open box → Shared moment → Draw → Align → Reveal → Back to draw
  Back: shared moment → box home
  Exit: logout

Flow D — Error recovery
  Unknown session → Exit (logout) or Enter Experience Box (clears session)
```

### Creation wizard steps

| Step | Label | User action |
|------|-------|-------------|
| 1 — Suggestion | Write description or tap a box-type suggestion as inspiration |
| 2 — Reflection | Justify why the group would accept the idea |
| 3 — Parameters | Rate effort, openness, novelty (1–5 stars each) |
| 4 — Classification | Confirm or adjust overall intensity (auto-suggested from parameters) |
| 5 — Bifurcation | Review summary; save and create another, or finish |

The wizard shows a persistent description card across steps and a five-segment progress indicator.

### Shared moment features

- **Filter modes:** Any (no intensity filter), Exact (fixed level 1–5), Up to (maximum level inclusive)
- **Draw action:** random selection from eligible experiences in the box
- **Result card:** shows intensity cover (level, parameters, seal) before reveal
- **Alignment hint:** urges group agreement before flipping
- **Reveal:** flip card to read full description
- **Return:** back to draw for another selection

### Box types (11)

Each type has a title, subtitle hint, distinct visual accent, and associated suggestion pack:

| Type | Subtitle hint (EN) |
|------|-------------------|
| Outings with friends | Light to intense outings in a group |
| Outings as a couple | Cafés, walks, and outings for two |
| Couple trips | Getaways and destinations for two |
| Intimate couple | Deeper connection and conversations |
| Trips with friends | Day trips, weekends, or planned travel |
| Experiences with friends | Courses, tours, and group experiences |
| Break the routine | Small habit breaks in daily life |
| First times | Trying new things calmly |
| Light discomfort | Stepping slightly out of comfort zone, with care |
| Connection moments | Presence, listening, and group bonding |
| Different experiences | Unusual things for the group |

Default box type when unspecified: **Outings with friends**.

---

## Detalhada

### Bootstrap loading

**Purpose:** Prepare language and onboarding state before rendering the main flow.

**Behavior:** Full-screen centered spinner. No user action. Transitions automatically to onboarding (first run) or authentication.

**States:** Loading only.

---

### Onboarding (4 steps)

**Purpose:** Introduce the product story and value proposition on first launch.

**Content per step:**
1. Problem — repetitive experiences, missing closeness
2. Insight — memorable moments were unexpected, yet postponed
3. Call to action — don't wait for chance; Intensity pushes you to act
4. Mechanics — collect unusual ideas, draw one, live memorable moments

**Actions:** Back, Next, Start (finish), Open quick guide. Language selector available.

**States:** No loading, empty, or error states. Can be reopened from authentication (non-first-run mode).

---

### Quick guide

**Purpose:** Persistent reference for product rules and social recommendations.

**Sections:** Core rule (7 bullets), recommended flow (3 bullets), intensity tips (4 bullets), consequence tips (4 bullets), intensity essence (2 bullets), closing tip card.

**Actions:** Start (first run — dismisses and continues), Close (reopen mode).

**States:** No loading, empty, or error states.

---

### Authentication

**Purpose:** Entry point for all sessions. Three modes selectable via cards.

**Experiences login pane:**
- Email and password fields
- Login action
- On success → group selection flow

**Experience Box login pane:**
- Add one or more user credential rows (email + password each)
- Login action — all credentials must succeed; together they define the group
- On success → box home

**Register pane:**
- Display name, email, password
- Register action — email must be on permission list
- On success → returns to login

**Toolbar:** Quick guide icon, onboarding reopen icon, language selector.

**States:**

| State | Presentation |
|-------|-------------|
| Loading | Button text changes to "Entering…" / "Registering…" |
| Error | Snackbar: login failure, token error, credential error, registration error |

---

### Unknown session

**Purpose:** Recovery when the session access mode is not Experiences or Experience Box.

**Content:** Warning with raw access mode value displayed.

**Actions:** Exit (logout), Enter Experience Box (clears session → returns to auth).

**States:** Persistent warning — not a transient snackbar.

---

### Group selection

**Purpose:** In Experiences mode, choose which participant group to contribute to.

**Content:** List of groups showing participant names and member count.

**Actions:** Tap group → Enter (updates session, proceeds to box selection). Logout from top bar.

**States:**

| State | Presentation |
|-------|-------------|
| Loading | Centered spinner |
| Empty | Card: "No groups yet…" |
| Error | Snackbar on load or selection failure |

---

### Box selection (Experiences mode)

**Purpose:** Choose which box within the selected group to add experiences to.

**Content:** Two-column grid of box visual cards — name, type badge with icon, type accent color.

**Actions:** Tap box → proceeds to experience list. Back → clears group (returns to group selection). Logout.

**States:**

| State | Presentation |
|-------|-------------|
| Loading | Centered spinner |
| Empty | Plain text: no boxes in this group |
| Error | Snackbar on load or selection failure |

---

### Experience list

**Purpose:** View and manage the current user's experiences in the active box during this session.

**Content:**
- Top bar themed to active box type
- Section: registered experiences (own contributions only in current session)
- Each experience card: intensity badge, parameters, seal, reveal/delete actions
- Reveal toggles visibility of full description text
- Delete removes own experience

**Actions:** + Create experience (opens wizard overlay). Back → clears box (returns to box selection). Logout.

**States:**

| State | Presentation |
|-------|-------------|
| Loading | Small spinner in section header |
| Empty | Card: no experiences yet |
| Error | Snackbar on list, reveal, or delete failure |

---

### Creation wizard (overlay)

**Purpose:** Guided five-step registration of a new experience.

**Persistent elements:** Description card (shows current text), five-segment progress bar, step pill indicator.

**Step details:**

**1 — Suggestion**
- Free-text description field
- Box-type suggestion pack displayed by intensity level — tappable examples as inspiration
- Suggestions change according to active box type

**2 — Reflection**
- Text field for group-acceptance justification

**3 — Parameters**
- Three star-rating rows: effort (teal), openness (lime), novelty (rose)
- Each with help text and dynamic per-level description

**4 — Classification**
- Auto-suggested intensity from parameter average (user can override)
- Five intensity levels with subtitles and colors

**5 — Bifurcation**
- Summary review of all entered data
- Save and create another (resets wizard, keeps overlay open)
- Save and finish (closes overlay, refreshes list)

**Validation:** Description required; reflection required; all parameters rated; intensity set. Errors via snackbar.

**States:**

| State | Presentation |
|-------|-------------|
| Loading | Semi-transparent overlay with spinner on submit |
| Empty | Suggestion step shows pack examples even before user input |
| Error | Snackbar: validation messages, submission failure |

---

### Box home (Experience Box mode)

**Purpose:** List group's boxes and create new ones.

**Content:** Two-column grid of box visual cards. Create box call-to-action.

**Actions:** Tap box → shared moment. Create box → create sub-view. Logout.

**States:**

| State | Presentation |
|-------|-------------|
| Loading | Centered spinner |
| Empty | Text: no boxes |
| Error | Snackbar on load failure |

---

### Create box (sub-view)

**Purpose:** Register a new box for the current group.

**Content:**
- Scrollable type picker — 11 options with title, subtitle, accent
- Name text field

**Actions:** Save → returns to box home with new box listed. Back → returns without saving.

**Validation:** Name required (snackbar if empty).

**States:**

| State | Presentation |
|-------|-------------|
| Loading | Button text "Creating…" |
| Error | Snackbar on API failure |

---

### Shared moment

**Purpose:** Group ritual — draw a random experience, align, reveal.

**Content:**
- Top bar themed to box type
- Filter chips: Any / Exact / Up to
- Intensity dot selector (when Exact or Up to selected)
- Draw button (label adapts to filter mode)
- Result area: flip card or hints

**Flow:**
1. Select filter (and level if applicable)
2. Tap draw → random selection
3. Card shows intensity cover (level, parameters, seal)
4. Alignment hint displayed
5. Flip → full description visible
6. Back to draw → return to step 1

**States:**

| State | Presentation |
|-------|-------------|
| Loading | Button text "Choosing…" during draw |
| Empty (pre-draw) | Hint card encouraging activation |
| Empty (post-draw) | "No available experience" when pool is empty for filter |
| Error | Snackbar with error prefix |

---

### Reusable interface elements (functional catalog)

| Element | Functional role |
|---------|----------------|
| **Brand header** | Product icon + name on auth |
| **Mode card** | Selectable authentication mode (Experiences / Experience Box / Register) |
| **Language selector** | Switch PT / EN / IT |
| **Gradient top bar** | Screen title, back, logout |
| **Primary button (brown)** | Default / Experiences actions |
| **Primary button (blue)** | Experience Box / group actions |
| **Card container** | Grouping content with visual boundary |
| **Section title row** | Icon + bold heading |
| **Small-caps label** | Uppercase section identifier |
| **Star rating row** | 1–5 input with helper text |
| **Filter chip** | Toggle draw filter mode |
| **Intensity dots** | Select level 1–5 for draw filter |
| **Box visual card** | Display box name, type, accent in grid |
| **Experience reveal card** | Flip between intensity cover and description |
| **Experience summary card** | Compact experience info with intensity badge and seal |
| **Wizard progress row** | Five segments + current step pill |
| **Suggestion tile** | Tappable example in wizard step 1 |
| **Snackbar** | Transient error and info messages |
| **Spinner** | Loading indicator (inline, centered, or overlay) |

---

### Session and navigation rules

- **Experiences mode** requires individual login; session carries `groupId` and `boxId` as user progresses
- **Experience Box mode** requires multi-user login; group emerges from credential combination
- **Back navigation** clears scope: experience list → clears box; box selection → clears group
- **Logout** from any authenticated screen returns to authentication
- **Wizard** is a full-screen overlay — does not alter session scope
- **Onboarding/manual** are overlays on bootstrap or auth — do not create sessions

---

## Lacunas e limitações

- **Experience editing** — no edit flow observed; only create, reveal toggle, and delete (own experiences).
- **Box management** — no rename, edit, or delete box observed.
- **Group management** — groups are implicit; no manual group creation or naming UI.
- **Consequence capture** — quick guide references consequences; no input screen found.
- **Notifications** — no push or in-app notification system observed.
- **Offline mode** — no offline-specific UI or cached-state indicators observed.
- **Profile/settings** — no user profile screen beyond language selection; no password change or account settings.
- **Web or desktop** — only mobile interface analyzed; other platforms not validated.
