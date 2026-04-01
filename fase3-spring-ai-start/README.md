# Fase 3 Spring AI - Chatbot con Memoria (Start)

## 📚 Labs incluidos

- **Lab 7:** Chatbot con memoria en RAM (`InMemoryChatMemory`)
- **Lab 8:** Chatbot con memoria persistente (`JdbcChatMemory` + H2)
- **Lab 9:** Búsqueda semántica en memoria con `SimpleVectorStore`
- **Lab 10:** Endpoint `GET /api/buscar` para documentos similares
- **Lab RAG:** Endpoint `POST /api/rag` para responder con contexto recuperado
- **Reto:** Cargar un PDF con `TikaDocumentReader` y buscar en él

## 🎯 Objetivo

Implementar un chatbot con memoria conversacional y búsqueda semántica usando **Spring AI**, soportando múltiples proveedores de IA (OpenAI, Anthropic, Gemini).

Temas teóricos que esta demo cubre:
- **Embeddings:** texto -> vectores numéricos.
- **Similitud coseno:** comparación de cercanía semántica entre vectores.
- **Modelos de embedding:** motor que genera los vectores.
- **Vector databases:** contraste entre `SimpleVectorStore`, `PgVector` y `Chroma`.

## 📋 Pre-requisitos

1. Java 21+
2. Maven 3.8+
3. API Key de al menos uno de estos proveedores:
   - OpenAI: https://platform.openai.com/api-keys
   - Anthropic: https://console.anthropic.com/
   - Google Vertex AI: https://cloud.google.com/vertex-ai

## 🚀 Configuración

### 1. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto o exporta las variables:

```bash
# Para OpenAI
export OPENAI_API_KEY=sk-...

# O para Anthropic
export ANTHROPIC_API_KEY=sk-ant-...

# O para Google Vertex AI
export VERTEX_AI_PROJECT_ID=tu-proyecto-id
```

### 2. Seleccionar proveedor

Edita `src/main/resources/application.yml` y descomenta el bloque del proveedor que deseas usar.

Por defecto está configurado OpenAI.

## 📝 Lab 7: Memoria en RAM

### Objetivo
Implementar un chatbot con memoria conversacional básica usando `InMemoryChatMemory`.

### Tareas (TODOs)

1. **ChatConfig.java**: Crear bean de `InMemoryChatMemory`
2. **ChatService.java**: Inicializar `ChatClient` con advisor de memoria
3. **ChatService.java**: Implementar método `chat(String mensaje)`
4. **ChatController.java**: Crear endpoint `POST /api/chat`

### Ejecutar

```bash
mvn spring-boot:run
```

### Probar

```bash
# Enviar primer mensaje
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Carlos"}'

# El bot debe recordar el nombre
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}'
```

## 📝 Lab 8: Memoria Persistente

### Objetivo
Implementar persistencia de conversaciones en base de datos H2 usando `JdbcChatMemory`.

### Tareas (TODOs)

1. **ChatConfigPersistent.java**: Crear bean de `JdbcChatMemory`
2. Verificar que la memoria persiste al reiniciar la aplicación

### Ejecutar con perfil persistente

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=persistent
```

### Verificar persistencia

1. Envía algunos mensajes
2. Reinicia la aplicación
3. Continúa la conversación - debe recordar el contexto anterior

### Ver la base de datos

Abre http://localhost:8080/h2-console

- **JDBC URL:** `jdbc:h2:file:./data/chatbot-memory`
- **Username:** `sa`
- **Password:** (dejar vacío)

## 🎯 RETO: Multi-sesión

### Objetivo
Crear endpoint que soporte múltiples usuarios/sesiones simultáneas.

### Tareas (TODOs)

1. **ChatService.java**: Implementar método `chat(String sessionId, String mensaje)`
2. **ChatController.java**: Crear endpoint `POST /api/chat/{sessionId}`

### Probar multi-sesión

```bash
# Usuario 1
curl -X POST http://localhost:8080/api/chat/user-123 \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Carlos"}'

# Usuario 2
curl -X POST http://localhost:8080/api/chat/user-456 \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Ana"}'

# Verificar que cada uno tiene su propio contexto
curl -X POST http://localhost:8080/api/chat/user-123 \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}'
# Respuesta esperada: "Carlos"

