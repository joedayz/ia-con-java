# Guía Paso a Paso: De Zero a Hero con Spring AI

Este documento describe el camino de aprendizaje recomendado para explorar este repositorio, desde los conceptos más básicos hasta sistemas de agentes avanzados.

---

## 1. Nivel Básico: Interacción con el Chat

En este nivel aprenderás a configurar y llamar a un modelo de lenguaje de forma directa y estructurada.

### 1.1 ChatModel Directo (El punto de partida)
- **Demo**: `components/apis/chat/src/main/java/com/example/chat_01/BasicPromptController.java`
- **Concepto**: Uso de la interfaz `ChatModel`.
- **Ejecución**:
  ```bash
  curl http://localhost:8080/chat/01/joke
  ```

### 1.2 ChatClient (API Fluida)
- **Demo**: `components/apis/chat/src/main/java/com/example/chat_02/ChatClientController.java`
- **Concepto**: Uso de `ChatClient.Builder`.
- **Ejecución**:
  ```bash
  curl "http://localhost:8080/chat/02/client/joke?topic=tecnologia"
  ```

### 1.3 Plantillas de Prompts
- **Demo**: `components/apis/chat/src/main/java/com/example/chat_03/PromptTemplateController.java`
- **Concepto**: Uso de `PromptTemplate` para parametrizar entradas.
- **Ejecución**:
  ```bash
  curl "http://localhost:8080/chat/03/joke?topic=futbol"
  ```

---

## 2. Nivel Intermedio: Estructura, Roles y Herramientas

Aquí empezamos a controlar más el comportamiento del modelo y a darle capacidades externas.

### 2.1 Salida Estructurada
- **Demo**: `components/apis/chat/src/main/java/com/example/chat_04/StructuredOutputConverterController.java`
- **Concepto**: `BeanOutputConverter`.
- **Ejecución**:
  ```bash
  # Obtener lista de obras
  curl "http://localhost:8080/chat/04/plays/list?author=Cervantes"
  # Obtener objetos JSON estructurados
  curl "http://localhost:8080/chat/04/plays/object?author=Shakespeare"
  ```

### 2.2 Roles (System, User, Assistant)
- **Demo**: `components/apis/chat/src/main/java/com/example/chat_06/RoleController.java`
- **Concepto**: Definir el comportamiento mediante el `System Message`.
- **Ejecución**:
  ```bash
  # Pregunta sobre frutas (permitido)
  curl http://localhost:8080/chat/06/fruit
  # Pregunta sobre vegetales (restringido por el sistema)
  curl http://localhost:8080/chat/06/veg
  ```

### 2.3 Function Calling (Herramientas)
- **Demo**: `components/apis/chat/src/main/java/com/example/chat_05/ToolController.java`
- **Concepto**: Registrar funciones Java como herramientas.
- **Ejecución**:
  ```bash
  # Consultar hora en una ciudad usando herramientas
  curl "http://localhost:8080/chat/05/time?city=Madrid"
  # Consultar clima
  curl "http://localhost:8080/chat/05/weather?city=Lima"
  ```

---

## 3. Nivel Avanzado: RAG, Memoria y Agentes

Sistemas complejos que combinan búsqueda de datos y razonamiento.

### 3.1 RAG (Generación Aumentada por Recuperación)
- **Demo**: `components/patterns/02-retrieval-augmented-generation/`
- **Conceptos**: `VectorStore`, `Similarity Search`.
- **Ejecución**:
  ```bash
  # 1. Cargar documentos en la base de datos vectorial
  curl http://localhost:8080/rag/02/load
  # 2. Consultar sobre tus documentos
  curl "http://localhost:8080/rag/02/query?topic=Which+bikes+are+best+for+mountains"
  ```

### 3.2 Memoria del Chat
- **Demo**: `components/patterns/03-chat-memory/`
- **Concepto**: `ChatMemory` y `Advisors`.
- **Ejecución**:
  ```bash
  # 1. Presentarse al modelo
  curl "http://localhost:8080/mem/02/hello?message=Hola,soy+Pepe"
  # 2. Verificar que te recuerda
  curl http://localhost:8080/mem/02/name
  ```

### 3.3 Model Context Protocol (MCP)
- **Demo**: `mcp/`
- **Concepto**: Integración con servidores MCP externos.
- **Nota**: Estas demos suelen ser aplicaciones CLI o requieren servidores MCP activos. Revisa el README específico en `mcp/03-basic-mcp-client/`.

