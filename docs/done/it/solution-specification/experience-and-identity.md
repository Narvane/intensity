# Esperienza e Identità

Questo documento descrive l'identità visiva, i modelli di interazione e il linguaggio comunicativo di Intensity — come il prodotto si presenta, come gli utenti percepiscono la sua interfaccia e quali convenzioni UX governano l'esperienza. Specifica *come la soluzione si sente e comunica*, senza dettagli di implementazione.

**Pubblico:** analisti, product owner, designer e QA funzionale — persone che devono comprendere o riprodurre l'esperienza del prodotto senza sapere come è stato costruito.

---

## Curta

Intensity si presenta come **Intensity**, con **icona a scatola** e un linguaggio visivo **caldo e basato su card**. L'interfaccia segue il tema **chiaro o scuro** del dispositivo e usa il **marrone** per la modalità **Esperienze** (contributo individuale) e il **blu** per la modalità **Box Esperienze** (rituale di gruppo). **Undici tipi di box** portano ciascuno una palette di accento distinta. La comunicazione è disponibile in **portoghese, inglese e italiano**, con tono emotivamente caldo e centrato sul gruppo. I principi UX centrali includono **rivelazione progressiva** (intensità prima del testo completo), **allineamento di gruppo prima di rivelare**, **onboarding** al primo avvio e **guida rapida** riutilizzabile, oltre a **feedback tramite snackbar** per gli errori.

---

## Média

### Marchio e identità visiva

| Elemento | Comportamento osservabile |
|----------|---------------------------|
| **Nome del prodotto** | "Intensity" — mostrato nell'intestazione di autenticazione, barre superiori e aree del marchio |
| **Logo** | Icona scatola/inventario abbinata al nome; nessun logo raster personalizzato osservato |
| **Intestazione autenticazione** | Barra blu a gradiente con icona e nome in bianco |
| **Superfici** | Sfondi off-white caldi con gradiente verticale sottile; card bianche con bordi caldi morbidi |
| **Palette primaria (modalità Esperienze)** | Marrone caldo come colore d'azione principale |
| **Palette partecipante (modalità Box Esperienze)** | Gradiente blu per azioni di gruppo e ruolo partecipante |
| **Livelli di intensità 1–5** | Verde → blu → ambra → arancione → rosso, ciascuno con tonalità di superficie corrispondente |
| **Parametri** | Sforzo (teal), apertura (verde lime), novità (rosa) — ciascuno con icona e colore di superficie |
| **Stelle di valutazione** | Ambra/oro |
| **Forme** | Angoli arrotondati in tutta l'interfaccia — da chip piccoli a card grandi |
| **Tipografia** | Titoli in grassetto (22/18 sp), corpo 16/14 sp; font predefinito di sistema |

L'interfaccia si adatta alla **preferenza chiaro/scuro del sistema**. In modalità scura, gli sfondi passano a marroni profondi; il marrone primario si schiarisce; il testo diventa off-white caldo.

### Tematizzazione per contesto

L'accento visivo cambia in base al contesto operativo:

| Contesto | Indicatore visivo |
|----------|-------------------|
| **Modalità Esperienze** | Pulsanti primari marroni, card di autenticazione marroni quando selezionate |
| **Modalità Box Esperienze** | Pulsanti primari blu, card di autenticazione blu quando selezionate, barra di stato blu all'avvio |
| **Tipo di box attivo** | Gradiente della barra superiore e accenti seguono la palette del tipo selezionato (11 temi distinti) |
| **Progresso assistente** | Barra a cinque segmenti con indicatore ambra del passo corrente |

### Modelli visivi ricorrenti

- **Card** — contenitori bianchi/superficie con raggio 16 dp, bordo caldo, elevazione leggera
- **Barra superiore a gradiente** — 62 dp di altezza, riempimento a gradiente, titolo e azioni in bianco
- **Pulsanti primari** — larghezza piena, forma a pillola, 52 dp di altezza; variante marrone o blu per ruolo
- **Intestazioni di sezione** — icona (26 dp) + titolo in grassetto; etichette small-caps con spaziatura tra lettere
- **Chip filtro** — forma a pillola; blu quando selezionato
- **Card con flip** — la card esperienza ruota sull'asse Y per rivelare la descrizione dopo l'allineamento
- **Card box in griglia** — griglia a due colonne, raggio 20 dp, badge tipo con icona
- **Punti intensità** — selettori circolari 1–5 per filtri di sorteggio
- **Valutazione a stelle** — interattiva 1–5 con testo di aiuto per livello sotto

