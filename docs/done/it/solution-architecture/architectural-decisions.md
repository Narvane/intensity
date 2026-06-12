# Decisioni Architetturali

Questo documento registra le scelte strutturali chiave che modellano l'architettura di Intensity — la loro motivazione, i trade-off, i vincoli noti e i rischi riconosciuti.

**Pubblico:** architetti e ingegneri senior che devono comprendere *perché* la soluzione è organizzata così, senza dettagli operativi di implementazione.

---

## Breve

Intensity concentra il **nucleo del prodotto sul client mobile** e usa un'**API di risorse centralizzata** per la persistenza dei dati — deliberatamente **non** un BFF. L'architettura privilegia la **semplicità**: solo REST, nessuna messaggistica o WebSocket, istanza API unica, molte istanze client. Il trade-off è la **custodia dei dati utente** sul server. Una **modalità offline** è identificata come direzione futura per maggiore privacy.

---

## Media

### Riepilogo decisioni

| Decisione | Scelta | Motivo principale |
|-----------|--------|-------------------|
| Nucleo prodotto | Client mobile | Il valore è nell'interfaccia e nell'esperienza guidata, non nella logica di business lato server |
| Stile API | API di risorse | Centralizzazione dati senza accoppiamento a schermate specifiche |
| Non usare BFF | Rifiutato | Regole di business semplici; la struttura dell'interfaccia è il differenziatore |
| Protocollo client–server | Solo REST | Sufficiente per i pattern di interazione dell'applicazione |
| Meccanismi real-time | Non utilizzati | Nessuna necessità di sync live, push o orchestrazione eventi |
| Topologia API | Istanza unica | Adeguata alla scala attuale; semplicità sulla distribuzione |
| Custodia dati | Server centralizzato | Necessaria per registrazione individuale esperienze per partecipante |
| Funzionamento offline | Fuori ambito attuale | Considerazione futura per privacy |

### Perché il nucleo vive sul client

Intensity è un'esperienza simile a un gioco costruita attorno a una meccanica semplice: raccogliere esperienze ed estrarne una a caso. Le regole di business non sono complesse — non esistono motori di regole intricati lato server né orchestrazioni multi-step.

Il valore del prodotto sta in:

- Come l'interfaccia struttura il percorso del giocatore
- L'assistente di creazione che guida riflessione e parametrizzazione
- Il rituale del momento condiviso che trasforma un'estrazione casuale in rivelazione deliberata
- I suggerimenti predefiniti che fungono da tutorial implicito

Poiché la differenziazione è esperienziale e non computazionale, il client porta il nucleo. Il server non deve detenere logica di presentazione, orchestrazione flussi o comportamento di estrazione.

### Perché API di risorse — e non BFF

Un BFF adatterebbe le risposte API a schermate o esigenze specifiche del client. Intensity rifiuta questo perché:

- Le schermate orchestrano già i propri flussi sul client
- Le risorse di dominio mappano pulitamente su operazioni CRUD senza aggregazione
- Aggiungere uno strato BFF introdurrebbe accoppiamento tra server e struttura UI senza beneficio proporzionale
- La meccanica di estrazione e il rituale di rivelazione non toccano mai il server

Il ruolo dell'API è più ristretto e ben definito: **essere la fonte di verità dei dati persistiti**.

### Perché i dati sono centralizzati

Un'API centralizzata è **indispensabile** per il modello sociale del prodotto:

- Ogni partecipante registra esperienze **individualmente dal proprio dispositivo** (modalità Esperienze)
- Quelle esperienze devono apparire nello **stesso box** quando il gruppo gioca insieme (modalità Box Esperienze)
- Gruppi, box ed esperienze devono essere **coerenti tra tutte le istanze client**

Senza persistenza centralizzata, il contributo per partecipante non convergerebbe in un pool condiviso.

### Trade-off accettati

| Beneficio | Costo |
|-----------|-------|
| Pool esperienze condiviso tra dispositivi | Il server conserva credenziali partecipanti e dati esperienze personali |
| Architettura semplice, rapida da costruire e mantenere | Nessun gioco offline; rete necessaria per operazioni persistite |
| Autonomia client su UX e comportamento di gioco | Il client deve mappare risorse alle schermate |
| Semplicità istanza API unica | Scalare richiederà evoluzione architetturale futura |
| Semplicità REST | Nessun aggiornamento real-time; i client recuperano stato su richiesta |

