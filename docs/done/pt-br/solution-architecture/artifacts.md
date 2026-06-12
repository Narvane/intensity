# Artefatos

Este documento cataloga os blocos estruturais do Intensity — as aplicações, serviços e componentes de persistência que compõem a solução e como as responsabilidades se distribuem entre eles.

**Público:** arquitetos e engenheiros seniores que precisam entender o que existe estruturalmente na solução sem detalhe de stack tecnológica ou procedimento de deploy.

---

## Curta

O Intensity é composto por **dois artefatos de aplicação** e **um artefato de persistência**: um **client mobile**, uma **API orientada a recursos** e um **banco de dados**. O **client concentra o núcleo** do produto — interface, fluxos e comportamento de jogo. A **API centraliza os dados** como fonte única da verdade. O **banco de dados** armazena o modelo de domínio e é acessado exclusivamente pela API.

---

## Média

### Inventário de artefatos

| Artefato | Tipo | Responsabilidade |
|----------|------|------------------|
| **Client** | Aplicativo mobile | Núcleo do produto: interface, fluxos de usuário, mecânica de sorteio, lógica do lado do client |
| **API** | Aplicação de servidor | Acesso orientado a recursos; orquestração de persistência |
| **Banco de dados** | Armazenamento de persistência | Armazenamento de dados de domínio; fonte da verdade por trás da API |

### Divisão de responsabilidades

```
┌──────────────────────────────────────────────┐
│                  CLIENT                       │
│  • Interface e estrutura da experiência       │
│  • Navegação e orquestração de telas          │
│  • Comportamento de sorteio, filtro e revelação│
│  • Assistente de criação e pacotes de sugestão│
│  • Preferências locais (ex.: idioma da UI)    │
│  • Consumo da API para operações persistidas  │
└──────────────────────┬───────────────────────┘
                       │ REST (recursos)
┌──────────────────────▼───────────────────────┐
│                    API                        │
│  • Endpoints de recursos para entidades       │
│  • Autenticação e autorização                 │
│  • Validação na fronteira de persistência     │
│  • Ponto único de acesso ao banco de dados    │
└──────────────────────┬───────────────────────┘
                       │
┌──────────────────────▼───────────────────────┐
│              BANCO DE DADOS                   │
│  • Participantes, grupos, caixinhas, experiências│
│  • Fonte da verdade de todos os dados persistidos│
└──────────────────────────────────────────────┘
```

### Client como núcleo do produto

O client não é uma camada fina de apresentação. Ele carrega o valor estrutural do produto:

- A interface guiada que ajuda os jogadores a usar a criatividade e criar momentos
- O assistente de criação e as sugestões pré-definidas que funcionam como tutorial implícito
- O ritual do momento compartilhado (filtros de intensidade, dica de alinhamento, revelação com flip de card)
- A mecânica de sorteio em si

As regras de negócio da aplicação são intencionalmente simples — essencialmente um sorteador sobre experiências coletadas. A complexidade e a diferenciação estão em como a interface estrutura a experiência, não em orquestração no servidor.

### API como centralizadora de dados

A API é uma **API de recursos**, não um Backend-for-Frontend (BFF). Expõe recursos de domínio para operações de criação, leitura, atualização e exclusão. Não agrega, remodela ou adapta respostas a telas específicas.

Esse papel é indispensável para:

- **Centralizar os dados** para que todas as instâncias do client compartilhem o mesmo estado persistido
- **Registrar experiências individualmente** para cada participante, cada um a partir do seu próprio dispositivo
- **Manter grupos, caixinhas e experiências** como domínio coerente entre dispositivos

### Banco de dados como persistência exclusiva

O banco de dados guarda tudo no modelo de dados funcional: participantes, grupos, caixinhas e experiências. Nenhum client armazena dados de domínio como fonte da verdade. Clients podem cachear ou manter estado transitório (como resultados de sorteio), mas a persistência sempre flui pela API até o banco de dados.

---

## Detalhada

### Client mobile

O client mobile é o artefato principal da solução. Os participantes o instalam em seus celulares. É o único artefato que escala para **muitas instâncias** — uma por dispositivo.

**Detém:**

| Área | O que o client trata |
|------|----------------------|
| **Apresentação** | Todas as telas, overlays, estados de carregamento/vazio/erro |
| **Fluxos de interação** | Bootstrap, onboarding, autenticação, caminho Experiências, caminho Caixa de Experiências |
| **Comportamento de jogo** | Sorteio aleatório com filtros de intensidade; resultados transitórios de sorteio |
| **Experiência de criação** | Assistente em cinco etapas, pacotes de sugestão, classificação de parâmetros |
| **Contexto de sessão** | Modo de acesso ativo, grupo selecionado, caixinha selecionada (escopo operacional) |
| **Estado local** | Preferência de idioma da interface; flag de onboarding na primeira execução |

**Delega à API:**

| Área | O que o client solicita à API |
|------|-------------------------------|
| **Autenticação** | Validação de credenciais contra participantes persistidos |
| **Cadastro** | Criação de novo participante |
| **CRUD de experiências** | Criar, listar, excluir experiências em uma caixinha |
| **Gestão de caixinhas** | Listar e criar caixinhas para um grupo |
| **Resolução de grupo** | Resolver o grupo formado pelos participantes autenticados |

O client não implementa padrão BFF. A modelagem de dados por tela acontece no lado do client após receber representações de recursos da API.

### API

A API é um artefato de aplicação único no servidor. Roda em um ambiente e atende todas as instâncias do client.

**Características:**

- **Orientada a recursos:** endpoints mapeiam recursos de domínio (participantes, grupos, caixinhas, experiências), não telas ou visões compostas
- **Sem estado na camada de aplicação:** sessão e contexto de navegação vivem no client; a API trata requisições de forma independente
- **Gateway exclusivo do banco de dados:** nenhum client se conecta diretamente ao banco de dados

**Não detém:**

- Layout de telas ou decisões de navegação
- Lógica de sorteio ou comportamento do ritual de revelação
- Conteúdo dos pacotes de sugestão ou orquestração das etapas do assistente
- Idioma da interface ou estado de onboarding

### Banco de dados

O banco de dados é um artefato de persistência conectado à API dentro do ambiente de servidor. Não é um artefato de aplicação implantado separadamente com lógica de negócio própria, mas é um componente estrutural distinto da arquitetura.

**Armazena:**

- Registros de participantes (nome de exibição, e-mail, credenciais)
- Associações de grupo derivadas da autenticação no modo Caixa de Experiências
- Caixinhas (nome, tipo, grupo proprietário)
- Experiências (descrição, intensidade, parâmetros, reflexão, autor, timestamps, selo de integridade)

**Não armazena:**

- Resultados de sorteio (transitórios, apenas no client)
- Preferência de idioma da interface
- Estado de conclusão do onboarding
- Textos dos pacotes de sugestão (embutidos no client)

### Relacionamentos entre artefatos

| De | Para | Relacionamento |
|----|------|----------------|
| Client | API | Consome recursos REST; muitos-para-um |
| API | Banco de dados | Lê e escreve; um-para-um no ambiente de servidor |
| Client | Banco de dados | Sem conexão direta |

### O que não é artefato separado

Os itens a seguir fazem parte do client ou da API, não são artefatos autônomos:

- **Pacotes de sugestão** — conteúdo embutido no client
- **Onboarding e guia rápido** — fluxos exclusivos do client
- **Motor de sorteio** — comportamento no client, não serviço no servidor
- **Message brokers ou barramentos de eventos** — ausentes na arquitetura atual
