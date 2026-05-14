#!/bin/bash
# Test script para Fase 4 - Spring AI + OpenAI + PgVector
# Prerequisitos: Docker (PostgreSQL+pgvector en puerto 5433), OPENAI_API_KEY

BASE_URL="http://localhost:8080"

echo "============================================"
echo "  Fase 4: Spring AI + OpenAI + PgVector"
echo "============================================"

echo ""
echo "--- 1. Estado del chat ---"
curl -s "$BASE_URL/api/chat/status" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/chat/status"

echo ""
echo "--- 2. Estado del vector store (PgVector) ---"
curl -s "$BASE_URL/api/buscar/status" | python3 -m json.tool 2>/dev/null || curl -s "$BASE_URL/api/buscar/status"

echo ""
echo "--- 3. Chat simple ---"
curl -s -X POST "$BASE_URL/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué es PgVector?"}' | python3 -m json.tool 2>/dev/null

echo ""
echo "--- 4. Cargar documentos demo en PgVector ---"
curl -s -X POST "$BASE_URL/api/buscar/demo" | python3 -m json.tool 2>/dev/null

echo ""
echo "--- 5. Búsqueda semántica en PgVector ---"
curl -s "$BASE_URL/api/buscar?query=embeddings%20vectores&topK=3" | python3 -m json.tool 2>/dev/null

echo ""
echo "--- 6. RAG simple ---"
curl -s -X POST "$BASE_URL/api/rag/simple" \
  -H "Content-Type: application/json" \
  -d '{"query": "¿Qué son los embeddings?", "topK": 3}' | python3 -m json.tool 2>/dev/null

echo ""
echo "--- 7. RAG con QuestionAnswerAdvisor ---"
curl -s -X POST "$BASE_URL/api/rag/advisor" \
  -H "Content-Type: application/json" \
  -d '{"query": "¿Cómo funciona la búsqueda por similitud?", "topK": 4}' | python3 -m json.tool 2>/dev/null

echo ""
echo "--- 8. Cargar documentos Markdown ---"
curl -s -X POST "$BASE_URL/api/rag/docs/cargar" \
  -H "Content-Type: application/json" \
  -d '{"path": "./data/docs"}' | python3 -m json.tool 2>/dev/null

echo ""
echo "--- 9. Preguntar sobre docs cargados ---"
curl -s -X POST "$BASE_URL/api/rag/docs/preguntar" \
  -H "Content-Type: application/json" \
  -d '{"query": "¿Qué es Spring AI y cómo se integra con RAG?", "topK": 5}' | python3 -m json.tool 2>/dev/null

echo ""
echo "============================================"
echo "  Swagger UI: $BASE_URL/swagger-ui.html"
echo "  NOTA: Los vectores persisten en PostgreSQL"
echo "============================================"
