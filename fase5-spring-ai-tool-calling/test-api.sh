#!/bin/bash
# =============================================================================
# Test Script: fase4-spring-ai-tool-calling
# Tool Calling con Spring AI + Ollama
# Puerto: 8081
# =============================================================================

BASE_URL="http://localhost:8081"
ENDPOINT="${BASE_URL}/api/tool-calling/chat"

echo "╔══════════════════════════════════════════════════════╗"
echo "║   Test: Tool Calling con Spring AI + Ollama         ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

# Verificar que la aplicación esté corriendo
echo "🔍 Verificando conexión a ${BASE_URL}..."
if ! curl -s "${BASE_URL}/actuator/health" > /dev/null 2>&1; then
    echo "❌ La aplicación no está corriendo en ${BASE_URL}"
    echo "   Ejecuta: ./mvnw -pl fase4-spring-ai-tool-calling spring-boot:run"
    exit 1
fi
echo "✅ Aplicación disponible"
echo ""

# Test 1: Lab 13 - obtenerClima
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🌤️  Test 1 - Lab 13: obtenerClima (clima de Lima)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: ¿Cómo está el clima en Lima?"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cómo está el clima en Lima?"}' | python3 -m json.tool
echo ""

# Test 2: Lab 13 - obtenerClima otra ciudad
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🌤️  Test 2 - Lab 13: obtenerClima (clima de Madrid)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: ¿Qué temperatura hace en Madrid?"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué temperatura hace en Madrid?"}' | python3 -m json.tool
echo ""

# Test 3: Reto - consultarPais (API real)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🌍 Test 3 - Reto: consultarPais (API real - Japón)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: Cuéntame sobre Japón: capital, población e idiomas"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "Cuéntame sobre Japón: capital, población e idiomas"}' | python3 -m json.tool
echo ""

# Test 4: Reto - consultarPais (otro país)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🌍 Test 4 - Reto: consultarPais (API real - Perú)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: ¿Cuál es la capital de Perú y cuántos habitantes tiene?"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es la capital de Perú y cuántos habitantes tiene?"}' | python3 -m json.tool
echo ""

# Test 5: Sin herramientas (pregunta general)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "💬 Test 5 - Pregunta general (sin tools)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: ¿Qué es Tool Calling en IA?"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "Explica brevemente qué es Tool Calling en IA"}' | python3 -m json.tool
echo ""

echo "╔══════════════════════════════════════════════════════╗"
echo "║   ✅ Tests completados                              ║"
echo "║   📚 Swagger UI: ${BASE_URL}/swagger-ui.html       ║"
echo "╚══════════════════════════════════════════════════════╝"
