# Visão Geral — O que é?

Documento de concepção do produto **Intensity**. Conteúdo derivado do comportamento observável da aplicação, dos textos exibidos ao usuário e da estrutura funcional do repositório.

---

## Pitch

### Curta

**Intensity** ajuda amigos e casais a colecionar ideias de experiências inusitadas, classificá-las por intensidade e sorteá-las para viver momentos de conexão — em vez de adiar o inesperado.

### Média

Cansado de experiências repetitivas que pouco aproximam quem importa? O **Intensity** é um aplicativo mobile para grupos e casais: cada pessoa registra ideias em caixinhas temáticas, classifica de 1 a 5 em intensidade e, quando estão juntos, sorteiam uma experiência para viver na hora. O produto encoraja a agir em vez de esperar a casualidade criar momentos marcantes.

### Detalhada

O **Intensity** transforma a coleta de ideias e o sorteio em um ritual compartilhado. Participantes convidados criam contas, formam grupos e alimentam **caixinhas de experiências** ao longo do tempo — cada ideia passa por sugestão, reflexão, parametrização (esforço, abertura, novidade) e classificação final de intensidade. No encontro presencial, o app sorteia uma experiência da caixa, com filtros por nível de intensidade. A essência do produto é **conexão, intensidade e descoberta**: não se trata de cumprir tarefas, mas de viver momentos marcantes com presença. O ecossistema observável inclui app mobile (Android, com alvo iOS) e API dedicada (**Intensity API**), com suporte a português, inglês e italiano.

---

## Descrição estilo App Store / Google Play

### Curta

Colecione ideias inusitadas. Sorteie. E viva momentos marcantes com quem importa.

### Média

**Intensity** — conexão, intensidade e descoberta.

Crie caixinhas com amigos ou em casal, adicione experiências ao longo do tempo, classifique de 1 a 5 e sorteie quando estiverem juntos. Filtros de intensidade, sugestões por tipo de caixa e manual integrado. Disponível em português, inglês e italiano.

### Detalhada

Você sente falta de proximidade? Os momentos mais marcantes quase sempre foram os mais inusitados — mas ficam para depois. O **Intensity** muda isso.

**Como funciona**

- Cadastre-se (acesso por convite) e entre solo para registrar experiências ou em grupo para abrir a **Caixa de Experiências**.
- Escolha ou crie caixinhas temáticas: saídas com amigos, viagens em casal, momentos de conexão e outras categorias disponíveis no app.
- Registre ideias com um assistente em cinco passos: sugestão, reflexão, estrelas de esforço/abertura/novidade e intensidade final (de *Leve* a *Adrenalina*).
- No encontro, sorteie uma experiência — qualquer nível, intensidade exata ou até um nível máximo.
- Antes de revelar, alinhem clima, limites e compromisso; o manual rápido orienta consequências e evolução gradual de intensidade.

Ideal para casais e grupos de amigos que querem sair da rotina com intenção. Desenvolvido por **Narvane**.

---

## Resumo executivo

### Curta

**Intensity** é um produto mobile com API da Narvane para grupos e casais registrarem, classificarem e sortearem experiências compartilhadas, promovendo conexão por meio de momentos inusitados planejados coletivamente.

### Média

O repositório contém duas aplicações principais: **intensity-api** (Spring Boot, PostgreSQL, autenticação JWT) e cliente **Kotlin Multiplatform** (Compose, Android e alvo iOS). O domínio central gira em torno de **grupos**, **caixinhas tipadas** e **experiências** com escala de intensidade de 1 a 5. Dois modos estruturam o uso: **Experiências** (cadastro individual, modo CURATE) e **Caixa de Experiências** (sessão coletiva com sorteio, modo CONNECT). O registro é fechado por lista de e-mails autorizados. Idiomas da interface: PT, EN e IT.

### Detalhada

**Problema endereçado (evidência: textos de onboarding e manual):** experiências repetitivas, distância emocional entre pessoas próximas e adiamento de momentos diferenciados que poderiam aproximá-las.

**Solução observável:** caixinhas colaborativas por grupo, sorteio ritualizado de experiências, parametrização multidimensional (intensidade, esforço, abertura, novidade) e fluxo de reflexão na criação de cada ideia.

**Arquitetura funcional (produto, não implementação):** participantes → grupos (combinação de pessoas que se conectaram juntas) → caixinhas (tipo temático) → experiências (conteúdo protegido no servidor).

**Fluxos principais:** onboarding e manual rápido → autenticação → modo individual (selecionar grupo → caixinha → cadastrar/editar experiências) ou modo em grupo (caixinhas → sorteio com filtros de intensidade).

**Estado observável:** versão de app `1.0.0`, acesso por convite, sem cliente web neste repositório. Regras sociais sugeridas no manual (consequências, trocas entre níveis) aparecem apenas como orientação textual — não foram encontradas como funcionalidade implementada na API ou no banco de dados.

---

## Evidências e limitações

| Tópico | Status |
|--------|--------|
| Nome de marca na interface | **Intensity** (`app.brand`, rótulo Android) |
| Essência do produto | *Conexão, intensidade e descoberta* (manual rápido) |
| Problema e proposta de valor | Onboarding em quatro passos (`PtDictionary.kt` e equivalentes EN/IT) |
| Público-alvo inferível | Grupos de amigos e casais (tipos de caixinha e copy do manual) |
| Tagline oficial única | **Não encontrada** — há fragmentos convergentes no onboarding |
| Nome "Intensity Box" | Aparece no README do cliente; a UI usa **Intensity** |
| Modelo de negócio / preço | **Não encontrado** no repositório |
| Consequências e trocas entre níveis | Orientação no manual; **sem evidência de implementação** no software |
| Cliente web | **Não presente** neste repositório |

**Fontes principais:** dicionários de interface (`PtDictionary.kt`, `EnDictionary.kt`, `ItDictionary.kt`), `IntensityApp.kt`, `openapi.yaml`, migrações de banco (`V221__intensity2_init.sql`), `ExperienceBoxTypeCodes.kt`.
