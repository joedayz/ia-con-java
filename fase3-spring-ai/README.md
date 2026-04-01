# Fase 3 Spring AI - Chatbot con Memoria (SOLUCIÓN)

## ✅ Solución completa de Labs 7, 8, 9, 10 y Reto

Este proyecto contiene la **implementación completa** de los siguientes labs:

- **Lab 7:** ✅ Chatbot con memoria en RAM (`InMemoryChatMemory`)
- **Lab 8:** ✅ Chatbot con memoria persistente (`JdbcChatMemory` + H2)
- **Lab 9:** ✅ Búsqueda semántica en memoria (`SimpleVectorStore`)
- **Lab 10:** ✅ Endpoint de búsqueda `GET /api/buscar`
- **Lab RAG:** ✅ Endpoint `/api/rag` con retrieval + grounding + generación
- **Reto:** ✅ Carga de PDF con `TikaDocumentReader` + búsqueda semántica

## 🎯 Características implementadas

### ✅ Lab 7: Memoria en RAM
- Bean de `InMemoryChatMemory` configurado
- `ChatService` con `ChatClient` y `MessageChatMemoryAdvisor`
- Endpoint `POST /api/chat` funcional
- Memoria conversacional que persiste durante la sesión

### ✅ Lab 8: Memoria Persistente
- Bean de `JdbcChatMemory` con H2 Database
- Perfil `persistent` activable
- Conversaciones que sobreviven al reinicio
- H2 Console habilitada para inspección

### ✅ Reto: Multi-sesión
- Endpoint `POST /api/chat/{sessionId}` implementado
- Soporte para múltiples usuarios simultáneos
- Cada sesión con historial independiente
- Endpoint `DELETE /api/chat/{sessionId}` para limpiar

### ✅ Lab 9 y 10: Embeddings + búsqueda semántica
- `SimpleVectorStore` en memoria para indexación rápida
- Endpoint `POST /api/buscar/demo` para cargar documentos teóricos
- Endpoint `GET /api/buscar?query=...&topK=...` para similitud semántica
- Soporte para búsqueda de conceptos: embeddings, coseno, modelos, vector DB

### ✅ Reto PDF: TikaDocumentReader
- Endpoint `POST /api/buscar/pdf` para indexar un PDF local
- Lectura del documento con `TikaDocumentReader`
- Fragmentos indexados con embeddings para luego consultarlos por similitud

### ✅ RAG básico en memoria
- Endpoint `POST /api/rag`
- Recupera fragmentos desde `SimpleVectorStore`
- Inyecta el contexto en el prompt
- Genera respuesta del LLM citando `[1]`, `[2]`, `[3]`

## 🚀 Uso

### 1. Configurar API Key (según perfil)

```bash
# Perfil OpenAI
export OPENAI_API_KEY=sk-...

# Perfil Anthropic
export ANTHROPIC_API_KEY=sk-ant-...

# Perfil Vertex AI
export VERTEX_AI_PROJECT_ID=tu-proyecto-id
export VERTEX_AI_LOCATION=us-central1
```

### 2. Ejecutar la aplicación

**OpenAI (perfil por defecto):**
```bash
mvn spring-boot:run
```

**Anthropic:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=anthropic
```

**Vertex AI:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=vertex
```

**Modo producción (memoria persistente + proveedor):**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=openai,persistent

