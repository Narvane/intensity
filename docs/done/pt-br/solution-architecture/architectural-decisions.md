# Decisões Arquiteturais

Este documento registra as escolhas estruturais centrais que moldam a arquitetura do Intensity — sua justificativa, trade-offs, restrições conhecidas e riscos reconhecidos.

**Público:** arquitetos e engenheiros seniores que precisam entender *por que* a solução está organizada assim, sem detalhe operacional de implementação.

---

## Curta

O Intensity concentra o **núcleo do produto no client mobile** e usa uma **API de recursos centralizada** para persistência de dados — deliberadamente **não** um BFF. A arquitetura privilegia a **simplicidade**: apenas REST, sem mensageria ou WebSockets, instância única da API, muitas instâncias do client. O trade-off é a **custódia dos dados do usuário** no servidor. Um **modo offline** é identificado como direção futura para maior privacidade.

---

## Média

### Resumo das decisões

| Decisão | Escolha | Motivo principal |
|---------|---------|------------------|
| Núcleo do produto | Client mobile | O valor está na interface e na experiência guiada, não em lógica de negócio no servidor |
| Estilo da API | API de recursos | Centralização de dados sem acoplamento a telas específicas |
| Não usar BFF | Rejeitado | Regras de negócio simples; estrutura da interface é o diferencial |
| Protocolo client–servidor | Apenas REST | Suficiente para os padrões de interação da aplicação |
| Mecanismos em tempo real | Não utilizados | Sem necessidade de sync ao vivo, push ou orquestração de eventos |
| Topologia da API | Instância única | Adequada à escala atual; simplicidade sobre distribuição |
| Custódia de dados | Servidor centralizado | Necessária para registro individual de experiências por participante |
| Operação offline | Fora do escopo atual | Consideração futura para privacidade |

### Por que o núcleo vive no client

O Intensity é uma experiência de jogo construída em torno de uma mecânica simples: coletar experiências e sortear uma aleatoriamente. As regras de negócio não são complexas — não há motores de regras intrincados no servidor nem orquestrações multi-etapa.

O valor do produto está em:

- Como a interface estrutura a jornada do jogador
- O assistente de criação que orienta reflexão e parametrização
- O ritual do momento compartilhado que transforma um sorteio aleatório em revelação deliberada
- Sugestões pré-definidas que funcionam como tutorial implícito

Como a diferenciação é experiencial e não computacional, o client carrega o núcleo. O servidor não precisa deter lógica de apresentação, orquestração de fluxos ou comportamento de sorteio.

### Por que API de recursos — e não BFF

Um BFF adaptaria respostas da API a telas ou necessidades específicas do client. O Intensity rejeita isso porque:

- As telas já orquestram seus próprios fluxos no client
- Recursos de domínio mapeiam limpo para operações CRUD sem agregação
- Adicionar camada BFF introduziria acoplamento entre servidor e estrutura de UI sem benefício proporcional
- A mecânica de sorteio e o ritual de revelação nunca tocam o servidor

O papel da API é mais estreito e bem definido: **ser a fonte da verdade dos dados persistidos**.

### Por que os dados são centralizados

Uma API centralizada é **indispensável** para o modelo social do produto:

- Cada participante registra experiências **individualmente do seu próprio dispositivo** (modo Experiências)
- Essas experiências devem aparecer na **mesma caixinha** quando o grupo joga junto (modo Caixa de Experiências)
- Grupos, caixinhas e experiências devem ser **consistentes entre todas as instâncias do client**

Sem persistência centralizada, a contribuição por participante não convergiria em um pool compartilhado.

### Trade-offs aceitos

| Benefício | Custo |
|-----------|-------|
| Pool de experiências compartilhado entre dispositivos | Servidor guarda credenciais de participantes e dados de experiências pessoais |
| Arquitetura simples, rápida de construir e manter | Sem jogo offline; rede necessária para operações persistidas |
| Autonomia do client sobre UX e comportamento de jogo | Client deve mapear recursos para telas |
| Simplicidade de instância única da API | Escalar exigirá evolução arquitetural futura |
| Simplicidade do REST | Sem atualizações em tempo real; clients puxam estado sob demanda |

