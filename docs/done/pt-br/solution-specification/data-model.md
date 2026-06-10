# Modelo de Dados

Este documento descreve o modelo de dados funcional do Intensity — as entidades de domínio, seus relacionamentos, taxonomias, parâmetros e conteúdo padrão que estruturam o funcionamento do produto. Especifica *o que existe* e *como os conceitos se relacionam*, sem detalhe de implementação.

**Público:** analistas, product owners, designers e QA funcional — pessoas que precisam entender o domínio da solução sem saber como ela foi construída.

---

## Curta

O domínio do Intensity gira em torno de **participantes** que formam **grupos**, coletam **experiências** em **caixinhas** temáticas e vivem juntos o momento de **sorteio e revelação**. **Sugestões pré-definidas por tipo de caixinha** orientam a criação de experiências — funcionam como tutorial implícito e mudam conforme o tipo da caixinha ativa. Cada experiência carrega uma descrição, um nível de **intensidade** geral (1–5), três **parâmetros** (esforço, abertura, novidade) e a **reflexão** do proponente. Onze **tipos de caixinha** organizam ideias por contexto; dois **modos de acesso** (Experiências e Caixa de Experiências) definem quem pode fazer o quê. Resultados de sorteio são transitórios e não são armazenados.

---

## Média

### Entidades centrais

| Entidade | O que representa |
|----------|------------------|
| **Participante** | Pessoa cadastrada (nome de exibição, e-mail, credenciais) que pode contribuir e entrar em grupos |
| **Grupo** | Conjunto de participantes que entraram juntos no modo Caixa de Experiências — identificado por essa combinação exata |
| **Caixinha** | Recipiente nomeado e temático onde as experiências de um grupo são coletadas |
| **Experiência** | Ideia concreta para fazer juntos, autoria de um participante, pertencente a uma caixinha |
| **Contexto de sessão** | Escopo operacional do uso atual: modo de acesso, grupo ativo, caixinha ativa |

### Como se conectam

