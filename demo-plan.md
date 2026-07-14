# Plano — ambiente demo (sample público)

Objetivo: permitir que recrutadores e curiosos **usem o produto de verdade no browser**, sem baixar app, sem tocar no banco de produção, com dados demonstrativos que se regeneram sozinhos.

Princípio: **mesmo código** (`api/` + `client/`). Só configs, seed e um stack Compose paralelo. Não é fork nem “fake UI”.

Referências: @ref:deploy-readme — [deploy/README.md](deploy/README.md); @ref:en-platforms — [platforms and environments](docs/en/solution-architecture/platforms-and-environments.md); @ref:en-how-it-works — [how it works](docs/en/product-conception/how-it-works.md).

---

## 1. Resultado esperado

| Item | Valor |
|------|--------|
| URL | `https://demo.<domínio>` (SPA React) |
| API | `https://demo-api.<domínio>` |
| Contas | três fixas; casal (2) + trio com amigos (3) |
| Dados | seed rico; reset diário automático |
| Isolamento | Postgres, JWT secret, volumes e domínios **separados** de prod |

No README do repo: link “Live demo” + e-mails/senha + aviso de reset.

---

## 2. Arquitetura (simples)

```
Internet :443
    ↓
  Caddy (pode ser o mesmo container com hosts extras, ou Compose demo)
    ├── demo.<domínio>      → arquivos estáticos do Vite (client dist)
    └── demo-api.<domínio>  → api:8080 (SPRING_PROFILES_ACTIVE=demo)
  postgres-demo (volume próprio)
```

### O que reutilizar

- Mesma imagem GHCR da API (`API_IMAGE` / tag do CI).
- Mesmo `npm run build` do client (modo Vite `demo`).
- Padrão atual de deploy: `deploy.sh` + compose + Caddy.

### O que criar (artefatos novos)

| Artefato | Papel |
|----------|--------|
| `api/.../application-demo.yml` | Datasource via env, CORS do demo, JWT via env, swagger opcional |
| `api/.../db/demo/seed-demo.sql` (+ runner) | Participantes, grupo, boxes, experiências, allowlist |
| `client/.env.demo` | `VITE_API_URL`, `VITE_INVITE_BASE_URL`, `VITE_DEMO=true` |
| `deploy/docker-compose.demo.yml` | api + postgres + (static/caddy ou serviço `web`) |
| `deploy/.env.demo.example` | Domínios, senhas, image tag |
| `deploy/deploy-demo.sh` | `pull` + `up` da stack demo |
| `deploy/reset-demo.sh` | drop schema / volume + re-seed |
| Cron no VPS | chama `reset-demo.sh` 1×/dia |

Projeto Compose distinto (`COMPOSE_PROJECT_NAME=intensity-demo`) para nunca confundir containers/volumes com prod.

---

## 3. Dados padrão — o que demonstrar

O visitante precisa **sentir o ritmo do produto** em poucos minutos:

> coletar → entrar no grupo → ver caixinhas → sortear → alinhar → revelar

Por isso o seed não é “usuário vazio”: já tem história compartilhada.

### 3.1 Personas (3 usuários, 2 grupos)

| Campo | Usuário A | Usuário B | Usuário C |
|-------|-----------|-----------|-----------|
| Display name | **Leo** | **Maya** | **Nico** |
| E-mail | `leo@demo.intensity.app` | `maya@demo.intensity.app` | `nico@demo.intensity.app` |
| Senha | `demo1234` (igual nos três — fricção zero) | idem | idem |
| Allowlist | os três inseridos no seed | idem | idem |

Narrativa: Leo e Maya são um casal; Nico é amigo do casal. O seed mostra **dois contextos reais** do produto — intimidade a dois e ritual com grupo de amigos — e que a mesma pessoa pode estar em mais de um grupo.

| Grupo | Membros | Cor | Papel no demo |
|-------|---------|-----|----------------|
| **Leo & Maya** | Leo, Maya | `coral` | Casal — caixinhas a dois, tour principal de draw |
| **Trio de viagem** | Leo, Maya, Nico | `teal` | Amigos (3) — caixinha `VIAGENS_COM_AMIGOS` |

