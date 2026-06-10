# Experiência e Identidade

Este documento descreve a identidade visual, os padrões de interação e a linguagem de comunicação do Intensity — como o produto se apresenta, como os usuários percebem sua interface e quais convenções de UX governam a experiência. Especifica *como a solução se sente e comunica*, sem detalhe de implementação.

**Público:** analistas, product owners, designers e QA funcional — pessoas que precisam entender ou reproduzir a experiência do produto sem saber como ele foi construído.

---

## Curta

O Intensity se apresenta como **Intensity**, com **ícone de caixa** e uma linguagem visual **quente e baseada em cards**. A interface segue o tema **claro ou escuro** do dispositivo e usa **marrom** para o modo **Experiências** (contribuição individual) e **azul** para o modo **Caixa de Experiências** (ritual em grupo). **Onze tipos de caixinha** carregam paleta de destaque distinta. A comunicação está disponível em **português, inglês e italiano**, com tom emocionalmente acolhedor e centrado no grupo. Princípios centrais de UX incluem **revelação progressiva** (intensidade antes do texto completo), **alinhamento em grupo antes de revelar**, **onboarding** na primeira execução e **guia rápido** reutilizável, além de **feedback por snackbar** para erros.

---

## Média

### Marca e identidade visual

| Elemento | Comportamento observável |
|----------|--------------------------|
| **Nome do produto** | "Intensity" — exibido no cabeçalho de autenticação, barras superiores e áreas de marca |
| **Logo** | Ícone de caixa/estoque pareado com o nome; nenhum logo raster personalizado observado |
| **Cabeçalho de autenticação** | Barra azul em gradiente com ícone e nome em branco |
| **Superfícies** | Fundos off-white quentes com gradiente vertical sutil; cards brancos com bordas quentes suaves |
| **Paleta primária (modo Experiências)** | Marrom quente como cor de ação principal |
| **Paleta de participante (modo Caixa de Experiências)** | Gradiente azul para ações de grupo e papel de participante |
| **Níveis de intensidade 1–5** | Verde → azul → âmbar → laranja → vermelho, cada um com tom de superfície correspondente |
| **Parâmetros** | Esforço (teal), abertura (verde-limão), novidade (rosa) — cada um com ícone e cor de superfície |
| **Estrelas de avaliação** | Âmbar/dourado |
| **Formas** | Cantos arredondados em toda a interface — de chips pequenos a cards grandes |
| **Tipografia** | Títulos em negrito (22/18 sp), corpo 16/14 sp; fonte padrão do sistema |

A interface adapta-se à **preferência claro/escuro do sistema**. No modo escuro, fundos mudam para marrons profundos; o marrom primário clareia; o texto torna-se off-white quente.

### Tematização por contexto

O destaque visual muda conforme o contexto operacional:

| Contexto | Indicador visual |
|----------|------------------|
| **Modo Experiências** | Botões primários marrons, cards de autenticação marrons quando selecionados |
| **Modo Caixa de Experiências** | Botões primários azuis, cards de autenticação azuis quando selecionados, barra de status azul na abertura |
| **Tipo de caixinha ativo** | Gradiente da barra superior e destaques seguem a paleta do tipo selecionado (11 temas distintos) |
| **Progresso do assistente** | Barra de cinco segmentos com indicador âmbar do passo atual |

### Padrões visuais recorrentes

- **Cards** — containers brancos/superfície com raio de 16 dp, borda quente, elevação leve
- **Barra superior em gradiente** — 62 dp de altura, preenchimento em gradiente, título e ações em branco
- **Botões primários** — largura total, formato pílula, 52 dp de altura; variante marrom ou azul conforme o papel
- **Cabeçalhos de seção** — ícone (26 dp) + título em negrito; rótulos em small-caps com espaçamento entre letras
- **Chips de filtro** — formato pílula; azul quando selecionado
- **Card com flip** — card de experiência gira no eixo Y para revelar descrição após alinhamento
- **Cards de caixinha em grade** — grade de duas colunas, raio de 20 dp, selo de tipo com ícone
- **Pontos de intensidade** — seletores circulares 1–5 para filtros de sorteio
- **Avaliação por estrelas** — interativo 1–5 com texto auxiliar por nível abaixo

Ícones são glifos no estilo material em toda a interface (grupos, favorito, caixa/estoque, estrela, raio, lâmpada, flip, etc.).

### Convenções de UX

