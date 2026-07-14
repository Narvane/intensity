# Piattaforme e Ambienti

Questo documento descrive dove Intensity viene eseguito — piattaforme di esecuzione, ambienti di deployment e pattern di utilizzo dei dispositivi. È scritto per architetti e senior engineer che pianificano infrastruttura e distribuzione client.

---

## Breve

Intensity gira su **due piattaforme di prodotto**: un **client mobile** (iOS e Android via Capacitor) e un **server centralizzato** (API + PostgreSQL). La **distribuzione canonica** è tramite app store; non esiste un prodotto web/PWA generale. Lo sviluppo **locale** abbina un'API localhost con Vite dev server o build emulator; la **produzione** esegue API e database in Docker su un VPS mentre i client store chiamano l'API HTTPS pubblica. Uno stack **demo pubblico** separato sullo stesso VPS serve la stessa UI React nel browser contro un database demo isolato (anteprima portfolio / recruiter — **non** è staging di release).

---

## Media

### Piattaforme di esecuzione

| Piattaforma | Ruolo | Istanze |
|-------------|-------|---------|
| **Client mobile** | UI prodotto completa, flussi, rituale estrazione, sessione locale | Un'installazione per dispositivo partecipante |
| **Server** | REST API + PostgreSQL co-locato | Produzione (+ demo pubblico opzionale) su un VPS |

**Topologia:** molti client mobile → una REST API → un database. Nessuna sync peer-to-peer, nessun CDN, nessun message broker. Il demo pubblico aggiunge una seconda coppia API+DB e un host SPA statico dietro lo stesso reverse proxy — senza una seconda topologia di prodotto.

### Pattern di utilizzo dispositivi

| Modalità | Pattern dispositivo |
|----------|---------------------|
| **Esperienze** | Ogni partecipante usa il proprio telefono per registrare idee |
| **Scatola delle Esperienze** | Rituale di gruppo (naviga scatole, invita, elimina, estrae, rivela) su **un telefono condiviso**; i contributi possono arrivare da dispositivi separati |

L'accettazione invito e il contributo individuale avvengono su dispositivi personali; il rituale di estrazione assume co-presenza su uno schermo condiviso. Il demo pubblico permette a una singola sessione browser di approssimare flussi multi-account cambiando credenziali.

### Ambienti

| Ambiente | Client | API | Database |
|----------|--------|-----|----------|
| **Locale** | Vite dev server o build debug Capacitor | `localhost:8080` | PostgreSQL via Docker Compose |
| **Produzione** | Build store (AAB/IPA) | HTTPS su VPS (`api.` / deep-link `app.`) | Container PostgreSQL sullo stesso VPS |
| **Demo pubblico** | SPA Vite statica (`demo-intensity.`) | HTTPS (`demo-intensity-api.`), profilo Spring `demo` | Postgres isolato + seed con reset giornaliero |

**Non** esiste un ambiente staging / promozione pre-produzione. Il demo è solo campione per anteprima prodotto; non deve mai condividere JWT secret o volumi database di produzione. Ops: @ref:deploy-readme — [deploy/README.md](../../../deploy/README.md).

### Requisiti runtime

- Mobile: iOS e Android versione corrente meno due major
- Server: VPS Linux, Docker 24+, Docker Compose v2
- Rete richiesta per tutte le operazioni persistite (nessuna baseline offline)

---

## Dettagliata

### Piattaforma mobile

Il client è un'**app ibrida**: UI React in shell WebView Capacitor con asset statici embedded dopo la build. Capacità native usate minimamente: lifecycle app, status bar, splash screen, preferenze locali (lingua, flag onboarding).

Distribuzione **di prodotto** esclusivamente tramite **Google Play** (AAB) e **Apple App Store** (IPA). Sideload e PWA generale fuori scope.

I deep link per **URL invito** in produzione risolvono nell'app installata (Universal Links / App Links) o richiedono installazione dallo store se assente. I link invito del demo usano l'host SPA demo (`/join`) e **non** devono comparire nei file di associazione nativa del dominio di produzione.

### Piattaforma server

Singolo processo JVM (Spring Boot) dietro reverse proxy (Caddy o equivalente) che termina TLS. PostgreSQL 16 co-locato nello stack Compose su un VPS. Produzione e demo usano progetti Compose, volumi e JWT secret separati; il demo riusa la stessa immagine GHCR dell'API con `SPRING_PROFILES_ACTIVE=demo`.

Lo scaling orizzontale non è baseline — l'architettura accetta API single-instance con percorso di evoluzione futuro documentato nelle decisioni architetturali.

### Topologia sviluppo locale

```
Macchina sviluppatore
├── client/     npm run dev → browser :5173
├── api/        spring-boot:run → :8080
└── docker      postgres → :5432

Opzionale: Capacitor copy → emulatore Android (10.0.2.2:8080) o dispositivo (IP LAN)
Opzionale: SPRING_PROFILES_ACTIVE=demo contro DB intensity_demo
```

Variabili d'ambiente:

| Variabile | Ruolo |
|-----------|-------|
| `VITE_API_URL` | URL base API al build del client |
| `VITE_INVITE_BASE_URL` | Host dei link invito (produzione o demo) |
| `VITE_API_PROXY_TARGET` | Proxy opzionale `/v1` in Vite (dev locale) |
| `VITE_DEMO` | Se `true`, mostra banner demo e scorciatoie auth |

TTL JWT API (default in `application.yml`, sovrascrivibili in produzione/demo):

| Sessione | Proprietà | Default |
|----------|-----------|---------|
| Experiences | `intensity.jwt.expiration-seconds` | 2_592_000 (30 giorni) |
| Experience Box | `intensity.jwt.experience-box-expiration-seconds` | 14_400 (4 ore) |

### Topologia produzione

```
App store → Client mobile
                ↓ HTTPS REST
           VPS (Docker Compose)
             ├── reverse proxy :443
             ├── container API :8080
             └── container PostgreSQL
```

Deploy attivato da webhook dopo che CI pusha l'immagine nel registry.

### Topologia demo pubblico (stesso VPS)

```
Browser → demo-intensity.<dominio> (nginx SPA statica)
                ↓ HTTPS REST
         demo-intensity-api.<dominio> → intensity-demo-api (profilo demo)
                            → postgres demo (seed + reset giornaliero)
```

Caddy (compose produzione) termina TLS per gli host demo e fa reverse proxy ai container demo sulla rete Docker condivisa `intensity`.

### Cosa è esplicitamente assente

Distribuzione web/PWA generale di prodotto, BaaS, Kubernetes, VPS staging per promozione release, CDN, WebSockets, gRPC, GraphQL, sync multi-dispositivo in tempo reale durante l'estrazione.

## Decisioni assunte in questa riscrittura

- I deep link invito del **prodotto store** sono una **preoccupazione piattaforma mobile** (App Links / Universal Links).
- L'hosting web del demo pubblico è una preoccupazione di **portfolio/preview**, non un secondo canale di prodotto.
- I flussi eliminazione scatola e invito richiedono rete; nessuna coda offline in baseline.
