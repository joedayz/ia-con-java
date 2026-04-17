# Fase 4 - Spring AI + Ollama + PgVector

Evolución de Fase 3: reemplaza `SimpleVectorStore` (en memoria) por **PgVector** (PostgreSQL) para almacenamiento vectorial persistente. Implementa `RetrievalAugmentationAdvisor` con `QuestionAnswerAdvisor` y arquitectura modular de interceptores.

## Arquitectura

```
                        ┌────────────────────────────────────────────────┐
                        │          RetrievalAugmentationAdvisor          │
┌─────────┐    ┌────────┤  Pre-procesamiento  │  Post-procesamiento     │    ┌──────────┐
│  Query   │───▶│Advisor │  (MultiQueryExpander│  (ContextualQueryAugm.) │───▶│   LLM    │
│ (usuario)│    │ Chain  │  SafeguardAdvisor)  │                         │    │ (Ollama) │
└─────────┘    └────────┤                     │                         │    └──────────┘
                        └──────────┬──────────┴─────────────────────────┘
                                   │
                        ┌──────────▼──────────┐
                        │    PostgreSQL 16     │
                        │    + PgVector        │
                        │  (HNSW / Coseno)     │
                        │  1024 dimensiones    │
                        └─────────────────────┘
```

### Diferencias con Fase 3

| Característica | Fase 3 | Fase 4 |
|---|---|---|
| Vector Store | SimpleVectorStore (en memoria) | PgVector (PostgreSQL persistente) |
| Persistencia de vectores | Se pierde al reiniciar | Persiste entre reinicios |
| Chat Memory | H2 (archivo local) | PostgreSQL |
| Base de datos | H2 | PostgreSQL 16 + pgvector |
| RAG Advisor | QuestionAnswerAdvisor | QuestionAnswerAdvisor + PgVector |
| Índice vectorial | Ninguno (fuerza bruta) | HNSW (búsqueda aproximada eficiente) |
| Contenedores | No necesarios | Docker/Podman para PostgreSQL |

---

## Requisitos

- **Java 21+**
- **Maven 3.9+**
- **Docker** o **Podman** (para PostgreSQL + pgvector)
- **Ollama** con los modelos:
  - `llama3.2` (chat)
  - `mxbai-embed-large` (embeddings, 1024 dimensiones)

---

## Inicio Rápido

### 1. Levantar PostgreSQL con PgVector

#### Con Docker

```bash
cd fase4-spring-ai-ollama-pgvector
docker compose up -d
```

#### Con Podman

```bash
cd fase4-spring-ai-ollama-pgvector
podman compose up -d
# Si no tienes podman-compose:
podman run -d --name fase4-pgvector \
  -e POSTGRES_DB=fase4_ragdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -v fase4_pgvector_data:/var/lib/postgresql/data \
  pgvector/pgvector:pg16
```

#### Verificar que PostgreSQL está corriendo

**Linux / macOS:**
```bash
docker exec fase4-pgvector psql -U postgres -d fase4_ragdb -c "SELECT 1;"
# o con podman:
podman exec fase4-pgvector psql -U postgres -d fase4_ragdb -c "SELECT 1;"
```

**Windows (PowerShell):**
```powershell
docker exec fase4-pgvector psql -U postgres -d fase4_ragdb -c "SELECT 1;"
# o con podman:
podman exec fase4-pgvector psql -U postgres -d fase4_ragdb -c "SELECT 1;"
```

---

### 2. Instalar y verificar Ollama

```bash
# Verificar modelos instalados
ollama list

# Descargar modelos si no están:
ollama pull llama3.2
ollama pull mxbai-embed-large

# Iniciar Ollama (si no está corriendo)
ollama serve
```

---

### 3. Compilar y ejecutar

**Linux / macOS:**
```bash
# Desde la raíz del proyecto
mvn clean install -pl fase4-spring-ai-ollama-pgvector -am -DskipTests

# Ejecutar
cd fase4-spring-ai-ollama-pgvector
mvn spring-boot:run
```

**Windows (PowerShell / CMD):**
```powershell
# Desde la raíz del proyecto
mvn clean install -pl fase4-spring-ai-ollama-pgvector -am -DskipTests

# Ejecutar
cd fase4-spring-ai-ollama-pgvector
mvn spring-boot:run
```

