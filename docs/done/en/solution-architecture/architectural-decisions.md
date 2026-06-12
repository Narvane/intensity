# Architectural Decisions

This document records the key structural choices that shape Intensity's architecture — their rationale, trade-offs, known constraints, and acknowledged risks.

**Audience:** senior architects and engineers who need to understand *why* the solution is organized as it is, without operational implementation detail.

---

## Short

Intensity places the **product nucleus on the mobile client** and uses a **centralized resource API** for data persistence — deliberately **not** a BFF. The architecture favors **simplicity**: REST only, no messaging or WebSockets, single API instance, many client instances. The trade-off is **custody of user data** on the server. An **offline mode** is identified as a future direction for greater privacy.

---

## Medium

### Decision summary

| Decision | Choice | Primary reason |
|----------|--------|----------------|
| Product nucleus | Mobile client | The value is in the interface and guided experience, not server-side business logic |
| API style | Resource API | Domain data centralization without screen-specific coupling |
| Not a BFF | Rejected | Business rules are simple; interface structure is the differentiator |
| Client–server protocol | REST only | Sufficient for the application's interaction patterns |
| Real-time mechanisms | Not used | No need for live sync, push, or event orchestration |
| API topology | Single instance | Adequate for current scale; simplicity over distribution |
| Data custody | Centralized server | Required for per-participant experience registration |
| Offline operation | Not in current scope | Future consideration for privacy |

### Why the core lives on the client

Intensity is a game-like experience built around a simple mechanic: collect experiences, then draw one at random. The business rules are not complex — there are no intricate server-side rule engines or multi-step server orchestrations.

The product's value lies in:

- How the interface structures the player's journey
- The creation wizard that guides reflection and parameterization
- The shared-moment ritual that turns a random draw into a deliberate reveal
- Pre-defined suggestions that function as an implicit tutorial

Because the differentiation is experiential rather than computational, the client carries the core. The server does not need to own presentation logic, flow orchestration, or draw behavior.

### Why a resource API — not a BFF

A BFF would tailor API responses to specific screens or client needs. Intensity rejects this because:

- Screens already orchestrate their own flows on the client
- Domain resources map cleanly to CRUD operations without aggregation
- Adding a BFF layer would introduce coupling between server and UI structure without proportional benefit
- The draw mechanic and reveal ritual never touch the server

The API's job is narrower and well-defined: **be the source of truth for persisted data**.

### Why data is centralized

A centralized API is **indispensable** for the product's social model:

- Each participant registers experiences **individually from their own device** (Experiences mode)
- Those experiences must appear in the **same box** when the group plays together (Experience Box mode)
- Groups, boxes, and experiences must be **consistent across all client instances**

Without centralized persistence, per-participant contribution would not converge into a shared pool.

### Trade-offs accepted

| Benefit | Cost |
|---------|------|
| Shared experience pool across devices | Server holds participant credentials and personal experience data |
| Simple architecture, fast to build and maintain | No offline play; network required for persisted operations |
| Client autonomy over UX and game behavior | Client must handle resource-to-screen mapping |
| Single API instance simplicity | Scaling requires future architectural evolution |
| REST simplicity | No real-time updates; clients pull state on demand |

### Known constraints

- **Network dependency:** authentication, registration, and experience management require API availability
- **Data responsibility:** operating a centralized API means responsibility for protecting participant data
- **Single-device ritual:** the draw happens on one shared phone; the architecture does not require multi-device real-time sync during play
- **No offline mode:** all domain reads and writes go through the API in the current architecture

---

## Detailed

### AD-01: Client as product nucleus

**Context:** The product is a mobile game-like experience centered on a draw mechanic over user-created content.

**Decision:** The mobile client owns interface structure, navigation, draw behavior, creation flows, and the shared-moment ritual. The server does not orchestrate user journeys.

**Rationale:**
- The application's rules are essentially a randomizer over a collected set — not a complex domain engine
- The interface guides players to use creativity and create moments; that guidance is the product's value
- Keeping the nucleus on the client allows the experience to evolve without server redeployment for UX changes

**Consequences:**
- Client releases carry product changes; API changes are driven by data model needs
- The API remains thin — validation and persistence, not business orchestration
- Testing focus shifts toward client behavior and flows

**Alternatives considered:**
- **Server-driven flows:** rejected — adds latency and coupling without benefit for a simple draw mechanic
- **BFF per platform:** rejected — only one client platform exists (mobile); no aggregation need

---

### AD-02: Resource API instead of BFF

**Context:** The client needs persisted data. The API could expose either domain resources or screen-specific aggregates.

