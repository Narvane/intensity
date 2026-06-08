# Modello Dati

Questo documento descrive il modello dati funzionale di Intensity — le entità di dominio, le loro relazioni, le tassonomie, i parametri e i contenuti predefiniti che strutturano il funzionamento del prodotto. Specifica *cosa esiste* e *come i concetti si relazionano*, senza dettagli di implementazione.

**Pubblico:** analisti, product owner, designer e QA funzionale — persone che devono comprendere il dominio della soluzione senza sapere come è stata costruita.

---

## Breve

Il dominio di Intensity ruota attorno ai **partecipanti** che formano **gruppi**, raccolgono **esperienze** in **scatole** tematiche e vivono insieme il momento di **estrazione e rivelazione**. I **suggerimenti predefiniti per tipo di scatola** orientano la creazione delle esperienze — fungono da tutorial implicito e cambiano in base al tipo di scatola attivo. Ogni esperienza porta una descrizione, un livello di **intensità** complessivo (1–5), tre **parametri** (sforzo, apertura, novità) e la **riflessione** del proponente. Undici **tipi di scatola** organizzano le idee per contesto; due **modalità di accesso** (Esperienze e Scatola delle Esperienze) definiscono chi può fare cosa. La registrazione è controllata da una **lista di autorizzazione**; i risultati dell'estrazione sono transitori e non vengono memorizzati.

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
- **Suggerimenti predefiniti:** pacchetti incorporati per tipo di scatola, mostrati nella fase Suggerimento della procedura; fungono da tutorial implicito di creazione

### Parametri e predefiniti

- La registrazione richiede un'email nella lista di autorizzazione
- Tipo di scatola predefinito se non specificato: **Uscite con amici**
- Intensità predefinita nella procedura guidata e nel filtro di estrazione: **3**
- Intensità suggerita: media arrotondata delle tre valutazioni dei parametri (il proponente può modificarla)
- **165 suggerimenti predefiniti per tipo di scatola** (11 tipi × 5 livelli × 3 ciascuno), con testi localizzati in portoghese, inglese e italiano
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

#### Suggerimenti predefiniti per tipo di scatola

I suggerimenti predefiniti sono **contenuto incorporato essenziale** di Intensity. Non sono decorazione opzionale: fungono da **tutorial implicito** su come creare buone esperienze — tono, granularità, audacia a ogni livello di intensità e coerenza con il tema della scatola. Ogni suggerimento deve essere ben pensato; esempi deboli insegnerebbero schemi sbagliati ai proponenti.

**Come funzionano nel prodotto:**

- Nella fase **Suggerimento** della procedura guidata di creazione, il proponente vede esempi raggruppati per **livello di intensità** (1–5).
- L'insieme di esempi proviene dal **tipo di scatola attivo** — il tipo della scatola in cui l'esperienza sarà salvata (contesto di sessione in modalità Esperienze).
- Creare una scatola con un dato tipo non copia i suggerimenti al suo interno; quel tipo **seleziona quale pacchetto di suggerimenti** viene offerto al momento di creare esperienze.
- Toccando un suggerimento si popola il campo descrizione (modificabile in ogni fase della procedura).
- Struttura: **11 tipi di scatola × 5 livelli di intensità × 3 suggerimenti = 165** esempi incorporati.

**Fonte nel sistema:**

- Struttura dei pacchetti e chiavi i18n: `client/mobile-app/src/commonMain/kotlin/com/intensity/mobile/app/ui/experience/ExperienceBoxSuggestionPacks.kt`
- Testi canonici per lingua:
  - Portoghese: `client/mobile-app/src/commonMain/kotlin/com/intensity/mobile/app/platform/i18n/dictionaries/SuggestionPacksPt.kt`
  - Inglese: `client/mobile-app/src/commonMain/kotlin/com/intensity/mobile/app/platform/i18n/dictionaries/SuggestionPacksEn.kt`
  - Italiano: `client/mobile-app/src/commonMain/kotlin/com/intensity/mobile/app/platform/i18n/dictionaries/SuggestionPacksIt.kt`

**Localizzazione:** i testi dei suggerimenti vengono serviti nella lingua dell'interfaccia selezionata (portoghese, inglese o italiano). Il catalogo seguente elenca i valori incorporati in **italiano**.

