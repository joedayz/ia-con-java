#!/bin/bash

# Script para probar la API REST de fase1-springboot
# Asegúrate de que la aplicación esté corriendo: mvn spring-boot:run

BASE_URL="http://localhost:8080"

echo "==================================="
echo "🧪 Probando Fase 1 Spring Boot API"
echo "==================================="
echo ""

# 1. Health check
echo "1️⃣ Health check..."
curl -s "${BASE_URL}/api/chat/health"
echo -e "\n"

# 2. GET simple
echo "2️⃣ GET /api/chat con mensaje simple..."
curl -s "${BASE_URL}/api/chat?message=Di%20hola%20en%20una%20palabra"
echo -e "\n"

# 3. POST sin system prompt
echo "3️⃣ POST /api/chat sin system prompt..."
curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "¿Qué es Java en 10 palabras?"
  }'
echo -e "\n"

# 4. POST con system prompt
echo "4️⃣ POST /api/chat con system prompt..."
curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Explica qué es un LLM",
    "system_prompt": "Eres un profesor de IA que explica de forma muy simple"
  }'
echo -e "\n"

echo "==================================="
echo "✅ Pruebas completadas"
echo "==================================="
