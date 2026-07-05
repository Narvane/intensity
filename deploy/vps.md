# Guia: migrar VPS de narvane-api → Intensity

Substituição completa na mesma VPS, reutilizando Docker, Compose, Caddy e webhook.
Domínio: **narvane.com.br** com subdomínios `api.` e `app.`.

> **Atenção:** este guia **apaga** containers, volumes e deploy do narvane-api.
> Se precisar de dados do banco antigo, faça backup antes de `docker compose down -v`.

---

## Visão geral

| Antes (narvane) | Depois (Intensity) |
|-----------------|-------------------|
| `narvane.com.br` → API + `/hooks/*` | `api.narvane.com.br` → API |
| Caddy no compose do narvane | Caddy no compose do Intensity |
| Webhook na porta 9000 (host) | Mesmo webhook, paths atualizados |
| — | `app.narvane.com.br` → deep links (`/.well-known/*`) |

Imagem Docker: `ghcr.io/<seu-usuario>/intesity-2/api` (tags `latest` e SHA do commit).

---

## Parte 1 — Antes de mexer na VPS

### 1.1 GitHub — secrets do repositório `intesity-2`

Em **Settings → Secrets and variables → Actions**, crie:

| Secret | Valor |
|--------|-------|
| `DEPLOY_WEBHOOK_SECRET` | string aleatória longa (ex.: `openssl rand -hex 32`) |
| `DEPLOY_WEBHOOK_URL` | ver seção 1.3 — configure **depois** do Caddy subir |

### 1.2 GitHub — primeira imagem no GHCR

1. Faça merge/push na branch `master` ou `main` com mudanças em `api/**`.
2. Confira em **Actions** que o workflow **API CI** passou.
3. Em **Packages** do GitHub, verifique o pacote `intesity-2/api`.
4. Se o pacote for privado: na VPS você precisará de `docker login ghcr.io` (passo 4.4).

### 1.3 URL do webhook (escolha uma opção)

**Opção A — Igual ao narvane (recomendado):** HTTPS via Caddy

```
https://api.narvane.com.br/hooks/intensity-api-deploy
```

Requer ajuste no `Caddyfile` (passo 5.3). Só configure `DEPLOY_WEBHOOK_URL` no GitHub **depois** disso funcionar.

**Opção B — Porta 9000 direta (como no README padrão):**

```
http://IP_DA_VPS:9000/hooks/intensity-api-deploy
```

Mais simples, menos seguro (porta aberta). Use firewall ou migre para A depois.

---

## Parte 2 — DNS no Registro.br