Le icone sono glifi in stile material in tutta l'interfaccia (gruppi, preferito, scatola/inventario, stella, fulmine, lampadina, flip, ecc.).

### Convenzioni UX

1. **Codifica colore per ruolo** — il marrone segnala contributo individuale (Esperienze); il blu segnala presenza di gruppo (Box Esperienze).
2. **Rivelazione progressiva** — descrizione esperienza nascosta fino a rivelazione esplicita (azione occhio nella lista, flip della card nel momento condiviso).
3. **Allineamento di gruppo prima di rivelare** — suggerimento tratteggiato ambra che invita ad accordarsi su clima, limiti e impegno prima di girare la card.
4. **Educazione al primo avvio** — onboarding illustrato in quattro passi, saltabile; guida rapida opzionale con regole del prodotto.
5. **Aiuto sempre raggiungibile** — guida rapida e onboarding riapribili dalla barra strumenti di autenticazione.
6. **Feedback errore transitorio** — errori mostrati tramite snackbar, non banner inline persistenti.
7. **Indicatori di caricamento** — spinner per bootstrap, caricamento liste e azioni di sorteggio; overlay semitrasparente durante invio assistente.
8. **Stati vuoti dedicati** — messaggi testuali di vuoto dentro card o sezioni, senza illustrazioni.
9. **Lista proponente per sessione** — la lista Esperienze mostra solo i contributi dell'utente corrente nella sessione attiva.
10. **Semantica intensità** — ogni livello (1–5) porta sottotitolo e colore; i parametri mostrano aiuto contestuale e descrizioni per stella.

### Linguaggio e tono comunicativo

**Lingue supportate:** portoghese (predefinito), inglese, italiano — selezionabili tramite controllo bandiera, disponibile nelle schermate di onboarding e autenticazione.

