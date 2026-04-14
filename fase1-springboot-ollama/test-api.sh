#!/bin/bash

# Script para probar la API REST de fase1-springboot-ollama con SOLO Ollama
# Asegúrate de que:
# 1. Ollama esté ejecutándose: ollama serve
# 2. Tengas un modelo instalado: ollama pull mistral
# 3. La aplicación esté corriendo: mvn spring-boot:run

BASE_URL="http://localhost:8080"

echo "============================================="
echo "🧪 Pruebas de Fase 1 Spring Boot Ollama"
echo "============================================="
echo ""

# 1. Health check
echo "1️⃣ Health Check"
echo "GET /api/chat/health"
echo ""
curl -s "${BASE_URL}/api/chat/health" | jq . 2>/dev/null || curl -s "${BASE_URL}/api/chat/health"
echo ""
echo ""

# 2. GET simple
echo "2️⃣ GET Simple - Hola"
echo "GET /api/chat?message=Hola"
echo ""
curl -s "${BASE_URL}/api/chat?message=Hola" | jq . 2>/dev/null || curl -s "${BASE_URL}/api/chat?message=Hola"
echo ""
echo ""

# 3. GET con pregunta
echo "3️⃣ GET - Pregunta"
echo "GET /api/chat?message=¿Cuál%20es%20la%20capital%20de%20Francia?"
echo ""
curl -s "${BASE_URL}/api/chat?message=¿Cuál%20es%20la%20capital%20de%20Francia?" | jq . 2>/dev/null || curl -s "${BASE_URL}/api/chat?message=¿Cuál%20es%20la%20capital%20de%20Francia?"
echo ""
echo ""

# 4. POST sin system prompt
echo "4️⃣ POST - Sin System Prompt"
echo "POST /api/chat"
echo ""
curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Escribe un haiku sobre la programación"}' | jq . 2>/dev/null || curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Escribe un haiku sobre la programación"}'
echo ""
echo ""

# 5. POST con system prompt
echo "5️⃣ POST - Con System Prompt"
echo "POST /api/chat"
echo ""
curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "¿Qué es Java?",
    "system_prompt": "Eres un profesor experto en programación. Explica de forma clara y concisa"
  }' | jq . 2>/dev/null || curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué es Java?", "system_prompt": "Eres un profesor experto en programación. Explica de forma clara y concisa"}'
echo ""
echo ""

# 6. POST generación de código
echo "6️⃣ POST - Generación de Código"
echo "POST /api/chat"
echo ""
curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Escribe una función en Python que sume dos números",
    "system_prompt": "Eres un programador experto"
  }' | jq . 2>/dev/null || curl -s -X POST "${BASE_URL}/api/chat" \
  -H "Content-Type: application/json" \
  -d '{"message": "Escribe una función en Python que sume dos números", "system_prompt": "Eres un programador experto"}'
echo ""
echo ""

echo "============================================="
echo "✅ Pruebas completadas"
echo "============================================="
echo ""
echo "💡 Tips:"
echo "   - Si ves errores JSON, instala jq: sudo apt-get install jq"
echo "   - Si la conexión falla, verifica que:"
echo "     1. ollama serve esté ejecutándose"
echo "     2. mvn spring-boot:run esté ejecutándose"
echo "     3. El modelo esté instalado: ollama pull mistral"
