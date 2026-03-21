# Fase 1 - Spring Boot

REST API simple con Spring Boot que integra OpenAI para chat completions.

## Requisitos

- Java 21
- Maven 3.8+
- API Key de OpenAI

## Configuración

Configura tu API Key de OpenAI:

```bash
export OPENAI_API_KEY="tu-api-key-aqui"
```

O agrégala en `~/.api-keys`:

```bash
export OPENAI_API_KEY="sk-..."
```

## Ejecutar

```bash
# Cargar variables de entorno
source ~/.api-keys

# Ejecutar en desarrollo
mvn spring-boot:run

# O compilar y ejecutar el JAR
mvn clean package
java -jar target/fase1-springboot-1.0.0.jar
```

La aplicación estará disponible en: http://localhost:8080

## Endpoints

### GET /api/chat

Envía un mensaje simple:

```bash
curl "http://localhost:8080/api/chat?message=Hola"
```

### POST /api/chat

Envía un mensaje con system prompt opcional:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Explica qué es un LLM",
    "system_prompt": "Eres un profesor de IA"
  }'
```

### GET /api/chat/health

Health check:

```bash
curl http://localhost:8080/api/chat/health
```

## Configuración Avanzada

Puedes personalizar la configuración en `src/main/resources/application.properties`:

```properties
openai.api.base=https://api.openai.com/v1
openai.api.model=gpt-3.5-turbo
openai.api.max-tokens=500
openai.api.timeout=30s
```

## Pruebas

Ver `test-api.sh` para ejemplos de pruebas con curl.
