# Decisioni Tecniche

Questo documento registra le scelte tecnologiche concrete per Intensity — con motivazioni, alternative considerate e criteri di valutazione. È scritto per sviluppatori che implementano o estendono il sistema.

---

## Breve

Intensity usa **Java 21 + Spring Boot 3.5** con **PostgreSQL 16** e **Flyway** sul server, e **React 19 + Vite 6 + Capacitor 7** sul client, in un **monorepo** deployato via **Docker su VPS** con **GitHub Actions → GHCR → webhook**. Il codice API si organizza per **cartelle dominio**; il codice client segue **Clean Architecture** come struttura cognitiva. REST evolve **retrocompatibilmente**; breaking change richiedono `/v2`.

---

## Media

### Indice decisioni

| ID | Decisione |
|----|-----------|
| **DT-01** | Java 21 + Spring Boot 3.5 + Maven |
| **DT-02** | PostgreSQL 16 |
| **DT-03** | Flyway + Hibernate |
| **DT-04** | React 19 + Vite 6 + TypeScript |
| **DT-05** | Capacitor 7 (shell WebView) |
| **DT-06** | Monorepo (`api/` + `client/`) |
| **DT-07** | VPS + Docker Compose per produzione |
| **DT-08** | GitHub Actions → GHCR → webhook deploy (solo API) |
| **DT-09** | Release store client manuali |
| **DT-10** | API retrocompatibile; `/v2` per break |
| **DT-11** | Nessun aggiornamento OTA (baseline) |
| **DT-12** | API: moduli dominio, layer semplici |
| **DT-13** | Client: mappa cognitiva Clean Architecture |
| **DT-14** | Codici invito: sottoinsieme Crockford Base32 6 caratteri |
| **DT-15** | Elimina scatola: DB ON DELETE CASCADE + guard servizio |
| **DT-16** | Email transazionale via Resend (reset password) |

### DT-01 — Java + Spring Boot

**Perché:** Ecosistema REST maturo, produttività JPA, integrazione Flyway forte, familiarità maintainer.

**Alternative rifiutate:** API Node (layering dominio meno strutturato per questo maintainer), server Kotlin (consistenza team con scelta Java esistente).

### DT-04 + DT-05 — React + Capacitor

**Perché:** Codebase web singola per iOS e Android; iterazione Vite veloce; Capacitor copre distribuzione store senza complessità bridge React Native.

**Alternative rifiutate:** React Native (costo bridge nativo più alto per bisogni nativi modesti), codebase nativa Swift/Kotlin duale (2× manutenzione).

### DT-12 — Struttura API

Cartelle domain-first (`participant/`, `group/`, `invite/`, `box/`, `experience/`). Ogni modulo: Controller → Service → Repository. Entità anemiche; regole business nei servizi. DTO al confine REST. L'infrastruttura trasversale (sicurezza JWT, configurazione web di CORS/errori/OpenAPI, tipi condivisi, demo seed, email in uscita) vive sotto un unico package `platform/`, così il livello superiore si legge come cinque concetti di dominio più una piattaforma.

Non aggregati DDD completi — CRUD pragmatico con policy esplicite (`ConviteExpiracaoPolicy`, `GrupoCapacidadeVerifier`).

### DT-13 — Struttura client

Casi d'uso indipendenti dai componenti React. Esempio:

```
ExecutarSorteioUseCase
ExcluirCaixinhaUseCase
AceitarConviteUseCase
```

I componenti presentazione chiamano casi d'uso; i casi d'uso chiamano adapter API.

### DT-14 — Codici invito

6 caratteri da alfabeto non ambiguo (no 0/O, 1/I). Unicità imposta da indice univoco DB con retry su collisione. Token link: UUID v4 indicizzato separatamente.

**Perché:** Codici brevi per condivisione verbale; link UUID per tap-to-open.

### DT-15 — Elimina scatola

FK `experience.box_id` con `ON DELETE CASCADE`. Il servizio verifica appartenenza prima dell'eliminazione. La transazione avvolge eliminazione + hook audit log (opzionale futuro).

**Perché:** Prevenire esperienze orfane; operazione autorevole singola.

### DT-16 — Resend per email transazionale

Il reset password è il primo caso d'uso di email in uscita. L'API chiama Resend via HTTPS (`intensity.email.resend-api-key`, `from`, `app-base-url`). Senza API key, il sender registra l'HTML nel log invece di chiamare Resend — adatto ai profili local e test.

**Perché:** Operazioni minime per un maintainer solo (niente SMTP sul VPS); deliverability con dominio verificato; il free tier copre il volume di reset.

**Alternative rifiutate:** SMTP proprio/Gmail (deliverability fragile), AWS SES (IAM/sandbox più pesante), SendGrid (più superficie di config per lo stesso lavoro).

---

## Dettagliata

### DT-02 — PostgreSQL

Il modello relazionale si adatta a gruppi, appartenenze, inviti, scatole, esperienze. Colonne JSON non usate per dominio core — chiarezza rispetto a flessibilità documento.

### DT-03 — Flyway + Hibernate

Flyway possiede verità schema; Hibernate valida mapping. Migrazioni come `V5__convite.sql` e `V6__rename_schema_to_english.sql` esemplificano evoluzione incrementale.

### DT-06 — Monorepo

API e client versionati insieme in un repo; documentazione in `docs/`. Semplifica context switching maintainer solo.

### DT-07 — VPS + Compose

Costo operativo inferiore a Kubernetes per API single-instance. Accetta rischio downtime durante restart deploy.

### DT-08 — CI webhook deploy

Percorso API automatizzato riduce attrito; client resta manuale per imprevedibilità review store.

### DT-10 — Compatibilità API

Aggiungere campi opzionali o nuovi endpoint (`POST /v1/groups/{id}/invites`, `DELETE /v1/boxes/{id}`, `PATCH /v1/groups/{id}`) è compatibile. Rimuovere campi o cambiare semantica richiede `/v2` e release client coordinata.

### Durata dei token di sessione

Configurati sotto `intensity.jwt` (`application.yml` / override di produzione):

| Modalità | TTL predefinito | Motivo |
|----------|-----------------|--------|
| Experiences | 30 giorni | Contributo individuale nel tempo |
| Experience Box | 4 ore | Rituale sul telefono condiviso; minore esposizione se il dispositivo resta sbloccato |

### DT-11 — Nessun OTA

Gli asset web Capacitor vanno con build store solo. Ciclo deploy API più veloce intenzionalmente disaccoppiato dal client.

### Criteri valutazione usati nelle decisioni

1. **Sostenibilità maintainer solo** — minimizzare parti mobili
2. **Realtà review store** — l'API non deve rompere client vecchi
3. **Fit prodotto** — logica rituale client-heavy resta in TypeScript
4. **Modello sociale** — Postgres centralizzato per scatole condivise
5. **Exit ramp futuri** — percorsi documentati verso scale, offline, push

### Tabella alternative (riepilogo)

| Bisogno | Scelto | Rifiutato |
|---------|--------|-----------|
| Shell mobile | Capacitor | RN, nativo duale |
| Stile API | Risorse REST | GraphQL, BFF |
| Trasporto invito | REST + deep link | Provider SMS, solo QR |
| Semantica elimina | Cascata hard | Archivio soft |
| Sync | Pull in lettura | Push WebSocket |

## Decisioni assunte in questa riscrittura

- **DT-14** e **DT-15** supportano nuove funzionalità invito ed eliminazione scatole.
- **DT-16** introduce Resend per email di reset password senza SMTP sul VPS.
- Il modulo **`invite/`** segue lo stesso pattern DT-12 dei domini esistenti.
