# Backlog

Mapa de referências do repositório: @ref:refs (`docs/refs.yaml`).

Executar na ordem das seções abaixo — dentro de cada seção, na ordem listada.

---

## Refatorações

Correções estruturais em componentes compartilhados (`ScreenHeader`, `ParameterStarField`, `ExperienceSummaryMeta`) antes de evoluir jornadas e telas que os consomem.

## Corrigir layout do cabeçalho com Voltar e Sair

### Descrição

As telas autenticadas usam o componente compartilhado `ScreenHeader` com três colunas em grid (`leading` | `body` | `trailing`): botão **Voltar** à esquerda, conteúdo central (`SessionModeChrome` com ícone do modo, rótulo EXPERIENCES/EXPERIENCE BOX, subtítulo, saudação ou chips de sala, e título da tela) e botão **Sair** à direita. Quando só existe o trailing (ex.: `GroupSelectionPage`, `BoxHomePage`), o cabeçalho respira e o bloco de título ocupa a largura útil. Quando **Voltar** e **Sair** coexistem (ex.: `BoxSelectionPage`, `SharedMomentPage`, `ExperienceListPage`), o conteúdo central fica espremido entre dois `NavButton` com ícone e rótulo — o modo, subtítulo e hierarquia visual quebram ou parecem desalinhados, especialmente em viewports estreitas.

O problema é estrutural: título e chrome de sessão não devem competir horizontalmente com os botões de navegação na mesma linha. A spec de design (@ref:pt-br-design-system) pede uma ação principal óbvia por tela e hierarquia clara; o layout atual viola isso quando a pilha de navegação está completa. Já existe no backlog a tarefa "Melhorar botões de navegação secundária (Voltar, Sair e afins)", focada no visual dos `NavButton` — esta tarefa trata da **arquitetura de layout** do header para que a solução seja estável em toda a aplicação, com ou sem botão de voltar.

### Prompt IA

**Objetivo:** Redesenhar o layout do cabeçalho de tela para que botões de navegação (Voltar, Sair, Fechar, Ajuda) e conteúdo de título/modo **não compartilhem a mesma faixa horizontal de forma que quebre a hierarquia**. A implementação deve ser analisada, validada em todas as telas consumidoras e entregue como padrão único reutilizável.

**Análise obrigatória (primeira etapa):**
1. Inventariar todos os usos de `ScreenHeader` e headers ad hoc que repetem o mesmo padrão.
2. Comparar telas **com** `leading` (Voltar) vs **sem** `leading` — documentar o desvio visual atual (screenshots ou descrição).
3. Avaliar abordagens e escolher a mais sólida; opções a considerar (não limitar a uma):
   - **Duas linhas:** faixa superior só para ações (`leading` | espaço flex | `trailing`); faixa inferior em largura total para `children` (modo + título).
   - **Slots simétricos:** colunas laterais com largura fixa/reservada igual (mesmo quando vazias) para estabilizar o centro — útil se mantiver layout em uma linha em alguns contextos.
   - **Ícone-only na faixa de ações** e rótulos só em `aria-label` na toolbar, liberando largura (coordenar com tarefa de NavButton se necessário).
   - **Variantes do header:** `stacked` (telas principais) vs `compact` (overlays como `CreationAssistant`).
4. Registrar a decisão em comentário breve no componente ou em nota no PR — justificar por que a solução não quebra quando `leading` é omitido.

**Estado atual (confirmar):**
- `client/src/presentation/components/ScreenHeader.tsx` — grid `auto 1fr auto`; `children` na coluna central.
- `client/src/presentation/components/ScreenHeader.module.css` — colunas laterais `auto`; `:empty` zera min-height mas não reserva largura simétrica.
- `client/src/presentation/components/SessionModeChrome.tsx` — bloco rico (ícone, modo, subtítulo, saudação/chips, `h1`); consumido dentro de `ScreenHeader` na maioria das telas autenticadas.
- `client/src/presentation/components/NavButton.tsx` — pills com ícone + texto; largura variável por locale ("Voltar", "Sair").
- Consumidores de `ScreenHeader`: `GroupSelectionPage`, `BoxSelectionPage`, `ExperienceListPage`, `BoxHomePage`, `SharedMomentPage`, `CreateBoxPage`, `CreateBoxExperiencesPage`, `CreationAssistant` (só `trailing` Fechar).

