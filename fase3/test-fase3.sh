#!/bin/bash

# Script interactivo para probar los labs de la Fase 3
# Chatbots con Memoria

echo "╔══════════════════════════════════════════════════════════╗"
echo "║   Fase 3: Chatbots con Memoria - Menu Interactivo      ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""

# Verificar API key
if [ -z "$OPENAI_API_KEY" ]; then
    echo "⚠️  OPENAI_API_KEY no está configurada"
    echo ""
    echo "Opciones:"
    echo "1. Exportar: export OPENAI_API_KEY=sk-tu-clave"
    echo "2. O configurar en archivo .env en la raíz del proyecto"
    echo ""
    read -p "¿Continuar de todos modos? (s/n): " continuar
    if [[ ! $continuar =~ ^[Ss]$ ]]; then
        exit 1
    fi
fi

cd "$(dirname "$0")"

while true; do
    echo ""
    echo "═══════════════════════════════════════════════════════════"
    echo "  MENÚ DE DEMOS - FASE 3"
    echo "═══════════════════════════════════════════════════════════"
    echo ""
    echo "  DEMOS INTRODUCTORIAS:"
    echo "    1) ChatbotSinMemoria          - Problema: sin contexto"
    echo "    2) ComparacionMemoria         - Demo: con vs sin memoria"
    echo ""
    echo "  LABORATORIOS:"
    echo "    3) ChatbotConMemoria          - Lab 7: Buffer memory"
    echo "    4) ChatbotConMemoriaPersistente - Lab 8: Archivo JSON"
    echo ""
    echo "  MULTI-PROVEEDOR:"
    echo "    5) ChatbotMultiProveedor (OpenAI)"
    echo "    6) ChatbotMultiProveedor (Anthropic)"
    echo "    7) ChatbotMultiProveedor (Gemini)"
    echo ""
    echo "  RETO:"
    echo "    8) GestorMultiSesion          - Múltiples usuarios"
    echo ""
    echo "  UTILIDADES:"
    echo "    9) Limpiar sesiones guardadas"
    echo "    10) Listar sesiones guardadas"
    echo "    11) Ver estructura del proyecto"
    echo ""
    echo "    0) 🚪 Salir"
    echo ""
    echo "═══════════════════════════════════════════════════════════"
    read -p "Selecciona una opción: " opcion

    case $opcion in
        1)
            echo ""
            echo "🚀 Ejecutando: ChatbotSinMemoria..."
            echo "   (Demo del problema - cada pregunta es independiente)"
            echo ""
            mvn -q exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotSinMemoria"
            ;;
        2)
            echo ""
            echo "🚀 Ejecutando: ComparacionMemoria..."
            echo "   (Compara respuestas con y sin memoria)"
            echo ""
            mvn -q exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ComparacionMemoria"
            ;;
        3)
            echo ""
            echo "🚀 Ejecutando: ChatbotConMemoria..."
            echo "   (Lab 7 - Buffer memory con List<Mensaje>)"
            echo ""
            echo "💡 Tip: Prueba decir tu nombre y luego pregunta cuál es"
            echo ""
            mvn -q exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoria"
            ;;
        4)
            echo ""
            echo "🚀 Ejecutando: ChatbotConMemoriaPersistente..."
            echo "   (Lab 8 - Memoria guardada en archivo JSON)"
            echo ""
            echo "💡 Tip: Usa el mismo session ID para continuar conversaciones"
            echo ""
            mvn -q exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoriaPersistente"
            ;;
        5)
            echo ""
            echo "🚀 Ejecutando: ChatbotMultiProveedor (OpenAI)..."
            echo "   (Chatbot con memoria - OpenAI GPT-3.5-turbo)"
            echo ""
            mvn -q exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiProveedor"
            ;;
        6)
            echo ""
            echo "🚀 Ejecutando: ChatbotMultiProveedor (Anthropic)..."
            echo "   (Chatbot con memoria - Anthropic Claude)"
            echo ""
            if [ -z "$ANTHROPIC_API_KEY" ]; then
                echo "⚠️  ANTHROPIC_API_KEY no configurada"
                echo "   Configura: export ANTHROPIC_API_KEY=sk-ant-tu-clave"
            else
                mvn -q exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiProveedor" \
                    -Dexec.args="anthropic"
            fi
        10)
            echo ""
            echo "📂 Sesiones guardadas en: sessions/"
            echo "─────────────────────────────────────────────"
            if [ -d "sessions" ] && [ "$(ls -A sessions)" ]; then
                ls -lh sessions/*.json 2>/dev/null | awk '{print "  📁 "$9" ("$5")"}'
                echo ""
                echo "Total: $(ls sessions/*.json 2>/dev/null | wc -l) sesión(es)"
            else
                echo "  (No hay sesiones guardadas)"
            fi
            echo "─────────────────────────────────────────────"
            ;;
        11   ;;
        8)
            echo ""
            echo "🚀 Ejecutando: GestorMultiSesion..."
            echo "   (Reto - Gestión de múltiples usuarios)"
            echo ""
            echo "💡 Comandos: /nueva [id], /listar, /info, /borrar [id]"
            echo ""
            mvn -q exec:java -Dexec.mainClass="com.joedayz.ia.fase3.GestorMultiSesion"
            ;;
        9)
            echo ""
            read -p "⚠️  ¿Borrar TODAS las sesiones guardadas? (s/n): " confirmar
            if [[ $confirmar =~ ^[Ss]$ ]]; then
                rm -rf sessions/*.json 2>/dev/null
                echo "🗑️  Sesiones eliminadas"
            else
                echo "❌ Cancelado"
            fi
            ;;
        7)
            echo ""
            echo "📂 Sesiones guardadas en: sessions/"
            echo "─────────────────────────────────────────────"
            if [ -d "sessions" ] && [ "$(ls -A sessions)" ]; then
                ls -lh sessions/*.json 2>/dev/null | awk '{print "  📁 "$9" ("$5")"}'
                echo ""
                echo "Total: $(ls sessions/*.json 2>/dev/null | wc -l) sesión(es)"
            else
                echo "  (No hay sesiones guardadas)"
            fi
            echo "─────────────────────────────────────────────"
            ;;
        8)
            echo ""
            echo "📁 Estructura del proyecto Fase 3:"
            echo "─────────────────────────────────────────────"
            tree -L 3 -I 'target|.git' . 2>/dev/null || find . -maxdepth 3 -name '*.java' -o -name '*.md' | sort
            echo "─────────────────────────────────────────────"
            ;;
        0)
            echo ""
            echo "👋 ¡Hasta luego!"
            exit 0
            ;;
        *)
            echo ""
            echo "❌ Opción inválida. Intenta de nuevo."
            ;;
    esac

    echo ""
    read -p "Presiona ENTER para continuar..."
done