### 3.4 Sistemas Agénticos
- **Demo**: `agentic-system/01-inner-monologue/`
- **Concepto**: Agentes con monólogo interno.
- **Ejecución**:
  ```bash
  # 1. Crear un agente
  curl -X POST http://localhost:8080/agents/inner-monologue/mi-agente
  # 2. Enviar un mensaje
  curl -X POST http://localhost:8080/agents/inner-monologue/mi-agente/messages \
       -H "Content-Type: application/json" \
       -d '{"text": "Ayúdame a planear un viaje a Japón"}'
  ```

---

## Cómo Probar las Demos

1. **Asegúrate de tener Docker y Ollama corriendo** (ver `README.md`).
   - Para que las demos funcionen, debes tener Ollama instalado y haber descargado los modelos necesarios:
     ```bash
     ollama pull llama3.2
     ollama pull mxbai-embed-large
     ```
2. **Instala y Levanta los servicios**: Este es un proyecto multi-módulo. Antes de correr cualquier aplicación, **debes instalar los módulos comunes** en tu repositorio local de Maven. Usa el flag `-U` para forzar la actualización de metadatos y resolver dependencias internas correctamente:
   ```bash
   ./mvnw clean install -DskipTests -U
   ```

   Luego, arranca el módulo que quieras probar (**NO PUEDES** ejecutar `mvn spring-boot:run` directamente desde la raíz sin especificar el proyecto):
   - **Para la mayoría de las demos (Niveles 1, 2, 3.1, 3.2)**:
     ```bash
     ./mvnw spring-boot:run -pl applications/provider-ollama
     ```
   - **Para MCP**:
     ```bash
     ./mvnw spring-boot:run -pl mcp/03-basic-mcp-client
     ```
   - **Para Sistemas Agénticos**:
     ```bash
     ./mvnw spring-boot:run -pl agentic-system/01-inner-monologue/inner-monologue-cli
     ```
3. **Usa cURL o HTTPie**: Los ejemplos anteriores usan `curl`. Si prefieres `httpie`:
   ```bash
   http :8080/chat/01/joke
   ```

### Error: `Unable to find a suitable main class`
Si ves este error, es porque Maven está intentando ejecutar una aplicación Spring Boot en el proyecto raíz, pero este proyecto es solo un contenedor (POM) de otros módulos. Siempre usa el flag `-pl` seguido de la ruta del módulo que deseas ejecutar.

### Error: `Web server failed to start. Port 8080 was already in use`
Este error ocurre cuando ya tienes otra aplicación corriendo en el puerto 8080 (quizás otra instancia de la demo o un servidor local).
- **Opción A (Recomendada)**: Cierra la aplicación que está usando el puerto.
- **Opción B**: Cambia el puerto de la demo al ejecutarla:
  ```bash
  ./mvnw spring-boot:run -pl applications/provider-ollama -Dspring-boot.run.arguments="--server.port=8081"
  ```
  *Nota: Si cambias el puerto, recuerda actualizar tus comandos `curl` (ej: `http://localhost:8081/chat/01/joke`).*

### Error: `Could not resolve dependencies for project com.example:...`
Este error ocurre cuando los módulos comunes (como `chat`, `rag`, `config-pgvector`) no están disponibles en tu repositorio local o Maven tiene una falla de resolución cacheada.
- **Solución**: Ejecuta el siguiente comando desde la raíz del proyecto para instalar todos los módulos forzando la actualización:
  ```bash
  ./mvnw clean install -DskipTests -U
  ```

### Error: `ConnectException: I/O error on POST request for "http://localhost:11434/api/chat"`
Este error indica que la aplicación no puede conectarse a Ollama.
1. **Verifica que Ollama esté corriendo**: Ejecuta `ollama list` en tu terminal. Si falla, inicia la aplicación de escritorio de Ollama.
2. **Verifica el puerto**: Ollama corre por defecto en el puerto `11434`.
3. **Descarga el modelo**: Asegúrate de haber descargado el modelo configurado (ver `application.yaml`):
   ```bash
   ollama pull llama3.2
   ```

### Error: `HTTP 400 - the input length exceeds the context length`
Este error ocurre cuando el contenido de un documento es demasiado grande para que el modelo de embedding lo procese en una sola llamada.
- **Solución**: Hemos implementado `TokenTextSplitter(200, 100, 10, 5000, true)` en todos los controladores de carga (`/load`) y embedding. Esto divide automáticamente los documentos grandes en fragmentos (chunks) seguros que el modelo `mxbai-embed-large` puede procesar (cuyo límite es de 512 tokens). Si creas nuevos cargadores de datos, asegúrate de usar un `TextSplitter` con parámetros similares.
- **Caso Especial (Shakespeare)**: El archivo `Shakespeare.txt` tiene 5.4MB. Intentar embeberlo en una sola petición (endpoint `/embed/03/big`) fallará siempre. Por eso, hemos desactivado la ejecución directa en ese endpoint y recomendamos usar `/embed/03/chunk`.
