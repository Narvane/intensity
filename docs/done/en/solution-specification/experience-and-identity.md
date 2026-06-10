# Experience and Identity

This document describes the visual identity, interaction patterns, and communication language of Intensity — how the product presents itself, how users perceive its interface, and which UX conventions govern the experience. It specifies *how the solution feels and communicates*, without implementation detail.

**Audience:** analysts, product owners, designers, and functional QA — people who need to understand or reproduce the product experience without knowing how it was built.

---

## Curta

Intensity presents itself as **Intensity**, with a **box icon** and a **warm, card-based** visual language. The interface follows the device's **light or dark theme** and uses **brown** for the **Experiences** mode (individual contribution) and **blue** for the **Experience Box** mode (group ritual). **Eleven box types** each carry a distinct accent palette. Communication is available in **Portuguese, English, and Italian**, with an emotionally warm, group-centric tone. Core UX principles include **progressive disclosure** (intensity before full text), **group alignment before reveal**, first-run **onboarding** and a reusable **quick guide**, and **snackbar feedback** for errors.

---

## Média

### Brand and visual identity

| Element | Observable behavior |
|---------|---------------------|
| **Product name** | "Intensity" — shown in authentication header, top bars, and brand areas |
| **Logo** | Box/inventory glyph icon paired with the product name; no custom raster logo observed |
| **Auth header** | Full-width blue gradient bar with white icon and name |
| **Surfaces** | Warm off-white backgrounds with subtle vertical gradient; white cards with soft warm borders |
| **Primary palette (Experiences mode)** | Warm brown as primary action color |
| **Participant palette (Experience Box mode)** | Blue gradient for group actions and participant role |
| **Intensity levels 1–5** | Green → blue → amber → orange → red, each with a matching surface tint |
| **Parameters** | Effort (teal), openness (lime green), novelty (rose) — each with icon and surface color |
| **Rating stars** | Amber/gold |
| **Shapes** | Rounded corners throughout — from small chips to large cards |
| **Typography** | Bold titles (22/18 sp), body text 16/14 sp; system default font |

The interface adapts to **system light/dark preference**. In dark mode, backgrounds shift to deep warm browns; primary brown lightens; text becomes warm off-white.

### Theming by context

Visual accent changes depending on operational context:

| Context | Visual cue |
|---------|------------|
| **Experiences mode** | Brown primary buttons, brown selectable auth cards |
| **Experience Box mode** | Blue primary buttons, blue selectable auth cards, blue status bar on launch |
| **Active box type** | Top bar gradient and accents follow the selected box type's palette (11 distinct themes) |
| **Wizard progress** | Five-segment bar with amber step indicator |

### Recurring visual patterns

- **Cards** — white/surface containers with 16 dp radius, warm border, light elevation
- **Gradient top bar** — 62 dp height, gradient fill, white title and actions
- **Primary buttons** — full-width, pill-shaped, 52 dp height; brown or blue variant by role
- **Section headers** — icon (26 dp) + bold title; small-caps labels with letter spacing
- **Filter chips** — pill shape; blue when selected
- **Flip card** — experience card rotates on Y-axis to reveal description after alignment
- **Box grid cards** — two-column grid, 20 dp radius, type badge with icon
- **Intensity dots** — circular 1–5 selectors for draw filters
- **Star ratings** — interactive 1–5 with per-level helper text below

Icons are standard material-style glyphs throughout (groups, favorite, inventory/box, star, bolt, lightbulb, flip, etc.).

### UX conventions

1. **Dual-role color coding** — brown signals individual contribution (Experiences); blue signals group presence (Experience Box).
2. **Progressive disclosure** — experience description hidden until explicit reveal (eye action in list, card flip in shared moment).
3. **Group alignment before reveal** — dashed amber hint urging agreement on mood, limits, and commitment before flipping the card.
4. **First-run education** — four-step illustrated onboarding, skippable; optional quick guide with product rules.
5. **Help always reachable** — quick guide and onboarding reopenable from authentication toolbar.
6. **Transient error feedback** — errors shown via snackbar messages, not persistent inline banners.
7. **Loading indicators** — spinner for bootstrap, list loads, and draw actions; semi-transparent overlay during wizard submission.
8. **Dedicated empty states** — textual empty messages inside cards or sections, without illustrations.
9. **Session-scoped curator list** — Experiences list shows only the current user's contributions in the active session.
10. **Intensity semantics** — each level (1–5) carries a subtitle and color; parameters show contextual help and per-star descriptions.

### Communication language and tone

**Supported languages:** Portuguese (default), English, Italian — selectable via flag control, available on onboarding and authentication screens.

**Product vocabulary** (as shown to users):

| Term (EN) | Role |
|-----------|------|
| Experience | A concrete idea to do together |
| Experience Box | Group ritual mode — boxes, draw, reveal |
| Box | Thematic container for collected experiences |
| Intensity | Overall boldness level 1–5 |
| Draw | Random selection from a box |
| Reveal | Deliberate moment to see full description |
| Seal | Integrity fingerprint on experience cards |
| Effort / Openness / Novelty | Three parameter ratings in the creation wizard |

