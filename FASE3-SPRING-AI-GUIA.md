# Nuevos Proyectos: Fase 3 Spring AI

## 🎉 ¡Implementación completa con Spring AI!

Se han creado dos nuevos proyectos que implementan los Labs 7, 8, 9, 10 y el Reto usando **Spring AI** en lugar de la implementación custom:

### 📂 Proyectos creados

1. **`fase3-spring-ai-start`** - Proyecto con TODOs para que los estudiantes completen
2. **`fase3-spring-ai`** - Solución completa implementada

## ✨ Características

### ✅ Lab 7: Memoria en RAM
- `InMemoryChatMemory` con Spring AI
- `ChatClient` con `MessageChatMemoryAdvisor`
- Endpoint REST `POST /api/chat`

### ✅ Lab 8: Memoria Persistente
- `JdbcChatMemory` con H2 Database
- Persistencia entre reinicios
- Perfiles de Spring (`dev` vs `persistent`)

### ✅ Reto: Multi-sesión
- Endpoint `POST /api/chat/{sessionId}`
- Soporte para múltiples usuarios simultáneos
- Cada sesión con historial independiente

### ✅ Lab 9: Búsqueda semántica con embeddings
- `SimpleVectorStore` para indexación en memoria
- Carga de documentos teóricos para ejercicios en clase
- Recuperación por similitud semántica

### ✅ Lab 10: Endpoint `/buscar`
- Endpoint `GET /api/buscar?query=...&topK=...`
- Retorna fragmentos similares y metadata

### ✅ Lab RAG: pipeline completo básico
- Endpoint `POST /api/rag`
- Recupera fragmentos desde `SimpleVectorStore`
- Inyecta el contexto en el prompt
- Genera respuesta citando el contexto recuperado

### ✅ Reto adicional: PDF + Tika
- Endpoint `POST /api/buscar/pdf`
- Lectura de PDFs con `TikaDocumentReader`
- Búsqueda semántica sobre el contenido del PDF

## 🔧 Proveedores de IA soportados

Ambos proyectos soportan **tres proveedores**:
- ✅ **OpenAI** (GPT-3.5/GPT-4)
- ✅ **Anthropic** (Claude)
- ✅ **Google Vertex AI** (Gemini)

## 🚀 Inicio rápido

### 1. Navegar al proyecto

```bash
# Para el proyecto de inicio (con TODOs)
cd fase3-spring-ai-start

# O para la solución completa
cd fase3-spring-ai
```

### 2. Configurar API Key

```bash
# OpenAI
export OPENAI_API_KEY=sk-...

# O Anthropic
export ANTHROPIC_API_KEY=sk-ant-...

# O Google Vertex AI
export VERTEX_AI_PROJECT_ID=tu-proyecto-id
```

### 3. Ejecutar

```bash
# Modo desarrollo (InMemoryChatMemory)
mvn spring-boot:run

# Modo producción (JdbcChatMemory persistente)
mvn spring-boot:run -Dspring-boot.run.profiles=persistent
```

### 4. Probar

```bash
# Chat simple
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Carlos"}'

# Multi-sesión
curl -X POST http://localhost:8080/api/chat/user-123 \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola, me llamo Carlos"}'

# Indexar documentos teóricos (Lab 9)
curl -X POST http://localhost:8080/api/buscar/demo

# Buscar por similitud semántica (Lab 10)
curl "http://localhost:8080/api/buscar?query=similitud%20coseno&topK=4"

# Reto: cargar PDF y buscar en él
curl -X POST http://localhost:8080/api/buscar/pdf \
  -H "Content-Type: application/json" \
  -d '{"path":"/Users/josediaz/Projects/JoeDayz/ia-con-java/docs/01-AI_Developer_Blueprint.pdf","sourceId":"ai-blueprint"}'

# RAG básico sobre el contenido recuperado
curl -X POST http://localhost:8080/api/rag \
  -H "Content-Type: application/json" \
  -d '{"question":"Explica la relación entre embeddings y similitud coseno","topK":4}'
```

