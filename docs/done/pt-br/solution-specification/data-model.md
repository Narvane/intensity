# Modelo de Dados

Este documento descreve o modelo de dados funcional do Intensity — as entidades de domínio, seus relacionamentos, taxonomias, parâmetros e conteúdo padrão que estruturam o funcionamento do produto. Especifica *o que existe* e *como os conceitos se relacionam*, sem detalhe de implementação.

**Público:** analistas, product owners, designers e QA funcional — pessoas que precisam entender o domínio da solução sem saber como ela foi construída.

---

## Curta

O domínio do Intensity gira em torno de **participantes** que formam **grupos**, coletam **experiências** em **caixinhas** temáticas e vivem juntos o momento de **sorteio e revelação**. Cada experiência carrega uma descrição, um nível de **intensidade** geral (1–5), três **parâmetros** (esforço, abertura, novidade) e a **reflexão** do proponente. Onze **tipos de caixinha** organizam ideias por contexto; dois **modos de acesso** (Experiências e Caixa de Experiências) definem quem pode fazer o quê. O cadastro é controlado por uma **lista de permissão**; resultados de sorteio são transitórios e não são armazenados.

---

## Média

### Entidades centrais

| Entidade | O que representa |
|----------|------------------|
| **Participante** | Pessoa cadastrada (nome de exibição, e-mail, credenciais) que pode contribuir e entrar em grupos |
| **Entrada na lista de permissão** | E-mail pré-aprovado autorizado a se cadastrar |
| **Grupo** | Conjunto de participantes que entraram juntos no modo Caixa de Experiências — identificado por essa combinação exata |
| **Caixinha** | Recipiente nomeado e temático onde as experiências de um grupo são coletadas |
| **Experiência** | Ideia concreta para fazer juntos, autoria de um participante, pertencente a uma caixinha |
| **Contexto de sessão** | Escopo operacional do uso atual: modo de acesso, grupo ativo, caixinha ativa |

### Como se conectam

```
Lista de permissão  →  autoriza  →  Participante
Participante  ↔  Grupo  (muitos-para-muitos, via quem entra junto)
Grupo  →  possui  →  Caixinha  (um-para-muitos)
Caixinha  →  contém  →  Experiência  (um-para-muitos)
Participante  →  autoria  →  Experiência  (um-para-muitos)
```

Um **grupo** não é nomeado manualmente — emerge da combinação única de participantes que se autenticam juntos no modo Caixa de Experiências. **Caixinhas** são criadas apenas nesse modo; **experiências** são registradas principalmente pelo modo Experiências.

### O que cada experiência carrega

| Atributo | Significado |
|----------|-------------|
| Descrição | Texto da experiência (até 1.000 caracteres) |
| Intensidade | Ousadia geral, níveis 1–5 |
| Parâmetros | Três avaliações de 1–5: esforço, abertura, novidade |
| Reflexão | Justificativa do proponente sobre aceitação do grupo |
| Autor | Quem registrou |
| Momento de registro | Quando foi salva |
| Selo de integridade | Impressão digital exibida nos cards como "Selo" |
| Caixinha pai | A qual caixinha pertence |

### Taxonomias em resumo

- **Modos de acesso:** Experiências (contribuição individual) e Caixa de Experiências (ritual em grupo)
- **Tipos de caixinha:** 11 categorias temáticas (passeios, viagens, intimidade, quebra de rotina, novidade, desconforto, conexão e mais — para casais e amigos)
- **Níveis de intensidade:** 1 Leve → 5 Adrenalina
- **Filtros de sorteio:** Qualquer, intensidade fixa ou intensidade máxima
- **Etapas do assistente de criação:** Sugestão → Reflexão → Parâmetros → Classificação → Bifurcação

### Parâmetros e padrões

- Cadastro exige e-mail na lista de permissão
- Tipo de caixinha padrão quando não especificado: **Saídas com amigos**
- Intensidade padrão no assistente e no filtro de sorteio: **3**
- Intensidade sugerida: média arredondada das três avaliações de parâmetros (proponente pode alterar)
- **165 sugestões de exemplo embutidas** (11 tipos × 5 níveis × 3 cada), atualmente em português para todos os idiomas da interface
- **Consequências, trocas e progressão gradual** existem apenas como orientação social — não como dados rastreados

