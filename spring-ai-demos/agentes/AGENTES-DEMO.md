# Agentes (Spring AI) – Guía de pruebas (Java 21)

Estos demos implementan patrones del capítulo “Employing agents” (prompt chaining, routing, parallelization y routing con memoria).

## Requisitos

- Java 21
- API key del proveedor (ej. OpenAI)
- Un folder local con archivos de reglas (PDF/TXT/etc)

Variables típicas:

```bash
export OPENAI_API_KEY="..."
```

## Propiedad obligatoria: `boardgame.rules.path`

Estos proyectos necesitan leer reglas desde disco. Configura:

- `boardgame.rules.path=file:/absolute/path/to/BoardGameRules`

Puedes ponerlo en `src/main/resources/application.properties` o pasarlo al ejecutar:

```bash
./gradlew bootRun -Dspring-boot.run.jvmArguments="-Dboardgame.rules.path=file:/absolute/path/to/BoardGameRules"
```

## 1) Prompt chaining (`chaining`)

### Levantar

```bash
cd spring-ai-demos/agentes/chaining
./gradlew bootRun -Dspring-boot.run.jvmArguments="-Dboardgame.rules.path=file:/absolute/path/to/BoardGameRules"
```

### Probar (`POST /ask`)

```bash
curl -s http://localhost:8080/ask \
  -H "Content-Type: application/json" \
  -d '{ "question": "What are the mechanics in the game Carcassonne?" }'
```

## 2) Routing (`routing`)

Este demo enruta tu pregunta a un “chain” distinto (ej. mechanics vs playerCount) usando un router con LLM.

### Levantar

```bash
cd spring-ai-demos/agentes/routing
./gradlew bootRun -Dspring-boot.run.jvmArguments="-Dboardgame.rules.path=file:/absolute/path/to/BoardGameRules"
```

### Probar (`POST /ask`)

Pregunta por mecánicas:

```bash
curl -s http://localhost:8080/ask \
  -H "Content-Type: application/json" \
  -d '{ "question": "What are the mechanics in Azul?" }'
```

Pregunta por número de jugadores:

```bash
curl -s http://localhost:8080/ask \
  -H "Content-Type: application/json" \
  -d '{ "question": "How many can play Azul?" }'
```

## 3) Routing con memoria (`routing-with-memory`)

Igual que `routing`, pero el fetcher usa memoria de chat para mantener contexto.

### Levantar

```bash
cd spring-ai-demos/agentes/routing-with-memory
./gradlew bootRun -Dspring-boot.run.jvmArguments="-Dboardgame.rules.path=file:/absolute/path/to/BoardGameRules"
```

### Probar

```bash
curl -s http://localhost:8080/ask \
  -H "Content-Type: application/json" \
  -d '{ "question": "Tell me about Burger Battle." }'
```

Luego, sin repetir el título, haz una segunda pregunta (la memoria debería ayudar):

```bash
curl -s http://localhost:8080/ask \
  -H "Content-Type: application/json" \
  -d '{ "question": "How many players can play it?" }'
```

## 4) Parallelization (`parallelization`)

Divide el trabajo en tareas que pueden ejecutarse en paralelo.

### Levantar

```bash
cd spring-ai-demos/agentes/parallelization
./gradlew bootRun -Dspring-boot.run.jvmArguments="-Dboardgame.rules.path=file:/absolute/path/to/BoardGameRules"
```

### Probar (`POST /ask`)

```bash
curl -s http://localhost:8080/ask \
  -H "Content-Type: application/json" \
  -d '{ "question": "Summarize and list mechanics for Sagrada." }'
```

## Notas de compatibilidad

- Si algo del libro no coincide, esta guía prioriza **lo que realmente esperan tus controllers** (JSON) y propiedades actuales.
- Cuando me pases la parte 2, ajusto esta guía para que calce 1:1 con esos ejemplos.

