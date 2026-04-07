#!/bin/bash

# Test script para verificar el chatbot Spring AI (SOLUCIÓN)

echo "╔══════════════════════════════════════════════════════╗"
echo "║   Test - Chatbot Spring AI (SOLUCIÓN)             ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

BASE_URL="http://localhost:8080"

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

# Función para imprimir headers de sección
print_section() {
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo -e "${BLUE}$1${NC}"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
}

# Verificar que el servidor está corriendo
echo "🔍 Verificando que el servidor está activo..."
if ! curl -s "$BASE_URL/actuator/health" > /dev/null 2>&1; then
    echo -e "${RED}✗${NC} El servidor no está corriendo en $BASE_URL"
    echo "   Inicia la aplicación con: mvn spring-boot:run"
    exit 1
fi
echo -e "${GREEN}✓${NC} Servidor activo"

# Test status endpoint
response=$(curl -s "$BASE_URL/api/chat/status")
echo -e "${GREEN}✓${NC} Status endpoint: $response"

# Test 1: Chat simple (Lab 7)
print_section "📝 Test 1: Chat simple - Lab 7 (sesión única)"

response=$(curl -s -X POST "$BASE_URL/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Carlos y me gustan los videojuegos"}')

echo "👤 Usuario: Hola, me llamo Carlos y me gustan los videojuegos"
echo "🤖 Bot: $(echo "$response" | python3 -c "import sys, json; print(json.load(sys.stdin)['response'])" 2>/dev/null || echo "$response")"
print_test "Enviar primer mensaje" $?
echo ""

# Test 2: Verificar memoria (Lab 7)
response=$(curl -s -X POST "$BASE_URL/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre y qué me gusta?"}')

echo "👤 Usuario: ¿Cuál es mi nombre y qué me gusta?"
bot_response=$(echo "$response" | python3 -c "import sys, json; print(json.load(sys.stdin)['response'])" 2>/dev/null || echo "$response")
echo "🤖 Bot: $bot_response"

if echo "$bot_response" | grep -qi "carlos"; then
    print_test "El bot recuerda el nombre (Carlos)" 0
else
    print_test "El bot recuerda el nombre (Carlos)" 1
fi

if echo "$bot_response" | grep -qi "videojuego"; then
    print_test "El bot recuerda los gustos (videojuegos)" 0
else
    print_test "El bot recuerda los gustos (videojuegos)" 1
fi

# Test 3: Multi-sesión (RETO)
print_section "📝 Test 2: Multi-sesión (RETO) - Múltiples usuarios"

