# Piattaforme e Ambienti

Questo documento descrive dove Intensity viene eseguito — le piattaforme di esecuzione, la topologia di deployment e quante istanze di ciascun componente esistono in produzione.

**Pubblico:** architetti e ingegneri senior che devono comprendere il layout strutturale della soluzione senza dettagli di implementazione o operativi.

---

## Breve

Intensity gira su **due piattaforme**: un **client mobile** sui telefoni dei partecipanti e un'**API centralizzata** su un server. Il client viene distribuito su **molti dispositivi**; l'API gira come **istanza unica** in **un ambiente server**. Un **database** è connesso a quell'ambiente ed è accessibile solo dall'API.

---

## Media

### Piattaforme di esecuzione

| Piattaforma | Ruolo | Istanze |
|-------------|-------|---------|
| **Mobile** | Ospita l'applicazione client — interfaccia, flussi di interazione e comportamento centrale del prodotto | Un'installazione per dispositivo del partecipante |
| **Server** | Ospita l'API e il database connesso | Un ambiente centralizzato |

Non esiste un client web nell'architettura attuale. Il prodotto viene erogato esclusivamente tramite l'applicazione mobile.

### Topologia di deployment

```
┌─────────────────────────────────────────────────────────┐
│  Ambiente server (istanza unica)                        │
│  ┌─────────┐      ┌──────────────┐                        │
│  │   API   │ ───► │   Database   │                        │
│  └────▲────┘      └──────────────┘                        │
└───────┼─────────────────────────────────────────────────┘
        │ REST
        │
   ┌────┴────┬──────────┬──────────┐
   │         │          │          │
┌──▼──┐  ┌──▼──┐   ┌──▼──┐   ┌──▼──┐
│Tel. │  │Tel. │   │Tel. │   │Tel. │   ... (molti client)
│Client│  │Client│   │Client│   │Client│
└─────┘  └─────┘   └─────┘   └─────┘
```

### Modello di ambienti

- **Ambiente client:** ogni dispositivo mobile del partecipante. La stessa build del client gira in modo indipendente su ogni telefono.
- **Ambiente server:** un runtime centralizzato unico dove API e database coesistono. Tutti i client convergono su questo ambiente come fonte di verità dei dati persistiti.

L'asimmetria è intenzionale: **molti client, un'API**. La registrazione individuale delle esperienze da parte di ogni partecipante richiede uno strato di persistenza condiviso, mentre l'esperienza del prodotto vive su ogni dispositivo.

---

## Dettagliata

### Piattaforma mobile

La piattaforma mobile è dove i partecipanti interagiscono con Intensity. Ospita:

- L'interfaccia completa e la struttura di navigazione
- Schermate di onboarding, autenticazione e flussi di creazione
- Il rituale del momento condiviso (estrazione, allineamento, rivelazione della card)
- Preferenze del client non persistite nel modello di dominio (come la lingua dell'interfaccia)

Ogni telefono esegue la propria istanza del client. Non è richiesto che tutti i partecipanti usino lo stesso modello di dispositivo o versione del sistema operativo oltre a quanto supportato dall'applicazione mobile.

In **modalità Esperienze**, ogni partecipante usa tipicamente il proprio telefono per registrare esperienze individualmente. In **modalità Box Esperienze**, il rituale di gruppo — navigare box, estrarre, rivelare — avviene su **un telefono condiviso**, mentre i contributi possono essere stati registrati da dispositivi separati.

### Piattaforma server

La piattaforma server esiste per centralizzare i dati persistiti. Ospita:

- L'**API** — l'unico punto di ingresso a livello applicativo per lettura e scrittura dei dati di dominio
- Il **database** — archivio di persistenza esclusivo del modello di dominio

L'API gira in **istanza unica** all'interno di un ambiente server. Non esiste topologia multi-regione o API scalata orizzontalmente nell'architettura attuale.

### Cosa gira dove

| Responsabilità | Client mobile | Server (API + database) |
|----------------|---------------|---------------------------|
| Interfaccia e flussi UX | ✓ | — |
| Rituale di estrazione e rivelazione | ✓ | — |
| Persistenza registrazione esperienze | invoca API | ✓ |
| Dati partecipante, gruppo, box, esperienza | legge via API | ✓ (fonte di verità) |
| Autenticazione contro credenziali persistite | invoca API | ✓ |
| Preferenza lingua interfaccia | ✓ (locale) | — |
| Pacchetti suggerimenti predefiniti | ✓ (incorporati) | — |

### Limiti fuori ambito

L'architettura attuale non include:

- Applicazione web o client basato su browser
- Topologia separata di staging o multi-ambiente (oltre all'ambiente server unico descritto qui)
- Funzionamento offline del client (identificato come direzione futura altrove)

Specifiche operative — provider di hosting, containerizzazione, pipeline CI/CD, monitoraggio — appartengono al livello Ingegneria e Operazioni.
