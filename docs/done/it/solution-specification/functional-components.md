# Componenti Funzionali

Questo documento cataloga i moduli funzionali, le schermate, i flussi utente e i comportamenti dell'interfaccia di Intensity — cosa l'utente può fare, dove e in quali condizioni. Specifica *cosa esiste funzionalmente* nell'interfaccia, senza dettagli di implementazione.

**Pubblico:** analisti, product owner, designer e QA funzionale — persone che devono mappare funzionalità, percorsi e comportamenti delle schermate senza sapere come l'app è stata costruita.

---

## Curta

Intensity è un'**applicazione mobile** organizzata attorno a **undici viste principali** più overlay. Dopo bootstrap e onboarding opzionale, l'utente si autentica in una di tre modalità (**Esperienze**, **Box Esperienze** o **Registrazione**). Il percorso **Esperienze** passa per selezione gruppo → selezione box → lista esperienze → assistente di creazione. Il percorso **Box Esperienze** passa per lista box → creazione opzionale box → momento condiviso (sorteggio e rivelazione). Ogni schermata gestisce esplicitamente stati di **caricamento**, **vuoto** e **errore**. Un **assistente di creazione** in cinque passi guida la registrazione delle esperienze. Il **momento condiviso** supporta filtri di intensità e rituale di rivelazione con flip della card.

---

## Média

### Moduli funzionali

| Modulo | Scopo |
|--------|-------|
| **Bootstrap** | Caricare preferenza lingua e stato primo avvio prima di mostrare contenuto |
| **Onboarding** | Introduzione illustrata in quattro passi alla storia del prodotto |
| **Guida rapida** | Manuale riutilizzabile con regole centrali, flusso e suggerimenti |
| **Autenticazione** | Login (Esperienze o Box Esperienze), registrazione, accesso all'aiuto |
| **Selezione gruppo** | Scegliere in quale gruppo di partecipanti contribuire (modalità Esperienze) |
| **Selezione box** | Scegliere quale box dentro il gruppo (modalità Esperienze) |
| **Lista esperienze** | Vedere, rivelare ed eliminare esperienze proprie nel box attivo |
| **Assistente creazione** | Flusso guidato in cinque passi per registrare nuova esperienza |
| **Home box** | Elencare e creare box (modalità Box Esperienze) |
| **Momento condiviso** | Sorteggio casuale con filtri, suggerimento allineamento e rivelazione card |
| **Recupero errore** | Schermata per stato sessione non riconosciuto con opzioni di uscita |

### Catalogo schermate

| # | Schermata | Quando mostrata |
|---|-----------|-----------------|
| 1 | **Caricamento bootstrap** | Preferenze lingua/onboarding non ancora pronte |
| 2 | **Onboarding** (4 passi) | Primo avvio |
| 3 | **Guida rapida** | Da onboarding o aiuto autenticazione; overlay |
| 4 | **Autenticazione** | Nessuna sessione attiva; onboarding completato |
| 5 | **Sessione sconosciuta** | Modalità accesso sessione non riconosciuta |
| 6 | **Selezione gruppo** | Modalità Esperienze; nessun gruppo scelto |
| 7 | **Selezione box** | Modalità Esperienze; gruppo impostato, box non scelto |
| 8 | **Lista esperienze** | Modalità Esperienze; gruppo e box impostati |
| 9 | **Assistente creazione** | Overlay dalla lista esperienze |
| 10 | **Home box** | Modalità Box Esperienze |
| 11 | **Crea box** | Sotto-vista dalla home box |
| 12 | **Momento condiviso** | Modalità Box Esperienze; box aperto |

L'autenticazione contiene anche tre **sotto-pannelli** (non rotte separate): login Esperienze, login multiutente Box Esperienze e Registrazione.

### Flussi utente principali

```
Flusso A — Primo avvio
  Caricamento → Onboarding (4 passi) → [Guida rapida opzionale] → Autenticazione

Flusso B — Esperienze (contributo individuale)
  Autenticazione → Selezione gruppo → Selezione box → Lista esperienze
    → [+ Crea esperienza] → Overlay assistente → ritorno alla lista
  Indietro: lista → selezione box → selezione gruppo
  Esci: logout da qualsiasi schermata autenticata

Flusso C — Box Esperienze (rituale di gruppo)
  Autenticazione (multiutente) → Home box → [Crea box] → Home box
    → Apri box → Momento condiviso → Sorteggio → Allinea → Rivela → Torna al sorteggio
  Indietro: momento condiviso → home box
  Esci: logout

Flusso D — Recupero errore
  Sessione sconosciuta → Esci (logout) o Entra nel Box Esperienze (pulisce sessione)
```