1. **Codificação de cor por papel** — marrom sinaliza contribuição individual (Experiências); azul sinaliza presença em grupo (Caixa de Experiências).
2. **Revelação progressiva** — descrição da experiência oculta até revelação explícita (ação de olho na lista, flip do card no momento compartilhado).
3. **Alinhamento em grupo antes de revelar** — dica tracejada âmbar pedindo acordo sobre clima, limites e compromisso antes de virar o card.
4. **Educação na primeira execução** — onboarding ilustrado em quatro passos, dispensável; guia rápido opcional com regras do produto.
5. **Ajuda sempre acessível** — guia rápido e onboarding reabertos pela barra de ferramentas de autenticação.
6. **Feedback de erro transitório** — erros exibidos via snackbar, não banners inline persistentes.
7. **Indicadores de carregamento** — spinner para bootstrap, carregamento de listas e ações de sorteio; overlay semitransparente durante envio do assistente.
8. **Estados vazios dedicados** — mensagens textuais de vazio dentro de cards ou seções, sem ilustrações.
9. **Lista do proponente por sessão** — lista de Experiências mostra apenas as contribuições do usuário atual na sessão ativa.
10. **Semântica de intensidade** — cada nível (1–5) carrega subtítulo e cor; parâmetros exibem ajuda contextual e descrições por estrela.

### Linguagem e tom de comunicação

**Idiomas suportados:** português (padrão), inglês, italiano — selecionáveis via controle de bandeira, disponível nas telas de onboarding e autenticação.

**Vocabulário do produto** (como exibido ao usuário):

| Termo | Papel |
|-------|-------|
| Experiência | Ideia concreta para fazer juntos |
| Caixa de Experiências | Modo ritual em grupo — caixinhas, sorteio, revelação |
| Caixinha | Recipiente temático para experiências coletadas |
| Intensidade | Nível geral de ousadia 1–5 |
| Sorteio | Seleção aleatória de uma caixinha |
| Revelar | Momento deliberado de ver a descrição completa |
| Selo | Impressão digital de integridade nos cards de experiência |
| Esforço / Abertura / Novidade | Três avaliações de parâmetros no assistente de criação |

**Subtítulos dos níveis de intensidade:** Leve → Desconfortável → Coragem → Ousado → Adrenalina.

**Características do tom:**
- Conversacional e emocionalmente acolhedor
- Centrado no grupo ("alinhem como grupo antes de revelar", "todos alimentam a caixinha")
- Instrutivo no guia rápido — regra central, fluxo recomendado, dicas de intensidade, dicas de consequência, essência do produto
- Prefixos de erro diretos ("Erro", "Falha no login") com mensagens de validação específicas no assistente

O onboarding narra uma história em quatro passos: problema (experiências repetitivas) → insight (memorável = inesperado) → chamada à ação → mecânica do produto (coletar, sortear, viver).

---

## Detalhada

### Sistema de cores em profundidade

O sistema visual organiza três famílias de cor:

**Tema base (claro):**
- Fundo: off-white quente `#FCFAF7`
- Containers de superfície: `#F6EFE6` / `#EFE7DB`
- Marrom primário: `#B0946F`
- Texto: `#1D1B20`; variante `#49454F`
- Erro: `#B3261E`

**Tema base (escuro):**
- Fundo: `#15110E`; superfície `#1C1713`
- Primário: `#D4BC9A`; sobre superfície `#ECE3DC`

**Tokens de papel e semântica:**
- Azul participante: gradiente `#1E5EFF` → `#4C7CFF`; superfície `#E8F1FF`
- Parâmetro esforço: teal `#00A3B4` / superfície `#DCFBFF`
- Parâmetro abertura: limão `#84CC16` / superfície `#F7FEE7`
- Parâmetro novidade: rosa `#E11D48` / superfície `#FFE4E6`
- Intensidade 1: verde `#2E7D32`; 2: azul `#0277BD`; 3: âmbar `#F9A825`; 4: laranja `#EF6C00`; 5: vermelho `#C62828` — cada um com tom de superfície correspondente
- Borda de card: marrom quente em baixa opacidade
- Estrela de avaliação: `#F9A825`

Cada um dos **onze tipos de caixinha** carrega destaque, superfície e gradiente de barra superior próprios — visíveis ao navegar caixinhas e quando uma caixinha está ativa na lista de experiências ou no momento compartilhado.

### Tipografia e espaçamento

Títulos usam negrito em 22 sp (grande) e 18 sp (médio). Corpo em 16 sp com altura de linha 22 sp; corpo secundário em 14 sp. Rótulos de seção em negrito 14 sp. Rótulos small-caps em maiúsculas com espaçamento entre letras.

Ritmo de espaçamento comum: padding de tela 16–18 dp; gaps entre cards 12–14 dp; padding horizontal da barra superior 2–16 dp.

