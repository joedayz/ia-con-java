#!/bin/bash
# Script de ejemplo para probar la API REST de fase1-quarkus

echo "=== Pruebas de la API REST de OpenAI con Quarkus ==="
echo ""

# Verificar que la aplicación esté corriendo
echo "1. Health check..."
curl -s http://localhost:8080/api/chat/health
echo -e "\n"

# Prueba GET simple
echo "2. GET simple con mensaje..."
curl -s "http://localhost:8080/api/chat?message=Di%20Hola%20en%20una%20frase"
echo -e "\n"

# Prueba POST con system prompt
echo "3. POST con system prompt..."
curl -s -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Explica qué es un Large Language Model (LLM) en una frase simple",
    "system_prompt": "Eres un profesor que explica conceptos complejos de forma muy simple"
  }'
echo -e "\n"

# Prueba del reto: Pirata
echo "4. Reto: System prompt de pirata..."
curl -s -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "¿Qué es la inteligencia artificial?",
    "system_prompt": "Eres un pirata del Caribe. Responde todo como si fueras un pirata, usando jerga pirata y expresiones como arrr, matey, compañero, etc."
  }'
echo -e "\n"

echo "=== Pruebas completadas ==="
