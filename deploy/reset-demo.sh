#!/usr/bin/env bash
# Wipe demo Postgres volume and recreate stack so Flyway + DemoSeedRunner run again.
# Intended for daily cron on the VPS.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

COMPOSE=(docker compose -f docker-compose.demo.yml --env-file .env.demo)

if [[ ! -f .env.demo ]]; then
  echo "Missing deploy/.env.demo" >&2
  exit 1
fi

# shellcheck disable=SC1091
source .env.demo

echo "Resetting demo database (down -v + up)..."
"${COMPOSE[@]}" down -v
"${COMPOSE[@]}" up -d

echo "Waiting for demo API health at https://${DEMO_API_DOMAIN}/actuator/health ..."
for _ in $(seq 1 60); do
  if curl -fsS "https://${DEMO_API_DOMAIN}/actuator/health" >/dev/null 2>&1; then
    echo "Demo reset complete."
    exit 0
  fi
  sleep 2
done

echo "Demo API did not become healthy in time; check: docker logs intensity-demo-api" >&2
exit 1
