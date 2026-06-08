# Modello Dati

Questo documento descrive il modello dati funzionale di Intensity — le entità di dominio, le loro relazioni, le tassonomie, i parametri e i contenuti predefiniti che strutturano il funzionamento del prodotto. Specifica *cosa esiste* e *come i concetti si relazionano*, senza dettagli di implementazione.

**Pubblico:** analisti, product owner, designer e QA funzionale — persone che devono comprendere il dominio della soluzione senza sapere come è stata costruita.

---

## Breve

Il dominio di Intensity ruota attorno ai **partecipanti** che formano **gruppi**, raccolgono **esperienze** in **scatole** tematiche e vivono insieme il momento di **estrazione e rivelazione**. Ogni esperienza porta una descrizione, un livello di **intensità** complessivo (1–5), tre **parametri** (sforzo, apertura, novità) e la **riflessione** del proponente. Undici **tipi di scatola** organizzano le idee per contesto; due **modalità di accesso** (Esperienze e Scatola delle Esperienze) definiscono chi può fare cosa. La registrazione è controllata da una **lista di autorizzazione**; i risultati dell'estrazione sono transitori e non vengono memorizzati.

---

## Media

### Entità centrali

| Entità | Cosa rappresenta |
|--------|------------------|
| **Partecipante** | Persona registrata (nome visualizzato, email, credenziali) che può contribuire e unirsi ai gruppi |
| **Voce nella lista di autorizzazione** | Email pre-approvata autorizzata a registrarsi |
| **Gruppo** | Insieme di partecipanti entrati insieme in modalità Scatola delle Esperienze — identificato da quella combinazione esatta |
| **Scatola** | Contenitore nominato e tematico dove vengono raccolte le esperienze di un gruppo |
| **Esperienza** | Idea concreta da fare insieme, autoria di un partecipante, appartenente a una scatola |
| **Contesto di sessione** | Ambito operativo dell'uso corrente: modalità di accesso, gruppo attivo, scatola attiva |

### Come si collegano

```
Lista di autorizzazione  →  autorizza  →  Partecipante
Partecipante  ↔  Gruppo  (molti-a-molti, tramite chi entra insieme)
Gruppo  →  possiede  →  Scatola  (uno-a-molti)
Scatola  →  contiene  →  Esperienza  (uno-a-molti)
Partecipante  →  autore  →  Esperienza  (uno-a-molti)
```

Un **gruppo** non viene nominato manualmente — emerge dalla combinazione unica di partecipanti che si autenticano insieme in modalità Scatola delle Esperienze. Le **scatole** si creano solo in quella modalità; le **esperienze** si registrano principalmente tramite la modalità Esperienze.

### Cosa porta ogni esperienza

| Attributo | Significato |
|-----------|-------------|
| Descrizione | Testo dell'esperienza (fino a 1.000 caratteri) |
| Intensità | Audacia complessiva, livelli 1–5 |
| Parametri | Tre valutazioni 1–5: sforzo, apertura, novità |
| Riflessione | Giustificazione del proponente sull'accettazione del gruppo |
| Autore | Chi l'ha registrata |
| Momento di registrazione | Quando è stata salvata |
| Sigillo di integrità | Impronta mostrata sulle card come "Sigillo" |
| Scatola padre | A quale scatola appartiene |

### Tassonomie in sintesi

- **Modalità di accesso:** Esperienze (contributo individuale) e Scatola delle Esperienze (rituale di gruppo)
- **Tipi di scatola:** 11 categorie tematiche (uscite, viaggi, intimità, rottura routine, novità, disagio, connessione e altro — per coppie e amici)
- **Livelli di intensità:** 1 Leggera → 5 Adrenalina
- **Filtri di estrazione:** Qualsiasi, intensità fissa o intensità massima
- **Passi della procedura guidata:** Suggerimento → Riflessione → Parametri → Classificazione → Biforcazione

### Parametri e predefiniti

- La registrazione richiede un'email nella lista di autorizzazione
- Tipo di scatola predefinito se non specificato: **Uscite con amici**
- Intensità predefinita nella procedura guidata e nel filtro di estrazione: **3**
- Intensità suggerita: media arrotondata delle tre valutazioni dei parametri (il proponente può modificarla)
- **165 suggerimenti di esempio incorporati** (11 tipi × 5 livelli × 3 ciascuno), attualmente in portoghese per tutte le lingue dell'interfaccia
- **Conseguenze, scambi e progressione graduale** esistono solo come guida sociale — non come dati tracciati

