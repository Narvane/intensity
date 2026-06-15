# Experience and Identity

This document defines Intensity's visual identity, UX guidelines, and communication tone — how the product looks, feels, and speaks to users. It is written for designers, product owners, and anyone shaping user-facing communication.

---

## Short

Intensity presents a **warm, intimate, courageous** brand. Two access modes use distinct accent colors: **brown** for Experiences (individual contribution) and **blue** for Experience Box (group ritual). Intensity levels map to a five-color scale from green (Light) to red (Adrenaline). Typography is clean and readable; cards and seals reinforce trust. Voice is direct, encouraging, and respectful of group consent.

---

## Medium

### Brand essence

| Attribute | Expression |
|-----------|------------|
| **Connection** | Soft gradients, paired imagery in onboarding, language of closeness |
| **Intensity** | Bold color scale, clear level labels, deliberate reveal animation |
| **Discovery** | Playful suggestion chips, themed box types, curiosity in copy |
| **Presence** | Minimal chrome during draw ritual; focus on the card moment |

### Visual system

**Primary palette:**

| Role | Usage |
|------|-------|
| Brown accent | Experiences mode — contribution, reflection |
| Blue accent | Experience Box mode — ritual, togetherness |
| Green → Red scale | Intensity levels 1–5 |
| Teal / Lime / Pink | Effort, Openness, Novelty parameters |
| Amber dashed hint | Pre-reveal alignment reminder |

**Intensity colors:**

| Level | Label | Color |
|-------|-------|-------|
| 1 | Light | Green |
| 2 | Uncomfortable | Blue |
| 3 | Courage | Amber |
| 4 | Bold | Orange |
| 5 | Adrenaline | Red |

### Logo and naming

- **Product name:** Intensity — always capitalized in UI
- **Logo:** Wordmark with subtle intensity gradient; used on splash, onboarding, and auth headers
- **App icon:** Abstract flame or pulse motif suggesting warmth and energy (store assets)

### UX principles

1. **Mode clarity** — color and header immediately signal Experiences vs Experience Box
2. **Progressive disclosure** — intensity before text; invite preview before join
3. **Explicit consent** — confirmations for delete box, leave group, accept invite
4. **Empty states as guidance** — empty box encourages contribution; empty draw pool explains filters
5. **Accessibility baseline** — touch targets ≥44pt; contrast meets WCAG AA for text; screen reader labels on primary actions

### Terminology (canonical)

| UI term | Meaning |
|---------|---------|
| Experience | A concrete idea to do together |
| Box | Thematic collection of experiences |
| Experience Box | Group mode for boxes and draw ritual |
| Group | People who share boxes |
| Intensity | How bold an experience feels (1–5) |
| Draw | Random selection of an experience from a box |
| Reveal | Flip card to see full description |
| Seal | Integrity mark on experience card |
| Invite | Link or code to join a group |
| Proponent | Person who contributed an experience |

Avoid technical terms like "hash" in user copy — use **Seal**.

---

## Detailed

### Onboarding visual narrative

Four illustrated steps tell the emotional story: repetitive routines → longing for connection → unusual moments postponed → Intensity as answer. Illustrations use diverse couples and friend groups; tone is hopeful, not clinical.

### Authentication panels

Three sub-panels within one auth screen:

| Panel | Visual cue | Primary action |
|-------|------------|----------------|
| Experiences login | Brown accent | Single credential form |
| Experience Box login | Blue accent | Multi-credential cards with "+" to add participant |
| Registration | Neutral | Display name, email, password |
| Join via invite | Green accent chip | Code entry field + "Continue" |

Invite entry is reachable from auth without full login — leads to preview screen after code validation.

### Box type presentation

Eleven types appear in a **two-column grid** with:

- Type seal (icon badge)
- Title
- Subtitle hint
- Distinct highlight color per type

Catalog has internal presentation sections (friends, couple, personal, social) but the creation UI shows a **flat list** without section headers.

### Experience cards

**List card (Experiences mode):** intensity badge, parameter dots or compact row, seal, truncated or hidden description depending on authorship.

**Draw card (Experience Box mode):** two-sided card with Y-axis flip animation. Cover: intensity, parameters, seal. Face: full description + reflection + author display name.

### Destructive actions

**Delete box** and **Leave group** use:

- Red or warning accent on confirm button
- Summary of impact (experience count / membership loss)
- Cancel as safe default (secondary button)

**Delete experience** (author only): simpler confirm dialog; no cascade beyond single item.

### Invite sharing sheet

Native share sheet with pre-filled message:

*"Join our group on Intensity — [link]. Or enter code: [CODE]"*

Code displayed in monospace, large, copyable. Expiry shown as human-readable date.

### Tone of voice

| Context | Style |
|---------|-------|
| Onboarding | Warm, narrative, second person |
| Quick guide | Direct rules, imperative verbs |
| Alignment hint | Gentle, amber — "Take a moment together before revealing" |
| Errors | Plain language, actionable recovery |
| Empty states | Encouraging, never blaming |

**Examples:**

- ✓ "Draw again if this one doesn't fit the moment."
- ✓ "Everyone in the room should belong to the same group."
- ✗ "Invalid group_combination_error."

### Localization

Interface supports **English**, **Portuguese (Brazil)**, and **Italian**. Domain terms are translated consistently (see localized docs). Suggestion pack examples follow interface language where localized packs exist; canonical authoring examples remain Portuguese in the embedded catalog.

### What identity deliberately avoids

- Gamification badges or streaks
- Social feed aesthetics
- Corporate enterprise UI patterns
- Aggressive urgency or FOMO copy

## Decisions assumed in this rewrite

- **Invite** UI uses green accent to distinguish from auth modes.
- **Delete box** follows the same destructive confirmation pattern as leave group.
- Filter labels in UI use **Exact** and **Up to** (not internal "fixed/max" naming).
