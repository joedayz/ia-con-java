#!/bin/bash

# Test script para verificar el chatbot Spring AI

echo "╔══════════════════════════════════════════════════════╗"
echo "║   Test - Chatbot Spring AI                        ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

BASE_URL="http://localhost:8080"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
DOCS_PATH="$SCRIPT_DIR/data/docs"

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para imprimir resultados
print_test() {
    local test_name=$1
    local status=$2
    if [ "$status" -eq 0 ]; then
        echo -e "${GREEN}✓${NC} $test_name"
    else
        echo -e "${RED}✗${NC} $test_name"
    fi
}

# Verificar que el servidor está corriendo
echo "🔍 Verificando que el servidor está activo..."
if ! curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1; then
    echo -e "${RED}✗${NC} El servidor no está corriendo en $BASE_URL"
    echo "   Inicia la aplicación con: mvn spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✓${NC} Servidor activo"
echo ""

# Test 1: Chat simple (Lab 7)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📝 Test 1: Chat simple (sesión única)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

response=$(curl -s -X POST "$BASE_URL/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Carlos y me gustan los videojuegos"}')

echo "Usuario: Hola, me llamo Carlos y me gustan los videojuegos"
echo "Bot: $response"
print_test "Enviar primer mensaje" $?
echo ""

# Test 2: Verificar memoria
response=$(curl -s -X POST "$BASE_URL/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre y qué me gusta?"}')

echo "Usuario: ¿Cuál es mi nombre y qué me gusta?"
echo "Bot: $response"

if echo "$response" | grep -qi "carlos"; then
    print_test "El bot recuerda el nombre (Carlos)" 0
else
    print_test "El bot recuerda el nombre (Carlos)" 1
fi

if echo "$response" | grep -qi "videojuego"; then
    print_test "El bot recuerda los gustos (videojuegos)" 0
else
    print_test "El bot recuerda los gustos (videojuegos)" 1
fi
echo ""

# Test 3: Multi-sesión (RETO)
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📝 Test 2: Multi-sesión (múltiples usuarios)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Usuario 1
response1=$(curl -s -X POST "$BASE_URL/api/chat/user-123" \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Carlos"}')

echo "[user-123] Usuario: Hola, me llamo Carlos"
echo "[user-123] Bot: $response1"
print_test "Sesión user-123 creada" $?
echo ""

# Usuario 2
response2=$(curl -s -X POST "$BASE_URL/api/chat/user-456" \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Ana"}')

echo "[user-456] Usuario: Hola, me llamo Ana"
echo "[user-456] Bot: $response2"
print_test "Sesión user-456 creada" $?
echo ""

# Verificar contexto usuario 1
response1=$(curl -s -X POST "$BASE_URL/api/chat/user-123" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}')

echo "[user-123] Usuario: ¿Cuál es mi nombre?"
echo "[user-123] Bot: $response1"

if echo "$response1" | grep -qi "carlos"; then
    print_test "Sesión user-123 recuerda 'Carlos'" 0
else
    print_test "Sesión user-123 recuerda 'Carlos'" 1
fi
echo ""

# Verificar contexto usuario 2
response2=$(curl -s -X POST "$BASE_URL/api/chat/user-456" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}')

echo "[user-456] Usuario: ¿Cuál es mi nombre?"
echo "[user-456] Bot: $response2"

if echo "$response2" | grep -qi "ana"; then
    print_test "Sesión user-456 recuerda 'Ana'" 0
else
    print_test "Sesión user-456 recuerda 'Ana'" 1
fi
echo ""

# Test 4: Limpiar sesión
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📝 Test 3: Limpiar sesión"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

curl -s -X DELETE "$BASE_URL/api/chat/user-123" > /dev/null
print_test "Limpieza de sesión user-123" $?
echo ""

# Verificar que se limpió
response=$(curl -s -X POST "$BASE_URL/api/chat/user-123" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}')

echo "[user-123] Usuario: ¿Cuál es mi nombre? (después de limpiar)"
echo "[user-123] Bot: $response"

if echo "$response" | grep -qi "carlos"; then
    print_test "Memoria limpiada correctamente" 1
else
    print_test "Memoria limpiada correctamente" 0
fi
echo ""

# Test 5: Búsqueda semántica
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🔍 Test 4: Búsqueda semántica"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

response=$(curl -s -X POST "$BASE_URL/api/buscar/demo")
echo "POST /api/buscar/demo: $response"
if echo "$response" | grep -q '"documentos"'; then
    print_test "Carga de documentos demo" 0
else
    print_test "Carga de documentos demo" 1
fi

response=$(curl -s "$BASE_URL/api/buscar?query=similitud%20coseno&topK=3")
echo "GET /api/buscar?query=similitud coseno: $response"
if echo "$response" | grep -q '"resultados"'; then
    print_test "Búsqueda semántica devuelve resultados" 0
else
    print_test "Búsqueda semántica devuelve resultados" 1
fi
echo ""

# Test 6: RAG
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🧠 Test 5: RAG (simple, advisor y docs)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

response=$(curl -s -X POST "$BASE_URL/api/rag/simple" \
  -H "Content-Type: application/json" \
  -d '{"query":"que son los embeddings","topK":3}')
echo "POST /api/rag/simple: $response"
if echo "$response" | grep -q '"answer"' && echo "$response" | grep -q '"citations"'; then
    print_test "RAG simple responde con citas" 0
else
    print_test "RAG simple responde con citas" 1
fi

response=$(curl -s -X POST "$BASE_URL/api/rag/advisor" \
  -H "Content-Type: application/json" \
  -d '{"query":"que es similitud coseno","topK":3}')
echo "POST /api/rag/advisor: $response"
if echo "$response" | grep -q '"answer"' && echo "$response" | grep -q '"citations"'; then
    print_test "RAG advisor responde con citas" 0
else
    print_test "RAG advisor responde con citas" 1
fi

response=$(curl -s -X POST "$BASE_URL/api/rag/docs/cargar" \
  -H "Content-Type: application/json" \
  -d "{\"path\":\"$DOCS_PATH\"}")
echo "POST /api/rag/docs/cargar: $response"
if echo "$response" | grep -q '"totalChunks"'; then
    print_test "Carga de docs Markdown" 0
else
    print_test "Carga de docs Markdown" 1
fi

response=$(curl -s -X POST "$BASE_URL/api/rag/docs/preguntar" \
  -H "Content-Type: application/json" \
  -d '{"query":"diferencia entre rag simple y advisor","topK":4}')
echo "POST /api/rag/docs/preguntar: $response"
if echo "$response" | grep -q '"answer"' && echo "$response" | grep -q '"citations"'; then
    print_test "Asistente de docs responde" 0
else
    print_test "Asistente de docs responde" 1
fi
echo ""

echo "╔══════════════════════════════════════════════════════╗"
echo "║   Tests completados                                  ║"
echo "╚══════════════════════════════════════════════════════╝"