**Direção de produto / UX:**
- Botões de chrome ficam em faixa dedicada; títulos e identificador de modo (`SessionModeChrome` ou equivalente) ocupam **100% da largura abaixo** (ou em área que não seja comprimida pelos botões).
- Com Voltar presente: título e modo **não** devem deslocar-se nem encolher de forma perceptível em relação à tela sem Voltar — a diferença deve ser só a presença do botão na faixa superior, não o layout do título.
- Sem Voltar: trailing (Sair) permanece à direita na faixa de ações; área de título alinhada à mesma margem esquerda das telas com Voltar (consistência de padding).
- Overlays (`CreationAssistant`, `QuickGuideOverlay`, `ShareInviteSheet`) devem seguir o mesmo princípio onde usam header — evitar exceções visuais sem motivo.

**Implementação sugerida:**
- Evoluir `ScreenHeader` (preferível a CSS ad hoc por página) com estrutura explícita, ex.:
  ```tsx
  <header>
    <div className={toolbar}>  {/* leading | flex spacer | trailing */} </div>
    <div className={content}>  {/* children — largura total */} </div>
  </header>
  ```
- Slots `leading` e `trailing` aceitam `undefined`/vazio sem colapsar margens do `content` de forma assimétrica.
- Remover regras de header duplicadas nos `.module.css` das páginas que conflitem com o componente central.
- `SessionModeChrome` permanece responsável pelo conteúdo de modo/título; **não** precisa saber se há Voltar — responsabilidade do `ScreenHeader`.
- Respeitar `env(safe-area-inset-*)` no topo; manter `margin-bottom` consistente com o restante do app.
- i18n: sem mudança obrigatória de copy; se adotar ícone-only na toolbar, garantir `aria-label` nos `NavButton`.

**Escopo de telas (validar visualmente após mudança):**
- Experiences: `GroupSelectionPage` (sem Voltar), `BoxSelectionPage`, `ExperienceListPage`, `CreateBoxExperiencesPage`.
- Experience Box: `BoxHomePage` (sem Voltar), `SharedMomentPage`, `CreateBoxPage`.
- Overlays: `CreationAssistant`.
- Verificar também `InvitePreviewPage`, `AuthPage`, `QuickGuideOverlay`, `ShareInviteSheet` — se não usam `ScreenHeader`, aplicar o **mesmo padrão de duas faixas** ou migrar para o componente se trivial.

**Regras arquiteturais:**
- Alterar apenas apresentação/layout; mesmas rotas, handlers de `NavButton` e props das páginas.
- Um único componente (ou família mínima: `ScreenHeader` + variante `compact`) — evitar três implementações de flexbox espalhadas.
- Tokens e espaçamento de @ref:pt-br-design-system (`--space-page`, `--touch-min`, `--radius-button`).
- Não depender de JavaScript para medir largura de botões se CSS (grid com colunas iguais, `1fr auto 1fr` na toolbar, etc.) resolver.

**Coordenação com outras tarefas:**
- Complementa "Melhorar botões de navegação secundária" e "Diferenciar sessão individual e sessão em grupo na interface" — pode ser executada antes ou depois; se o visual dos `NavButton` mudar depois, o layout do header deve continuar válido.
- Não bloquear nem implementar tarefas de conteúdo de grupo/caixinha.

**Critérios de aceitação:**
- Em `BoxSelectionPage` e `SharedMomentPage` (Voltar + Sair): `SessionModeChrome` e `h1` usam largura total; sem compressão horizontal do bloco de modo entre os botões.
- Em `GroupSelectionPage` e `BoxHomePage` (só Sair): título/modo alinhados à mesma grade visual das telas com Voltar (sem “pulo” de margem).
- Redimensionar viewport mobile estreito (~320px): sem sobreposição de texto e botões; alvos de toque ≥ 48px preservados.
- Todos os consumidores de `ScreenHeader` migrados; sem regressão de navegação (Voltar, Sair, Fechar).
- Build do client passa; revisão visual manual nas telas listadas.

