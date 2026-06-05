# Overview — What Is It?

Product conception document for **Intensity**. Content derived from observable application behavior, user-facing text, and the functional structure of the repository.

---

## Pitch

### Short

**Intensity** helps friends and couples collect unusual experience ideas, classify them by intensity, and draw one to live meaningful moments of connection — instead of postponing the unexpected.

### Medium

Tired of repetitive experiences that barely bring you closer to the people who matter? **Intensity** is a mobile app for groups and couples: each person registers ideas in themed boxes, rates them from 1 to 5 in intensity, and when together they draw one experience to live in the moment. The product pushes you to act instead of waiting for chance to create memorable moments.

### Detailed

**Intensity** turns idea collection and drawing into a shared ritual. Invited participants create accounts, form groups, and feed **experience boxes** over time — each idea goes through suggestion, reflection, parametrization (effort, openness, novelty), and final intensity classification. At an in-person gathering, the app draws one experience from the box, with intensity-level filters. The product essence is **connection, intensity, and discovery**: it is not about completing tasks, but about living meaningful moments with presence. The observable ecosystem includes a mobile app (Android, with iOS target) and a dedicated API (**Intensity API**), with support for Portuguese, English, and Italian.

---

## App Store / Google Play Description

### Short

Collect unusual ideas. Draw one. And live memorable moments with the people who matter.

### Medium

**Intensity** — connection, intensity, and discovery.

Create boxes with friends or as a couple, add experiences over time, rate from 1 to 5, and draw when you are together. Intensity filters, suggestions by box type, and an integrated quick guide. Available in Portuguese, English, and Italian.

### Detailed

Missing that sense of closeness? The most memorable moments were usually the most unexpected ones — and yet they almost always get postponed. **Intensity** changes that.

**How it works**

- Sign up (invite-only access) and enter solo to register experiences or as a group to open the **Experience Box**.
- Choose or create themed boxes: outings with friends, couple trips, connection moments, and other categories available in the app.
- Register ideas with a five-step assistant: suggestion, reflection, effort/openness/novelty stars, and final intensity (from *Light* to *Adrenaline*).
- At the gathering, draw one experience — any level, exact intensity, or up to a maximum level.
- Before revealing, align mood, limits, and commitment; the quick guide covers consequences and gradual intensity progression.

Ideal for couples and friend groups who want to break routine with intention. Developed by **Narvane**.

---

## Executive Summary

### Short

**Intensity** is a mobile product with API by Narvane for groups and couples to register, classify, and draw shared experiences, promoting connection through collectively planned unusual moments.

### Medium

The repository contains two main applications: **intensity-api** (Spring Boot, PostgreSQL, JWT authentication) and a **Kotlin Multiplatform** client (Compose, Android and iOS target). The central domain revolves around **groups**, **typed boxes**, and **experiences** with an intensity scale from 1 to 5. Two modes structure usage: **Experiences** (individual registration, CURATE mode) and **Experience Box** (collective session with drawing, CONNECT mode). Registration is closed via an authorized email list. Interface languages: PT, EN, and IT.

### Detailed

**Problem addressed (evidence: onboarding and quick guide copy):** repetitive experiences, emotional distance between close people, and postponement of distinctive moments that could bring them closer.

**Observable solution:** collaborative boxes per group, ritualized experience drawing, multidimensional parametrization (intensity, effort, openness, novelty), and a reflection flow when creating each idea.

**Functional architecture (product, not implementation):** participants → groups (combination of people who connected together) → boxes (thematic type) → experiences (content protected on the server).

**Main flows:** onboarding and quick guide → authentication → individual mode (select group → box → register/edit experiences) or group mode (boxes → draw with intensity filters).

**Observable state:** app version `1.0.0`, invite-only access, no web client in this repository. Social rules suggested in the quick guide (consequences, swaps between levels) appear only as textual guidance — they were not found as implemented functionality in the API or database.

---

## Evidence and Limitations

| Topic | Status |
|-------|--------|
| Brand name in UI | **Intensity** (`app.brand`, Android label) |
| Product essence | *Connection, intensity, and discovery* (quick guide) |
| Problem and value proposition | Four-step onboarding (`PtDictionary.kt` and EN/IT equivalents) |
| Inferable target audience | Friend groups and couples (box types and quick guide copy) |
| Single official tagline | **Not found** — convergent fragments exist in onboarding |
| Name "Intensity Box" | Appears in client README; UI uses **Intensity** |
| Business model / pricing | **Not found** in the repository |
| Consequences and level swaps | Guidance in quick guide; **no evidence of implementation** in software |
| Web client | **Not present** in this repository |

**Primary sources:** UI dictionaries (`PtDictionary.kt`, `EnDictionary.kt`, `ItDictionary.kt`), `IntensityApp.kt`, `openapi.yaml`, database migrations (`V221__intensity2_init.sql`), `ExperienceBoxTypeCodes.kt`.
