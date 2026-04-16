# Fase 3 Spring AI + Ollama

Proyecto Spring Boot de la fase 3 adaptado para usar **solo Ollama**.

Ahora el chat usa **persistencia real en H2 por defecto**. Si prefieres el comportamiento anterior (solo RAM), puedes cambiar una propiedad.

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

### Windows / PowerShell UTF-8 recomendado

Si ves caracteres como `Ã¡`, `Ã©`, `Â¿` o `Â¡`, configura la consola en UTF-8 antes de probar la API:

```powershell
chcp 65001
[Console]::InputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$OutputEncoding = [Console]::OutputEncoding
```

Luego ejecuta la app o los scripts normalmente.

## 3. Configuración opcional

Por defecto el proyecto usa:
- `OLLAMA_BASE_URL=http://localhost:11434`
- `OLLAMA_CHAT_MODEL=llama3.2`
- `OLLAMA_EMBEDDING_MODEL=mxbai-embed-large`
- `APP_CHAT_MEMORY_REPOSITORY=jdbc` → memoria persistente en H2
- `APP_CHAT_MEMORY_MAX_MESSAGES=20` → ventana de contexto por sesión

Si quieres cambiarlos:

### Linux / macOS
```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_CHAT_MODEL=llama3.2
export OLLAMA_EMBEDDING_MODEL=mxbai-embed-large
export APP_CHAT_MEMORY_REPOSITORY=jdbc
export APP_CHAT_MEMORY_MAX_MESSAGES=20
mvn spring-boot:run
```

### Windows
```powershell
$env:OLLAMA_BASE_URL="http://localhost:11434"
$env:OLLAMA_CHAT_MODEL="llama3.2"
$env:OLLAMA_EMBEDDING_MODEL="mxbai-embed-large"
$env:APP_CHAT_MEMORY_REPOSITORY="jdbc"
$env:APP_CHAT_MEMORY_MAX_MESSAGES="20"
mvn spring-boot:run
```

### Volver al modo anterior en memoria (sin persistencia)

#### Linux / macOS
```bash
export APP_CHAT_MEMORY_REPOSITORY=in-memory
mvn spring-boot:run
```

#### Windows
```powershell
$env:APP_CHAT_MEMORY_REPOSITORY="in-memory"
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

### Health check

#### Linux / macOS
```bash
curl http://localhost:8080/actuator/health
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/actuator/health
```

### Chat simple

#### Linux / macOS
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Hola, me llamo Carlos"}'
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/chat `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"message":"Hola, me llamo Carlos"}'
```

### Verificar memoria

#### Linux / macOS
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"¿Cómo me llamo?"}'
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/chat `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"message":"¿Cómo me llamo?"}'
```

### Verificar persistencia tras reiniciar

1. Envía un mensaje a una sesión fija, por ejemplo `demo-persistencia`.
2. Reinicia la aplicación.
3. Vuelve a preguntar por el dato anterior usando la misma sesión.

#### Linux / macOS
```bash
curl -X POST http://localhost:8080/api/chat/demo-persistencia \
  -H "Content-Type: application/json" \
  -d '{"message":"Hola, me llamo Carlos y hoy es mi primer día conversando contigo"}'

# Reinicia la app aquí

curl -X POST http://localhost:8080/api/chat/demo-persistencia \
  -H "Content-Type: application/json" \
  -d '{"message":"¿Cómo me llamo y qué día es hoy para mí?"}'
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/chat/demo-persistencia `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"message":"Hola, me llamo Carlos y hoy es mi primer día conversando contigo"}'

# Reinicia la app aquí

Invoke-RestMethod -Uri http://localhost:8080/api/chat/demo-persistencia `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"message":"¿Cómo me llamo y qué día es hoy para mí?"}'
```

### Estado del servicio de chat

#### Linux / macOS
```bash
curl http://localhost:8080/api/chat/status
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/chat/status
```

### Chat multi-sesión

#### Linux / macOS
```bash
curl -X POST http://localhost:8080/api/chat/user-123 \
  -H "Content-Type: application/json" \
  -d '{"message":"Hola, me llamo Carlos"}'

curl -X POST http://localhost:8080/api/chat/user-456 \
  -H "Content-Type: application/json" \
  -d '{"message":"Hola, me llamo Ana"}'

curl -X POST http://localhost:8080/api/chat/user-123 \
  -H "Content-Type: application/json" \
  -d '{"message":"¿Cuál es mi nombre?"}'
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/chat/user-123 `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"message":"Hola, me llamo Carlos"}'

Invoke-RestMethod -Uri http://localhost:8080/api/chat/user-456 `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"message":"Hola, me llamo Ana"}'