**Restrições:**
- Não alterar copy, identidade de modo (cores EXPERIENCES vs EXPERIENCE BOX) nem conteúdo de `SessionModeChrome` além de ajustes de margem herdados do novo layout.
- Não redesenhar botões primários de toolbar (Criar, Convidar, Sortear).
- Não introduzir biblioteca de UI externa.

---

## Corrigir layout dos parâmetros no card de experiência

### Descrição

Na lista de experiências do modo Experiences (`ExperienceListPage`), cada card do autor exibe metadados via `ExperienceSummaryMeta`: chip de intensidade, três parâmetros (esforço, abertura, novidade) com ícone, rótulo e estrelas, e selo de integridade. A tarefa anterior "Redesenhar cards de experiência na lista do modo Experiences" já introduziu o ícone de olho e ações Editar/Excluir — porém o bloco de parâmetros permanece visualmente quebrado em larguras típicas de celular.

O layout atual usa `ParameterStarsGroup` com `layout="inline"`: cada parâmetro é um chip em flex row com `flex-wrap`, dentro de um `inlineGroup` também em flex com wrap (`flex: 1 1 7.5rem` por campo). Quando o card fica estreito — ainda mais com o botão de olho ocupando espaço à direita em `metaRow` — **Esforço** e **Abertura** aparecem lado a lado, mas as fileiras de estrelas de ambos caem na mesma linha horizontal e **colidem/sobrepõem** no centro do card. **Novidade**, por ser mais larga ou quebrar para linha seguinte, pode parecer correta enquanto os dois primeiros parâmetros ficam ilegíveis.

O problema é de composição CSS compartilhada (`ParameterStarField.module.css`), não de dados. A spec pede que intensidade, parâmetros e selo sejam legíveis de relance na listagem (@ref:pt-br-functional-components, @ref:pt-br-design-system). Esta tarefa corrige o layout para que cada parâmetro seja um bloco autocontido, sem sobreposição, em qualquer largura de card usada na lista.

### Prompt IA

**Objetivo:** Corrigir o layout quebrado dos parâmetros (estrelas sobrepostas) no card de experiência da lista do modo Experiences, com solução estável em mobile e reutilizável onde `ExperienceSummaryMeta` aparece em contexto de lista.

**Análise obrigatória (primeira etapa):**
1. Reproduzir o bug em `ExperienceListPage` com experiência que tenha os três parâmetros preenchidos — viewport ~320–390px, com e sem botão de olho visível.
2. Inspecionar cascata CSS: `ExperienceCard.metaRow` (flex, `min-width: 0`) → `ExperienceSummaryMeta.meta` → `ParameterStarsGroup` (`inlineGroup`) → `ParameterStarField` (`.inline` com `flex-wrap`).
3. Confirmar que a causa é **wrap compartilhado** entre campos irmãos (estrelas de parâmetros diferentes na mesma linha visual), não tamanho incorreto de `StarRating`.
4. Escolher abordagem que isole cada parâmetro; documentar brevemente no PR.

**Estado atual (confirmar):**
- `client/src/presentation/experiences/ExperienceCard.tsx` — `ExperienceSummaryMeta` sem `compact`; `metaRow` com olho à direita.
- `client/src/presentation/components/ExperienceSummaryMeta.tsx` — `layout="inline"` para parâmetros quando não `compact`.
- `client/src/presentation/components/ParameterStarField.tsx` — layouts `picker` | `cover` | `inline`.
- `client/src/presentation/components/ParameterStarField.module.css` — `.inlineGroup` + `.inline` (row + wrap).
- `client/src/presentation/components/StarRating.module.css` — estrelas `sm` em contexto inline/cover.

**Comportamento esperado:**
- Cada parâmetro (Esforço, Abertura, Novidade) forma um **bloco visual fechado**: ícone + rótulo + estrelas **nunca** compartilham linha de estrelas com outro parâmetro.
- Ordem de leitura clara: intensidade no topo → parâmetros → selo.
- Layout legível em card de largura total menos padding de página e espaço do botão de olho.
- Manter vocabulário visual atual: ícones coloridos (`parameterVisuals.ts`), rótulos i18n, estrelas na cor do parâmetro, fundo `--surface-sunken` nos chips se fizer sentido.
- Itens de outros participantes que usam o mesmo meta: mesmo layout corrigido (sem olho).

