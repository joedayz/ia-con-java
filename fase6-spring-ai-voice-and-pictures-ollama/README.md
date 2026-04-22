# Fase 6 - Spring AI Voice and Pictures (Ollama)

Proyecto equivalente a la fase 6 original, pero orientado a entorno local:

- **Chat/Vision con Ollama** (Spring AI)
- **Generación de imagen PNG** usando Ollama para crear SVG y render local a PNG
- **Text-to-Speech local** con `say` (macOS)
- **Speech-to-Text local** con `whisper` CLI (opcional)

## Requisitos

- Java 21+
- Maven 3.9+
- Ollama instalado y ejecutándose en `http://localhost:11434`
- Modelo recomendado: `llama3.2-vision`
- Opcional para transcripción: `whisper` CLI instalado

## Configuración

`src/main/resources/application.properties` ya incluye valores por defecto. Variables opcionales:

```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_CHAT_MODEL=llama3.2-vision
export APP_VOICE_TTS_DEFAULT_VOICE=Monica
export APP_VOICE_STT_COMMAND=whisper
```

## Arrancar

```bash
cd fase6-spring-ai-voice-and-pictures-ollama
mvn spring-boot:run
```

- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Endpoints

- `POST /api/voice/tts?text=...&voice=Monica` -> audio AIFF
- `POST /api/voice/transcribe` (multipart `audio`) -> texto
- `POST /api/image/generate-url?prompt=...` -> data URL (`data:image/png;base64,...`)
- `POST /api/image/generate-png?prompt=...` -> bytes PNG
- `POST /api/vision/describe` (multipart `image`, `question`) -> descripción

## Notas importantes

- `tts` usa comando local `say` (en macOS).
- `transcribe` usa `whisper` CLI; si no está instalado, el endpoint devolverá error.
- La generación de imagen no usa DALL-E; se genera SVG con Ollama y luego se renderiza a PNG.
