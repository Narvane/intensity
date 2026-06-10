# Componentes Funcionais

Este documento cataloga os módulos funcionais, telas, fluxos de usuário e comportamentos de interface do Intensity — o que o usuário pode fazer, onde e sob quais condições. Especifica *o que existe funcionalmente* na interface, sem detalhe de implementação.

**Público:** analistas, product owners, designers e QA funcional — pessoas que precisam mapear funcionalidades, jornadas e comportamentos de tela sem saber como o app foi construído.

---

## Curta

O Intensity é um **aplicativo mobile** organizado em torno de **onze visões principais** mais overlays. Após bootstrap e onboarding opcional, o usuário autentica-se em um de três modos (**Experiências**, **Caixa de Experiências** ou **Cadastro**). O caminho **Experiências** passa por seleção de grupo → seleção de caixinha → lista de experiências → assistente de criação. O caminho **Caixa de Experiências** passa por lista de caixinhas → criação opcional de caixinha → momento compartilhado (sorteio e revelação). Cada tela trata explicitamente estados de **carregamento**, **vazio** e **erro**. Um **assistente de criação** em cinco etapas orienta o registro de experiências. O **momento compartilhado** suporta filtros de intensidade e ritual de revelação com flip de card.

---

## Média

### Módulos funcionais

| Módulo | Finalidade |
|--------|------------|
| **Bootstrap** | Carregar preferência de idioma e estado de primeira execução antes de exibir conteúdo |
| **Onboarding** | Introdução ilustrada em quatro passos à história do produto |
| **Guia rápido** | Manual reutilizável com regras centrais, fluxo e dicas |
| **Autenticação** | Login (Experiências ou Caixa de Experiências), cadastro, acesso à ajuda |
| **Seleção de grupo** | Escolher em qual grupo de participantes contribuir (modo Experiências) |
| **Seleção de caixinha** | Escolher qual caixinha dentro do grupo (modo Experiências) |
| **Lista de experiências** | Ver, revelar e excluir experiências próprias na caixinha ativa |
| **Assistente de criação** | Fluxo guiado em cinco etapas para registrar nova experiência |
| **Início da caixinha** | Listar e criar caixinhas (modo Caixa de Experiências) |
| **Momento compartilhado** | Sorteio aleatório com filtros, dica de alinhamento e revelação de card |
| **Recuperação de erro** | Tela para estado de sessão não reconhecido com opções de saída |

### Catálogo de telas

| # | Tela | Quando exibida |
|---|------|----------------|
| 1 | **Carregamento bootstrap** | Preferências de idioma/onboarding ainda não prontas |
| 2 | **Onboarding** (4 passos) | Primeira execução |
| 3 | **Guia rápido** | Do onboarding ou ajuda na autenticação; overlay |
| 4 | **Autenticação** | Sem sessão ativa; onboarding concluído |
| 5 | **Sessão desconhecida** | Modo de acesso da sessão não reconhecido |
| 6 | **Seleção de grupo** | Modo Experiências; nenhum grupo escolhido |
| 7 | **Seleção de caixinha** | Modo Experiências; grupo definido, caixinha não escolhida |
| 8 | **Lista de experiências** | Modo Experiências; grupo e caixinha definidos |
| 9 | **Assistente de criação** | Overlay a partir da lista de experiências |
| 10 | **Início da caixinha** | Modo Caixa de Experiências |
| 11 | **Criar caixinha** | Sub-visão a partir do início da caixinha |
| 12 | **Momento compartilhado** | Modo Caixa de Experiências; caixinha aberta |

A autenticação também contém três **sub-painéis** (não rotas separadas): login Experiências, login multiusuário Caixa de Experiências e Cadastro.

### Fluxos principais de usuário

```
Fluxo A — Primeira execução
  Carregamento → Onboarding (4 passos) → [Guia rápido opcional] → Autenticação

Fluxo B — Experiências (contribuição individual)
  Autenticação → Seleção de grupo → Seleção de caixinha → Lista de experiências
    → [+ Criar experiência] → Overlay do assistente → volta à lista
  Voltar: lista → seleção de caixinha → seleção de grupo
  Sair: logout de qualquer tela autenticada

Fluxo C — Caixa de Experiências (ritual em grupo)
  Autenticação (multiusuário) → Início da caixinha → [Criar caixinha] → Início da caixinha
    → Abrir caixinha → Momento compartilhado → Sorteio → Alinhar → Revelar → Voltar ao sorteio
  Voltar: momento compartilhado → início da caixinha
  Sair: logout

Fluxo D — Recuperação de erro
  Sessão desconhecida → Sair (logout) ou Entrar na Caixa de Experiências (limpa sessão)
```

