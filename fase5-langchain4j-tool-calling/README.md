# Fase 4 - LangChain4j Tool Calling

**Tool Calling con @Tool de LangChain4j y Ollama**

## Conceptos

En LangChain4j, las herramientas se definen con la anotación **`@Tool`** en métodos de componentes Spring:

```java
@Component
public class CalculadoraTools {
    @Tool("Suma dos números y retorna el resultado")
    public double sumar(double a, double b) {
        return a + b;
    }
}
```

Y se conectan al LLM mediante **AI Services**:

```java
AiServices.builder(Assistant.class)
    .chatLanguageModel(chatLanguageModel)
    .tools(calculadoraTools, fechaTools, paisApiTools)
    .build();
```

## Lab 14: Calculadora y fechaActual() con @Tool

### Herramientas de Calculadora
- `sumar(a, b)` - Suma dos números
- `restar(a, b)` - Resta dos números
- `multiplicar(a, b)` - Multiplica dos números
- `dividir(a, b)` - Divide dos números
- `raizCuadrada(n)` - Raíz cuadrada
- `potencia(base, exp)` - Potencia

### Herramientas de Fecha
- `fechaActual()` - Fecha actual en español
- `fechaHoraActual()` - Fecha y hora actual en español

## Reto: API REST Real (consultarPais)

La herramienta `consultarPais` usa `@Tool` + `@P` para consultar **restcountries.com** (API gratuita):

```java
@Tool("Consulta información real de un país")
public String consultarPais(@P("Nombre del país") String pais) {
    // Llama a restcountries.com y retorna datos reales
}
```

## Herramientas Disponibles

| Herramienta | Clase | Descripción |
|------------|-------|-------------|
| `sumar`, `restar`, `multiplicar`, `dividir`, `raizCuadrada`, `potencia` | CalculadoraTools | Operaciones matemáticas (Lab 14) |
| `fechaActual`, `fechaHoraActual` | FechaTools | Fecha/hora en español (Lab 14) |
| `consultarPais` | PaisApiTools | API real restcountries.com (Reto) |

## Requisitos

- **Java 21**
- **Ollama** corriendo en `http://localhost:11434`
- **Modelo**: `llama3.2` (`ollama pull llama3.2`)

## Ejecutar

```bash
# Desde el directorio del módulo
cd fase4-langchain4j-tool-calling
../mvnw spring-boot:run

# O desde la raíz del proyecto
./mvnw -pl fase4-langchain4j-tool-calling spring-boot:run
```

**Puerto**: 8082

## Probar

### Con curl

```bash
# Lab 14: Calculadora
curl -s -X POST http://localhost:8082/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuánto es 125 multiplicado por 37?"}' | jq .

# Lab 14: Raíz cuadrada
curl -s -X POST http://localhost:8082/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Cuál es la raíz cuadrada de 144?"}' | jq .

# Lab 14: Fecha actual
curl -s -X POST http://localhost:8082/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué fecha es hoy?"}' | jq .

# Lab 14: Operaciones encadenadas
curl -s -X POST http://localhost:8082/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Suma 100 + 250, luego multiplícalo por 3"}' | jq .

# Reto: Consultar país (API real)
curl -s -X POST http://localhost:8082/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Cuéntame sobre Colombia: capital, población e idiomas"}' | jq .

# Sin herramientas: pregunta general
curl -s -X POST http://localhost:8082/api/tool-calling/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿Qué es la inteligencia artificial?"}' | jq .
```

### Con script de pruebas

```bash
chmod +x test-api.sh
./test-api.sh
```

### Con Swagger UI

Abrir en el navegador: http://localhost:8082/swagger-ui.html

## Estructura del Proyecto

```
fase4-langchain4j-tool-calling/
├── pom.xml
├── README.md
├── test-api.sh
├── test-api.ps1
└── src/main/
    ├── java/com/joedayz/ia/langchain4j/tools/
    │   ├── ToolCallingApplication.java          # Main class
    │   ├── config/
    │   │   ├── LangChain4jConfig.java           # AiServices + tools config
    │   │   └── OpenApiConfig.java               # Swagger config
    │   ├── controller/
    │   │   └── ToolCallingController.java       # REST endpoint
    │   ├── dto/
    │   │   ├── ChatRequest.java                 # Request DTO
    │   │   └── ChatResponse.java                # Response DTO
    │   ├── service/
    │   │   └── Assistant.java                   # AI Service interface
    │   └── tool/
    │       ├── CalculadoraTools.java             # @Tool: operaciones matemáticas
    │       ├── FechaTools.java                   # @Tool: fecha y hora
    │       └── PaisApiTools.java                 # @Tool: API real restcountries.com
    └── resources/
        └── application.yml
```

## Comparación: Spring AI vs LangChain4j

| Aspecto | Spring AI (fase4-spring-ai-tool-calling) | LangChain4j (este módulo) |
|---------|------------------------------------------|---------------------------|
| **Registro de tools** | `@Bean` + `Function<I,O>` + `@Description` | `@Tool` en métodos de `@Component` |
| **Schema de input** | `@JsonPropertyDescription` en records | `@P` en parámetros del método |
| **Invocación** | `ChatClient.toolNames("nombre")` | `AiServices.tools(objeto)` |
| **Configuración** | `spring.ai.ollama.*` | `langchain4j.ollama.*` |
| **Puerto** | 8081 | 8082 |

## Variables de Entorno (opcionales)

| Variable | Default | Descripción |
|----------|---------|-------------|
| `OLLAMA_BASE_URL` | `http://localhost:11434` | URL de Ollama |
| `OLLAMA_CHAT_MODEL` | `llama3.2` | Modelo de chat |
