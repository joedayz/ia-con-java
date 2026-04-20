# 🎓 Guía del Curso: IA con Java

**Horario:** Martes y Viernes de 7:00 PM a 9:00 PM (2 horas por sesión)  
**Inicio:** Martes 17 de Marzo, 2026  
**Requisitos previos:** Java 17+, Maven, cuenta OpenAI (API Key), IDE (IntelliJ/VS Code)  
**Repositorio:** `ia-con-java` (proyecto Maven multi-módulo)

---

## 📅 Cronograma Clase a Clase

| # | Fecha | Día | Temas Teóricos | Laboratorios | Módulo |
|---|-------|-----|----------------|--------------|--------|
| 1 | 17 Mar | Martes | Fundamentos de IA y LLMs. Qué es un LLM, Transformers, tokens, API REST de OpenAI, ¿por qué Java? | **Lab 1:** Configurar `.env` y compilar proyecto. **Lab 2:** Ejecutar `PrimeraLlamadaOpenAI` y modificar prompt. **Reto:** Agregar system prompt de pirata. | `fase1` |
| 2 | 20 Mar | Viernes | **PDF 04:** The Augmented Developer. Arquitectura multi-proveedor (OpenAI + Anthropic + Ollama). APIs REST con Quarkus. Modelos locales con Ollama. | **Lab 3:** Construir paso a paso `fase1-start` (Java puro). **Lab 4:** Construir paso a paso `fase1-quarkus-start` (REST API). **Lab 5 (opcional):** Intro a `fase1-ollama` (modelos locales). **Tarea:** Agregar soporte Anthropic. | `fase1-start` + `fase1-quarkus-start` + `fase1-ollama` |
| 3 | 24 Mar | Martes | Prompt Engineering: system prompts, zero-shot, few-shot, chain of thought, salida estructurada (JSON). | **Lab 5:** Ejecutar `PromptEngineering` interactivo. **Lab 6:** Crear `ClasificadorSentimiento.java` con few-shot. **Reto:** Salida JSON con `entity()` en Spring AI. | `fase2` + Spring Boot |
| 4 | 27 Mar | Viernes | Chatbots con memoria: tipos de memoria (buffer, window, summary), memoria persistente, multi-sesión. | **Lab 7:** Crear `ChatbotConMemoria.java` con historial en lista. **Lab 8:** Memoria persistente con `JdbcChatMemory` en Spring AI. **Reto:** Endpoint `/chat/{sessionId}` multi-usuario. | `fase3` + Spring Boot |
| 5 | 31 Mar | Martes | Embeddings: vectores, similitud coseno, modelos de embedding. Vector databases: PgVector, Chroma, SimpleVectorStore. | **Lab 9:** Búsqueda semántica en memoria con `SimpleVectorStore`. **Lab 10:** Endpoint `/buscar` que retorna documentos similares. **Reto:** Cargar un PDF con `TikaDocumentReader` y buscar en él. | Spring Boot |
| 6 | 3 Abr | Viernes | RAG completo: flujo indexación + consulta, chunks, por qué RAG > fine-tuning, RAG en Java puro vs con vector DB persistente. | **Lab 11:** Ejecutar `RAGSimple` (contexto en prompt). **Lab 12:** RAG con Spring AI + `QuestionAnswerAdvisor`. **Lab 12+ (opcional):** `fase4-spring-ai-ollama-pgvector` (PgVector persistente + advisor pipeline). **Reto:** Asistente de documentación con 3-5 archivos MD. | `fase4` + `fase4-spring-ai-ollama-pgvector` |
| 7 | 7 Abr | Martes | Tool Calling: qué es, flujo (LLM decide → app ejecuta → LLM responde), implementación real en Spring AI y LangChain4j. | **Lab 13:** `fase4-spring-ai-tool-calling` con `ToolConfig.obtenerClima()` y `consultarPais` (API real). **Lab 14:** `fase4-langchain4j-tool-calling` con `CalculadoraTools`, `FechaTools` y `PaisApiTools`. | `fase4-spring-ai-tool-calling` + `fase4-langchain4j-tool-calling` |
| 8 | 10 Abr | Viernes | AI Agents: patrón ReAct, componentes (LLM + Tools + Memory + orquestador), agents vs chatbots, multi-agent intro. | **Lab 15:** Agente "Asistente de Desarrollo" con 3 herramientas y `AiServices`. **Reto:** Agente "Analista de datos" con CSV + estadísticas + reporte. | LangChain4j |
| 9 | 14 Abr | Martes | Arquitectura completa: diseño backend+frontend, streaming SSE, consideraciones de producción (costos, seguridad, observabilidad). | **Lab 16:** Backend con endpoint SSE `/chat/stream`. **Lab 17:** Frontend HTML+JS con `EventSource`. **Reto:** Integrar memoria + RAG + tool en un solo endpoint. | Spring Boot + HTML |
| 10 | 17 Abr | Viernes | Repaso general, patrones avanzados, mejores prácticas, Q&A. | **Lab Final:** Proyecto integrador — app completa con chat UI + RAG + al menos 1 tool + memoria persistente + streaming. Presentación de proyectos. | Proyecto final |

---

## Estructura General del Curso

