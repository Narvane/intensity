# Plataformas e Ambientes

Este documento descreve onde o Intensity roda — plataformas de execução, ambientes de implantação e padrões de uso de dispositivos. É escrito para arquitetos e engenheiros seniores que planejam infraestrutura e distribuição do cliente.

---

## Curta

O Intensity roda em **duas plataformas de produto**: um **cliente mobile** (iOS e Android via Capacitor) e um **servidor centralizado** (API + PostgreSQL). A **distribuição canônica** é pelas lojas de apps; não há produto web/PWA geral. **Local** emparelha API em localhost com servidor de desenvolvimento Vite ou builds em emulador; **produção** executa API e banco em Docker em uma VPS enquanto clientes de loja chamam a API HTTPS pública. Uma stack **demo pública** separada na mesma VPS serve a mesma UI React no browser contra um banco demo isolado (preview para portfólio / recrutadores — **não** é staging de release).

---

## Média

### Plataformas de execução

| Plataforma | Papel | Instâncias |
|------------|-------|------------|
| **Cliente mobile** | UI completa do produto, fluxos, ritual de sorteio, sessão local | Uma instalação por dispositivo do participante |
| **Servidor** | API REST + PostgreSQL co-localizado | Produção (+ demo pública opcional) em uma VPS |

**Topologia:** muitos clientes mobile → uma API REST → um banco de dados. Sem sincronização peer-to-peer, sem CDN, sem message broker. A demo pública adiciona um segundo par API+DB e um host SPA estático atrás do mesmo reverse proxy — ainda sem segunda topologia de produto.

### Padrões de uso de dispositivos

| Modo | Padrão de dispositivo |
|------|----------------------|
| **Experiências** | Cada participante usa seu próprio celular para registrar ideias |
| **Caixa de Experiências** | Ritual em grupo (navegar caixinhas, convidar, excluir, sortear, revelar) em **um celular compartilhado**; contribuições podem vir de dispositivos separados |

Aceitação de convite e contribuição individual acontecem em dispositivos pessoais; o ritual de sorteio assume co-presença em uma tela compartilhada. A demo pública permite que uma única sessão de browser aproxime fluxos multi-conta trocando credenciais.

### Ambientes

| Ambiente | Cliente | API | Banco de dados |
|----------|---------|-----|----------------|
| **Local** | Servidor dev Vite ou build debug Capacitor | `localhost:8080` | PostgreSQL via Docker Compose |
| **Produção** | Builds de loja (AAB/IPA) | HTTPS na VPS (`api.` / deep-link `app.`) | Container PostgreSQL na mesma VPS |
| **Demo pública** | SPA Vite estático (`demo-intensity.`) | HTTPS (`demo-intensity-api.`), profile Spring `demo` | Postgres isolado + seed com reset diário |

**Não** há ambiente de staging / promoção pré-produção. Demo é só amostra para preview do produto; nunca deve compartilhar JWT secret nem volumes de banco da produção. Ops: @ref:demo-plan — [`demo-plan.md`](../../../demo-plan.md); @ref:deploy-readme — [deploy/README.md](../../../deploy/README.md).

### Requisitos de runtime

- Mobile: iOS e Android na versão atual menos duas versões principais
- Servidor: VPS Linux, Docker 24+, Docker Compose v2
- Rede obrigatória para todas as operações persistidas (sem baseline offline)

---

## Detalhada

### Plataforma mobile

O cliente é um **app híbrido**: UI React em shell WebView Capacitor com assets estáticos embutidos após build. Capacidades nativas usadas minimamente: ciclo de vida do app, barra de status, splash screen, preferências locais (idioma, flag de onboarding).

Distribuição **de produto** exclusivamente via **Google Play** (AAB) e **Apple App Store** (IPA). Sideload e PWA geral ficam fora de escopo.

Deep links de **URLs de convite** em produção resolvem no app instalado (Universal Links / App Links) ou pedem instalação da loja se ausente. Links de convite da demo usam o host da SPA demo (`/join`) e **não** devem constar nos arquivos de associação nativa do domínio de produção.

### Plataforma servidor

Processo JVM único (Spring Boot) atrás de reverse proxy (Caddy ou equivalente) terminando TLS. PostgreSQL 16 co-localizado na stack Compose em uma VPS. Produção e demo usam projetos Compose, volumes e JWT secrets separados; a demo reutiliza a mesma imagem GHCR da API com `SPRING_PROFILES_ACTIVE=demo`.

Escalonamento horizontal não é baseline — arquitetura aceita API de instância única com caminho de evolução futura documentado em decisões arquiteturais.

### Topologia de desenvolvimento local

```
Máquina do desenvolvedor
├── client/     npm run dev → browser :5173
├── api/        spring-boot:run → :8080
└── docker      postgres → :5432

Opcional: Capacitor copy → emulador Android (10.0.2.2:8080) ou dispositivo (IP LAN)
Opcional: SPRING_PROFILES_ACTIVE=demo contra DB intensity_demo
```

Variáveis de ambiente:

| Variável | Papel |
|----------|-------|
| `VITE_API_URL` | URL base da API no build do cliente |
| `VITE_INVITE_BASE_URL` | Host dos links de convite (produção ou demo) |
| `VITE_API_PROXY_TARGET` | Proxy opcional de `/v1` no Vite (dev local) |
| `VITE_DEMO` | Quando `true`, exibe banner demo e atalhos de auth |

TTL dos JWT da API (padrões em `application.yml`, sobrescrevíveis em produção/demo):

| Sessão | Propriedade | Padrão |
|--------|-------------|--------|
| Experiences | `intensity.jwt.expiration-seconds` | 2_592_000 (30 dias) |
| Experience Box | `intensity.jwt.experience-box-expiration-seconds` | 14_400 (4 horas) |

### Topologia de produção

```
Lojas de apps → Clientes mobile
                ↓ HTTPS REST
           VPS (Docker Compose)
             ├── reverse proxy :443
             ├── container API :8080
             └── container PostgreSQL
```

Deploy disparado por webhook após CI enviar imagem ao registry.

### Topologia da demo pública (mesma VPS)

```
Browser → demo-intensity.<domínio> (nginx SPA estático)
                ↓ HTTPS REST
         demo-intensity-api.<domínio> → intensity-demo-api (profile demo)
                            → postgres demo (seed + reset diário)
```

O Caddy (compose de produção) termina TLS dos hosts demo e faz reverse proxy para os containers demo na rede Docker compartilhada `intensity`.

### O que está explicitamente ausente

Distribuição web/PWA geral de produto, BaaS, Kubernetes, VPS de staging para promoção de release, CDN, WebSockets, gRPC, GraphQL, sincronização multi-dispositivo em tempo real durante o sorteio.

## Decisões assumidas nesta reescrita

- Deep links de convite do **produto de loja** são **preocupação de plataforma mobile** (App Links / Universal Links).
- Hosting web da demo pública é preocupação de **portfólio/preview**, não um segundo canal de produto.
- Fluxos de exclusão de caixinha e convite exigem rede; sem fila offline na baseline.
