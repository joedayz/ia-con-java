#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd -- "$SCRIPT_DIR/.." && pwd)"
STACK_DIR="${ES_LOCAL_DIR:-$ROOT_DIR/elastic-start-local}"

runtime_usable() {
  local runtime="$1"
  command -v "$runtime" >/dev/null 2>&1 && "$runtime" info >/dev/null 2>&1
}

detect_runtime() {
  if [[ -n "${ES_CONTAINER_RUNTIME:-}" ]]; then
    case "$ES_CONTAINER_RUNTIME" in
      docker|podman)
        if runtime_usable "$ES_CONTAINER_RUNTIME"; then
          echo "$ES_CONTAINER_RUNTIME"
          return 0
        fi
        echo "ES_CONTAINER_RUNTIME=$ES_CONTAINER_RUNTIME no esta disponible o no responde." >&2
        return 1
        ;;
      *)
        echo "ES_CONTAINER_RUNTIME invalido: $ES_CONTAINER_RUNTIME (usa docker o podman)" >&2
        return 1
        ;;
    esac
  fi

  if runtime_usable docker; then
    echo "docker"
    return 0
  fi

  if runtime_usable podman; then
    echo "podman"
    return 0
  fi

  echo ""
}

if [[ -f "$STACK_DIR/.env" ]]; then
  echo "start-local ya existe en: $STACK_DIR"
  echo "Si necesitas reinstalar, elimina esa carpeta y vuelve a ejecutar este script."
  exit 0
fi

echo "Instalando Elasticsearch y Kibana localmente con start-local..."
RUNTIME="$(detect_runtime)"

if [[ -z "$RUNTIME" ]]; then
  echo "No se detecto Docker ni Podman operativos en esta maquina." >&2
  echo "Instala y levanta Docker Desktop o Podman, luego reintenta." >&2
  exit 1
fi

START_LOCAL_URL="https://elastic.co/start-local"
if [[ "$RUNTIME" == "podman" ]]; then
  START_LOCAL_URL="https://elastic.co/start-local-podman"
fi

echo "Runtime detectado: $RUNTIME"
echo "Usando instalador: $START_LOCAL_URL"
ES_LOCAL_DIR="$STACK_DIR" curl -fsSL "$START_LOCAL_URL" | sh

echo
echo "Instalacion completada."
echo "Carpeta: $STACK_DIR"
echo "Siguiente paso: source ./scripts/use-elastic-local-env.sh"

