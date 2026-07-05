# Integrations and Communication

This document describes how Intensity's components communicate — protocols, data flows, contracts, and dependency directions. It is written for architects and senior engineers integrating or extending the system.

---

## Short

The mobile client talks to the API via **REST over HTTPS** (request/response, client-initiated). The API talks to PostgreSQL via **ORM persistence**. There is **no server push**, **no WebSockets**, and **no direct client-to-database path**. Invites use HTTPS links resolved by the mobile OS into the app. Consistency is **eventual** — clients refresh on read.

---

## Medium

### Integration map

```
Mobile client ──REST (HTTPS)──► API ──JPA/Hibernate──► PostgreSQL
     │                              │
     └── no direct DB               └── sole persistence gateway
```

| Integration | Protocol | Direction |
|-------------|----------|-----------|
| Client → API | REST JSON | Client initiates |
| API → DB | SQL via ORM | API only |
| Client → OS share sheet | Native bridge | Outbound invite sharing |
| Deep link → Client | App/Universal Links | Inbound invite open |

### Sync model

**Pull-based eventual consistency.** When a participant adds an experience from their phone, other clients see it on the next API read. The shared-phone ritual fetches the experience pool from the API immediately before drawing.

No live notifications when data changes. No multi-device sync during draw — one phone holds draw state locally.

### Key flows

**Authentication**

```
Client POST /auth/login { email, password }
  ← { token, participantId, displayName }
Client stores token locally for subsequent requests
```

**Joint login (Experience Box)**

```
Client POST /auth/group { credentials[] }
  ← { token(s), groupId, members[] }
  OR 409 if credentials span incompatible groups
```

**Invite lifecycle**

```
POST /groups/{id}/invites        → { code, linkToken, expiresAt }
GET  /invites/validate?code=      → { groupPreview, expiresAt, status }
POST /invites/{id}/accept       → { groupId, membership confirmed }
DELETE /invites/{id}             → revoked
```

**Experience registration (Experiences mode)**

```
Client collects assistant input locally
POST /boxes/{id}/experiences { description, intensity, params, type, reflection? }
  ← experience persisted with seal
Branching: POST /boxes/{id}/experiences/batch { experiences: [...] } (up to 5)
  ← list of persisted experiences with seals
```

**Box deletion (Experience Box mode)**

```
DELETE /boxes/{id}
  ← 204; cascade removes experiences server-side
Client refreshes GET /groups/{id}/boxes
```

**Draw ritual (no API write)**

```
GET /boxes/{id}/experiences → pool
Client filters, randomizes, reveals locally
(no POST for draw result)
```

### Error contract

REST errors return `{ code, message }` with appropriate HTTP status. Client maps to user-facing copy. Critical cases:

| Status | Scenario |
|--------|----------|
| 401 | Invalid or expired token |
| 403 | Not a group member |
| 404 | Box, group, or invite not found |
| 409 | Group membership conflict on joint login |
| 410 | Invite expired or revoked |
| 422 | Validation failure (name length, intensity range) |

---

## Detailed

### REST resource outline

| Resource | Operations |
|----------|------------|
| `/auth/login` | POST single participant |
| `/auth/group` | POST multi participant joint session |
| `/participants` | POST register |
| `/groups` | GET list for participant; POST implicit via auth |
| `/groups/{id}/members` | GET; DELETE self (leave) |
| `/groups/{id}/invites` | POST create; GET list active |
| `/invites/validate` | GET by code or token |
| `/invites/{id}/accept` | POST |
| `/invites/{id}` | DELETE revoke |
| `/groups/{id}/boxes` | GET list |
| `/boxes` | POST create |
| `/boxes/{id}` | DELETE (cascade) |
| `/boxes/{id}/experiences` | GET list; POST create |
| `/experiences/{id}` | PUT update; DELETE (author only) |

Version prefix `/v1` implied; breaking changes require `/v2` per technical decisions.

### Invite link contract

Deep link format (illustrative):

```
https://app.intensity.example/join?t={linkToken}
```

Mobile OS routes to installed app → client calls `GET /invites/validate?t=` → preview screen.

Code path: user enters `AB12CD` → `GET /invites/validate?code=AB12CD`.

Both channels resolve the same invite record.

### Security on the wire

- TLS everywhere in production
- Bearer token on authenticated requests
- Tokens stored in secure client storage (Capacitor Preferences or platform keystore wrapper)
- No credentials in invite links — token is opaque, single-purpose

### Explicitly absent integrations

Payment gateways, analytics SDKs, push notification services (FCM/APNs), external IdP (OAuth), CDN asset pipeline, message queues, webhooks from client.

### Operational webhook (engineering layer)

Production API deploy uses inbound webhook from CI — documented in engineering layer, not a product integration.

## Decisions assumed in this rewrite

- Invite validation is a **read-only GET** before accept POST.
- Joint login returns **409** when credentials belong to different existing groups.
- Box delete is **synchronous REST** with server-side cascade.
