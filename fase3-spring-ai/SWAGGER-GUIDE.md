# 📚 Guía de Swagger UI para Estudiantes

## ¿Qué es Swagger UI?

**Swagger UI** es una herramienta que genera automáticamente una **documentación interactiva** de tu API REST. En lugar de usar curl o Postman, puedes:

- ✅ Ver todos los endpoints disponibles
- ✅ Entender qué parámetros necesita cada endpoint
- ✅ Probar la API directamente desde el navegador
- ✅ Ver ejemplos de requests y responses
- ✅ Comprender mejor la arquitectura REST

---

## 🚀 Cómo Acceder

### 1. Iniciar la aplicación

```bash
# Opción 1: OpenAI (recomendado para búsqueda semántica)
mvn spring-boot:run -Dspring-boot.run.profiles=openai

# Opción 2: Anthropic (solo para chat)
mvn spring-boot:run -Dspring-boot.run.profiles=anthropic

# Opción 3: Vertex AI / Gemini
mvn spring-boot:run -Dspring-boot.run.profiles=vertex
```

### 2. Abrir Swagger UI en el navegador

```
http://localhost:8080/swagger-ui.html
```

---

## 📖 Estructura de Swagger UI

### Secciones principales:

#### 1️⃣ **💬 Chat**
- `POST /api/chat` - Chat simple (sesión única)
- `POST /api/chat/{sessionId}` - Chat multi-sesión (RETO)
- `DELETE /api/chat/{sessionId}` - Limpiar sesión
- `GET /api/chat/status` - Estado del servicio

#### 2️⃣ **🔍 Búsqueda Semántica** 
- `POST /api/buscar/demo` - Cargar documentos de demostración
- `GET /api/buscar` - Buscar por similitud semántica
- `POST /api/buscar/pdf` - Indexar un PDF local

#### 3️⃣ **🧠 RAG (Retrieval Augmented Generation)**
- `POST /api/rag` - Respuesta con contexto de documentos

---

## 🎯 Tutorial Paso a Paso

### Lab 7: Chat con Memoria

#### Paso 1: Expandir la sección "💬 Chat"
Haz clic en `POST /api/chat`

#### Paso 2: Clic en "Try it out"
Se habilitará el editor de JSON

#### Paso 3: Editar el Request Body
```json
{
  "message": "Hola, me llamo Carlos"
}
```

#### Paso 4: Clic en "Execute"
Verás la respuesta del chatbot

#### Paso 5: Probar la memoria
Envía otro mensaje:
```json
{
  "message": "¿Recuerdas mi nombre?"
}
```

**Resultado esperado:** El chatbot debe responder "Carlos" 🎉

---

### Lab 9-10: Búsqueda Semántica

#### Paso 1: Indexar documentos de demostración
1. Expandir `POST /api/buscar/demo`
2. Clic en "Try it out"
3. Clic en "Execute"

**Respuesta esperada:**
```json
{
  "message": "Documentos teóricos indexados en memoria",
  "documentos": 5
}
```

#### Paso 2: Buscar por similitud
1. Expandir `GET /api/buscar`
2. Clic en "Try it out"
3. Ingresar parámetros:
   - **query:** `similitud coseno`
   - **topK:** `3`
4. Clic en "Execute"

**Resultado esperado:** Verás los 3 documentos más relevantes sobre similitud coseno

---

### RAG: Respuestas con Contexto

#### Paso 1: Asegurarse de tener documentos indexados
Ejecuta `POST /api/buscar/demo` primero

#### Paso 2: Hacer una pregunta con RAG
1. Expandir `POST /api/rag`
2. Clic en "Try it out"
3. Request Body:
```json
{
  "query": "¿Qué son los embeddings?",
  "topK": 3
}
```
4. Clic en "Execute"

**Resultado esperado:**
```json
{
  "answer": "Los embeddings son...[1][2]",
  "fragments": [...],
  "provider": "openai"
}
```

La respuesta incluye **citas [1][2][3]** que referencian los documentos recuperados.

---

## 🎨 Características Avanzadas

### 1. **Schemas (Modelos)**
Al final de la página, verás la sección "Schemas" con todos los DTOs:
- `ChatRequest`
- `ChatResponse`
- `BuscarResponse`
- `RagRequest`
- `RagResponse`

Estos muestran la estructura exacta de cada objeto.

### 2. **Ejemplos Múltiples**
Algunos endpoints tienen múltiples ejemplos disponibles. Busca el dropdown de ejemplos:
- `Saludo`
- `Pregunta`
- `Seguimiento`

### 3. **Filtro de Endpoints**
Usa la barra de búsqueda superior para filtrar endpoints rápidamente.

