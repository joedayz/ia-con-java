# Fase 6 - Spring AI Voice and Pictures

Demo de **voz** e **imágenes** con Spring AI y OpenAI. Un único proyecto que cubre tres capacidades multimodales:

| Capacidad | Tecnología | Endpoint |
|-----------|-----------|----------|
| Text-to-Speech | OpenAI TTS (`tts-1`) | `POST /api/voice/tts` |
| Speech-to-Text | OpenAI Whisper (`whisper-1`) | `POST /api/voice/transcribe` |
| Generación de imágenes (URL) | OpenAI DALL-E 3 | `POST /api/image/generate-url` |
| Generación de imágenes (PNG) | OpenAI DALL-E 3 | `POST /api/image/generate-png` |
| Análisis de imágenes (Vision) | GPT-4o Vision | `POST /api/vision/describe` |

---

## Requisitos

- Java 21+
- Maven 3.9+
- API Key de OpenAI con acceso a: `gpt-4o`, `dall-e-3`, `tts-1`, `whisper-1`

---

## Configuración

Exporta tu API key antes de arrancar:

```bash
export OPENAI_API_KEY=sk-...
```

---

## Arrancar la aplicación

```bash
mvn spring-boot:run
```

Swagger UI disponible en: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Endpoints

### 🔊 Voice — Text-to-Speech

Convierte texto a audio MP3. Voces disponibles: `alloy`, `echo`, `fable`, `onyx`, `nova`, `shimmer`.

```bash
curl -X POST "http://localhost:8080/api/voice/tts?text=Hola%20mundo&voice=nova" \
  --output speech.mp3
```

### 🎙️ Voice — Speech-to-Text (Transcripción)

Transcribe un archivo de audio a texto.

```bash
curl -X POST http://localhost:8080/api/voice/transcribe \
  -F "audio=@mi-audio.mp3"
```

### 🖼️ Image — Generar URL

Genera una imagen con DALL-E 3 y devuelve la URL temporal de OpenAI.

```bash
curl -X POST "http://localhost:8080/api/image/generate-url?prompt=Un+gato+astronauta+en+Marte"
```

### 🖼️ Image — Generar PNG

Genera una imagen con DALL-E 3 y descarga directamente el PNG.

```bash
curl -X POST "http://localhost:8080/api/image/generate-png?prompt=Una+ciudad+futurista+al+atardecer" \
  --output imagen.png
```

### 👁️ Vision — Analizar imagen

Sube una imagen y hazle una pregunta. GPT-4o Vision la analiza.

```bash
curl -X POST http://localhost:8080/api/vision/describe \
  -F "image=@foto.jpg" \
  -F "question=¿Qué hay en esta imagen?"
```

---

## Estructura del proyecto

```
src/main/java/com/joedayz/fase6/
├── Fase6Application.java
├── voice/
│   ├── VoiceService.java          (interfaz)
│   ├── OpenAiVoiceService.java    (implementación TTS + Whisper)
│   └── VoiceController.java       (POST /api/voice/tts, /transcribe)
├── image/
│   ├── ImageService.java          (interfaz)
│   ├── OpenAiImageService.java    (implementación DALL-E 3)
│   └── ImageController.java       (POST /api/image/generate-url, /generate-png)
└── vision/
    └── VisionController.java      (POST /api/vision/describe)
```
