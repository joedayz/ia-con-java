# Indice de Demos (Ollama primero)

Este indice esta pensado para recorrer las demos una por una, con ejemplos claros y un mini guion para explicar cada paso.

## Nota rapida de multiplataforma

- En `Windows PowerShell`, usa `curl.exe` para evitar el alias de `curl`.
- En `Linux/macOS`, usa `curl` normal.

## 0) Preparacion rapida

1. Instala dependencias del proyecto (solo una vez).

**Linux/macOS**
```bash
./mvnw clean install -DskipTests -U
```

**Windows PowerShell**
```powershell
.\mvnw.cmd clean install -DskipTests -U
```

2. Asegura Ollama y modelos.

**Linux/macOS**
```bash
ollama pull llama3.2
ollama pull mxbai-embed-large
ollama pull llava
```

**Windows PowerShell**
```powershell
ollama pull llama3.2
ollama pull mxbai-embed-large
ollama pull llava
```

3. Levanta la app principal para demos HTTP.

**Linux/macOS**
```bash
./mvnw spring-boot:run -pl applications/provider-ollama
```

**Windows PowerShell**
```powershell
.\mvnw.cmd spring-boot:run -pl applications/provider-ollama
```

4. Verifica que la app responda.

**Linux/macOS**
```bash
curl http://localhost:8080/debug
```

**Windows PowerShell**
```powershell
curl.exe http://localhost:8080/debug
```

---

## 1) Ruta recomendada de clase (uno por uno)

> Dinamica sugerida por demo: **(a) concepto**, **(b) endpoint**, **(c) que observar en la respuesta**.

### Demo 1 - Chat basico
- **Codigo**: `components/apis/chat/src/main/java/com/example/chat_01/BasicPromptController.java`
- **Objetivo**: ver la llamada minima con `ChatModel`.

**Linux/macOS**
```bash
curl http://localhost:8080/chat/01/joke
```

**Windows PowerShell**
```powershell
curl.exe http://localhost:8080/chat/01/joke
```

- **Guion corto**: "Mando un prompt simple y recibo texto. Este es el punto de partida de todo lo demas."

### Demo 2 - ChatClient (API fluida)
- **Codigo**: `components/apis/chat/src/main/java/com/example/chat_02/ChatClientController.java`
- **Objetivo**: pasar de `ChatModel` a `ChatClient` para trabajar con prompts mas estructurados.

**Linux/macOS**
```bash
curl "http://localhost:8080/chat/02/client/joke?topic=futbol"
curl http://localhost:8080/chat/02/client/threeJokes
```

**Windows PowerShell**
```powershell
curl.exe "http://localhost:8080/chat/02/client/joke?topic=futbol"
curl.exe http://localhost:8080/chat/02/client/threeJokes
```

- **Guion corto**: "Misma idea del chat basico, pero con una API mas rica para crecer hacia casos reales."

### Demo 3 - Prompt templates
- **Codigo**: `components/apis/chat/src/main/java/com/example/chat_03/PromptTemplateController.java`
- **Objetivo**: inyectar variables de forma segura y reusable.

**Linux/macOS**
```bash
curl "http://localhost:8080/chat/03/joke?topic=tecnologia"
curl "http://localhost:8080/chat/03/plays?author=Shakespeare"
```

**Windows PowerShell**
```powershell
curl.exe "http://localhost:8080/chat/03/joke?topic=tecnologia"
curl.exe "http://localhost:8080/chat/03/plays?author=Shakespeare"
```

- **Guion corto**: "Separar plantilla y variables evita prompts hardcodeados y mejora mantenimiento."

### Demo 4 - Salida estructurada (JSON/objetos)
- **Codigo**: `components/apis/chat/src/main/java/com/example/chat_04/StructuredOutputConverterController.java`
- **Objetivo**: convertir salida de LLM en listas, mapas o POJOs.

**Linux/macOS**
```bash
curl "http://localhost:8080/chat/04/plays/list?author=Cervantes"
curl "http://localhost:8080/chat/04/plays/object?author=Shakespeare"
```

**Windows PowerShell**
```powershell
curl.exe "http://localhost:8080/chat/04/plays/list?author=Cervantes"
curl.exe "http://localhost:8080/chat/04/plays/object?author=Shakespeare"
```

- **Guion corto**: "No solo texto libre: tambien podemos pedir formatos listos para negocio/API."

### Demo 5 - Tool calling
- **Codigo**: `components/apis/chat/src/main/java/com/example/chat_05/ToolController.java`
- **Objetivo**: dejar que el modelo use herramientas Java.