```
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
- **Sugestões pré-definidas:** pacotes embutidos por tipo de caixinha, exibidos na etapa Sugestão do assistente; funcionam como tutorial implícito de criação

### Parâmetros e padrões

- Tipo de caixinha padrão quando não especificado: **Saídas com amigos**
- Intensidade padrão no assistente e no filtro de sorteio: **3**
- Intensidade sugerida: média arredondada das três avaliações de parâmetros (proponente pode alterar)
- **165 sugestões pré-definidas por tipo de caixinha** (11 tipos × 5 níveis × 3 cada), com textos localizados em português, inglês e italiano
- **Consequências, trocas e progressão gradual** existem apenas como orientação social — não como dados rastreados

---

## Detalhada

### Participante e cadastro

Um **participante** é quem concluiu o cadastro. Possui **nome de exibição** (mostrado nas listas de grupo), **e-mail** (identidade de login) e **credenciais** (e-mail + senha).

**Login na Caixa de Experiências:** ao entrar nesse modo, a interface exibe um ou mais cards de credenciais — cada um com campos de e-mail e senha. Há sempre pelo menos um card; um controle **+** adiciona outro quando mais pessoas vão jogar juntas. Cada card deve ser preenchido com as credenciais de um participante cadastrado para formar o grupo. Usuários cadastrados não aparecem em lista para seleção; as credenciais são digitadas manualmente em cada card.

**Celular compartilhado para jogar:** cadastrar e registrar experiências acontece individualmente, cada um no seu dispositivo. Jogar juntos — navegar caixinhas, sortear, revelar — acontece em **um único celular compartilhado**, em geral quem empresta o aparelho ao grupo. Não existe papel explícito de "host"; a expectativa é simplesmente que o grupo não jogue cada um no seu celular durante o ritual.

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
| **Caixa de Experiências** | Vários participantes juntos em um único dispositivo compartilhado | Formar grupo, criar caixinhas, navegar, sortear, revelar |

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

---

### Conteúdo padrão e embutido

#### Sugestões pré-definidas por tipo de caixinha

As sugestões pré-definidas são **conteúdo embutido essencial** do Intensity. Não são decoração opcional: funcionam como **tutorial implícito** de como criar boas experiências — tom, granularidade, ousadia em cada nível de intensidade e adequação ao tema da caixinha. Cada sugestão precisa ser bem pensada; exemplos fracos ensinariam padrões errados aos proponentes.

**Como funcionam no produto:**

- Na etapa **Sugestão** do assistente de criação, o proponente vê exemplos agrupados por **nível de intensidade** (1–5).
- O conjunto de exemplos vem do **tipo da caixinha ativa** — o tipo da caixinha onde a experiência será salva (contexto de sessão no modo Experiências).
- Criar uma caixinha com determinado tipo não copia sugestões para dentro dela; em vez disso, esse tipo **seleciona qual pacote de sugestões** é oferecido no momento de criar experiências.
- Ao tocar em uma sugestão, o campo de descrição é preenchido (editável em qualquer etapa do assistente).
- Estrutura: **11 tipos de caixinha × 5 níveis de intensidade × 3 sugestões = 165** exemplos embutidos.

**Fonte no sistema:**

- Estrutura dos pacotes e chaves de i18n: `client/mobile-app/src/commonMain/kotlin/com/intensity/mobile/app/ui/experience/ExperienceBoxSuggestionPacks.kt`
- Textos canônicos (português): `client/mobile-app/src/commonMain/kotlin/com/intensity/mobile/app/platform/i18n/dictionaries/SuggestionPacksPt.kt`

**Localização:** os textos abaixo são os valores canônicos embutidos hoje — em português para todos os idiomas da interface.

##### Saídas com amigos (`outings_friends`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Pedir comida juntos e comer assistindo algo na casa de alguém |
| 1 – Leve | 2 | Vivenciar uma dinâmica simples juntos em uma noite tranquila |
| 1 – Leve | 3 | Se reunir só para conversar sem pressa em casa |
| 2 – Desconfortável | 1 | Ir a um bar ou café diferente durante a noite |
| 2 – Desconfortável | 2 | Fazer um churrasco na casa de alguém em um sábado à tarde |
| 2 – Desconfortável | 3 | Sair para jantar em um lugar que ninguém do grupo foi ainda |
| 3 – Coragem | 1 | Ir a um karaokê ou boliche em grupo |
| 3 – Coragem | 2 | Passar um dia inteiro juntos em algum lugar da cidade |
| 3 – Coragem | 3 | Fazer um rolê que normalmente não é padrão do grupo |
| 4 – Ousado | 1 | Organizar uma noite temática (ex: comida mexicana, italiana, etc.) |
| 4 – Ousado | 2 | Planejar um dia inteiro com várias atividades diferentes |
| 4 – Ousado | 3 | Fazer um encontro com mais pessoas além do grupo habitual |
| 5 – Adrenalina | 1 | Fazer uma viagem em grupo para uma cidade próxima no final de semana |
| 5 – Adrenalina | 2 | Alugar um lugar para passar um final de semana juntos |
| 5 – Adrenalina | 3 | Fazer um rolê totalmente fora do comum para o grupo |

##### Saídas em casal (`outings_couple`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Tomar um café juntos em um lugar novo |
| 1 – Leve | 2 | Fazer uma caminhada conversando sem usar o celular |
| 1 – Leve | 3 | Assistir um filme juntos escolhido na hora |
| 2 – Desconfortável | 1 | Jantar em um restaurante que vocês nunca foram |
| 2 – Desconfortável | 2 | Fazer um piquenique em um parque da cidade |
| 2 – Desconfortável | 3 | Sair à noite sem planejar o destino |
| 3 – Coragem | 1 | Passar um dia inteiro juntos fora de casa |
| 3 – Coragem | 2 | Fazer uma atividade que um gosta e o outro normalmente não faria |
| 3 – Coragem | 3 | Ir juntos a um evento (show, feira, etc.) |
| 4 – Ousado | 1 | Passar um final de semana fora da cidade |
| 4 – Ousado | 2 | Planejar um dia surpresa para o outro |
| 4 – Ousado | 3 | Fazer uma noite temática em casa |
| 5 – Adrenalina | 1 | Viajar juntos para um destino que vocês já comentaram |
| 5 – Adrenalina | 2 | Fazer uma experiência marcante (trilha, passeio diferente) |
| 5 – Adrenalina | 3 | Passar um dia inteiro desconectados (sem celular) |

##### Viagens em casal (`trips_couple`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Listar três destinos que os dois topariam conhecer |
| 1 – Leve | 2 | Ver juntos fotos de uma viagem antiga e relembrar |
| 1 – Leve | 3 | Pesquisar passagens só por curiosidade, sem compromisso |
| 2 – Desconfortável | 1 | Reservar um fim de semana em uma pousada perto |
| 2 – Desconfortável | 2 | Fazer um roteiro de um dia com paradas que os dois escolhem |
| 2 – Desconfortável | 3 | Ir a um museu ou cidade vizinha pela primeira vez juntos |
| 3 – Coragem | 1 | Planejar uma viagem de 3 a 5 dias com orçamento combinado |
| 3 – Coragem | 2 | Experimentar um tipo de hospedagem diferente do habitual |
| 3 – Coragem | 3 | Viajar sem itinerário fechado, só com destino definido |
| 4 – Ousado | 1 | Fazer uma viagem com alguma experiência leve (trilha, estrada, etc.) |
| 4 – Ousado | 2 | Combinar uma viagem surpresa parcial (só uma parte é segredo) |
| 4 – Ousado | 3 | Repetir um destino favorito mas com roteiro novo |
| 5 – Adrenalina | 1 | Planejar uma viagem internacional juntos |
| 5 – Adrenalina | 2 | Fazer uma viagem longa com desconexão quase total |
| 5 – Adrenalina | 3 | Marcar uma experiência fora da zona de conforto, combinada aos dois |

##### Íntimo em casal (`intimate_couple`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Ter uma conversa mais profunda sobre algo leve |
| 1 – Leve | 2 | Relembrar momentos importantes do relacionamento |
| 1 – Leve | 3 | Compartilhar algo que nunca foi dito antes (leve) |
| 2 – Desconfortável | 1 | Fazer um momento romântico planejado em casa |
| 2 – Desconfortável | 2 | Escrever algo significativo um para o outro |
| 2 – Desconfortável | 3 | Criar um momento especial fora da rotina |
| 3 – Coragem | 1 | Ter uma conversa que vem sendo evitada |
| 3 – Coragem | 2 | Fazer algo novo juntos na relação |
| 3 – Coragem | 3 | Sair da rotina emocional do casal |
| 4 – Ousado | 1 | Se abrir emocionalmente sobre algo importante |
| 4 – Ousado | 2 | Explorar algo novo na relação com mais profundidade |
| 4 – Ousado | 3 | Criar um momento totalmente fora do padrão |
| 5 – Adrenalina | 1 | Ter uma conversa transformadora sobre o relacionamento |
| 5 – Adrenalina | 2 | Fazer algo que exige alta vulnerabilidade |
| 5 – Adrenalina | 3 | Criar uma experiência marcante para o casal |

##### Viagens com amigos (`trips_friends`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Conversar sobre uma viagem que vocês sempre comentam |
| 1 – Leve | 2 | Escolher um destino que todos acham interessante |
| 1 – Leve | 3 | Montar um roteiro simples juntos |
| 2 – Desconfortável | 1 | Fazer um bate-volta para uma cidade próxima |
| 2 – Desconfortável | 2 | Passar um dia turístico em outra cidade |
| 2 – Desconfortável | 3 | Conhecer um lugar novo na região |
| 3 – Coragem | 1 | Planejar uma viagem real em grupo |
| 3 – Coragem | 2 | Passar um final de semana fora juntos |
| 3 – Coragem | 3 | Dividir tarefas para organizar a viagem |
| 4 – Ousado | 1 | Viajar para um destino que ninguém do grupo conhece |
| 4 – Ousado | 2 | Planejar uma viagem com atividades diferentes (natureza, cultura, etc.) |
| 4 – Ousado | 3 | Fazer uma viagem mais estruturada em grupo |
| 5 – Adrenalina | 1 | Fazer uma viagem longa juntos |
| 5 – Adrenalina | 2 | Viajar para outro país em grupo |
| 5 – Adrenalina | 3 | Fazer uma experiência marcante durante a viagem |

##### Experiências com amigos (`experiences_friends`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Experimentar juntos um lanche de um lugar novo |
| 1 – Leve | 2 | Fazer um passeio curto que ninguém fez antes |
| 1 – Leve | 3 | Trocar ideias de experiências fora da rotina |
| 2 – Desconfortável | 1 | Ir a um workshop ou aula rápida em grupo (culinária, dança, etc.) |
| 2 – Desconfortável | 2 | Fazer um tour guiado ou visita cultural diferente |
| 2 – Desconfortável | 3 | Experimentar uma atividade ao ar livre leve |
| 3 – Coragem | 1 | Marcar uma experiência que exige um pouco mais de coragem em grupo |
| 3 – Coragem | 2 | Planejar um dia com duas experiências novas seguidas |
| 3 – Coragem | 3 | Convidar alguém de fora para uma experiência com o grupo |
| 4 – Ousado | 1 | Fazer uma atividade intensa em grupo (rapel, rafting, etc.) se todos toparem |
| 4 – Ousado | 2 | Organizar um evento temático com experiências inéditas |
| 4 – Ousado | 3 | Passar um dia inteiro experimentando coisas novas na cidade |
| 5 – Adrenalina | 1 | Planejar uma experiência marcante fora da cidade em grupo |
| 5 – Adrenalina | 2 | Combinar algo que o grupo nunca faria sozinho |
| 5 – Adrenalina | 3 | Criar um ritual de grupo para repetir depois de uma experiência forte |

##### Sair da rotina (`break_routine`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Trocar o lugar do café da manhã ou almoço na semana |
| 1 – Leve | 2 | Caminhar por um bairro que você quase não visita |
| 1 – Leve | 3 | Fazer uma tarefa rotineira em outro horário ou com música diferente |
| 2 – Desconfortável | 1 | Ir a um cinema ou teatro sozinho ou com alguém em dia incomum |
| 2 – Desconfortável | 2 | Experimentar um hobby barato por uma tarde |
| 2 – Desconfortável | 3 | Trocar o trajeto casa-trabalho por um dia |
| 3 – Coragem | 1 | Marcar um encontro com você mesmo: museu, parque, livraria |
| 3 – Coragem | 2 | Fazer algo que você adia há meses por preguiça |
| 3 – Coragem | 3 | Convidar alguém para quebrar a rotina juntos |
| 4 – Ousado | 1 | Planejar um dia off-grid ou quase sem telas |
| 4 – Ousado | 2 | Fazer uma mini viagem solo de ida e volta no mesmo dia |
| 4 – Ousado | 3 | Assumir uma experiência pessoal em público (corrida, curso, etc.) |
| 5 – Adrenalina | 1 | Mudar algo estrutural da rotina por uma semana (sono, trabalho, etc.) |
| 5 – Adrenalina | 2 | Fazer uma experiência que assusta um pouco mas te atrai |
| 5 – Adrenalina | 3 | Compartilhar com o grupo um plano de saída de zona de conforto |

##### Primeiras vezes (`first_times`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Anotar três primeiras vezes pequenas para a semana |
| 1 – Leve | 2 | Experimentar um ingrediente ou receita nunca feita |
| 1 – Leve | 3 | Ouvir um gênero musical que você evita |
| 2 – Desconfortável | 1 | Ir sozinho a um evento onde você não conhece ninguém |
| 2 – Desconfortável | 2 | Experimentar uma modalidade de exercício nova |
| 2 – Desconfortável | 3 | Tentar um esporte ou dinâmica que o grupo nunca fez |
| 3 – Coragem | 1 | Fazer algo artístico pela primeira vez (aula, open mic, etc.) |
| 3 – Coragem | 2 | Dirigir ou ir de transporte público a um lugar inédito |
| 3 – Coragem | 3 | Pedir ajuda em algo que você sempre faz sozinho |
| 4 – Ousado | 1 | Marcar uma primeira vez que envolve vulnerabilidade leve |
| 4 – Ousado | 2 | Fazer um pernoite ou viagem curta inédita |
| 4 – Ousado | 3 | Compartilhar com o grupo uma primeira vez que deu medo |
| 5 – Adrenalina | 1 | Planejar uma primeira vez que mexe com identidade ou medo real |
| 5 – Adrenalina | 2 | Combinar com o grupo uma experiência inédita para todos |
| 5 – Adrenalina | 3 | Registrar e celebrar uma primeira vez marcante |

##### Desconforto leve (`light_discomfort`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Dizer não a algo pequeno que você sempre aceita por educação |
| 1 – Leve | 2 | Fazer uma caminhada um pouco mais longa que o habitual |
| 1 – Leve | 3 | Experimentar uma roupa ou estilo fora do padrão |
| 2 – Desconfortável | 1 | Participar de uma roda de conversa honesta sobre um tema meio incômodo |
| 2 – Desconfortável | 2 | Fazer uma atividade em grupo onde você não é o melhor |
| 2 – Desconfortável | 3 | Pedir feedback sincero a alguém próximo |
| 3 – Coragem | 1 | Propor ao grupo algo que gera um pouco de constrangimento saudável |
| 3 – Coragem | 2 | Ficar em silêncio ou meditação guiada por mais tempo que o confortável |
| 3 – Coragem | 3 | Assumir um papel diferente numa dinâmica de grupo |
| 4 – Ousado | 1 | Combinar uma experiência físico ou social leve fora da zona de conforto |
| 4 – Ousado | 2 | Falar sobre um limite pessoal com calma em grupo |
| 4 – Ousado | 3 | Fazer uma dinâmica de improviso ou teatro com o grupo |
| 5 – Adrenalina | 1 | Planejar uma experiência que mistura diversão e desconforto leve combinado |
| 5 – Adrenalina | 2 | Revisitar um tema evitado com apoio do grupo |
| 5 – Adrenalina | 3 | Celebrar coragem depois de um momento desconfortável bem vivido |

##### Momentos de conexão (`connection_moments`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Fazer rodada rápida: um elogio sincero para cada pessoa |
| 1 – Leve | 2 | Contar uma memória boa que envolve o grupo |
| 1 – Leve | 3 | Perguntar como você está e ouvir de verdade |
| 2 – Desconfortável | 1 | Preparar um café ou lanche coletivo sem pressa |
| 2 – Desconfortável | 2 | Fazer uma dinâmica de gratidão em grupo |
| 2 – Desconfortável | 3 | Compartilhar um objetivo pessoal pequeno para os próximos meses |
| 3 – Coragem | 1 | Criar um ritual simples de conexão (check-in semanal, etc.) |
| 3 – Coragem | 2 | Fazer uma caminhada em silêncio seguida de conversa aberta |
| 3 – Coragem | 3 | Escrever cartões anônimos de apoio dentro do grupo |
| 4 – Ousado | 1 | Mediar um papo mais profundo com regras de respeito combinadas |
| 4 – Ousado | 2 | Planejar um encontro só para escutar uns aos outros |
| 4 – Ousado | 3 | Fazer uma atividade que exige cooperação real (cozinhar junto, etc.) |
| 5 – Adrenalina | 1 | Marcar um retiro curto ou encontro longo focado em conexão |
| 5 – Adrenalina | 2 | Criar um compromisso de grupo para cuidar uns dos outros |
| 5 – Adrenalina | 3 | Viver um momento de vulnerabilidade compartilhada com segurança |

##### Experiências diferentes (`different_experiences`)

| Intensidade | # | Sugestão |
|-------------|---|----------|
| 1 – Leve | 1 | Trocar papéis numa dinâmica simples (quem organiza, quem cozinha, etc.) |
| 1 – Leve | 2 | Experimentar comida de um país que ninguém conhece direito |
| 1 – Leve | 3 | Assistir a um tipo de show que o grupo nunca foi |
| 2 – Desconfortável | 1 | Fazer um tour alternativo na cidade (becos, feiras, arte de rua) |
| 2 – Desconfortável | 2 | Montar uma playlist coletiva e comentar cada faixa |
| 2 – Desconfortável | 3 | Ir a um espaço cultural fora do radar do grupo |
| 3 – Coragem | 1 | Combinar uma experiência sensorial (jantar às cegas, etc.) |
| 3 – Coragem | 2 | Fazer uma dinâmica de improviso com regras novas |
| 3 – Coragem | 3 | Planejar um encontro com tema inusitado |
| 4 – Ousado | 1 | Participar de uma experiência guiada por forasteiro ou especialista |
| 4 – Ousado | 2 | Fazer um percurso noturno ou em local incomum |
| 4 – Ousado | 3 | Criar uma experiência criativa em grupo com apresentação final |
| 5 – Adrenalina | 1 | Marcar uma experiência fora do comum que exige planejamento conjunto |
| 5 – Adrenalina | 2 | Viajar ou acampar com roteiro experimental |
| 5 – Adrenalina | 3 | Documentar e compartilhar aprendizados depois da experiência |

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
| Grupo | Fingerprint, ID de grupo |
| Caixinha | Tabela de caixinhas de experiência |
| Experiência | Cifra de descrição, linha |
| Parâmetros (esforço / abertura / novidade) | Nomes de colunas de estrelas |
| Reflexão | Cifra de informações adicionais |
| Selo de integridade | Campo de hash da descrição |
| Modo Experiências / Caixa de Experiências | Códigos internos de modo |
| Proponente | Rótulos internos de papel |