### Etapas do assistente de criação

| Etapa | Rótulo | Ação do usuário |
|-------|--------|-----------------|
| 1 — Sugestão | Escrever descrição ou tocar em sugestão do tipo de caixinha como inspiração |
| 2 — Reflexão | Justificar por que o grupo aceitaria a ideia |
| 3 — Parametrização | Avaliar esforço, abertura, novidade (1–5 estrelas cada) |
| 4 — Classificação | Confirmar ou ajustar intensidade geral (sugerida automaticamente pelos parâmetros) |
| 5 — Bifurcação | Revisar resumo; salvar e criar outra, ou finalizar |

O assistente exibe um card de descrição persistente ao longo das etapas e indicador de progresso de cinco segmentos.

### Funcionalidades do momento compartilhado

- **Modos de filtro:** Qualquer (sem filtro de intensidade), Exata (nível fixo 1–5), Até (nível máximo inclusivo)
- **Ação de sorteio:** seleção aleatória entre experiências elegíveis na caixinha
- **Card de resultado:** mostra capa de intensidade (nível, parâmetros, selo) antes da revelação
- **Dica de alinhamento:** pede acordo em grupo antes de virar o card
- **Revelar:** flip do card para ler descrição completa
- **Retorno:** voltar ao sorteio para nova seleção

### Tipos de caixinha (11)

Cada tipo tem título, dica de subtítulo, destaque visual distinto e pacote de sugestões associado:

| Tipo | Dica de subtítulo |
|------|-------------------|
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

Tipo de caixinha padrão quando não especificado: **Saídas com amigos**.

---

## Detalhada

### Carregamento bootstrap

**Finalidade:** Preparar idioma e estado de onboarding antes de renderizar o fluxo principal.

**Comportamento:** Spinner centralizado em tela cheia. Sem ação do usuário. Transita automaticamente para onboarding (primeira execução) ou autenticação.

**Estados:** Apenas carregamento.

---

### Onboarding (4 passos)

**Finalidade:** Introduzir a história e proposta de valor do produto na primeira execução.

**Conteúdo por passo:**
1. Problema — experiências repetitivas, falta de proximidade
2. Insight — momentos memoráveis eram inesperados, mas adiados
3. Chamada à ação — não espere o acaso; o Intensity empurra para agir
4. Mecânica — coletar ideias incomuns, sortear uma, viver momentos memoráveis

**Ações:** Voltar, Avançar, Começar (finalizar), Abrir guia rápido. Seletor de idioma disponível.

**Estados:** Sem carregamento, vazio ou erro. Pode ser reaberto pela autenticação (modo não-primeira-execução).

---

### Guia rápido

**Finalidade:** Referência persistente para regras do produto e recomendações sociais.

**Seções:** Regra central (7 itens), fluxo recomendado (3 itens), dicas de intensidade (4 itens), dicas de consequência (4 itens), essência do Intensity (2 itens), card de dica de fechamento.

**Ações:** Começar (primeira execução — dispensa e continua), Fechar (modo reabertura).

**Estados:** Sem carregamento, vazio ou erro.

---

### Autenticação

**Finalidade:** Ponto de entrada para todas as sessões. Três modos selecionáveis via cards.

**Painel login Experiências:**
- Campos de e-mail e senha
- Ação de entrar
- Ao sucesso → fluxo de seleção de grupo

**Painel login Caixa de Experiências:**
- Adicionar uma ou mais linhas de credencial (e-mail + senha cada)
- Ação de entrar — todas as credenciais devem ter sucesso; juntas definem o grupo
- Ao sucesso → início da caixinha

**Painel Cadastro:**
- Nome de exibição, e-mail, senha
- Ação de cadastrar — e-mail deve estar na lista de permissão
- Ao sucesso → retorna ao login

**Barra de ferramentas:** Ícone do guia rápido, ícone de reabrir onboarding, seletor de idioma.

**Estados:**

| Estado | Apresentação |
|--------|--------------|
| Carregamento | Texto do botão muda para "Entrando…" / "Cadastrando…" |
| Erro | Snackbar: falha de login, erro de token, erro de credencial, erro de cadastro |

---

### Sessão desconhecida

**Finalidade:** Recuperação quando o modo de acesso da sessão não é Experiências nem Caixa de Experiências.

**Conteúdo:** Aviso com valor bruto do modo de acesso exibido.

**Ações:** Sair (logout), Entrar na Caixa de Experiências (limpa sessão → retorna à autenticação).

