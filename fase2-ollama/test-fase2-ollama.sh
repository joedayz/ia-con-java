#!/bin/bash

# Script para ejecutar los labs de fase2-ollama
# Uso: ./test-fase2-ollama.sh [numero-lab]

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

if [[ "$(basename "$SCRIPT_DIR")" == "fase2-ollama" ]]; then
    cd "$PROJECT_ROOT"
fi

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Fase 2 Ollama - Prompt Engineering${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

OLLAMA_URL="${OLLAMA_BASE_URL:-http://localhost:11434}"

if ! curl -s -f -o /dev/null "$OLLAMA_URL/api/version"; then
    echo -e "${RED}Error: Ollama no responde en $OLLAMA_URL${NC}"
    echo "Inicia Ollama con: ollama serve"
    exit 1
fi

if ! command -v ollama > /dev/null 2>&1; then
    echo -e "${YELLOW}Aviso: comando 'ollama' no encontrado en PATH.${NC}"
    echo "Si ya tienes servidor remoto, puedes continuar con --model manual."
fi

if ! command -v mvn > /dev/null 2>&1; then
    echo -e "${RED}Error: Maven no esta instalado${NC}"
    exit 1
fi

seleccionar_modelo() {
    echo -e "${CYAN}Modelo (enter para default / OLLAMA_MODEL):${NC}" >&2
    read -r -p "Modelo: " model_choice
    echo "$model_choice"
}

ejecutar_lab() {
    local clase=$1
    local nombre=$2
    local model_choice

    echo -e "${YELLOW}Ejecutando: $nombre${NC}"
    model_choice=$(seleccionar_modelo)

    if [ -n "$model_choice" ]; then
        mvn -pl fase2-ollama exec:java \
            -Dexec.mainClass="com.joedayz.ia.fase2.ollama.$clase" \
            -Dexec.args="--model=$model_choice"
    else
        mvn -pl fase2-ollama exec:java \
            -Dexec.mainClass="com.joedayz.ia.fase2.ollama.$clase"
    fi
}

if [ $# -eq 1 ]; then
    case $1 in
        1) ejecutar_lab "PromptEngineering" "Lab 5: Chatbot Interactivo" ;;
        2) ejecutar_lab "ClasificadorSentimiento" "Lab 6: Clasificador Sentimientos" ;;
        3) ejecutar_lab "ChainOfThought" "Bonus: Chain of Thought" ;;
        4) ejecutar_lab "SalidaEstructurada" "Bonus: Salida Estructurada" ;;
        5) ejecutar_lab "ComparacionZeroShotVsFewShot" "Demo: Zero-Shot vs Few-Shot" ;;
        *)
            echo -e "${RED}Opcion invalida${NC}"
            exit 1
            ;;
    esac
    exit 0
fi

while true; do
    echo -e "${GREEN}Selecciona un lab:${NC}"
    echo "  1. Lab 5: Chatbot Interactivo"
    echo "  2. Lab 6: Clasificador Sentimientos"
    echo "  3. Bonus: Chain of Thought"
    echo "  4. Bonus: Salida Estructurada"
    echo "  5. Demo: Zero-Shot vs Few-Shot"
    echo "  0. Salir"
    echo ""

    read -r -p "Opcion: " opcion
    echo ""

    case $opcion in
        1) ejecutar_lab "PromptEngineering" "Lab 5: Chatbot Interactivo" ;;
        2) ejecutar_lab "ClasificadorSentimiento" "Lab 6: Clasificador Sentimientos" ;;
        3) ejecutar_lab "ChainOfThought" "Bonus: Chain of Thought" ;;
        4) ejecutar_lab "SalidaEstructurada" "Bonus: Salida Estructurada" ;;
        5) ejecutar_lab "ComparacionZeroShotVsFewShot" "Demo: Zero-Shot vs Few-Shot" ;;
        0) exit 0 ;;
        *) echo -e "${RED}Opcion invalida${NC}" ;;
    esac

    echo ""
    read -r -p "Presiona Enter para continuar..." _
    echo ""
done