> Não reutilizar `alice@example.com` / `bob@example.com` do Flyway V2 no texto público do README — esses são fixtures de teste. Contas demo têm domínio próprio.

### 3.2 Caixinhas (5)

**Grupo “Leo & Maya” (casal)**

| # | Nome | Tipo (`BoxType`) | `requireAllParticipants` | Por quê |
|---|------|------------------|--------------------------|---------|
| 1 | **Fim de semana** | `SAIDAS_EM_CASAL` | `false` | Box principal cheia — destino do tour de draw/reveal |
| 2 | **Sair da rotina** | `SAIR_DA_ROTINA` | `false` | Segunda temática; mostra variedade de tipos |
| 3 | **Só nós dois** | `MOMENTOS_DE_CONEXAO` | `true` | Só aparece com Leo **e** Maya na sessão conjunta |

Box 3 vazia ou com 1–2 ideias leves: útil para mostrar listagem filtrada / criação, sem poluir o ritual principal.

**Grupo “Trio de viagem” (3 amigos)**

| # | Nome | Tipo (`BoxType`) | `requireAllParticipants` | Por quê |
|---|------|------------------|--------------------------|---------|
| 4 | **Próxima viagem** | `VIAGENS_COM_AMIGOS` | `false` | Caixinha de **grupo**; ideias dos três para uma viagem juntos |
| 5 | **Rolês da galera** | `EXPERIENCIAS_COM_AMIGOS` | `true` | Só na sessão com os **três** autenticados — contraste com a regra a dois |

Box 5 pode ficar quase vazia (0–2 ideias): o valor é mostrar `requireAllParticipants` num grupo maior.

### 3.3 Experiências (pool do draw)

**Box “Fim de semana” — ~8 ideias** (só Leo/Maya, intensidades 1–5):

| Intensidade | Autor | Ideia (rascunho) | Params (E/U/N) | Type cover | Reflection |
|-------------|---------|------------------|----------------|------------|------------|
| 1 | Maya | Café da manhã num lugar novo do bairro, sem celular na mesa | 1/2/2 | `connection` | Quero presença simples |
| 2 | Leo | Trilha curta + piquenique improvisado | 3/2/3 | `explore` | — |
| 2 | Maya | Noite de jogos de tabuleiro em casa, só nós | 1/1/2 | `connection` | — |
| 3 | Leo | Jantar sem combinar o restaurante — um escolhe no caminho | 2/4/3 | `randomness` | Gosto da surpresa controlada |
| 3 | Maya | Sessão de fotos um do outro na cidade, 10 poses aleatórias | 2/3/4 | `creativity` | — |
| 4 | Leo | Karaoke em bar desconhecido (uma música cada) | 3/4/4 | `exposure` | Fora da zona de conforto, mas juntos |
| 4 | Maya | Cozinhar um prato que nunca fizemos, só com ingredientes sorteados no mercado | 4/3/4 | `constraints` | — |
| 5 | Leo | Passar a tarde “sem plano”: sair andando e só aceitar o que aparecer | 2/5/5 | `explore` | Quero ver se a gente se diverte no improvável |

**Box “Sair da rotina” — ~4 ideias** (Leo/Maya, intensidades médias/altas):

- Escrever cartas um para o outro e ler em voz alta  
- Trocar playlists “segredos” e ouvir de olhos vendados  
- Visitar um museu / exposição sem ler as placas primeiro  
- Fazer algo que um sempre quis e o outro nunca topou (combinar antes)

**Box “Próxima viagem” — ~6 ideias** (os três autores; tom de grupo):

| Intensidade | Autor | Ideia (rascunho) | Params (E/U/N) | Type cover | Reflection |
|-------------|---------|------------------|----------------|------------|------------|
| 2 | Nico | Hostel barato + um dia só de mapa aberto, sem itinerário | 2/3/3 | `explore` | Quero ver o lugar sem guia |
| 2 | Maya | Pôr do sol num mirante que nenhum de nós conhece | 2/2/3 | `connection` | — |
| 3 | Leo | Um jantar “local demais”: pedir o que o garçom recomendaria para a própria família | 2/4/3 | `randomness` | — |
| 3 | Nico | Aluguel de bikes e seguir a rota mais longa no mapa | 4/3/3 | `explore` | — |
| 4 | Maya | Trocar de câmera/celular um com o outro por meio dia e só fotografar o outro | 2/3/4 | `creativity` | Dinâmica boa a três |
| 5 | Leo | Pegar um transporte regional sem destino fixo e descer onde “parecer legal” | 3/5/5 | `explore` | Só se os três toparem o risco |

