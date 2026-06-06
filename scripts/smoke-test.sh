#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
base_url="${1:-http://localhost:${PORTAL_PORT:-8080}}"

if ! command -v curl >/dev/null 2>&1; then
  echo "curl is required" >&2
  exit 1
fi

require_content() {
  local path="$1"
  local expected="$2"
  local body
  body="$(curl -fsS "${base_url}${path}")"
  if [[ "$body" != *"$expected"* ]]; then
    echo "FAIL ${path} is missing expected text: ${expected}" >&2
    exit 1
  fi
  echo "PASS ${path} contains '${expected}'"
}

# Reuse endpoint availability checks first.
"$repo_root/scripts/health-check.sh" "$base_url"

echo "Running smoke checks at ${base_url}"
require_content "/" "Train Order Operator"
require_content "/" "/train-order/orders"
require_content "/" "/clearance-card/ui/clearance-cards"

echo "Smoke tests passed."