---

## Dettagliata

### Partecipante e registrazione

Un **partecipante** è chi ha completato la registrazione. Ha un **nome visualizzato** (mostrato nelle liste di gruppo), un'**email** (identità di accesso) e **credenziali** (email + password).

Prima di diventare partecipante, l'email deve comparire nella **lista di autorizzazione alla registrazione**. È un controllo amministrativo — non un concetto gestito dall'utente nell'app, ma definisce chi può entrare. Voci di esempio includono `proponente@intensity.app`, `membro1@intensity.app` e `membro2@intensity.app`.

I partecipanti registrati compaiono nell'interfaccia di accesso della Scatola delle Esperienze così il gruppo può selezionare chi è presente.

**Non modellato:** foto profilo, preferenze di notifica o impostazioni per utente oltre a ciò che il client memorizza localmente (come la lingua dell'interfaccia).

### Gruppo

Un **gruppo** è l'insieme di persone entrate insieme in modalità **Scatola delle Esperienze**. Ha:

| Attributo | Significato |
|-----------|-------------|
| Partecipanti | Membri di questo gruppo |
| Momento di creazione | Quando questa combinazione è stata formata per la prima volta |

**Regola di identità:** la stessa combinazione di partecipanti corrisponde sempre allo stesso gruppo. Se Alice e Bob entrano insieme, formano un gruppo; se Alice, Bob e Carol entrano, è un gruppo diverso. Un partecipante può appartenere a più gruppi a seconda delle combinazioni di ogni sessione.

**Non modellato:** nome visibile all'utente, modifica del gruppo o creazione esplicita al di fuori della combinazione di accesso.

### Scatola

Una **scatola** è un contenitore tematico dove vengono raccolte le esperienze di un gruppo.

| Attributo | Significato |
|-----------|-------------|
| Nome | Etichetta scelta dall'utente (es. "Festa di sabato") |
| Tipo | Una delle 11 categorie tematiche |
| Momento di creazione | Quando la scatola è stata creata |
| Gruppo padre | Quale gruppo la possiede |

Le scatole si creano in modalità **Scatola delle Esperienze**, non durante il contributo individuale. Ogni tipo porta metadati di presentazione (colore di accento, icona, suggerimento nel sottotitolo) che orientano il tipo di idee attese.

**Non modellato:** rinominare, modificare o eliminare la scatola.

### Esperienza

Un'**esperienza** è un'idea concreta da fare insieme.

| Attributo | Vincolo / regola |
|-----------|------------------|
| Descrizione | Obbligatoria; max 1.000 caratteri |
| Intensità | Obbligatoria; intero 1–5 |
| Sforzo | Obbligatorio; 1–5 stelle |
| Apertura | Obbligatorio; 1–5 stelle |
| Novità | Obbligatorio; 1–5 stelle |
| Riflessione | Obbligatoria nel flusso attuale; max 2.000 caratteri per campo |
| Autore | Registrato alla creazione; solo l'autore può modificare o eliminare |
| Sigillo di integrità | Derivato dalla descrizione; mostrato sulle card |

#### Regole di visibilità

| Contesto | Cosa vedono gli altri |
|----------|----------------------|
| **Modalità Esperienze** (lista dell'autore) | Descrizione completa delle proprie esperienze; gli altri vedono solo il riepilogo (intensità + sigillo, senza testo) |
| **Modalità Scatola delle Esperienze** (estrazione) | Intensità e parametri prima; descrizione completa solo dopo la **Rivelazione** |

L'interfaccia informa anche che **le esperienze non sono crittografate** — avviso di trasparenza su come viene gestito il testo.

#### Riflessione

Il modello funzionale supporta tre campi di riflessione:

| Campo | Comportamento attuale |
|-------|----------------------|
| "Tutti accetterebbero questa esperienza, per quanto insolita?" | **Raccolto** nella procedura guidata di creazione |
| "Coinvolge tutti?" | Supportato nel modello dati; **non raccolto** nell'interfaccia attuale |
| "C'è un lieve disagio?" | Supportato nel modello dati; **non raccolto** nell'interfaccia attuale |

Quando un solo campo è popolato, le card mostrano quella domanda unica; quando più campi sono popolati, compaiono tutti i blocchi.

### Contesto di sessione

Sebbene non sia un'entità gestita dall'utente, il **contesto di sessione** delimita ogni operazione:

| Elemento | Valori |
|----------|--------|
| Modalità di accesso | **Esperienze** o **Scatola delle Esperienze** |
| Gruppo attivo | Selezionato o formato all'accesso |
| Scatola attiva | Scatola selezionata (in modalità Esperienze) |
| Tipo di scatola | Tipo della scatola attiva (guida suggerimenti e tema) |

| Modalità | Chi entra | Operazioni di dominio |
|----------|-----------|----------------------|
| **Esperienze** | Un partecipante | Registrare, modificare, eliminare esperienze; scegliere gruppo e scatola |
| **Scatola delle Esperienze** | Più partecipanti insieme | Formare gruppo, creare scatole, navigare, estrarre, rivelare |

### Risultato dell'estrazione (transitorio)

Un'**estrazione** seleziona casualmente un'esperienza da una scatola. **Non viene persistita** — ogni attivazione crea una nuova selezione.

| Elemento | Significato |
|----------|-------------|
| Esperienza selezionata | Un'esperienza dalla scatola, filtrata se richiesto |
| Filtro applicato | Qualsiasi, intensità fissa o intensità massima |
| Stato di rivelazione | Se la descrizione completa è stata mostrata |

**Non modellato:** cronologia estrazioni, eventi di rivelazione, stato di completamento o pratiche sociali (conseguenze, scambi).

---

### Panoramica delle relazioni

```
                    ┌─────────────────────┐
                    │ Voce nella lista    │
                    │  di autorizzazione  │
                    └──────────┬──────────┘
                               │ autorizza
                               ▼
┌──────────────┐      ┌────────────────┐      ┌──────────────┐
│ Partecipante │◄────►│     Gruppo     │─────►│    Scatola   │
└──────┬───────┘      └────────────────┘      └──────┬───────┘
       │                                              │
       │ autore                                       │ contiene
       ▼                                              ▼
              ┌──────────────────────────────────────────┐
              │              Esperienza                    │
              └──────────────────────────────────────────┘
```

---

### Tassonomie

#### Modalità di accesso

| Etichetta all'utente | Ambito funzionale |
|----------------------|-------------------|
| **Esperienze** | Contributo individuale: registrare e gestire esperienze |
| **Scatola delle Esperienze** | Rituale di gruppo: creare scatole, estrarre, rivelare |

#### Tipi di scatola (11 categorie)

Predefinito: **Uscite con amici**

| Tipo | Suggerimento sottotitolo (IT) |
|------|-------------------------------|
| Uscite con amici | Uscite leggere o intense in gruppo |
| Uscite in coppia | Caffè, passeggiate e appuntamenti per due |
| Viaggi in coppia | Fughe e destinazioni per due |
| Intimità in coppia | Connessione e conversazioni più profonde |
| Viaggi con amici | Gite, weekend o viaggi pianificati |
| Esperienze con amici | Corsi, tour ed esperienze di gruppo |
| Rompi la routine | Piccole rotture della routine quotidiana |
| Prime volte | Provare cose nuove con calma |
| Leggero disagio | Uscire un po' dalla zona di comfort con cura |
| Momenti di connessione | Presenza, ascolto e legame di gruppo |
| Esperienze diverse | Cose insolite per il gruppo |

Il catalogo raggruppa i tipi in sezioni di presentazione (amici, coppia, personale, sociale), ma l'interfaccia di creazione mostra un elenco piatto senza etichette di sezione.

#### Livelli di intensità (1–5)

| Livello | Etichetta |
|---------|-----------|
| 1 | Leggera |
| 2 | Scomodo |
| 3 | Coraggio |
| 4 | Audace |
| 5 | Adrenalina |

#### Parametri dell'esperienza

Ogni dimensione è valutata da 1–5 con livelli verbali definiti:

| Dimensione | Domanda al proponente |
|------------|----------------------|
| **Sforzo** | Quanto è impegnativo farlo? |
| **Apertura** | Quanta esposizione gentile o sincerità richiede? |
| **Novità** | Quanto è diverso da ciò che fate di solito insieme? |

**Intensità suggerita:** il sistema propone un livello in base alla media arrotondata delle tre valutazioni; il proponente può modificarla nel passo di Classificazione.

#### Filtri di estrazione

| Filtro | Comportamento |
|--------|---------------|
| **Qualsiasi** | Estrae da tutte le esperienze della scatola |
| **Intensità fissa** | Solo esperienze esattamente al livello N |
| **Intensità massima** | Esperienze al livello N o inferiore |

Livello predefinito del filtro nell'interfaccia: **3**.

#### Passi della procedura guidata

| Passo | Scopo |
|-------|-------|
| 1 – Suggerimento | Descrivere un'idea o scegliere un esempio |
| 2 – Riflessione | Giustificare se tutti accetterebbero |
| 3 – Parametri | Valutare sforzo, apertura, novità |
| 4 – Classificazione | Impostare intensità finale (con suggerimento) |
| 5 – Biforcazione | Rivedere, salvare, eventualmente crearne un'altra |

#### Lingua dell'interfaccia (preferenza del client)

| Codice | Lingua |
|--------|--------|
| `pt` | Portoghese (predefinito) |
| `en` | Inglese |
| `it` | Italiano |

Memorizzata sul client; non fa parte del modello di dominio persistito.

---

### Parametri, vincoli e configurazioni

| Parametro | Valore / regola |
|-----------|-----------------|
| Lunghezza max descrizione | 1.000 caratteri |
| Lunghezza max riflessione | 2.000 caratteri per campo |
| Intervallo intensità | 1–5 (obbligatorio) |
| Valutazioni parametri | 1–5 ciascuna, tutte e tre obbligatorie |
| Tipo di scatola predefinito | Uscite con amici |
| Intensità predefinita (procedura guidata e filtro) | 3 |
| Modifica/eliminazione solo dall'autore | Solo l'autore dell'esperienza può modificarla o rimuoverla |
| Registrazione con lista di autorizzazione | L'email deve essere nella lista prima della registrazione |

---

### Contenuti predefiniti e incorporati

#### Lista di autorizzazione alla registrazione (esempi seed)

- `proponente@intensity.app`
- `membro1@intensity.app`
- `membro2@intensity.app`

#### Pacchetti di suggerimenti

Il client incorpora **165 esperienze di esempio**: 11 tipi di scatola × 5 livelli di intensità × 3 suggerimenti ciascuno. Toccando un esempio si popola il campo descrizione (modificabile in ogni fase).

**Lacuna di localizzazione:** il testo dei suggerimenti è attualmente **solo in portoghese**, indipendentemente dalla lingua dell'interfaccia.

#### Onboarding e guida rapida (solo client)

- Narrazione onboarding in quattro passi (problema → momenti insoliti → agire → raccogliere/estrarre/vivere)
- Sezioni della guida rapida: regola centrale, flusso consigliato, suggerimenti intensità, pratiche sociali

Sono contenuti di presentazione, non entità di dominio.

#### Pratiche sociali (solo guida)

La guida rapida e il documento dei principi raccomandano pratiche che **non hanno entità corrispondenti**:

- Definire una **conseguenza** prima di rivelare
- **Scambiare** esperienze di intensità diverse
- **Progressione graduale** dell'intensità nel tempo

---

### Terminologia canonica

| Usare nella Camada 2 | Evitare |
|----------------------|---------|
| Partecipante | Tabella utenti, nomi di classi |
| Voce nella lista di autorizzazione | Tabella email consentite |
| Gruppo | Fingerprint, ID gruppo |
| Scatola | Tabella scatole esperienza |
| Esperienza | Cifratura descrizione, riga |
| Parametri (sforzo / apertura / novità) | Nomi colonne stelle |
| Riflessione | Cifratura informazioni aggiuntive |
| Sigillo di integrità | Campo hash descrizione |
| Modalità Esperienze / Scatola delle Esperienze | Codici interni di modalità |
| Proponente | Etichette interne di ruolo |

---

## Lacune e limitazioni

| Argomento | Stato |
|-----------|-------|
| Modello riflessione vs interfaccia | Tre campi supportati; solo uno raccolto oggi |
| Ciclo di vita della scatola | Rinominare, modificare o eliminare non osservati |
| Denominazione del gruppo | Nessun nome visibile — solo lista partecipanti |
| Persistenza estrazione | Estrazioni ed eventi di rivelazione non memorizzati |
| Pratiche sociali | Conseguenze, scambi, progressione — solo guida |
| Localizzazione suggerimenti | Testo in portoghese servito per tutte le lingue |
| Profilo partecipante | Nessun avatar, preferenze o notifiche oltre nome/email |
| Sezioni tipo di scatola | Sezioni esistono nel catalogo; interfaccia mostra elenco piatto |
