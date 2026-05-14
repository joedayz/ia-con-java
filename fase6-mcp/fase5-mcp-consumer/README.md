# Fase 5 MCP Consumer

Demo Spring Boot con:

- Chat + tool calling MCP hacia `fase5-mcp-provider`
- Endpoint multimodal para analizar imagenes con Ollama

## Requisitos

- Java 21+
- Maven 3.8+
- Ollama activo en `http://localhost:11434`
- Provider MCP activo en `http://localhost:8091`

## Modelo recomendado para multimodal

Para el endpoint de imagen usa un modelo de vision, por ejemplo:

```bash
ollama pull llava
```

Luego ejecuta el consumer con ese modelo:

```bash
export OLLAMA_CHAT_MODEL=llava
mvn spring-boot:run
```

## Endpoints

- `POST /api/mcp/chat`
- `GET /api/mcp/demo/cronograma/{clase}`
- `GET /api/mcp/demo/modulo/{fase}`
- `POST /api/mcp/demo/actividad`
- `GET /api/multimodal/status`
- `POST /api/multimodal/analyze` (multipart: `image`, opcional `prompt`)

## Prueba rapida de multimodal

```bash
curl -X POST http://localhost:8092/api/multimodal/analyze \
  -F "image=@/ruta/a/tu/imagen.png" \
  -F "prompt=Describe lo mas importante de la escena"
```

Swagger:

- `http://localhost:8092/swagger-ui.html`

