# RAG Demo con Spring AI + MongoDB en Docker

Este proyecto ahora esta preparado para ejecutarse de forma simple en local usando:

- `source /Users/josediaz/.api-keys` para cargar tus secretos.
- MongoDB en contenedor (Docker o Podman).
- `just` para orquestar toda la demo.

## Requisitos

- Java 21+
- Docker Desktop (recomendado para alumnos) o Podman
- `just` (macOS: `brew install just`)
- Tu archivo `/Users/josediaz/.api-keys` con al menos:

```bash
export OPENAI_API_KEY="tu_api_key"
```

## Configuracion usada por Spring

`src/main/resources/application.properties` usa `OPENAI_API_KEY` para el LLM y deja Mongo fijo para la demo local:

```properties
spring.ai.openai.api-key=${OPENAI_API_KEY:}
spring.data.mongodb.uri=mongodb://localhost:27017/rag?directConnection=true
spring.data.mongodb.database=rag
```

## Flujo rapido con just

Desde la raiz del proyecto:

```bash
just
just run
```

Si aparece `command not found: docker`, instala y abre Docker Desktop antes de ejecutar `just run`.

`justfile` detecta automaticamente el runtime: usa Docker si existe, y si no, usa Podman.

Si usas Podman en macOS y falla `just run`, inicializa la VM de Podman:

```bash
podman machine init
podman machine start
```

Comandos utiles:

```bash
just mongo-up
just mongo-reset
just mongo-logs
just load-docs
just ask "How to analyze time-series data with Python and MongoDB? Explain the steps"
just mongo-down
```

`just load-docs` ahora carga archivos desde `docs/` (incluyendo `.pdf`) para que luego puedas preguntar sobre ese contenido en `/faq`.

## Flujo manual (sin just)

```bash
source /Users/josediaz/.api-keys
docker compose up -d mongodb
./mvnw spring-boot:run
```

## Endpoints de la demo

- Cargar documentos: `http://localhost:8080/api/docs/load`
- Preguntas RAG: `http://localhost:8080/faq?message=Tu pregunta`

## Archivos agregados para esta adaptacion

- `docker-compose.yml`: MongoDB local en Docker.
- `justfile`: comandos simplificados para correr la demo.
- `scripts/dev-env.zsh`: helper para cargar entorno desde `/Users/josediaz/.api-keys`.
- `.env.example`: referencia de variables esperadas.

## Nota

Este proyecto usa `spring-ai-starter-vector-store-mongodb-atlas`. Si en tu entorno local notas limitaciones de busqueda vectorial, puedes editar `src/main/resources/application.properties` y apuntar `spring.data.mongodb.uri` a un cluster de MongoDB Atlas.

Para correr local en contenedor, este repo usa `mongodb/mongodb-atlas-local` en `docker-compose.yml`. Si usas `mongo:7.x` veras errores como `no such command: 'createSearchIndexes'`.

Si ya habias levantado el proyecto con otra imagen de Mongo, ejecuta `just mongo-reset` para recrear volumenes y luego `just run`.