##### Uscite con amici (`outings_friends`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Ordinare da mangiare insieme e mangiare guardando qualcosa a casa di qualcuno |
| 1 - Light | 2 | Provare insieme una dinamica semplice in una serata tranquilla |
| 1 - Light | 3 | Trovarsi solo per chiacchierare con calma a casa |
| 2 - Uncomfortable | 1 | Andare in un bar o caffe diverso di sera |
| 2 - Uncomfortable | 2 | Fare una grigliata a casa di qualcuno un sabato pomeriggio |
| 2 - Uncomfortable | 3 | Uscire a cena in un posto dove nessuno del gruppo e ancora stato |
| 3 - Courage | 1 | Andare a fare karaoke o bowling in gruppo |
| 3 - Courage | 2 | Passare un'intera giornata insieme in un posto della citta |
| 3 - Courage | 3 | Fare un'uscita che di solito non e nello stile del gruppo |
| 4 - Bold | 1 | Organizzare una serata a tema (es. cucina messicana, italiana, ecc.) |
| 4 - Bold | 2 | Pianificare una giornata intera con varie attivita diverse |
| 4 - Bold | 3 | Organizzare un incontro con piu persone oltre al gruppo abituale |
| 5 - Adrenaline | 1 | Fare un viaggio di gruppo in una citta vicina nel weekend |
| 5 - Adrenaline | 2 | Affittare un posto per passare un weekend insieme |
| 5 - Adrenaline | 3 | Fare un'uscita totalmente fuori dal comune per il gruppo |

##### Uscite in coppia (`outings_couple`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Prendere un caffe insieme in un posto nuovo |
| 1 - Light | 2 | Fare una passeggiata parlando senza usare il cellulare |
| 1 - Light | 3 | Guardare insieme un film scelto sul momento |
| 2 - Uncomfortable | 1 | Cenare in un ristorante dove non siete mai stati |
| 2 - Uncomfortable | 2 | Fare un picnic in un parco della citta |
| 2 - Uncomfortable | 3 | Uscire la sera senza pianificare la meta |
| 3 - Courage | 1 | Passare un'intera giornata insieme fuori casa |
| 3 - Courage | 2 | Fare un'attivita che piace a uno e che l'altro di solito non sceglierebbe |
| 3 - Courage | 3 | Andare insieme a un evento (concerto, fiera, ecc.) |
| 4 - Bold | 1 | Passare un weekend fuori citta |
| 4 - Bold | 2 | Pianificare una giornata a sorpresa per il partner |
| 4 - Bold | 3 | Organizzare una serata a tema a casa |
| 5 - Adrenaline | 1 | Viaggiare insieme verso una meta di cui avete gia parlato |
| 5 - Adrenaline | 2 | Fare un'esperienza memorabile insieme (sentiero, uscita diversa) |
| 5 - Adrenaline | 3 | Passare un'intera giornata disconnessi (senza cellulare) |

##### Viaggi con amici (`trips_friends`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Parlare di un viaggio che nominate sempre |
| 1 - Light | 2 | Scegliere una meta che tutti trovano interessante |
| 1 - Light | 3 | Preparare insieme un itinerario semplice |
| 2 - Uncomfortable | 1 | Fare una gita in giornata in una citta vicina |
| 2 - Uncomfortable | 2 | Passare una giornata turistica in un'altra citta |
| 2 - Uncomfortable | 3 | Scoprire un posto nuovo nella vostra zona |
| 3 - Courage | 1 | Pianificare un vero viaggio di gruppo |
| 3 - Courage | 2 | Passare un weekend fuori insieme |
| 3 - Courage | 3 | Dividere i compiti per organizzare il viaggio |
| 4 - Bold | 1 | Viaggiare verso una meta che nessuno del gruppo conosce |
| 4 - Bold | 2 | Pianificare un viaggio con attivita diverse (natura, cultura, ecc.) |
| 4 - Bold | 3 | Fare un viaggio di gruppo piu strutturato |
| 5 - Adrenaline | 1 | Fare un viaggio lungo insieme |
| 5 - Adrenaline | 2 | Viaggiare all'estero in gruppo |
| 5 - Adrenaline | 3 | Fare un'esperienza memorabile durante il viaggio |

