# Deploy no servidor (VPS) – passo a passo

Este guia cobre: instalar Docker e dependências, subir a aplicação com o compose de produção, instalar o **webhook** para receber o deploy automático e configurar o GitHub Actions para chamar o webhook após o build.

---

## 1. Pré-requisitos

- VPS com SSH (Ubuntu 22.04 ou Debian 12 recomendado).
- Acesso root ou usuário com `sudo`.
- Repositório no GitHub com a imagem publicada no GHCR (após o primeiro build no Actions).

---

## 2. Instalar Docker e Docker Compose

Conecte no servidor e rode:

```bash
# Atualizar pacotes
sudo apt update && sudo apt upgrade -y

# Dependências
sudo apt install -y ca-certificates curl gnupg

# Chave e repositório Docker
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Instalar Docker
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Usuário atual pode rodar docker sem sudo (opcional)
sudo usermod -aG docker $USER
# Faça logout e login de novo para aplicar
```

Verifique:

```bash
docker --version
docker compose version
```

---

## 3. Colocar o projeto no servidor e configurar

Escolha um diretório (ex.: `/opt/narvane-api`). Você pode **clonar o repositório** ou só copiar os arquivos necessários.

### Opção A: Clonar o repositório

```bash
sudo mkdir -p /opt/narvane-api
sudo chown $USER:$USER /opt/narvane-api
cd /opt
git clone https://github.com/Narvane/narvane-api.git
cd narvane-api
```

(Substitua `Narvane/narvane-api` pelo seu `usuario/repo`.)

### Opção B: Só os arquivos do deploy

```bash
sudo mkdir -p /opt/narvane-api/scripts
sudo chown $USER:$USER /opt/narvane-api /opt/narvane-api/scripts
```

Copie para o servidor (via SCP, SFTP ou criando manualmente):

- `docker-compose.prod.yml` → `/opt/narvane-api/`
- `scripts/deploy.sh` → `/opt/narvane-api/scripts/`

Depois:

```bash
chmod +x /opt/narvane-api/scripts/deploy.sh
```

### Senha do Postgres (produção)

Crie um arquivo `.env` na pasta do compose com a senha do banco (e use a mesma no app):

```bash
cd /opt/narvane-api
echo "DS_PASS=sua_senha_segura_aqui" > .env
chmod 600 .env
```

O `docker-compose.prod.yml` usa `DS_PASS` do `.env`. Ajuste `sua_senha_segura_aqui`.

---

## 4. Login no GHCR para poder dar pull da imagem

A imagem fica em `ghcr.io/<usuario>/<repo>:latest`. Se o pacote for **privado**, o servidor precisa estar logado no GitHub Container Registry.

1. No GitHub: **Settings** → **Developer settings** → **Personal access tokens** → **Tokens (classic)**. Crie um token com permissão **`read:packages`**.
2. No servidor:

```bash
echo "SEU_TOKEN_AQUI" | docker login ghcr.io -u SEU_USUARIO_GITHUB --password-stdin
```

Assim o Docker consegue fazer `docker compose pull` da imagem. Para pacotes **públicos**, em muitos casos o login também é necessário; use o mesmo procedimento.

---

## 5. Subir a aplicação (primeira vez)

```bash
cd /opt/narvane-api
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
docker compose -f docker-compose.prod.yml ps
```

A API fica em `http://IP_DO_SERVIDOR:8080`. Confira com um browser ou `curl`.

---

## 6. Instalar o webhook (adnanh/webhook)

O webhook escuta uma URL e, quando recebe um POST com o segredo certo, executa o script de deploy.

### 6.1 Baixar o binário

