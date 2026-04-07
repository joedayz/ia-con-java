#!/usr/bin/env bash
set -euo pipefail
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd -- "$SCRIPT_DIR/.." && pwd)"
STACK_DIR="${ES_LOCAL_DIR:-$ROOT_DIR/elastic-start-local}"
ENV_FILE="$STACK_DIR/.env"
if [[ ! -f "$ENV_FILE" ]]; then
  echo "No se encontro $ENV_FILE"
  echo "Primero ejecuta: ./scripts/setup-elastic-local.sh"
  return 1 2>/dev/null || exit 1
fi
# shellcheck disable=SC1090
set -a
source "$ENV_FILE"
set +a
export ES_SERVER_URL="${ES_LOCAL_URL:-http://localhost:9200}"
export ES_USERNAME="elastic"
export ES_PASSWORD="${ES_LOCAL_PASSWORD:?ES_LOCAL_PASSWORD no encontrado en $ENV_FILE}"
echo "Variables cargadas para Spring Boot:"
echo "  ES_SERVER_URL=$ES_SERVER_URL"
echo "  ES_USERNAME=$ES_USERNAME"
echo "  ES_PASSWORD=<hidden>"