### Restrições conhecidas

- **Dependência de rede:** autenticação, cadastro e gestão de experiências exigem API disponível
- **Responsabilidade pelos dados:** operar API centralizada implica responsabilidade pela proteção dos dados dos participantes
- **Ritual em dispositivo único:** o sorteio acontece em um celular compartilhado; a arquitetura não exige sync multi-dispositivo em tempo real durante o jogo
- **Sem modo offline:** todas as leituras e escritas de domínio passam pela API na arquitetura atual

---

## Detalhada

### DA-01: Client como núcleo do produto

**Contexto:** O produto é uma experiência mobile de jogo centrada em mecânica de sorteio sobre conteúdo criado pelos usuários.

**Decisão:** O client mobile detém estrutura de interface, navegação, comportamento de sorteio, fluxos de criação e ritual do momento compartilhado. O servidor não orquestra jornadas de usuário.

**Justificativa:**
- As regras da aplicação são essencialmente um sorteador sobre um conjunto coletado — não um motor de domínio complexo
- A interface guia os jogadores a usar a criatividade e criar momentos; essa orientação é o valor do produto
- Manter o núcleo no client permite evoluir a experiência sem redeploy do servidor para mudanças de UX

**Consequências:**
- Releases do client carregam mudanças de produto; mudanças na API são impulsionadas por necessidades do modelo de dados
- A API permanece enxuta — validação e persistência, não orquestração de negócio
- Foco de testes desloca-se para comportamento e fluxos do client

**Alternativas consideradas:**
- **Fluxos dirigidos pelo servidor:** rejeitado — adiciona latência e acoplamento sem benefício para mecânica de sorteio simples
- **BFF por plataforma:** rejeitado — existe apenas uma plataforma de client (mobile); sem necessidade de agregação

---

### DA-02: API de recursos em vez de BFF

**Contexto:** O client precisa de dados persistidos. A API poderia expor recursos de domínio ou agregados específicos de tela.

**Decisão:** A API expõe endpoints orientados a recursos mapeados a entidades de domínio.

**Justificativa:**
- Recursos alinham-se ao modelo de dados funcional (participantes, grupos, caixinhas, experiências)
- CRUD mapeia diretamente sem composição de visão no servidor
- O client já sabe moldar dados para cada tela
- Um BFF duplicaria conhecimento de apresentação no servidor

**Consequências:**
- O client realiza agregação ou filtragem necessária localmente (ex.: filtros de intensidade no sorteio)
- Versionamento da API vincula-se ao modelo de domínio, não a iterações de UI
- Novas telas podem ser adicionadas sem mudanças na API se consumirem recursos existentes

**Alternativas consideradas:**
- **BFF (Backend-for-Frontend):** rejeitado — o negócio não justifica a camada adicional; a interface é o valor, não composição no servidor
- **GraphQL:** rejeitado — CRUD de recursos via REST é suficiente; sem requisitos complexos de consulta

---

### DA-03: Dados centralizados com instância única da API

**Contexto:** Múltiplos clients (celulares) devem compartilhar estado persistido. Cada participante contribui experiências individualmente.

**Decisão:** Uma instância da API em um ambiente de servidor atende todos os clients. O banco de dados por trás dela é a fonte única da verdade.

**Justificativa:**
- Registro individual de experiências por participante exige camada de persistência compartilhada
- Instância única é adequada à escala esperada de um jogo social entre casais e grupos de amigos
- Simplicidade reduz overhead operacional e de desenvolvimento

**Consequências:**
- Todos os clients dependem da disponibilidade da API para operações persistidas
- O operador assume responsabilidade pela proteção dos dados dos usuários
- Escalar além de instância única é preocupação futura, não requisito atual

**Alternativas consideradas:**
- **Apenas local / sync peer-to-peer:** rejeitado — impediria contribuição individual de dispositivos separados convergindo em uma caixinha
- **API multi-instância com balanceamento:** não requerida na escala atual; adiciona complexidade sem benefício imediato

