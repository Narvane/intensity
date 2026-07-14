#!/bin/sh
# Create intensity_demo on first Postgres volume init (docker-entrypoint-initdb.d).
set -eu
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE DATABASE intensity_demo;
EOSQL