**Estados:** Aviso persistente — não snackbar transitório.

---

### Seleção de grupo

**Finalidade:** No modo Experiências, escolher em qual grupo de participantes contribuir.

**Conteúdo:** Lista de grupos mostrando nomes dos participantes e contagem de membros.

**Ações:** Tocar grupo → Entrar (atualiza sessão, segue para seleção de caixinha). Logout na barra superior.

**Estados:**

| Estado | Apresentação |
|--------|--------------|
| Carregamento | Spinner centralizado |
| Vazio | Card: "Ainda não há grupos…" |
| Erro | Snackbar em falha de carregamento ou seleção |

---

### Seleção de caixinha (modo Experiências)

**Finalidade:** Escolher qual caixinha dentro do grupo selecionado para adicionar experiências.

**Conteúdo:** Grade de duas colunas de cards visuais de caixinha — nome, selo de tipo com ícone, cor de destaque do tipo.

**Ações:** Tocar caixinha → segue para lista de experiências. Voltar → limpa grupo (retorna à seleção de grupo). Logout.

**Estados:**

| Estado | Apresentação |
|--------|--------------|
| Carregamento | Spinner centralizado |
| Vazio | Texto simples: nenhuma caixinha neste grupo |
| Erro | Snackbar em falha de carregamento ou seleção |

---

### Lista de experiências

**Finalidade:** Ver e gerenciar as experiências do usuário atual na caixinha ativa durante esta sessão.

**Conteúdo:**
- Barra superior tematizada ao tipo de caixinha ativo
- Seção: experiências registradas (apenas contribuições próprias na sessão atual)
- Cada card de experiência: selo de intensidade, parâmetros, selo de integridade, ações revelar/excluir
- Revelar alterna visibilidade do texto completo da descrição
- Excluir remove experiência própria

**Ações:** + Criar experiência (abre overlay do assistente). Voltar → limpa caixinha (retorna à seleção de caixinha). Logout.

**Estados:**

| Estado | Apresentação |
|--------|--------------|
| Carregamento | Spinner pequeno no cabeçalho da seção |
| Vazio | Card: ainda não há experiências |
| Erro | Snackbar em falha de lista, revelação ou exclusão |

---

### Assistente de criação (overlay)

**Finalidade:** Registro guiado em cinco etapas de nova experiência.

**Elementos persistentes:** Card de descrição (mostra texto atual), barra de progresso de cinco segmentos, indicador de pílula do passo.

**Detalhes por etapa:**

**1 — Sugestão**
- Campo de descrição em texto livre
- Pacote de sugestões do tipo de caixinha exibido por nível de intensidade — exemplos tocáveis como inspiração
- Sugestões mudam conforme o tipo de caixinha ativo

**2 — Reflexão**
- Campo de texto para justificativa de aceitação pelo grupo

**3 — Parametrização**
- Três linhas de estrelas: esforço (teal), abertura (limão), novidade (rosa)
- Cada uma com texto de ajuda e descrição dinâmica por nível

**4 — Classificação**
- Intensidade sugerida automaticamente pela média dos parâmetros (usuário pode sobrescrever)
- Cinco níveis de intensidade com subtítulos e cores

**5 — Bifurcação**
- Revisão resumida de todos os dados inseridos
- Salvar e criar outra (reinicia assistente, mantém overlay aberto)
- Salvar e finalizar (fecha overlay, atualiza lista)

**Validação:** Descrição obrigatória; reflexão obrigatória; todos os parâmetros avaliados; intensidade definida. Erros via snackbar.

**Estados:**

| Estado | Apresentação |
|--------|--------------|
| Carregamento | Overlay semitransparente com spinner no envio |
| Vazio | Etapa Sugestão mostra exemplos do pacote mesmo antes de entrada do usuário |
| Erro | Snackbar: mensagens de validação, falha de envio |

---

### Início da caixinha (modo Caixa de Experiências)

**Finalidade:** Listar caixinhas do grupo e criar novas.

**Conteúdo:** Grade de duas colunas de cards visuais de caixinha. Chamada para criar caixinha.

**Ações:** Tocar caixinha → momento compartilhado. Criar caixinha → sub-visão de criação. Logout.

**Estados:**

| Estado | Apresentação |
|--------|--------------|
| Carregamento | Spinner centralizado |
| Vazio | Texto: nenhuma caixinha |
| Erro | Snackbar em falha de carregamento |

---

### Criar caixinha (sub-visão)

**Finalidade:** Registrar nova caixinha para o grupo atual.

**Conteúdo:**
- Seletor de tipo rolável — 11 opções com título, subtítulo, destaque
- Campo de nome

