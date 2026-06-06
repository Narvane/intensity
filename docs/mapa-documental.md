# Mapa Documental

Este mapa organiza a documentação do produto em **quatro camadas progressivas**, da visão de negócio até a engenharia.

## Como ler este mapa

### Documentos a produzir

Itens com **checkbox** são documentos oficiais. Cada um gera um arquivo em `/docs/done/{pt-br|en|it}/`.

```markdown
- [ ] **Nome do documento**   ← produzir
```

### Referências ilustrativas

Itens **sem checkbox**, listados abaixo de um documento, são **exemplos do tipo de informação esperada**. Orientam a intenção; **não** definem títulos nem estrutura obrigatória do Markdown.

```markdown
- [ ] **Visão Geral - O que é?**
- Pitch                        ← exemplo, não seção fixa
- Descrição estilo App Store   ← exemplo, não seção fixa
```

O agente deve capturar a **intenção** do documento e organizar o conteúdo conforme o produto — sem reproduzir mecanicamente todos os exemplos.

### Versões de profundidade

Cada documento produzido deve conter **três versões** do mesmo conteúdo:

- **Curta** — essência da mensagem central
- **Média** — a mesma mensagem com mais contexto e conexão
- **Detalhada** — a mesma mensagem com maior profundidade conceitual, sem mudar de camada

As três versões pertencem ao documento, não a cada exemplo ilustrativo.

Regra estrutural: Curta, Média e Detalhada variam em densidade explicativa, não em tipo de conteúdo permitido. A versão detalhada não autoriza antecipar conteúdo da próxima camada.

### Níveis de abstração

| Camada | Foco | Linguagem |
|--------|------|-----------|
| **1. Concepção do Produto** | O que é, para quem, qual problema, qual valor | Negócio e produto — sem tecnologia |
| **2. Especificação da Solução** | Como funciona funcionalmente: fluxos, regras, domínio | Funcional — sem implementação |
| **3. Arquitetura da Solução** | Como está organizada: componentes, integrações, APIs | Técnica estrutural |
| **4. Engenharia e Operação** | Como é construída e mantida: stack, deploy, CI/CD | Técnica operacional |

Detalhes completos de abstração e conteúdo permitido por camada: ver `agent.md`.

---

## 1. Concepção do Produto

*O que o produto é e por que existe. Público: usuários, stakeholders, gestores, investidores, analistas — sem conhecimento técnico.*

- [x] **Visão Geral - O que é?**
  - *Exemplos de conteúdo:* pitch; descrição estilo loja de apps; resumo executivo; essência; proposta de valor; posicionamento
- [x] **Funcionamento - Como funciona?**
  - *Exemplos de conteúdo:* dinâmica geral da experiência do usuário; macrofluxos percebidos; conceitos introdutórios; linguagem funcional sem parametrização
- [x] **Princípios - Por que funciona assim?**
  - *Exemplos de conteúdo:* filosofia do produto; objetivos; premissas; trade-offs conceituais; problemas que a solução busca resolver

---

## 2. Especificação da Solução

*Como a solução funciona do ponto de vista funcional. Público: analistas, POs, designers, QA — ainda sem implementação.*

- [ ] **Modelo de Dados**
  - *Exemplos de conteúdo:* entidades; categorias; parâmetros; configurações; conteúdo padrão; taxonomias
- [ ] **Experiência e Identidade**
  - *Exemplos de conteúdo:* logo; paleta de cores; tema visual; diretrizes de UX; linguagem e tom de comunicação
- [ ] **Componentes Funcionais**
  - *Exemplos de conteúdo:* módulos funcionais; funcionalidades; telas; fluxos de usuário

---

## 3. Arquitetura da Solução

*Como a solução está organizada. Público: arquitetos e engenheiros seniores.*

- [ ] **Plataformas e Ambientes**
  - *Exemplos de conteúdo:* web; mobile; backend; infraestrutura; ambientes de execução
- [ ] **Artefatos**
  - *Exemplos de conteúdo:* aplicações; serviços; APIs; bancos de dados; bibliotecas; componentes compartilhados
- [ ] **Integrações e Comunicação**
  - *Exemplos de conteúdo:* fluxos de comunicação; dependências; protocolos; eventos; contratos
- [ ] **Decisões Arquiteturais**
  - *Exemplos de conteúdo:* justificativas; trade-offs; restrições; riscos conhecidos

---

## 4. Engenharia e Operação

*Como a solução é construída e mantida. Público: desenvolvedores, DevOps, mantenedores.*

- [ ] **Ferramentas**
  - *Exemplos de conteúdo:* linguagens; frameworks; bibliotecas; serviços externos
- [ ] **Processo de Desenvolvimento**
  - *Exemplos de conteúdo:* organização do trabalho; versionamento; deploy; testes; qualidade
- [ ] **Equipe e Responsabilidades**
  - *Exemplos de conteúdo:* papéis; ownership; manutenção; sustentação
- [ ] **Decisões Técnicas**
  - *Exemplos de conteúdo:* motivos das escolhas; alternativas descartadas; critérios de avaliação
