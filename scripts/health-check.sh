#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
base_url="${1:-http://localhost:${PORTAL_PORT:-8080}}"
max_attempts="${MAX_ATTEMPTS:-20}"
sleep_seconds="${SLEEP_SECONDS:-3}"

if ! command -v curl >/dev/null 2>&1; then
  echo "curl is required" >&2
  exit 1
fi

http_status() {
  local path="$1"
  curl -fsS -o /dev/null -w "%{http_code}" "${base_url}${path}"
}

assert_status_exact() {
  local path="$1"
  local expected="$2"
  local status
  status="$(http_status "$path")"
  if [[ "$status" != "$expected" ]]; then
    echo "FAIL ${path} expected ${expected} got ${status}" >&2
    return 1
  fi
  echo "PASS ${path} -> ${status}"
}

assert_status_not_5xx() {
  local path="$1"
  local status
  status="$(curl -sS -o /dev/null -w "%{http_code}" "${base_url}${path}")"
  if [[ "$status" =~ ^5 ]]; then
    echo "FAIL ${path} got ${status}" >&2
    return 1
  fi
  echo "PASS ${path} -> ${status}"
}

assert_actuator_up() {
  local path="$1"
  local body
  body="$(curl -fsS "${base_url}${path}")"

  if ! grep -Eq '"status"\s*:\s*"UP"' <<<"$body"; then
    echo "FAIL ${path} is not UP: ${body}" >&2
    return 1
  fi

  echo "PASS ${path} reports UP"
}

retry_check() {
  local label="$1"
  shift
  local attempt=1

  while true; do
    if "$@"; then
      return 0
    fi

    if (( attempt >= max_attempts )); then
      echo "FAIL ${label} after ${attempt} attempts" >&2
      return 1
    fi

    echo "Waiting for ${label} (${attempt}/${max_attempts})..."
    attempt=$((attempt + 1))
    sleep "$sleep_seconds"
  done
}

echo "Checking stack at ${base_url}"
retry_check "portal /healthz" assert_status_exact "/healthz" "200"
retry_check "portal /" assert_status_exact "/" "200"

# Wait for both upstream Spring Boot services to report healthy.
retry_check "train-order actuator" assert_actuator_up "/train-order/actuator/health"
retry_check "clearance-card actuator" assert_actuator_up "/clearance-card/actuator/health"

# Secondary check: app UI entry routes are reachable through the proxy.
retry_check "train-order ui" assert_status_exact "/train-order/orders" "200"
retry_check "clearance-card ui" assert_status_exact "/clearance-card/ui/clearance-cards" "200"

echo "Health checks passed."