1. Acesse [registro.br](https://registro.br) → seu domínio **narvane.com.br** → **DNS**.
2. Anote o **IP público** da VPS (`curl -4 ifconfig.me` na VPS).
3. Adicione dois registros **A**:

| Tipo | Nome | Destino | TTL |
|------|------|---------|-----|
| A | `api` | `IP_DA_VPS` | 300–3600 |
| A | `app` | `IP_DA_VPS` | 300–3600 |

Isso cria `api.narvane.com.br` e `app.narvane.com.br`.

4. Aguarde propagação (minutos a algumas horas). Teste:

```bash
dig +short api.narvane.com.br
dig +short app.narvane.com.br
```

Ambos devem retornar o IP da VPS.

### Sobre `narvane.com.br` (raiz)

- Pode continuar apontando para a VPS enquanto o narvane existir.
- Depois da migração: remova o registro A da raiz, ou redirecione no Caddy, ou deixe sem uso.
- **Não é obrigatório** comprar outro domínio.

---

## Parte 3 — Limpar o narvane-api na VPS

Conecte via SSH.

### 3.1 Inventário

```bash
docker ps -a
docker volume ls
ls -la /opt/
```

Procure algo como `/opt/narvane`, `/opt/narvane-api` ou pasta com `docker-compose.yml` e `Caddyfile` antigo.

### 3.2 Parar e remover o stack antigo

```bash
cd /opt/narvane/deploy   # ajuste o caminho real
docker compose down
```

Se **não** precisar dos dados do Postgres do narvane:

```bash
docker compose down -v
```

Remova imagens órfãs (opcional):

```bash
docker image ls | grep -i narvane
docker rmi <IMAGE_ID>   # se ainda existir
```

### 3.3 Parar serviços systemd do narvane (se houver)

```bash
sudo systemctl list-units --type=service | grep -iE 'narvane|webhook|deploy'
```

Se existir unit do webhook apontando para hooks do narvane:

```bash
sudo systemctl stop narvane-webhook   # nome pode variar
sudo systemctl disable narvane-webhook
```

**Não desinstale** o binário `webhook` — vamos reutilizá-lo.

### 3.4 Backup do `.env` antigo (opcional)

```bash
cp /opt/narvane/deploy/.env ~/narvane-env-backup.txt
```

Útil para lembrar padrões de senha/email, **não** reutilize JWT/DB do narvane no Intensity.

### 3.5 Remover pasta antiga (quando tiver certeza)

```bash
sudo rm -rf /opt/narvane
```

### 3.6 Liberar portas 80 e 443

```bash
sudo ss -tlnp | grep -E ':80|:443'
```

Não deve haver nada escutando antes de subir o Intensity. Se houver:

```bash
docker stop <container_id>
```

---

## Parte 4 — Instalar o Intensity

### 4.1 Clonar o repositório

```bash
sudo mkdir -p /opt/intensity
sudo chown "$USER:$USER" /opt/intensity
git clone https://github.com/<SEU_USUARIO>/intesity-2.git /opt/intensity
cd /opt/intensity
git checkout master   # ou main
```

### 4.2 Configurar `deploy/.env`

```bash
cd /opt/intensity/deploy
cp .env.example .env
nano .env
```

Exemplo para narvane.com.br:

```env
API_IMAGE=ghcr.io/<seu-usuario-github-minusculo>/intesity-2/api:latest

API_DOMAIN=api.narvane.com.br
APP_DOMAIN=app.narvane.com.br
ACME_EMAIL=seu@email.com

POSTGRES_DB=intensity
POSTGRES_USER=intensity
POSTGRES_PASSWORD=<gere-senha-forte>
INTENSITY_JWT_SECRET=<gere-32+-chars-aleatorios>

DEPLOY_WEBHOOK_SECRET=<mesmo-valor-do-secret-no-GitHub>
```

Gerar segredos:

```bash
openssl rand -base64 32
```

### 4.3 Scripts executáveis

```bash
chmod +x deploy.sh webhook/receive.sh
```

### 4.4 Login no GHCR (se o pacote for privado)

1. GitHub → **Settings → Developer settings → Personal access tokens**
2. Token com `read:packages` (classic) ou permissão Packages no fine-grained.
3. Na VPS:

```bash
echo "SEU_TOKEN" | docker login ghcr.io -u SEU_USUARIO_GITHUB --password-stdin
```

### 4.5 Ajustar `hooks.json` (caminho do script)

O arquivo já espera `/opt/intensity`:

```json
"execute-command": "/opt/intensity/deploy/webhook/receive.sh"
```

Se clonou em outro path, edite `deploy/webhook/hooks.json` para bater com o caminho real.

---

## Parte 5 — Caddy e webhook

### 5.1 Caddy padrão do repo (sem `/hooks` no HTTPS)

O `Caddyfile` do repo já cobre `api.` e `app.`. Suba o stack:

```bash
cd /opt/intensity/deploy
./deploy.sh
```

Na primeira subida o Caddy pede certificados Let's Encrypt (DNS já deve estar ok).

### 5.2 Webhook no host (reutilizar do narvane)

Crie ou atualize a unit systemd `/etc/systemd/system/intensity-webhook.service`:

```ini
[Unit]
Description=Intensity deploy webhook
After=network.target docker.service

[Service]
ExecStart=/usr/bin/webhook -hooks /opt/intensity/deploy/webhook/hooks.json -port 9000 -verbose
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now intensity-webhook
sudo systemctl status intensity-webhook
```

Teste local:

```bash
curl -X POST http://127.0.0.1:9000/hooks/intensity-api-deploy \
  -H "Content-Type: application/json" \
  -H "X-Deploy-Secret: SEU_SECRET" \
  -d '{"sha":"latest"}'
```

### 5.3 (Recomendado) Webhook atrás do Caddy — estilo narvane

Edite `deploy/Caddyfile` para o bloco da API ficar assim:

```caddy
{$API_DOMAIN} {
    handle /hooks/* {
        reverse_proxy host.docker.internal:9000
    }

    handle {
        reverse_proxy api:8080
    }
}
```

No `docker-compose.prod.yml`, no serviço `proxy`, adicione:

```yaml
extra_hosts:
  - "host.docker.internal:host-gateway"
```

Reinicie o proxy:

```bash
docker compose -f docker-compose.prod.yml up -d proxy
```

Teste:

```bash
curl -X POST "https://api.narvane.com.br/hooks/intensity-api-deploy" \
  -H "Content-Type: application/json" \
  -H "X-Deploy-Secret: SEU_SECRET" \
  -d '{"sha":"latest"}'
```

### 5.4 Configurar secret no GitHub (se ainda não fez)

`DEPLOY_WEBHOOK_URL`:

```
https://api.narvane.com.br/hooks/intensity-api-deploy
```

(opção A) ou `http://IP:9000/hooks/intensity-api-deploy` (opção B).

---

## Parte 6 — Verificação

```bash
# API
curl -fsS https://api.narvane.com.br/actuator/health

# Deep links
curl -fsS https://app.narvane.com.br/.well-known/assetlinks.json
curl -fsS https://app.narvane.com.br/.well-known/apple-app-site-association

# Containers
docker ps
```

Esperado:

| Container | Função |
|-----------|--------|
| `intensity-postgres` | Banco |
| `intensity-api` | Spring Boot |
| `intensity-proxy` | Caddy :80/:443 |

### Deploy automático via CI

1. Faça um push em `api/**` na `master`.
2. Confira **Actions** → build + push GHCR + POST webhook.
3. Na VPS: `docker logs intensity-api --tail 50` se algo falhar.

---

## Parte 7 — Deep links (antes da loja)

Edite no repo (não só na VPS):

- `client/deep-link/.well-known/assetlinks.json` — SHA256 do certificado Android release
- `client/deep-link/.well-known/apple-app-site-association` — Team ID Apple

Depois: `git pull` na VPS e `docker compose -f docker-compose.prod.yml up -d proxy`.

---

## Parte 8 — Rollback

No `deploy/.env`:

```env
API_IMAGE=ghcr.io/<user>/intesity-2/api:<SHA_ANTERIOR>
```

```bash
./deploy.sh
```

---

## Checklist rápido

- [ ] Registro.br: registros A para `api` e `app`
- [ ] DNS propagado (`dig`)
- [ ] narvane: `docker compose down` (+ `-v` se ok apagar dados)
- [ ] Portas 80/443 livres
- [ ] Repo em `/opt/intensity`
- [ ] `deploy/.env` preenchido
- [ ] `docker login ghcr.io` (se pacote privado)
- [ ] `./deploy.sh` OK
- [ ] Webhook systemd apontando para `intensity/deploy/webhook/hooks.json`
- [ ] (Opcional) `/hooks/*` no Caddy + `extra_hosts`
- [ ] GitHub secrets: `DEPLOY_WEBHOOK_URL`, `DEPLOY_WEBHOOK_SECRET`
- [ ] `curl` health + `.well-known` OK
- [ ] Push teste na `master` dispara deploy

---

## Problemas comuns

| Sintoma | Causa provável | Solução |
|---------|----------------|---------|
| Caddy não sobe | 80/443 ocupadas | `ss -tlnp`, pare stack antigo |
| Certificado falha | DNS não aponta para VPS | Confira Registro.br, aguarde TTL |
| `pull` falha | GHCR sem login | `docker login ghcr.io` |
| Webhook 401 | Secret diferente | `.env` = GitHub secret |
| Webhook não alcança host | Falta `extra_hosts` | Adicione no compose do proxy |
| API unhealthy | Postgres senha errada | Confira `.env` e logs `intensity-api` |

---

## Ordem sugerida de execução

1. DNS no Registro.br (pode propagar enquanto você limpa a VPS)
2. GitHub: gerar `DEPLOY_WEBHOOK_SECRET` (URL depois)
3. Push para gerar imagem no GHCR
4. SSH: limpar narvane
5. Clonar Intensity + `.env`
6. `./deploy.sh`
7. Webhook systemd (+ Caddy `/hooks/*` se quiser)
8. GitHub: `DEPLOY_WEBHOOK_URL`
9. Testes `curl` + push de teste