**Abordagens aceitáveis (escolher a mais sólida):**
- **A)** Novo layout `list` (ou `card`) em `ParameterStarField`: coluna por parâmetro (ícone + label numa linha, estrelas na linha abaixo); `inlineGroup` vira coluna ou grid de 1 coluna.
- **B)** Grid CSS no grupo: `grid-template-columns: 1fr` em viewports estreitas; opcional 2–3 colunas só acima de breakpoint se couber sem colisão.
- **C)** Uma linha horizontal **por parâmetro** com `flex-wrap: nowrap` no bloco de estrelas e `min-width` garantido — três linhas empilhadas no card.
- **D)** Passar `layout="cover"` ou variante compacta só na lista — se vertical couber sem scroll no card (avaliar altura).

Evitar: `flex-wrap` no `.inline` que permita estrelas órfãs na mesma linha que estrelas de outro `.field` irmão.

**Arquivos prováveis:**
- `client/src/presentation/components/ParameterStarField.tsx`
- `client/src/presentation/components/ParameterStarField.module.css`
- `client/src/presentation/components/ExperienceSummaryMeta.tsx` — usar novo layout na lista (prop `variant` ou trocar `inline` → `list`).
- `client/src/presentation/components/ExperienceSummaryMeta.module.css` — espaçamento vertical entre blocos.
- `client/src/presentation/experiences/ExperienceCard.module.css` — se `metaRow` precisar de ajuste (ex. empilhar olho em breakpoint extremo).
- Opcional: teste visual ou snapshot de estrutura DOM se o projeto tiver padrão.

**Regras arquiteturais:**
- Não alterar API, domínio nem valores de parâmetros.
- Não mudar capa do sorteio (`DrawResultCard`, `layout="cover"`) salvo extrair CSS compartilhado sem regressão.
- Manter `aria-label` em `ParameterStarsGroup` e descrição por parâmetro em `StarRating`.
- Seguir tokens @ref:pt-br-design-system; alvos de toque do olho e ações ≥ 48px inalterados.

**Critérios de aceitação:**
- Em mobile (~320px), card com três parâmetros: **zero sobreposição** de estrelas entre Esforço, Abertura e Novidade.
- Cada parâmetro identificável: ícone, rótulo e quantidade de estrelas correta (1–5).
- Intensidade, selo, olho, Editar e Excluir permanecem funcionais e bem posicionados.
- `DrawResultCard` / capa do sorteio sem regressão visual.
- Assistente de criação (`layout="picker"`) inalterado.
- Build do client passa; revisão manual em `ExperienceListPage` com 1+ cards.

**Restrições:**
- Escopo: correção de layout de metadados no card de lista — não redesenhar o card inteiro nem ritual de sorteio.
- Não remover estrelas nem voltar a chips numéricos antigos.
- Não alterar regras de visibilidade (`experienceVisibility`) nesta tarefa.

---

## Funcionalidades Principais

Jornadas centrais do produto após componentes compartilhados estáveis.

## Refinar seleção multi-grupo no modo Experiences

### Descrição

No modo Experiences, a jornada prevista pela spec (@ref:pt-br-functional-components, Fluxo B) é: autenticação individual → **seleção de grupo** → seleção de caixinha → lista de experiências. O modelo de dados já define que um participante pode pertencer a **vários grupos** (@ref:pt-br-data-model) e a API lista todos via `GET /v1/groups` no modo Experiences (`GroupQueryService.listForPrincipal` filtra por `participantId`). O login individual **não** fixa um único grupo na sessão — `SessionState` só carrega `groupId` após o usuário escolher um grupo na navegação.

Apesar disso, a experiência atual não comunica bem essa hierarquia. A `GroupSelectionPage` mostra cards quase idênticos (todos em `--teal`), exibindo apenas contagem de membros sem nomes; ações de **Convidar** e **Sair do grupo** aparecem já na listagem, embora convite seja conceito de grupo (a `BoxSelectionPage` já oferece convite dentro do grupo). Na autenticação, a aba **Código convite** cobre entrada em grupo por convite sem estar dentro de um grupo — comportamento desejado, mas precisa permanecer claro como caminho global, distinto do convite gerado por um membro já dentro do grupo.

