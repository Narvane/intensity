# Integrações e Comunicação

Este documento descreve como os componentes estruturais do Intensity se comunicam — os protocolos, padrões de interação e fronteiras de fluxo de dados entre client, API e banco de dados.

**Público:** arquitetos e engenheiros seniores que precisam entender a topologia de integração sem contratos de implementação ou detalhe de stack tecnológica.

---

## Curta

O **client** mobile se comunica com a **API** exclusivamente via **REST**. A API se comunica com o **banco de dados** por sua camada de persistência. Não há **mensageria**, **WebSockets** nem **conexão direta client–banco de dados**. A API expõe interface **orientada a recursos** — não agregados adaptados a telas.

---

## Média

### Mapa de comunicação

```
Client  ──REST──►  API  ──persistência──►  Banco de dados
  │                  │
  │                  └── gateway único para dados persistidos
  │
  └── sem caminho direto ao banco de dados
```

### Protocolos e padrões

| Integração | Protocolo / padrão | Direção |
|------------|-------------------|---------|
| Client → API | REST (HTTP) | Requisição/resposta |
| API → Banco de dados | Acesso de persistência | Leitura/escrita |
| Client → Banco de dados | — | Não permitido |
| Client ↔ Client | — | Sem comunicação peer-to-peer |
| API ↔ Serviços externos | — | Nenhum na arquitetura atual |

### REST como único canal client–servidor

Todas as operações persistidas fluem por chamadas REST do client à API:

- Autenticação e cadastro
- Listagem e criação de caixinhas
- Criação, listagem e exclusão de experiências
- Resolução de grupos a partir do login multi-participante

O client inicia toda interação. A API responde com representações de recursos. Não há push iniciado pelo servidor para os clients.

### Estilo de contrato: API de recursos

A API segue modelo **orientado a recursos**:

- Endpoints representam entidades de domínio (participantes, caixinhas, experiências), não fluxos de interface
- Respostas carregam dados de recursos, não view models específicos de tela
- O client é responsável por mapear recursos às necessidades da interface

Isso difere de um BFF (Backend-for-Frontend), que exporia endpoints moldados por tela ou por plataforma de client. O Intensity evita esse padrão deliberadamente.

### O que está explicitamente ausente

| Mecanismo | Status |
|-----------|--------|
| Filas de mensagens / barramentos de eventos | Não utilizados |
| WebSockets / push do servidor | Não utilizados |
| GraphQL | Não utilizado |
| gRPC | Não utilizado |
| Sincronização client-a-client | Não utilizada |
| Integrações com terceiros | Não utilizadas |

A ausência de mensageria e canais em tempo real reflete a simplicidade da aplicação: não há necessidade de sincronização multi-dispositivo ao vivo durante o ritual de sorteio (que acontece em um celular compartilhado), nem de orquestração complexa de eventos no servidor.

---

## Detalhada

### Modelo de interação client–API

O client opera como **consumidor** de recursos da API. Ciclos típicos de interação:

**Fluxo de autenticação:**
1. Client envia credenciais à API
2. API valida contra dados de participante persistidos
3. API retorna resultado da autenticação
4. Client estabelece contexto de sessão localmente

**Registro de experiência (modo Experiências):**
1. Client coleta entrada do assistente localmente
2. Client envia recurso de experiência à API
3. API persiste no banco de dados
4. API retorna representação da experiência criada
5. Client atualiza a visão da lista de experiências

**Momento compartilhado (modo Caixa de Experiências):**
1. Client solicita experiências da caixinha ativa via API
2. API retorna recursos de experiência do banco de dados
3. Client executa sorteio, filtro e revelação localmente
4. Resultado do sorteio permanece no client — sem escrita de volta à API

Esse último padrão é significativo: o sorteio é inteiramente no client. A API não participa do momento ritual além de fornecer o pool de experiências.

### Características das requisições

| Propriedade | Comportamento |
|-------------|---------------|
| Iniciador | Sempre o client |
| Acoplamento | Fraco — representações de recursos, não contratos de tela |
| Estado | Sessão e contexto de navegação mantidos no client |
| Tratamento de falha | Client exibe erros (ex.: snackbar em falha de API) |
| Idempotência | Não mandatada arquiteturalmente nesta camada |

### Interação API–banco de dados

A API é a **única aplicação** que acessa o banco de dados. Essa fronteira garante:

- Validação consistente antes da persistência
- Fonte única da verdade
- Sem fragmentação de dados entre instâncias do client

A API traduz operações REST em ações de persistência. ORM, camada de consulta ou pool de conexões pertencem à camada de Engenharia.

### Fluxo de dados por operação

| Operação | Papel do client | Papel da API | Papel do banco |
|----------|-----------------|--------------|----------------|
| Cadastrar participante | Envia payload de cadastro | Valida e persiste | Armazena participante |
| Login (Experiências) | Envia credenciais | Valida | Lê participante |
| Login (Caixa de Experiências, multi-usuário) | Envia múltiplas credenciais | Resolve grupo | Lê participantes e grupo |
| Criar caixinha | Envia recurso de caixinha | Persiste com associação ao grupo | Armazena caixinha |
| Listar caixinhas | Solicita por grupo | Consulta e retorna | Lê caixinhas |
| Criar experiência | Envia recurso de experiência | Persiste com autor e caixinha | Armazena experiência |
| Listar experiências | Solicita por caixinha | Consulta e retorna | Lê experiências |
| Excluir experiência | Envia requisição de exclusão | Remove se autorizado | Exclui experiência |
| Sortear experiência | Filtra e seleciona localmente | — (sem chamada) | — |
| Alterar idioma da interface | Armazena localmente | — (sem chamada) | — |

### Modelo de sincronização

Não há sincronização em tempo real entre clients. A consistência é **eventual via REST**:

- Quando um participante registra uma experiência do seu celular, outros clients a veem na próxima leitura da API
- Durante o ritual do momento compartilhado em um celular, o client atualiza o pool de experiências da API antes de sortear

Nenhum mecanismo de notificação push ou atualização ao vivo avisa clients quando dados mudam. Cada client puxa o estado atual quando necessário.

### Integrações externas

A arquitetura atual não possui integrações com serviços externos — sem gateways de pagamento, pipelines de analytics, provedores de identidade ou CDNs no nível arquitetural. Se adicionadas no futuro, integrariam pela API ou pelo client como novos caminhos de comunicação documentados separadamente.

### Limites para camadas inferiores

Este documento descreve *o que* se comunica e *como* no nível estrutural. Não especifica:

- Códigos de status HTTP, headers ou schemas de payload
- Formato ou expiração de token de autenticação
- Configuração de conexão com banco de dados
- Políticas de retry ou circuit breakers

Esses detalhes pertencem à camada de Engenharia e Operação.
