# Guía del Instructor: RAG con Spring AI + MongoDB Atlas

## Objetivo de la Demo

Mostrar cómo construir un sistema **RAG (Retrieval-Augmented Generation)** que:
1. Carga documentos (PDFs, TXT, MD, JSON, CSV, etc.) en un **vector store** de MongoDB Atlas.
2. Cuando el usuario hace una pregunta, busca los fragmentos más relevantes por **similitud semántica** (vector search).
3. Envía esos fragmentos como contexto al **LLM (GPT-4o)** para generar una respuesta fundamentada en los documentos.

> **Concepto clave para los alumnos:** Sin RAG, el LLM solo sabe lo que aprendió en su entrenamiento. Con RAG, le "inyectamos" conocimiento propio (PDFs de la empresa, documentación interna, etc.) en tiempo de consulta.

---

## Stack Tecnológico

| Componente | Tecnología | Versión |
|---|---|---|
| Framework | Spring Boot | 3.5.5 |
| IA / LLM | Spring AI + OpenAI GPT-4o | 1.0.1 |
| Vector Store | MongoDB Atlas (local en Docker) | latest |
| PDF parsing | Apache PDFBox | 3.0.3 |
| Java | JDK | 21 |
| Automatización | `just` | - |

---

## Arquitectura de la Demo

```
┌─────────────┐     GET /api/docs/load      ┌──────────────────┐
│  Instructor  │ ──────────────────────────► │ DocsLoaderController │
│  (browser)   │                             └────────┬─────────┘
│              │                                      │
│              │                             ┌────────▼─────────┐
│              │                             │ DocsLoaderService │
│              │                             │  - Lee docs/     │
│              │                             │  - Extrae texto  │
│              │                             │  - Chunking      │
│              │                             └────────┬─────────┘
│              │                                      │ vectorStore.add()
│              │                             ┌────────▼─────────┐
│              │                             │  MongoDB Atlas    │
│              │                             │  (Vector Store)   │
│              │                             │  embeddings +     │
│              │                             │  texto original   │
│              │                             └────────┬─────────┘
│              │                                      │
│              │     GET /faq?message=...    ┌────────▼─────────┐
│              │ ──────────────────────────► │  RagController    │
│              │                             │  ChatClient +     │
│              │ ◄────────────────────────── │  QuestionAnswer   │
│  Respuesta   │     respuesta generada      │  Advisor          │
└─────────────┘                             └──────────────────┘
```

### Flujo paso a paso

1. **Carga de documentos** → `GET /api/docs/load`
   - `DocsLoaderService` recorre la carpeta `docs/`
   - Extrae texto de PDFs con PDFBox (página por página)
   - Lee archivos de texto plano (TXT, MD, JSON, CSV, etc.)
   - Divide el texto en **chunks** de máx. 2000 tokens (~8000 caracteres)
   - Envía cada chunk al `VectorStore` → Spring AI genera el **embedding** vía OpenAI y lo almacena en MongoDB

2. **Consulta RAG** → `GET /faq?message=Tu pregunta`
   - El `QuestionAnswerAdvisor` intercepta la pregunta
   - Busca en MongoDB los chunks más similares al embedding de la pregunta (**vector search**)
   - Inyecta esos chunks como contexto en el prompt del LLM
   - GPT-4o genera una respuesta **basada en los documentos cargados**

---

## Estructura del Código (4 archivos clave)

### 1. `RagAppApplication.java` — Punto de entrada
Clase estándar `@SpringBootApplication`. La autoconfiguración de Spring AI se encarga de crear los beans de `ChatClient`, `VectorStore`, y el modelo de embeddings.

### 2. `RagController.java` — Endpoint de consulta RAG
```java
@RestController
public class RagController {
    private final ChatClient chatClient;

    public RagController(ChatClient.Builder builder, VectorStore vectorStore) {
        // El QuestionAnswerAdvisor es la pieza clave del RAG
        this.chatClient = builder
            .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
            .build();
    }

    @GetMapping("/faq")
    public String faq(@RequestParam(value = "message", ...) String message) {
        return chatClient.prompt().user(message).call().content();
    }
}
```