---

## Detalhada

### Participante e cadastro

Um **participante** é quem concluiu o cadastro. Possui **nome de exibição** (mostrado nas listas de grupo), **e-mail** (identidade de login) e **credenciais** (e-mail + senha).

Antes de se tornar participante, o e-mail deve constar na **lista de permissão de cadastro**. É um controle administrativo — não um conceito que o usuário gerencia no app, mas define quem pode entrar. Entradas de exemplo incluem `proponente@intensity.app`, `membro1@intensity.app` e `membro2@intensity.app`.

Participantes cadastrados aparecem na interface de login da Caixa de Experiências para que o grupo selecione quem está presente.

**Não modelado:** foto de perfil, preferências de notificação ou configurações por usuário além do que o cliente armazena localmente (como idioma da interface).

### Grupo

Um **grupo** é o conjunto de pessoas que entraram no modo **Caixa de Experiências** juntas. Possui:

| Atributo | Significado |
|----------|-------------|
| Participantes | Membros deste grupo |
| Momento de criação | Quando essa combinação foi formada pela primeira vez |

**Regra de identidade:** a mesma combinação de participantes sempre corresponde ao mesmo grupo. Se Alice e Bob entram juntos, formam um grupo; se Alice, Bob e Carol entram, é outro grupo. Um participante pode pertencer a vários grupos conforme as combinações de cada sessão.

**Não modelado:** nome visível ao usuário, edição de grupo ou criação explícita fora da combinação de login.

### Caixinha

Uma **caixinha** é um recipiente temático onde experiências de um grupo são coletadas.

| Atributo | Significado |
|----------|-------------|
| Nome | Rótulo escolhido pelo usuário (ex.: "Festa de sábado") |
| Tipo | Uma das 11 categorias temáticas |
| Momento de criação | Quando a caixinha foi criada |
| Grupo pai | Qual grupo a possui |

Caixinhas são criadas no modo **Caixa de Experiências**, não durante a contribuição individual. Cada tipo carrega metadados de apresentação (cor de destaque, ícone, dica de subtítulo) que orientam o tipo de ideias esperadas.

**Não modelado:** renomear, editar ou excluir caixinha.

### Experiência

Uma **experiência** é uma ideia concreta para fazer juntos.

| Atributo | Restrição / regra |
|----------|-------------------|
| Descrição | Obrigatória; máx. 1.000 caracteres |
| Intensidade | Obrigatória; inteiro 1–5 |
| Esforço | Obrigatório; 1–5 estrelas |
| Abertura | Obrigatório; 1–5 estrelas |
| Novidade | Obrigatório; 1–5 estrelas |
| Reflexão | Obrigatória no fluxo atual; máx. 2.000 caracteres por campo |
| Autor | Registrado na criação; só o autor pode editar ou excluir |
| Selo de integridade | Derivado da descrição; exibido nos cards |

#### Regras de visibilidade

| Contexto | O que os outros veem |
|----------|----------------------|
| **Modo Experiências** (lista do autor) | Descrição completa das próprias experiências; outros veem apenas resumo (intensidade + selo, sem texto) |
| **Modo Caixa de Experiências** (sorteio) | Intensidade e parâmetros primeiro; descrição completa só após **Revelação** |

A interface também informa que **as experiências não são criptografadas** — aviso de transparência sobre como o texto é tratado.

#### Reflexão

O modelo funcional suporta três campos de reflexão:

| Campo | Comportamento atual |
|-------|---------------------|
| "Todos aceitariam essa experiência, por mais peculiar que seja?" | **Coletado** no assistente de criação |
| "Envolve todos?" | Suportado no modelo de dados; **não coletado** na interface atual |
| "Há um leve desconforto?" | Suportado no modelo de dados; **não coletado** na interface atual |

Quando apenas um campo está preenchido, os cards mostram essa pergunta única; quando vários estão preenchidos, todos os blocos aparecem.

### Contexto de sessão

Embora não seja uma entidade gerenciada pelo usuário, o **contexto de sessão** delimita toda operação:

