# 🚀 Fase 1 Spring Boot Start - API REST Multi-Provider

## 🎯 Objetivo
Construir paso a paso una API REST con Spring Boot que se conecte a OpenAI y Anthropic.

## 📋 Estructura del Proyecto

```
src/main/java/com/joedayz/ia/fase1/springboot/start/
├── config/
│   └── IAConfig.java          # Configuración de proveedores
├── model/
│   ├── Message.java           # Modelo para mensajes
│   ├── ChatRequest.java       # Request a la API de IA
│   └── ChatResponse.java      # Response de la API de IA
├── service/
│   └── IAService.java         # Servicio que llama a las APIs
└── controller/
    └── ChatController.java    # Endpoints REST
```

## 🛠️ Pasos a Seguir en Clase

### Paso 1: Configurar application.properties
```properties
# Configuración de OpenAI
ia.openai.key=${OPENAI_API_KEY:}
ia.openai.base=https://api.openai.com/v1
ia.openai.model=gpt-3.5-turbo

# Configuración de Anthropic
ia.anthropic.key=${ANTHROPIC_API_KEY:}
ia.anthropic.base=https://api.anthropic.com/v1
ia.anthropic.model=claude-3-haiku-20240307
```

### Paso 2: Crear los modelos (Records)
- `Message`: record para mensajes (role, content)
- `ChatRequest`: record para el request (model, messages, max_tokens)
- `ChatResponse`: record para el response

### Paso 3: Implementar IAConfig
- Leer configuración desde application.properties con @ConfigurationProperties
- Propiedades para OpenAI y Anthropic

### Paso 4: Implementar IAService
- Método `chat(String message, String provider)`
- Soporte para OpenAI y Anthropic
- Usar WebClient de Spring para HTTP

### Paso 5: Crear ChatController
- Endpoint GET para mensajes simples
- Endpoint POST con body JSON
- Health check endpoint

## 🚀 Ejecutar

```bash
mvn spring-boot:run
```

## 📡 Probar

```bash
# Health check
curl http://localhost:8080/api/chat/health

# GET simple
curl "http://localhost:8080/api/chat?message=Hola&provider=openai"

# POST con JSON
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Hola", "provider": "openai"}'
```

## 📚 Conceptos que Aprenderás

1. **Spring Boot**: Framework de aplicaciones Java empresariales
2. **REST Controllers**: Crear endpoints HTTP con @RestController
3. **Dependency Injection**: Inyección de dependencias con @Autowired/@Service
4. **Configuration Properties**: Configuración externalizada con @ConfigurationProperties
5. **WebClient**: Cliente HTTP reactivo de Spring
6. **Records**: Clases de datos inmutables (Java 14+)
7. **Multi-Provider**: Abstracción para múltiples proveedores de IA

## 🎓 Tarea

Implementar un tercer proveedor (ej: Google Gemini) siguiendo el patrón establecido.
