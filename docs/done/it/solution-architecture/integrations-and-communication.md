# Integrazioni e Comunicazione

Questo documento descrive come i componenti strutturali di Intensity comunicano — i protocolli, i pattern di interazione e i confini del flusso dati tra client, API e database.

**Pubblico:** architetti e ingegneri senior che devono comprendere la topologia di integrazione senza contratti di implementazione o dettagli di stack tecnologico.

---

## Breve

Il **client** mobile comunica con l'**API** esclusivamente via **REST**. L'API comunica con il **database** attraverso il proprio strato di persistenza. Non esistono **messaggistica**, **WebSocket** né **connessione diretta client–database**. L'API espone un'interfaccia **orientata alle risorse** — non aggregati adattati alle schermate.

---

## Media

### Mappa di comunicazione

```
Client  ──REST──►  API  ──persistenza──►  Database
  │                  │
  │                  └── gateway unico per dati persistiti
  │
  └── nessun percorso diretto al database
```

### Protocolli e pattern

| Integrazione | Protocollo / pattern | Direzione |
|--------------|---------------------|-----------|
| Client → API | REST (HTTP) | Richiesta/risposta |
| API → Database | Accesso persistenza | Lettura/scrittura |
| Client → Database | — | Non consentito |
| Client ↔ Client | — | Nessuna comunicazione peer-to-peer |
| API ↔ Servizi esterni | — | Nessuno nell'architettura attuale |

### REST come unico canale client–server

Tutte le operazioni persistite fluiscono attraverso chiamate REST dal client all'API:

- Autenticazione e registrazione
- Elenco e creazione box
- Creazione, elenco ed eliminazione esperienze
- Risoluzione gruppi dal login multi-partecipante

Il client avvia ogni interazione. L'API risponde con rappresentazioni di risorse. Non esiste push avviato dal server verso i client.

### Stile contratto: API di risorse

L'API segue un modello **orientato alle risorse**:

- Gli endpoint rappresentano entità di dominio (partecipanti, box, esperienze), non flussi di interfaccia
- Le risposte portano dati di risorse, non view model specifici di schermata
- Il client è responsabile di mappare le risorse alle esigenze dell'interfaccia

Questo differisce da un BFF (Backend-for-Frontend), che esporrebbe endpoint modellati per schermata o piattaforma client. Intensity evita deliberatamente quel pattern.

### Cosa è esplicitamente assente

| Meccanismo | Stato |
|------------|-------|
| Code messaggi / bus eventi | Non utilizzati |
| WebSocket / push server | Non utilizzati |
| GraphQL | Non utilizzato |
| gRPC | Non utilizzato |
| Sincronizzazione client-client | Non utilizzata |
| Integrazioni terze parti | Non utilizzate |

L'assenza di messaggistica e canali real-time riflette la semplicità dell'applicazione: non serve sincronizzazione multi-dispositivo live durante il rituale di estrazione (che avviene su un telefono condiviso), né orchestrazione complessa di eventi lato server.

---

## Dettagliata

### Modello di interazione client–API

Il client opera come **consumatore** di risorse API. Cicli tipici di interazione:

**Flusso autenticazione:**
1. Il client invia credenziali all'API
2. L'API valida contro dati partecipante persistiti
3. L'API restituisce risultato autenticazione
4. Il client stabilisce contesto sessione localmente

**Registrazione esperienza (modalità Esperienze):**
1. Il client raccoglie input assistente localmente
2. Il client invia risorsa esperienza all'API
3. L'API persiste nel database
4. L'API restituisce rappresentazione esperienza creata
5. Il client aggiorna la vista lista esperienze

**Momento condiviso (modalità Box Esperienze):**
1. Il client richiede esperienze del box attivo via API
2. L'API restituisce risorse esperienza dal database
3. Il client esegue estrazione, filtro e rivelazione localmente
4. Il risultato estrazione resta sul client — nessuna scrittura verso l'API

Quest'ultimo pattern è significativo: l'estrazione è interamente lato client. L'API non partecipa al momento rituale oltre a fornire il pool di esperienze.

### Caratteristiche delle richieste

| Proprietà | Comportamento |
|-----------|---------------|
| Iniziatore | Sempre il client |
| Accoppiamento | Debole — rappresentazioni risorse, non contratti schermata |
| Stato | Sessione e contesto navigazione mantenuti sul client |
| Gestione errori | Il client mostra errori (es. snackbar su fallimento API) |
| Idempotenza | Non imposta architetturalmente a questo livello |

### Interazione API–database

L'API è l'**unica applicazione** che accede al database. Questo confine garantisce:

- Validazione coerente prima della persistenza
- Fonte unica di verità
- Nessuna frammentazione dati tra istanze client

L'API traduce operazioni REST in azioni di persistenza. ORM, layer query o connection pooling appartengono al livello Ingegneria.

### Flusso dati per operazione

| Operazione | Ruolo client | Ruolo API | Ruolo database |
|------------|--------------|-----------|----------------|
| Registrare partecipante | Invia payload registrazione | Valida e persiste | Memorizza partecipante |
| Login (Esperienze) | Invia credenziali | Valida | Legge partecipante |
| Login (Box Esperienze, multi-utente) | Invia credenziali multiple | Risolve gruppo | Legge partecipanti e gruppo |
| Creare box | Invia risorsa box | Persiste con associazione gruppo | Memorizza box |
| Elencare box | Richiede per gruppo | Interroga e restituisce | Legge box |
| Creare esperienza | Invia risorsa esperienza | Persiste con autore e box | Memorizza esperienza |
| Elencare esperienze | Richiede per box | Interroga e restituisce | Legge esperienze |
| Eliminare esperienza | Invia richiesta eliminazione | Rimuove se autorizzato | Elimina esperienza |
| Estrarre esperienza | Filtra e seleziona localmente | — (nessuna chiamata) | — |
| Cambiare lingua interfaccia | Memorizza localmente | — (nessuna chiamada) | — |

### Modello di sincronizzazione

Non esiste sincronizzazione real-time tra client. La coerenza è **eventuale via REST**:

- Quando un partecipante registra un'esperienza dal proprio telefono, altri client la vedono alla prossima lettura API
- Durante il rituale del momento condiviso su un telefono, il client aggiorna il pool esperienze dall'API prima di estrarre

Nessun meccanismo di notifica push o aggiornamento live avvisa i client quando i dati cambiano. Ogni client recupera lo stato corrente quando necessario.

### Integrazioni esterne

L'architettura attuale non ha integrazioni con servizi esterni — nessun gateway pagamenti, pipeline analytics, provider identità o CDN a livello architetturale. Se aggiunte in futuro, integrerebbero tramite API o client come nuovi percorsi di comunicazione documentati separatamente.

### Confini per livelli inferiori

Questo documento descrive *cosa* comunica e *come* a livello strutturale. Non specifica:

- Codici stato HTTP, header o schema payload
- Formato o scadenza token autenticazione
- Configurazione connessione database
- Politiche retry o circuit breaker

Questi dettagli appartengono al livello Ingegneria e Operazioni.
