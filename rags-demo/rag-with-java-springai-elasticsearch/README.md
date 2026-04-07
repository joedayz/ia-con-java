# RAG demo with Spring AI, OpenAI and Elasticsearch

This repository contains the source code for the
[RAG made easy with Spring AI + Elasticsearch](https://www.elastic.co/search-labs/blog/java-rag-spring-ai-es)
blog post.

## Requisitos

- Java 21+
- Docker Desktop (o Podman)
- `just`
- Una API key de OpenAI (`OPENAI_API_KEY`)

## Levantar Elasticsearch + Kibana local con start-local

Este proyecto ya esta preparado para leer estas variables:

- `ES_SERVER_URL`
- `ES_USERNAME`
- `ES_PASSWORD`

Para simplificar el flujo local, se agregan recetas en `Justfile` que usan
[`elastic/start-local`](https://github.com/elastic/start-local) y mapean sus variables (`ES_LOCAL_*`) a las que consume Spring Boot.

`just setup-elastic` detecta automaticamente si usar Docker o Podman.
Si quieres forzar uno, define `ES_CONTAINER_RUNTIME` con `docker` o `podman`.

```bash
just setup-elastic
eval "$(just env-export)"
```

```bash
ES_CONTAINER_RUNTIME=podman just setup-elastic
```

Al terminar deberias tener:

- Elasticsearch en `http://localhost:9200`
- Kibana en `http://localhost:5601`

## Ejecutar la demo

```bash
just run-app
```

`just run-app` intenta cargar `OPENAI_API_KEY` automaticamente desde `$HOME/.api-keys`
si la variable no esta exportada en tu shell.

## Probar endpoints

Ingestar un PDF (ruta absoluta local):

```bash
just ingest-pdf "/ruta/absoluta/manual.pdf"
```

Tambien acepta rutas relativas al directorio del proyecto (por ejemplo `docs/manual.pdf`)
o rutas de classpath con prefijo `classpath:`.

Consultar el RAG:

```bash
just query "What is the command range in Runewars?"
```

## Verificacion rapida de Elasticsearch

```bash
just health-es
```

## Parar todo al final

Si la app esta corriendo en foreground con `just run-app`, detenla con `Ctrl+C`.

Luego detiene Elasticsearch y Kibana:

```bash
just stop-elastic
```

Para volver a levantarlos sin reinstalar:

```bash
just start-elastic
```

## Fallback sin just

Si prefieres no usar `just`, los scripts siguen disponibles:

```bash
./scripts/setup-elastic-local.sh
source ./scripts/use-elastic-local-env.sh
```
