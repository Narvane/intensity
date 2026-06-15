# Esperienza e Identità

Questo documento definisce l'identità visiva, le linee guida UX e il tono comunicativo di Intensity — come il prodotto appare, si percepisce e parla agli utenti. È scritto per designer, product owner e chiunque modelli la comunicazione rivolta all'utente.

---

## Breve

Intensity presenta un brand **caldo, intimo e coraggioso**. Due modalità di accesso usano colori accento distinti: **marrone** per Esperienze (contributo individuale) e **blu** per Scatola delle Esperienze (rituale di gruppo). I livelli di intensità mappano a una scala a cinque colori dal verde (Leggero) al rosso (Adrenalina). La tipografia è pulita e leggibile; carte e sigilli rafforzano la fiducia. La voce è diretta, incoraggiante e rispettosa del consenso del gruppo.

---

## Media

### Essenza del brand

| Attributo | Espressione |
|-----------|-------------|
| **Connessione** | Gradienti morbidi, immagini accoppiate in onboarding, linguaggio di vicinanza |
| **Intensità** | Scala colori audace, etichette livello chiare, animazione deliberata di rivelazione |
| **Scoperta** | Chip suggerimenti giocosi, tipi scatola tematici, curiosità nel copy |
| **Presenza** | Chrome minimo durante il rituale di estrazione; focus sul momento della carta |

### Sistema visivo

**Palette primaria:**

| Ruolo | Uso |
|-------|-----|
| Accento marrone | Modalità Esperienze — contributo, riflessione |
| Accento blu | Modalità Scatola delle Esperienze — rituale, insieme |
| Scala Verde → Rosso | Livelli intensità 1–5 |
| Teal / Lime / Rosa | Parametri Impegno, Apertura, Novità |
| Suggerimento ambra tratteggiato | Promemoria allineamento pre-rivelazione |

**Colori intensità:**

| Livello | Etichetta | Colore |
|---------|-----------|--------|
| 1 | Leggero | Verde |
| 2 | Scomodo | Blu |
| 3 | Coraggio | Ambra |
| 4 | Audace | Arancione |
| 5 | Adrenalina | Rosso |

### Logo e naming

- **Nome prodotto:** Intensity — sempre con iniziale maiuscola nell'interfaccia
- **Logo:** Wordmark con sottile gradiente di intensità; usato su splash, onboarding e header auth
- **Icona app:** Motivo astratto fiamma o impulso che suggerisce calore ed energia (asset store)

### Principi UX

1. **Chiarezza modalità** — colore e header segnalano immediatamente Esperienze vs Scatola delle Esperienze
2. **Divulgazione progressiva** — intensità prima del testo; anteprima invito prima dell'adesione
3. **Consenso esplicito** — conferme per elimina scatola, lascia gruppo, accetta invito
4. **Stati vuoti come guida** — scatola vuota incoraggia il contributo; pool estrazione vuoto spiega i filtri
5. **Baseline accessibilità** — target touch ≥44pt; contrasto conforme WCAG AA per testo; etichette screen reader sulle azioni principali

### Terminologia (canonica)

| Termine UI | Significato |
|------------|-------------|
| Esperienza | Un'idea concreta da fare insieme |
| Scatola | Collezione tematica di esperienze |
| Scatola delle Esperienze | Modalità gruppo per scatole e rituale di estrazione |
| Gruppo | Persone che condividono scatole |
| Intensità | Quanto audace sembra un'esperienza (1–5) |
| Estrazione | Selezione casuale di un'esperienza da una scatola |
| Rivela | Gira la carta per vedere la descrizione completa |
| Sigillo | Marchio di integrità sulla carta esperienza |
| Invito | Link o codice per unirsi a un gruppo |
| Proponente | Persona che ha contribuito con un'esperienza |

Evitare termini tecnici come "hash" nel copy utente — usare **Sigillo**.

---

## Dettagliata

### Narrativa visiva di onboarding

