#!/usr/bin/env bash
# Build the Vite demo SPA and publish into deploy/demo-web for nginx.
# Prefers local npm; falls back to Docker (node:22) when npm is missing — typical on the VPS.
# Usage:
#   ./deploy/publish-demo-client.sh
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
CLIENT_DIR="$ROOT_DIR/client"
OUT_DIR="$SCRIPT_DIR/demo-web"
STALE_MARKER='intensity-demo-device-shell'

if [[ ! -f "$CLIENT_DIR/.env.demo" ]]; then
  echo "Missing client/.env.demo" >&2
  exit 1
fi

if grep -Rql "$STALE_MARKER" "$CLIENT_DIR/src" 2>/dev/null; then
  echo "ERROR: stale device-shell code still in client/src — pull latest main." >&2
  exit 1
fi

VITE_API_URL=""
VITE_INVITE_BASE_URL=""

if [[ -f "$SCRIPT_DIR/.env.demo" ]]; then
  # shellcheck disable=SC1091
  set -a
  source "$SCRIPT_DIR/.env.demo"
  set +a
  if [[ -n "${DEMO_API_DOMAIN:-}" && -n "${DEMO_APP_DOMAIN:-}" ]]; then
    VITE_API_URL="https://${DEMO_API_DOMAIN}"
    VITE_INVITE_BASE_URL="https://${DEMO_APP_DOMAIN}/join"
    echo "Building with hosts from deploy/.env.demo (${DEMO_APP_DOMAIN})"
  fi
fi

export VITE_DEMO=true
echo "VITE_DEMO=${VITE_DEMO}"

build_with_npm() {
  cd "$CLIENT_DIR"
  export VITE_DEMO=true
  if [[ -n "$VITE_API_URL" ]]; then
    export VITE_API_URL VITE_INVITE_BASE_URL
  fi
  rm -rf dist
  npm ci
  npm run build:demo
}

build_with_docker() {
  echo "npm not found — building with Docker node:22"
  rm -rf "$CLIENT_DIR/dist"
  docker run --rm \
    -v "$ROOT_DIR:/repo" \
    -w /repo/client \
    -e "VITE_API_URL=${VITE_API_URL}" \
    -e "VITE_INVITE_BASE_URL=${VITE_INVITE_BASE_URL}" \
    -e "VITE_DEMO=true" \
    node:22-bookworm \
    bash -lc 'rm -rf dist && npm ci && npm run build:demo'
}

if command -v npm >/dev/null 2>&1; then
  build_with_npm
elif command -v docker >/dev/null 2>&1; then
  build_with_docker
else
  echo "Need npm or Docker to build the demo client." >&2
  exit 1
fi

if [[ ! -f "$CLIENT_DIR/dist/index.html" ]]; then
  echo "ERROR: client/dist/index.html missing after build." >&2
  exit 1
fi

if grep -Rql "$STALE_MARKER" "$CLIENT_DIR/dist"; then
  echo "ERROR: build produced stale device-shell bundle — source may be outdated." >&2
  exit 1
fi
echo "Build OK (no device-shell marker in dist)."

echo "Publishing dist → deploy/demo-web (wipe first)"
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"
cp -a "$CLIENT_DIR/dist/." "$OUT_DIR/"
touch "$OUT_DIR/.gitkeep"

if grep -Rql "$STALE_MARKER" "$OUT_DIR"; then
  echo "ERROR: demo-web still contains device-shell marker after copy." >&2
  exit 1
fi

echo "Published OK."
echo "Restart web:"
echo "  cd $SCRIPT_DIR && docker compose -f docker-compose.demo.yml --env-file .env.demo up -d --force-recreate web"