### 4. **Curl Command Generator**
Después de ejecutar una petición, Swagger UI te muestra el comando curl equivalente. Útil para documentación o scripts.

---

## 🔧 Comparación: Swagger UI vs curl vs Postman

| Característica | Swagger UI | curl | Postman |
|----------------|-----------|------|---------|
| **Documentación automática** | ✅ | ❌ | ❌ |
| **Probar desde navegador** | ✅ | ❌ | Requiere instalación |
| **Ver estructura de datos** | ✅ | ❌ | ⚠️ Manual |
| **Ejemplos integrados** | ✅ | ❌ | ⚠️ Manual |
| **Exportar OpenAPI spec** | ✅ | ❌ | ⚠️ Parcial |
| **Scripting avanzado** | ❌ | ✅ | ✅ |

---

## 🐛 Troubleshooting

### Error: "No EmbeddingModel disponible"
**Causa:** Estás usando el perfil `anthropic` pero intentando usar búsqueda semántica.

**Solución:** Usa el perfil `openai` o `vertex`:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=openai
```

### Error 400: "El mensaje no puede estar vacío"
**Causa:** El campo `message` está vacío o null.

**Solución:** Asegúrate de enviar un JSON válido:
```json
{
  "message": "tu texto aquí"
}
```

### Swagger UI no carga
**Causa:** La aplicación no está corriendo o hay un error en el puerto.

**Solución:**
1. Verifica que la app esté corriendo: `http://localhost:8080/actuator/health`
2. Revisa los logs en la consola
3. Confirma que el puerto 8080 esté libre

---

## 📝 Para Presentación / Clase

### Demo sugerida (5 minutos):

1. **Mostrar la interfaz** (30 seg)
   - Explicar las 3 secciones principales

2. **Demo Chat** (1 min)
   - POST /api/chat → "Hola, me llamo X"
   - POST /api/chat → "¿Recuerdas mi nombre?"
   - Mostrar cómo mantiene contexto

3. **Demo Multi-sesión** (1 min)
   - POST /api/chat/user-1 → "Me llamo Ana"
   - POST /api/chat/user-2 → "Me llamo Luis"
   - POST /api/chat/user-1 → "¿Mi nombre?"
   - Explicar sesiones independientes

4. **Demo Búsqueda Semántica** (1.5 min)
   - POST /api/buscar/demo
   - GET /api/buscar?query=embeddings
   - Mostrar resultados ordenados por relevancia

5. **Demo RAG** (1.5 min)
   - POST /api/rag → "¿Qué es la similitud coseno?"
   - Mostrar respuesta con citas [1][2][3]
   - Explicar el flujo: búsqueda → contexto → generación

---

## 💡 Conceptos Clave para Estudiantes

### REST API
Swagger UI ayuda a visualizar la arquitectura REST:
- **Resources:** `/api/chat`, `/api/buscar`, `/api/rag`
- **Verbs:** GET (lectura), POST (creación/acción), DELETE (borrar)
- **Status Codes:** 200 (OK), 400 (Bad Request), 500 (Error)

### Request/Response Cycle
Cada endpoint muestra claramente:
- **Request Body:** Lo que envías (JSON)
- **Response Body:** Lo que recibes (JSON)
- **Headers:** Metadata (Content-Type, etc.)

### API Design Best Practices
Swagger UI hace visibles las buenas prácticas:
- ✅ Rutas semánticas (`/api/chat`, no `/api/endpoint1`)
- ✅ Verbos HTTP correctos (POST para acciones, GET para lectura)
- ✅ Respuestas consistentes
- ✅ Documentación clara de parámetros

---

## 📚 Recursos Adicionales

- **OpenAPI Specification:** https://swagger.io/specification/
- **Springdoc OpenAPI:** https://springdoc.org/
- **Swagger UI Demo:** https://petstore.swagger.io/

---

## ✅ Checklist para Estudiantes

- [ ] Accedí a Swagger UI en http://localhost:8080/swagger-ui.html
- [ ] Probé el endpoint `POST /api/chat`
- [ ] Verifiqué que el chatbot recuerda el contexto
- [ ] Probé chat multi-sesión con diferentes sessionIds
- [ ] Cargué documentos con `POST /api/buscar/demo`
- [ ] Busqué documentos con `GET /api/buscar`
- [ ] Probé RAG con `POST /api/rag`
- [ ] Entendí la diferencia entre los 3 proveedores (OpenAI, Anthropic, Vertex)
- [ ] Exporté un comando curl desde Swagger UI

---

¡Ahora estás listo para explorar y entender mejor la API REST con Spring AI! 🚀
