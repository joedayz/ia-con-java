# AI Patterns (Spring AI) – Guía de pruebas (Java 21)

Esta carpeta contiene demos de “AI patterns” con Spring Boot + Spring AI.

## Requisitos

- Java 21
- Una API key del proveedor (ej. OpenAI)

Variables típicas (ajusta según tu proveedor/modelo):

```bash
export OPENAI_API_KEY="..."
```

> Si tus proyectos usan `spring.ai.openai.*`, Spring AI leerá `OPENAI_API_KEY` automáticamente.

## 1) Simple Translator (`simple-translator`)

### Levantar

```bash
cd spring-ai-demos/ai-patterns/simple-translator
./gradlew bootRun
```

### Probar

```bash
curl -s http://localhost:8080/translate \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Where is the bathroom?",
    "sourceLanguage": "English",
    "targetLanguage": "Spanish"
  }'
```

## 2) Sentiment Analysis (`sentiment-analysis`)

### Levantar

```bash
cd spring-ai-demos/ai-patterns/sentiment-analysis
./gradlew bootRun
```

### Probar

```bash
curl -s http://localhost:8080/sentiment \
  -H "Content-Type: application/json" \
  -d '{ "text": "I love this product. It saved me hours!" }'
```

Ejemplos equivalentes a los del libro (tu endpoint espera JSON):

```bash
curl -s http://localhost:8080/sentiment \
  -H "Content-Type: application/json" \
  -d '{ "text": "It'\''s the end of the world." }'
```

```bash
curl -s http://localhost:8080/sentiment \
  -H "Content-Type: application/json" \
  -d '{ "text": "It'\''s the end of the world as we know it." }'
```

```bash
curl -s http://localhost:8080/sentiment \
  -H "Content-Type: application/json" \
  -d '{ "text": "It'\''s the end of the world as we know it. And I feel fine" }'
```

```bash
curl -s http://localhost:8080/sentiment \
  -H "Content-Type: application/json" \
  -d '{ "text": "That'\''s great, it starts with an earthquake. Birds and snakes, and aeroplanes. And Lenny Bruce is not afraid." }'
```

## 3) Board Game Buddy (`board-game-buddy`)

Este proyecto está protegido con **Basic Auth** (ver `SecurityConfig`):

- `mickey` / `password` (incluye `ROLE_PREMIUM_USER`)
- `donald` / `password`

### Levantar

```bash
cd spring-ai-demos/ai-patterns/board-game-buddy
./gradlew bootRun
```

Nota (OpenAI Moderation):
- Si ves `Invalid value for 'model' = text-moderation-latest`, asegúrate de tener configurado:
  - `spring.ai.openai.moderation.options.model=omni-moderation-latest`

### 3.1) Preguntar (`/ask`)

```bash
curl -s http://localhost:8080/ask \
  -u mickey:password \
  -H "Content-Type: application/json" \
  -H "X_AI_CONVERSATION_ID: demo" \
  -d '{
    "gameTitle": "Burger Battle",
    "question": "How do I win the game?",
    "language": "Spanish"
  }'
```

### 3.2) Resumir reglas desde un PDF (`/summarize`)

Envía un PDF/archivo como `multipart/form-data` en el part `rulesDocument`:

```bash
curl -s http://localhost:8080/summarize \
  -u mickey:password \
  -F "rulesDocument=@$(pwd)/docs/09_Spec-Driven-Development.pdf;type=application/pdf"
```

## 4) Summarization runner (`summarization`)

Este demo no expone endpoint: corre al iniciar la app **si** configuras `rules.resource`.

### Levantar

```bash
cd spring-ai-demos/ai-patterns/summarization
./gradlew bootRun -Dspring-boot.run.jvmArguments="-Drules.resource=file:$(cd ../../.. && pwd)/docs/09_Spec-Driven-Development.pdf"
```

## Notas sobre “desactualizado”

- En estos demos se usa `ChatClient` (Spring AI 1.x). Si cambias de proveedor/modelo, normalmente solo cambia configuración (`spring.ai.*`) y dependencias starter.
- Si algún endpoint o propiedad no coincide con tu parte 2, lo ajusto y actualizo esta guía.

