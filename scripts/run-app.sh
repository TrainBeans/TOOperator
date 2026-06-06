#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
compose_file="$repo_root/docker-compose.yml"
base_url="http://localhost:${PORTAL_PORT:-8080}"
foreground=false
skip_health_check=false

usage() {
  cat <<'EOF'
Usage: ./scripts/run-app.sh [--foreground] [--skip-health-check]

Options:
  --foreground         Run docker compose in foreground mode.
  --skip-health-check  Start containers but do not run health checks.
  -h, --help           Show this help message.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --foreground)
      foreground=true
      ;;
    --skip-health-check)
      skip_health_check=true
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage
      exit 1
      ;;
  esac
  shift
done

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is required" >&2
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "docker compose plugin is required" >&2
  exit 1
fi

chmod +x "$repo_root/scripts/validate-compose.sh" "$repo_root/scripts/health-check.sh"
"$repo_root/scripts/validate-compose.sh"

if [[ "$foreground" == true ]]; then
  docker compose -f "$compose_file" up --build
  exit 0
fi

docker compose -f "$compose_file" up -d --build

if [[ "$skip_health_check" == false ]]; then
  "$repo_root/scripts/health-check.sh" "$base_url"
fi

echo "Application is running at ${base_url}"
echo "Use 'docker compose -f ${compose_file} down' to stop it."