**Vocabolario del prodotto** (come mostrato all'utente):

| Termine | Ruolo |
|---------|-------|
| Esperienza | Idea concreta da fare insieme |
| Box Esperienze | Modalità rituale di gruppo — box, sorteggio, rivelazione |
| Box | Contenitore tematico per esperienze raccolte |
| Intensità | Livello generale di audacia 1–5 |
| Sorteggio | Selezione casuale da un box |
| Rivelare | Momento deliberato di vedere la descrizione completa |
| Sigillo | Impronta di integrità sulle card esperienza |
| Sforzo / Apertura / Novità | Tre valutazioni di parametri nell'assistente di creazione |

**Sottotitoli livelli intensità:** Leggera → Scomoda → Coraggio → Audace → Adrenalina.

**Caratteristiche del tono:**
- Conversazionale ed emotivamente caldo
- Centrato sul gruppo ("concordate come gruppo prima di rivelare", "tutti alimentano il box")
- Istruttivo nella guida rapida — regola centrale, flusso raccomandato, suggerimenti intensità, suggerimenti conseguenza, essenza del prodotto
- Prefissi errore diretti ("Errore", "Accesso fallito") con messaggi di validazione specifici nell'assistente

L'onboarding narra una storia in quattro passi: problema (esperienze ripetitive) → insight (memorabile = inaspettato) → invito all'azione → meccanica del prodotto (raccogliere, sorteggiare, vivere).

---

## Detalhada

### Sistema colori in profondità

Il sistema visivo organizza tre famiglie di colore:

**Tema base (chiaro):**
- Sfondo: off-white caldo `#FCFAF7`
- Contenitori superficie: `#F6EFE6` / `#EFE7DB`
- Marrone primario: `#B0946F`
- Testo: `#1D1B20`; variante `#49454F`
- Errore: `#B3261E`

**Tema base (scuro):**
- Sfondo: `#15110E`; superficie `#1C1713`
- Primario: `#D4BC9A`; su superficie `#ECE3DC`

**Token ruolo e semantica:**
- Blu partecipante: gradiente `#1E5EFF` → `#4C7CFF`; superficie `#E8F1FF`
- Parametro sforzo: teal `#00A3B4` / superficie `#DCFBFF`
- Parametro apertura: lime `#84CC16` / superficie `#F7FEE7`
- Parametro novità: rosa `#E11D48` / superficie `#FFE4E6`
- Intensità 1: verde `#2E7D32`; 2: blu `#0277BD`; 3: ambra `#F9A825`; 4: arancione `#EF6C00`; 5: rosso `#C62828` — ciascuno con tonalità superficie corrispondente
- Bordo card: marrone caldo a bassa opacità
- Stella valutazione: `#F9A825`

Ciascuno degli **undici tipi di box** porta accento, superficie e gradiente barra superiore propri — visibili navigando i box e quando un box è attivo nella lista esperienze o nel momento condiviso.

### Tipografia e spaziatura

I titoli usano grassetto a 22 sp (grande) e 18 sp (medio). Il corpo è a 16 sp con altezza riga 22 sp; corpo secondario a 14 sp. Le etichette di sezione in grassetto 14 sp. Etichette small-caps in maiuscolo con spaziatura tra lettere.

Ritmo spaziatura comune: padding schermo 16–18 dp; gap tra card 12–14 dp; padding orizzontale barra superiore 2–16 dp.

I raggi degli angoli vanno da 9 dp (extra piccolo) a 24 dp (extra grande), conferendo sensazione morbida e accogliente.

### Sfondo schermate

La maggior parte delle schermate usa gradiente verticale dal colore superficie caldo al bianco (chiaro) o da tono medio-scuro a sfondo profondo (scuro), stabilendo continuità visiva nel flusso.

### Onboarding e guida rapida

**Onboarding** — quattro passi illustrati con indicatori a punti, navigazione Indietro/Avanti, "Inizia" per terminare e scorciatoia "Apri guida rapida". Selettore lingua disponibile. Riapribile dall'autenticazione.

**Guida rapida** — cinque sezioni di contenuto:
1. **Regola centrale** — raccogliere nel tempo, prendere sul serio il sorteggio, valutare 1–5, scegliere un'esperienza, definire conseguenza, decidere prima di rivelare
2. **Flusso raccomandato** — tutti alimentano il box; attivare sorteggio quando insieme; allinearsi prima di rivelare
3. **Suggerimenti — Intensità** — iniziare basso, aumentare gradualmente, permettere scambi, usare filtri
4. **Suggerimenti — Conseguenza** — definire prima, costo reale, scalare se qualcuno si tira indietro, variare con scambi
5. **Essenza Intensity** — connessione, intensità, scoperta; vivere momenti significativi con presenza

Suggerimento di chiusura: "In caso di dubbio, concordate come gruppo prima di rivelare."

### Modalità visive autenticazione

Tre card di modalità selezionabili nella schermata di autenticazione:

| Modalità | Colore quando selezionata | Sottotitolo |
|----------|---------------------------|-------------|
| **Esperienze** | Marrone | Registra esperienze e scegli un box |
| **Box Esperienze** | Blu | Il gruppo entra insieme; gruppo condiviso |
| **Registrazione** | Marrone | Crea un nuovo account |

Il pannello Esperienze mostra login email/password. Il pannello Box Esperienze permette di aggiungere una o più credenziali utente prima del login. Il pannello Registrazione raccoglie nome visualizzato, email e password.

### Presentazione parametri e valutazioni

Nell'assistente di creazione, ogni parametro (sforzo, apertura, novità) mostra:
- Icona e colore dedicati
- Frase di aiuto che spiega cosa misura il parametro
- Riga stelle 1–5 con prompt di tocco
- Descrizione dinamica per livello che si aggiorna al cambiare delle stelle

La classificazione intensità mostra i cinque sottotitoli di livello con colori corrispondenti e permette regolazione manuale del livello suggerito automaticamente.

### Presentazione sorteggio e rivelazione

Nel momento condiviso, prima del sorteggio:
- Chip filtro: **Qualsiasi**, **Esatta**, **Fino a** — con selettore opzionale punti intensità
- Card suggerimento che incoraggia attivazione quando il box è vuoto

Dopo il sorteggio:
- La card mostra prima copertina intensità (livello, parametri, sigillo)
- Suggerimento tratteggiato ambra invita allineamento di gruppo prima di rivelare
- Azione flip rivela descrizione completa
- "Torna al sorteggio" ritorna alla selezione filtri

### Identità navigazione

La navigazione è lineare e guidata dallo stato — senza barra tab o sidebar persistente. Le azioni indietro usano icone nella barra superiore che reimpostano l'ambito sessione (box → gruppo → autenticazione). Logout sempre disponibile dalle barre autenticate. Gli overlay (onboarding, guida, assistente) si sovrappongono alla schermata corrente senza sostituire lo stack di navigazione.

### Osservazioni accessibilità

Il contrasto segue i default del tema. Intensità e parametri usano colore **e** etichette testuali (sottotitoli, descrizioni di aiuto). Focus e navigazione da tastiera non hanno potuto essere completamente validati da analisi statica. Le etichette per screen reader non sono state verificate esaustivamente.
