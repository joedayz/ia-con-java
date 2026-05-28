# springai-workshop

Workshop para construir aplicaciones con IA usando **Spring Boot** y **Spring AI**, con **OpenAI** como proveedor por defecto en section-1.

Es el equivalente en Spring del workshop [quarkus-workshop-langchain4j](../quarkus-workshop-langchain4j/): mismo dominio (Miles of Smiles, agente de soporte al cliente) y progresión por pasos.

## Requisitos

- Java 21+
- Maven 3.8+
- `OPENAI_API_KEY` configurada en el entorno

```bash
export OPENAI_API_KEY=sk-...
```

**Step 06:** levanta Postgres con pgvector antes de la app:

```bash
cd section-1/step-06
docker compose up -d
```

(Puerto host **5436** → contenedor 5432; ver `application.properties` para `POSTGRES_*` opcionales.)

## Ejecutar un paso

```bash
cd section-1/step-02   # o step-01, step-03, ...
./mvnw spring-boot:run
```

Tras cambiar `application.properties`, reinicia la app (Ctrl+C y vuelve a ejecutar). Refresca el navegador.

Abre [http://localhost:8080](http://localhost:8080) y usa el chat en la esquina inferior derecha.

## Estructura

| Directorio | Descripción |
|------------|-------------|
| `section-1/step-01` | Integración básica con OpenAI + WebSocket + UI de chat |
| `section-1/step-02` | Configuración del LLM: temperature, max-tokens, top-p |
| `section-1/step-03` | Streaming de respuestas token a token por WebSocket |
| `section-1/step-04` | System message: rol y alcance del agente Miles of Smiles |
| `section-1/step-05` | RAG (EasyRAG): ingesta + `QuestionAnswerAdvisor` |
| `section-1/step-06` | RAG manual + **PgVector** (PostgreSQL en Docker) + `RagIngestion` / `RagRetriever` |
| `section-1/step-07` | Tools / function calling: reservas con JPA + PostgreSQL |
| `section-1/step-08` | Cliente MCP + tools locales |
| `section-1/step-08-mcp-server` | Servidor MCP de clima (puerto 8081) |
| `section-1/step-09` | Input guardrails: detección de prompt injection |
| `section-1/step-10` | Observabilidad + fault tolerance (Resilience4j) |
| `section-1/step-11` | LLM local (Ollama) + output guardrail numérico |
| `section-2/step-01` | Primer agente autónomo: flota de coches + `CleaningAgent` |
| `section-2/step-02` | Workflow paralelo: limpieza + condición del coche |
| `section-2/step-03` | Workflows secuencial + paralelo + condicional (mantenimiento/limpieza) |
| `section-2/step-04` | Patrón supervisor + análisis de disposición |
| `section-2/step-05` | Human-in-the-loop: aprobación humana para disposición |
| `section-2/step-06` | Multimodal: `CarImageAnalysisAgent` enriquece feedback con foto |
| `section-2/step-07` | Agente remoto A2A: `RemotePricingAgent` → servicio en :8888 |
| `section-2/step-07-remote-pricing` | Servidor del agente de pricing (A2A / REST) |

Cada paso es una aplicación Spring Boot independiente y ejecutable.

## Section 2 — Sistemas agénticos

Escenario distinto: **gestión de flota** de Miles of Smiles (devoluciones de alquiler, limpieza). En Quarkus se usa `quarkus-langchain4j-agentic` (`@Agent`, `@SequenceAgent`, `@SupervisorAgent`); en Spring AI los workflows se orquestan con servicios Java + `ChatClient` y tools.

| Quarkus | Spring AI (este workshop) |
|---------|---------------------------|
| `@Agent` + `@ToolBox` | `ChatClient` + `.defaultTools(...)` |
| `@ParallelAgent` | `FeedbackWorkflow` + `CompletableFuture` (step-03) |
| `@SequenceAgent` | `CarProcessingWorkflow` encadena sub-workflows (step-03) |
| `@ConditionalAgent` | `CarAssignmentWorkflow` (step-03; reemplazado por supervisor en step-04) |
| `@SupervisorAgent` | `FleetSupervisorAgent` + `SupervisorTools` (step-04+) |
| `@HumanInTheLoop` | `HumanApprovalAgent` + `ApprovalService` + UI modal (step-05) |
| `@A2AClientAgent` | `RemotePricingAgent` + `step-07-remote-pricing` (step-07) |
| `@ParallelMapperAgent` | `FeedbackAnalysisWorkflow` + `FeedbackTask` (step-04) |
| `@SupervisorAgent` | Supervisor con `ChatClient` (steps 04+) |
| OpenAI + PostgreSQL Dev Services | OpenAI + H2 en memoria |

**Section 1 y 2** usan **OpenAI** (`OPENAI_API_KEY`; section-1 con `gpt-4o`, section-2 con `gpt-4o-mini`).

```bash
export OPENAI_API_KEY=sk-...

cd section-2/step-01
./mvnw spring-boot:run
```

Abre [http://localhost:8080](http://localhost:8080), elige un coche **Rented**, escribe feedback (p. ej. *Car has dog hair all over the back seat*) y pulsa **Return**.

```bash
cd section-2/step-02
./mvnw spring-boot:run
```

Tras un **Return**, la columna **Condition** se actualiza; dos agentes corren en paralelo (`CleaningAgent` + `CarConditionFeedbackAgent`).

### Step 03 — tres patrones de workflow

```bash
cd section-2/step-03
./mvnw spring-boot:run
```

1. **Paralelo** (`FeedbackWorkflow`): `CleaningFeedbackAgent` + `MaintenanceFeedbackAgent`
2. **Condicional** (`CarAssignmentWorkflow`): solo `MaintenanceAgent` o `CleaningAgent` si aplica (mantenimiento primero)
3. **Secuencial** (`CarProcessingWorkflow`): feedback → asignación → `CarConditionFeedbackAgent`

Prueba mantenimiento: *Engine making strange knocking noise*. Prueba limpieza: *Dog hair all over back seat*.

### Step 04 — patrón supervisor

```bash
cd section-2/step-04
./mvnw spring-boot:run
```

1. **Paralelo** (`FeedbackAnalysisWorkflow`): mismo agente, 3 tareas (limpieza, mantenimiento, disposición)
2. **Supervisor** (`FleetSupervisorAgent`): decide qué tools invocar (`PricingAgent`, `DispositionAgent`, etc.)
3. **Final** (`CarConditionFeedbackAgent`): JSON estructurado → `CarConditions`

Prueba disposición: *Car was totaled in a collision, frame damage*. Prueba limpieza: *Dog hair on seats*.

### Step 05 — Human-in-the-Loop

```bash
cd section-2/step-05
./mvnw spring-boot:run
```

Vehículo de **alto valor** con daño severo (p. ej. BMW X5: *totaled in collision, frame damage*):

1. El workflow **se pausa** y aparece el botón flotante **Approval Needed**
2. Elige **Keep & Repair** o **Dispose** en el modal
3. El workflow **reanuda** y actualiza la flota

API: `GET /api/approvals/pending`, `POST /api/approvals/{id}/decide`

### Step 06 — multimodal

```bash
cd section-2/step-06
./mvnw spring-boot:run
```

En el formulario **Return** puedes adjuntar una foto del coche. `CarImageAnalysisAgent` (visión con `gpt-4o-mini`) fusiona texto + imagen en un único feedback enriquecido antes del resto del workflow.

Imagen de ejemplo en el workshop: `src/main/resources/static/samples/q4-tree.png` (árbol sobre el capó — prueba disposición).

### Step 07 — agente remoto (A2A)

Dos procesos (como en Quarkus):

```bash
# Terminal 1 — servicio remoto de pricing
cd section-2/step-07-remote-pricing
./mvnw spring-boot:run

# Terminal 2 — sistema multi-agente
cd section-2/step-07
./mvnw spring-boot:run
```

- Descubrimiento: [http://localhost:8888/.well-known/agent.json](http://localhost:8888/.well-known/agent.json)
- El supervisor invoca `RemotePricingAgent` (equiv. `@A2AClientAgent`) en lugar del LLM local
- En Quarkus se usa JSON-RPC A2A; aquí REST HTTP documentado como facade del mismo contrato

**Section 2 completa** (steps 01–07).

## Paridad con el workshop Quarkus

| Quarkus + LangChain4j | Spring AI |
|-----------------------|-----------|
| `@RegisterAiService` | `ChatClient` + servicio Spring |
| `@SessionScoped` | Memoria por `WebSocketSession` id |
| `Multi<String>` (streaming) | `Flux<String>` con `ChatClient.stream().content()` |
| `@SystemMessage` | `ChatClient.defaultSystem(...)` |
| EasyRAG | `EasyRagIngestor` + `QuestionAnswerAdvisor` + `SimpleVectorStore` |
| `RagIngestion` / `RagRetriever` | `RagIngestion` + `RagRetriever.augmentUserMessage()` |
| `bge-small-en-q` (ONNX) | OpenAI `text-embedding-3-small` (embeddings) |
| PgVector | **Step 06:** `spring-ai-starter-vector-store-pgvector` + Docker Compose. Steps 05 y 07–11: `SimpleVectorStore` en memoria (sin Docker) |
| `@Tool` / `@ToolBox` | `@Tool` en `BookingTools` + `ChatClient.tools(...)` |
| Panache + PostgreSQL | Spring Data JPA + PostgreSQL (docker compose de step-06) |
| `@McpToolBox("weather")` | `spring-ai-starter-mcp-client` + `ToolCallbackProvider` |
| `@InputGuardrails` | `PromptInjectionGuard` + `PromptInjectionDetectionService` |
| `log-requests` / `log-responses` | `logging.level.org.springframework.ai.*=DEBUG` |
| Micrometer / OpenTelemetry | Actuator + Prometheus + OTLP tracing |
| `@Timeout` / `@Retry` / `@Fallback` | `ResilientLlmInvoker` (Resilience4j) |
| Jlama (inferencia Java embebida) | Perfil `ollama-local` con Ollama (`llama3.2`) |
| `@OutputGuardrails` | `NumericOutputSanitizer` en `PromptInjectionDetectionService` |
| `@Agent` / `@ToolBox` | `CleaningAgent` + `CleaningTool` (`section-2`) |

### Step 11 — LLM local y sanitización de salida

Por defecto usa **OpenAI** para el chat. Para probar un modelo local (equivalente a Jlama en el workshop Quarkus):

```bash
ollama pull llama3.2
cd section-1/step-11
./mvnw spring-boot:run -Dspring-boot.run.profiles=ollama-local
```

Los modelos pequeños suelen devolver texto junto al score numérico del detector de injection; `NumericOutputSanitizer` extrae el número antes de evaluar el umbral.

### Step 10 — métricas y chaos testing

- Métricas: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)
- Health: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- Para probar fallback, descomenta en `application.properties`: `spring.ai.openai.base-url=https://api.example.com`

### Step 08 — dos procesos

Terminal 1 (servidor MCP):

```bash
cd section-1/step-08-mcp-server
./mvnw spring-boot:run
```

Terminal 2 (cliente chat en :8080):

```bash
cd section-1/step-08
./mvnw spring-boot:run
```