### Vincoli noti

- **Dipendenza di rete:** autenticazione, registrazione e gestione esperienze richiedono API disponibile
- **Responsabilità dati:** operare un'API centralizzata implica responsabilità per la protezione dei dati dei partecipanti
- **Rituale su dispositivo unico:** l'estrazione avviene su un telefono condiviso; l'architettura non richiede sync multi-dispositivo real-time durante il gioco
- **Nessuna modalità offline:** tutte le letture e scritture di dominio passano dall'API nell'architettura attuale

---

## Dettagliata

### DA-01: Client come nucleo del prodotto

**Contesto:** Il prodotto è un'esperienza mobile simile a un gioco centrata su meccanica di estrazione su contenuto creato dagli utenti.

**Decisione:** Il client mobile detiene struttura interfaccia, navigazione, comportamento estrazione, flussi di creazione e rituale del momento condiviso. Il server non orchestra percorsi utente.

**Motivazione:**
- Le regole dell'applicazione sono essenzialmente un estrattore su un insieme raccolto — non un motore di dominio complesso
- L'interfaccia guida i giocatori a usare la creatività e creare momenti; quella guida è il valore del prodotto
- Mantenere il nucleo sul client permette di evolvere l'esperienza senza redeploy server per cambiamenti UX

**Conseguenze:**
- I release client portano cambiamenti prodotto; i cambiamenti API sono guidati da esigenze del modello dati
- L'API resta snella — validazione e persistenza, non orchestrazione business
- Il focus dei test si sposta verso comportamento e flussi client

**Alternative considerate:**
- **Flussi guidati dal server:** rifiutato — aggiunge latenza e accoppiamento senza beneficio per meccanica di estrazione semplice
- **BFF per piattaforma:** rifiutato — esiste una sola piattaforma client (mobile); nessuna necessità di aggregazione

---

### DA-02: API di risorse invece di BFF

**Contesto:** Il client necessita dati persistiti. L'API potrebbe esporre risorse di dominio o aggregati specifici di schermata.

**Decisione:** L'API espone endpoint orientati alle risorse mappati alle entità di dominio.

**Motivazione:**
- Le risorse si allineano al modello dati funzionale (partecipanti, gruppi, box, esperienze)
- CRUD mappa direttamente senza composizione vista lato server
- Il client sa già modellare i dati per ogni schermata
- Un BFF duplicherebbe conoscenza di presentazione sul server

**Conseguenze:**
- Il client esegue aggregazione o filtraggio necessario localmente (es. filtri intensità durante estrazione)
- Il versionamento API è legato al modello di dominio, non alle iterazioni UI
- Nuove schermate possono essere aggiunte senza cambiamenti API se consumano risorse esistenti

**Alternative considerate:**
- **BFF (Backend-for-Frontend):** rifiutato — il business non giustifica lo strato aggiuntivo; l'interfaccia è il valore, non la composizione lato server
- **GraphQL:** rifiutato — CRUD risorse via REST è sufficiente; nessun requisito query complesso

---

### DA-03: Dati centralizzati con istanza API unica

**Contesto:** Molteplici client (telefoni) devono condividere stato persistito. Ogni partecipante contribuisce esperienze individualmente.

**Decisione:** Un'istanza API in un ambiente server serve tutti i client. Il database dietro di essa è l'unica fonte di verità.

**Motivazione:**
- La registrazione individuale esperienze per partecipante richiede uno strato di persistenza condiviso
- Istanza unica è adeguata alla scala attesa di un gioco sociale tra coppie e gruppi di amici
- La semplicità riduce overhead operativo e di sviluppo

**Conseguenze:**
- Tutti i client dipendono dalla disponibilità API per operazioni persistite
- L'operatore assume responsabilità per la protezione dei dati utente
- Scalare oltre istanza unica è preoccupazione futura, non requisito attuale

**Alternative considerate:**
- **Solo locale / sync peer-to-peer:** rifiutato — impedirebbe contributo individuale da dispositivi separati convergendo in un box
- **API multi-istanza con bilanciamento:** non richiesta alla scala attuale; aggiunge complessità senza beneficio immediato

