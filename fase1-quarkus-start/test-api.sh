#!/bin/bash

# Script para probar la API de chat (fase1-quarkus-start)
# Asegúrate de tener el servidor corriendo: mvn quarkus:dev

BASE_URL="http://localhost:8080"

echo "=== Pruebas de Chat API ==="
echo ""

# Test 1: Health check
echo "1. Health check..."
curl -s "$BASE_URL/api/chat/health"
echo -e "\n"

# Test 2: GET simple
echo "2. GET /api/chat con mensaje simple..."
curl -s "$BASE_URL/api/chat?message=Hola,%20¿cómo%20estás?"
echo -e "\n"

# Test 3: POST con JSON
echo "3. POST /api/chat con JSON..."
curl -s -X POST "$BASE_URL/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message":"Explica qué es un LLM en una frase"}'
echo -e "\n"

# Test 4: Pregunta técnica
echo "4. Pregunta técnica sobre Java..."
curl -s -X POST "$BASE_URL/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message":"¿Cuál es la diferencia entre una interface y una clase abstracta en Java?"}'
echo -e "\n"

echo "=== Tests completados ==="
