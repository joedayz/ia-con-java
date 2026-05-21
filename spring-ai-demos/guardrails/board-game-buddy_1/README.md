# board-game-buddy_1

Demo de guardrails / RAG con **Qdrant** (vector store + memoria de chat).

## Requisitos

- Java 21
- `OPENAI_API_KEY`
- **Docker Desktop** en marcha (Qdrant se levanta vía `compose.yaml` al hacer `bootRun`)

## Ejecutar

```bash
export OPENAI_API_KEY=sk-...
cd spring-ai-demos/guardrails/board-game-buddy_1
./gradlew bootRun
```

Spring Boot arranca **Qdrant** (puerto gRPC `6334`) y **Jaeger** (`16686`) desde `compose.yaml`.

### Sin Docker Compose automático

```bash
docker compose up -d qdrant
./gradlew bootRun -Dspring.docker.compose.enabled=false
```

## Error `UNAVAILABLE: io exception` (Qdrant)

Significa que **Qdrant no está escuchando en `localhost:6334`**.

1. Comprueba Docker: `docker ps` (debe aparecer `qdrant`).
2. Levanta manualmente: `docker compose up -d qdrant`
3. Verifica el puerto: `nc -zv localhost 6334`

Si otro demo ya usa Qdrant en `6334`, reutilízalo (misma colección `board-game-buddy`).

## Endpoints

- `GET /burgerBattleArt?burger=Classic` — chat + imagen (requiere reglas en vector store; ver `game-rules-loader`)