| Bloque | Tema | Tipo | Duración estimada |
|--------|------|------|-------------------|
| 1 | Fundamentos de IA y LLMs | Teoría | 45 min |
| 2 | Primera llamada a OpenAI desde Java | Lab | 30 min |
| 3 | Introducción a Spring AI y LangChain4j | Teoría | 30 min |
| 4 | Configuración de Spring AI + conexión LLM | Lab | 30 min |
| 5 | Prompt Engineering | Teoría + Lab | 45 min |
| 6 | Chatbots con memoria persistente | Teoría + Lab | 45 min |
| 7 | Embeddings y búsqueda semántica | Teoría + Lab | 60 min |
| 8 | RAG (Retrieval Augmented Generation) | Teoría + Lab | 60 min |
| 9 | Tool Calling e integración con APIs | Teoría + Lab | 45 min |
| 10 | AI Agents con LangChain4j | Teoría + Lab | 60 min |
| 11 | Arquitectura completa (backend + frontend) | Teoría + Lab | 60 min |

---

## BLOQUE 1 — Fundamentos de IA y LLMs aplicados a Java

### Contenido Teórico

1. **¿Qué es un LLM?**
   - Modelos de lenguaje: de n-gramas a Transformers
   - Arquitectura Transformer simplificada (atención, tokens, contexto)
   - Modelos populares: GPT-4, Claude, Llama, Gemini, Mistral

2. **¿Cómo funciona la API de un LLM?**
   - Protocolo: HTTP REST → `POST /chat/completions`
   - Estructura del request: `model`, `messages[]` (roles: system, user, assistant), `max_tokens`, `temperature`
   - Estructura del response: `choices[].message.content`
   - Concepto de tokens y costos

3. **¿Por qué Java para IA?**
   - Ecosistema empresarial existente (Spring Boot, microservicios)
   - Frameworks emergentes: Spring AI, LangChain4j
   - Java 17+: records, text blocks, HttpClient nativo, sealed classes
   - Comparación con Python (LangChain, LlamaIndex)

4. **Conceptos clave**
   - Temperature vs Top-p (creatividad vs determinismo)
   - Context window (ventana de contexto)
   - Tokens: tokenización, límites, costos
   - Streaming vs respuesta completa

### Diagrama para la pizarra

```
┌─────────────┐     HTTP POST        ┌──────────────┐
│  Java App   │ ──────────────────▶  │  OpenAI API  │
│ (HttpClient)│  {messages, model}   │  /chat/comp.  │
│             │ ◁──────────────────  │              │
└─────────────┘   {choices[].msg}    └──────────────┘
```

---

## BLOQUE 2 — Lab: Primera llamada a OpenAI desde Java (Fase 1)

### Objetivo
Que el alumno realice su primera llamada a la API de OpenAI usando Java puro (sin frameworks).

### Preparación
```bash
# 1. Clonar el repo y configurar API key
cp .env.example .env
# Editar .env → OPENAI_API_KEY=sk-tu-clave

# 2. Compilar todo el proyecto
mvn clean install
```

### Ejercicio guiado (20 min)

**Archivo de referencia:** `fase1/src/main/java/.../PrimeraLlamadaOpenAI.java`

1. **Revisar el código juntos** — Analizar:
   - Cómo se lee la API key desde `.env` con `EnvConfig`
   - Cómo se construye el JSON del request (text blocks de Java 17)
   - Uso de `HttpClient` nativo (Java 11+)
   - Parseo manual del response JSON
   
2. **Ejecutar:**
   ```bash
   mvn -pl fase1 exec:java
   ```

3. **Ejecutar con prompt personalizado:**
   ```bash
   mvn -pl fase1 exec:java -Dexec.args="Explica qué es Java en 2 líneas"
   ```

### Ejercicio autónomo (10 min)

> **Reto:** Modifica `PrimeraLlamadaOpenAI.java` para que:
> 1. Agregue un `system prompt` que diga "Eres un pirata que solo habla en español"
> 2. El `messages[]` ahora tenga 2 mensajes: system + user
> 3. Ejecuta y observa la diferencia en la respuesta
>
> **Pista:** Revisa cómo `PromptEngineering.java` (fase2) usa system + user.

---

## BLOQUE 3 — Introducción a Spring AI y LangChain4j

### Contenido Teórico

1. **Spring AI**
   - Framework oficial de Spring para integración con IA
   - Abstracción `ChatClient`, `ChatModel`, `Prompt`, `ChatResponse`
   - Auto-configuración (`spring-ai-openai-spring-boot-starter`)
   - Soporte para: OpenAI, Azure OpenAI, Ollama, HuggingFace, Anthropic
   - Integración nativa con Spring Boot (properties, beans, perfiles)

2. **LangChain4j**
   - Port a Java de LangChain (Python)
   - Conceptos: `ChatLanguageModel`, `AiServices`, `@UserMessage`, `@SystemMessage`
   - Módulos: `langchain4j-open-ai`, `langchain4j-ollama`, etc.
   - Ventajas: fuertemente tipado, interfaces declarativas, tool calling nativo

3. **Comparación Spring AI vs LangChain4j**

   | Aspecto | Spring AI | LangChain4j |
   |---------|-----------|-------------|
   | Integración Spring | Nativa (starters) | Manual (beans) |
   | Tipado | Moderado | Fuerte (AiServices) |
   | Tool calling | Sí | Sí (anotaciones) |
   | RAG | VectorStore + DocumentReader | EmbeddingStore + Ingestor |
   | Madurez | GA desde 2024 | Estable desde 2023 |
   | Ideal para | Apps Spring Boot | Cualquier app Java |

4. **¿Cuándo usar cuál?**
   - Spring AI: si ya tienes Spring Boot
   - LangChain4j: si quieres más control o no usas Spring
   - Ambos: ¡se pueden combinar!

