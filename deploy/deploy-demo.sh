#!/usr/bin/env bash
# Pull API image and start/restart the public demo stack (isolated DB + seed).
# Requires production stack running (shared Docker network `intensity` + Caddy).
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

COMPOSE=(docker compose -f docker-compose.demo.yml --env-file .env.demo)

if [[ ! -f .env.demo ]]; then
  echo "Missing deploy/.env.demo — copy .env.demo.example and fill in secrets." >&2
  exit 1
fi

# shellcheck disable=SC1091
source .env.demo

if [[ -z "${DEMO_API_DOMAIN:-}" || -z "${DEMO_APP_DOMAIN:-}" ]]; then
  echo "Set DEMO_API_DOMAIN and DEMO_APP_DOMAIN in .env.demo" >&2
  exit 1
fi

if ! docker network inspect intensity >/dev/null 2>&1; then
  echo "Docker network 'intensity' not found. Start production first: ./deploy.sh" >&2
  exit 1
fi

if [[ -n "${1:-}" && -n "${2:-}" ]]; then
  export API_IMAGE="${1}:${2}"
fi

echo "Writing Caddy demo site snippet..."
mkdir -p caddy-snippets
cat > caddy-snippets/demo.caddy <<EOF
${DEMO_API_DOMAIN} {
	reverse_proxy intensity-demo-api:8080
}

${DEMO_APP_DOMAIN} {
	reverse_proxy intensity-demo-web:80
}
EOF

echo "Deploying demo ${API_IMAGE}..."
"${COMPOSE[@]}" pull api
"${COMPOSE[@]}" up -d

if [[ ! -f demo-web/index.html ]]; then
  echo "Note: deploy/demo-web has no SPA yet. Run ./publish-demo-client.sh then re-run this script (or restart web)."
fi

if [[ -f .env ]]; then
  echo "Reloading production Caddy to pick up demo sites..."
  docker compose -f docker-compose.prod.yml --env-file .env up -d proxy
fi

echo "Demo stack up."
echo "  API health: curl -fsS https://${DEMO_API_DOMAIN}/actuator/health"
echo "  Web:        curl -fsSI https://${DEMO_APP_DOMAIN}/"
echo "Accounts: leo@ / maya@ / nico@demo.intensity.app  password demo1234"