# Usuario 1
response1=$(curl -s -X POST "$BASE_URL/api/chat/user-123" \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Carlos"}')

echo "👤 [user-123] Usuario: Hola, me llamo Carlos"
bot1=$(echo "$response1" | python3 -c "import sys, json; print(json.load(sys.stdin)['response'])" 2>/dev/null || echo "$response1")
echo "🤖 [user-123] Bot: $bot1"
print_test "Sesión user-123 creada" $?
echo ""

# Usuario 2
response2=$(curl -s -X POST "$BASE_URL/api/chat/user-456" \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Ana"}')

echo "👤 [user-456] Usuario: Hola, me llamo Ana"
bot2=$(echo "$response2" | python3 -c "import sys, json; print(json.load(sys.stdin)['response'])" 2>/dev/null || echo "$response2")
echo "🤖 [user-456] Bot: $bot2"
print_test "Sesión user-456 creada" $?
echo ""

# Verificar contexto usuario 1
response1=$(curl -s -X POST "$BASE_URL/api/chat/user-123" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}')

echo "👤 [user-123] Usuario: ¿Cuál es mi nombre?"
bot1=$(echo "$response1" | python3 -c "import sys, json; print(json.load(sys.stdin)['response'])" 2>/dev/null || echo "$response1")
echo "🤖 [user-123] Bot: $bot1"

if echo "$bot1" | grep -qi "carlos"; then
    print_test "Sesión user-123 recuerda 'Carlos'" 0
else
    print_test "Sesión user-123 recuerda 'Carlos'" 1
fi
echo ""

# Verificar contexto usuario 2
response2=$(curl -s -X POST "$BASE_URL/api/chat/user-456" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}')

echo "👤 [user-456] Usuario: ¿Cuál es mi nombre?"
bot2=$(echo "$response2" | python3 -c "import sys, json; print(json.load(sys.stdin)['response'])" 2>/dev/null || echo "$response2")
echo "🤖 [user-456] Bot: $bot2"

if echo "$bot2" | grep -qi "ana"; then
    print_test "Sesión user-456 recuerda 'Ana'" 0
else
    print_test "Sesión user-456 recuerda 'Ana'" 1
fi

# Test 4: Limpiar sesión
print_section "📝 Test 3: Limpiar sesión"

curl -s -X DELETE "$BASE_URL/api/chat/user-123" > /dev/null
print_test "Limpieza de sesión user-123" $?
echo ""

# Verificar que se limpió
response=$(curl -s -X POST "$BASE_URL/api/chat/user-123" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}')

echo "👤 [user-123] Usuario: ¿Cuál es mi nombre? (después de limpiar)"
bot_clean=$(echo "$response" | python3 -c "import sys, json; print(json.load(sys.stdin)['response'])" 2>/dev/null || echo "$response")
echo "🤖 [user-123] Bot: $bot_clean"

if echo "$bot_clean" | grep -qi "carlos"; then
    print_test "Memoria limpiada correctamente" 1
    echo "   ⚠️  La sesión todavía recuerda 'Carlos' (no se limpió)"
else
    print_test "Memoria limpiada correctamente" 0
fi

# Test 5: Búsqueda semántica (Lab 9/10)
print_section "📝 Test 4: Búsqueda semántica (SimpleVectorStore)"

seed=$(curl -s -X POST "$BASE_URL/api/buscar/demo")
echo "📚 Seed demo: $seed"
print_test "Indexar documentos teóricos" $?

search=$(curl -s "$BASE_URL/api/buscar?query=similitud%20coseno&topK=3")
echo "🔎 Buscar 'similitud coseno': $search"

if echo "$search" | grep -qi "coseno"; then
    print_test "La búsqueda devuelve contenido semánticamente relevante" 0
else
    print_test "La búsqueda devuelve contenido semánticamente relevante" 1
fi

# Test 6: RAG básico
print_section "📝 Test 5: RAG básico (retrieval + generation)"

rag=$(curl -s -X POST "$BASE_URL/api/rag" \
  -H "Content-Type: application/json" \
  -d '{"query":"Explica la relacion entre embeddings y similitud coseno","topK":4}')
echo "🧠 RAG: $rag"

if echo "$rag" | grep -qi '"answer"'; then
    print_test "El endpoint /api/rag responde con answer" 0
else
    print_test "El endpoint /api/rag responde con answer" 1
fi

# Test 7: Lab 11 - RAG Simple (contexto en el prompt)
print_section "📝 Test 6: Lab 11 - RAG Simple (/api/rag/simple)"

rag_simple=$(curl -s -X POST "$BASE_URL/api/rag/simple" \
  -H "Content-Type: application/json" \
  -d '{"query":"¿Qué es SimpleVectorStore y para qué sirve?","topK":3}')
echo "🧠 RAG Simple: $rag_simple"

if echo "$rag_simple" | grep -qi '"answer"'; then
    print_test "Lab 11: /api/rag/simple genera respuesta con citas" 0
else
    print_test "Lab 11: /api/rag/simple genera respuesta con citas" 1
fi

if echo "$rag_simple" | grep -qi '"citations"'; then
    print_test "Lab 11: La respuesta incluye citations" 0
else
    print_test "Lab 11: La respuesta incluye citations" 1
fi

# Test 8: Lab 12 - RAG con QuestionAnswerAdvisor
print_section "📝 Test 7: Lab 12 - RAG con QuestionAnswerAdvisor (/api/rag/advisor)"

rag_advisor=$(curl -s -X POST "$BASE_URL/api/rag/advisor" \
  -H "Content-Type: application/json" \
  -d '{"query":"¿Cómo funcionan los embeddings?","topK":4}')
echo "🧠 RAG Advisor: $rag_advisor"

if echo "$rag_advisor" | grep -qi '"answer"'; then
    print_test "Lab 12: /api/rag/advisor genera respuesta con QuestionAnswerAdvisor" 0
else
    print_test "Lab 12: /api/rag/advisor genera respuesta con QuestionAnswerAdvisor" 1
fi

# Test 9: Reto - Asistente de documentación con Markdown
print_section "📝 Test 8: Reto - Asistente de documentación Markdown"

DOCS_DIR="./data/docs"
if [ -d "$DOCS_DIR" ]; then
  # Cargar documentos MD
  docs_response=$(curl -s -X POST "$BASE_URL/api/rag/docs/cargar" \
    -H "Content-Type: application/json" \
    -d "{\"path\":\"$DOCS_DIR\"}")
  echo "📚 Carga Markdown: $docs_response"
  print_test "Reto: Cargar archivos .md del directorio" $?

  # Preguntar sobre la documentación
  docs_pregunta=$(curl -s -X POST "$BASE_URL/api/rag/docs/preguntar" \
    -H "Content-Type: application/json" \
    -d '{"query":"¿Cuál es la diferencia entre RAG Simple y QuestionAnswerAdvisor?","topK":5}')
  echo "🧠 Pregunta docs: $docs_pregunta"

  if echo "$docs_pregunta" | grep -qi '"answer"'; then
      print_test "Reto: El asistente de docs genera respuesta" 0
  else
      print_test "Reto: El asistente de docs genera respuesta" 1
  fi

  # Segunda pregunta
  docs_pregunta2=$(curl -s -X POST "$BASE_URL/api/rag/docs/preguntar" \
    -H "Content-Type: application/json" \
    -d '{"query":"¿Qué proveedores de IA soporta Spring AI?","topK":4}')
  echo "🧠 Pregunta docs 2: $docs_pregunta2"
  print_test "Reto: Segunda pregunta sobre los docs" $?
else
  echo -e "${YELLOW}⚠${NC} Directorio $DOCS_DIR no encontrado (se omite test)"
fi

# Test 10: Reto PDF (opcional)
print_section "📝 Test 9: Reto PDF con Tika (opcional)"

PDF_PATH="/Users/josediaz/Projects/JoeDayz/ia-con-java/docs/01-AI_Developer_Blueprint.pdf"
if [ -f "$PDF_PATH" ]; then
  pdf_response=$(curl -s -X POST "$BASE_URL/api/buscar/pdf" \
    -H "Content-Type: application/json" \
    -d "{\"path\":\"$PDF_PATH\",\"sourceId\":\"ai-blueprint\"}")
  echo "📄 Carga PDF: $pdf_response"
  print_test "Indexar PDF con TikaDocumentReader" $?
else
  echo -e "${YELLOW}⚠${NC} PDF no encontrado en $PDF_PATH (se omite test opcional)"
fi

# Resumen final
print_section "✅ Tests completados"

echo ""
echo "💡 Comandos útiles:"
echo "   Ver H2 Console: http://localhost:8080/h2-console"
echo "   Ver logs: tail -f nohup.out"
echo "   Status: curl $BASE_URL/api/chat/status"
echo ""
