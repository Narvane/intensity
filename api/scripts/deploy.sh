#!/usr/bin/env bash
# Script de deploy no servidor: pull da imagem e restart dos containers.
# Uso: ./deploy.sh [DIR]
# DIR = pasta onde está docker-compose.prod.yml (default: diretório do script/..)

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_DIR="${1:-$SCRIPT_DIR/..}"
COMPOSE_FILE="docker-compose.prod.yml"

cd "$COMPOSE_DIR"

echo "[$(date -Iseconds)] Pulling images..."
docker compose -f "$COMPOSE_FILE" pull

echo "[$(date -Iseconds)] Restarting containers..."
docker compose -f "$COMPOSE_FILE" up -d

echo "[$(date -Iseconds)] Deploy done."