**Decision:** The API exposes resource-oriented endpoints mapped to domain entities.

**Rationale:**
- Resources align with the functional data model (participants, groups, boxes, experiences)
- CRUD maps directly without server-side view composition
- The client already knows how to shape data for each screen
- A BFF would duplicate presentation knowledge on the server

**Consequences:**
- The client performs any needed aggregation or filtering locally (e.g. intensity filters during draw)
- API versioning is tied to the domain model, not to UI iterations
- New screens can be added without API changes if they consume existing resources

**Alternatives considered:**
- **BFF (Backend-for-Frontend):** rejected — the business does not justify the added layer; interface is the value, not server-side composition
- **GraphQL:** rejected — resource CRUD via REST is sufficient; no complex query requirements

---

### AD-03: Centralized data with single API instance

**Context:** Multiple clients (phones) must share persisted state. Each participant contributes experiences individually.

**Decision:** One API instance in one server environment serves all clients. The database behind it is the single source of truth.

**Rationale:**
- Per-participant experience registration requires a shared persistence layer
- A single instance is adequate for the expected scale of a social game among couples and friend groups
- Simplicity reduces operational and developmental overhead

**Consequences:**
- All clients depend on API availability for persisted operations
- The operator assumes responsibility for user data protection
- Scaling beyond a single instance is a future concern, not a current requirement

**Alternatives considered:**
- **Local-only / peer-to-peer sync:** rejected — would prevent individual contribution from separate devices converging into one box
- **Multi-instance API with load balancing:** not required at current scale; adds complexity without immediate benefit

---

### AD-04: REST only — no messaging or WebSockets

**Context:** The client and API need to communicate. Multiple protocols and patterns are available.

**Decision:** REST (HTTP request/response) is the sole client–server communication mechanism. No message queues, event buses, or WebSocket channels.

**Rationale:**
- Persisted operations are discrete CRUD events, not continuous streams
- The draw ritual runs on one shared phone — no need for live multi-device synchronization
- No server-initiated notifications are required in the current product
- Simplicity aligns with the application's scope

**Consequences:**
- Clients pull fresh data when needed; no push-based updates
- API failures are handled per-request on the client (e.g. error snackbar)
- Adding real-time features later would require revisiting this decision

**Alternatives considered:**
- **WebSockets for live updates:** rejected — no product scenario requires server push during play
- **Message queue for async processing:** rejected — no background processing or event-driven workflows in the current domain

---

### AD-05: Simplicity over architectural complexity

**Context:** Intensity is a focused product with a narrow domain and a simple core mechanic.

**Decision:** The architecture consists of exactly two application artifacts (client + API) and one persistence artifact (database), connected by REST.

**Rationale:**
- The product is not an enterprise platform with complex integration needs
- Over-engineering would slow development without improving the player experience
- The problem being solved is experiential, not computational

**Consequences:**
- Fewer moving parts to deploy, monitor, and maintain
- Clear responsibility boundaries between client and API
- Some future needs (offline, scaling, analytics) will require explicit architectural evolution

---

### AD-06: Data custody trade-off (accepted risk)

**Context:** Centralizing data enables the social contribution model but places participant information on the server.

**Decision:** Accept server-side data custody as a necessary trade-off for centralized persistence.

**Rationale:**
- Individual experience registration from separate devices requires a shared store
- Participant credentials and experience content must persist somewhere accessible to all clients
- The product cannot fulfill its core loop without this centralization

**Consequences:**
- The operator must handle data responsibly — protection, access control, and compliance considerations
- Participants trust the service with their credentials and creative content
- Privacy-sensitive players may prefer alternatives

**Mitigation direction (future, not current scope):**
- An **offline mode** has been identified as a future possibility to offer greater privacy to players
- This would be a significant architectural evolution affecting client, API, and sync model
- Not part of the current architecture

---

### Risks and evolution paths

| Risk | Current state | Possible evolution |
|------|---------------|-------------------|
| API unavailability blocks persisted operations | Accepted | Offline mode with local storage and sync |
| Single API instance limits scale | Accepted | Horizontal scaling behind load balancer |
| No real-time data refresh between clients | Accepted | Push notifications or polling optimization |
| Centralized data privacy concerns | Acknowledged | Offline mode; data minimization policies |
| Server data breach impact | Acknowledged | Security practices in Engineering layer |

### What these decisions do not cover

Technology choices (programming languages, frameworks, database engine), deployment procedures, CI/CD, monitoring, and security implementation belong to the **Engineering and Operations** layer. This document addresses structural organization and its rationale only.
