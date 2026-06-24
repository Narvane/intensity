# Plano de desenvolvimento — backlog UI/auth/ritual

Plano derivado de `backlog.md` (6 tarefas), ordenado conforme `agents/order-backlog.md`.

**Premissa:** a tarefa de auth pode exigir decisão sobre reutilização de token vs re-digitar senha no Box — validar contrato API antes de implementar slot 1 sem senha.

---

## Visão geral

| Etapa | Tarefa | Tipo | PR sugerido |
|-------|--------|------|-------------|
| 1 | Sessão individual persistida na tela de login | Feature / auth | `feat/auth-persisted-experiences` |
| 2 | Modo de sessão no footer sticky | Refatoração layout | `refactor/session-mode-footer` |
| 3 | Capa do sorteio (selo, virar carta, dica) | Feature ritual | `feat/draw-cover-flip-copy` |
| 4 | Card compacto na listagem | Melhoria UX | `ui/experience-list-compact` |
| 5 | Polish visual do wizard | Melhoria UX | `ui/wizard-warm-polish` |
| 6 | Ícone do app (logo-icon) | Polimento / assets | `chore/app-launcher-icon` |

---

## Etapa 1 — Sessão individual persistida na tela de login

**Objetivo:** login Experiences “lembra” o participante na `/auth`; Box pré-preenche slot 1.

**Subentregas**

1. **Leitura de sessão em `AuthPage`**
   - `useSession()` + `refresh()` on mount
   - Derivar `hasExperiencesSession = session?.accessMode === 'EXPERIENCES'`

2. **UI aba Experiências**
   - Campos disabled + e-mail de `session` / registro
   - Botão logoff compacto no card
   - CTA continuar vs submit login

3. **UI aba Experience Box**
   - Pré-preencher participante 1 com e-mail da sessão Experiences
   - Manter fluxo multi-credencial para demais membros
   - Avaliar: API `LoginExperienceBoxUseCase` exige senha por slot — se sim, só e-mail pré-preenchido + autocomplete

4. **Navegação “voltar” vs logout**
   - Garantir que voltar para `/auth` **não** chame `useAppLogout` (só botões “Sair” explícitos)
   - Revisar `NavButton` back em páginas Experiences

5. **Opcional:** `autocomplete` attributes; nota em docs sobre WebView password manager

**Gate de saída**
- [ ] Fluxo host descrito pelo usuário (Experiences → auth → Box slot 1) funciona
- [ ] Logoff restaura forms
- [ ] Testes auth/guest existentes verdes; build OK

**Risco:** armazenar senha — **não fazer**; display-only na UI.

---

## Etapa 2 — Modo de sessão no footer sticky

**Objetivo:** liberar topo; chrome de modo fixo embaixo.

**Subentregas**

1. Criar `SessionModeFooter` ou prop `placement="footer"` em `SessionModeChrome`
2. CSS sticky + safe-area + sombra superior
3. Migrar páginas: `GroupSelectionPage`, `BoxSelectionPage`, `ExperienceListPage`, `CreateBoxExperiencesPage`, `BoxHomePage`, `SharedMomentPage`, `CreateBoxPage`
4. Ajustar `ScreenHeader`: título no `content`, sem `SessionModeChrome` nos children

**Gate de saída**
- [ ] Footer visível em todas as telas de sessão
- [ ] Conteúdo não oculto atrás do footer
- [ ] Smoke em Experiences e Experience Box

**Dependência:** independente da etapa 1; pode paralelizar após etapa 1 ou em PR separado.

---

## Etapa 3 — Capa do sorteio

**Objetivo:** sem overflow; copy “virar carta”; dica em card.

**Subentregas**

1. Remover `coverLabel` de `DrawResultCard`
2. Refatorar layout `DrawCardCover` — selo em fluxo (grid rows: hero, params, seal)
3. i18n `reveal`, `alignmentHint` (pt-BR, en, it)
4. Componente/markup dica: dashed border + `Lightbulb` + fundo amarelo suave

**Gate de saída**
- [ ] Capa legível em mobile sem clip
- [ ] Botão “Virar carta”; flip 3D OK
- [ ] Dica com aparência de “tip card”

---

## Etapa 4 — Card compacto na listagem

**Objetivo:** intensidade + flip na mesma linha; parâmetros centralizados.

**Subentregas**

1. Refatorar header do `ExperienceCard` (flip + `IntensityBadge`)
2. CSS `listCompact` / `experienceList` — centralização horizontal
3. Verificar flip all e altura do card

**Gate de saída**
- [ ] Card mais baixo que versão atual
- [ ] Sem regressão flip/edit/delete

---

## Etapa 5 — Polish visual do wizard

**Objetivo:** explorer warm/yellow; step 3 colorido por parâmetro.

**Subentregas**

1. `SuggestionExplorer.module.css` — paleta yellow/coral warm; ícones nos botões
2. `ParameterStarField` wizard + `StarRating` — fundos/bordas por `--param-*`
3. Revisão contraste WCAG

**Gate de saída**
- [ ] Step 1 e 3 visualmente distintos do explorer roxo anterior
- [ ] Wizard E2E manual OK

**Dependência:** nenhuma com etapa 4.

---

## Etapa 6 — Ícone do app

**Objetivo:** launcher Android (e iOS) com `assets/logo-icon.png`.

**Subentregas**

1. Exportar adaptive icon Android (foreground PNG/WebP por densidade)
2. Atualizar `ic_launcher_foreground` / mipmaps
3. iOS AppIcon se `client/ios` existir
4. README ou STORE_RELEASE nota opcional

**Gate de saída**
- [ ] Ícone correto após install no device/emulador

---

## Estratégia de PRs

| Opção | Composição |
|-------|------------|
| **3 PRs (recomendado)** | `1` auth · `2+3` layout+sorteio · `4+5+6` polish |
| **6 PRs** | Uma etapa por PR |

Recomendação: **etapa 1 isolada** (maior risco de produto); **etapa 2** antes de polimentos que assumem layout de header.

---

## Validação final do ciclo

1. `npm run build` + `npm test` no client
2. Smoke manual:
   - Login Experiences → voltar auth → campos locked → logoff
   - Auth → Box → slot 1 pré-preenchido → login grupo → sorteio
   - Footer sticky em grupos/caixinhas/lista/sorteio
   - Capa sorteio sem overflow; virar carta; dica com lâmpada
   - Lista compacta; wizard amarelo/step 3 colorido
   - Ícone launcher no Android
3. `./mvnw test` se etapa 1 tocar API (improvável)

---

## Fora de escopo

- Novo endpoint de auth (salvo descoberta na etapa 1)
- Redesign completo de `ScreenHeader`
- Splash screen animada