**Ações:** Salvar → retorna ao início da caixinha com nova caixinha listada. Voltar → retorna sem salvar.

**Validação:** Nome obrigatório (snackbar se vazio).

**Estados:**

| Estado | Apresentação |
|--------|--------------|
| Carregamento | Texto do botão "Criando…" |
| Erro | Snackbar em falha de API |

---

### Momento compartilhado

**Finalidade:** Ritual em grupo — sortear experiência aleatória, alinhar, revelar.

**Conteúdo:**
- Barra superior tematizada ao tipo de caixinha
- Chips de filtro: Qualquer / Exata / Até
- Seletor de pontos de intensidade (quando Exata ou Até selecionado)
- Botão de sorteio (rótulo adapta ao modo de filtro)
- Área de resultado: card com flip ou dicas

**Fluxo:**
1. Selecionar filtro (e nível se aplicável)
2. Tocar sorteio → seleção aleatória
3. Card mostra capa de intensidade (nível, parâmetros, selo)
4. Dica de alinhamento exibida
5. Flip → descrição completa visível
6. Voltar ao sorteio → retorna ao passo 1

**Estados:**

| Estado | Apresentação |
|--------|--------------|
| Carregamento | Texto do botão "Escolhendo…" durante sorteio |
| Vazio (pré-sorteio) | Card de dica incentivando ativação |
| Vazio (pós-sorteio) | "Nenhuma experiência disponível" quando pool vazio para o filtro |
| Erro | Snackbar com prefixo de erro |

---

### Elementos de interface reutilizáveis (catálogo funcional)

| Elemento | Papel funcional |
|----------|-----------------|
| **Cabeçalho de marca** | Ícone + nome do produto na autenticação |
| **Card de modo** | Modo de autenticação selecionável (Experiências / Caixa de Experiências / Cadastro) |
| **Seletor de idioma** | Alternar PT / EN / IT |
| **Barra superior em gradiente** | Título da tela, voltar, logout |
| **Botão primário (marrom)** | Ações padrão / Experiências |
| **Botão primário (azul)** | Ações Caixa de Experiências / grupo |
| **Container card** | Agrupar conteúdo com limite visual |
| **Linha de título de seção** | Ícone + cabeçalho em negrito |
| **Rótulo small-caps** | Identificador de seção em maiúsculas |
| **Linha de estrelas** | Entrada 1–5 com texto auxiliar |
| **Chip de filtro** | Alternar modo de filtro de sorteio |
| **Pontos de intensidade** | Selecionar nível 1–5 para filtro de sorteio |
| **Card visual de caixinha** | Exibir nome, tipo, destaque em grade |
| **Card de revelação de experiência** | Flip entre capa de intensidade e descrição |
| **Card resumo de experiência** | Info compacta com selo de intensidade e selo de integridade |
| **Linha de progresso do assistente** | Cinco segmentos + pílula do passo atual |
| **Bloco de sugestão** | Exemplo tocável na etapa 1 do assistente |
| **Snackbar** | Mensagens transitórias de erro e informação |
| **Spinner** | Indicador de carregamento (inline, centralizado ou overlay) |

---

### Regras de sessão e navegação

- **Modo Experiências** exige login individual; sessão carrega `groupId` e `boxId` conforme o usuário avança
- **Modo Caixa de Experiências** exige login multiusuário; grupo emerge da combinação de credenciais
- **Navegação voltar** limpa escopo: lista de experiências → limpa caixinha; seleção de caixinha → limpa grupo
- **Logout** de qualquer tela autenticada retorna à autenticação
- **Assistente** é overlay em tela cheia — não altera escopo da sessão
- **Onboarding/guia** são overlays no bootstrap ou autenticação — não criam sessões

---

## Lacunas e limitações

- **Edição de experiência** — nenhum fluxo de edição observado; apenas criar, alternar revelação e excluir (experiências próprias).
- **Gestão de caixinha** — nenhuma renomeação, edição ou exclusão de caixinha observada.
- **Gestão de grupo** — grupos são implícitos; sem interface de criação ou nomeação manual de grupo.
- **Captura de consequência** — guia rápido referencia consequências; nenhuma tela de entrada encontrada.
- **Notificações** — nenhum sistema de push ou notificação in-app observado.
- **Modo offline** — sem interface específica offline ou indicadores de estado em cache observados.
- **Perfil/configurações** — sem tela de perfil além da seleção de idioma; sem troca de senha ou configurações de conta.
- **Web ou desktop** — apenas interface mobile analisada; outras plataformas não validadas.
