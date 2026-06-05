# Panoramica — Cos'è?

Documento di concezione del prodotto **Intensity**. Contenuto derivato dal comportamento osservabile dell'applicazione, dai testi mostrati all'utente e dalla struttura funzionale del repository.

---

## Pitch

### Breve

**Intensity** aiuta amici e coppie a raccogliere idee di esperienze insolite, classificarle per intensità e sorteggiarne una per vivere momenti di connessione — invece di rimandare l'inaspettato.

### Media

Stanco di esperienze ripetitive che ti avvicinano poco alle persone importanti? **Intensity** è un'app mobile per gruppi e coppie: ogni persona registra idee in box tematici, le classifica da 1 a 5 per intensità e, quando sono insieme, sorteggiano un'esperienza da vivere sul momento. Il prodotto ti incoraggia ad agire invece di aspettare che sia il caso a creare momenti memorabili.

### Dettagliata

**Intensity** trasforma la raccolta di idee e il sorteggio in un rituale condiviso. I partecipanti invitati creano account, formano gruppi e alimentano **box di esperienze** nel tempo — ogni idea passa per suggerimento, riflessione, parametrizzazione (sforzo, apertura, novità) e classificazione finale dell'intensità. Nell'incontro di persona, l'app sorteggia un'esperienza dal box, con filtri per livello di intensità. L'essenza del prodotto è **connessione, intensità e scoperta**: non si tratta di completare compiti, ma di vivere momenti intensi con presenza. L'ecosistema osservabile include app mobile (Android, con target iOS) e API dedicata (**Intensity API**), con supporto per portoghese, inglese e italiano.

---

## Descrizione stile App Store / Google Play

### Breve

Colleziona idee insolite. Sorteggia. E vivi momenti memorabili con chi conta.

### Media

**Intensity** — connessione, intensità e scoperta.

Crea box con amici o in coppia, aggiungi esperienze nel tempo, classifica da 1 a 5 e sorteggia quando siete insieme. Filtri di intensità, suggerimenti per tipo di box e guida rapida integrata. Disponibile in portoghese, inglese e italiano.

### Dettagliata

Ti manca più vicinanza? I momenti più memorabili sono quasi sempre i più inaspettati — eppure vengono quasi sempre rimandati. **Intensity** cambia questo.

**Come funziona**

- Registrati (accesso su invito) e accedi da solo per registrare esperienze o in gruppo per aprire il **Box Esperienze**.
- Scegli o crea box tematici: uscite con amici, viaggi di coppia, momenti di connessione e altre categorie disponibili nell'app.
- Registra idee con un assistente in cinque passi: suggerimento, riflessione, stelle di sforzo/apertura/novità e intensità finale (da *Leggero* a *Adrenalina*).
- Nell'incontro, sorteggia un'esperienza — qualsiasi livello, intensità esatta o fino a un livello massimo.
- Prima di rivelare, allineate clima, limiti e impegno; la guida rapida orienta su conseguenze ed evoluzione graduale dell'intensità.

Ideale per coppie e gruppi di amici che vogliono uscire dalla routine con intenzione. Sviluppato da **Narvane**.

---

## Riassunto esecutivo

### Breve

**Intensity** è un prodotto mobile con API di Narvane per gruppi e coppie che registrano, classificano e sorteggiano esperienze condivise, promuovendo la connessione attraverso momenti insoliti pianificati collettivamente.

### Media

Il repository contiene due applicazioni principali: **intensity-api** (Spring Boot, PostgreSQL, autenticazione JWT) e client **Kotlin Multiplatform** (Compose, Android e target iOS). Il dominio centrale ruota attorno a **gruppi**, **box tipizzati** ed **esperienze** con scala di intensità da 1 a 5. Due modalità strutturano l'uso: **Esperienze** (registrazione individuale, modalità CURATE) e **Box Esperienze** (sessione collettiva con sorteggio, modalità CONNECT). La registrazione è chiusa tramite lista di e-mail autorizzate. Lingue dell'interfaccia: PT, EN e IT.

### Dettagliata

**Problema affrontato (evidenza: testi di onboarding e guida rapida):** esperienze ripetitive, distanza emotiva tra persone vicine e rimando di momenti distintivi che potrebbero avvicinarle.

**Soluzione osservabile:** box collaborativi per gruppo, sorteggio ritualizzato di esperienze, parametrizzazione multidimensionale (intensità, sforzo, apertura, novità) e flusso di riflessione nella creazione di ogni idea.

**Architettura funzionale (prodotto, non implementazione):** partecipanti → gruppi (combinazione di persone che si sono connesse insieme) → box (tipo tematico) → esperienze (contenuto protetto sul server).

**Flussi principali:** onboarding e guida rapida → autenticazione → modalità individuale (seleziona gruppo → box → registra/modifica esperienze) o modalità di gruppo (box → sorteggio con filtri di intensità).

**Stato osservabile:** versione app `1.0.0`, accesso su invito, nessun client web in questo repository. Regole sociali suggerite nella guida rapida (conseguenze, scambi tra livelli) compaiono solo come orientamento testuale — non sono state trovate come funzionalità implementata nell'API o nel database.

---

## Evidenze e limitazioni

| Argomento | Stato |
|-----------|--------|
| Nome del brand nell'interfaccia | **Intensity** (`app.brand`, etichetta Android) |
| Essenza del prodotto | *Connessione, intensità e scoperta* (guida rapida) |
| Problema e proposta di valore | Onboarding in quattro passi (`PtDictionary.kt` ed equivalenti EN/IT) |
| Pubblico target inferibile | Gruppi di amici e coppie (tipi di box e testi della guida) |
| Tagline ufficiale unica | **Non trovata** — esistono frammenti convergenti nell'onboarding |
| Nome "Intensity Box" | Compare nel README del client; l'UI usa **Intensity** |
| Modello di business / prezzo | **Non trovato** nel repository |
| Conseguenze e scambi tra livelli | Orientamento nella guida; **nessuna evidenza di implementazione** nel software |
| Client web | **Non presente** in questo repository |

**Fonti principali:** dizionari dell'interfaccia (`PtDictionary.kt`, `EnDictionary.kt`, `ItDictionary.kt`), `IntensityApp.kt`, `openapi.yaml`, migrazioni del database (`V221__intensity2_init.sql`), `ExperienceBoxTypeCodes.kt`.
