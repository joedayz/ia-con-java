# Fase 3 Spring AI + Ollama

Proyecto Spring Boot de la fase 3 adaptado para usar **solo Ollama**.

Incluye:
- Chat con memoria conversacional
- Chat multi-sesión
- Búsqueda semántica con embeddings locales
- RAG manual y RAG con `QuestionAnswerAdvisor`
- Carga de PDF con Tika
- Asistente de documentación con archivos Markdown
- Swagger UI

## Requisitos

- Java 21+
- Maven 3.8+
- [Ollama](https://ollama.com/) instalado y ejecutándose

## Modelos recomendados

- Chat: `llama3.2`
- Embeddings: `mxbai-embed-large`

## 1. Instalar / levantar Ollama

### Linux / macOS
```bash
ollama serve
```

En otra terminal:
```bash
ollama pull llama3.2
ollama pull mxbai-embed-large
```

### Windows
```powershell
ollama serve
```

En otra terminal de PowerShell:
```powershell
ollama pull llama3.2
ollama pull mxbai-embed-large
```

> Si ya tienes el servicio de Ollama corriendo en segundo plano, no necesitas ejecutar `ollama serve` manualmente.

## 2. Ejecutar la aplicación

### Linux / macOS
```bash
cd fase3-spring-ai-ollama
mvn spring-boot:run
```

### Windows
```powershell
cd fase3-spring-ai-ollama
mvn spring-boot:run
```

## 3. Configuración opcional

Por defecto el proyecto usa:
- `OLLAMA_BASE_URL=http://localhost:11434`
- `OLLAMA_CHAT_MODEL=llama3.2`
- `OLLAMA_EMBEDDING_MODEL=mxbai-embed-large`

Si quieres cambiarlos:

### Linux / macOS
```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_CHAT_MODEL=llama3.2
export OLLAMA_EMBEDDING_MODEL=mxbai-embed-large
mvn spring-boot:run
```

### Windows
```powershell
$env:OLLAMA_BASE_URL="http://localhost:11434"
$env:OLLAMA_CHAT_MODEL="llama3.2"
$env:OLLAMA_EMBEDDING_MODEL="mxbai-embed-large"
mvn spring-boot:run
```

## 4. Accesos útiles

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health: `http://localhost:8080/actuator/health`
- H2 Console: `http://localhost:8080/h2-console`

## 5. Endpoints principales

### Chat
- `POST /api/chat`
- `POST /api/chat/{sessionId}`
- `DELETE /api/chat/{sessionId}`
- `GET /api/chat/status`

### Búsqueda semántica
- `GET /api/buscar/status`
- `POST /api/buscar/demo`
- `GET /api/buscar?query=...&topK=...`
- `POST /api/buscar/pdf`

### RAG
- `POST /api/rag`
- `POST /api/rag/simple`
- `POST /api/rag/advisor`
- `POST /api/rag/docs/cargar`
- `POST /api/rag/docs/preguntar`

## 6. Pruebas rápidas

### Chat simple
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Hola, me llamo Carlos"}'
```

### Verificar memoria
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"¿Cómo me llamo?"}'
```

### Indexar documentos demo
```bash
curl -X POST http://localhost:8080/api/buscar/demo
```

### Buscar por similitud
```bash
curl "http://localhost:8080/api/buscar?query=similitud%20coseno&topK=3"
```

### RAG manual
```bash
curl -X POST http://localhost:8080/api/rag/simple \
  -H "Content-Type: application/json" \
  -d '{"query":"¿Qué son los embeddings?","topK":3}'
```

### Cargar documentación Markdown
```bash
curl -X POST http://localhost:8080/api/rag/docs/cargar \
  -H "Content-Type: application/json" \
  -d '{"path":"./data/docs"}'
```

### Preguntar a la documentación
```bash
curl -X POST http://localhost:8080/api/rag/docs/preguntar \
  -H "Content-Type: application/json" \
  -d '{"query":"¿Cuál es la diferencia entre RAG simple y advisor?","topK":4}'
```

## 7. Scripts de prueba

### Linux / macOS
```bash
chmod +x test-api.sh
./test-api.sh
```

### Windows
```powershell
./test-api.ps1
```

## Troubleshooting

### Error: no responde Ollama
Verifica:
- que Ollama esté corriendo en `http://localhost:11434`
- que los modelos existan:

```bash
ollama list
```

### Error en búsqueda semántica o RAG
Normalmente indica que falta el modelo de embeddings. Descárgalo:

```bash
ollama pull mxbai-embed-large
```

### Error: conexión rechazada a `localhost:11434`
Inicia Ollama antes de levantar Spring Boot.

## Datos de ejemplo

El proyecto incluye Markdown de ejemplo en `data/docs/` para probar el asistente de documentación sin preparación adicional.
