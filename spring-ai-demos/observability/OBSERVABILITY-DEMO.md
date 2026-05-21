# Demo paso a paso: Observabilidad de IA (Spring AI + Micrometer)

Este demo usa los proyectos:

- `observability/board-game-buddy`: API para hacer preguntas y generar telemetría (métricas + trazas).
- `observability/game-rules-loader` (opcional): carga documentos al vector store (Qdrant) para que el RAG tenga datos “reales”.

Incluye:

- **Métricas**: Actuator `/actuator/metrics` + Prometheus `/actuator/prometheus`
- **Prometheus/Grafana**: dashboards a partir de series Prometheus
- **Tracing**: envío OTLP a Jaeger y visualización en `http://localhost:16686`

---

## 0) Requisitos

- Docker **o** Podman (Podman Desktop o Podman Engine) corriendo
- JDK compatible con los proyectos (recomendado: **Java 21**; el repo usa Spring Boot 3.5.x)
- API Key de OpenAI

---

## 1) Levantar infraestructura (Qdrant + Jaeger + Prometheus + Grafana)

En una terminal, desde `spring-ai-demos/observability/board-game-buddy`.

### Opción A: Docker Compose

```bash
cd spring-ai-demos/observability/board-game-buddy
docker compose up -d
```

### Opción B: Podman Compose

Si tienes `podman-compose` instalado o el subcomando `podman compose`:

```bash
cd spring-ai-demos/observability/board-game-buddy
podman compose up -d
```

Si tu instalación usa `podman-compose` (con guion):

```bash
cd spring-ai-demos/observability/board-game-buddy
podman-compose up -d
```

UIs:

- Jaeger: `http://localhost:16686`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

> Nota macOS + Podman: el archivo `prometheus-config.yml` apunta a `host.docker.internal:8080`.
> Con Podman, el hostname más común para llegar al host es `host.containers.internal`.
> Si Prometheus no scrapea:
> - cambia el target a `host.containers.internal:8080`, o
> - deja `host.docker.internal:8080` si en tu Podman Desktop sí resuelve, o
> - como último recurso, usa la IP que corresponda a tu host/VM de Podman (varía según setup).

> Nota Qdrant: en este repo se recomienda **evitar `qdrant:latest`** para el demo, porque el cliente Java puede exigir
> compatibilidad de versión. Los `compose.yaml` usan una versión fija (`v1.14.1`) para reducir fricción.

---

## 2) Variables de entorno (OpenAI + OTLP)

### macOS/Linux (bash/zsh)

```bash
export OPENAI_API_KEY="TU_KEY"
export OTLP_TRACING_ENABLED=true
export OTLP_TRACING_URL=http://localhost:4317
```

### Windows (PowerShell)

```powershell
$env:OPENAI_API_KEY="TU_KEY"
$env:OTLP_TRACING_ENABLED="true"
$env:OTLP_TRACING_URL="http://localhost:4317"
```

---

## 3) Arrancar Board Game Buddy (app principal del demo)

En otra terminal:

```bash
cd spring-ai-demos/observability/board-game-buddy
./gradlew bootRun
```

Cuando esté arriba, la app escuchará en `http://localhost:8080`.

---

## 4) (Opcional) Poblar el vector store con `game-rules-loader`

Esto hace que el RAG tenga documentos; si lo omites, igual verás telemetría IA, pero el vector store puede estar “vacío”.

En otra terminal:

```bash
cd spring-ai-demos/observability/game-rules-loader
mkdir -p dropoff
./gradlew bootRun --args='--file.supplier.directory=dropoff'
```

Ahora copia documentos al `dropoff/` (pdf/txt/docx). Observa logs hasta ver que escribe en el vector store.

> Nota: el `README.md` de este subproyecto menciona Chroma, pero este repo está configurado para **Qdrant**.
>
> Tip para demo rápido: hay un documento de ejemplo en `game-rules-loader/dropoff-samples/burger-battle.txt`.
> Puedes copiarlo a `dropoff/` para que la pregunta “Classic burger” tenga contexto.

---

