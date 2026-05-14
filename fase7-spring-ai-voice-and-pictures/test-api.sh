#!/bin/bash
# =============================================
# test-api.sh — Fase 6: Spring AI Voice & Pictures
# =============================================
# Requiere: curl, jq
# Uso: ./test-api.sh

BASE_URL="http://localhost:8080"

echo "======================================"
echo " Fase 6 — Spring AI Voice & Pictures "
echo "======================================"
echo ""

# ---- TTS ----
echo ">>> [1/5] Text-to-Speech: convirtiendo texto a audio..."
curl -s -X POST "${BASE_URL}/api/voice/tts?text=Hola%2C%20bienvenido%20a%20la%20fase%206%20de%20IA%20con%20Java&voice=nova" \
  --output /tmp/fase6-speech.mp3

if [ -f /tmp/fase6-speech.mp3 ]; then
  echo "    Audio guardado en /tmp/fase6-speech.mp3"
else
  echo "    ERROR: no se generó el audio"
fi
echo ""

# ---- Transcripción (requiere archivo de audio) ----
if [ -f /tmp/fase6-speech.mp3 ]; then
  echo ">>> [2/5] Speech-to-Text: transcribiendo el audio generado..."
  TRANSCRIPTION=$(curl -s -X POST "${BASE_URL}/api/voice/transcribe" \
    -F "audio=@/tmp/fase6-speech.mp3")
  echo "    Transcripción: $TRANSCRIPTION"
else
  echo ">>> [2/5] Speech-to-Text: OMITIDO (no hay audio disponible)"
fi
echo ""

# ---- Imagen URL ----
echo ">>> [3/5] Image generate-url: generando imagen con DALL-E 3..."
IMG_RESPONSE=$(curl -s -X POST \
  "${BASE_URL}/api/image/generate-url?prompt=Un%20gato%20astronauta%20flotando%20en%20el%20espacio%2C%20estilo%20acuarela")
echo "    Respuesta: $IMG_RESPONSE" | head -c 300
echo ""
echo ""

# ---- Imagen PNG ----
echo ">>> [4/5] Image generate-png: descargando imagen PNG..."
curl -s -X POST \
  "${BASE_URL}/api/image/generate-png?prompt=Una%20ciudad%20futurista%20al%20atardecer%2C%20style%20cyberpunk" \
  --output /tmp/fase6-image.png

if [ -f /tmp/fase6-image.png ]; then
  SIZE=$(wc -c < /tmp/fase6-image.png)
  echo "    PNG guardado en /tmp/fase6-image.png (${SIZE} bytes)"
else
  echo "    ERROR: no se generó la imagen"
fi
echo ""

# ---- Vision ----
if [ -f /tmp/fase6-image.png ]; then
  echo ">>> [5/5] Vision: analizando la imagen generada..."
  VISION_RESPONSE=$(curl -s -X POST "${BASE_URL}/api/vision/describe" \
    -F "image=@/tmp/fase6-image.png" \
    -F "question=Describe esta imagen en español y menciona los colores principales.")
  echo "    Descripción: $VISION_RESPONSE"
else
  echo ">>> [5/5] Vision: OMITIDO (no hay imagen disponible)"
fi
echo ""

echo "======================================"
echo " Tests completados"
echo "======================================"