# Otras combinaciones:
mvn spring-boot:run -Dspring-boot.run.profiles=anthropic,persistent
mvn spring-boot:run -Dspring-boot.run.profiles=vertex,persistent
```

### 3. Acceder a Swagger UI 📚

**¡NUEVO!** Documentación interactiva de la API con Swagger UI:

```
🌐 Swagger UI:  http://localhost:8080/swagger-ui.html
📄 OpenAPI JSON: http://localhost:8080/v3/api-docs
```

**Ventajas de usar Swagger UI:**
- ✅ **Documentación visual** de todos los endpoints
- ✅ **Probar la API directamente** desde el navegador
- ✅ **Ver ejemplos** de requests y responses
- ✅ **Entender los parámetros** sin leer código
- ✅ **Exportar la especificación OpenAPI** para clientes

**Endpoints disponibles en Swagger:**
- 💬 **Chat:** `/api/chat` (simple y multi-sesión)
- 🔍 **Búsqueda Semántica:** `/api/buscar` (demo, PDF, búsqueda)
- 🧠 **RAG:** `/api/rag` (retrieval + generación)

**Flujo recomendado para estudiantes:**
1. Abrir http://localhost:8080/swagger-ui.html
2. Probar POST `/api/chat` → Enviar "Hola, me llamo Carlos"
3. Probar POST `/api/buscar/demo` → Indexar documentos
4. Probar GET `/api/buscar?query=similitud coseno&topK=3`
5. Probar POST `/api/rag` → Pregunta con contexto

### 4. Probar los endpoints (alternativa: curl)

**Chat simple (Lab 7):**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Carlos"}'

curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}'
# Respuesta esperada: "Carlos"
```

**Chat multi-sesión (Reto):**
```bash
# Usuario 1
curl -X POST http://localhost:8080/api/chat/user-123 \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Carlos"}'

# Usuario 2
curl -X POST http://localhost:8080/api/chat/user-456 \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Ana"}'

# Verificar contextos independientes
curl -X POST http://localhost:8080/api/chat/user-123 \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}'
# Respuesta: "Carlos"

curl -X POST http://localhost:8080/api/chat/user-456 \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es mi nombre?"}'
# Respuesta: "Ana"
```

**Limpiar sesión:**
```bash
curl -X DELETE http://localhost:8080/api/chat/user-123
```

**Indexar documentos teóricos (Lab 9):**
```bash
curl -X POST http://localhost:8080/api/buscar/demo
```

**Buscar por similitud semántica (Lab 10):**
```bash
curl "http://localhost:8080/api/buscar?query=similitud%20coseno&topK=4"
curl "http://localhost:8080/api/buscar?query=pgvector%20chroma&topK=4"
```

**Reto: cargar PDF con Tika y buscar en él:**
```bash
curl -X POST http://localhost:8080/api/buscar/pdf \
  -H "Content-Type: application/json" \
  -d '{"path":"/Users/josediaz/Projects/JoeDayz/ia-con-java/docs/01-AI_Developer_Blueprint.pdf","sourceId":"ai-blueprint"}'

curl "http://localhost:8080/api/buscar?query=developer%20productivity&topK=5"
```

**RAG completo sobre el contexto recuperado:**
```bash
curl -X POST http://localhost:8080/api/rag \
  -H "Content-Type: application/json" \
  -d '{"question":"Explica que es la similitud coseno y como se relaciona con embeddings","topK":4}'
```

### 4. Testing automatizado

Ejecuta el script de pruebas completo:

```bash
chmod +x test-api.sh
./test-api.sh
```

En PowerShell:

```powershell
./test-api.ps1
```

## 🗄️ Base de datos (Lab 8)

Con el perfil `persistent` activo:

**H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/chatbot-memory`
- Username: `sa`
- Password: (vacío)

**Tablas creadas:**
- `CHAT_MEMORY`: Almacena los mensajes por sesión

**Consulta SQL de ejemplo:**
```sql
SELECT * FROM CHAT_MEMORY ORDER BY TIMESTAMP DESC;
```

## 🔧 Cambiar proveedor de IA

La configuración ahora está separada por perfiles:

- `src/main/resources/application-openai.yml`
- `src/main/resources/application-anthropic.yml`
- `src/main/resources/application-vertex.yml`

Selecciona el perfil al ejecutar:

```bash
# OpenAI (default)
mvn spring-boot:run

# Anthropic
mvn spring-boot:run -Dspring-boot.run.profiles=anthropic