## 📊 Comparación con implementación anterior

| Aspecto | `fase3` (original) | `fase3-spring-ai` (nuevo) |
|---------|-------------------|---------------------------|
| **Framework** | Custom `ServicioIA` | Spring AI |
| **Memoria** | Archivos JSON | InMemory / JDBC |
| **Persistencia** | Manual (JSON) | Automática (H2) |
| **API** | CLI (Scanner) | REST API |
| **Proveedores** | OpenAI/Anthropic | OpenAI/Anthropic/Gemini |
| **Multi-sesión** | Archivos por sesión | `sessionId` en BD |
| **Producción** | ❌ | ✅ |

## 🎯 ¿Cuál usar?

### Usa `fase3` (original) si:
- Quieres entender la mecánica interna
- Necesitas control total del flujo
- Prefieres implementación más simple
- CLI es suficiente

### Usa `fase3-spring-ai` (nuevo) si:
- Necesitas API REST lista para producción
- Quieres usar características de Spring AI
- Requieres escalabilidad
- Planeas integrar con otros servicios Spring

## 📁 Estructura del workspace

```
ia-con-java/
├── fase3/                      # Original: implementación custom + CLI
├── fase3-start/                # Original: versión con TODOs
├── fase3-spring-ai/            # ✨ NUEVO: solución completa Spring AI
├── fase3-spring-ai-start/      # ✨ NUEVO: versión con TODOs Spring AI
└── ...
```

## 📚 Documentación

Cada proyecto incluye:
- ✅ `README.md` detallado con instrucciones
- ✅ `test-api.sh` para pruebas automatizadas
- ✅ Comentarios inline explicativos
- ✅ Configuración multi-proveedor

## 🔍 Testing

Ambos proyectos incluyen scripts de testing:

```bash
cd fase3-spring-ai  # o fase3-spring-ai-start
chmod +x test-api.sh
./test-api.sh
```

El script verifica:
- ✅ Memoria conversacional (Lab 7)
- ✅ Multi-sesión (Reto)
- ✅ Limpieza de sesiones
- ✅ Persistencia (Lab 8)

## 💡 Tips para instructores

1. **Progresión recomendada:**
   - Primero enseñar `fase3` (entender fundamentos)
   - Luego mostrar `fase3-spring-ai` (best practices)

2. **Para labs:**
   - Usar `fase3-spring-ai-start` con los TODOs
   - Referirse a `fase3-spring-ai` para la solución

3. **Demo en clase:**
   - Mostrar H2 Console para visualizar memoria
   - Comparar InMemory vs Jdbc en vivo
   - Demostrar multi-sesión con múltiples usuarios

## 🆚 Comparación de endpoints

### fase3 (original)
```bash
# No tiene endpoints REST
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoria"
```

### fase3-spring-ai (nuevo)
```bash
# REST API completa
POST /api/chat
POST /api/chat/{sessionId}
DELETE /api/chat/{sessionId}
GET /api/chat/status
POST /api/buscar/demo
GET /api/buscar?query=...&topK=4
POST /api/buscar/pdf
POST /api/rag
```

## 🧠 Alineación con teoría (Embeddings y Vector DB)

La demo de Labs 9/10 está diseñada para reforzar estos temas:

1. **Embeddings (vectores):** cada documento y consulta se transforma en un vector.
2. **Similitud coseno:** criterio de cercanía semántica usado para recuperar resultados.
3. **Modelos de embedding:** componente que genera los vectores (OpenAI/Vertex, según perfil).
4. **Vector databases:**
   - `SimpleVectorStore` para demos en memoria,
   - `PgVector` para persistencia en PostgreSQL,
   - `Chroma` para despliegues ligeros de búsqueda vectorial.

## 🚧 Qué aún no cubre esta demo