---

## BLOQUE 4 — Lab: Configuración de Spring AI y conexión con LLM

### Objetivo
Crear un proyecto Spring Boot con Spring AI que se conecte a OpenAI.

### Ejercicio guiado (30 min)

> **Nota:** Este lab es un proyecto nuevo separado. Los alumnos crean un proyecto Spring Boot desde cero.

#### Paso 1: Crear proyecto Spring Boot
Ir a [start.spring.io](https://start.spring.io):
- **Spring Boot:** 3.3+
- **Dependencies:** Spring Web, Spring AI OpenAI
- **Java:** 17
- Descargar y abrir en IDE

#### Paso 2: Configurar `application.properties`
```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-3.5-turbo
spring.ai.openai.chat.options.temperature=0.7
```

#### Paso 3: Crear un controlador REST
```java
@RestController
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String mensaje) {
        return chatClient.prompt()
                .user(mensaje)
                .call()
                .content();
    }
}
```

#### Paso 4: Probar
```bash
mvn spring-boot:run
curl "http://localhost:8080/chat?mensaje=Hola+desde+Spring+AI"
```

### Ejercicio autónomo

> **Reto:** Agrega un segundo endpoint `/chat-formal` que use un system prompt: "Eres un abogado formal. Responde siempre en lenguaje jurídico."
>
> ```java
> @GetMapping("/chat-formal")
> public String chatFormal(@RequestParam String mensaje) {
>     return chatClient.prompt()
>             .system("Eres un abogado formal. Responde en lenguaje jurídico.")
>             .user(mensaje)
>             .call()
>             .content();
> }
> ```

---

## BLOQUE 5 — Prompt Engineering

### Contenido Teórico

1. **System Prompts**
   - Definición del "personaje" y comportamiento del modelo
   - Restricciones, formato de salida, idioma
   - System prompt vs user prompt: cuándo usar cada uno

2. **Técnicas de Prompt Engineering**
   - **Zero-shot:** pregunta directa sin ejemplos
   - **Few-shot:** incluir 2-3 ejemplos en el prompt
   - **Chain of Thought (CoT):** "Piensa paso a paso"
   - **Prompts estructurados:** salida en JSON, Markdown, listas

3. **Ejemplos comparativos**
   ```
   ❌ Zero-shot malo:    "Clasifica este texto"
   ✅ Zero-shot bueno:   "Clasifica el siguiente texto como POSITIVO, NEGATIVO o NEUTRO. 
                          Responde solo con la clasificación."
   
   ✅ Few-shot:          "Clasifica el sentimiento:
                          Texto: 'Me encanta!' → POSITIVO
                          Texto: 'Horrible'    → NEGATIVO
                          Texto: '{input}'      → "
   ```

4. **Salida estructurada (JSON)**
   - Pedir al modelo que responda en JSON
   - Spring AI: `BeanOutputConverter` para mapear a POJOs
   - LangChain4j: `AiServices` con tipos de retorno

### Lab: Prompt Engineering (Fase 2)

**Archivo:** `fase2/src/main/java/.../PromptEngineering.java`

#### Ejercicio guiado (15 min)
1. Ejecutar fase2 y probar el chatbot interactivo:
   ```bash
   mvn -pl fase2 exec:java
   ```
2. Analizar el `SYSTEM_PROMPT` definido y cómo afecta las respuestas
3. Probar preguntas técnicas vs no técnicas

#### Ejercicio autónomo: Few-Shot Classifier (20 min)

> **Reto:** Crea una nueva clase `ClasificadorSentimiento.java` en fase2 que:
> 1. Use few-shot prompting para clasificar el sentimiento de textos
> 2. El system prompt debe incluir 3 ejemplos:
>    - "El producto es excelente" → POSITIVO
>    - "No me gustó nada" → NEGATIVO  
>    - "El producto es normal" → NEUTRO
> 3. Lea textos del usuario por consola y los clasifique
>
> **Bonus:** Que el modelo responda en JSON: `{"texto": "...", "sentimiento": "POSITIVO", "confianza": 0.95}`

#### Ejercicio autónomo: Salida JSON con Spring AI (10 min)

> **Reto (Spring AI):** Crea un endpoint que reciba un texto y devuelva un POJO:
> ```java
> record Analisis(String sentimiento, double confianza, List<String> palabrasClave) {}
>
> @GetMapping("/analizar")
> public Analisis analizar(@RequestParam String texto) {
>     return chatClient.prompt()
>         .user("Analiza el sentimiento de: " + texto)
>         .call()
>         .entity(Analisis.class);
> }
> ```

---

## BLOQUE 6 — Chatbots con Memoria Persistente

### Contenido Teórico

1. **El problema de la memoria**
   - Los LLMs son stateless: no recuerdan conversaciones anteriores
   - Solución: enviar historial completo en cada request
   - Límite: ventana de contexto (4K, 16K, 128K tokens)

2. **Tipos de memoria**
   - **Buffer Memory:** historial completo (simple, costoso)
   - **Window Memory:** últimos N mensajes
   - **Summary Memory:** resumen de la conversación
   - **Persistente:** guardar en BD (H2, PostgreSQL, Redis)

3. **Implementación en Java puro**
   - `List<Mensaje>` en memoria → `chatConHistorial()` del `ServicioIA`
   
4. **Implementación con Spring AI**
   - `ChatMemory` interface → `InMemoryChatMemory`, `JdbcChatMemory`
   - `MessageChatMemoryAdvisor` para inyectar memoria automáticamente

5. **Implementación con LangChain4j**
   - `ChatMemory` → `MessageWindowChatMemory`, `TokenWindowChatMemory`
   - `ChatMemoryProvider` + `@MemoryId` para multi-usuario

### Lab: Chatbot con memoria (Fase 3 extendido)

**Base:** `fase3/src/main/java/.../DemoServicioIA.java`

#### Ejercicio guiado: Chatbot con historial (20 min)

> Crear `ChatbotConMemoria.java` en fase3:
> ```java
> public class ChatbotConMemoria {
>     public static void main(String[] args) {
>         ServicioIA servicio = new ServicioIA();
>         List<ServicioIA.Mensaje> historial = new ArrayList<>();
>         
>         // System prompt persistente
>         historial.add(new ServicioIA.Mensaje("system", 
>             "Eres un tutor de Java. Recuerdas todo lo que el alumno te dice."));
>         
>         Scanner sc = new Scanner(System.in);
>         System.out.println("Chatbot con memoria. Escribe 'salir' para terminar.");
>         
>         while (true) {
>             System.out.print("Tú> ");
>             String input = sc.nextLine();
>             if ("salir".equalsIgnoreCase(input.trim())) break;
>             
>             String respuesta = servicio.chatConHistorial(historial, input);
>             historial.add(ServicioIA.Mensaje.usuario(input));
>             historial.add(ServicioIA.Mensaje.asistente(respuesta));
>             
>             System.out.println("Bot> " + respuesta);
>         }
>     }
> }
> ```
>
> **Probar:** Decirle "Me llamo Carlos", luego preguntarle "¿Cómo me llamo?" → debe recordar.

#### Ejercicio autónomo: Memoria persistente con Spring AI (25 min)

> **Reto:** En el proyecto Spring Boot:
> 1. Agregar dependencia `spring-ai-jdbc` y H2
> 2. Configurar `ChatMemory` con `JdbcChatMemory`
> 3. Crear endpoint `POST /chat/{sessionId}` que mantenga conversaciones separadas por sesión
> 4. Reiniciar la app y verificar que la memoria persiste
>
> ```java
> @PostMapping("/chat/{sessionId}")
> public String chat(@PathVariable String sessionId, @RequestBody String mensaje) {
>     return chatClient.prompt()
>         .advisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 20))
>         .user(mensaje)
>         .call()
>         .content();
> }
> ```

---

## BLOQUE 7 — Embeddings y Búsqueda Semántica

### Contenido Teórico

1. **¿Qué es un embedding?**
   - Representación vectorial de texto (arreglo de floats)
   - Modelos: `text-embedding-ada-002`, `text-embedding-3-small`
   - Similitud: coseno, distancia euclidiana
   - Ejemplo visual: "gato" y "felino" están cerca; "gato" y "avión" lejos

2. **Vector Databases**
   - ¿Para qué? Búsqueda eficiente por similitud en millones de vectores
   - Opciones: PgVector (PostgreSQL), Chroma, Milvus, Pinecone, Qdrant, Redis
   - Concepto: indexar → buscar → obtener top-K resultados

3. **Flujo de embeddings**
   ```
   Texto → Modelo Embedding → Vector [0.12, -0.34, 0.56, ...] → Almacenar en VectorDB
                                                                        ↓
   Query → Modelo Embedding → Vector [0.11, -0.33, 0.55, ...] → Buscar similares → Top K resultados
   ```

4. **Spring AI: VectorStore**
   - `EmbeddingModel` → genera embeddings
   - `VectorStore` → almacena y busca (PgVector, Chroma, SimpleVectorStore)
   - `Document` → unidad de texto + metadata

5. **LangChain4j: EmbeddingStore**
   - `EmbeddingModel` + `EmbeddingStore` (InMemory, Chroma, PgVector)
   - `EmbeddingStoreIngestor` para cargar documentos

### Lab: Embeddings con Spring AI (30 min)

#### Ejercicio guiado: Búsqueda semántica en memoria

> **Paso 1:** Agregar dependencia en el proyecto Spring Boot:
> ```xml
> <dependency>
>     <groupId>org.springframework.ai</groupId>
>     <artifactId>spring-ai-tika-document-reader</artifactId>
> </dependency>
> ```
>
> **Paso 2:** Crear servicio de búsqueda semántica:
> ```java
> @Service
> public class BusquedaSemantica {
>
>     private final VectorStore vectorStore;
>
>     public BusquedaSemantica(EmbeddingModel embeddingModel) {
>         this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
>         cargarDocumentos();
>     }
>
>     private void cargarDocumentos() {
>         List<Document> docs = List.of(
>             new Document("Java es un lenguaje de programación orientado a objetos creado por Sun Microsystems."),
>             new Document("Python es un lenguaje interpretado conocido por su simplicidad."),
>             new Document("Spring Boot facilita la creación de aplicaciones Java empresariales."),
>             new Document("Docker permite empaquetar aplicaciones en contenedores portátiles."),
>             new Document("Kubernetes orquesta contenedores en clusters distribuidos.")
>         );
>         vectorStore.add(docs);
>     }
>
>     public List<Document> buscar(String consulta) {
>         return vectorStore.similaritySearch(
>             SearchRequest.builder().query(consulta).topK(3).build()
>         );
>     }
> }
> ```
>
> **Paso 3:** Endpoint REST:
> ```java
> @GetMapping("/buscar")
> public List<String> buscar(@RequestParam String q) {
>     return busquedaSemantica.buscar(q).stream()
>         .map(Document::getText)
>         .toList();
> }
> ```
>
> **Probar:**
> ```bash
> curl "http://localhost:8080/buscar?q=lenguaje+de+programación"
> # → Devuelve Java y Python (semánticamente similares)
>
> curl "http://localhost:8080/buscar?q=contenedores"
> # → Devuelve Docker y Kubernetes
> ```

#### Ejercicio autónomo (30 min)

> **Reto:** Cargar un archivo PDF o TXT como documentos y buscar en él:
> 1. Colocar un PDF en `src/main/resources/docs/`
> 2. Usar `TikaDocumentReader` para leerlo
> 3. Dividirlo en chunks con `TokenTextSplitter`
> 4. Almacenarlo en el `VectorStore`
> 5. Buscar contenido del PDF por pregunta natural

---

## BLOQUE 8 — RAG (Retrieval Augmented Generation)

### Contenido Teórico

1. **¿Qué es RAG?**
   - Problema: los LLMs no conocen tus datos privados
   - Solución: recuperar contexto relevante → inyectar en el prompt → generar respuesta
   - Ventaja sobre fine-tuning: más barato, actualizable, sin reentrenar

2. **Flujo RAG completo**
   ```
   ┌──────────────────── INDEXACIÓN (offline) ───────────────────┐
   │ Documentos → Chunks → Embeddings → VectorStore             │
   └─────────────────────────────────────────────────────────────┘
   
   ┌──────────────────── CONSULTA (online) ──────────────────────┐
   │ Pregunta del usuario                                        │
   │   → Embedding de la pregunta                                │
   │   → Búsqueda en VectorStore (top-K chunks)                  │
   │   → Construir prompt: system + contexto + pregunta          │
   │   → Enviar al LLM                                           │
   │   → Respuesta basada en TUS datos                           │
   └─────────────────────────────────────────────────────────────┘
   ```

3. **RAG en Java puro (sin vector DB)**
   - Inyectar contexto directamente en el system prompt (como en fase4)
   - Limitación: no escala a documentos grandes

4. **RAG con Spring AI**
   - `QuestionAnswerAdvisor` + `VectorStore`
   - Automático: busca contexto → inyecta en prompt → responde

5. **RAG con LangChain4j**
   - `ContentRetriever` + `EmbeddingStoreContentRetriever`
   - `AiServices` con `@Tool` y retriever integrado

### Lab: RAG Completo

#### Ejercicio guiado: RAG simple en Java puro (15 min)

**Archivo:** `fase4/src/main/java/.../RAGSimple.java`

1. Ejecutar y analizar el código:
   ```bash
   mvn -pl fase4 exec:java
   ```
2. Observar cómo el `SYSTEM_RAG` inyecta el documento como contexto
3. Preguntar: "¿Qué se hace en la fase 3?" → debe responder del contexto
4. Preguntar algo fuera del contexto → debe decir "No encontré esa información"

#### Ejercicio guiado: RAG con Spring AI + VectorStore (25 min)

> En el proyecto Spring Boot:
> ```java
> @RestController
> public class RAGController {
>
>     private final ChatClient chatClient;
>     private final VectorStore vectorStore;
>
>     public RAGController(ChatClient.Builder builder, VectorStore vectorStore) {
>         this.chatClient = builder.build();
>         this.vectorStore = vectorStore;
>     }
>
>     @PostMapping("/rag")
>     public String rag(@RequestBody String pregunta) {
>         return chatClient.prompt()
>             .advisors(new QuestionAnswerAdvisor(vectorStore))
>             .user(pregunta)
>             .call()
>             .content();
>     }
> }
> ```
>
> **Probar:** Subir documentos al vector store, luego preguntar sobre ellos.

#### Ejercicio autónomo (20 min)

> **Reto completo:** Construir un "Asistente de documentación" que:
> 1. Cargue 3-5 archivos Markdown de un proyecto open source
> 2. Los divida en chunks y los indexe en SimpleVectorStore
> 3. Exponga un endpoint `/preguntar` que haga RAG
> 4. Responda preguntas solo con la información de los docs

---

## BLOQUE 9 — Tool Calling e Integración con APIs Externas

### Contenido Teórico

1. **¿Qué es Tool Calling (Function Calling)?**
   - El LLM no ejecuta código, pero puede **decidir** qué función llamar
   - Flujo: Pregunta → LLM decide "necesito llamar `obtenerClima(ciudad)`" → Tu app ejecuta → Resultado al LLM → Respuesta final
   - Antes: "function calling"; ahora: "tool calling" (estándar OpenAI)

2. **Flujo de Tool Calling**
   ```
   Usuario: "¿Qué clima hace en Lima?"
       ↓
   LLM: "Necesito llamar obtenerClima(ciudad='Lima')"
       ↓
   Tu App: ejecuta obtenerClima("Lima") → {temp: 22, estado: "nublado"}
       ↓
   LLM: "En Lima hace 22°C y está nublado"
       ↓
   Usuario recibe respuesta enriquecida
   ```

3. **Tool Calling con Spring AI**
   - Definir funciones como `@Bean` de tipo `Function<Request, Response>`
   - Registrar tools con `.toolNames("nombreTool")`
   - Spring AI maneja el loop automáticamente

4. **Tool Calling con LangChain4j**
   - Anotar métodos con `@Tool`
   - Registrar herramientas en `AiServices.builder().tools(misTools)`
   - Soporte para descripciones y parámetros tipados

### Lab: Tool Calling

#### Ejercicio guiado: Demo real con Spring AI (25 min)

**Módulo:** `fase4-spring-ai-tool-calling`

> **Paso 1:** Revisar herramientas reales en `ToolConfig.java`:
> ```java
> @Configuration
> public class ToolConfig {
>
>     @Bean("obtenerClima")
>     @Description("Obtiene el clima actual para una ciudad dada")
>     public Function<ClimaRequest, ClimaResponse> obtenerClima() {
>         return request -> {
>             // Simulación de clima para ciudades conocidas
>             return new ClimaResponse(request.ciudad(), "22°C", "Parcialmente nublado", "78%");
>         };
>     }
>
>     @Bean("consultarPais")
>     @Description("Consulta información real de un país")
>     public Function<PaisRequest, PaisResponse> consultarPais() {
>         // API real: https://restcountries.com/v3.1/name/{pais}
>     }
> }
> ```
>
> **Paso 2:** Revisar endpoint en `ToolCallingController`:
> ```java
> @PostMapping("/chat")
> public ChatResponse chat(@RequestBody ChatRequest request) {
>     String response = chatClient.prompt()
>         .toolNames("obtenerClima", "consultarPais")
>         .user(request.message())
>         .call()
>         .content();
>
>     return new ChatResponse(response);
> }
> ```
>
> **Probar:**
> ```bash
> # Puerto 8081
> curl -s -X POST http://localhost:8081/api/tool-calling/chat \
>   -H "Content-Type: application/json" \
>   -d '{"message": "¿Cómo está el clima en Lima?"}' | jq .
>
> curl -s -X POST http://localhost:8081/api/tool-calling/chat \
>   -H "Content-Type: application/json" \
>   -d '{"message": "Cuéntame sobre Japón: capital, población e idiomas"}' | jq .
> ```

#### Ejercicio guiado: Demo real con LangChain4j (20 min)

**Módulo:** `fase4-langchain4j-tool-calling`

> ```java
> @Component
> public class CalculadoraTools {
>
>     @Tool("Suma dos números y retorna el resultado")
>     public double sumar(double a, double b) {
>             return a + b;
>     }
>
>     @Tool("Divide dos números")
>     public double dividir(double a, double b) {
>         return a / b;
>     }
> }
> ```

> Configuración real en `LangChain4jConfig`:
> ```java
> AiServices.builder(Assistant.class)
>     .chatLanguageModel(chatLanguageModel)
>     .tools(calculadoraTools, fechaTools, paisApiTools)
>     .build();
> ```

> **Probar (puerto 8082):**
> ```bash
> curl -s -X POST http://localhost:8082/api/tool-calling/chat \
>   -H "Content-Type: application/json" \
>   -d '{"message": "¿Cuánto es 125 multiplicado por 37?"}' | jq .
>
> curl -s -X POST http://localhost:8082/api/tool-calling/chat \
>   -H "Content-Type: application/json" \
>   -d '{"message": "¿Qué fecha es hoy?"}' | jq .
> ```

#### Extensión opcional: conectar RAG + Tool Calling (10 min)

> Si quieres una demo puente de arquitectura completa, usa `fase4-spring-ai-ollama-pgvector` para recuperar contexto con `/api/rag/advisor` o `/api/buscar`, y luego continúa con Tool Calling en los módulos de Labs 13-14.

---

## BLOQUE 10 — AI Agents con LangChain4j

### Contenido Teórico

1. **¿Qué es un AI Agent?**
   - Un agente es un LLM que puede **razonar**, **planificar** y **actuar**
   - Diferencia con chatbot: el agente decide qué herramientas usar y en qué orden
   - Patrón ReAct: Reason → Act → Observe → Repeat

2. **Componentes de un Agent**
   ```
   ┌────────────────────────────────────────────┐
   │                 AI AGENT                    │
   │                                             │
   │  ┌─────────┐  ┌─────────┐  ┌────────────┐ │
   │  │   LLM   │  │  Tools  │  │   Memory   │ │
   │  │(cerebro)│  │(acciones)│  │(contexto)  │ │
   │  └─────────┘  └─────────┘  └────────────┘ │
   │                                             │
   │  ┌─────────────────────────────────────┐   │
   │  │         Orquestador (loop)          │   │
   │  │  1. Recibe pregunta                 │   │
   │  │  2. LLM razona qué hacer            │   │
   │  │  3. Ejecuta herramienta             │   │
   │  │  4. Observa resultado               │   │
   │  │  5. Repite hasta tener respuesta    │   │
   │  └─────────────────────────────────────┘   │
   └────────────────────────────────────────────┘
   ```

3. **Agents con LangChain4j**
   - `AiServices` + `@Tool` + `ChatMemory` = Agente completo
   - El modelo decide cuándo y qué tools usar
   - Memoria mantiene el contexto entre iteraciones

4. **Multi-Agent (avanzado)**
   - Múltiples agentes especializados que colaboran
   - Ejemplo: Agente investigador + Agente escritor + Agente revisor

### Lab: Construir un AI Agent

#### Ejercicio guiado (30 min)

> **Agente "Asistente de Desarrollo" con 3 herramientas:**
> ```java
> public class AgenteDesarrollo {
>
>     // --- HERRAMIENTAS ---
>     static class DevTools {
>
>         @Tool("Busca información en la documentación del proyecto")
>         public String buscarDocs(String query) {
>             // Simulación de búsqueda en docs
>             Map<String, String> docs = Map.of(
>                 "deployment", "Despliegue con Docker: docker build -t app . && docker run -p 8080:8080 app",
>                 "database", "Base de datos: PostgreSQL 15, conexión en application.properties",
>                 "api", "API REST en /api/v1/*, autenticación con JWT"
>             );
>             return docs.entrySet().stream()
>                 .filter(e -> e.getKey().contains(query.toLowerCase()))
>                 .map(Map.Entry::getValue)
>                 .findFirst()
>                 .orElse("No encontré documentación sobre: " + query);
>         }
>
>         @Tool("Ejecuta un comando en el sistema y retorna la salida")
>         public String ejecutarComando(String comando) {
>             // Solo permitir comandos seguros (whitelist)
>             List<String> permitidos = List.of("java --version", "mvn --version", "git status");
>             if (!permitidos.contains(comando)) {
>                 return "Comando no permitido por seguridad: " + comando;
>             }
>             // Ejecutar de forma segura...
>             return "Salida simulada de: " + comando;
>         }
>
>         @Tool("Obtiene las dependencias Maven del proyecto")
>         public String obtenerDependencias() {
>             return "Dependencias: spring-boot-starter-web, spring-ai-openai, langchain4j-open-ai, postgresql";
>         }
>     }
>
>     // --- INTERFAZ DEL AGENTE ---
>     interface AsistenteIA {
>         String chat(@MemoryId String sessionId, @UserMessage String mensaje);
>     }
>
>     public static void main(String[] args) {
>         ChatLanguageModel model = OpenAiChatModel.builder()
>             .apiKey(EnvConfig.getOpenAiApiKey())
>             .modelName("gpt-4")   // GPT-4 es mejor para agentes
>             .build();
>
>         AsistenteIA agente = AiServices.builder(AsistenteIA.class)
>             .chatLanguageModel(model)
>             .chatMemoryProvider(memId -> MessageWindowChatMemory.withMaxMessages(20))
>             .tools(new DevTools())
>             .systemMessageProvider(memId -> "Eres un asistente de desarrollo. "
>                 + "Usa las herramientas disponibles para responder. "
>                 + "Si necesitas varias herramientas, úsalas en secuencia.")
>             .build();
>
>         Scanner sc = new Scanner(System.in);
>         String sessionId = "sesion-1";
>         System.out.println("Agente de desarrollo listo. Escribe 'salir' para terminar.");
>
>         while (true) {
>             System.out.print("Tú> ");
>             String input = sc.nextLine();
>             if ("salir".equalsIgnoreCase(input.trim())) break;
>
>             String respuesta = agente.chat(sessionId, input);
>             System.out.println("Agente> " + respuesta);
>         }
>     }
> }
> ```
>
> **Probar:**
> - "¿Cómo despliego la app?" → usa `buscarDocs("deployment")`
> - "¿Qué versión de Java tenemos?" → usa `ejecutarComando("java --version")`
> - "¿Qué dependencias usa el proyecto?" → usa `obtenerDependencias()`
> - "Dame un resumen del stack técnico" → usa múltiples herramientas

#### Ejercicio autónomo (30 min)

> **Reto:** Crear un agente "Analista de datos" que tenga 3 herramientas:
> 1. `leerCSV(archivo)` — lee un CSV y retorna las primeras 5 filas
> 2. `calcularEstadisticas(columna)` — retorna min, max, promedio de una columna
> 3. `generarReporte(titulo)` — genera un mini-reporte en Markdown
>
> El usuario debe poder decir: "Analiza el archivo ventas.csv y dime cuál fue el mejor mes"

---

## BLOQUE 11 — Arquitectura Completa (Backend + Frontend)

### Contenido Teórico

1. **Arquitectura de referencia**
   ```
   ┌─────────────────────────────────────────────────────────────┐
   │                        FRONTEND                             │
   │   React / Angular / Thymeleaf / HTMX                       │
   │   - Chat UI con mensajes                                    │
   │   - Upload de documentos                                    │
   │   - Indicador de "pensando..."                              │
   └────────────────────────┬────────────────────────────────────┘
                            │ REST / WebSocket / SSE
   ┌────────────────────────▼────────────────────────────────────┐
   │                    SPRING BOOT BACKEND                       │
   │                                                              │
   │  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
   │  │ ChatController│  │ RAGService   │  │ AgentService     │  │
   │  │ (REST/WS)    │  │ (retrieval)  │  │ (tools+memory)   │  │
   │  └──────┬───────┘  └──────┬───────┘  └──────┬───────────┘  │
   │         │                  │                  │              │
   │  ┌──────▼──────────────────▼──────────────────▼───────────┐ │
   │  │              Spring AI / LangChain4j                    │ │
   │  │  ChatClient  │  VectorStore  │  AiServices + Tools      │ │
   │  └──────┬──────────────┬──────────────────┬───────────────┘ │
   │         │              │                  │                  │
   │  ┌──────▼───┐   ┌─────▼──────┐    ┌─────▼──────┐          │
   │  │ OpenAI   │   │ PgVector / │    │ APIs ext.  │          │
   │  │ / Ollama │   │ Chroma     │    │ (clima,etc)│          │
   │  └──────────┘   └────────────┘    └────────────┘          │
   │                                                              │
   │  ┌────────────────────────────────────────────────────────┐ │
   │  │              PostgreSQL / H2                             │ │
   │  │  - Chat history  - User sessions  - Vector embeddings   │ │
   │  └────────────────────────────────────────────────────────┘ │
   └──────────────────────────────────────────────────────────────┘
   ```

2. **Streaming de respuestas**
   - Server-Sent Events (SSE) para respuestas token por token
   - Spring AI: `.stream()` en vez de `.call()`
   - Frontend: `EventSource` API
   - UX: el usuario ve la respuesta generándose en tiempo real

3. **Consideraciones de producción**
   - Rate limiting y control de costos
   - Caching de respuestas similares
   - Logging de conversaciones (auditoría)
   - Seguridad: no exponer API keys, validar input
   - Observabilidad: traces con OpenTelemetry + Micrometer

4. **Frontend: opciones**
   - **HTMX + Thymeleaf:** fullstack Java, sin JS framework
   - **React/Next.js:** SPA con `fetch` al backend
   - **Angular:** enterprise, con HttpClient
   - **Vaadin/Hilla:** fullstack Java con componentes web

### Lab: App completa con streaming (30 min)

#### Ejercicio guiado: Backend con streaming SSE

> ```java
> @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
> public Flux<String> chatStream(@RequestParam String mensaje) {
>     return chatClient.prompt()
>         .user(mensaje)
>         .stream()
>         .content();
> }
> ```

#### Ejercicio guiado: Frontend mínimo con HTML + JS

> ```html
> <!DOCTYPE html>
> <html>
> <head><title>Chat IA</title></head>
> <body>
>     <div id="chat" style="height:400px; overflow-y:auto; border:1px solid #ccc; padding:10px;"></div>
>     <input id="input" placeholder="Escribe tu mensaje..." style="width:80%">
>     <button onclick="enviar()">Enviar</button>
>
>     <script>
>         function enviar() {
>             const input = document.getElementById('input');
>             const chat = document.getElementById('chat');
>             const mensaje = input.value;
>             input.value = '';
>             
>             chat.innerHTML += '<p><b>Tú:</b> ' + mensaje + '</p>';
>             const respP = document.createElement('p');
>             respP.innerHTML = '<b>Bot:</b> ';
>             chat.appendChild(respP);
>             
>             const source = new EventSource('/chat/stream?mensaje=' + encodeURIComponent(mensaje));
>             source.onmessage = (e) => {
>                 respP.innerHTML += e.data;
>                 chat.scrollTop = chat.scrollHeight;
>             };
>             source.onerror = () => source.close();
>         }
>     </script>
> </body>
> </html>
> ```

#### Ejercicio autónomo: App completa (30 min)

> **Reto final:** Construir una app completa que combine todo lo visto:
> 1. **Backend Spring Boot** con:
>    - Endpoint de chat con memoria por sesión
>    - RAG sobre documentación cargada
>    - Al menos 1 tool (ej: buscar en Google, calcular, obtener fecha)
>    - Streaming SSE
> 2. **Frontend** (HTML/JS o React):
>    - UI de chat con historial visual
>    - Botón para subir documentos (para RAG)
>    - Streaming visual de respuestas
> 3. **Persistencia:**
>    - H2 o PostgreSQL para historial de chat
>    - SimpleVectorStore o PgVector para embeddings

---

## Resumen de Labs por Fase del Proyecto

| Módulo | Lab | Código existente | Por crear |
|--------|-----|-----------------|-----------|
| `fase1` | Primera llamada a OpenAI | ✅ `PrimeraLlamadaOpenAI.java` | Reto: agregar system prompt |
| `fase2` | Prompt Engineering | ✅ `PromptEngineering.java` | `ClasificadorSentimiento.java` |
| `fase3` | Servicio IA + Chatbot | ✅ `DemoServicioIA.java` | `ChatbotConMemoria.java` |
| `fase4` | Lab 11 - RAG Simple | ✅ `RAGSimple.java` | Reto: RAG con más documentos |
| `fase4-spring-ai-ollama-pgvector` | Lab 12+ (opcional) - RAG persistente | ✅ `RagController`, `BusquedaController` | Ajustar dataset y prompts para caso real |
| `fase4-spring-ai-tool-calling` | Lab 13 - Tool Calling Spring AI | ✅ `ToolConfig`, `ToolCallingController` | Reto: nueva tool externa |
| `fase4-langchain4j-tool-calling` | Lab 14 - Tool Calling LangChain4j | ✅ `CalculadoraTools`, `FechaTools`, `PaisApiTools` | Reto: tool adicional con validación |
| Spring Boot (integrador) | Labs 15-17 y proyecto final | — | Integrar memoria + RAG + tools + streaming |

---

## Checklist del Instructor

- [ ] `.env` configurado con API key válida
- [ ] `mvn clean install` compila sin errores
- [ ] Fase 1 ejecuta y obtiene respuesta de OpenAI
- [ ] Proyecto Spring Boot de ejemplo preparado
- [ ] PDFs/documentos de ejemplo para lab de RAG
- [ ] WiFi estable para llamadas a la API
- [ ] Plan B: Ollama local por si la API tiene límites

---

## Recursos Adicionales

- **Spring AI docs:** https://docs.spring.io/spring-ai/reference/
- **LangChain4j docs:** https://docs.langchain4j.dev/
- **OpenAI API reference:** https://platform.openai.com/docs/api-reference
- **Ollama (modelos locales):** https://ollama.ai/
- **PgVector:** https://github.com/pgvector/pgvector