Ao sair de um grupo, a spec atual diz que as experiências do autor **permanecem** nas caixinhas (@ref:pt-br-data-model). O produto passou a exigir o oposto: ao sair, **todas as experiências daquele participante nas caixinhas do grupo** devem ser removidas — mudança de regra de negócio que impacta API, copy de confirmação e testes.

Esta tarefa torna explícita a navegação multi-grupo, reorganiza convite/saída conforme o contexto, enriquece os cards de grupo com nomes dos membros e cor distinta, e alinha o comportamento de saída à nova expectativa de produto.

### Prompt IA

**Objetivo:** Evoluir a jornada Experiences para deixar claro que o usuário pode ter vários grupos, cada um com suas caixinhas; melhorar a listagem de grupos; mover convite/saída para o contexto correto; e, ao sair de um grupo, remover as experiências do participante nas caixinhas desse grupo.

**Estado atual (confirmar na implementação):**
- Rotas: `/groups` → `/groups/:groupId/boxes` → `/groups/:groupId/boxes/:boxId/experiences` — hierarquia já existe.
- `GroupSelectionPage`: lista grupos, convite e sair na listagem; cards sem nomes de membros; cor única (`--teal`).
- `BoxSelectionPage`: convite dentro do grupo (`ShareInviteSheet`); **sem** ação de sair do grupo.
- `AuthPage`: aba `invite` para entrar em grupo por código/deep link — manter como entrada global.
- `GET /v1/groups` retorna `{ id, memberCount, createdAt }` — **sem** nomes de membros.
- OpenAPI documenta `GET /v1/groups/{groupId}/members`, mas o controller atual (`GroupMemberController`) só expõe `DELETE` — implementar listagem ou enriquecer resposta de grupos.
- `GroupMembershipService.leave` remove apenas a membresia; experiências do autor **não** são excluídas hoje — diverge da nova regra.

**Comportamento esperado — navegação e convite:**
1. Após login Experiences, o usuário vê **primeiro** a lista de seus grupos (`GroupSelectionPage`) — título/copy reforçando que são *suas turmas/grupos*.
2. Ao tocar em um grupo, entra na **seleção de caixinhas** daquele grupo (`BoxSelectionPage`).
3. **Convidar** (gerar/compartilhar convite do grupo) fica **apenas dentro do grupo** — toolbar ou ação equivalente em `BoxSelectionPage` (já existe; remover da listagem de grupos em `GroupSelectionPage`).
4. **Entrar em grupo por convite** permanece **fora** do contexto de grupo: aba "Código convite" na autenticação e fluxo `/join` (`InvitePreviewPage`). Empty state da listagem de grupos pode apontar para esse caminho quando não houver grupos.
5. Voltar: caixinhas → grupos (`NavButton` back já aponta para `/groups`).

**Comportamento esperado — listagem de grupos:**
1. Cada card de grupo exibe, de forma discreta (canto ou linha secundária pequena), os **nomes de exibição** dos membros — ex.: "Ana, Bruno e +2" quando houver muitos. Usar `displayName`, nunca e-mail.
2. Manter contagem de membros se útil, mas priorizar legibilidade dos nomes para distinguir grupos sem nome formal (grupos são identificados pelo conjunto de membros — @ref:pt-br-data-model).
3. **Cor de fundo por grupo**, determinística e estável (hash de `groupId` → paleta do design system: `--coral`, `--teal`, `--purple`, `--yellow` — @ref:pt-br-design-system). Evitar N cores iguais consecutivas na lista quando possível.
4. Toque no card navega para as caixinhas; não misturar ações destrutivas no mesmo alvo de toque principal.

**Comportamento esperado — sair do grupo:**
1. Ação **Sair do grupo** disponível **dentro do grupo** (`BoxSelectionPage`), não na listagem de grupos — estilo terciário/ghost, alinhado ao backlog de padronização de headers (@ref:backlog, seção de headers).
2. Reutilizar `LeaveGroupDialog` e `LeaveGroupUseCase`.
3. **Nova regra:** ao confirmar saída, o backend remove a membresia **e exclui todas as experiências** cujo autor seja o participante que saiu (ou os participantes da sessão conjunta, no modo Experience Box) **em caixinhas daquele grupo**.
4. Atualizar copy do diálogo (`groups.leaveDialog.message` e equivalentes) — hoje diz que experiências permanecem; deve refletir remoção.
5. Após sair em Experiences: voltar à listagem de grupos; limpar `NavigationPort` se o grupo ativo era o que saiu.
6. Comportamento de **último membro** permanece: exclusão do grupo, caixinhas, experiências restantes e convites (@ref:pt-br-data-model).