- No usa `PgVector` ni `Chroma` en runtime.
- No hace reranking.
- No hace chunking avanzado.
- No usa metadata filtering sofisticado.
- Sí implementa ya el pipeline básico de RAG: retrieval -> prompt grounding -> generation.

## 🔧 Configuración de proveedores

Editar `src/main/resources/application.yml` y descomentar el bloque deseado:

```yaml
# OpenAI (por defecto)
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}

# Anthropic
#spring:
#  ai:
#    anthropic:
#      api-key: ${ANTHROPIC_API_KEY}

# Gemini
#spring:
#  ai:
#    vertex:
#      ai:
#        gemini:
#          project-id: ${VERTEX_AI_PROJECT_ID}
```

## 🗄️ Base de datos (Lab 8)

Con perfil `persistent`:

**H2 Console:** http://localhost:8080/h2-console  
**JDBC URL:** `jdbc:h2:file:./data/chatbot-memory`  
**User:** `sa` | **Pass:** (vacío)

**Ver mensajes:**
```sql
SELECT * FROM CHAT_MEMORY ORDER BY TIMESTAMP DESC;
```

## ⚠️ Notas importantes

- Spring AI está en **milestone** (1.0.0-M1)
- La API puede cambiar en versiones futuras
- Para producción, esperar versión GA
- Los ejemplos funcionan con las versiones actuales de OpenAI/Anthropic/Gemini

## 📖 Referencias

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Chat Memory Guide](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_chat_memory)
- [OpenAI Spring Boot Starter](https://docs.spring.io/spring-ai/reference/api/clients/openai-chat.html)
- [Anthropic Spring Boot Starter](https://docs.spring.io/spring-ai/reference/api/clients/anthropic-chat.html)

## 🎓 Para estudiantes

Si estás siguiendo el curso:

1. **Lab 7:** Completa TODOs en `ChatConfig.java` y `ChatService.java`
2. **Lab 8:** Completa TODOs en `ChatConfigPersistent.java`
3. **Reto:** Completa TODOs en `ChatController.java` para multi-sesión

Todos los TODOs tienen **PISTAS** y explicaciones detalladas.

## ✅ Checklist de implementación

Para `fase3-spring-ai-start`:

### Lab 7
- [ ] `ChatConfig.chatMemory()` - Crear `InMemoryChatMemory`
- [ ] `ChatService` constructor - Inicializar `ChatClient` con advisor
- [ ] `ChatService.chat(mensaje)` - Implementar método simple
- [ ] `ChatController.chat()` - Crear endpoint básico

### Lab 8
- [ ] `ChatConfigPersistent.chatMemory()` - Crear `JdbcChatMemory`
- [ ] Probar con perfil `persistent`
- [ ] Verificar persistencia en H2 Console

### Reto
- [ ] `ChatService.chat(sessionId, mensaje)` - Multi-sesión
- [ ] `ChatController.chatWithSession()` - Endpoint con sessionId
- [ ] Probar múltiples usuarios simultáneos

### Lab 9
- [ ] Implementar `SemanticSearchService.cargarDocumentosDemo()` con `SimpleVectorStore`
- [ ] Crear endpoint `POST /api/buscar/demo`

### Lab 10
- [ ] Implementar `SemanticSearchService.buscar(query, topK)`
- [ ] Crear endpoint `GET /api/buscar`

### Reto PDF
- [ ] Implementar `SemanticSearchService.cargarPdf(path, sourceId)` con `TikaDocumentReader`
- [ ] Crear endpoint `POST /api/buscar/pdf`

### Lab RAG
- [ ] Implementar `RagService.answer(request)`
- [ ] Crear endpoint `POST /api/rag`
- [ ] Pedir al modelo que cite `[1]`, `[2]`, `[3]` según el contexto recuperado

## 🎉 ¡Listo!

Ahora tienes dos implementaciones completas:
- **Custom:** Control total, educativo
- **Spring AI:** Producción, best practices

¡Disfruta explorando ambas aproximaciones! 🚀
