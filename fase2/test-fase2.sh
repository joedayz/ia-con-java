#!/bin/bash

# Script para ejecutar los labs de Fase 2 - Prompt Engineering
# Uso: ./test-fase2.sh [numero-lab]

set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Fase 2 - Prompt Engineering${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Verificar si existe el archivo .env
if [ ! -f ".env" ]; then
    echo -e "${RED}❌ Error: Archivo .env no encontrado${NC}"
    echo ""
    echo "Crea un archivo .env en la raíz del proyecto con:"
    echo "OPENAI_API_KEY=tu-api-key-aqui"
    echo "OPENAI_API_BASE=https://api.openai.com/v1"
    echo "# O alternativamente:"
    echo "# ANTHROPIC_API_KEY=tu-api-key-aqui"
    echo "# GEMINI_API_KEY=tu-api-key-aqui"
    exit 1
fi

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}❌ Error: Maven no está instalado${NC}"
    exit 1
fi

# Función para seleccionar proveedor
seleccionar_proveedor() {
    echo -e "${CYAN}Selecciona el proveedor de IA:${NC}"
    echo ""
    echo "  1. OpenAI (GPT-3.5)"
    echo "  2. Anthropic (Claude 3 Haiku)"
    echo "  3. Google Gemini 2.5 Flash"
    echo "  4. Usar configuración por defecto (.env)"
    echo ""
    read -p "Proveedor: " prov_opcion
    echo ""
    
    case $prov_opcion in
        1)
            echo "openai"
            ;;
        2)
            echo "anthropic"
            ;;
        3)
            echo "gemini"
            ;;
        4|"")
            echo ""
            ;;
        *)
            echo -e "${RED}❌ Opción inválida, usando configuración por defecto${NC}"
            echo ""
            ;;
    esac
}

# Función para mostrar el menú
mostrar_menu() {
    echo -e "${GREEN}Selecciona un lab:${NC}"
    echo ""
    echo "  1. Lab 5: Chatbot Interactivo (PromptEngineering)"
    echo "  2. Lab 6: Clasificador de Sentimientos (Few-Shot)"
    echo "  3. Bonus: Chain of Thought (Razonamiento paso a paso)"
    echo "  4. Bonus: Salida Estructurada (JSON)"
    echo "  5. Demo: Comparación Zero-Shot vs Few-Shot"
    echo ""
    echo "  0. Salir"
    echo ""
}

# Función para ejecutar un lab
ejecutar_lab() {
    local lab=$1
    local nombre=$2
    local clase=$3
    
    echo -e "${YELLOW}▶ Ejecutando: $nombre${NC}"
    echo ""
    
    # Seleccionar proveedor
    local provider=$(seleccionar_proveedor)
    
    if [ -n "$provider" ]; then
        echo -e "${BLUE}Usando proveedor: $provider${NC}"
        echo ""
        mvn -pl fase2 exec:java -Dexec.mainClass="com.joedayz.ia.fase2.$clase" \
            -Dexec.args="--provider=$provider" -q
    else
        echo -e "${BLUE}Usando proveedor configurado en .env${NC}"
        echo ""
        mvn -pl fase2 exec:java -Dexec.mainClass="com.joedayz.ia.fase2.$clase" -q
    fi
    
    echo ""
    echo -e "${GREEN}✓ Lab completado${NC}"
    echo ""
}

# Si se pasa un argumento, ejecutar directamente
if [ $# -eq 1 ]; then
    case $1 in
        1)
            ejecutar_lab "Lab 5" "Chatbot Interactivo" "PromptEngineering"
            ;;
        2)
            ejecutar_lab "Lab 6" "Clasificador Sentimientos" "ClasificadorSentimiento"
            ;;
        3)
            ejecutar_lab "Bonus" "Chain of Thought" "ChainOfThought"
            ;;
        4)
            ejecutar_lab "Bonus" "Salida Estructurada" "SalidaEstructurada"
            ;;
        5)
            ejecutar_lab "Demo" "Zero-Shot vs Few-Shot" "ComparacionZeroShotVsFewShot"
            ;;
        *)
            echo -e "${RED}❌ Opción inválida${NC}"
            exit 1
            ;;
    esac
    exit 0
fi

# Si no hay argumentos, mostrar menú interactivo
while true; do
    mostrar_menu
    read -p "Opción: " opcion
    echo ""
    
    case $opcion in
        1)
            ejecutar_lab "Lab 5" "Chatbot Interactivo" "PromptEngineering"
            ;;
        2)
            ejecutar_lab "Lab 6" "Clasificador Sentimientos" "ClasificadorSentimiento"
            ;;
        3)
            ejecutar_lab "Bonus" "Chain of Thought" "ChainOfThought"
            ;;
        4)
            ejecutar_lab "Bonus" "Salida Estructurada" "SalidaEstructurada"
            ;;
        5)
            ejecutar_lab "Demo" "Zero-Shot vs Few-Shot" "ComparacionZeroShotVsFewShot"
            ;;
        0)
            echo -e "${GREEN}👋 ¡Hasta pronto!${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}❌ Opción inválida${NC}"
            echo ""
            ;;
    esac
    
    read -p "Presiona Enter para continuar..."
    echo ""
done