**API e domínio (backend):**
- Estender `GroupMembershipService.leave` (ou serviço colaborador) para deletar experiências do(s) participante(s) que saem em caixinhas do `groupId` antes de remover membresia.
- Preferir query/repository por `groupId` + `authorId`(s); transação única.
- Expor nomes de membros na listagem de grupos — opções:
  - **A)** Enriquecer `GroupResponse` com `members: [{ participantId, displayName }]` ou `memberPreview: string[]` em `GET /v1/groups`; ou
  - **B)** Implementar `GET /v1/groups/{groupId}/members` conforme OpenAPI e agregar no client (evitar N+1 excessivo — considerar endpoint enriquecido se performance for problema).
- Atualizar `openapi/openapi.yaml` e testes de integração (`GroupIntegrationTest`, `BoxIntegrationTest`, novo teste de leave + remoção de experiências).

**Client:**
- `client/src/domain/box/boxTypes.ts` — estender tipo `Group` com dados de membros/preview.
- `client/src/domain/box/boxUseCases.ts` — `ListGroupsUseCase`; eventual `ListGroupMembersUseCase`.
- `client/src/presentation/groups/GroupSelectionPage.tsx` + `.module.css` — cards com cor, preview de nomes; remover convite/sair.
- `client/src/presentation/boxes/BoxSelectionPage.tsx` — adicionar sair do grupo (padrão `BoxHomePage`).
- Novo helper `groupVisuals.ts` (espelhar `boxVisuals.ts` / `sessionModeVisuals.ts`) para cor estável por `groupId`.
- i18n `groups.*`, `boxes.*` em `pt-BR`, `en`, `it`.

**Regras arquiteturais:**
- Clean Architecture no client (DT-13): use cases no domínio, API via adapters.
- Convite continua sendo **por grupo** (`POST /v1/groups/{groupId}/invites`) — não por caixinha.
- Modo Experience Box (`BoxHomePage`) mantém convite e sair no contexto da sessão conjunta; alinhar copy de saída à nova regra de remoção de experiências se aplicável aos leavers.
- Seguir @ref:pt-br-design-system: cards coloridos sólidos, texto branco ou contraste adequado, tipografia secundária pequena para nomes.

**Documentação:**
- Atualizar @ref:pt-br-data-model (tabela "Sair do grupo") e equivalentes `en`/`it`: experiências do autor **são removidas** ao sair.
- Ajuste mínimo em @ref:pt-br-functional-components se a posição de convite/sair mudar na descrição do módulo "Gerenciamento de grupo".

**Critérios de aceitação:**
- Participante com 2+ grupos vê todos na `GroupSelectionPage` e distingue visualmente cada um (cor + nomes).
- Convite na listagem de grupos removido; convite acessível dentro de `BoxSelectionPage`.
- Entrada por convite global continua na autenticação e `/join`.
- Sair do grupo disponível em `BoxSelectionPage`; confirmação descreve remoção das contribuições do usuário.
- Após sair, experiências do autor não aparecem mais nas caixinhas do grupo (verificar via API/listagem).
- Cores dos grupos usam tokens do design system; preview de nomes abrevia corretamente grupos grandes.
- Testes de integração API para leave + cascade de experiências; testes unitários client para formatação de preview de membros (se extraído).
- Build e testes passam; OpenAPI alinhado.

**Restrições:**
- Não alterar formação de grupo por login conjunto nem regras de convite (expiração, revogação).
- Não implementar nome editável para grupos — identidade continua sendo o conjunto de membros.
- Não redesenhar `BoxCard` nem lista de experiências nesta tarefa.
- Escopo de remoção de experiências: apenas ao **sair do grupo**; não mudar exclusão manual de experiência pelo autor.

---

## Melhorias