| Elemento | Valores |
|----------|---------|
| Modo de acesso | **Experiências** ou **Caixa de Experiências** |
| Grupo ativo | Selecionado ou formado no login |
| Caixinha ativa | Caixinha selecionada (no modo Experiências) |
| Tipo de caixinha | Tipo da caixinha ativa (orienta sugestões e tema) |

| Modo | Quem entra | Operações de domínio |
|------|------------|----------------------|
| **Experiências** | Um participante | Registrar, editar, excluir experiências; escolher grupo e caixinha |
| **Caixa de Experiências** | Vários participantes juntos | Formar grupo, criar caixinhas, navegar, sortear, revelar |

### Resultado de sorteio (transitório)

Um **sorteio** seleciona aleatoriamente uma experiência de uma caixinha. **Não é persistido** — cada ativação gera uma nova seleção.

| Elemento | Significado |
|----------|-------------|
| Experiência selecionada | Uma experiência da caixinha, filtrada se solicitado |
| Filtro aplicado | Qualquer, intensidade fixa ou intensidade máxima |
| Estado de revelação | Se a descrição completa já foi exibida |

**Não modelado:** histórico de sorteios, eventos de revelação, status de conclusão ou práticas sociais (consequências, trocas).

---

### Visão geral dos relacionamentos

```
                    ┌─────────────────────┐
                    │ Entrada na lista    │
                    │   de permissão      │
                    └──────────┬──────────┘
                               │ autoriza
                               ▼
┌──────────────┐      ┌────────────────┐      ┌──────────────┐
│ Participante │◄────►│     Grupo      │─────►│   Caixinha   │
└──────┬───────┘      └────────────────┘      └──────┬───────┘
       │                                              │
       │ autoria                                      │ contém
       ▼                                              ▼
              ┌──────────────────────────────────────────┐
              │              Experiência                   │
              └──────────────────────────────────────────┘
```

---

### Taxonomias

#### Modos de acesso

| Rótulo ao usuário | Escopo funcional |
|-------------------|------------------|
| **Experiências** | Contribuição individual: registrar e gerenciar experiências |
| **Caixa de Experiências** | Ritual em grupo: criar caixinhas, sortear, revelar |

#### Tipos de caixinha (11 categorias)

Padrão: **Saídas com amigos**

| Tipo | Dica de subtítulo (PT) |
|------|------------------------|
| Saídas com amigos | Rolês leves a intensos em grupo |
| Saídas em casal | Cafés, passeios e rolês a dois |
| Viagens em casal | Escapadas e destinos a dois |
| Íntimo em casal | Conexão e conversas mais profundas |
| Viagens com amigos | Bate-volta, fim de semana ou viagem planejada |
| Experiências com amigos | Cursos, tours e experiências em grupo |
| Sair da rotina | Pequenas quebras de hábito no dia a dia |
| Primeiras vezes | Experimentar coisas novas com calma |
| Desconforto leve | Sair um pouco da zona de conforto, com cuidado |
| Momentos de conexão | Presença, escuta e vínculo em grupo |
| Experiências diferentes | Coisas fora do comum para o grupo |

O catálogo agrupa tipos em seções de apresentação (amigos, casal, pessoal, social), mas a interface de criação mostra lista plana sem rótulos de seção.

#### Níveis de intensidade (1–5)

| Nível | Rótulo |
|-------|--------|
| 1 | Leve |
| 2 | Desconfortável |
| 3 | Coragem |
| 4 | Ousado |
| 5 | Adrenalina |

#### Parâmetros da experiência

Cada dimensão é avaliada de 1–5 com níveis verbais definidos:

| Dimensão | Pergunta ao proponente |
|----------|------------------------|
| **Esforço** | Quão exigente é fazer isso? |
| **Abertura** | Quanta exposição gentil ou sinceridade pede? |
| **Novidade** | Quão diferente do que vocês costumam fazer juntos? |

**Intensidade sugerida:** o sistema propõe um nível com base na média arredondada das três avaliações; o proponente pode alterar na etapa de Classificação.

#### Filtros de sorteio

| Filtro | Comportamento |
|--------|---------------|
| **Qualquer** | Sorteia entre todas as experiências da caixinha |
| **Intensidade fixa** | Apenas experiências exatamente no nível N |
| **Intensidade máxima** | Experiências no nível N ou abaixo |