##### Viaggi in coppia (`trips_couple`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Elencare tre destinazioni che andrebbero bene a entrambi |
| 1 - Light | 2 | Guardare insieme foto di un vecchio viaggio e ricordare |
| 1 - Light | 3 | Cercare voli solo per curiosita, senza impegno |
| 2 - Uncomfortable | 1 | Prenotare un weekend in una struttura vicina |
| 2 - Uncomfortable | 2 | Creare un itinerario di un giorno con tappe scelte da entrambi |
| 2 - Uncomfortable | 3 | Andare insieme per la prima volta in un museo o in una citta vicina |
| 3 - Courage | 1 | Pianificare un viaggio di 3-5 giorni con budget condiviso |
| 3 - Courage | 2 | Provare un tipo di alloggio diverso dal solito |
| 3 - Courage | 3 | Viaggiare senza itinerario fisso, con solo la destinazione definita |
| 4 - Bold | 1 | Fare un viaggio con una sfida leggera (sentiero, on the road, ecc.) |
| 4 - Bold | 2 | Organizzare un viaggio parzialmente a sorpresa (solo una parte e segreta) |
| 4 - Bold | 3 | Tornare in una meta preferita ma con itinerario nuovo |
| 5 - Adrenaline | 1 | Pianificare insieme un viaggio internazionale |
| 5 - Adrenaline | 2 | Fare un viaggio lungo con disconnessione quasi totale |
| 5 - Adrenaline | 3 | Programmare un'esperienza fuori dalla zona di comfort, concordata da entrambi |

##### Intimita in coppia (`intimate_couple`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Avere una conversazione piu profonda su qualcosa di leggero |
| 1 - Light | 2 | Ricordare momenti importanti della relazione |
| 1 - Light | 3 | Condividere qualcosa mai detto prima (leggero) |
| 2 - Uncomfortable | 1 | Creare un momento romantico pianificato a casa |
| 2 - Uncomfortable | 2 | Scrivere qualcosa di significativo l'uno per l'altro |
| 2 - Uncomfortable | 3 | Creare un momento speciale fuori dalla routine |
| 3 - Courage | 1 | Avere una conversazione che state evitando |
| 3 - Courage | 2 | Provare qualcosa di nuovo insieme nella relazione |
| 3 - Courage | 3 | Uscire dalla routine emotiva della coppia |
| 4 - Bold | 1 | Aprirsi emotivamente su qualcosa di importante |
| 4 - Bold | 2 | Esplorare qualcosa di nuovo nella relazione con piu profondita |
| 4 - Bold | 3 | Creare un momento totalmente fuori dallo schema abituale |
| 5 - Adrenaline | 1 | Avere una conversazione trasformativa sulla relazione |
| 5 - Adrenaline | 2 | Fare qualcosa che richiede alta vulnerabilita |
| 5 - Adrenaline | 3 | Creare un'esperienza memorabile per la coppia |

##### Esperienze con amici (`experiences_friends`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Provare insieme uno snack di un posto nuovo |
| 1 - Light | 2 | Fare una breve uscita che nessuno ha mai fatto |
| 1 - Light | 3 | Scambiare idee su esperienze fuori dalla routine |
| 2 - Uncomfortable | 1 | Andare a un workshop o a una lezione breve in gruppo (cucina, danza, ecc.) |
| 2 - Uncomfortable | 2 | Fare un tour guidato o una visita culturale diversa |
| 2 - Uncomfortable | 3 | Provare un'attivita all'aperto leggera |
| 3 - Courage | 1 | Prenotare un'esperienza che richiede un po' piu coraggio in gruppo |
| 3 - Courage | 2 | Pianificare una giornata con due nuove esperienze consecutive |
| 3 - Courage | 3 | Invitare una persona esterna per un'esperienza con il gruppo |
| 4 - Bold | 1 | Fare un'attivita intensa in gruppo (discesa in corda, rafting, ecc.) se tutti accettano |
| 4 - Bold | 2 | Organizzare un evento a tema con esperienze inedite |
| 4 - Bold | 3 | Passare un'intera giornata provando cose nuove in citta |
| 5 - Adrenaline | 1 | Pianificare un'esperienza memorabile di gruppo fuori citta |
| 5 - Adrenaline | 2 | Concordare qualcosa che il gruppo non farebbe mai da solo |
| 5 - Adrenaline | 3 | Creare un rituale di gruppo da ripetere dopo un'esperienza forte |