Ajustes de UX e composição visual que dependem dos componentes e fluxos anteriores.

## Compactar capa da carta sorteada com ênfase em intensidade

### Descrição

No momento compartilhado (`SharedMomentPage`), após sortear, a **capa** da carta (`DrawResultCard`, face antes da revelação) exibe intensidade, parâmetros (esforço, abertura, novidade) e selo de integridade para o grupo alinhar antes de virar a carta (@ref:pt-br-functional-components). A implementação atual empilha `IntensityBadge` (chip pequeno com nível + rótulo), três blocos verticais de parâmetros com ícone, label e estrelas (`ParameterStarsGroup` layout `cover`), e o selo — tudo dentro de `.cover` com `overflow: auto` e `min-height: 16rem`. Em viewports típicas de celular o conteúdo ultrapassa a altura do card e **aparece barra de rolagem**, quebrando a metáfora de carta física retangular e o ritual de leitura rápida em grupo.

A spec prevê na capa: nível de intensidade, parâmetros e selo (@ref:pt-br-data-model, @ref:pt-br-design-system — escala de intensidade 1–5 com calor afetivo). A tarefa "Representar parâmetros com estrelas na criação e na carta de sorteio" já alinhou parâmetros a ícones + estrelas coloridas; esta tarefa foca na **composição visual da capa do sorteio**: sem scroll, formato retangular estável, hierarquia clara com **intensidade em destaque**, parâmetros legíveis porém compactos, e selo quase imperceptível.

### Prompt IA

**Objetivo:** Redesenhar a capa da carta sorteada para caber em um card retangular fixo, **sem scroll**, com ênfase visual na intensidade e parâmetros compactos.

**Análise obrigatória (primeira etapa):**
1. Medir o layout atual em `DrawResultCard.module.css` — causa do scroll: `overflow: auto` em `.cover` + coluna vertical alta (`coverGroup` com `gap: 0.85rem` × 3 parâmetros + selo + `coverLabel`).
2. Propor composição que caiba em ~16rem de altura (ou aspect ratio explícito, ex. 4:3 / 3:2) em largura de tela mobile com `--space-page` — validar em ~320px e ~390px.
3. Documentar decisão de layout (grid vs flex, ordem dos blocos) antes de codificar.

**Estado atual (confirmar):**
- `client/src/presentation/shared-moment/DrawResultCard.tsx` — capa usa `ExperienceSummaryMeta` com `compact`.
- `client/src/presentation/shared-moment/DrawResultCard.module.css` — `.cover { overflow: auto }`; fundo com gradiente por `--intensity-accent`.
- `client/src/presentation/components/ExperienceSummaryMeta.tsx` — ordem: `IntensityBadge` → `ParameterStarsGroup` (layout `cover`) → `IntegritySeal`.
- `client/src/presentation/components/IntensityBadge.tsx` — chip textual (`intensity.levelNamed`); sem número hero.
- `client/src/presentation/components/ParameterStarField.tsx` + `ParameterStarField.module.css` — layout `cover`: ícone, label, estrelas `sm` em coluna.
- `client/src/presentation/components/IntegritySeal.tsx` — selo com ícone + label + código; `compact` ainda legível.
- Cores: `intensityTokens.ts`, tokens CSS `--intensity-accent`, `--param-effort/openness/novelty`.

**Hierarquia visual desejada (capa do sorteio):**

1. **Intensidade — protagonista**
   - Número do nível (1–5) **muito grande** (ex.: `clamp(3rem, 12vw, 4.5rem)`, peso 800–900).
   - Label pequeno abaixo ou acima: **"Intensidade"** (i18n `intensity.label` ou chave nova `sharedMoment.intensityLabel`).
   - Cor do número na cor canônica do nível (@ref:pt-br-design-system — escala 1–5); opcional: nome do nível (Leve, Coragem…) em tamanho secundário **muito** menor — não competir com o dígito.
   - Substituir ou complementar `IntensityBadge` apenas na capa do sorteio — não obrigar o mesmo visual na lista de experiências.