Nível padrão do filtro na interface: **3**.

#### Etapas do assistente de criação

| Etapa | Propósito |
|-------|-----------|
| 1 – Sugestão | Descrever uma ideia ou escolher um exemplo |
| 2 – Reflexão | Justificar se todos aceitariam |
| 3 – Parâmetros | Avaliar esforço, abertura, novidade |
| 4 – Classificação | Definir intensidade final (com sugestão) |
| 5 – Bifurcação | Revisar, salvar, opcionalmente criar outra |

#### Idioma da interface (preferência do cliente)

| Código | Idioma |
|--------|--------|
| `pt` | Português (padrão) |
| `en` | Inglês |
| `it` | Italiano |

Armazenado no cliente; não faz parte do modelo de domínio persistido.

---

### Parâmetros, restrições e configurações

| Parâmetro | Valor / regra |
|-----------|---------------|
| Tamanho máx. da descrição | 1.000 caracteres |
| Tamanho máx. da reflexão | 2.000 caracteres por campo |
| Faixa de intensidade | 1–5 (obrigatório) |
| Avaliações de parâmetros | 1–5 cada, os três obrigatórios |
| Tipo de caixinha padrão | Saídas com amigos |
| Intensidade padrão (assistente e filtro) | 3 |
| Edição/exclusão só pelo autor | Apenas o autor da experiência pode alterar ou remover |
| Cadastro com lista de permissão | E-mail deve estar na lista antes do cadastro |

---

### Conteúdo padrão e embutido

#### Lista de permissão de cadastro (exemplos de seed)

- `proponente@intensity.app`
- `membro1@intensity.app`
- `membro2@intensity.app`

#### Pacotes de sugestão

O cliente embute **165 experiências de exemplo**: 11 tipos de caixinha × 5 níveis de intensidade × 3 sugestões cada. Ao tocar em um exemplo, o campo de descrição é preenchido (editável em qualquer etapa).

**Lacuna de localização:** o texto das sugestões está atualmente **apenas em português**, independentemente do idioma da interface.

#### Onboarding e guia rápido (apenas no cliente)

- Narrativa de onboarding em quatro passos (problema → momentos incomuns → agir → coletar/sortear/viver)
- Seções do guia rápido: regra central, fluxo recomendado, dicas de intensidade, práticas sociais

São conteúdo de apresentação, não entidades de domínio.

#### Práticas sociais (apenas orientação)

O guia rápido e o documento de princípios recomendam práticas que **não têm entidades correspondentes**:

- Definir uma **consequência** antes de revelar
- **Trocar** experiências de intensidades diferentes
- **Progressão gradual** de intensidade ao longo do tempo

---

### Terminologia canônica

| Usar na Camada 2 | Evitar |
|------------------|--------|
| Participante | Tabela de usuários, nomes de classes |
| Entrada na lista de permissão | Tabela de e-mails permitidos |
| Grupo | Fingerprint, ID de grupo |
| Caixinha | Tabela de caixinhas de experiência |
| Experiência | Cifra de descrição, linha |
| Parâmetros (esforço / abertura / novidade) | Nomes de colunas de estrelas |
| Reflexão | Cifra de informações adicionais |
| Selo de integridade | Campo de hash da descrição |
| Modo Experiências / Caixa de Experiências | Códigos internos de modo |
| Proponente | Rótulos internos de papel |

---

## Lacunas e limitações

| Tópico | Status |
|--------|--------|
| Modelo de reflexão vs interface | Três campos suportados; apenas um coletado hoje |
| Ciclo de vida da caixinha | Renomear, editar ou excluir não observados |
| Nomeação de grupo | Sem nome visível — apenas lista de participantes |
| Persistência de sorteio | Sorteios e eventos de revelação não são armazenados |
| Práticas sociais | Consequências, trocas, progressão — apenas orientação |
| Localização das sugestões | Texto em português servido para todos os idiomas |
| Perfil do participante | Sem avatar, preferências ou notificações além de nome/e-mail |
| Seções de tipo de caixinha | Seções existem no catálogo; interface mostra lista plana |