curl -X POST http://localhost:8080/api/chat/user-456 \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}'
# Respuesta esperada: "Ana"
```

## 🧪 Testing completo

Usa el script de pruebas incluido:

```bash
chmod +x test-api.sh
./test-api.sh
```

En PowerShell:

```powershell
./test-api.ps1
```

## 🧭 Lab 9: Búsqueda semántica con `SimpleVectorStore`

### Objetivo
Indexar documentos en memoria y recuperar los más similares según una consulta.

### Tareas (TODOs)

1. **SemanticSearchService.java**: inyectar/configurar `EmbeddingModel`
2. **SemanticSearchService.java**: crear `SimpleVectorStore`
3. **SemanticSearchService.java**: cargar documentos teóricos (embeddings, coseno, modelos, vector DB)
4. **BusquedaController.java**: habilitar `POST /api/buscar/demo`

### Probar

```bash
curl -X POST http://localhost:8080/api/buscar/demo
```

## 🧭 Lab 10: Endpoint `/buscar`

### Objetivo
Exponer un endpoint REST que haga búsqueda por similitud semántica.

### Tareas (TODOs)

1. **SemanticSearchService.java**: implementar método `buscar(query, topK)`
2. **BusquedaController.java**: implementar `GET /api/buscar?query=...&topK=...`
3. Limitar `topK` a un rango razonable (ej. 1..20)

### Probar

```bash
curl "http://localhost:8080/api/buscar?query=similitud%20coseno&topK=4"
curl "http://localhost:8080/api/buscar?query=pgvector%20chroma&topK=4"
```

## 🧩 Reto: Cargar PDF con `TikaDocumentReader`

### Objetivo
Ingerir un PDF local, convertirlo en documentos indexables y consultarlo por similitud.

### Tareas (TODOs)

1. Agregar dependencias de vector store y Tika compatibles con la versión de Spring AI usada
2. **SemanticSearchService.java**: implementar `cargarPdf(path, sourceId)`
3. **BusquedaController.java**: completar `POST /api/buscar/pdf`

### Probar

```bash
curl -X POST http://localhost:8080/api/buscar/pdf \
  -H "Content-Type: application/json" \
  -d '{"path":"/Users/josediaz/Projects/JoeDayz/ia-con-java/docs/01-AI_Developer_Blueprint.pdf","sourceId":"ai-blueprint"}'

curl "http://localhost:8080/api/buscar?query=developer%20productivity&topK=5"
```

## 🧠 Lab RAG: retrieval + prompt + generation

### Objetivo
Dar el salto desde la búsqueda semántica hacia un RAG básico:
1. recuperar fragmentos relevantes,
2. meterlos en el prompt,
3. generar una respuesta con citas.

### Tareas (TODOs)

1. **SemanticSearchService.java**: exponer recuperación de `Document` crudos
2. **RagService.java**: construir prompt con contexto enumerado `[1]`, `[2]`, `[3]`
3. **RagController.java**: crear `POST /api/rag`
4. Pedir al modelo que cite usando los índices del contexto

### Probar

```bash
curl -X POST http://localhost:8080/api/rag \
  -H "Content-Type: application/json" \
  -d '{"question":"Explica la relación entre embeddings y similitud coseno","topK":4}'
```

## 🔍 Conceptos clave de Spring AI

### ChatMemory
Almacena el historial de conversación:
- `InMemoryChatMemory`: Volátil (se pierde al reiniciar)
- `JdbcChatMemory`: Persistente (sobrevive reinicios)

### MessageChatMemoryAdvisor
Componente que:
1. Recupera historial previo de `ChatMemory`
2. Lo agrega al contexto antes de enviar al LLM
3. Guarda nuevos mensajes en `ChatMemory`

### Parámetros importantes
```java
new MessageChatMemoryAdvisor(chatMemory, sessionId, windowSize)
```
- `sessionId`: Identificador único de la conversación
- `windowSize`: Número máximo de mensajes a recordar

### Embeddings y similitud coseno
- Un embedding es un vector de alta dimensión que representa significado.
- La similitud coseno compara direcciones entre vectores, no su magnitud.
- En búsqueda semántica, se recuperan textos con mayor similitud a la consulta.

### Vector databases (visión práctica)
- `SimpleVectorStore`: memoria local, ideal para clase/labs.
- `PgVector`: persistencia en PostgreSQL para entornos reales.
- `Chroma`: base vectorial especializada, muy usada en prototipos RAG.

### Qué todavía NO estamos haciendo
- `PgVector` o `Chroma` reales en runtime
- reranking
- chunking avanzado
- metadata filtering sofisticado
- citas verificadas automáticamente por el pipeline

## 📚 Recursos

- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/)
- [Chat Memory Guide](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory)
- [OpenAI Spring Boot](https://docs.spring.io/spring-ai/reference/api/clients/openai-chat.html)
- [Anthropic Spring Boot](https://docs.spring.io/spring-ai/reference/api/clients/anthropic-chat.html)

## ⚠️ Troubleshooting

### Error: "No ChatModel bean found"
- Verifica que configuraste correctamente el API key en `application.yml`
- Asegúrate de tener las variables de entorno definidas

### Error: "Table not found"
- Spring AI crea las tablas automáticamente
- Verifica que `spring.jpa.hibernate.ddl-auto=update` esté configurado

### La memoria no persiste
- Verifica que estás usando el perfil `persistent`
- Revisa que el archivo H2 se está creando en `./data/chatbot-memory.mv.db`
