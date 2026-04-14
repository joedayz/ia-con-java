# 🚀 Fase 1 Quarkus Start - API REST Multi-Provider

## 🎯 Objetivo
Construir paso a paso una API REST con Quarkus que se conecte a OpenAI y Anthropic.

## 📋 Estructura del Proyecto

```
src/main/java/com/joedayz/ia/fase1/quarkus/start/
├── config/
│   └── IAConfig.java          # Configuración de proveedores
├── model/
│   ├── Message.java           # Modelo para mensajes
│   ├── ChatRequest.java       # Request a la API de IA
│   └── ChatResponse.java      # Response de la API de IA
├── service/
│   └── IAService.java         # Servicio que llama a las APIs
└── resource/
    └── ChatResource.java      # Endpoints REST
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
- Leer configuración desde application.properties
- Exponer propiedades vía @ConfigMapping

### Paso 4: Implementar IAService
- Método `chat(String message, String provider)`
- Soporte para OpenAI y Anthropic
- Usar JAX-RS Client para HTTP

### Paso 5: Implementar ChatResource
- GET `/api/chat?message=...&provider=openai`
- POST `/api/chat` con body JSON

## 🚀 Ejecutar

```bash
# Modo desarrollo (hot reload)
mvn quarkus:dev

# Probar
curl "http://localhost:8080/api/chat?message=Hola&provider=openai"

# O con POST
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Explica qué es un LLM","provider":"openai"}'
```

En PowerShell:

```powershell
./test-api.ps1
```

## 📚 Conceptos Clave
- **Records**: clases inmutables para DTOs
- **@ConfigMapping**: configuración type-safe
- **@ApplicationScoped**: beans singleton
- **@Inject**: inyección de dependencias
- **JAX-RS**: API REST estándar
