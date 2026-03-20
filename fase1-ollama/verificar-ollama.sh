#!/bin/bash

# Script para verificar que Ollama está correctamente configurado

echo "🔍 Verificando Ollama..."
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

OLLAMA_URL="${OLLAMA_BASE_URL:-http://localhost:11434}"

# 1. Verificar si Ollama está instalado
echo "1️⃣  Verificando instalación de Ollama..."
if command -v ollama &> /dev/null; then
    echo -e "${GREEN}✅ Ollama está instalado${NC}"
    ollama --version
else
    echo -e "${RED}❌ Ollama NO está instalado${NC}"
    echo ""
    echo "💡 Solución:"
    echo "   macOS:   brew install ollama"
    echo "   Linux:   curl -fsSL https://ollama.com/install.sh | sh"
    echo "   Windows: https://ollama.com/download"
    exit 1
fi

echo ""

# 2. Verificar si el servidor está corriendo
echo "2️⃣  Verificando servidor Ollama en $OLLAMA_URL..."
if curl -s -f -o /dev/null "$OLLAMA_URL/api/version"; then
    echo -e "${GREEN}✅ Servidor Ollama está corriendo${NC}"
    VERSION=$(curl -s "$OLLAMA_URL/api/version" | grep -o '"version":"[^"]*"' | cut -d'"' -f4)
    echo "   Versión: $VERSION"
else
    echo -e "${RED}❌ Servidor Ollama NO está corriendo${NC}"
    echo ""
    echo "💡 Solución: Ejecuta en otra terminal:"
    echo "   ollama serve"
    exit 1
fi

echo ""

# 3. Verificar modelos instalados
echo "3️⃣  Verificando modelos instalados..."
MODELOS=$(ollama list | tail -n +2 | wc -l | tr -d ' ')

if [ "$MODELOS" -gt 0 ]; then
    echo -e "${GREEN}✅ Hay $MODELOS modelo(s) instalado(s)${NC}"
    echo ""
    ollama list
else
    echo -e "${YELLOW}⚠️  No hay modelos instalados${NC}"
    echo ""
    echo "💡 Solución: Instala al menos un modelo:"
    echo "   ollama pull mistral      # 4.1 GB - Recomendado"
    echo "   ollama pull llama3.2     # 2 GB - Más rápido"
    echo "   ollama pull phi3         # 2.3 GB - Para laptops modestas"
    exit 1
fi

echo ""

# 4. Verificar API de chat
echo "4️⃣  Verificando API de chat..."
RESPONSE=$(curl -s -X POST "$OLLAMA_URL/v1/chat/completions" \
  -H "Content-Type: application/json" \
  -d '{"model":"'$(ollama list | tail -n +2 | head -1 | awk '{print $1}')'","messages":[{"role":"user","content":"Di solo: OK"}],"stream":false}' \
  --max-time 30)

if echo "$RESPONSE" | grep -q "content"; then
    echo -e "${GREEN}✅ API de chat funciona correctamente${NC}"
    echo ""
    CONTENT=$(echo "$RESPONSE" | grep -o '"content":"[^"]*"' | head -1 | cut -d'"' -f4)
    echo "   Respuesta de prueba: $CONTENT"
else
    echo -e "${RED}❌ API de chat NO funciona correctamente${NC}"
    echo "   Response: $RESPONSE"
    exit 1
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${GREEN}🎉 ¡Todo está configurado correctamente!${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "Puedes ejecutar el código con:"
echo "   mvn clean compile exec:java"
echo ""
echo "Modelos disponibles para usar:"
ollama list | tail -n +2 | awk '{print "   - " $1}'
echo ""
echo "Ejemplo con modelo específico:"
PRIMER_MODELO=$(ollama list | tail -n +2 | head -1 | awk '{print $1}')
echo "   mvn exec:java -Dexec.args=\"--model $PRIMER_MODELO Explica qué es Java\""
echo ""