Quattro passi illustrati raccontano la storia emotiva: routine ripetitive → nostalgia di connessione → momenti inusuali rimandati → Intensity come risposta. Le illustrazioni usano coppie e gruppi di amici diversi; il tono è speranzoso, non clinico.

### Pannelli di autenticazione

Tre sotto-pannelli all'interno di una schermata auth:

| Pannello | Indizio visivo | Azione principale |
|----------|----------------|-------------------|
| Login Esperienze | Accento marrone | Form credenziale singola |
| Login Scatola delle Esperienze | Accento blu | Carte multi-credenziale con "+" per aggiungere partecipante |
| Registrazione | Neutro | Nome visualizzato, email, password |
| Unisciti via invito | Chip accento verde | Campo inserimento codice + "Continua" |

L'inserimento invito è raggiungibile dall'auth senza login completo — conduce alla schermata anteprima dopo la validazione del codice.

### Presentazione tipi scatola

Undici tipi appaiono in una **griglia a due colonne** con:

- Sigillo tipo (badge icona)
- Titolo
- Sottotitolo suggerimento
- Colore evidenziazione distinto per tipo

Il catalogo ha sezioni di presentazione interne (amici, coppia, personale, sociale) ma l'UI di creazione mostra un **elenco piatto** senza header di sezione.

### Carte esperienza

**Carta elenco (modalità Esperienze):** badge intensità, punti parametri o riga compatta, sigillo, descrizione troncata o nascosta a seconda della paternità.

**Carta estrazione (modalità Scatola delle Esperienze):** carta a due lati con animazione flip asse Y. Copertina: intensità, parametri, sigillo. Fronte: descrizione completa + riflessione + nome visualizzato autore.

### Azioni distruttive

**Elimina scatola** e **Lascia gruppo** usano:

- Accento rosso o avviso sul pulsante conferma
- Riepilogo impatto (conteggio esperienze / perdita appartenenza)
- Annulla come default sicuro (pulsante secondario)

**Elimina esperienza** (solo autore): dialogo conferma più semplice; nessuna cascata oltre il singolo elemento.

### Foglio condivisione invito

Share sheet nativo con messaggio precompilato:

*"Unisciti al nostro gruppo su Intensity — [link]. Oppure inserisci il codice: [CODICE]"*

Codice mostrato in monospace, grande, copiabile. Scadenza mostrata come data leggibile.

### Tono di voce

| Contesto | Stile |
|----------|-------|
| Onboarding | Caldo, narrativo, seconda persona |
| Guida rapida | Regole dirette, verbi imperativi |
| Suggerimento allineamento | Delicato, ambra — "Prendetevi un momento insieme prima di rivelare" |
| Errori | Linguaggio semplice, recupero azionabile |
| Stati vuoti | Incoraggiante, mai colpevolizzante |

**Esempi:**

- ✓ "Estrai di nuovo se questa non si adatta al momento."
- ✓ "Tutti nella stanza dovrebbero appartenere allo stesso gruppo."
- ✗ "Invalid group_combination_error."

### Localizzazione

L'interfaccia supporta **inglese**, **portoghese (Brasile)** e **italiano**. I termini di dominio sono tradotti in modo coerente (vedi documenti localizzati). Gli esempi dei pacchetti suggerimenti seguono la lingua dell'interfaccia dove esistono pacchetti localizzati; gli esempi canonici di authoring restano in portoghese nel catalogo embedded.

### Cosa l'identità evita deliberatamente

- Badge gamification o streak
- Estetica social feed
- Pattern UI enterprise corporate
- Copy di urgenza aggressiva o FOMO

## Decisioni assunte in questa riscrittura

- L'UI **Invito** usa accento verde per distinguersi dalle modalità auth.
- **Elimina scatola** segue lo stesso pattern di conferma distruttiva di lascia gruppo.
- Le etichette filtro nell'UI usano **Esatto** e **Fino a** (non naming interno "fixed/max").