Raios de canto variam de 9 dp (extra pequeno) a 24 dp (extra grande), conferindo sensação suave e acolhedora.

### Fundo das telas

A maioria das telas usa gradiente vertical da cor de superfície quente para branco (claro) ou de tom médio-escuro para fundo profundo (escuro), estabelecendo continuidade visual no fluxo.

### Onboarding e guia rápido

**Onboarding** — quatro passos ilustrados com indicadores de ponto, navegação Voltar/Avançar, "Começar" para finalizar e atalho "Abrir guia rápido". Seletor de idioma disponível. Pode ser reaberto pela autenticação.

**Guia rápido** — cinco seções de conteúdo:
1. **Regra central** — coletar ao longo do tempo, levar o sorteio a sério, avaliar 1–5, escolher uma experiência, definir consequência, decidir antes de revelar
2. **Fluxo recomendado** — todos alimentam a caixinha; ativar sorteio quando juntos; alinhar antes de revelar
3. **Dicas — Intensidade** — começar baixo, evoluir gradualmente, permitir trocas, usar filtros
4. **Dicas — Consequência** — definir primeiro, custo real, escalar se alguém desistir, variar com trocas
5. **Essência do Intensity** — conexão, intensidade, descoberta; viver momentos significativos com presença

Dica de fechamento: "Em caso de dúvida, alinhem como grupo antes de revelar."

### Modos visuais de autenticação

Três cards de modo selecionáveis na tela de autenticação:

| Modo | Cor quando selecionado | Subtítulo |
|------|------------------------|-----------|
| **Experiências** | Marrom | Registrar experiências e escolher caixinha |
| **Caixa de Experiências** | Azul | Grupo entra junto; grupo compartilhado |
| **Cadastro** | Marrom | Criar conta nova |

O painel Experiências exibe login por e-mail/senha. O painel Caixa de Experiências permite adicionar uma ou mais credenciais de usuário antes do login. O painel Cadastro coleta nome de exibição, e-mail e senha.

### Apresentação de parâmetros e avaliações

No assistente de criação, cada parâmetro (esforço, abertura, novidade) exibe:
- Ícone e cor dedicados
- Frase de ajuda explicando o que o parâmetro mede
- Linha de estrelas 1–5 com prompt de toque
- Descrição dinâmica por nível que atualiza conforme as estrelas mudam

A classificação de intensidade mostra os cinco subtítulos de nível com cores correspondentes e permite ajuste manual do nível sugerido automaticamente.

### Apresentação de sorteio e revelação

No momento compartilhado, antes do sorteio:
- Chips de filtro: **Qualquer**, **Exata**, **Até** — com seletor opcional de pontos de intensidade
- Card de dica incentivando ativação quando a caixinha está vazia

Após o sorteio:
- Card mostra capa de intensidade primeiro (nível, parâmetros, selo)
- Dica tracejada âmbar pede alinhamento em grupo antes de revelar
- Ação de flip revela descrição completa
- "Voltar ao sorteio" retorna à seleção de filtros

### Identidade de navegação

A navegação é linear e orientada por estado — sem barra de abas ou barra lateral persistente. Ações de voltar usam ícones na barra superior que redefinem o escopo da sessão (caixinha → grupo → autenticação). Logout sempre disponível nas barras autenticadas. Overlays (onboarding, guia, assistente) empilham sobre a tela atual sem substituir a pilha de navegação.

### Observações de acessibilidade

O contraste segue os padrões do tema. Intensidade e parâmetros usam cor **e** rótulos textuais (subtítulos, descrições auxiliares). Foco e navegação por teclado não puderam ser totalmente validados por análise estática. Rótulos para leitores de tela não foram auditados exaustivamente.

---

## Lacunas e limitações

- **Logo personalizado** — apenas ícone de caixa padrão é usado; nenhuma marca raster ou vetorial além da combinação ícone + wordmark foi encontrada.
- **Breakpoints responsivos** — o app visa mobile; adaptações para tablet e desktop não foram observadas.
- **Auditoria de acessibilidade** — contraste, navegação por teclado e cobertura de leitores de tela não foram totalmente validados.
- **Tradução parcial** — alguns textos dinâmicos de sugestão podem passar sem localização completa em todos os idiomas; strings de interface com chave estão totalmente traduzidas em PT, EN e IT.
- **Tema personalizável** — apenas claro/escuro do sistema é suportado; sem seletor de tema no app.
- **Interface de consequência** — o guia rápido referencia consequências como orientação social; nenhuma tela de entrada de consequência foi encontrada na interface.
