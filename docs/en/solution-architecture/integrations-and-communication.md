# Integrations and Communication

This document describes how Intensity's components communicate — protocols, data flows, contracts, and dependency directions. It is written for architects and senior engineers integrating or extending the system.

---

## Short

The mobile client talks to the API via **REST over HTTPS** (request/response, client-initiated). The API talks to PostgreSQL via **ORM persistence** and sends transactional email (password reset) via **Resend**. There is **no server push**, **no WebSockets**, and **no direct client-to-database path**. Invites use HTTPS links resolved by the mobile OS into the app. Consistency is **eventual** — clients refresh on read.

---

## Medium

### Integration map

```
Mobile client ──REST (HTTPS)──► API ──JPA/Hibernate──► PostgreSQL
     │                              │
     │                              └── Resend (HTTPS) ──► participant inbox
     └── no direct DB               └── sole persistence gateway
```

| Integration | Protocol | Direction |
|-------------|----------|-----------|
| Client → API | REST JSON | Client initiates |
| API → DB | SQL via ORM | API only |
| API → Resend | HTTPS JSON | Outbound transactional email |
| Client → OS share sheet | Native bridge | Outbound invite sharing |
| Deep link → Client | App/Universal Links | Inbound invite open |

### Sync model

**Pull-based eventual consistency.** When a participant adds an experience from their phone, other clients see it on the next API read. The shared-phone ritual fetches the experience pool from the API immediately before drawing.

No live notifications when data changes. No multi-device sync during draw — one phone holds draw state locally.

### Key flows

**Authentication**

```
Client POST /v1/auth/login { email, password }
  ← { token, participantId, displayName, accessMode? }
Client stores token locally for subsequent requests
```

**Password reset**

```
Client POST /v1/auth/forgot-password { email }
  ← 204 (always; email sent only when the account exists)
API → Resend → participant inbox with /auth/reset-password?t={token}
Client POST /v1/auth/reset-password { token, password }
  ← 204
```

**Joint login (Experience Box)**

```
Client POST /v1/auth/group { credentials[], reuseSessionToken? }
  ← { token, groupId, groupIds, members, accessMode }
  OR 409 if credentials span incompatible groups
```

`reuseSessionToken` may carry an existing Experiences JWT so slot 1 need not re-enter a password when that participant is already signed in.

**Invite lifecycle**

```
POST /v1/groups/{id}/invites        → { code, linkToken, expiresAt }
GET  /v1/invites/validate?code=      → { groupPreview, expiresAt, status }
POST /v1/invites/{id}/accept       → { groupId, membership confirmed }
DELETE /v1/invites/{id}             → revoked
```

**Experience registration (Experiences mode)**

```
Client collects assistant input locally
POST /v1/boxes/{id}/experiences { description, intensity, params, type, reflection? }
  ← experience persisted with seal
Branching: POST /v1/boxes/{id}/experiences/batch { experiences: [...] } (up to 5)
  ← list of persisted experiences with seals
```

**Box deletion (Experience Box mode)**

```
DELETE /v1/boxes/{id}
  ← 204; cascade removes experiences server-side
Client refreshes GET /v1/groups/{id}/boxes
```

**Draw ritual (no API write)**

```
GET /v1/boxes/{id}/experiences → pool
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

All paths below are under the `/v1` prefix. Canonical contract: @ref:openapi.

| Resource | Operations |
|----------|------------|
| `/v1/auth/login` | POST single participant |
| `/v1/auth/group` | POST multi-participant joint session (optional `reuseSessionToken`) |
| `/v1/auth/forgot-password` | POST request reset email (always 204) |
| `/v1/auth/reset-password` | POST set new password with token |
| `/v1/participants` | POST register |
| `/v1/groups` | GET list for participant; POST create (name, color) |
| `/v1/groups/{id}` | PATCH update name/color |
| `/v1/groups/{id}/members` | DELETE self (leave) |
| `/v1/groups/{id}/invites` | POST create; GET list active |
| `/v1/invites/validate` | GET by code or token |
| `/v1/invites/{id}/accept` | POST |
| `/v1/invites/{id}` | DELETE revoke |
| `/v1/groups/{id}/boxes` | GET list |
| `/v1/boxes` | POST create (including `requireAllParticipants`) |
| `/v1/boxes/{id}` | DELETE (cascade) |
| `/v1/boxes/{id}/experiences` | GET list; POST create |
| `/v1/boxes/{id}/experiences/batch` | POST create up to 5 |
| `/v1/experiences/{id}` | PUT update; DELETE (author only) |

Breaking changes require `/v2` per technical decisions. Member listing for a group is returned on group/auth responses; there is no separate `GET /v1/groups/{id}/members` handler in the API.

### Invite link contract

Deep link format (illustrative):

```
https://app.intensity.example/join?t={linkToken}
```

Mobile OS routes to installed app → client calls `GET /v1/invites/validate?t=` → preview screen.

Code path: user enters `AB12CD` → `GET /v1/invites/validate?code=AB12CD`.

Both channels resolve the same invite record.

### Security on the wire

- TLS everywhere in production
- Bearer token on authenticated requests
- Tokens stored in secure client storage (Capacitor Preferences or platform keystore wrapper)
- No credentials in invite links — token is opaque, single-purpose

### Explicitly absent integrations

Payment gateways, analytics SDKs, push notification services (FCM/APNs), external IdP (OAuth), CDN asset pipeline, message queues, webhooks from client. Transactional email uses Resend only (password reset).

### Operational webhook (engineering layer)

Production API deploy uses inbound webhook from CI — documented in engineering layer, not a product integration.

## Decisions assumed in this rewrite

- Invite validation is a **read-only GET** before accept POST.
- Joint login returns **409** when credentials belong to different existing groups.
- Box delete is **synchronous REST** with server-side cascade.
- Groups support explicit **create** and **PATCH** for display name and color.