---

### DA-04: Apenas REST — sem mensageria ou WebSockets

**Contexto:** Client e API precisam se comunicar. Múltiplos protocolos e padrões estão disponíveis.

**Decisão:** REST (HTTP requisição/resposta) é o único mecanismo de comunicação client–servidor. Sem filas de mensagens, barramentos de eventos ou canais WebSocket.

**Justificativa:**
- Operações persistidas são eventos CRUD discretos, não fluxos contínuos
- O ritual de sorteio roda em um celular compartilhado — sem necessidade de sincronização multi-dispositivo ao vivo
- Nenhuma notificação iniciada pelo servidor é requerida no produto atual
- Simplicidade alinha-se ao escopo da aplicação

**Consequências:**
- Clients puxam dados frescos quando necessário; sem atualizações baseadas em push
- Falhas da API são tratadas por requisição no client (ex.: snackbar de erro)
- Adicionar recursos em tempo real no futuro exigirá revisitar esta decisão

**Alternativas consideradas:**
- **WebSockets para atualizações ao vivo:** rejeitado — nenhum cenário de produto exige push do servidor durante o jogo
- **Fila de mensagens para processamento assíncrono:** rejeitado — sem processamento em background ou fluxos orientados a eventos no domínio atual

---

### DA-05: Simplicidade sobre complexidade arquitetural

**Contexto:** O Intensity é um produto focado com domínio estreito e mecânica central simples.

**Decisão:** A arquitetura consiste em exatamente dois artefatos de aplicação (client + API) e um artefato de persistência (banco de dados), conectados por REST.

**Justificativa:**
- O produto não é uma plataforma enterprise com necessidades complexas de integração
- Superengenharia atrasaria desenvolvimento sem melhorar a experiência do jogador
- O problema resolvido é experiencial, não computacional

**Consequências:**
- Menos partes móveis para implantar, monitorar e manter
- Fronteiras de responsabilidade claras entre client e API
- Algumas necessidades futuras (offline, escala, analytics) exigirão evolução arquitetural explícita

---

### DA-06: Trade-off de custódia de dados (risco aceito)

**Contexto:** Centralizar dados habilita o modelo de contribuição social, mas coloca informações de participantes no servidor.

**Decisão:** Aceitar custódia de dados no servidor como trade-off necessário para persistência centralizada.

**Justificativa:**
- Registro individual de experiências a partir de dispositivos separados exige armazenamento compartilhado
- Credenciais de participantes e conteúdo de experiências devem persistir em local acessível a todos os clients
- O produto não cumpre seu loop central sem essa centralização

**Consequências:**
- O operador deve tratar os dados com responsabilidade — proteção, controle de acesso e considerações de conformidade
- Participantes confiam o serviço com suas credenciais e conteúdo criativo
- Jogadores sensíveis à privacidade podem preferir alternativas

**Direção de mitigação (futuro, fora do escopo atual):**
- Um **modo offline** foi identificado como possibilidade futura para oferecer maior privacidade aos jogadores
- Seria evolução arquitetural significativa afetando client, API e modelo de sincronização
- Não faz parte da arquitetura atual

---

### Riscos e caminhos de evolução

| Risco | Estado atual | Possível evolução |
|-------|--------------|-------------------|
| Indisponibilidade da API bloqueia operações persistidas | Aceito | Modo offline com armazenamento local e sync |
| Instância única da API limita escala | Aceito | Escala horizontal atrás de load balancer |
| Sem refresh de dados em tempo real entre clients | Aceito | Notificações push ou otimização de polling |
| Preocupações de privacidade com dados centralizados | Reconhecido | Modo offline; políticas de minimização de dados |
| Impacto de violação de dados no servidor | Reconhecido | Práticas de segurança na camada de Engenharia |

### O que estas decisões não cobrem

Escolhas tecnológicas (linguagens, frameworks, engine de banco de dados), procedimentos de deploy, CI/CD, monitoramento e implementação de segurança pertencem à camada de **Engenharia e Operação**. Este documento trata apenas da organização estrutural e sua justificativa.
