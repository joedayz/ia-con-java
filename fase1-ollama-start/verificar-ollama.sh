#!/bin/bash

# Script de prueba rápida para Ollama
# Verifica instalación y modelos disponibles

echo "=== Verificación de Ollama ==="
echo ""

# 1. Verificar si Ollama está instalado
echo "1. Verificando instalación..."
if command -v ollama &> /dev/null; then
    echo "   ✅ Ollama instalado: $(ollama --version)"
else
    echo "   ❌ Ollama NO instalado"
    echo ""
    echo "   💡 Instalar con:"
    echo "      macOS:   brew install ollama"
    echo "      Linux:   curl -fsSL https://ollama.com/install.sh | sh"
    echo "      Windows: https://ollama.com/download"
    exit 1
fi

echo ""

# 2. Verificar si está corriendo
echo "2. Verificando servidor..."
if curl -s http://localhost:11434/api/version &> /dev/null; then
    VERSION=$(curl -s http://localhost:11434/api/version | grep -o '"version":"[^"]*"' | cut -d'"' -f4)
    echo "   ✅ Servidor corriendo (versión: $VERSION)"
else
    echo "   ⚠️  Servidor NO está corriendo"
    echo ""
    echo "   💡 Iniciar con: ollama serve"
    exit 1
fi

echo ""

# 3. Listar modelos instalados
echo "3. Modelos instalados:"
MODELOS=$(ollama list 2>/dev/null | tail -n +2)
if [ -z "$MODELOS" ]; then
    echo "   ⚠️  No hay modelos instalados"
    echo ""
    echo "   💡 Descargar con:"
    echo "      ollama pull mistral    (4.1 GB - Recomendado)"
    echo "      ollama pull llama3.2   (2 GB - Ligero)"
    echo "      ollama pull phi3       (2.3 GB - Rápido)"
else
    echo "$MODELOS" | while read line; do
        echo "   ✅ $line"
    done
fi

echo ""

# 4. Prueba rápida si hay modelos
PRIMER_MODELO=$(ollama list 2>/dev/null | tail -n +2 | head -1 | awk '{print $1}')
if [ -n "$PRIMER_MODELO" ]; then
    echo "4. Probando modelo '$PRIMER_MODELO'..."
    echo "   Enviando: 'Di hola en una frase'"
    
    RESPUESTA=$(curl -s http://localhost:11434/v1/chat/completions \
        -H "Content-Type: application/json" \
        -d "{
            \"model\": \"$PRIMER_MODELO\",
            \"messages\": [{\"role\": \"user\", \"content\": \"Di hola en una frase\"}],
            \"stream\": false
        }" | grep -o '"content":"[^"]*"' | head -1 | cut -d'"' -f4)
    
    if [ -n "$RESPUESTA" ]; then
        echo "   ✅ Respuesta: $RESPUESTA"
    else
        echo "   ❌ No se pudo obtener respuesta"
    fi
else
    echo "4. Saltando prueba (no hay modelos instalados)"
fi

echo ""
echo "=== Verificación Completa ==="
echo ""
echo "📚 Para usar en el curso:"
echo "   cd fase1-ollama-start"
echo "   mvn clean compile exec:java"