# Vertex
mvn spring-boot:run -Dspring-boot.run.profiles=vertex
```

## 📁 Estructura del proyecto

```
fase3-spring-ai/
├── src/main/java/com/joedayz/ia/springai/
│   ├── ChatbotApplication.java          # Main de Spring Boot
│   ├── config/
│   │   ├── ChatConfig.java              # ✅ InMemoryChatMemory (Lab 7)
│   │   └── ...
│   ├── controller/
│   │   └── ChatController.java          # ✅ Endpoints REST
│   ├── service/
│   │   └── ChatService.java             # ✅ Lógica de negocio
│   └── dto/
│       ├── ChatRequest.java
│       └── ChatResponse.java
├── src/main/resources/
│   ├── application.yml                   # Configuración base
│   ├── application-openai.yml            # Perfil OpenAI
│   ├── application-anthropic.yml         # Perfil Anthropic
│   └── application-vertex.yml            # Perfil Vertex
├── pom.xml
├── README.md
├── test-api.sh                           # Script de pruebas
└── test-api.ps1                          # Script de pruebas PowerShell
```

## 🎓 Conceptos de Spring AI aplicados

### 1. ChatClient
Builder fluido para interactuar con LLMs:
```java
chatClient.prompt()
    .advisors(...)
    .user(mensaje)
    .call()
    .content();
```

### 2. MessageChatMemoryAdvisor
Gestiona automáticamente la memoria:
- Recupera historial de `ChatMemory`
- Lo inyecta en el contexto
- Guarda nuevos mensajes

### 3. ChatMemory
Almacena conversaciones:
- `InMemoryChatMemory`: En RAM (desarrollo)
- `JdbcChatMemory`: En BD (producción)

### 4. Multi-Session Support
Cada `sessionId` tiene su propio historial:
```java
.advisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 20))
```

### 5. Embeddings y búsqueda vectorial (Labs 9 y 10)
- Un **embedding model** convierte texto a vectores numéricos.
- `SimpleVectorStore` guarda esos vectores en memoria.
- La búsqueda usa **similitud coseno** para traer los fragmentos más cercanos.

### 6. Vector databases (marco teórico)
- `SimpleVectorStore`: ideal para demos/labs en memoria.
- `PgVector`: opción SQL persistente para producción.
- `Chroma`: base vectorial dedicada, simple de levantar para prototipos RAG.

### 7. Qué sí hace y qué no hace este RAG
- ✅ Recupera fragmentos semánticamente similares.
- ✅ Mete el contexto recuperado dentro del prompt.
- ✅ Genera respuesta final citando contexto.
- ❌ No hace reranking.
- ❌ No hace chunking avanzado.
- ❌ No usa metadata filtering sofisticado.
- ❌ No persiste embeddings en `PgVector` o `Chroma` todavía.

## 📊 Comparación: InMemory vs Jdbc

| Característica | InMemoryChatMemory | JdbcChatMemory |
|---------------|-------------------|----------------|
| **Persistencia** | ❌ Se pierde al reiniciar | ✅ Sobrevive reinicios |
| **Velocidad** | ⚡ Muy rápida | 🐢 Ligeramente más lenta |
| **Escalabilidad** | ❌ Una instancia | ✅ Múltiples instancias |
| **Casos de uso** | Dev, testing, demos | Producción, auditoría |

## 🚦 Testing de calidad

El proyecto incluye casos de prueba para:
- ✅ Memoria conversacional (Lab 7)
- ✅ Persistencia entre reinicios (Lab 8)
- ✅ Múltiples sesiones independientes (Reto)
- ✅ Limpieza de sesiones

## 📚 Referencias

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Chat Memory Guide](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory)
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai)

## 💡 Tips de producción

1. **Usar JdbcChatMemory en producción** para persistencia
2. **Configurar límite de ventana** (`windowSize`) apropiado
3. **Implementar limpieza periódica** de sesiones antiguas
4. **Monitorear uso de tokens** con Actuator
5. **Usar perfiles de Spring** para diferentes ambientes

## ⚠️ Notas importantes

- **Spring AI está en milestone** (versión 1.0.0-M1)
- La API puede cambiar en versiones futuras
- Para producción, usar versiones estables cuando estén disponibles
- Los modelos pueden tener limitaciones de tokens