### Passi assistente creazione

| Passo | Etichetta | Azione utente |
|-------|-----------|---------------|
| 1 — Suggerimento | Scrivere descrizione o toccare suggerimento del tipo box come ispirazione |
| 2 — Riflessione | Giustificare perché il gruppo accetterebbe l'idea |
| 3 — Parametrizzazione | Valutare sforzo, apertura, novità (1–5 stelle ciascuno) |
| 4 — Classificazione | Confermare o regolare intensità generale (suggerita automaticamente dai parametri) |
| 5 — Biforcazione | Rivedere riepilogo; salva e crea un'altra, o termina |

L'assistente mostra una card descrizione persistente lungo i passi e indicatore progresso a cinque segmenti.

### Funzionalità momento condiviso

- **Modalità filtro:** Qualsiasi (nessun filtro intensità), Esatta (livello fisso 1–5), Fino a (livello massimo inclusivo)
- **Azione sorteggio:** selezione casuale tra esperienze eleggibili nel box
- **Card risultato:** mostra copertina intensità (livello, parametri, sigillo) prima della rivelazione
- **Suggerimento allineamento:** invita accordo di gruppo prima di girare la card
- **Rivelare:** flip card per leggere descrizione completa
- **Ritorno:** torna al sorteggio per nuova selezione

### Tipi di box (11)

Ogni tipo ha titolo, suggerimento sottotitolo, accento visivo distinto e pacchetto suggerimenti associato:

| Tipo | Suggerimento sottotitolo |
|------|--------------------------|
| Uscite con amici | Uscite di gruppo da leggere a intense |
| Uscite in coppia | Caffè, passeggiate e uscite a due |
| Viaggi in coppia | Fughe e destinazioni a due |
| Intimità in coppia | Connessione e conversazioni più profonde |
| Viaggi con amici | Gite, weekend o viaggi pianificati |
| Esperienze con amici | Corsi, tour ed esperienze di gruppo |
| Rompi la routine | Piccole rotture della routine quotidiana |
| Prime volte | Provare cose nuove con calma |
| Leggero disagio | Uscire un po' dalla zona di comfort con cura |
| Momenti di connessione | Presenza, ascolto e legame nel gruppo |
| Esperienze diverse | Cose fuori dal comune per il gruppo |

Tipo box predefinito se non specificato: **Uscite con amici**.

---

## Detalhada

### Caricamento bootstrap

**Scopo:** Preparare lingua e stato onboarding prima di renderizzare il flusso principale.

**Comportamento:** Spinner centrato a schermo intero. Nessuna azione utente. Transita automaticamente a onboarding (primo avvio) o autenticazione.

**Stati:** Solo caricamento.

---

### Onboarding (4 passi)

**Scopo:** Introdurre la storia e la proposta di valore del prodotto al primo avvio.

**Contenuto per passo:**
1. Problema — esperienze ripetitive, mancanza di vicinanza
2. Insight — i momenti memorabili erano inaspettati, ma rimandati
3. Invito all'azione — non aspettare il caso; Intensity spinge ad agire
4. Meccanica — raccogli idee insolite, sorteggiane una, vivi momenti memorabili

**Azioni:** Indietro, Avanti, Inizia (termina), Apri guida rapida. Selettore lingua disponibile.

**Stati:** Nessun caricamento, vuoto o errore. Riapribile dall'autenticazione (modalità non-primo-avvio).

---

### Guida rapida

**Scopo:** Riferimento persistente per regole del prodotto e raccomandazioni sociali.

**Sezioni:** Regola centrale (7 punti), flusso raccomandato (3 punti), suggerimenti intensità (4 punti), suggerimenti conseguenza (4 punti), essenza Intensity (2 punti), card suggerimento di chiusura.

**Azioni:** Inizia (primo avvio — chiude e continua), Chiudi (modalità riapertura).

**Stati:** Nessun caricamento, vuoto o errore.

---

### Autenticazione

**Scopo:** Punto di ingresso per tutte le sessioni. Tre modalità selezionabili tramite card.

**Pannello login Esperienze:**
- Campi email e password
- Azione accedi
- Al successo → flusso selezione gruppo

**Pannello login Box Esperienze:**
- Aggiungere una o più righe credenziale (email + password ciascuna)
- Azione accedi — tutte le credenziali devono avere successo; insieme definiscono il gruppo
- Al successo → home box

