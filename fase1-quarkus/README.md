# Fase 1 - Quarkus: Primera llamada a la API de OpenAI

Esta es la versión con **Quarkus** del proyecto Fase 1. Proporciona una API REST para interactuar con OpenAI.

## 🚀 Características

- **REST API** con endpoints GET y POST
- **Configuración flexible** vía variables de entorno o `application.properties`
- **System prompts** opcionales para personalizar el comportamiento
- **Logging** detallado para debugging
- **Hot reload** en modo desarrollo con Quarkus Dev Mode

## 📋 Requisitos

- Java 17+
- Maven 3.6+
- API Key de OpenAI (configurada en variable de entorno `OPENAI_API_KEY`)

## ⚙️ Configuración

### 1. Configurar API Key

La aplicación intenta leer `OPENAI_API_KEY` automáticamente desde el `.env` en la raíz del repositorio.

También puedes exportar la variable de entorno:

```bash
export OPENAI_API_KEY=sk-tu-clave-aqui
```

O configúrala en el archivo `.env` en la raíz del repositorio:

```bash
OPENAI_API_KEY=sk-tu-clave-aqui
OPENAI_API_BASE=https://api.openai.com/v1  # Opcional
OPENAI_API_MODEL=gpt-3.5-turbo              # Opcional
```

### 2. Configuración adicional (opcional)

Puedes modificar `src/main/resources/application.properties` para ajustar:
- Puerto del servidor (por defecto 8080)
- Modelo de OpenAI (por defecto gpt-3.5-turbo)
- Timeout de las peticiones
- Nivel de logging

## 🏃 Cómo ejecutar

### Referencia rápida de Fase 1 (Java puro)

Desde la raíz del repo también tienes estas variantes:

```bash
mvn -pl fase1 exec:java
mvn -pl fase1 exec:java -Dexec.args="Explica qué es Java"  # FASE 1 (con mensaje)
```

### Modo Desarrollo (con hot reload)

```bash
cd fase1-quarkus
mvn quarkus:dev
```

O desde la raíz del proyecto:

```bash
mvn -pl fase1-quarkus quarkus:dev
```

La aplicación estará disponible en `http://localhost:8080`

### Modo Producción

Compilar y ejecutar:

```bash
cd fase1-quarkus
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

## 🔌 Uso de la API

### 1. Health Check

Verifica que la aplicación esté funcionando:

```bash
curl http://localhost:8080/api/chat/health
```

### 2. Chat simple (GET)

```bash
curl "http://localhost:8080/api/chat?message=Hola,%20¿cómo%20estás?"
```

### 3. Chat con system prompt (POST)

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Explica qué es un LLM en una frase",
    "system_prompt": "Eres un profesor de inteligencia artificial"
  }'
```

### 4. Reto: System prompt de pirata

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "¿Qué es la inteligencia artificial?",
    "system_prompt": "Eres un pirata del Caribe. Responde todo como si fueras un pirata, usando jerga pirata."
  }'
```

## 📝 Respuestas de ejemplo

### Success Response:

```json
{
  "response": "Un LLM (Large Language Model) es un modelo de inteligencia artificial...",
  "model": "gpt-3.5-turbo",
  "timestamp": 1710691200000
}
```

### Error Response:

```json
{
  "error": "OPENAI_API_KEY no está configurada..."
}
```

## 🛠️ Compilación del proyecto completo

Desde la raíz del repositorio:

```bash
mvn clean install
```

## 📚 Comparación con la versión Java puro (fase1)

| Aspecto | Fase1 (Java puro) | Fase1-Quarkus |
|---------|-------------------|---------------|
| **Ejecución** | CLI con `mvn exec:java` | REST API con servidor web |
| **Configuración** | `.env` con librería dotenv-java | Variables de entorno + Quarkus Config |
| **HTTP Client** | `java.net.http.HttpClient` | JAX-RS Client (Jakarta) |
| **Logging** | `System.out.println` | JBoss Logging |
| **Hot Reload** | No | Sí (Quarkus Dev Mode) |
| **Producción** | Script simple | Aplicación web completa |

## 🎯 Labs de la Sesión 1

### Lab 1: Configurar y compilar

```bash
export OPENAI_API_KEY=tu-clave
cd fase1-quarkus
mvn clean package
```

### Lab 2: Ejecutar y probar

```bash
mvn quarkus:dev
# En otra terminal:
curl "http://localhost:8080/api/chat?message=Di%20Hola%20desde%20Quarkus"
```

### Reto: System prompt de pirata

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Explícame qué son los tokens en IA",
    "system_prompt": "Hablas como un pirata. Usa expresiones como arrr, matey, etc."
  }'
```

## 🐛 Troubleshooting

### Error: "OPENAI_API_KEY no está configurada"

Verifica que exista `.env` en la raíz del repo con una clave válida:

```bash
cat ../.env | grep OPENAI_API_KEY
```

O exporta la variable de entorno antes de ejecutar:

```bash
export OPENAI_API_KEY=sk-tu-clave
```

### Error: "Connection refused" o timeout

Verifica que:
- Tu API key sea válida
- Tengas conexión a internet
- No tengas un firewall bloqueando la conexión

### Puerto 8080 en uso

Cambia el puerto en `application.properties`:

```properties
quarkus.http.port=8081
```

## 📖 Recursos

- [Quarkus - Guía de inicio](https://quarkus.io/get-started/)
- [OpenAI API Documentation](https://platform.openai.com/docs/api-reference)
- [Quarkus REST Client](https://quarkus.io/guides/rest-client)

## 🎓 Siguiente paso

Continúa con **Fase 2** donde aprenderás sobre Prompt Engineering y técnicas avanzadas de comunicación con LLMs.
