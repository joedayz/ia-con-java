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
export APP_VOICE_STT_MODEL=tiny
export APP_VOICE_STT_TIMEOUT_SECONDS=60
```

## Arrancar

```bash
cd fase6-spring-ai-voice-and-pictures-ollama
mvn spring-boot:run
```

- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Endpoints

- `POST /api/voice/tts?text=...&voice=Monica` -> audio local (AIFF en macOS, WAV en Windows)
- `POST /api/voice/transcribe` (multipart `audio`) -> texto
- `POST /api/image/generate-url?prompt=...` -> data URL (`data:image/png;base64,...`)
- `POST /api/image/generate-png?prompt=...` -> bytes PNG
- `POST /api/vision/describe` (multipart `image`, `question`) -> descripción

## Pruebas de endpoints (copy/paste)

Precondición: aplicación corriendo en `http://localhost:8080`.

### 1) Voice - Text to Speech (`/api/voice/tts`)

Linux/macOS (curl/bash):

```bash
BASE_URL="http://localhost:8080"

curl -X POST "$BASE_URL/api/voice/tts?text=Hola%20desde%20Ollama&voice=Monica" \
  --output speech.aiff
```

Windows (PowerShell):

```powershell
$BASE_URL = "http://localhost:8080"

Invoke-WebRequest -Uri "$BASE_URL/api/voice/tts?text=Hola%20desde%20Ollama&voice=Monica" `
  -Method POST `
  -OutFile ".\speech.wav"
```

Salida esperada: archivo `speech.aiff` (macOS) o `speech.wav` (Windows).

### 2) Voice - Speech to Text (`/api/voice/transcribe`)

Linux/macOS (curl/bash):

```bash
BASE_URL="http://localhost:8080"

curl -X POST "$BASE_URL/api/voice/transcribe" \
  -F "audio=@./speech.aiff"
```

Windows (PowerShell):

```powershell
$BASE_URL = "http://localhost:8080"

$form = @{ audio = Get-Item ".\speech.wav" }
Invoke-RestMethod -Uri "$BASE_URL/api/voice/transcribe" -Method POST -Form $form
```

Salida esperada: JSON con `transcription`.

### 3) Image - Generar URL (`/api/image/generate-url`)

Linux/macOS (curl/bash):

```bash
BASE_URL="http://localhost:8080"

curl -X POST "$BASE_URL/api/image/generate-url?prompt=Un%20perro%20astronauta%20en%20estilo%20pixel%20art"
```

Windows (PowerShell):

```powershell
$BASE_URL = "http://localhost:8080"

Invoke-RestMethod -Uri "$BASE_URL/api/image/generate-url?prompt=Un%20perro%20astronauta%20en%20estilo%20pixel%20art" -Method POST
```

Salida esperada: JSON con `url` (formato `data:image/png;base64,...`).

### 4) Image - Generar PNG (`/api/image/generate-png`)

Linux/macOS (curl/bash):

```bash
BASE_URL="http://localhost:8080"

curl -X POST "$BASE_URL/api/image/generate-png?prompt=Una%20ciudad%20futurista%20de%20noche" \
  --output image.png
```

Windows (PowerShell):

```powershell
$BASE_URL = "http://localhost:8080"

Invoke-WebRequest -Uri "$BASE_URL/api/image/generate-png?prompt=Una%20ciudad%20futurista%20de%20noche" `
  -Method POST `
  -OutFile ".\image.png"
```

Salida esperada: archivo `image.png` generado.

### 5) Vision - Describir imagen (`/api/vision/describe`)

Linux/macOS (curl/bash):

```bash
BASE_URL="http://localhost:8080"

curl -X POST "$BASE_URL/api/vision/describe" \
  -F "image=@./image.png" \
  -F "question=Describe esta imagen en espanol y menciona colores principales"
```

Windows (PowerShell):

```powershell
$BASE_URL = "http://localhost:8080"

$form = @{
  image = Get-Item ".\image.png"
  question = "Describe esta imagen en espanol y menciona colores principales"
}
Invoke-RestMethod -Uri "$BASE_URL/api/vision/describe" -Method POST -Form $form
```

Salida esperada: JSON con `description` y `question`.

## Scripts de prueba automáticos

- Linux/macOS: `./test-api.sh`
- Windows PowerShell: `./test-api.ps1`

## Notas importantes

- `tts` usa comando local `say` (en macOS).
- En Windows, `tts` usa PowerShell + `System.Speech` y genera WAV.
- `transcribe` usa `whisper` CLI; si no está instalado, el endpoint devolverá error.
- La generación de imagen no usa DALL-E; se genera SVG con Ollama y luego se renderiza a PNG.

## Troubleshooting STT (muy comun en Windows)

Si llamas `POST /api/voice/transcribe` y no existe `whisper` en PATH, ahora la API responde:

- HTTP `503 Service Unavailable`
- JSON con `error=STT_UNAVAILABLE` y un `hint` accionable

Ejemplo de respuesta:

```json
{
  "error": "STT_UNAVAILABLE",
  "message": "No se encontro el comando STT 'whisper' en el sistema.",
  "hint": "Instala whisper CLI o configura APP_VOICE_STT_COMMAND con ruta absoluta."
}
```

Configurar ruta absoluta del comando STT:

Linux/macOS:

```bash
export APP_VOICE_STT_COMMAND="/usr/local/bin/whisper"
```

Windows PowerShell:

```powershell
$env:APP_VOICE_STT_COMMAND="C:\\Users\\TU_USUARIO\\AppData\\Local\\Programs\\Python\\Python312\\Scripts\\whisper.exe"
```

### Si `transcribe` tarda mucho

- La primera ejecucion de `whisper` puede descargar/cargar el modelo y tardar bastante.
- En CPU, `base` tarda mas que `tiny`.
- La API ahora tiene timeout configurable para evitar que la request quede colgada.

Recomendado para clase:

```bash
export APP_VOICE_STT_MODEL=tiny
export APP_VOICE_STT_TIMEOUT_SECONDS=60
```

Precalentar `whisper` una vez antes de la demo (descarga modelo y cache):

```bash
$APP_VOICE_STT_COMMAND --model tiny --language es --output_format txt --output_dir /tmp ./speech.aiff
```

