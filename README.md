# IA con Java

Demos del curso **IA con Java**. Cada fase es un **proyecto Maven independiente** dentro del repositorio. Las API keys se configuran en un archivo `.env` en la raíz.

## Estructura (multi-módulo)

```
ia-con-java/
├── pom.xml                 # Parent POM (no tiene código, solo agrupa módulos)
├── .env.example / .env      # Configuración de API keys (en la raíz)
├── common/                  # Proyecto compartido: EnvConfig, ServicioIA
│   └── src/main/java/...
├── fase1/                   # Proyecto Fase 1 - Primera llamada a la API (Java puro)
├── fase1-quarkus/          # Proyecto Fase 1 - Versión con Quarkus (REST API)
├── fase2/                   # Proyecto Fase 2 - Prompt engineering
├── fase3/                   # Proyecto Fase 3 - Servicio de IA (demo)
└── fase4/                   # Proyecto Fase 4 - RAG simple
```

## Requisitos

- Java 17+
- Maven 3.6+

## Configuración (API keys)

1. En la **raíz del repositorio** copia el archivo de ejemplo y edítalo:

   ```bash
   cp .env.example .env
   ```

2. Edita `.env` y asigna tu `OPENAI_API_KEY`:

   ```
   OPENAI_API_KEY=sk-tu-clave-aqui
   ```

Opcional: si usas otro endpoint (Azure OpenAI, proxy, etc.):

```
OPENAI_API_BASE=https://tu-endpoint.com/v1
```

**Importante:** El archivo `.env` no se sube al repositorio. No compartas tu clave.

## Cómo ejecutar cada proyecto (fase)

Ejecuta siempre desde la **raíz del repositorio** para que se encuentre el `.env`:

| Fase | Descripción | Comando |
|------|-------------|---------|
| **Fase 1** | Primera llamada a la API de OpenAI (Java puro) | `mvn -pl fase1 exec:java` |
| **FASE 1 (con mensaje)** | Ejecuta Fase 1 con un prompt inicial personalizado | `mvn -pl fase1 exec:java -Dexec.args="Explica qué es Java"` |
| **Fase 1 (Quarkus)** | REST API con hot reload (lee `.env` en la raíz automáticamente) | `mvn -pl fase1-quarkus quarkus:dev` |
| **Fase 1 (Quarkus con mensaje)** | Envía un mensaje al endpoint REST (equivalente al "con mensaje" de Java puro) | `curl "http://localhost:8080/api/chat?message=Explica%20qu%C3%A9%20es%20Java"` |
| **Fase 2** | Prompt engineering (consola interactiva) | `mvn -pl fase2 exec:java` |
| **Fase 3** | Demo del servicio de IA compartido | `mvn -pl fase3 exec:java` |
| **Fase 4** | RAG simple (consola interactiva) | `mvn -pl fase4 exec:java` |

### Fase 1: Dos versiones disponibles

#### Versión Java puro (`fase1`)
Aplicación de consola que hace una sola llamada a OpenAI:
```bash
mvn -pl fase1 exec:java
# Con mensaje personalizado:
mvn -pl fase1 exec:java -Dexec.args="¿Qué es la IA?"
```

#### Versión Quarkus (`fase1-quarkus`)
REST API con hot reload, útil para integraciones:
```bash
# Iniciar en modo desarrollo:
# Si tienes .env en la raíz, se carga automáticamente.
# Exportar OPENAI_API_KEY es opcional y tiene prioridad.
mvn -pl fase1-quarkus quarkus:dev

# En otra terminal:
curl "http://localhost:8080/api/chat?message=Hola%20desde%20Quarkus"

# Equivalente a "FASE 1 (con mensaje)" de Java puro:
curl "http://localhost:8080/api/chat?message=Explica%20qu%C3%A9%20es%20Java"
```

Ver [fase1-quarkus/README.md](fase1-quarkus/README.md) para más detalles.

### Compilar todo

```bash
mvn compile
```

### Compilar y ejecutar solo una fase

```bash
mvn -pl fase1 compile exec:java
mvn -pl fase2 compile exec:java
# etc.
```

Cada fase tiene su propio `pom.xml` y su clase principal configurada en el plugin `exec-maven-plugin`.

## Contenido por fase

- **common**: Carga de `.env` (`EnvConfig`) y servicio reutilizable `ServicioIA`. Lo usan fase3 y fase4.
- **fase1**: HttpClient, primera petición a `chat/completions`.
- **fase2**: System prompt y consola para preguntas/respuestas.
- **fase3**: Uso de `ServicioIA` desde código (demo del módulo common).
- **fase4**: RAG con un documento de contexto en el prompt (sin vector store).

Si tu curso define más fases, añade un nuevo módulo (carpeta + `pom.xml`) y listado en `<modules>` del parent.