---

### DA-04: Solo REST — nessuna messaggistica o WebSocket

**Contesto:** Client e API devono comunicare. Sono disponibili molteplici protocolli e pattern.

**Decisione:** REST (HTTP richiesta/risposta) è l'unico meccanismo di comunicazione client–server. Nessuna coda messaggi, bus eventi o canale WebSocket.

**Motivazione:**
- Le operazioni persistite sono eventi CRUD discreti, non flussi continui
- Il rituale di estrazione gira su un telefono condiviso — nessuna necessità di sincronizzazione multi-dispositivo live
- Nessuna notifica avviata dal server è richiesta nel prodotto attuale
- La semplicità si allinea all'ambito dell'applicazione

**Conseguenze:**
- I client recuperano dati freschi quando necessario; nessun aggiornamento basato su push
- I fallimenti API sono gestiti per richiesta sul client (es. snackbar errore)
- Aggiungere funzionalità real-time in futuro richiederà rivedere questa decisione

**Alternative considerate:**
- **WebSocket per aggiornamenti live:** rifiutato — nessuno scenario prodotto richiede push server durante il gioco
- **Coda messaggi per elaborazione asincrona:** rifiutato — nessuna elaborazione in background o flussi event-driven nel dominio attuale

---

### DA-05: Semplicità sulla complessità architetturale

**Contesto:** Intensity è un prodotto focalizzato con dominio ristretto e meccanica centrale semplice.

**Decisione:** L'architettura consiste in esattamente due artefatti applicativi (client + API) e un artefatto di persistenza (database), connessi da REST.

**Motivazione:**
- Il prodotto non è una piattaforma enterprise con esigenze di integrazione complesse
- L'over-engineering rallenterebbe lo sviluppo senza migliorare l'esperienza del giocatore
- Il problema risolto è esperienziale, non computazionale

**Conseguenze:**
- Meno parti mobili da deployare, monitorare e mantenere
- Confini di responsabilità chiari tra client e API
- Alcune esigenze future (offline, scala, analytics) richiederanno evoluzione architetturale esplicita

---

### DA-06: Trade-off custodia dati (rischio accettato)

**Contesto:** Centralizzare i dati abilita il modello di contributo sociale ma colloca informazioni partecipante sul server.

**Decisione:** Accettare custodia dati lato server come trade-off necessario per persistenza centralizzata.

**Motivazione:**
- La registrazione individuale esperienze da dispositivi separati richiede archivio condiviso
- Credenziali partecipanti e contenuto esperienze devono persistere in un luogo accessibile a tutti i client
- Il prodotto non completa il proprio ciclo centrale senza questa centralizzazione

**Conseguenze:**
- L'operatore deve gestire i dati con responsabilità — protezione, controllo accesso e considerazioni conformità
- I partecipanti affidano al servizio le proprie credenziali e contenuto creativo
- Giocatori sensibili alla privacy possono preferire alternative

**Direzione mitigazione (futuro, fuori ambito attuale):**
- Una **modalità offline** è stata identificata come possibilità futura per offrire maggiore privacy ai giocatori
- Sarebbe evoluzione architetturale significativa che impatterebbe client, API e modello di sincronizzazione
- Non fa parte dell'architettura attuale

---

### Rischi e percorsi di evoluzione

| Rischio | Stato attuale | Possibile evoluzione |
|---------|---------------|----------------------|
| Indisponibilità API blocca operazioni persistite | Accettato | Modalità offline con archivio locale e sync |
| Istanza API unica limita scala | Accettato | Scalatura orizzontale dietro load balancer |
| Nessun refresh dati real-time tra client | Accettato | Notifiche push o ottimizzazione polling |
| Preoccupazioni privacy dati centralizzati | Riconosciuto | Modalità offline; politiche minimizzazione dati |
| Impatto violazione dati server | Riconosciuto | Pratiche sicurezza nel livello Ingegneria |

### Cosa queste decisioni non coprono

Scelte tecnologiche (linguaggi, framework, engine database), procedure deploy, CI/CD, monitoraggio e implementazione sicurezza appartengono al livello **Ingegneria e Operazioni**. Questo documento tratta solo l'organizzazione strutturale e la sua motivazione.