##### Rompi la routine (`break_routine`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Cambiare il posto in cui fai colazione o pranzo durante la settimana |
| 1 - Light | 2 | Camminare in un quartiere che visiti raramente |
| 1 - Light | 3 | Fare un'attivita di routine in un altro orario o con musica diversa |
| 2 - Uncomfortable | 1 | Andare al cinema o a teatro da soli o con qualcuno in un giorno insolito |
| 2 - Uncomfortable | 2 | Provare un hobby economico per un pomeriggio |
| 2 - Uncomfortable | 3 | Cambiare il tragitto casa-lavoro per un giorno |
| 3 - Courage | 1 | Fissare un appuntamento con te stesso: museo, parco, libreria |
| 3 - Courage | 2 | Fare qualcosa che rimandi da mesi per pigrizia |
| 3 - Courage | 3 | Invitare qualcuno a rompere la routine insieme |
| 4 - Bold | 1 | Pianificare una giornata off-grid o quasi senza schermi |
| 4 - Bold | 2 | Fare una mini gita da solo andata e ritorno nello stesso giorno |
| 4 - Bold | 3 | Assumerti un'esperienza personale in pubblico (corsa, corso, ecc.) |
| 5 - Adrenaline | 1 | Cambiare un aspetto strutturale della routine per una settimana (sonno, lavoro, ecc.) |
| 5 - Adrenaline | 2 | Fare un'esperienza che spaventa un po' ma ti attrae |
| 5 - Adrenaline | 3 | Condividere con il gruppo un piano per uscire dalla zona di comfort |

##### Prime volte (`first_times`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Annotare tre piccole prime volte per la settimana |
| 1 - Light | 2 | Provare un ingrediente o una ricetta mai fatta |
| 1 - Light | 3 | Ascoltare un genere musicale che di solito eviti |
| 2 - Uncomfortable | 1 | Andare da solo a un evento dove non conosci nessuno |
| 2 - Uncomfortable | 2 | Provare una nuova modalita di allenamento |
| 2 - Uncomfortable | 3 | Provare uno sport o una dinamica che il gruppo non ha mai fatto |
| 3 - Courage | 1 | Fare qualcosa di artistico per la prima volta (lezione, open mic, ecc.) |
| 3 - Courage | 2 | Guidare o prendere i mezzi pubblici verso un posto nuovo per te |
| 3 - Courage | 3 | Chiedere aiuto in qualcosa che fai sempre da solo |
| 4 - Bold | 1 | Programmare una prima volta che coinvolga una leggera vulnerabilita |
| 4 - Bold | 2 | Fare un pernottamento o un breve viaggio inedito |
| 4 - Bold | 3 | Condividere con il gruppo una prima volta che ti ha fatto paura |
| 5 - Adrenaline | 1 | Pianificare una prima volta che tocchi identita o una paura reale |
| 5 - Adrenaline | 2 | Concordare con il gruppo un'esperienza inedita per tutti |
| 5 - Adrenaline | 3 | Registrare e celebrare una prima volta memorabile |

##### Leggero disagio (`light_discomfort`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Dire no a qualcosa di piccolo che accetti sempre per educazione |
| 1 - Light | 2 | Fare una camminata un po' piu lunga del solito |
| 1 - Light | 3 | Provare un abbigliamento o uno stile fuori dal tuo schema abituale |
| 2 - Uncomfortable | 1 | Partecipare a un cerchio di conversazione sincero su un tema un po' scomodo |
| 2 - Uncomfortable | 2 | Fare un'attivita di gruppo in cui non sei il migliore |
| 2 - Uncomfortable | 3 | Chiedere un feedback sincero a una persona vicina |
| 3 - Courage | 1 | Proporre al gruppo qualcosa che generi un po' di imbarazzo sano |
| 3 - Courage | 2 | Restare in silenzio o in meditazione guidata piu a lungo del confortevole |
| 3 - Courage | 3 | Assumere un ruolo diverso in una dinamica di gruppo |
| 4 - Bold | 1 | Concordare un'esperienza fisica o sociale leggera fuori dalla zona di comfort |
| 4 - Bold | 2 | Parlare con calma di un limite personale nel gruppo |
| 4 - Bold | 3 | Fare una dinamica di improvvisazione o teatro con il gruppo |
| 5 - Adrenaline | 1 | Pianificare un'esperienza che mescoli divertimento e leggero disagio condiviso |
| 5 - Adrenaline | 2 | Riaffrontare un tema evitato con il supporto del gruppo |
| 5 - Adrenaline | 3 | Celebrare il coraggio dopo un momento scomodo vissuto bene |