**Pannello Registrazione:**
- Nome visualizzato, email, password
- Azione registra — email deve essere nella lista permessi
- Al successo → ritorna al login

**Barra strumenti:** Icona guida rapida, icona riapri onboarding, selettore lingua.

**Stati:**

| Stato | Presentazione |
|-------|---------------|
| Caricamento | Testo pulsante cambia in "Accesso in corso…" / "Registrazione in corso…" |
| Errore | Snackbar: fallimento login, errore token, errore credenziale, errore registrazione |

---

### Sessione sconosciuta

**Scopo:** Recupero quando la modalità accesso sessione non è Esperienze né Box Esperienze.

**Contenuto:** Avviso con valore grezzo modalità accesso mostrato.

**Azioni:** Esci (logout), Entra nel Box Esperienze (pulisce sessione → ritorna ad autenticazione).

**Stati:** Avviso persistente — non snackbar transitorio.

---

### Selezione gruppo

**Scopo:** In modalità Esperienze, scegliere in quale gruppo di partecipanti contribuire.

**Contenuto:** Lista gruppi mostrando nomi partecipanti e conteggio membri.

**Azioni:** Tocca gruppo → Entra (aggiorna sessione, procede a selezione box). Logout dalla barra superiore.

**Stati:**

| Stato | Presentazione |
|-------|---------------|
| Caricamento | Spinner centrato |
| Vuoto | Card: "Non ci sono ancora gruppi…" |
| Errore | Snackbar su fallimento caricamento o selezione |

---

### Selezione box (modalità Esperienze)

**Scopo:** Scegliere quale box dentro il gruppo selezionato per aggiungere esperienze.

**Contenuto:** Griglia a due colonne di card visuali box — nome, badge tipo con icona, colore accento tipo.

**Azioni:** Tocca box → procede a lista esperienze. Indietro → pulisce gruppo (ritorna a selezione gruppo). Logout.

**Stati:**

| Stato | Presentazione |
|-------|---------------|
| Caricamento | Spinner centrato |
| Vuoto | Testo semplice: nessun box in questo gruppo |
| Errore | Snackbar su fallimento caricamento o selezione |

---

### Lista esperienze

**Scopo:** Vedere e gestire le esperienze dell'utente corrente nel box attivo durante questa sessione.

**Contenuto:**
- Barra superiore tematizzata al tipo box attivo
- Sezione: esperienze registrate (solo contributi propri nella sessione corrente)
- Ogni card esperienza: badge intensità, parametri, sigillo, azioni rivela/elimina
- Rivelare alterna visibilità testo descrizione completo
- Elimina rimuove esperienza propria

**Azioni:** + Crea esperienza (apre overlay assistente). Indietro → pulisce box (ritorna a selezione box). Logout.

**Stati:**

| Stato | Presentazione |
|-------|---------------|
| Caricamento | Spinner piccolo nell'intestazione sezione |
| Vuoto | Card: non ci sono ancora esperienze |
| Errore | Snackbar su fallimento lista, rivelazione o eliminazione |

---

### Assistente creazione (overlay)

**Scopo:** Registrazione guidata in cinque passi di nuova esperienza.

**Elementi persistenti:** Card descrizione (mostra testo corrente), barra progresso cinque segmenti, indicatore pillola passo.

**Dettagli per passo:**

**1 — Suggerimento**
- Campo descrizione testo libero
- Pacchetto suggerimenti tipo box mostrato per livello intensità — esempi toccabili come ispirazione
- I suggerimenti cambiano secondo il tipo box attivo

**2 — Riflessione**
- Campo testo per giustificazione accettazione gruppo

**3 — Parametrizzazione**
- Tre righe stelle: sforzo (teal), apertura (lime), novità (rosa)
- Ciascuna con testo aiuto e descrizione dinamica per livello

**4 — Classificazione**
- Intensità suggerita automaticamente dalla media parametri (utente può sovrascrivere)
- Cinque livelli intensità con sottotitoli e colori

**5 — Biforcazione**
- Revisione riepilogo di tutti i dati inseriti
- Salva e crea un'altra (reimposta assistente, mantiene overlay aperto)
- Salva e termina (chiude overlay, aggiorna lista)

**Validazione:** Descrizione obbligatoria; riflessione obbligatoria; tutti parametri valutati; intensità impostata. Errori tramite snackbar.

**Stati:**

| Stato | Presentazione |
|-------|---------------|
| Caricamento | Overlay semitrasparente con spinner all'invio |
| Vuoto | Passo Suggerimento mostra esempi pacchetto anche prima input utente |
| Errore | Snackbar: messaggi validazione, fallimento invio |

