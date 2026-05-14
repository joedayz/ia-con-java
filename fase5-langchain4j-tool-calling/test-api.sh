#!/bin/bash
# =============================================================================
# Test Script: fase4-langchain4j-tool-calling
# Tool Calling con LangChain4j + Ollama
# Puerto: 8082
# =============================================================================

BASE_URL="http://localhost:8082"
ENDPOINT="${BASE_URL}/api/tool-calling/chat"

echo "╔══════════════════════════════════════════════════════╗"
echo "║   Test: Tool Calling con LangChain4j + Ollama       ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

# Verificar que la aplicación esté corriendo
echo "🔍 Verificando conexión a ${BASE_URL}..."
if ! curl -s "${BASE_URL}/actuator/health" > /dev/null 2>&1; then
    echo "❌ La aplicación no está corriendo en ${BASE_URL}"
    echo "   Ejecuta: ./mvnw -pl fase4-langchain4j-tool-calling spring-boot:run"
    exit 1
fi
echo "✅ Aplicación disponible"
echo ""

# Test 1: Lab 14 - Calculadora (multiplicación)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🧮 Test 1 - Lab 14: Calculadora (multiplicación)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: ¿Cuánto es 125 multiplicado por 37?"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuánto es 125 multiplicado por 37?"}' | python3 -m json.tool
echo ""

# Test 2: Lab 14 - Calculadora (raíz cuadrada)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🧮 Test 2 - Lab 14: Calculadora (raíz cuadrada)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: ¿Cuál es la raíz cuadrada de 144?"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es la raíz cuadrada de 144?"}' | python3 -m json.tool
echo ""

# Test 3: Lab 14 - Fecha actual
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📅 Test 3 - Lab 14: fechaActual()"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: ¿Qué fecha y hora es ahora?"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué fecha y hora es ahora?"}' | python3 -m json.tool
echo ""

# Test 4: Lab 14 - Operaciones encadenadas
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🧮 Test 4 - Lab 14: Operaciones encadenadas"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: Suma 100 + 250, luego multiplícalo por 3"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "Suma 100 + 250, luego multiplícalo por 3"}' | python3 -m json.tool
echo ""

# Test 5: Reto - consultarPais (API real)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🌍 Test 5 - Reto: consultarPais (API real - Colombia)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: Cuéntame sobre Colombia: capital, población e idiomas"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "Cuéntame sobre Colombia: capital, población e idiomas"}' | python3 -m json.tool
echo ""

# Test 6: Reto - consultarPais (otro país)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🌍 Test 6 - Reto: consultarPais (API real - España)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📤 Pregunta: ¿Qué idiomas se hablan en España y cuál es su población?"
echo ""
curl -s -X POST "${ENDPOINT}" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué idiomas se hablan en España y cuál es su población?"}' | python3 -m json.tool
echo ""

# Test 7: Sin herramientas (pregunta general)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "💬 Test 7 - Pregunta general (sin tools)"
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