---

### 4. Probar

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Script de prueba (Linux/macOS)**: `./test-api.sh`

---

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| **Chat** | | |
| `POST` | `/api/chat` | Chat simple (sesión auto-generada) |
| `POST` | `/api/chat/{sessionId}` | Chat con sesión específica |
| `DELETE` | `/api/chat/{sessionId}` | Borrar historial de sesión |
| `GET` | `/api/chat/status` | Estado del servicio de chat |
| **Búsqueda Semántica** | | |
| `GET` | `/api/buscar/status` | Estado del PgVectorStore |
| `POST` | `/api/buscar/demo` | Cargar documentos de ejemplo |
| `GET` | `/api/buscar?query=...&topK=4` | Buscar por similitud semántica |
| `POST` | `/api/buscar/pdf` | Indexar un archivo PDF |
| **RAG** | | |
| `POST` | `/api/rag/simple` | RAG con pipeline manual |
| `POST` | `/api/rag/advisor` | RAG con QuestionAnswerAdvisor |
| `POST` | `/api/rag/docs/cargar` | Cargar documentos Markdown |
| `POST` | `/api/rag/docs/preguntar` | Preguntar sobre documentación |
| `POST` | `/api/rag` | Alias de `/api/rag/simple` |

---

## Pruebas manuales con cURL

### 1. Estado de los servicios

**Linux / macOS:**
```bash
# Estado del chat
curl -s http://localhost:8080/api/chat/status | python3 -m json.tool

# Estado del vector store
curl -s http://localhost:8080/api/buscar/status | python3 -m json.tool
```

**Windows (PowerShell):**
```powershell
# Estado del chat
Invoke-RestMethod -Uri http://localhost:8080/api/chat/status | ConvertTo-Json

# Estado del vector store
Invoke-RestMethod -Uri http://localhost:8080/api/buscar/status | ConvertTo-Json
```

**Windows (CMD con curl):**
```cmd
curl -s http://localhost:8080/api/chat/status
curl -s http://localhost:8080/api/buscar/status
```

---

### 2. Chat simple

**Linux / macOS:**
```bash
curl -s -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué es PgVector y para qué sirve?"}' | python3 -m json.tool
```

**Windows (PowerShell):**
```powershell
$body = @{ message = "¿Qué es PgVector y para qué sirve?" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/chat `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 5
```

**Windows (CMD con curl):**
```cmd
curl -s -X POST http://localhost:8080/api/chat -H "Content-Type: application/json" -d "{\"message\": \"¿Qué es PgVector y para qué sirve?\"}"
```

---

### 3. Chat con sesión (memoria conversacional)

**Linux / macOS:**
```bash
# Primera pregunta en sesión "mi-sesion"
curl -s -X POST http://localhost:8080/api/chat/mi-sesion \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué es RAG?"}' | python3 -m json.tool

# Segunda pregunta (recuerda el contexto anterior)
curl -s -X POST http://localhost:8080/api/chat/mi-sesion \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Y cómo se implementa con Spring AI?"}' | python3 -m json.tool

