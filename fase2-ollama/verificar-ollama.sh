#!/bin/bash

# Verifica que Ollama y los modelos esten listos para fase2-ollama.

set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

OLLAMA_URL="${OLLAMA_BASE_URL:-http://localhost:11434}"

echo "Verificando Ollama para fase2-ollama..."
echo ""

if command -v ollama > /dev/null 2>&1; then
    echo -e "${GREEN}OK${NC} ollama instalado"
else
    echo -e "${YELLOW}Aviso:${NC} comando ollama no esta en PATH"
fi

if curl -s -f -o /dev/null "$OLLAMA_URL/api/version"; then
    echo -e "${GREEN}OK${NC} servidor disponible en $OLLAMA_URL"
else
    echo -e "${RED}Error:${NC} servidor no disponible en $OLLAMA_URL"
    echo "Ejecuta: ollama serve"
    exit 1
fi

if command -v ollama > /dev/null 2>&1; then
    MODELOS=$(ollama list | tail -n +2 | wc -l | tr -d ' ')
    if [ "$MODELOS" -gt 0 ]; then
        echo -e "${GREEN}OK${NC} modelos instalados: $MODELOS"
        ollama list
    else
        echo -e "${RED}Error:${NC} no hay modelos instalados"
        echo "Ejecuta: ollama pull mistral"
        exit 1
    fi
fi

echo ""
echo -e "${GREEN}Listo.${NC} Puedes ejecutar:"
echo "  mvn -pl fase2-ollama exec:java"

