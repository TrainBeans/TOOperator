#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is required" >&2
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "docker compose plugin is required" >&2
  exit 1
fi

# Validate compose structure and interpolation.
docker compose -f "$repo_root/docker-compose.yml" config >/dev/null

# Ensure persistent data volumes are mounted at /app/data for both apps.
compose_config="$(docker compose -f "$repo_root/docker-compose.yml" config)"
for mount in "trainorder-data:/app/data" "clearancecard-data:/app/data"; do
  volume_name="${mount%%:*}"
  if ! printf '%s\n' "$compose_config" | grep -q "$volume_name"; then
    echo "Missing persistent volume mount: $mount" >&2
    exit 1
  fi
done

echo "Compose file is valid."