# Borrar la sesión
curl -s -X DELETE http://localhost:8080/api/chat/mi-sesion | python3 -m json.tool
```

**Windows (PowerShell):**
```powershell
# Primera pregunta
$body = @{ message = "¿Qué es RAG?" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/chat/mi-sesion `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 5

# Segunda pregunta (recuerda el contexto)
$body = @{ message = "¿Y cómo se implementa con Spring AI?" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/chat/mi-sesion `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 5

# Borrar la sesión
Invoke-RestMethod -Method Delete -Uri http://localhost:8080/api/chat/mi-sesion | ConvertTo-Json
```

---

### 4. Cargar documentos demo en PgVector

**Linux / macOS:**
```bash
curl -s -X POST http://localhost:8080/api/buscar/demo | python3 -m json.tool
```

**Windows (PowerShell):**
```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/buscar/demo | ConvertTo-Json -Depth 5
```

**Windows (CMD):**
```cmd
curl -s -X POST http://localhost:8080/api/buscar/demo
```

---

### 5. Búsqueda semántica

**Linux / macOS:**
```bash
# Buscar por similitud (top 3 resultados)
curl -s "http://localhost:8080/api/buscar?query=embeddings%20vectores&topK=3" | python3 -m json.tool

# Buscar sobre RAG
curl -s "http://localhost:8080/api/buscar?query=retrieval%20augmented%20generation&topK=4" | python3 -m json.tool
```

**Windows (PowerShell):**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/buscar?query=embeddings vectores&topK=3" | ConvertTo-Json -Depth 5

Invoke-RestMethod -Uri "http://localhost:8080/api/buscar?query=retrieval augmented generation&topK=4" | ConvertTo-Json -Depth 5
```

**Windows (CMD):**
```cmd
curl -s "http://localhost:8080/api/buscar?query=embeddings%%20vectores&topK=3"
```

---

### 6. RAG simple (pipeline manual)

**Linux / macOS:**
```bash
curl -s -X POST http://localhost:8080/api/rag/simple \
  -H "Content-Type: application/json" \
  -d '{"query": "¿Qué son los embeddings y cómo se usan en IA?", "topK": 3}' | python3 -m json.tool
```

**Windows (PowerShell):**
```powershell
$body = @{ query = "¿Qué son los embeddings y cómo se usan en IA?"; topK = 3 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/rag/simple `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 10
```

**Windows (CMD):**
```cmd
curl -s -X POST http://localhost:8080/api/rag/simple -H "Content-Type: application/json" -d "{\"query\": \"¿Qué son los embeddings?\", \"topK\": 3}"
```

---

### 7. RAG con QuestionAnswerAdvisor

**Linux / macOS:**
```bash
curl -s -X POST http://localhost:8080/api/rag/advisor \
  -H "Content-Type: application/json" \
  -d '{"query": "¿Cómo funciona la búsqueda por similitud coseno?", "topK": 4}' | python3 -m json.tool
```

**Windows (PowerShell):**
```powershell
$body = @{ query = "¿Cómo funciona la búsqueda por similitud coseno?"; topK = 4 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/rag/advisor `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 10
```

**Windows (CMD):**
```cmd
curl -s -X POST http://localhost:8080/api/rag/advisor -H "Content-Type: application/json" -d "{\"query\": \"¿Cómo funciona la búsqueda por similitud coseno?\", \"topK\": 4}"
```

---

### 8. Cargar documentos Markdown para RAG

**Linux / macOS:**
```bash
curl -s -X POST http://localhost:8080/api/rag/docs/cargar \
  -H "Content-Type: application/json" \
  -d '{"path": "./data/docs"}' | python3 -m json.tool
```

**Windows (PowerShell):**
```powershell
$body = @{ path = "./data/docs" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/rag/docs/cargar `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 5
```

**Windows (CMD):**
```cmd
curl -s -X POST http://localhost:8080/api/rag/docs/cargar -H "Content-Type: application/json" -d "{\"path\": \"./data/docs\"}"
```

---

### 9. Preguntar sobre documentación cargada

**Linux / macOS:**
```bash
curl -s -X POST http://localhost:8080/api/rag/docs/preguntar \
  -H "Content-Type: application/json" \
  -d '{"query": "¿Qué es Spring AI y cómo se integra con RAG?", "topK": 5}' | python3 -m json.tool
```

**Windows (PowerShell):**
```powershell
$body = @{ query = "¿Qué es Spring AI y cómo se integra con RAG?"; topK = 5 } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/rag/docs/preguntar `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 10
```

**Windows (CMD):**
```cmd
curl -s -X POST http://localhost:8080/api/rag/docs/preguntar -H "Content-Type: application/json" -d "{\"query\": \"¿Qué es Spring AI y cómo se integra con RAG?\", \"topK\": 5}"
```

---

### 10. Indexar un PDF

**Linux / macOS:**
```bash
curl -s -X POST http://localhost:8080/api/buscar/pdf \
  -H "Content-Type: application/json" \
  -d '{"path": "./data/mi-documento.pdf", "sourceId": "manual-tecnico"}' | python3 -m json.tool
```

**Windows (PowerShell):**
```powershell
$body = @{ path = "./data/mi-documento.pdf"; sourceId = "manual-tecnico" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/buscar/pdf `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 5
```

---

## Flujo de prueba recomendado

```
1. Verificar servicios    → GET /api/chat/status + GET /api/buscar/status
2. Cargar documentos      → POST /api/buscar/demo
3. Buscar por similitud   → GET /api/buscar?query=embeddings&topK=3
4. RAG simple             → POST /api/rag/simple
5. RAG con Advisor        → POST /api/rag/advisor
6. Cargar Markdown        → POST /api/rag/docs/cargar  (path: ./data/docs)
7. Preguntar sobre docs   → POST /api/rag/docs/preguntar
8. Chat con memoria       → POST /api/chat/mi-sesion  (hacer 2+ preguntas)
```

---

## Configuración

### Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `OLLAMA_BASE_URL` | `http://localhost:11434` | URL de Ollama |
| `OLLAMA_CHAT_MODEL` | `llama3.2` | Modelo de chat |
| `OLLAMA_EMBEDDING_MODEL` | `mxbai-embed-large` | Modelo de embeddings (1024 dims) |
| `POSTGRES_HOST` | `localhost` | Host de PostgreSQL |
| `POSTGRES_PORT` | `5432` | Puerto de PostgreSQL |
| `POSTGRES_DB` | `fase4_ragdb` | Base de datos |
| `POSTGRES_USER` | `postgres` | Usuario |
| `POSTGRES_PASSWORD` | `postgres` | Contraseña |
| `APP_CHAT_MEMORY_REPOSITORY` | `jdbc` | `jdbc` (PostgreSQL) o `memory` |
| `APP_CHAT_MEMORY_MAX_MESSAGES` | `20` | Máximo de mensajes en memoria |

### PgVector

La tabla `vector_store` se crea automáticamente con `initialize-schema=true`. Usa índice **HNSW** con **distancia coseno** y dimensiones **1024** (mxbai-embed-large).

Para verificar la tabla creada:
```bash
# Docker
docker exec fase4-pgvector psql -U postgres -d fase4_ragdb -c "\dt"
docker exec fase4-pgvector psql -U postgres -d fase4_ragdb -c "SELECT count(*) FROM vector_store;"

# Podman
podman exec fase4-pgvector psql -U postgres -d fase4_ragdb -c "\dt"
podman exec fase4-pgvector psql -U postgres -d fase4_ragdb -c "SELECT count(*) FROM vector_store;"
```

---

## Gestión de contenedores

### Docker

```bash
# Levantar
docker compose up -d

# Ver logs
docker compose logs -f postgres

# Estado
docker compose ps

# Detener (mantiene datos)
docker compose down

# Detener y borrar datos (volúmenes)
docker compose down -v

# Reiniciar
docker compose restart
```

### Podman

```bash
# Con podman-compose
podman compose up -d
podman compose logs -f postgres
podman compose ps
podman compose down
podman compose down -v

# Sin podman-compose (contenedor individual)
podman run -d --name fase4-pgvector \
  -e POSTGRES_DB=fase4_ragdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -v fase4_pgvector_data:/var/lib/postgresql/data \
  pgvector/pgvector:pg16

# Ver logs
podman logs -f fase4-pgvector

# Detener
podman stop fase4-pgvector

# Eliminar contenedor
podman rm fase4-pgvector

# Eliminar contenedor + volumen
podman rm fase4-pgvector && podman volume rm fase4_pgvector_data
```

---

## Troubleshooting

| Problema | Solución |
|---|---|
| `Connection refused` en `/api/chat` | Verificar que **Ollama** esté corriendo: `ollama serve` |
| `Connection refused` en PgVector | Verificar contenedor: `docker ps` o `podman ps` |
| `Table vector_store does not exist` | Reiniciar app (se crea con `initialize-schema=true`) |
| Embeddings de dimensión incorrecta | Asegurar que el modelo sea `mxbai-embed-large` (1024 dims) |
| Puerto 5432 ocupado | Cambiar en `docker-compose.yml` y `application.yml`, o detener otro PostgreSQL |
| Búsqueda retorna 0 resultados | Ejecutar primero `POST /api/buscar/demo` para indexar documentos |