Selo (`seal`) e demais constraints: o seed usa o **mesmo pipeline da API** (ver §5) para não inventar hashes inválidos.

### 3.4 Convite pré-seedado (opcional, útil)

Um invite `ACTIVE` do **Trio de viagem** (crescer grupo / fluxo `/join` fica mais óbvio num grupo de amigos), com:

- `code` fixo documentável, ex. `DEMO01` (respeitar alfabeto do gerador: sem I/O/0/1 se a regra atual exigir)
- `link_token` UUID fixo
- `expires_at` longe o suficiente **ou** renovado a cada reset

Serve para mostrar validate/accept no browser **sem** criar invite na hora. Se o fluxo web de `/join` ficar para uma fase seguinte, o invite seed pode ser adiado — mas o plano já reserva.

### 3.5 Tour sugerido no README (~90s)

1. Abrir demo → banner “Ambiente demonstrativo”  
2. Login **Experiences** como Leo → ver **dois grupos** → abrir **Fim de semana** → ver ideias do casal  
3. Trocar para **Trio de viagem** → abrir **Próxima viagem** → ver ideias com Nico no meio  
4. Modo **Experience Box** → login conjunto Leo + Maya → draw em **Fim de semana** → alinhar → revelar  
5. (Opcional) Login conjunto Leo + Maya + Nico → Home do trio → ver **Próxima viagem** + box “Rolês da galera” (só com os três)  
6. (Opcional) Nico sozinho → adicionar uma ideia na viagem → reaparece após refresh  

Cobre os dois modos, multi-grupo e joint login 2 vs 3 — tudo trocando conta no mesmo browser.

### 3.6 O que **não** colocar no seed

- Dados íntimos explícitos demais (box `INTIMO_EM_CASAL` fica fora do seed público).  
- Quarto usuário ou terceiro grupo (ruído no pitch).  
- Histórico de draws (produto não persiste draws).  
- Dez caixas vazias.
---

## 4. Riscos de ambiente e bloqueios planejados

O app de loja e a VPS de prod **não** devem colidir com o demo. Abaixo: risco → mitigação.

### 4.1 Isolamento duro (obrigatório)

| Risco | Mitigação |
|-------|-----------|
| Contaminar Postgres prod | Volume e service name separados; nunca apontar `SPRING_DATASOURCE_*` demo para prod |
| JWT demo aceito em prod (e vice-versa) | `INTENSITY_JWT_SECRET` **diferente** por stack |
| Deploy demo derruba prod | `deploy-demo.sh` + compose project name distintos; webhook de prod **não** reinicia demo (opcional: segundo hook depois) |
| CORS abrir o demo no domínio errado | Em `application-demo.yml`, permitir **só** `https://demo.<domínio>` (+ localhost p/ debug) |

### 4.2 Invites e deep links (o ponto mais sensível)

Hoje:

- Links usam `VITE_INVITE_BASE_URL` (prod → `https://app.narvane.com.br/join`).
- Caddy em `APP_DOMAIN` **não** serve a SPA — só `/.well-known` + 404 (“abre no app”).
- Validate de invite é endpoint **público**.

| Risco | Mitigação |
|-------|-----------|
| Invite do demo gerar link de **prod** | Build demo com `VITE_INVITE_BASE_URL=https://demo.<domínio>/join` |
| App de loja abrir link demo e bater na API **prod** | Host diferente (`demo.` ≠ `app.`); associação Universal/App Links **não** inclui o host demo |
| Recrutador abrir invite no browser e ver 404 | Demo **hospeda a SPA**; rota `/join` funciona no web |
| Alguém colar token de invite demo na API prod | Bancos separados → validate falha / não encontra; sem vazamento cruzado |
| Preview público expor nomes do grupo demo | OK (são personas fictícias); não seedar e-mails no preview (API já omite e-mail) |

