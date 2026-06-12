# Integrations and Communication

This document describes how the structural components of Intensity communicate — the protocols, interaction patterns, and data flow boundaries between client, API, and database.

**Audience:** senior architects and engineers who need to understand integration topology without implementation contracts or technology stack detail.

---

## Short

The mobile **client** communicates with the **API** exclusively via **REST**. The API communicates with the **database** through its persistence layer. There is **no messaging**, **no WebSockets**, and **no direct client-to-database connection**. The API exposes a **resource-oriented** interface — not screen-tailored aggregates.

---

## Medium

### Communication map

```
Client  ──REST──►  API  ──persistence──►  Database
  │                  │
  │                  └── single gateway to persisted data
  │
  └── no direct path to database
```

### Protocols and patterns

| Integration | Protocol / pattern | Direction |
|-------------|-------------------|-----------|
| Client → API | REST (HTTP) | Request/response |
| API → Database | Persistence access | Read/write |
| Client → Database | — | Not permitted |
| Client ↔ Client | — | No peer communication |
| API ↔ External services | — | None in current architecture |

### REST as the sole client–server channel

All persisted operations flow through REST calls from the client to the API:

- Authentication and registration
- Listing and creating boxes
- Creating, listing, and deleting experiences
- Resolving groups from multi-participant login

The client initiates every interaction. The API responds with resource representations. There is no server-initiated push to clients.

### Resource API contract style

The API follows a **resource-oriented** model:

- Endpoints represent domain entities (participants, boxes, experiences), not UI workflows
- Responses carry resource data, not screen-specific view models
- The client is responsible for mapping resources to interface needs

This differs from a BFF (Backend-for-Frontend), which would expose endpoints shaped per screen or per client platform. Intensity deliberately avoids that pattern.

### What is explicitly absent

| Mechanism | Status |
|-----------|--------|
| Message queues / event buses | Not used |
| WebSockets / server push | Not used |
| GraphQL | Not used |
| gRPC | Not used |
| Client-to-client sync | Not used |
| Third-party integrations | Not used |

The absence of messaging and real-time channels reflects the application's simplicity: there is no need for live multi-device synchronization during the draw ritual (which happens on one shared phone), nor for complex server-side event orchestration.

---

## Detailed

### Client-to-API interaction model

The client operates as a **consumer** of API resources. Typical interaction cycles:

**Authentication flow:**
1. Client sends credentials to the API
2. API validates against persisted participant data
3. API returns authentication result
4. Client establishes session context locally

**Experience registration (Experiences mode):**
1. Client collects wizard input locally
2. Client sends experience resource to the API
3. API persists to database
4. API returns created experience representation
5. Client updates the experience list view

**Shared moment (Experience Box mode):**
1. Client requests experiences for the active box via API
2. API returns experience resources from database
3. Client performs draw, filter, and reveal locally
4. Draw result remains on the client — no write back to API

This last pattern is significant: the draw is entirely client-side. The API is not involved in the ritual moment beyond supplying the experience pool.

### Request characteristics

| Property | Behavior |
|----------|----------|
| Initiator | Always the client |
| Coupling | Loose — resource representations, not screen contracts |
| State | Session and navigation context held on client |
| Failure handling | Client surfaces errors (e.g. snackbar on API failure) |
| Idempotency | Not architecturally mandated at this layer |

### API-to-database interaction

The API is the **sole application** that accesses the database. This boundary ensures:

- Consistent validation before persistence
- A single source of truth
- No data fragmentation across client instances

The API translates REST operations into persistence actions. The specific ORM, query layer, or connection pooling belongs to the Engineering layer.

### Data flow by operation

| Operation | Client role | API role | Database role |
|-----------|-------------|----------|---------------|
| Register participant | Sends registration payload | Validates and persists | Stores participant |
| Login (Experiences) | Sends credentials | Validates | Reads participant |
| Login (Experience Box, multi-user) | Sends multiple credentials | Resolves group | Reads participants and group |
| Create box | Sends box resource | Persists with group association | Stores box |
| List boxes | Requests by group | Queries and returns | Reads boxes |
| Create experience | Sends experience resource | Persists with author and box | Stores experience |
| List experiences | Requests by box | Queries and returns | Reads experiences |
| Delete experience | Sends delete request | Removes if authorized | Deletes experience |
| Draw experience | Filters and selects locally | — (no call) | — |
| Change UI language | Stores locally | — (no call) | — |

### Synchronization model

There is no real-time synchronization between clients. Consistency is **eventual through REST**:

- When a participant registers an experience from their phone, other clients see it on the next API read
- During the shared-moment ritual on one phone, the client refreshes the experience pool from the API before drawing

No push notification or live update mechanism notifies clients when data changes. Each client pulls current state when needed.

### External integrations

The current architecture has no integrations with external services — no payment gateways, analytics pipelines, identity providers, or content delivery networks at the architectural level. If added in the future, they would integrate through the API or client as new communication paths documented separately.

### Boundaries for lower layers

This document describes *what* communicates and *how* at the structural level. It does not specify:

- HTTP status codes, headers, or payload schemas
- Authentication token format or expiration
- Database connection configuration
- Retry policies or circuit breakers

Those details belong to the Engineering and Operations layer.