Consulte a [página de releases](https://github.com/adnanh/webhook/releases) e use a versão Linux (ex.: `webhook-linux-amd64.tar.gz`). Exemplo para a última versão:

```bash
cd /tmp
WEBHOOK_VERSION="2.9.0"   # ajuste se houver versão mais nova
wget "https://github.com/adnanh/webhook/releases/download/${WEBHOOK_VERSION}/webhook-linux-amd64.tar.gz"
tar -xzf webhook-linux-amd64.tar.gz
sudo mv webhook-linux-amd64/webhook /usr/local/bin/webhook
sudo chmod +x /usr/local/bin/webhook
rm -rf webhook-linux-amd64 webhook-linux-amd64.tar.gz
webhook -version
```

### 6.2 Configurar o hook

Copie o exemplo e edite:

```bash
sudo mkdir -p /etc/webhook
sudo cp /opt/narvane-api/scripts/hooks.json.example /etc/webhook/hooks.json
sudo nano /etc/webhook/hooks.json
```

Altere:

1. **`execute-command`**: caminho do script de deploy, ex.: `/opt/narvane-api/scripts/deploy.sh`
2. **`command-working-directory`**: ex.: `/opt/narvane-api`
3. **`value`** (dentro de `trigger-rule` → `match`): coloque um **segredo forte** (ex.: string longa aleatória). Esse valor será o **DEPLOY_WEBHOOK_SECRET** no GitHub.

Exemplo (só a parte relevante):

```json
"execute-command": "/opt/narvane-api/scripts/deploy.sh",
"command-working-directory": "/opt/narvane-api",
...
"value": "meu_segredo_super_secreto_123",
```

Salve e feche.

### 6.3 Serviço systemd

Crie o serviço:

```bash
sudo nano /etc/systemd/system/webhook.service
```

Conteúdo:

```ini
[Unit]
Description=Webhook para deploy
After=network.target

[Service]
Type=simple
ExecStart=/usr/local/bin/webhook -hooks /etc/webhook/hooks.json -verbose -port 9000
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

Ative e inicie:

```bash
sudo systemctl daemon-reload
sudo systemctl enable webhook
sudo systemctl start webhook
sudo systemctl status webhook
```

O webhook fica escutando na **porta 9000**. A URL do hook é:

`http://IP_DO_SERVIDOR:9000/hooks/deploy-narvane-api`

(o `id` do hook no `hooks.json` é `deploy-narvane-api`).

### 6.4 Firewall

Libere a porta 9000 (e 8080 se quiser a API acessível):

```bash
sudo ufw allow 22
sudo ufw allow 8080
sudo ufw allow 9000
sudo ufw enable
```

Para produção, o ideal é colocar um **reverse proxy** (Nginx/Caddy) na frente e expor só 80/443, e fazer o proxy encaminhar para 9000 e 8080. Isso fica para um passo futuro.

### 6.5 Testar o webhook (no servidor)

```bash
curl -X POST http://localhost:9000/hooks/deploy-narvane-api \
  -H "X-Webhook-Secret: SEU_SEGREDO_AQUI"
```

Use o mesmo segredo que colocou no `hooks.json`. Deve retornar 200 e o deploy rodar (pull + restart).

---

## 7. Configurar o GitHub Actions para chamar o webhook

### 7.1 Secrets no repositório

No GitHub: repositório → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**. Crie:

| Nome                     | Valor                                                                 |
|--------------------------|-----------------------------------------------------------------------|
| `DEPLOY_WEBHOOK_URL`     | URL completa do hook, ex.: `http://IP_OU_DOMINIO:9000/hooks/deploy-narvane-api` |
| `DEPLOY_WEBHOOK_SECRET`  | O mesmo valor que você colocou em `value` no `hooks.json`             |

Se usar HTTPS no futuro, use `https://...` na URL.

### 7.2 Step no workflow

O workflow já deve ter um step (adicionado no repositório) que, após o build e push da imagem, chama o webhook:

- Faz um `POST` para `DEPLOY_WEBHOOK_URL`
- Envia o header `X-Webhook-Secret: DEPLOY_WEBHOOK_SECRET`

Assim, a cada push na branch configurada (ex.: `master`), o Actions:

1. Gera a imagem e envia para o GHCR.
2. Chama o webhook no seu servidor.
3. O webhook executa o script de deploy (pull + restart).

Se o step ainda não existir no `.github/workflows/build-and-push.yml`, ele deve ser algo assim (no mesmo job, depois do “Build and push image”):

```yaml
- name: Trigger deploy on server
  if: success()
  run: |
    curl -sf -X POST \
      -H "X-Webhook-Secret: ${{ secrets.DEPLOY_WEBHOOK_SECRET }}" \
      "${{ secrets.DEPLOY_WEBHOOK_URL }}"
```

(No repositório já deve estar o step equivalente; confira o arquivo do workflow.)

---

## 8. Resumo do fluxo

1. **Servidor:** Docker + Docker Compose instalados; projeto em `/opt/narvane-api`; `.env` com `DS_PASS`; login no GHCR; webhook instalado e rodando na porta 9000; hook configurado com o mesmo segredo que está no GitHub.
2. **GitHub:** Secrets `DEPLOY_WEBHOOK_URL` e `DEPLOY_WEBHOOK_SECRET`; workflow com step que chama essa URL após o build.
3. **A cada push na master:** Actions faz build → push da imagem → POST no webhook → servidor executa `deploy.sh` → `docker compose pull` + `up -d` → aplicação atualizada.

---

## 9. Ajustes opcionais

- **HTTPS no webhook:** Coloque Nginx ou Caddy na frente e use `https://seu-dominio.com/webhook/...` e configure o proxy para a porta 9000. Aí use essa URL em `DEPLOY_WEBHOOK_URL`.
- **Imagem privada:** Mantenha o `docker login ghcr.io` no servidor (ou um cron que renova o token se necessário).
- **Logs do deploy:** O webhook pode logar stdout/stderr do script; veja a opção `include-command-output-in-response` na documentação do webhook.