2. **Parâmetros — compactos, mesmo vocabulário visual**
   - Manter **ícone colorido**, **label** (Esforço / Abertura / Novidade) e **estrelas preenchidas** na cor do parâmetro — como hoje.
   - Reduzir espaçamento: gaps menores, estrelas `sm` ou nova variante `xs`, ícones menores.
   - Layout sugerido (escolher o que couber sem scroll):
     - **Três colunas** lado a lado na metade inferior do card; ou
     - **Uma linha** com três mini-blocos; ou
     - Ícone + label abreviado na mesma linha que as estrelas.
   - Somente leitura; `aria-label` por parâmetro preservado.

3. **Selo — discreto**
   - Quase imperceptível: fonte mínima (ex. 0.6–0.65rem), opacidade reduzida, sem ícone grande ou só ícone minúsculo.
   - Posição: canto inferior (ex. bottom-right), não competir com intensidade.
   - Manter `aria-label` e `title` para acessibilidade; código do selo pode truncar visualmente (ex. últimos 4–6 chars) se necessário — valor completo no `title`.

4. **Sem scroll**
   - Remover `overflow: auto` da capa; usar `overflow: hidden`.
   - Todo o conteúdo (incl. `coverLabel` "Antes de revelar" / equivalente) deve caber sem rolagem vertical nem horizontal.
   - Card permanece retangular com cantos `--radius-card`; altura fixa ou `aspect-ratio` consistente entre sorteios.

**Implementação sugerida:**
- Criar variante dedicada da meta da capa — ex. `DrawCardCover` ou `ExperienceSummaryMeta variant="drawCover"` — para não poluir lista/inline.
- Novo componente ou variante `IntensityHero` para o número grande (props: `level`, opcional `showName`).
- Estender `ParameterStarsGroup` com `layout="drawCover"` (ou reutilizar `inline` apertado) — CSS em `ParameterStarField.module.css`.
- `IntegritySeal`: prop `variant="minimal"` ou `drawCover` — opacidade ~0.45–0.55, tamanho reduzido.
- Ajustar `DrawResultCard.module.css`: grid da capa, ex.:
  ```
  [ coverLabel — opcional, pequeno ]
  [     INTENSIDADE HERO (centro)     ]
  [  effort  |  openness  |  novelty  ]
  [                    selo mínimo ↘ ]
  ```
- `coverLabel` pode encolher (fonte menor) ou integrar ao hero se redundante — avaliar na análise.
- Face revelada (`ExperienceContentBlock`) e animação de flip **inalteradas**.

**i18n:**
- `client/src/i18n/locales/{pt-BR,en,it}.json` — label "Intensidade" / "Intensity" / equivalente; revisar `sharedMoment.coverLabel` se o layout mudar.

**Regras arquiteturais:**
- Sem mudança de API nem campos de experiência (`intensity`, `parameters`, `seal`).
- Reutilizar `StarRating`, `parameterVisuals.ts`, `INTENSITY_COLORS` / tokens CSS existentes.
- Contraste: número de intensidade legível sobre fundo `color-mix` da capa; não depender só de cor (nível também visível como dígito grande).
- Não alterar filtros de sorteio, botões Sortear/Revelar nem `SharedMomentPage` além do card.

**Documentação (ajuste mínimo):**
- Atualizar @ref:pt-br-functional-components se a descrição da carta de resultado mencionar apenas "chip" de intensidade — passar a refletir número em destaque na capa.

**Critérios de aceitação:**
- Capa da carta sorteada **sem barra de rolagem** em mobile 320px e 390px de largura.
- Intensidade exibida com número grande + label "Intensidade" pequeno; cor do nível correta (1–5).
- Três parâmetros visíveis com ícone, label e estrelas coloridas — mais compactos que o layout atual.
- Selo presente mas visualmente secundário (usuário de produto descreve como "quase não enxergável").
- Formato retangular estável; flip e face revelada sem regressão.
- `ExperienceListPage` e outros usos de `ExperienceSummaryMeta` não degradados (variante isolada à capa do sorteio).
- Três locales com paridade de chaves novas/alteradas.
- Build do client passa.

**Restrições:**
- Escopo limitado à **capa** de `DrawResultCard` no momento compartilhado.
- Não redesenhar carta revelada, animação de flip ou dica de alinhamento âmbar.
- Não converter intensidade geral para estrelas (permanece número 1–5).
- Não alterar assistente de criação nem explorer de sugestões.

---