## 5) Generar telemetría con requests (curl / PowerShell)

### 5.1) `POST /ask` (chat + advisors + vector store)

#### curl (macOS/Linux)

```bash
curl -sS -X POST "http://localhost:8080/ask" \
  -H "Content-Type: application/json" \
  -H "X_AI_CONVERSATION_ID: demo-obs-1" \
  -d '{"gameTitle":"Burger Battle","question":"What ingredients are on the Classic burger?"}'
```

Para generar métricas/trazas rápido (sin editar a mano), pega esto y listo (3 preguntas, misma conversación):

```bash
for q in \
  "What ingredients are on the Classic burger?" \
  "What ingredients are on the Spicy burger?" \
  "Which burger is best for beginners and why?"
do
  curl -sS -X POST "http://localhost:8080/ask" \
    -H "Content-Type: application/json" \
    -H "X_AI_CONVERSATION_ID: demo-obs-1" \
    -d "{\"gameTitle\":\"Burger Battle\",\"question\":\"$q\"}" >/dev/null
done
echo "OK (3 requests)"
```

#### PowerShell

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/ask" `
  -Headers @{ "X_AI_CONVERSATION_ID"="demo-obs-1" } `
  -ContentType "application/json" `
  -Body (@{ gameTitle="Burger Battle"; question="What ingredients are on the Classic burger?" } | ConvertTo-Json)
```

Para generar métricas/trazas rápido (3 requests, misma conversación):

```powershell
$qs = @(
  "What ingredients are on the Classic burger?",
  "What ingredients are on the Spicy burger?",
  "Which burger is best for beginners and why?"
)