**Bloqueios / flags sugeridos (`intensity.demo.*` ou `VITE_DEMO`):**

1. **Client** `VITE_DEMO=true`: banner persistente (“Dados fictícios · reset diário”); credenciais de atalho opcionais na tela de login (pré-preencher Leo / Maya / Nico).  
2. **API** profile `demo`: property `intensity.demo.enabled=true`.  
3. **Invites em demo:**  
   - Geração de link **sempre** com base configurável `intensity.demo.invite-public-base-url` (redundância se algum client antigo aparecer).  
   - Ou, na v1: confiar no `VITE_INVITE_BASE_URL` do build + CORS restrito (mais simples). Preferir o mais simples primeiro; API-side só se surgir link errado na prática.  
4. **Registro aberto:** manter allowlist. Seed inclui só Leo/Maya/Nico (+ e-mails extras se quiser “convidar alguém”). Evita spam criando contas no demo público.  
5. **Não** registrar `demo.<domínio>` nos well-known de produção / manifests nativos.

### 4.3 Outros riscos

| Risco | Mitigação |
|-------|-----------|
| Docs dizem “sem web client / sem staging” | Demo é **exceção explícita** para portfolio; ao implementar, atualizar @ref:en-platforms (e traduções) numa tarefa do plano |
| Capacidade da VPS (2 APIs + 2 Postgres) | Demo pode usar a mesma imagem com limites modestos (`MEM` opcional); reset diário evita inchar disco |
| Visitante “estraga” o seed | Esperado — reset diário; opcional botão interno “Reset now” só com secret admin (fase 2) |
| Swagger público em demo | Decisão: **ligar** swagger-ui só em demo pode ajudar recrutador técnico; se ligar, não expor em prod |
| Rate limit / abuso | Opcional depois; baseline: allowlist + reset |

### 4.4 Política do que o demo **pode** alterar

- **Escrita liberada** (criar experiência, invite, editar grupo): experiência realista.  
- Reset diário restaura o snapshot.  
- Não precisa de modo read-only na v1.

---

## 5. Seed técnico

### Abordagem recomendada

**Script de aplicação pós-migrate**, não `R__` Flyway “cego”:

1. Subir API com Flyway normal (`V2`…`V9`).  
2. Rodar job/comando `DemoSeedRunner` (profile `demo`) **ou** `psql -f seed-demo.sql` gerado a partir de um small Java/`./mvnw` util que usa `PasswordEncoder` + `SealService`.

Motivo: senha BCrypt e seal SHA precisam ser idênticos à lógica de produção.

Idempotência: seed verifica “já existe `leo@demo.intensity.app`?” → skip; o **reset** é que limpa o schema e deixa o seed rodar de novo.

### Fluxo de reset diário

```
cron 03:00 UTC
  → deploy/reset-demo.sh
      → DROP SCHEMA public CASCADE; CREATE SCHEMA public;
      → restart api (Flyway reapply)
      → seed runner
```

Alternativa ainda mais burra (aceitável): `docker compose down -v` no volume demo + `up -d`.

---

## 6. Client web no demo

O client já roda no browser via Vite. Para demo:

1. `vite build --mode demo` (lê `.env.demo`).  
2. Caddy (ou nginx alpine) serve `dist/` em `demo.<domínio>`.  
3. SPA fallback (`try_files` → `index.html`) para rotas `/join`, `/box-home`, etc.  
4. Capacitor **não** é necessário nesse host.

Deep link nativo continua só em `app.<domínio>` (prod).

---

## 7. Fases de desenvolvimento

### Fase 0 — Decisões rápidas (30 min)

- [ ] Domínios DNS: `demo.` e `demo-api.` (ou path único atrás do mesmo host — preferir dois subdomínios, espelha prod).  
- [ ] Confirmar que a VPS aguenta o segundo Postgres.  
- [ ] Textos finais Leo/Maya/Nico + senha `demo1234`.

