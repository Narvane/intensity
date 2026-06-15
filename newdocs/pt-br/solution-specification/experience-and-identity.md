# Experiência e Identidade

Este documento define a identidade visual do Intensity, diretrizes de UX e tom de comunicação — como o produto parece, sente e fala com os usuários. É escrito para designers, product owners e quem molda a comunicação voltada ao usuário.

---

## Curta

O Intensity apresenta uma marca **calorosa, íntima e corajosa**. Dois modos de acesso usam cores de destaque distintas: **marrom** para Experiências (contribuição individual) e **azul** para Caixa de Experiências (ritual em grupo). Níveis de intensidade mapeiam para uma escala de cinco cores do verde (Leve) ao vermelho (Adrenalina). Tipografia é limpa e legível; cartas e selos reforçam confiança. A voz é direta, encorajadora e respeitosa do consentimento do grupo.

---

## Média

### Essência da marca

| Atributo | Expressão |
|----------|-----------|
| **Conexão** | Gradientes suaves, imagens em pares no onboarding, linguagem de proximidade |
| **Intensidade** | Escala de cores ousada, rótulos claros de nível, animação deliberada de revelação |
| **Descoberta** | Chips de sugestão lúdicos, tipos temáticos de caixinha, curiosidade no copy |
| **Presença** | Chrome mínimo durante ritual de sorteio; foco no momento da carta |

### Sistema visual

**Paleta primária:**

| Papel | Uso |
|-------|-----|
| Destaque marrom | Modo Experiências — contribuição, reflexão |
| Destaque azul | Modo Caixa de Experiências — ritual, união |
| Escala Verde → Vermelho | Níveis de intensidade 1–5 |
| Teal / Lima / Rosa | Parâmetros Esforço, Abertura, Novidade |
| Dica tracejada âmbar | Lembrete de alinhamento pré-revelação |

**Cores de intensidade:**

| Nível | Rótulo | Cor |
|-------|--------|-----|
| 1 | Leve | Verde |
| 2 | Desconfortável | Azul |
| 3 | Coragem | Âmbar |
| 4 | Ousadia | Laranja |
| 5 | Adrenalina | Vermelho |

### Logo e nomenclatura

- **Nome do produto:** Intensity — sempre capitalizado na interface
- **Logo:** Wordmark com gradiente sutil de intensidade; usado em splash, onboarding e cabeçalhos de autenticação
- **Ícone do app:** Motivo abstrato de chama ou pulso sugerindo calor e energia (assets de loja)

### Princípios de UX

1. **Clareza de modo** — cor e cabeçalho sinalizam imediatamente Experiências vs Caixa de Experiências
2. **Divulgação progressiva** — intensidade antes do texto; prévia de convite antes de entrar
3. **Consentimento explícito** — confirmações para excluir caixinha, sair do grupo, aceitar convite
4. **Estados vazios como orientação** — caixinha vazia incentiva contribuição; pool de sorteio vazio explica filtros
5. **Linha de base de acessibilidade** — alvos de toque ≥44pt; contraste atende WCAG AA para texto; rótulos de leitor de tela em ações primárias

### Terminologia (canônica)

| Termo na interface | Significado |
|--------------------|-------------|
| Experiência | Uma ideia concreta para fazer juntos |
| Caixinha | Coleção temática de experiências |
| Caixa de Experiências | Modo em grupo para caixinhas e ritual de sorteio |
| Grupo | Pessoas que compartilham caixinhas |
| Intensidade | Quão ousada uma experiência parece (1–5) |
| Sorteio | Seleção aleatória de uma experiência de uma caixinha |
| Revelar | Virar carta para ver descrição completa |
| Selo | Marca de integridade na carta de experiência |
| Convite | Link ou código para entrar em um grupo |
| Proponente | Pessoa que contribuiu uma experiência |

Evitar termos técnicos como "hash" no copy do usuário — usar **Selo**.

---

## Detalhada

### Narrativa visual do onboarding

Quatro etapas ilustradas contam a história emocional: rotinas repetitivas → saudade de conexão → momentos inusitados adiados → Intensity como resposta. Ilustrações usam casais e grupos de amigos diversos; tom é esperançoso, não clínico.

### Painéis de autenticação

Três subpainéis dentro de uma tela de autenticação:

| Painel | Indicador visual | Ação primária |
|--------|------------------|---------------|
| Login Experiências | Destaque marrom | Formulário de credencial única |
| Login Caixa de Experiências | Destaque azul | Cartões multi-credencial com "+" para adicionar participante |
| Registro | Neutro | Nome de exibição, e-mail, senha |
| Entrar via convite | Chip destaque verde | Campo de entrada de código + "Continuar" |

Entrada por convite é acessível da autenticação sem login completo — leva à tela de prévia após validação do código.

### Apresentação de tipos de caixinha

Onze tipos aparecem em **grade de duas colunas** com:

- Selo do tipo (badge de ícone)
- Título
- Dica de subtítulo
- Cor de destaque distinta por tipo

O catálogo tem seções internas de apresentação (amigos, casal, pessoal, social), mas a UI de criação mostra uma **lista plana** sem cabeçalhos de seção.

### Cartas de experiência

**Carta de lista (modo Experiências):** badge de intensidade, pontos de parâmetro ou linha compacta, selo, descrição truncada ou oculta dependendo da autoria.

**Carta de sorteio (modo Caixa de Experiências):** carta de dois lados com animação de virada no eixo Y. Capa: intensidade, parâmetros, selo. Face: descrição completa + reflexão + nome de exibição do autor.

### Ações destrutivas

**Excluir caixinha** e **Sair do grupo** usam:

- Destaque vermelho ou de aviso no botão de confirmar
- Resumo do impacto (contagem de experiências / perda de membresia)
- Cancelar como padrão seguro (botão secundário)

**Excluir experiência** (apenas autor): diálogo de confirmação mais simples; sem cascata além do item único.

### Folha de compartilhamento de convite

Folha de compartilhamento nativa com mensagem pré-preenchida:

*"Entre no nosso grupo no Intensity — [link]. Ou digite o código: [CÓDIGO]"*

Código exibido em monoespaçado, grande, copiável. Expiração mostrada como data legível.

### Tom de voz

| Contexto | Estilo |
|----------|--------|
| Onboarding | Caloroso, narrativo, segunda pessoa |
| Guia rápido | Regras diretas, verbos imperativos |
| Dica de alinhamento | Gentil, âmbar — "Reservem um momento juntos antes de revelar" |
| Erros | Linguagem simples, recuperação acionável |
| Estados vazios | Encorajador, nunca culpando |

**Exemplos:**

- ✓ "Sorteiem de novo se esta não couber no momento."
- ✓ "Todos na sala devem pertencer ao mesmo grupo."
- ✗ "Invalid group_combination_error."

### Localização

A interface suporta **inglês**, **português (Brasil)** e **italiano**. Termos de domínio são traduzidos consistentemente (ver docs localizados). Exemplos de pacotes de sugestão seguem o idioma da interface onde pacotes localizados existem; exemplos canônicos de autoria permanecem em português no catálogo embutido.

### O que a identidade evita deliberadamente

- Badges de gamificação ou sequências
- Estética de feed social
- Padrões de UI corporativa enterprise
- Urgência agressiva ou copy de FOMO

## Decisões assumidas nesta reescrita

- UI de **Convite** usa destaque verde para distinguir dos modos de autenticação.
- **Excluir caixinha** segue o mesmo padrão de confirmação destrutiva que sair do grupo.
- Rótulos de filtro na UI usam **Exata** e **Até** (não nomenclatura interna "fixed/max").