**Intensity level subtitles:** Light → Uncomfortable → Courage → Bold → Adrenaline.

**Tone characteristics:**
- Conversational and emotionally warm ("Tired of repetitive experiences?", "live memorable moments")
- Group-centric ("align as a group before revealing", "everyone feeds the box")
- Instructional in the quick guide — core rule, recommended flow, intensity tips, consequence tips, product essence
- Plain error prefixes ("Error", "Login failed") with specific validation messages in the wizard

Onboarding narrates a four-step story: problem (repetitive experiences) → insight (memorable = unexpected) → call to action → product mechanics (collect, draw, live).

---

## Detalhada

### Color system in depth

The visual system layers three color families:

**Base theme (light):**
- Background: warm off-white `#FCFAF7`
- Surface containers: `#F6EFE6` / `#EFE7DB`
- Primary brown: `#B0946F`
- Body text: `#1D1B20`; variant `#49454F`
- Error: `#B3261E`

**Base theme (dark):**
- Background: `#15110E`; surface `#1C1713`
- Primary: `#D4BC9A`; on-surface `#ECE3DC`

**Role and semantic tokens:**
- Participant blue: `#1E5EFF` → `#4C7CFF` gradient; surface `#E8F1FF`
- Parameter effort: teal `#00A3B4` / surface `#DCFBFF`
- Parameter openness: lime `#84CC16` / surface `#F7FEE7`
- Parameter novelty: rose `#E11D48` / surface `#FFE4E6`
- Intensity 1: green `#2E7D32`; 2: blue `#0277BD`; 3: amber `#F9A825`; 4: orange `#EF6C00`; 5: red `#C62828` — each with matching surface tint
- Card border: warm brown at low opacity
- Rating star: `#F9A825`

Each of the **eleven box types** carries its own accent, surface, and top-bar gradient — visible when browsing boxes and when a box is active in the experiences list or shared moment.

### Typography and spacing

Titles use bold weight at 22 sp (large) and 18 sp (medium). Body runs at 16 sp with 22 sp line height; secondary body at 14 sp. Labels for sections use bold 14 sp. Small-caps section labels are uppercase with letter spacing.

Common spacing rhythm: 16–18 dp screen padding; 12–14 dp gaps between cards; top bar horizontal padding 2–16 dp.

Corner radii scale from 9 dp (extra small) to 24 dp (extra large), giving a soft, approachable feel.

### Screen backdrop

Most screens use a vertical gradient from warm surface color to white (light) or from mid-dark to deep background (dark), establishing visual continuity across the flow.

### Onboarding and quick guide

**Onboarding** — four illustrated steps with dot indicators, Back/Next navigation, "Start" to finish, and "Open quick guide" shortcut. Language selector available. Can be reopened from authentication.

**Quick guide** — five content sections:
1. **Core rule** — collect over time, take the draw seriously, rate 1–5, choose one experience, define a consequence, decide before revealing
2. **Recommended flow** — everyone feeds the box; trigger draw when together; align before revealing
3. **Tips — Intensity** — start low, increase gradually, allow swaps, use filters
4. **Tips — Consequence** — set first, real cost, scale on back-out, vary with swaps
5. **Intensity essence** — connection, intensity, discovery; living meaningful moments with presence

Closing tip: "If in doubt, align as a group before revealing."

### Authentication visual modes

Three selectable mode cards on the authentication screen:

| Mode | Color when selected | Subtitle (EN) |
|------|---------------------|---------------|
| **Experiences** | Brown | Register experiences and choose a box |
| **Experience Box** | Blue | Group enters together; shared group |
| **Register** | Brown | Create a new account |

The Experiences pane shows email/password login. The Experience Box pane allows adding one or more user credentials before login. Register pane collects display name, email, and password.

### Parameter and rating presentation

In the creation wizard, each parameter (effort, openness, novelty) displays:
- A dedicated icon and color
- A help sentence explaining what the parameter measures
- A 1–5 star row with tap-to-rate prompt
- A dynamic per-level description that updates as stars change

Intensity classification shows the five level subtitles with matching colors and allows manual adjustment of the auto-suggested level.

### Draw and reveal presentation

In the shared moment, before drawing:
- Filter chips: **Any**, **Exact**, **Up to** — with optional intensity dot selector
- Hint card encouraging activation when the box is empty

After drawing:
- Card shows intensity cover first (level, parameters, seal)
- Amber dashed hint urges group alignment before reveal
- Flip action reveals full description
- "Back to draw" returns to filter selection

### Navigation identity

Navigation is linear and state-driven — no tab bar or persistent sidebar. Back actions use top-bar icons that reset session scope (box → group → auth). Logout is always available from authenticated top bars. Overlays (onboarding, manual, wizard) stack on the current screen without replacing the navigation stack.

### Accessibility observations

Contrast follows theme defaults. Intensity and parameters use color **and** text labels (subtitles, helper descriptions). Focus and keyboard navigation could not be fully validated from static analysis. Screen reader labels were not exhaustively audited.