**Puntos a destacar en clase:**
- `QuestionAnswerAdvisor` es el "advisor" de Spring AI que implementa RAG automáticamente
- No necesitas escribir código de búsqueda vectorial ni de construcción de prompts — el advisor lo hace
- La inyección del `VectorStore` conecta con MongoDB Atlas

### 3. `DocsLoaderController.java` — Trigger de carga
Endpoint simple `GET /api/docs/load` que invoca al servicio.

### 4. `DocsLoaderService.java` — Lógica de ingestión
**Puntos clave para explicar:**
- **Chunking manual**: divide texto en fragmentos de ~2000 tokens (estimación: 1 token ≈ 4 chars)
- **Soporte PDF**: usa PDFBox para extraer texto página por página
- **Metadata**: cada chunk lleva `sourceName`, `path` y `page` (para PDFs)
- **Batching**: envía documentos al vector store en lotes de 100
- **Validaciones**: solo UTF-8, extensiones soportadas, manejo de archivos vacíos

### 5. `application.properties` — Configuración
```properties
spring.ai.openai.api-key=${OPENAI_API_KEY:}
spring.ai.openai.chat.options.model=gpt-4o
spring.ai.vectorstore.mongodb.initialize-schema=true
spring.data.mongodb.uri=mongodb://localhost:27017/rag?directConnection=true
spring.data.mongodb.database=rag
```

**Destacar:**
- `initialize-schema=true` → crea automáticamente el índice vectorial en MongoDB
- La API key viene de variable de entorno (nunca hardcodeada)
- Se usa `mongodb-atlas-local` en Docker (no el `mongo` estándar) porque soporta vector search

---

## Pre-requisitos para la Demo

1. **Java 21** instalado
2. **Docker Desktop** corriendo (o Podman)
3. **`just`** instalado: `brew install just`
4. Archivo `~/.api-keys` con:
   ```bash
   export OPENAI_API_KEY="sk-..."
   ```
5. Un PDF en la carpeta `docs/` (ya viene uno de ejemplo: `BASES+CLOUDF_20260224_175315_332.pdf`)

---

## Paso a Paso para la Demo en Clase

### Paso 1: Verificar pre-requisitos
```bash
cd rags-demo/spring-ai-rag-mongodb
just check-prereqs
```

### Paso 2: Levantar MongoDB
```bash
just mongo-up
```
> Explicar: se usa la imagen `mongodb/mongodb-atlas-local` que incluye soporte para **Atlas Vector Search** localmente.

### Paso 3: Arrancar la aplicación
```bash
just run
```
Esto:
- Carga las variables de entorno desde `~/.api-keys`
- Ejecuta `./mvnw spring-boot:run`
- Al arrancar, `initialize-schema=true` crea el índice vectorial en MongoDB

### Paso 4: Cargar documentos
```bash
just load-docs
```
O desde el navegador: `http://localhost:8080/api/docs/load`

**Mostrar la respuesta** — indica cuántos archivos se procesaron y cuántos chunks se generaron.

> **Momento pedagógico:** Explicar qué es un chunk, por qué se divide el texto, y qué son los embeddings que se almacenan junto al texto.

### Paso 5: Hacer preguntas RAG
```bash
just ask "¿Qué dice el documento sobre cloud foundations?"
```
O desde el navegador:
```
http://localhost:8080/faq?message=¿Qué dice el documento sobre cloud foundations?
```

> **Momento pedagógico:** Comparar la respuesta con y sin documentos cargados. Sin docs, el LLM responde con conocimiento general. Con docs, responde basándose en el PDF cargado.

### Paso 6: (Opcional) Verificar en MongoDB
```bash
docker exec -it rag-mongodb mongosh
use rag
db.vector_store.find().limit(1).pretty()
```
Mostrar que cada documento tiene:
- `content`: el texto del chunk
- `embedding`: un array de ~1536 floats (el vector)
- `metadata`: source, path, page

### Paso 7: Limpiar
```bash
just mongo-down
# O para purgar datos:
just mongo-reset
```

