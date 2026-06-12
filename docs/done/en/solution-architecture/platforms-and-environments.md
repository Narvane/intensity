# Platforms and Environments

This document describes where Intensity runs — the execution platforms, deployment topology, and how many instances of each component exist in production.

**Audience:** senior architects and engineers who need to understand the structural layout of the solution without implementation or operational detail.

---

## Short

Intensity runs on **two platforms**: a **mobile client** on participants' phones and a **centralized API** on a server. The client is deployed to **many devices**; the API runs as a **single instance** in **one server environment**. A **database** is connected to that environment and is accessed only by the API.

---

## Medium

### Execution platforms

| Platform | Role | Instances |
|----------|------|-----------|
| **Mobile** | Hosts the client application — interface, interaction flows, and core product behavior | One installation per participant device |
| **Server** | Hosts the API and its connected database | One centralized environment |

There is no web client in the current architecture. The product is delivered exclusively through the mobile application.

### Deployment topology

```
┌─────────────────────────────────────────────────────────┐
│  Server environment (single instance)                   │
│  ┌─────────┐      ┌──────────────┐                        │
│  │   API   │ ───► │   Database   │                        │
│  └────▲────┘      └──────────────┘                        │
└───────┼─────────────────────────────────────────────────┘
        │ REST
        │
   ┌────┴────┬──────────┬──────────┐
   │         │          │          │
┌──▼──┐  ┌──▼──┐   ┌──▼──┐   ┌──▼──┐
│Phone│  │Phone│   │Phone│   │Phone│   ... (many clients)
│Client│  │Client│   │Client│   │Client│
└─────┘  └─────┘   └─────┘   └─────┘
```

### Environment model

- **Client environment:** each participant's mobile device. The same client build runs independently on every phone.
- **Server environment:** a single centralized runtime where the API and database coexist. All clients converge on this environment as the source of truth for persisted data.

The asymmetry is intentional: **many clients, one API**. Individual experience registration by each participant requires a shared persistence layer, while the product experience itself lives on each device.

---

## Detailed

### Mobile platform

The mobile platform is where participants interact with Intensity. It hosts:

- The full user interface and navigation structure
- Onboarding, authentication screens, and creation flows
- The shared-moment ritual (draw, alignment, card reveal)
- Client-side preferences not persisted in the domain model (such as UI language)

Each phone runs its own client instance. There is no requirement that all participants use the same device model or operating system version beyond what the mobile application supports.

In **Experiences mode**, each participant typically uses their own phone to register experiences individually. In **Experience Box mode**, the group ritual — browsing boxes, drawing, revealing — happens on **one shared phone**, while contributions may still have been registered from separate devices.

### Server platform

The server platform exists to centralize persisted data. It hosts:

- The **API** — the only application-layer entry point for reading and writing domain data
- The **database** — exclusive persistence store for the domain model

The API runs in a **single instance** within one server environment. There is no multi-region or horizontally scaled API topology in the current architecture.

### What runs where

| Concern | Mobile client | Server (API + database) |
|---------|---------------|---------------------------|
| User interface and UX flows | ✓ | — |
| Draw and reveal ritual | ✓ | — |
| Experience registration persistence | invokes API | ✓ |
| Participant, group, box, experience data | reads via API | ✓ (source of truth) |
| Authentication against persisted credentials | invokes API | ✓ |
| UI language preference | ✓ (local) | — |
| Pre-defined suggestion packs | ✓ (embedded) | — |

### Boundaries not in scope

The current architecture does not include:

- A web application or browser-based client
- Separate staging or multi-environment topology (beyond the conceptual single server environment described here)
- Offline-capable client operation (identified as a future direction elsewhere)

Operational specifics — hosting provider, containerization, CI/CD pipelines, monitoring — belong to the Engineering and Operations layer.
