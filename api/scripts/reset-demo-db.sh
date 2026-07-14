#!/usr/bin/env bash
# Reset local demo database (intensity_demo) and leave it empty for the next API boot + seed.
# Usage (from api/): ./scripts/reset-demo-db.sh
# Requires: docker compose postgres running, then restart API with SPRING_PROFILES_ACTIVE=demo
set -euo pipefail
cd "$(dirname "$0")/.."

echo "Dropping and recreating database intensity_demo..."
docker compose exec -T postgres psql -U intensity -d postgres -v ON_ERROR_STOP=1 <<'SQL'
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'intensity_demo' AND pid <> pg_backend_pid();
DROP DATABASE IF EXISTS intensity_demo;
CREATE DATABASE intensity_demo OWNER intensity;
SQL

echo "Done. Start/restart the API with: SPRING_PROFILES_ACTIVE=demo ./mvnw spring-boot:run"
