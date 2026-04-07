#!/usr/bin/env zsh

set -euo pipefail

API_KEYS_FILE="/Users/josediaz/.api-keys"

if [[ ! -f "$API_KEYS_FILE" ]]; then
  echo "No se encontro $API_KEYS_FILE"
  return 1 2>/dev/null || exit 1
fi

source "$API_KEYS_FILE"

echo "Entorno cargado: OPENAI_API_KEY"

