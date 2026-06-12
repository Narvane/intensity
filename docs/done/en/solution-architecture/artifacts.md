# Artifacts

This document catalogs the structural building blocks of Intensity — the applications, services, and persistence components that compose the solution and how responsibilities are distributed among them.

**Audience:** senior architects and engineers who need to understand what exists structurally in the solution without technology stack or deployment procedure detail.

---

## Short

Intensity is composed of **two application artifacts** and **one persistence artifact**: a **mobile client**, a **resource-oriented API**, and a **database**. The **client holds the core** of the product — interface, flows, and game behavior. The **API centralizes data** as the single source of truth. The **database** stores the domain model and is accessed exclusively by the API.

---

## Medium

### Artifact inventory

| Artifact | Type | Responsibility |
|----------|------|----------------|
| **Client** | Mobile application | Product nucleus: interface, user flows, draw mechanic, client-side logic |
| **API** | Server application | Resource-oriented data access; persistence orchestration |
| **Database** | Persistence store | Domain data storage; source of truth backing the API |

### Responsibility split

```
┌──────────────────────────────────────────────┐
│                  CLIENT                       │
│  • Interface and experience structure         │
│  • Navigation and screen orchestration        │
│  • Draw, filter, and reveal behavior          │
│  • Creation wizard and suggestion packs       │
│  • Local preferences (e.g. UI language)       │
│  • API consumption for persisted operations   │
└──────────────────────┬───────────────────────┘
                       │ REST (resources)
┌──────────────────────▼───────────────────────┐
│                    API                        │
│  • Resource endpoints for domain entities     │
│  • Authentication and authorization           │
│  • Data validation at persistence boundary    │
│  • Single access point to the database        │
└──────────────────────┬───────────────────────┘
                       │
┌──────────────────────▼───────────────────────┐
│                 DATABASE                      │
│  • Participants, groups, boxes, experiences   │
│  • Source of truth for all persisted data     │
└──────────────────────────────────────────────┘
```

### Client as product nucleus

The client is not a thin presentation layer. It carries the structural value of the product:

- The guided interface that helps players use creativity and create moments
- The creation wizard and pre-defined suggestions that function as an implicit tutorial
- The shared-moment ritual (intensity filters, alignment hint, flip-card reveal)
- The draw mechanic itself

The application's business rules are intentionally simple — essentially a random draw over collected experiences. Complexity and differentiation live in how the interface structures the experience, not in server-side orchestration.

### API as data centralizer

The API is a **resource API**, not a Backend-for-Frontend (BFF). It exposes domain resources for create, read, update, and delete operations. It does not aggregate, reshape, or tailor responses to specific screens.

This role is indispensable for:

- **Centralizing data** so all client instances share the same persisted state
- **Registering experiences individually** for each participant, each from their own device
- **Maintaining groups, boxes, and experiences** as a coherent domain across devices

### Database as exclusive persistence

The database holds everything in the functional domain model: participants, groups, boxes, and experiences. No client stores domain data as a source of truth. Clients may cache or hold transient state (such as draw results), but persistence always flows through the API to the database.

---

## Detailed

### Mobile client

The mobile client is the primary artifact of the solution. Participants install it on their phones. It is the only artifact that scales to **many instances** — one per device.

**Owns:**

| Area | What the client handles |
|------|-------------------------|
| **Presentation** | All screens, overlays, loading/empty/error states |
| **Interaction flows** | Bootstrap, onboarding, authentication, Experiences path, Experience Box path |
| **Game behavior** | Random draw with intensity filters; transient draw results |
| **Creation experience** | Five-step wizard, suggestion packs, parameter rating |
| **Session context** | Active access mode, selected group, selected box (operational scope) |
| **Local state** | UI language preference; first-run onboarding flag |

**Delegates to the API:**

| Area | What the client requests from the API |
|------|---------------------------------------|
| **Authentication** | Credential validation against persisted participants |
| **Registration** | New participant creation |
| **Experience CRUD** | Create, list, delete experiences in a box |
| **Box management** | List and create boxes for a group |
| **Group resolution** | Resolve the group formed by authenticated participants |

The client does not implement a BFF pattern. Screen-specific data shaping happens on the client side after receiving resource representations from the API.

### API

The API is a single server-side application artifact. It runs in one environment and serves all client instances.

**Characteristics:**

- **Resource-oriented:** endpoints map to domain resources (participants, groups, boxes, experiences), not to UI screens or composite views
- **Stateless at the application layer:** session and navigation context live on the client; the API handles requests independently
- **Exclusive database gateway:** no client connects to the database directly

**Does not own:**

- Screen layout or navigation decisions
- Draw logic or reveal ritual behavior
- Suggestion pack content or wizard step orchestration
- UI language or onboarding state

### Database

The database is a persistence artifact connected to the API within the server environment. It is not a separately deployed application artifact with its own business logic, but it is a distinct structural component of the architecture.

**Stores:**

- Participant records (display name, email, credentials)
- Group associations derived from Experience Box authentication
- Boxes (name, type, owning group)
- Experiences (description, intensity, parameters, reflection, author, timestamps, integrity seal)

**Does not store:**

- Draw results (transient, client-only)
- UI language preference
- Onboarding completion state
- Suggestion pack texts (embedded in the client)

### Artifact relationships

| From | To | Relationship |
|------|----|--------------|
| Client | API | Consumes REST resources; many-to-one |
| API | Database | Reads and writes; one-to-one within server environment |
| Client | Database | No direct connection |

### What is not a separate artifact

The following are part of the client or API, not standalone artifacts:

- **Suggestion packs** — embedded content within the client
- **Onboarding and quick guide** — client-only flows
- **Draw engine** — client-side behavior, not a server service
- **Message brokers or event buses** — not present in the current architecture