**Linux/macOS**
```bash
curl "http://localhost:8080/chat/05/time?city=Madrid"
curl "http://localhost:8080/chat/05/weather?city=Monterrey"
curl "http://localhost:8080/chat/05/search?query=find%20me%20a%20sushi%20restaurant%20for%202%20people"
```

**Windows PowerShell**
```powershell
curl.exe "http://localhost:8080/chat/05/time?city=Madrid"
curl.exe "http://localhost:8080/chat/05/weather?city=Monterrey"
curl.exe "http://localhost:8080/chat/05/search?query=find%20me%20a%20sushi%20restaurant%20for%202%20people"
```

- **Guion corto**: "El LLM deja de ser solo conversacion y empieza a ejecutar acciones con datos externos."

### Demo 6 - Roles (system/user)
- **Codigo**: `components/apis/chat/src/main/java/com/example/chat_06/RoleController.java`
- **Objetivo**: controlar comportamiento mediante mensaje de sistema.

**Linux/macOS**
```bash
curl http://localhost:8080/chat/06/fruit
curl http://localhost:8080/chat/06/veg
```

**Windows PowerShell**
```powershell
curl.exe http://localhost:8080/chat/06/fruit
curl.exe http://localhost:8080/chat/06/veg
```

- **Guion corto**: "El system prompt define reglas del asistente y limita su dominio."

### Demo 7 - Multimodal (texto + imagen)
- **Codigo**: `components/apis/chat/src/main/java/com/example/chat_07/MultiModalController.java`
- **Objetivo**: analizar imagen con modelo multimodal (`llava`).

**Linux/macOS**
```bash
curl http://localhost:8080/chat/07/explain
```

**Windows PowerShell**
```powershell
curl.exe http://localhost:8080/chat/07/explain
```

- **Guion corto**: "El input ya no es solo texto; el modelo interpreta medios adicionales."

### Demo 8 - Streaming
- **Codigo**: `components/apis/chat/src/main/java/com/example/chat_08/StreamingChatModelController.java`
- **Objetivo**: recibir la respuesta en flujo continuo.

**Linux/macOS**
```bash
curl -N "http://localhost:8080/chat/08/essay?topic=Impact%20of%20AI%20on%20Education"
```

**Windows PowerShell**
```powershell
curl.exe -N "http://localhost:8080/chat/08/essay?topic=Impact%20of%20AI%20on%20Education"
```

- **Guion corto**: "Mejora UX: el usuario ve tokens llegar en tiempo real."

---

## 2) Continuacion natural: RAG y memoria

> Cuando termines chat, pasa a estos dos para mostrar casos empresariales.

### Demo 9 - RAG con advisors
- **Codigo**: `components/patterns/02-retrieval-augmented-generation/src/main/java/com/example/rag_02/AdvisorController.java`

**Linux/macOS**
```bash
curl http://localhost:8080/rag/02/load
curl "http://localhost:8080/rag/02/query?topic=Which%20bikes%20have%20extra%20long%20range"
```

**Windows PowerShell**
```powershell
curl.exe http://localhost:8080/rag/02/load
curl.exe "http://localhost:8080/rag/02/query?topic=Which%20bikes%20have%20extra%20long%20range"
```

- **Nota**: si usas pgvector, levanta antes `docker/postgres`.

### Demo 10 - Memoria conversacional
- **Codigo**: `components/patterns/03-chat-memory/src/main/java/com/example/mem_02/ChatHistoryController.java`

**Linux/macOS**
```bash
curl "http://localhost:8080/mem/02/hello?message=Hola%2C%20me%20llamo%20Jose"
curl http://localhost:8080/mem/02/name
```

**Windows PowerShell**
```powershell
curl.exe "http://localhost:8080/mem/02/hello?message=Hola%2C%20me%20llamo%20Jose"
curl.exe http://localhost:8080/mem/02/name
```

- **Guion corto**: "Sin memoria no hay continuidad; con advisor de memoria el chat recuerda contexto."

---

## 3) Recomendacion para explicar en vivo

- Usa 10-12 minutos por demo: 3 min concepto, 4 min codigo, 3 min prueba.
- Cierra cada demo con una frase: "Que problema real resuelve esto?".
- No mezcles muchos endpoints a la vez: 1 o 2 por demo para mantener foco.
- Si falla algo, valida en este orden: Ollama -> modelo descargado -> app en puerto correcto.

Para una version mas extensa del recorrido, revisa tambien `ROADMAP.md`.