##### Momenti di connessione (`connection_moments`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Fare un giro rapido: un complimento sincero per ogni persona |
| 1 - Light | 2 | Raccontare un bel ricordo che coinvolge il gruppo |
| 1 - Light | 3 | Chiedere come stai e ascoltare davvero |
| 2 - Uncomfortable | 1 | Preparare un caffe o uno snack condiviso senza fretta |
| 2 - Uncomfortable | 2 | Fare una dinamica di gratitudine in gruppo |
| 2 - Uncomfortable | 3 | Condividere un piccolo obiettivo personale per i prossimi mesi |
| 3 - Courage | 1 | Creare un semplice rituale di connessione (check-in settimanale, ecc.) |
| 3 - Courage | 2 | Fare una camminata in silenzio seguita da una conversazione aperta |
| 3 - Courage | 3 | Scrivere biglietti anonimi di supporto all'interno del gruppo |
| 4 - Bold | 1 | Facilitare una conversazione piu profonda con regole di rispetto condivise |
| 4 - Bold | 2 | Pianificare un incontro solo per ascoltarsi a vicenda |
| 4 - Bold | 3 | Fare un'attivita che richieda vera cooperazione (cucinare insieme, ecc.) |
| 5 - Adrenaline | 1 | Programmare un breve ritiro o un lungo incontro focalizzato sulla connessione |
| 5 - Adrenaline | 2 | Creare un impegno di gruppo per prendersi cura gli uni degli altri |
| 5 - Adrenaline | 3 | Vivere un momento di vulnerabilita condivisa in sicurezza |

##### Esperienze diverse (`different_experiences`)

| Intensità | # | Suggerimento |
|-------------|---|----------|
| 1 - Light | 1 | Scambiarsi i ruoli in una dinamica semplice (chi organizza, chi cucina, ecc.) |
| 1 - Light | 2 | Provare cibo di un paese che nessuno conosce bene |
| 1 - Light | 3 | Andare a vedere un tipo di spettacolo dove il gruppo non e mai stato |
| 2 - Uncomfortable | 1 | Fare un tour alternativo in citta (vicoli, mercati, street art) |
| 2 - Uncomfortable | 2 | Creare una playlist condivisa e commentare ogni traccia |
| 2 - Uncomfortable | 3 | Andare in uno spazio culturale fuori dal radar del gruppo |
| 3 - Courage | 1 | Concordare un'esperienza sensoriale (cena al buio, ecc.) |
| 3 - Courage | 2 | Fare una dinamica di improvvisazione con nuove regole |
| 3 - Courage | 3 | Pianificare un incontro con un tema insolito |
| 4 - Bold | 1 | Partecipare a un'esperienza guidata da un esterno o da uno specialista |
| 4 - Bold | 2 | Fare un percorso notturno o in un luogo insolito |
| 4 - Bold | 3 | Creare un'esperienza creativa di gruppo con presentazione finale |
| 5 - Adrenaline | 1 | Programmare un'esperienza fuori dal comune che richieda pianificazione condivisa |
| 5 - Adrenaline | 2 | Viaggiare o campeggiare con un itinerario sperimentale |
| 5 - Adrenaline | 3 | Documentare e condividere gli apprendimenti dopo l'esperienza |

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
| Localizzazione suggerimenti | Pacchetti localizzati disponibili in portoghese, inglese e italiano |
| Profilo partecipante | Nessun avatar, preferenze o notifiche oltre nome/email |
| Sezioni tipo di scatola | Sezioni esistono nel catalogo; interfaccia mostra elenco piatto |