Invoke-RestMethod -Uri http://localhost:8080/api/chat/user-123 `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"message":"¿Cuál es mi nombre?"}'
```

### Limpiar sesión

#### Linux / macOS
```bash
curl -X DELETE http://localhost:8080/api/chat/user-123
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/chat/user-123 -Method DELETE
```

### Estado del vector store

#### Linux / macOS
```bash
curl http://localhost:8080/api/buscar/status
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/buscar/status
```

### Indexar documentos demo

#### Linux / macOS
```bash
curl -X POST http://localhost:8080/api/buscar/demo
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/buscar/demo -Method POST
```

### Buscar por similitud

#### Linux / macOS
```bash
curl "http://localhost:8080/api/buscar?query=similitud%20coseno&topK=3"
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/buscar?query=similitud%20coseno&topK=3"
```

### Cargar PDF local

#### Linux / macOS
```bash
curl -X POST http://localhost:8080/api/buscar/pdf \
  -H "Content-Type: application/json" \
  -d '{"path":"/Users/josediaz/Projects/JoeDayz/ia-con-java/docs/01-AI_Developer_Blueprint.pdf","sourceId":"ai-blueprint"}'
```

#### Windows (PowerShell)
```powershell
$pdfPath = "C:/ruta/al/archivo.pdf"
Invoke-RestMethod -Uri http://localhost:8080/api/buscar/pdf `
  -Method POST `
  -ContentType "application/json" `
  -Body (@{ path = $pdfPath; sourceId = "mi-pdf" } | ConvertTo-Json -Compress)
```

### RAG alias (`/api/rag`)

#### Linux / macOS
```bash
curl -X POST http://localhost:8080/api/rag \
  -H "Content-Type: application/json" \
  -d '{"query":"¿Qué son los embeddings?","topK":3}'
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/rag `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"query":"¿Qué son los embeddings?","topK":3}'
```

### RAG manual

#### Linux / macOS
```bash
curl -X POST http://localhost:8080/api/rag/simple \
  -H "Content-Type: application/json" \
  -d '{"query":"¿Qué son los embeddings?","topK":3}'
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/rag/simple `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"query":"¿Qué son los embeddings?","topK":3}'
```

### RAG con advisor

#### Linux / macOS
```bash
curl -X POST http://localhost:8080/api/rag/advisor \
  -H "Content-Type: application/json" \
  -d '{"query":"¿Qué es la similitud coseno?","topK":3}'
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/rag/advisor `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"query":"¿Qué es la similitud coseno?","topK":3}'
```

### Cargar documentación Markdown

#### Linux / macOS
```bash
DOCS_PATH="$(pwd)/data/docs"

curl -X POST http://localhost:8080/api/rag/docs/cargar \
  -H "Content-Type: application/json" \
  -d "{\"path\":\"$DOCS_PATH\"}"
```

#### Windows (PowerShell)
```powershell
$docsPath = Join-Path (Get-Location) "data/docs"

Invoke-RestMethod -Uri http://localhost:8080/api/rag/docs/cargar `
  -Method POST `
  -ContentType "application/json" `
  -Body (@{ path = $docsPath } | ConvertTo-Json -Compress)
```

### Preguntar a la documentación

#### Linux / macOS
```bash
curl -X POST http://localhost:8080/api/rag/docs/preguntar \
  -H "Content-Type: application/json" \
  -d '{"query":"¿Cuál es la diferencia entre RAG simple y advisor?","topK":4}'
```

#### Windows (PowerShell)
```powershell
Invoke-RestMethod -Uri http://localhost:8080/api/rag/docs/preguntar `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"query":"¿Cuál es la diferencia entre RAG simple y advisor?","topK":4}'
```

## 7. Scripts de prueba

### Linux / macOS
```bash
chmod +x test-api.sh
./test-api.sh
```

### Windows
```powershell
chcp 65001
[Console]::InputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$OutputEncoding = [Console]::OutputEncoding

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

### En Windows veo caracteres rotos como `Ã¡`, `Ã±` o `Â¿`
Antes de ejecutar comandos interactivos o `test-api.ps1`, fuerza UTF-8:

```powershell
chcp 65001
[Console]::InputEncoding = [System.Text.UTF8Encoding]::new($false)
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$OutputEncoding = [Console]::OutputEncoding
```

Si aún ocurre, revisa también que tu terminal/IDE esté guardando y mostrando archivos en UTF-8.

En este módulo el backend ya fuerza UTF-8 para requests y responses HTTP. Si todavía ves `Â¡` o `DÃ­a`, el problema suele estar en el cliente (terminal, Swagger incrustado en otra página, proxy o extensión del navegador).

## Datos de ejemplo

El proyecto incluye Markdown de ejemplo en `data/docs/` para probar el asistente de documentación sin preparación adicional.
