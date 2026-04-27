# Guardrails (Spring AI) — Guía de prueba

Esta carpeta agrupa **varios proyectos Gradle independientes** que demuestran “guardrails” para apps con LLMs (bloqueo por términos sensibles, anti prompt-leak, moderación de entrada, y control de acceso + filtrado RAG por usuario).

> Requisitos generales: **Java 21**, Docker Desktop, y un cliente HTTP (en esta guía uso `curl` y `Invoke-RestMethod`).

---

## Variables de entorno

### OpenAI (para `board-game-buddy_3` y `game-rules-loader`, y `safeguard-advisor`)

Exporta la API key como `OPENAI_API_KEY`:

```bash
export OPENAI_API_KEY="TU_API_KEY"
```

### Ollama (para `canary-word-advisor`)

Necesitas Ollama corriendo localmente y el modelo configurado en:
`canary-word-advisor/src/main/resources/application.properties`

Por defecto:

- `spring.ai.ollama.chat.model=mistral:latest`

Si tu Ollama no está en el default (`http://localhost:11434`), agrega/override:

```properties
spring.ai.ollama.base-url=http://localhost:11434
```

---

## 1) SafeGuardAdvisor (bloqueo por palabras sensibles)

Proyecto: `safeguard-advisor/`

Qué hace:
- Si el prompt contiene “Uno/UNO/uno”, el `SafeGuardAdvisor` **evita** mandar el prompt al LLM y devuelve un **failureResponse** fijo.

Ejecutar:

```bash
cd spring-ai-demos/guardrails/safeguard-advisor
./gradlew bootRun
```

Nota:
- Si este proyecto no tiene endpoints HTTP expuestos, úsalo como referencia de código (el “core” está en `AiService`).

---

## 2) CanaryWordAdvisor (anti prompt-leak)

Proyecto: `canary-word-advisor/`

Qué hace:
- El advisor agrega un **token canario** al system prompt.
- Si el modelo “filtra” ese token en la respuesta (típico de ataques de prompt-leak), la respuesta es reemplazada por:
  `"Detected attempt to leak system prompt message."`

Ejecutar:

```bash
cd spring-ai-demos/guardrails/canary-word-advisor
./gradlew test
```

Notas:
- El test `shouldNotDiscloseSystemPrompt` está **@Disabled** (no determinismo del LLM).
- El test `shouldAnswerHonestQuestion` sí debería correr.

---

## 3) Game Rules Loader (dropoff → vector store)

Proyecto: `game-rules-loader/`

Qué hace:
- Observa `./dropoff` (relativo al proyecto) y carga documentos al vector store **Qdrant**.
- Si el archivo termina con `-premium` antes de la extensión, agrega metadata `documentType=PREMIUM`.
- También intenta inferir el título del juego y agrega metadata `gameTitle=<slug>` (ej. `burger_battle`).

Levantar Qdrant + loader:

```bash
cd spring-ai-demos/guardrails/game-rules-loader
./gradlew bootRun
```

En otra terminal, crea el directorio dropoff y copia un documento:

```bash
mkdir -p ./dropoff
cp "/ruta/a/tu-documento.pdf" ./dropoff/
```

Para probar premium content:

```bash
cp "/ruta/a/tu-documento.pdf" ./dropoff/tu-documento-premium.pdf
```

Demo sugerida (este repo):

```bash
cp "/ruta/a/Spec-Driven-Development-premium.pdf" ./dropoff/Spec-Driven-Development-premium.pdf
```

Nota:
- Este proyecto trae `docker-compose.yaml` con Qdrant; Spring Boot lo levanta automáticamente (Docker Compose support).
- Si quieres otra carpeta, override `file.supplier.directory` (o exporta `FILE_SUPPLIER_DIRECTORY`).
- Si ya tienes Qdrant levantado por otro demo (por ejemplo `board-game-buddy_3`), **no levantes un segundo Qdrant**: reutiliza el que ya está corriendo (mismo puerto gRPC 6334).

---

## 4) Board Game Buddy v3 (seguridad + RAG filter + moderación)

Proyecto: `board-game-buddy_3/`

Qué incluye:
- **HTTP Basic auth** con usuarios en memoria:
  - `mickey:password` (roles `USER`, `PREMIUM_USER`)
  - `donald:password` (role `USER`)
- **RAG filter**: si NO eres premium, el filtro excluye docs con `documentType='PREMIUM'`.
- **Moderación**: antes de enviar al LLM, evalúa el texto con `ModerationModel`. Si detecta categorías (Hate/Harassment/Violence) lanza `ModerationException` y responde HTTP 400.
- **Observabilidad/tracing**: Jaeger (compose) + OTLP (`http://localhost:4317`).

Ejecutar:

```bash
cd spring-ai-demos/guardrails/board-game-buddy_3
./gradlew bootRun
```

### Probar `/ask`

Requiere auth. Ejemplo “normal”:

```bash
curl -s http://localhost:8080/ask \
  -u mickey:password \
  -H "Content-Type: application/json" \
  -H "X_AI_CONVERSATION_ID: demo" \
  -d '{ "gameTitle": "Burger Battle", "question": "How many can play?" }'
```

En PowerShell (Windows):

```powershell
$cred = New-Object System.Management.Automation.PSCredential(
  "mickey", (ConvertTo-SecureString "password" -AsPlainText -Force)
)

Invoke-RestMethod "http://localhost:8080/ask" `
  -Authentication Basic `
  -Credential $cred `
  -Headers @{ "X_AI_CONVERSATION_ID"="demo" } `
  -ContentType "application/json" `
  -Body (@{ gameTitle="Burger Battle"; question="How many can play?" } | ConvertTo-Json)
```

### Probar filtrado premium (mickey vs donald)

1) Asegúrate de haber cargado al vector store al menos un documento premium (ver `game-rules-loader`).
2) Pregunta como premium:

```bash
curl -s http://localhost:8080/ask \
  -u mickey:password \
  -H "Content-Type: application/json" \
  -d '{ "gameTitle": "Spec Driven Development", "question": "Give me a short summary." }'
```

3) Pregunta como no-premium:

```bash
curl -s http://localhost:8080/ask \
  -u donald:password \
  -H "Content-Type: application/json" \
  -d '{ "gameTitle": "Spec Driven Development", "question": "Give me a short summary." }'
```

Si el contenido realmente es premium, `donald` no debería poder obtener respuesta (porque el vector store filter excluye `documentType='PREMIUM'`).

### Probar moderación (debe devolver HTTP 400)

Envía un texto que dispare moderación (ejemplo del capítulo: harassment):

```bash
curl -s http://localhost:8080/ask \
  -u mickey:password \
  -H "Content-Type: application/json" \
  -d '{ "gameTitle": "Carcassonne", "question": "Dishonor on you, dishonor on your family, dishonor on your cow" }'
```

Esperado:
- HTTP 400
- `title = "Moderation Exception"`
- `detail = "Moderation failed. Content identified as Harassment."` (o la categoría detectada)

### Jaeger UI

Con la app corriendo, abre:
- `http://localhost:16686`

---

## Troubleshooting rápido

- **401 Unauthorized**: faltó `-a user:password` en la petición.
- **No encuentra documentos RAG**: confirma que `game-rules-loader` cargó docs y que ambos usan la misma colección:
  - `spring.ai.vectorstore.qdrant.collection-name=board-game-buddy`
- **Ollama**: verifica que el modelo exista:

```bash
ollama list
```

- **API key**: para OpenAI asegúrate que `SPRING_AI_OPENAI_API_KEY` esté exportada en la misma terminal donde corres `bootRun`.