foreach ($q in $qs) {
  Invoke-RestMethod -Method Post -Uri "http://localhost:8080/ask" `
    -Headers @{ "X_AI_CONVERSATION_ID"="demo-obs-1" } `
    -ContentType "application/json" `
    -Body (@{ gameTitle="Burger Battle"; question=$q } | ConvertTo-Json) | Out-Null
}

"OK (3 requests)"
```

Qué estás demostrando:

- Se ejecuta el `ChatClient` con **dos advisors**:
  - `QuestionAnswerAdvisor` (RAG: busca docs similares)
  - `VectorStoreChatMemoryAdvisor` (memoria: guarda/consulta historial por `X_AI_CONVERSATION_ID`)
- Eso dispara operaciones:
  - GenAI (chat y a veces embedding)
  - Vector store (queries/adds)

### 5.2) `GET /burgerBattleArt` (chat + generación de imagen)

#### curl (texto)

```bash
curl -sS "http://localhost:8080/burgerBattleArt?burger=Classic"
```

#### curl (PNG)

```bash
curl -sS -H "Accept: image/png" \
  "http://localhost:8080/burgerBattleArt?burger=Classic" \
  --output burger.png
```

#### PowerShell (PNG)

```powershell
Invoke-WebRequest -Uri "http://localhost:8080/burgerBattleArt?burger=Classic" `
  -Headers @{ Accept = "image/png" } `
  -OutFile "burger.png"
```

> **Error `Unknown parameter: response_format`**: los modelos `gpt-image-1` no admiten ese parámetro. El proyecto usa `gpt-image-1`, `OpenAiImageModelConfig` (defaults sin `response_format`) y `SpringAiImageService` (no lo envía). Si falla, revisa que no tengas `SPRING_AI_OPENAI_IMAGE_OPTIONS_RESPONSE_FORMAT` en el entorno. Recompila: `./gradlew clean bootRun`.

---

## 6) Actuator (validar que hay métricas y encontrar nombres “reales”)

### 6.1) Ver todas las métricas

#### curl

```bash
curl -sS "http://localhost:8080/actuator/metrics"
```

#### PowerShell

```powershell
(Invoke-RestMethod "http://localhost:8080/actuator/metrics").names
```

### 6.2) Inspeccionar una métrica específica

> Los nombres pueden cambiar entre versiones. Si alguna de abajo da 404, usa estos comandos para “descubrir” los nombres y luego cópialos.

#### curl (descubrir nombres rápido) — recomendado con `jq`

```bash
curl -sS "http://localhost:8080/actuator/metrics" \
  | jq -r '.names[]' \
  | egrep '^(gen_ai|spring\.ai|db\.vector)\.' \
  | sort -u
```

#### curl (descubrir nombres rápido) — alternativa sin `jq` (Python 3)

```bash
curl -sS "http://localhost:8080/actuator/metrics" \
  | python3 -c 'import json,sys; print("\n".join(json.load(sys.stdin)["names"]))' \
  | egrep '^(gen_ai|spring\.ai|db\.vector)\.' \
  | sort -u
```

#### PowerShell (descubrir nombres rápido)

```powershell
(Invoke-RestMethod "http://localhost:8080/actuator/metrics").names |
  Where-Object { $_ -match '^(gen_ai|spring\.ai|db\.vector)\.' } |
  Sort-Object -Unique
```

#### curl

```bash
curl -sS "http://localhost:8080/actuator/metrics/gen_ai.client.operation"
curl -sS "http://localhost:8080/actuator/metrics/gen_ai.client.token.usage"
curl -sS "http://localhost:8080/actuator/metrics/db.vector.client.operation"
curl -sS "http://localhost:8080/actuator/metrics/spring.ai.chat.client"
curl -sS "http://localhost:8080/actuator/metrics/spring.ai.advisor"
```

Qué significa cada una (cómo explicarlo en clase):

- **`gen_ai.client.operation`**: latencias y conteos de llamadas al proveedor GenAI (OpenAI en este demo).
  - **`COUNT`**: cuántas operaciones GenAI se han hecho desde que levantaste la app.
  - **`TOTAL_TIME`**: tiempo total acumulado (segundos) de esas operaciones.
  - **`MAX`**: el máximo observado (segundos) para una operación.
  - **Tags útiles**:
    - **`gen_ai.operation.name`**: `chat`, `embedding`, `image` (qué tipo de operación fue).
    - **`gen_ai.request.model` / `gen_ai.response.model`**: modelo pedido vs el que realmente respondió.
    - **`error`**: si hubo error (en tu salida sale `none`).

- **`gen_ai.client.token.usage`**: tokens consumidos por operación/modelo.
  - **`COUNT`**: número de tokens (dependiendo del tag, input/output/total).
  - **Tags útiles**:
    - **`gen_ai.token.type`**: `input`, `output`, `total` (filtra para evitar confusiones).
    - **`gen_ai.operation.name`**: separa tokens de `chat` vs `embedding`.

- **`db.vector.client.operation`**: operaciones y latencia contra el vector store (Qdrant).
  - **Tags útiles**:
    - **`db.operation.name`**: `query` vs `add` (por qué ves “más de una query/add” en un `/ask`).
    - **`db.system`**: `qdrant`.

- **`spring.ai.chat.client`**: tiempo/contador del **framework** (Spring AI `ChatClient`) envolviendo la llamada.
  - Útil para ver “lo que tarda mi pipeline” vs “lo que tarda OpenAI” y “lo que tarda el vector store”.

- **`spring.ai.advisor`**: tiempo/contador de los **advisors** ejecutados.
  - **Tag `spring.ai.advisor.name`** te deja separar, por ejemplo:
    - `VectorStoreChatMemoryAdvisor` (memoria por conversación)
    - `QuestionAnswerAdvisor` (RAG: búsqueda de documentos)

Ejemplo filtrando por tags (si aplica):

```bash
curl -sS "http://localhost:8080/actuator/metrics/gen_ai.client.operation?tag=gen_ai.operation.name:chat"
curl -sS "http://localhost:8080/actuator/metrics/gen_ai.client.token.usage?tag=gen_ai.token.type:input"
```

---

## 7) Prometheus endpoint (formato Prometheus: `gen_ai_*`, `spring_ai_*`, `db_vector_*`)

### 7.1) Ver series en bruto

#### curl

```bash
curl -sS "http://localhost:8080/actuator/prometheus" | egrep '^(gen_ai|spring_ai|db_vector)_'
```

#### PowerShell

```powershell
(Invoke-WebRequest "http://localhost:8080/actuator/prometheus").Content `
  -split "`n" | Select-String "^(gen_ai|spring_ai|db_vector)_"
```

Cómo leer estas líneas (para explicarlo rápido):

- **Formato**: `nombre_metrica{label="valor",...} valor`
  - Los `{...}` son **labels** (también llamados tags/dimensiones). Te sirven para filtrar en Prometheus/Grafana.

- **Sufijos más comunes**:
  - **`*_seconds_count`**: cuántas veces ocurrió la operación (contador).
  - **`*_seconds_sum`**: suma total del tiempo (segundos) acumulado.
  - **`*_seconds_max`**: el máximo observado (segundos) en el periodo/vida del proceso.
  - **`*_active_seconds_*`**: “en este instante” (concurrencia/actividad); suele ser 0 si no estás disparando requests en ese momento.

- **Ejemplos del demo**:
  - **`gen_ai_client_operation_seconds_*`**: operaciones GenAI por tipo (`gen_ai_operation_name=chat|embedding|image`) y modelo.
  - **`gen_ai_client_token_usage_total`**: tokens, filtrables por `gen_ai_token_type=input|output|total`.
  - **`db_vector_client_operation_seconds_*`**: operaciones al vector store, filtrables por `db_operation_name=add|query`.
  - **`spring_ai_*`**: tiempos/contadores del framework (ChatClient/advisors) con labels como `spring_ai_advisor_name`.

---

## 8) Prometheus UI: queries recomendadas

Abre `http://localhost:9090` y prueba:

- **Latencia máxima GenAI** (chat/embedding/image):

```promql
gen_ai_client_operation_seconds_max
```

- **Solo embeddings** (si existe ese label):

```promql
gen_ai_client_operation_seconds_max{gen_ai_operation_name="embedding"}
```

- **Tokens** (si existe):

```promql
gen_ai_client_token_usage_total
```

- **Vector store ops**:

```promql
db_vector_client_operation_seconds_count
```

> Tip: escribe el nombre y Prometheus autocompleta. Para labels, escribe `{` y verás sugerencias.

---

## 9) Grafana (mínimo viable)

Abre `http://localhost:3000` y configura un Data Source de Prometheus:

- **URL**: `http://prometheus:9090`

Luego crea un panel y usa la query:

```promql
gen_ai_client_operation_seconds_max
```

Filtra por label (si existe) para separar chat vs embedding vs image.

---

## 10) Tracing en Jaeger: la “historia” detrás de las métricas

1) Confirma que arrancaste `board-game-buddy` con:
   - `OTLP_TRACING_ENABLED=true`
   - `OTLP_TRACING_URL=http://localhost:4317`
2) Haz 1–2 requests a `POST /ask`.
3) Abre `http://localhost:16686`:
   - **Service**: `Board Game Buddy`
   - **Operation**: busca `POST /ask` (o similar)

Qué explicar (lo que el autor resaltó):

- En el trace verás spans que justifican “actividad extra” en el vector store:
  - Una query relacionada a **memoria** (por `X_AI_CONVERSATION_ID`)
  - Otra query relacionada al **RAG** (filtro por `gameTitle` / metadatos)
  - Adds para guardar entradas en memoria

---

## Troubleshooting rápido

- **No veo métricas `gen_ai` / `spring_ai`**:
  - Asegúrate de haber ejecutado al menos 1 request a `/ask`.
  - Revisa `/actuator/metrics` y filtra por `ai`, `gen`, `spring.ai`, `vector`.

- **Prometheus no muestra series**:
  - Verifica que `/actuator/prometheus` responda.
  - En Linux, revisa `host.docker.internal` (ver nota del paso 1).

- **Jaeger no muestra trazas**:
  - Asegúrate de `OTLP_TRACING_ENABLED=true`.
  - Confirma que el contenedor Jaeger expone `4317` y está arriba (`docker compose ps`).
  - Haz un request a `/ask` después de levantar todo.

