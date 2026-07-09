# Integrazioni e Comunicazione

Questo documento descrive come i componenti di Intensity comunicano — protocolli, flussi dati, contratti e direzioni delle dipendenze. È scritto per architetti e senior engineer che integrano o estendono il sistema.

---

## Breve

Il client mobile comunica con l'API via **REST su HTTPS** (request/response, avviato dal client). L'API comunica con PostgreSQL via **persistenza ORM**. Non c'è **push server**, **WebSockets** né **percorso diretto client-database**. Gli inviti usano link HTTPS risolti dal sistema operativo mobile nell'app. La consistenza è **eventuale** — i client aggiornano in lettura.

---

## Media

### Mappa integrazioni

```
Client mobile ──REST (HTTPS)──► API ──JPA/Hibernate──► PostgreSQL
     │                              │
     └── nessun DB diretto          └── unico gateway persistenza
```

| Integrazione | Protocollo | Direzione |
|--------------|------------|-----------|
| Client → API | REST JSON | Client avvia |
| API → DB | SQL via ORM | Solo API |
| Client → share sheet OS | Bridge nativo | Invito condiviso in uscita |
| Deep link → Client | App/Universal Links | Apertura invito in ingresso |

### Modello sync

**Consistenza eventuale pull-based.** Quando un partecipante aggiunge un'esperienza dal proprio telefono, gli altri client la vedono alla successiva lettura API. Il rituale telefono condiviso recupera il pool esperienze dall'API immediatamente prima dell'estrazione.

Nessuna notifica live quando i dati cambiano. Nessuna sync multi-dispositivo durante l'estrazione — un telefono mantiene lo stato estrazione localmente.

### Flussi chiave

**Autenticazione**

```
Client POST /v1/auth/login { email, password }
  ← { token, participantId, displayName }
Client memorizza token localmente per richieste successive
```

**Login congiunto (Scatola delle Esperienze)**

```
Client POST /v1/auth/group { credentials[], reuseSessionToken? }
  ← { token, groupId, groupIds, members, accessMode }
  OPPURE 409 se le credenziali appartengono a gruppi incompatibili
```

**Ciclo di vita invito**

```
POST /v1/groups/{id}/invites        → { code, linkToken, expiresAt }
GET  /v1/invites/validate?code=      → { groupPreview, expiresAt, status }
POST /v1/invites/{id}/accept       → { groupId, membership confirmed }
DELETE /v1/invites/{id}             → revocato
```

**Registrazione esperienza (modalità Esperienze)**

```
Client raccoglie input assistente localmente
POST /v1/boxes/{id}/experiences { description, intensity, params, type, reflection? }
  ← esperienza persistita con sigillo
Ramificazione: POST /v1/boxes/{id}/experiences/batch { experiences: [...] } (fino a 5)
  ← elenco di esperienze persistite con sigilli
```

**Eliminazione scatola (modalità Scatola delle Esperienze)**

```
DELETE /v1/boxes/{id}
  ← 204; rimuove esperienze a cascata lato server
Client aggiorna GET /v1/groups/{id}/boxes
```

**Rituale estrazione (nessuna scrittura API)**

```
GET /v1/boxes/{id}/experiences → pool
Client filtra, randomizza, rivela localmente
(nessun POST per risultato estrazione)
```

### Contratto errori

Gli errori REST restituiscono `{ code, message }` con HTTP status appropriato. Il client mappa al copy utente. Casi critici:

| Status | Scenario |
|--------|----------|
| 401 | Token non valido o scaduto |
| 403 | Non membro del gruppo |
| 404 | Scatola, gruppo o invito non trovato |
| 409 | Conflitto appartenenza gruppo al login congiunto |
| 410 | Invito scaduto o revocato |
| 422 | Fallimento validazione (lunghezza nome, range intensità) |

---

## Dettagliata

### Outline risorse REST

| Risorsa | Operazioni |
|---------|------------|
| `/v1/auth/login` | POST singolo partecipante |
| `/v1/auth/group` | POST sessione congiunta multi partecipante (opzionale `reuseSessionToken`) |
| `/v1/participants` | POST registrazione |
| `/v1/groups` | GET elenco; POST crea (nome, colore) |
| `/v1/groups/{id}` | PATCH aggiorna nome/colore |
| `/v1/groups/{id}/members` | DELETE self (uscita) |
| `/v1/groups/{id}/invites` | POST crea; GET elenco attivi |
| `/v1/invites/validate` | GET per codice o token |
| `/v1/invites/{id}/accept` | POST |
| `/v1/invites/{id}` | DELETE revoca |
| `/v1/groups/{id}/boxes` | GET elenco |
| `/v1/boxes` | POST crea (include `requireAllParticipants`) |
| `/v1/boxes/{id}` | DELETE (cascata) |
| `/v1/boxes/{id}/experiences` | GET elenco; POST crea |
| `/v1/boxes/{id}/experiences/batch` | POST crea fino a 5 |
| `/v1/experiences/{id}` | PUT aggiorna; DELETE (solo autore) |

Prefisso versione `/v1` esplicito; breaking change richiedono `/v2` per decisioni tecniche. Non esiste handler `GET /v1/groups/{id}/members` — i membri arrivano nelle risposte di gruppo/auth.

### Contratto link invito

Formato deep link (illustrativo):

```
https://app.intensity.example/join?t={linkToken}
```

Il sistema operativo mobile instrada all'app installata → il client chiama `GET /convites/validar?t=` → schermata anteprima.

Percorso codice: l'utente inserisce `AB12CD` → `GET /convites/validar?code=AB12CD`.

Entrambi i canali risolvono lo stesso record invito.

### Sicurezza sul wire

- TLS ovunque in produzione
- Bearer token sulle richieste autenticate
- Token memorizzati in storage client sicuro (Capacitor Preferences o wrapper keystore piattaforma)
- Nessuna credenziale nei link invito — il token è opaco, a scopo singolo

### Integrazioni esplicitamente assenti

Gateway pagamento, SDK analytics, servizi push notification (FCM/APNs), IdP esterno (OAuth), pipeline asset CDN, message queue, webhook dal client.

### Webhook operativo (layer ingegneria)

Il deploy API produzione usa webhook in ingresso da CI — documentato nel layer ingegneria, non un'integrazione prodotto.

## Decisioni assunte in questa riscrittura

- La validazione invito è una **GET read-only** prima dell'accept POST.
- Il login congiunto restituisce **409** quando le credenziali appartengono a gruppi esistenti diversi.
- L'eliminazione scatola è **REST sincrona** con cascata lato server.
