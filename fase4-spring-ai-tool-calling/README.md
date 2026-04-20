# Fase 4 - Spring AI Tool Calling

**Tool Calling (Function Calling) con Spring AI y Ollama**

## Conceptos

**Tool Calling** permite que el LLM decida cuándo invocar funciones/herramientas externas:

```
Usuario → Pregunta → LLM analiza tools → LLM decide llamar tool → App ejecuta → LLM responde
```

1. El usuario envía una pregunta
2. El LLM analiza las herramientas disponibles y sus descripciones
3. El LLM decide llamar a una herramienta (o responder directamente)
4. Spring AI ejecuta la función automáticamente
5. El LLM incorpora el resultado y genera la respuesta final

## Lab 13: Tool obtenerClima() con @Bean

En Spring AI, las herramientas se registran como **beans** de tipo `Function<Request, Response>` con `@Description`:

```java
@Bean("obtenerClima")
@Description("Obtiene el clima actual para una ciudad dada")
public Function<ClimaRequest, ClimaResponse> obtenerClima() {
    return request -> {
        // lógica de la herramienta
        return new ClimaResponse(request.ciudad(), "22°C", "Soleado", "45%");
    };
}
```

Y se habilitan en el ChatClient:

```java
chatClient.prompt()
    .toolNames("obtenerClima", "consultarPais")
    .user(message)
    .call()
    .content();
```

## Reto: API REST Real (consultarPais)

La herramienta `consultarPais` consulta **restcountries.com** (API gratuita, sin API key) para obtener información real de cualquier país.

Flujo implementado en `ToolConfig.consultarPais`:

1. Limpia el texto del país (`sanitizeCountry`)
2. Genera variantes (normalización sin tildes y alias comunes en español)
3. Intenta resolver por `v3.1/name/{pais}`
4. Si no encuentra, reintenta por `v3.1/translation/{pais}`
5. Si no hay coincidencias, retorna fallback controlado (`No encontrado`)

Alias soportados (ejemplos):

- `alemania` -> `germany`
- `japon` -> `japan`
- `espana` / `españa` -> `spain`
- `estados unidos` -> `united states`

## Herramientas Disponibles

| Herramienta | Tipo | Descripción |
|------------|------|-------------|
| `obtenerClima` | Simulada | Retorna clima de ciudades predefinidas (Lab 13) |
| `consultarPais` | API Real | Consulta restcountries.com para info de países (Reto) |

## Requisitos

- **Java 21**
- **Ollama** corriendo en `http://localhost:11434`
- **Modelo**: `llama3.2` (`ollama pull llama3.2`)

## Ejecutar

### Linux / macOS

```bash
# Desde el directorio del módulo
cd fase4-spring-ai-tool-calling
../mvnw spring-boot:run

# O desde la raíz del proyecto
./mvnw -pl fase4-spring-ai-tool-calling spring-boot:run
```

### Windows (PowerShell)

```powershell
# Desde el directorio del módulo
cd fase4-spring-ai-tool-calling
..\mvnw.cmd spring-boot:run

# O desde la raíz del proyecto
.\mvnw.cmd -pl fase4-spring-ai-tool-calling spring-boot:run
```

**Puerto**: 8081

## Probar

### Con curl

#### Linux / macOS

```bash
# Lab 13: Consultar clima (tool obtenerClima)
curl -s -X POST http://localhost:8081/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cómo está el clima en Lima?"}' | jq .

# Lab 13: Otra ciudad
curl -s -X POST http://localhost:8081/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué temperatura hace en Madrid?"}' | jq .

# Reto: Consultar país (tool consultarPais - API real)
curl -s -X POST http://localhost:8081/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Cuéntame sobre Japón: capital, población e idiomas"}' | jq .

# Alias en español: Alemania (mapea a germany)
curl -s -X POST http://localhost:8081/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Dame datos de Alemania: capital e idiomas"}' | jq .

# Caso no encontrado (fallback controlado)
curl -s -X POST http://localhost:8081/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Cuéntame sobre Wakanda: capital y población"}' | jq .

# Sin herramientas: pregunta general
curl -s -X POST http://localhost:8081/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué es la inteligencia artificial?"}' | jq .
```

#### Windows (PowerShell)

```powershell
# Lab 13: Consultar clima (tool obtenerClima)
$body = @{ message = "¿Cómo está el clima en Lima?" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/tool-calling/chat `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 6

# Lab 13: Otra ciudad
$body = @{ message = "¿Qué temperatura hace en Madrid?" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/tool-calling/chat `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 6

# Reto: Consultar país (tool consultarPais - API real)
$body = @{ message = "Cuéntame sobre Japón: capital, población e idiomas" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/tool-calling/chat `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 6

# Alias en español: Alemania (mapea a germany)
$body = @{ message = "Dame datos de Alemania: capital e idiomas" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/tool-calling/chat `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 6

# Caso no encontrado (fallback controlado)
$body = @{ message = "Cuéntame sobre Wakanda: capital y población" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/tool-calling/chat `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 6

# Sin herramientas: pregunta general
$body = @{ message = "¿Qué es la inteligencia artificial?" } | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8081/api/tool-calling/chat `
  -ContentType "application/json" -Body $body | ConvertTo-Json -Depth 6
```

### Con script de pruebas

#### Linux / macOS

```bash
chmod +x test-api.sh
./test-api.sh
```

#### Windows (PowerShell)

```powershell
.\test-api.ps1
# Si tu política de ejecución bloquea scripts:
# powershell -ExecutionPolicy Bypass -File .\test-api.ps1
```

### Con Swagger UI

Abrir en el navegador: http://localhost:8081/swagger-ui.html

## Estructura del Proyecto

```
fase4-spring-ai-tool-calling/
├── pom.xml
├── README.md
├── test-api.sh
├── test-api.ps1
└── src/main/
    ├── java/com/joedayz/ia/springai/tools/
    │   ├── ToolCallingApplication.java          # Main class
    │   ├── config/
    │   │   ├── ToolConfig.java                  # @Bean Function (tools)
    │   │   └── OpenApiConfig.java               # Swagger config
    │   ├── controller/
    │   │   └── ToolCallingController.java       # REST endpoint
    │   ├── dto/
    │   │   ├── ChatRequest.java                 # Request DTO
    │   │   └── ChatResponse.java                # Response DTO
    │   └── function/
    │       ├── ClimaRequest.java                # Input: obtenerClima
    │       ├── ClimaResponse.java               # Output: obtenerClima
    │       ├── PaisRequest.java                 # Input: consultarPais
    │       └── PaisResponse.java                # Output: consultarPais
    └── resources/
        └── application.yml
```

## Variables de Entorno (opcionales)

| Variable | Default | Descripción |
|----------|---------|-------------|
| `OLLAMA_BASE_URL` | `http://localhost:11434` | URL de Ollama |
| `OLLAMA_CHAT_MODEL` | `llama3.2` | Modelo de chat |