---

### Home box (modalità Box Esperienze)

**Scopo:** Elencare box del gruppo e crearne di nuovi.

**Contenuto:** Griglia a due colonne di card visuali box. Invito a creare box.

**Azioni:** Tocca box → momento condiviso. Crea box → sotto-vista creazione. Logout.

**Stati:**

| Stato | Presentazione |
|-------|---------------|
| Caricamento | Spinner centrato |
| Vuoto | Testo: nessun box |
| Errore | Snackbar su fallimento caricamento |

---

### Crea box (sotto-vista)

**Scopo:** Registrare nuovo box per il gruppo corrente.

**Contenuto:**
- Selettore tipo scorrevole — 11 opzioni con titolo, sottotitolo, accento
- Campo nome

**Azioni:** Salva → ritorna a home box con nuovo box elencato. Indietro → ritorna senza salvare.

**Validazione:** Nome obbligatorio (snackbar se vuoto).

**Stati:**

| Stato | Presentazione |
|-------|---------------|
| Caricamento | Testo pulsante "Creazione in corso…" |
| Errore | Snackbar su fallimento API |

---

### Momento condiviso

**Scopo:** Rituale di gruppo — sorteggiare esperienza casuale, allinearsi, rivelare.

**Contenuto:**
- Barra superiore tematizzata al tipo box
- Chip filtro: Qualsiasi / Esatta / Fino a
- Selettore punti intensità (quando Esatta o Fino a selezionato)
- Pulsante sorteggio (etichetta si adatta alla modalità filtro)
- Area risultato: card con flip o suggerimenti

**Flusso:**
1. Selezionare filtro (e livello se applicabile)
2. Toccare sorteggio → selezione casuale
3. Card mostra copertina intensità (livello, parametri, sigillo)
4. Suggerimento allineamento mostrato
5. Flip → descrizione completa visibile
6. Torna al sorteggio → ritorna al passo 1

**Stati:**

| Stato | Presentazione |
|-------|---------------|
| Caricamento | Testo pulsante "Scelta in corso…" durante sorteggio |
| Vuoto (pre-sorteggio) | Card suggerimento che incoraggia attivazione |
| Vuoto (post-sorteggio) | "Nessuna esperienza disponibile" quando pool vuoto per filtro |
| Errore | Snackbar con prefisso errore |

---

### Elementi interfaccia riutilizzabili (catalogo funzionale)

| Elemento | Ruolo funzionale |
|----------|------------------|
| **Intestazione marchio** | Icona + nome prodotto in autenticazione |
| **Card modalità** | Modalità autenticazione selezionabile (Esperienze / Box Esperienze / Registrazione) |
| **Selettore lingua** | Alternare PT / EN / IT |
| **Barra superiore a gradiente** | Titolo schermata, indietro, logout |
| **Pulsante primario (marrone)** | Azioni predefinite / Esperienze |
| **Pulsante primario (blu)** | Azioni Box Esperienze / gruppo |
| **Contenitore card** | Raggruppare contenuto con confine visivo |
| **Riga titolo sezione** | Icona + intestazione grassetto |
| **Etichetta small-caps** | Identificatore sezione maiuscolo |
| **Riga stelle** | Input 1–5 con testo aiuto |
| **Chip filtro** | Alternare modalità filtro sorteggio |
| **Punti intensità** | Selezionare livello 1–5 per filtro sorteggio |
| **Card visuale box** | Mostrare nome, tipo, accento in griglia |
| **Card rivelazione esperienza** | Flip tra copertina intensità e descrizione |
| **Card riepilogo esperienza** | Info compatta con badge intensità e sigillo |
| **Riga progresso assistente** | Cinque segmenti + pillola passo corrente |
| **Blocco suggerimento** | Esempio toccabile nel passo 1 assistente |
| **Snackbar** | Messaggi transitori errore e informazione |
| **Spinner** | Indicatore caricamento (inline, centrato o overlay) |

---

### Regole sessione e navigazione

- **Modalità Esperienze** richiede login individuale; la sessione porta `groupId` e `boxId` man mano che l'utente avanza
- **Modalità Box Esperienze** richiede login multiutente; il gruppo emerge dalla combinazione credenziali
- **Navigazione indietro** pulisce ambito: lista esperienze → pulisce box; selezione box → pulisce gruppo
- **Logout** da qualsiasi schermata autenticata ritorna ad autenticazione
- **Assistente** è overlay a schermo intero — non altera ambito sessione
- **Onboarding/guida** sono overlay su bootstrap o autenticazione — non creano sessioni
