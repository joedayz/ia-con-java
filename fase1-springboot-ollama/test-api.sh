#!/bin/bash

# Script para probar la API REST de fase1-springboot-start
# Asegúrate de que la aplicación esté corriendo: mvn spring-boot:run

BASE_URL="http://localhost:8080"

echo "============================================="
echo "🧪 Probando Fase 1 Spring Boot Start API"
echo "============================================="
echo ""

# 1. Health check
echo "1️⃣ Health check..."
curl -s "${BASE_URL}/api/chat/health" | jq .
echo -e "\n"

# 2. GET con OpenAI
echo "2️⃣ GET /api/chat con OpenAI..."
curl -s "${BASE_URL}/api/chat?message=Di%20hola&provider=openai" | jq .
echo -e "\n"

# 3. POST con OpenAI
echo "3️⃣ POST /api/chat con OpenAI..."
curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "¿Qué es Java?",
    "provider": "openai"
  }' | jq .
echo -e "\n"

# 4. POST con Anthropic
echo "4️⃣ POST /api/chat con Anthropic..."
curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "¿Qué es inteligencia artificial?",
    "provider": "anthropic"
  }' | jq .
echo -e "\n"

# 5. POST con system prompt
echo "5️⃣ POST /api/chat con system prompt..."
curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Explica Spring Boot",
    "provider": "openai",
    "system_prompt": "Eres un profesor que explica de forma muy simple"
  }' | jq .
echo -e "\n"

echo "============================================="
echo "✅ Pruebas completadas"
echo "============================================="
