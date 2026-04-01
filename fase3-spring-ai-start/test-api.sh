#!/bin/bash

# Test script para verificar el chatbot Spring AI

echo "╔══════════════════════════════════════════════════════╗"
echo "║   Test - Chatbot Spring AI                        ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

BASE_URL="http://localhost:8080"

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

echo "╔══════════════════════════════════════════════════════╗"
echo "║   Tests completados                                  ║"
echo "╚══════════════════════════════════════════════════════╝"