---

## Flujo Manual (sin `just`)

Si `just` no está disponible:

```bash
# 1. Cargar variables de entorno
source ~/.api-keys

# 2. Levantar MongoDB
docker compose up -d mongodb

# 3. Arrancar la app
./mvnw spring-boot:run

# 4. Cargar docs (otra terminal)
curl http://localhost:8080/api/docs/load

# 5. Preguntar
curl "http://localhost:8080/faq?message=¿Qué dice el documento?"

# 6. Parar
docker compose down
```

---

## Preguntas Sugeridas para la Demo

| Pregunta | Lo que demuestra |
|---|---|
| `¿Qué dice el documento sobre cloud foundations?` | RAG responde con info del PDF |
| `¿Quién es el autor del documento?` | Extracción de metadatos del PDF |
| `Explain quantum computing` | Sin relación con los docs → el LLM responde con conocimiento general (mostrar la diferencia) |
| `Resume el documento en 3 puntos` | Capacidad de síntesis sobre el contenido cargado |

---

## Conceptos Clave para Explicar en Clase

### 1. ¿Qué es RAG?
**Retrieval-Augmented Generation**: técnica que combina búsqueda de información (retrieval) con generación de texto (LLM). En vez de fine-tunear el modelo, le das contexto relevante en cada consulta.

### 2. ¿Qué es un Embedding?
Un vector numérico (array de ~1536 floats con OpenAI) que representa el **significado semántico** del texto. Textos con significado similar tienen vectores cercanos en el espacio vectorial.

### 3. ¿Qué es un Vector Store?
Base de datos optimizada para buscar por **similitud de vectores**. MongoDB Atlas soporta esto nativamente con Atlas Vector Search. Alternativas: pgvector, Pinecone, Elasticsearch.

### 4. ¿Qué es el Chunking?
Dividir documentos grandes en fragmentos pequeños para:
- Que el embedding represente un concepto específico (no un doc entero)
- Respetar los límites de tokens del LLM
- Mejorar la relevancia de la búsqueda

### 5. ¿Cómo funciona el `QuestionAnswerAdvisor`?
Es un **advisor pattern** de Spring AI que intercepta cada prompt:
1. Toma la pregunta del usuario
2. Genera su embedding
3. Busca los K chunks más similares en el vector store
4. Los añade al prompt como contexto
5. Envía el prompt enriquecido al LLM

---

## Posibles Problemas y Soluciones

| Problema | Solución |
|---|---|
| `Connection refused` en MongoDB | Verificar: `docker ps` → ¿está corriendo `rag-mongodb`? |
| `OPENAI_API_KEY` vacía | `source ~/.api-keys` antes de `just run` |
| `just: command not found` | `brew install just` |
| Docker no arranca | Abrir Docker Desktop primero |
| PDF no se carga | Verificar que está en `docs/` y que es un PDF válido con texto (no escaneado como imagen) |
| Respuestas genéricas (no usan los docs) | ¿Se ejecutó `just load-docs` primero? Verificar en MongoDB que hay datos |

---

## Extensiones para Ejercicios

1. **Agregar más documentos**: Pedir a los alumnos que pongan sus propios PDFs/TXTs en `docs/` y recarguen
2. **Comparar con otros vector stores**: El proyecto `rags-demo/` tiene demos con pgvector, Pinecone y Elasticsearch
3. **Modificar el chunking**: Cambiar `MAX_TOKENS_PER_CHUNK` y observar cómo afecta la calidad de las respuestas
4. **Agregar un system prompt**: Modificar `RagController` para incluir instrucciones específicas al LLM (ej: "responde siempre en español")

---

## Tiempo Estimado de la Demo

| Actividad | Minutos |
|---|---|
| Explicación teórica (RAG, embeddings, vector store) | 10-15 |
| Mostrar el código y la arquitectura | 10 |
| Demo en vivo (levantar, cargar, preguntar) | 10 |
| Explorar MongoDB y ver los embeddings | 5 |
| Preguntas y ejercicios | 10 |
| **Total** | **~45-50 min** |