### Fase 1 — API demo profile + seed

- [ ] `application-demo.yml` + CORS.  
- [ ] Seed runner idempotente (3 participants, 2 groups, 5 boxes, ~18 experiences, allowlist).  
- [ ] Teste local: `SPRING_PROFILES_ACTIVE=demo` + Postgres scratch.  
- [ ] Script `reset-demo` testável no docker-compose da API.

### Fase 2 — Compose + Caddy demo na VPS

- [ ] `docker-compose.demo.yml` + `.env.demo.example`.  
- [ ] `deploy-demo.sh`.  
- [ ] TLS via Caddy para os dois hosts.  
- [ ] Health check `demo-api` / static `demo`.  
- [ ] Cron de reset.

### Fase 3 — Client modo demo

- [ ] `.env.demo` + build mode.  
- [ ] Banner `VITE_DEMO`.  
- [ ] Publicar `dist` no volume/serviço web.  
- [ ] Garantir `/join` no browser (validate + accept contra demo-api).  
- [ ] Atalhos de login (opcional, UX).

### Fase 4 — DX e portfolio

- [ ] Seção no @ref:readme com URL + credenciais + “reset diário”.  
- [ ] Atualizar @ref:en-platforms (e pt-br/it) mencionando ambiente **demo** sem chamar de staging de release.  
- [ ] (Opcional) CI: job `deploy-demo` após push em `master` (mesmo SHA da imagem prod).  
- [ ] (Opcional) screenshot / GIF no README.

### Fora de escopo (v1)

- Espelho contínuo de dados de prod.  
- Multi-região / CDN.  
- Push, e-mail, OAuth.  
- Quarto participante ou mais grupos.  
- Feature flags genéricas além de `demo.enabled`.

---

## 8. Ordem de implementação sugerida (PRs)

1. **PR A** — profile `demo` + seed + teste de integração “mundo Leo/Maya/Nico”.  
2. **PR B** — `deploy/*demo*` + docs de ops no `deploy/README.md`.  
3. **PR C** — client `.env.demo`, banner, hosting estático, README live demo.  
4. **PR D** — ajustes de docs de ambientes + refs; polimento invite/join web se necessário.

Cada PR deve ser utilizável sozinho em local; só B+C precisam da VPS.

---

## 9. Critérios de pronto

- [ ] Abrir `https://demo.<domínio>` sem instalar nada.  
- [ ] Login Leo vê dois grupos; joint Leo+Maya abre caixas do casal (“Só nós dois” só com os dois).  
- [ ] Joint Leo+Maya+Nico abre o trio; “Rolês da galera” só com os três; draw em “Próxima viagem” com ideias dos três.  
- [ ] Draw na “Fim de semana” com pool ≥ 5 ideias e intensidades variadas.  
- [ ] Criar experiência nova persiste até o próximo reset.  
- [ ] Invite gerado no demo aponta para `https://demo.<domínio>/join?t=…` e **nunca** para o host de prod.  
- [ ] Postgres/JWT/volumes de prod intactos após deploy e após reset demo.  
- [ ] README explica credenciais e reset em ≤ 5 linhas.

---

## 10. Estimativa grosseira

| Fatia | Esforço |
|-------|---------|
| Seed + profile API | ~0,5–1 dia |
| Compose/Caddy/cron | ~0,5 dia |
| Client demo + banner + join web | ~0,5–1 dia |
| Docs + DNS + primeiro deploy | ~0,5 dia |

**Total:** ~2–3 dias de trabalho focado, sem ouro.

---

## 11. Decisões travadas neste plano

1. Mesmo código / mesma imagem API; ambiente via profile + env.  
2. Três usuários (Leo, Maya, Nico); grupo casal + trio de amigos; cinco boxes (incl. `VIAGENS_COM_AMIGOS`); seed narrativo.  
3. Web-only para visitação; app store continua só em prod.  
4. Allowlist fechada + escrita liberada + reset diário.  
5. Isolamento por domínio, DB, JWT e compose project — sem “modo demo” dentro do processo prod.  
6. Bloqueio especial mínimo: flag visual no client + invite base URL demo; endurecer na API só se precisar.
