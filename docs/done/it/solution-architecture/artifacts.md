# Artefatti

Questo documento cataloga i blocchi strutturali di Intensity — le applicazioni, i servizi e i componenti di persistenza che compongono la soluzione e come le responsabilità si distribuiscono tra loro.

**Pubblico:** architetti e ingegneri senior che devono comprendere cosa esiste strutturalmente nella soluzione senza dettagli di stack tecnologico o procedure di deploy.

---

## Breve

Intensity è composto da **due artefatti applicativi** e **un artefatto di persistenza**: un **client mobile**, un'**API orientata alle risorse** e un **database**. Il **client concentra il nucleo** del prodotto — interfaccia, flussi e comportamento di gioco. L'**API centralizza i dati** come unica fonte di verità. Il **database** memorizza il modello di dominio ed è accessibile esclusivamente dall'API.

---

## Media

### Inventario artefatti

| Artefatto | Tipo | Responsabilità |
|-----------|------|----------------|
| **Client** | Applicazione mobile | Nucleo del prodotto: interfaccia, flussi utente, meccanica di estrazione, logica lato client |
| **API** | Applicazione server | Accesso orientato alle risorse; orchestrazione della persistenza |
| **Database** | Archivio di persistenza | Memorizzazione dati di dominio; fonte di verità dietro l'API |

### Divisione delle responsabilità

```
┌──────────────────────────────────────────────┐
│                  CLIENT                       │
│  • Interfaccia e struttura dell'esperienza    │
│  • Navigazione e orchestrazione schermate     │
│  • Comportamento estrazione, filtro, rivelazione│
│  • Assistente creazione e pacchetti suggerimenti│
│  • Preferenze locali (es. lingua UI)            │
│  • Consumo API per operazioni persistite      │
└──────────────────────┬───────────────────────┘
                       │ REST (risorse)
┌──────────────────────▼───────────────────────┐
│                    API                        │
│  • Endpoint risorse per entità di dominio     │
│  • Autenticazione e autorizzazione            │
│  • Validazione al confine di persistenza      │
│  • Punto unico di accesso al database         │
└──────────────────────┬───────────────────────┘
                       │
┌──────────────────────▼───────────────────────┐
│                 DATABASE                      │
│  • Partecipanti, gruppi, box, esperienze      │
│  • Fonte di verità di tutti i dati persistiti │
└──────────────────────────────────────────────┘
```

### Client come nucleo del prodotto

Il client non è uno strato di presentazione sottile. Porta il valore strutturale del prodotto:

- L'interfaccia guidata che aiuta i giocatori a usare la creatività e creare momenti
- L'assistente di creazione e i suggerimenti predefiniti che fungono da tutorial implicito
- Il rituale del momento condiviso (filtri intensità, suggerimento allineamento, rivelazione flip card)
- La meccanica di estrazione stessa

Le regole di business dell'applicazione sono intenzionalmente semplici — essenzialmente un'estrattore casuale su esperienze raccolte. La complessità e la differenziazione stanno in come l'interfaccia struttura l'esperienza, non nell'orchestrazione lato server.

### API come centralizzatore dati

L'API è un'**API di risorse**, non un Backend-for-Frontend (BFF). Espone risorse di dominio per operazioni di creazione, lettura, aggiornamento ed eliminazione. Non aggrega, rimodella o adatta risposte a schermate specifiche.

Questo ruolo è indispensabile per:

- **Centralizzare i dati** affinché tutte le istanze client condividano lo stesso stato persistito
- **Registrare esperienze individualmente** per ogni partecipante, ciascuno dal proprio dispositivo
- **Mantenere gruppi, box ed esperienze** come dominio coerente tra dispositivi

### Database come persistenza esclusiva

Il database conserva tutto nel modello dati funzionale: partecipanti, gruppi, box ed esperienze. Nessun client memorizza dati di dominio come fonte di verità. I client possono cachare o mantenere stato transitorio (come risultati di estrazione), ma la persistenza fluisce sempre attraverso l'API verso il database.

---

## Dettagliata

### Client mobile

Il client mobile è l'artefatto principale della soluzione. I partecipanti lo installano sui propri telefoni. È l'unico artefatto che scala a **molte istanze** — una per dispositivo.

**Detiene:**

| Area | Cosa gestisce il client |
|------|-------------------------|
| **Presentazione** | Tutte le schermate, overlay, stati caricamento/vuoto/errore |
| **Flussi di interazione** | Bootstrap, onboarding, autenticazione, percorso Esperienze, percorso Box Esperienze |
| **Comportamento di gioco** | Estrazione casuale con filtri intensità; risultati transitori |
| **Esperienza di creazione** | Assistente in cinque passi, pacchetti suggerimenti, valutazione parametri |
| **Contesto sessione** | Modalità accesso attiva, gruppo selezionato, box selezionato (ambito operativo) |
| **Stato locale** | Preferenza lingua interfaccia; flag onboarding al primo avvio |

**Delega all'API:**

| Area | Cosa il client richiede all'API |
|------|----------------------------------|
| **Autenticazione** | Validazione credenziali contro partecipanti persistiti |
| **Registrazione** | Creazione nuovo partecipante |
| **CRUD esperienze** | Creare, elencare, eliminare esperienze in un box |
| **Gestione box** | Elencare e creare box per un gruppo |
| **Risoluzione gruppo** | Risolvere il gruppo formato dai partecipanti autenticati |

Il client non implementa il pattern BFF. La modellazione dati per schermata avviene lato client dopo aver ricevuto rappresentazioni di risorse dall'API.

### API

L'API è un artefatto applicativo unico lato server. Gira in un ambiente e serve tutte le istanze client.

**Caratteristiche:**

- **Orientata alle risorse:** gli endpoint mappano risorse di dominio (partecipanti, gruppi, box, esperienze), non schermate o viste composite
- **Senza stato a livello applicativo:** sessione e contesto navigazione vivono sul client; l'API gestisce richieste in modo indipendente
- **Gateway esclusivo del database:** nessun client si connette direttamente al database

**Non detiene:**

- Layout schermate o decisioni di navigazione
- Logica di estrazione o comportamento rituale di rivelazione
- Contenuto pacchetti suggerimenti o orchestrazione passi assistente
- Lingua interfaccia o stato onboarding

### Database

Il database è un artefatto di persistenza connesso all'API nell'ambiente server. Non è un artefatto applicativo deployato separatamente con logica di business propria, ma è un componente strutturale distinto dell'architettura.

**Memorizza:**

- Record partecipanti (nome visualizzato, email, credenziali)
- Associazioni gruppo derivate dall'autenticazione in modalità Box Esperienze
- Box (nome, tipo, gruppo proprietario)
- Esperienze (descrizione, intensità, parametri, riflessione, autore, timestamp, sigillo integrità)

**Non memorizza:**

- Risultati estrazione (transitori, solo client)
- Preferenza lingua interfaccia
- Stato completamento onboarding
- Testi pacchetti suggerimenti (incorporati nel client)

### Relazioni tra artefatti

| Da | A | Relazione |
|----|---|-----------|
| Client | API | Consuma risorse REST; molti-a-uno |
| API | Database | Legge e scrive; uno-a-uno nell'ambiente server |
| Client | Database | Nessuna connessione diretta |

### Cosa non è artefatto separato

I seguenti elementi fanno parte del client o dell'API, non sono artefatti autonomi:

- **Pacchetti suggerimenti** — contenuto incorporato nel client
- **Onboarding e guida rapida** — flussi esclusivi del client
- **Motore estrazione** — comportamento lato client, non servizio server
- **Message broker o bus eventi** — assenti nell'architettura attuale
