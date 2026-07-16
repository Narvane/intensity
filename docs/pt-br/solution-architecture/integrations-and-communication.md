# Integrações e Comunicação

Este documento descreve como os componentes do Intensity se comunicam — protocolos, fluxos de dados, contratos e direções de dependência. É escrito para arquitetos e engenheiros seniores que integram ou estendem o sistema.

---

## Curta

O cliente mobile conversa com a API via **REST sobre HTTPS** (request/response, iniciado pelo cliente). A API conversa com PostgreSQL via **persistência ORM** e envia e-mail transacional (redefinição de senha) via **Resend**. Não há **push do servidor**, **WebSockets** nem **caminho direto cliente-para-banco de dados**. Convites usam links HTTPS resolvidos pelo SO mobile no app. Consistência é **eventual** — clientes atualizam na leitura.

---

## Média

### Mapa de integração

```
Cliente mobile ──REST (HTTPS)──► API ──JPA/Hibernate──► PostgreSQL
     │                              │
     │                              └── Resend (HTTPS) ──► caixa de entrada do participante
     └── sem DB direto              └── único gateway de persistência
```

| Integração | Protocolo | Direção |
|------------|-----------|---------|
| Cliente → API | REST JSON | Cliente inicia |
| API → DB | SQL via ORM | Apenas API |
| API → Resend | HTTPS JSON | E-mail transacional de saída |
| Cliente → folha de compartilhamento do SO | Ponte nativa | Compartilhamento de convite de saída |
| Deep link → Cliente | App/Universal Links | Abertura de convite de entrada |

### Modelo de sincronização

**Consistência eventual baseada em pull.** Quando um participante adiciona uma experiência do celular, outros clientes veem na próxima leitura da API. O ritual em celular compartilhado busca o pool de experiências da API imediatamente antes do sorteio.

Sem notificações ao vivo quando dados mudam. Sem sincronização multi-dispositivo durante sorteio — um celular mantém estado de sorteio localmente.

### Fluxos-chave

**Autenticação**

```
Cliente POST /v1/auth/login { email, password }
  ← { token, participantId, displayName }
Cliente armazena token localmente para requisições subsequentes
```

**Redefinição de senha**

```
Cliente POST /v1/auth/forgot-password { email }
  ← 204 (sempre; e-mail enviado só se a conta existir)
API → Resend → caixa de entrada com {APP_BASE}/auth/reset-password?t={token}
Navegador ou app abre a página de reset (página estática no APP_DOMAIN; deep link Capacitor quando instalado)
Cliente POST /v1/auth/reset-password { token, password }
  ← 204
```

O `APP_DOMAIN` hospeda `/.well-known` de deep link e a página **estática** de redefinição de senha. Não hospeda o SPA React completo (cliente de loja é Capacitor; o SPA demo público fica no host demo).

**Login conjunto (Caixa de Experiências)**

```
Cliente POST /v1/auth/group { credentials[], reuseSessionToken? }
  ← { token, groupId, groupIds, members, accessMode }
  OU 409 se credenciais abrangem grupos incompatíveis
```

**Ciclo de vida de convite**

```
POST /v1/groups/{id}/invites        → { code, linkToken, expiresAt }
GET  /v1/invites/validate?code=      → { groupPreview, expiresAt, status }
POST /v1/invites/{id}/accept       → { groupId, membership confirmed }
DELETE /v1/invites/{id}             → revogado
```

**Registro de experiência (modo Experiências)**

```
Cliente coleta entrada do assistente localmente
POST /v1/boxes/{id}/experiences { description, intensity, params, type, reflection? }
  ← experiência persistida com selo
Bifurcação: POST /v1/boxes/{id}/experiences/batch { experiences: [...] } (até 5)
  ← lista de experiências persistidas com selos
```

**Exclusão de caixinha (modo Caixa de Experiências)**

```
DELETE /v1/boxes/{id}
  ← 204; remove experiências em cascata no servidor
Cliente atualiza GET /v1/groups/{id}/boxes
```

**Ritual de sorteio (sem escrita na API)**

```
GET /v1/boxes/{id}/experiences → pool
Cliente filtra, randomiza, revela localmente
(sem POST para resultado de sorteio)
```

### Contrato de erro

Erros REST retornam `{ code, message }` com status HTTP apropriado. Cliente mapeia para copy voltada ao usuário. Casos críticos:

| Status | Cenário |
|--------|---------|
| 401 | Token inválido ou expirado |
| 403 | Não é membro do grupo |
| 404 | Caixinha, grupo ou convite não encontrado |
| 409 | Conflito de membresia de grupo no login conjunto |
| 410 | Convite expirado ou revogado |
| 422 | Falha de validação (tamanho do nome, faixa de intensidade) |

---

## Detalhada

### Esboço de recursos REST

| Recurso | Operações |
|---------|-----------|
| `/v1/auth/login` | POST participante único |
| `/v1/auth/group` | POST sessão conjunta multi participante (opcional `reuseSessionToken`) |
| `/v1/auth/forgot-password` | POST solicitar e-mail de redefinição (sempre 204) |
| `/v1/auth/reset-password` | POST definir nova senha com token |
| `/v1/participants` | POST registrar |
| `/v1/groups` | GET listar; POST criar (nome, cor) |
| `/v1/groups/{id}` | PATCH atualizar nome/cor |
| `/v1/groups/{id}/members` | DELETE self (sair) |
| `/v1/groups/{id}/invites` | POST criar; GET listar ativos |
| `/v1/invites/validate` | GET por código ou token |
| `/v1/invites/{id}/accept` | POST |
| `/v1/invites/{id}` | DELETE revogar |
| `/v1/groups/{id}/boxes` | GET listar |
| `/v1/boxes` | POST criar (inclui `requireAllParticipants`) |
| `/v1/boxes/{id}` | DELETE (cascata) |
| `/v1/boxes/{id}/experiences` | GET listar; POST criar |
| `/v1/boxes/{id}/experiences/batch` | POST criar até 5 |
| `/v1/experiences/{id}` | PUT atualizar; DELETE (apenas autor) |

Prefixo de versão `/v1` explícito; mudanças breaking exigem `/v2` conforme decisões técnicas. Não há handler `GET /v1/groups/{id}/members` — membros vêm nas respostas de grupo/auth.

### Contrato de link de convite

Formato de deep link (ilustrativo):

```
https://app.intensity.example/join?t={linkToken}
```

SO mobile roteia para app instalado → cliente chama `GET /convites/validar?t=` → tela de prévia.

Caminho por código: usuário insere `AB12CD` → `GET /convites/validar?code=AB12CD`.

Ambos os canais resolvem o mesmo registro de convite.

### Segurança na rede

- TLS em todo lugar em produção
- Bearer token em requisições autenticadas
- Tokens armazenados em armazenamento seguro do cliente (Capacitor Preferences ou wrapper de keystore da plataforma)
- Sem credenciais em links de convite — token é opaco, de propósito único

### Integrações explicitamente ausentes

Gateways de pagamento, SDKs de analytics, serviços de push notification (FCM/APNs), IdP externo (OAuth), pipeline de assets CDN, filas de mensagens, webhooks do cliente. E-mail transacional usa apenas Resend (redefinição de senha).

### Webhook operacional (camada de engenharia)

Deploy da API de produção usa webhook de entrada do CI — documentado na camada de engenharia, não é integração de produto.

## Decisões assumidas nesta reescrita

- Validação de convite é um **GET somente leitura** antes do POST de aceitar.
- Login conjunto retorna **409** quando credenciais pertencem a grupos existentes diferentes.
- Exclusão de caixinha é **REST síncrono** com cascata no servidor.
