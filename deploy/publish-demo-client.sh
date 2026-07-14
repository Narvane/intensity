#!/usr/bin/env bash
# Build the Vite demo SPA and publish into deploy/demo-web for nginx.
# Usage (from repo root or deploy/):
#   ./deploy/publish-demo-client.sh
# Optional: DEMO_API_DOMAIN / DEMO_APP_DOMAIN override baked URLs via temp .env
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
CLIENT_DIR="$ROOT_DIR/client"
OUT_DIR="$SCRIPT_DIR/demo-web"

cd "$CLIENT_DIR"

if [[ ! -f .env.demo ]]; then
  echo "Missing client/.env.demo" >&2
  exit 1
fi

# Optional overrides from deploy/.env.demo hostnames
if [[ -f "$SCRIPT_DIR/.env.demo" ]]; then
  # shellcheck disable=SC1091
  source "$SCRIPT_DIR/.env.demo"
  if [[ -n "${DEMO_API_DOMAIN:-}" && -n "${DEMO_APP_DOMAIN:-}" ]]; then
    export VITE_API_URL="https://${DEMO_API_DOMAIN}"
    export VITE_INVITE_BASE_URL="https://${DEMO_APP_DOMAIN}/join"
    export VITE_DEMO=true
    echo "Building with hosts from deploy/.env.demo (${DEMO_APP_DOMAIN})"
  fi
fi

npm ci
npm run build:demo

echo "Publishing dist → deploy/demo-web"
find "$OUT_DIR" -mindepth 1 ! -name '.gitkeep' -exec rm -rf {} +
cp -a dist/. "$OUT_DIR/"

echo "Done. Restart web: cd deploy && docker compose -f docker-compose.demo.yml --env-file .env.demo up -d web"
