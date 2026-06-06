#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
compose_file="$repo_root/docker-compose.yml"
base_url="${1:-http://localhost:${PORTAL_PORT:-8080}}"
skip_build="${CI_SMOKE_SKIP_BUILD:-false}"

cleanup() {
  docker compose -f "$compose_file" down -v --remove-orphans || true
}

trap cleanup EXIT

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is required" >&2
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "docker compose plugin is required" >&2
  exit 1
fi

chmod +x "$repo_root/scripts/validate-compose.sh" \
         "$repo_root/scripts/health-check.sh" \
         "$repo_root/scripts/smoke-test.sh"

"$repo_root/scripts/validate-compose.sh"

if [[ "$skip_build" == "true" ]]; then
  docker compose -f "$compose_file" pull trainorder clearancecard
  docker compose -f "$compose_file" up -d
else
  docker compose -f "$compose_file" up -d --build
fi

"$repo_root/scripts/health-check.sh" "$base_url"
"$repo_root/scripts/smoke-test.sh" "$base_url"

echo "CI smoke run passed."

