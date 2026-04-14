# 🚀 Fase 1 Spring Boot Ollama - API REST

## 🎯 Objetivo
Construir una API REST con Spring Boot que se conecte a Ollama para utilizar modelos de IA locales sin necesidad de API keys.

## ✨ Características Principales

- **🎉 SIN API KEY REQUERIDA**: Ollama se ejecuta localmente
- **🤖 Modelos Locales**: Mistral, Llama 2, Neural Chat, etc.
- **⚡ API Compatible**: Compatible con OpenAI Chat Completions API
- **📦 Spring Boot**: Arquitectura moderna y escalable
- **🔧 Fácil Configuración**: Properties externalizadas

## 📋 Estructura del Proyecto

```
src/main/java/com/joedayz/ia/fase1/springboot/start/
├── config/
│   └── OllamaConfig.java      # Configuración de Ollama
├── model/
│   ├── Message.java           # Modelo para mensajes
│   ├── ChatRequest.java       # Request a la API de Ollama
│   └── ChatResponse.java      # Response de la API de Ollama
├── service/
│   └── OllamaService.java     # Servicio que llama a Ollama
└── controller/
    └── ChatController.java    # Endpoints REST
```

## 🛠️ Requisitos Previos

### 1. Ollama Instalado
Descarga e instala Ollama desde [ollama.ai](https://ollama.ai)

### 2. Ollama Ejecutándose
```bash
ollama serve
```

### 3. Modelo Disponible
```bash
ollama pull mistral
```

## 📝 Configuración (application.properties)

```properties
# Base URL de Ollama
ia.ollama.base=http://localhost:11434

# Modelo a usar
ia.ollama.model=mistral

# Tokens máximos
ia.ollama.max-tokens=500

# Timeout (2m para modelos locales)
ia.ollama.timeout=2m
```

## 🚀 Ejecutar

```bash
mvn spring-boot:run
```

## 📡 Probar

```bash
# Health check
curl http://localhost:8080/api/chat/health

# GET simple
curl "http://localhost:8080/api/chat?message=Hola"

# POST con system prompt
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿2+2?", "system_prompt": "Responde en una línea"}'
```

## 📚 Conceptos que Aprenderás

1. **Spring Boot**: Framework Java moderno
2. **REST Controllers**: Crear endpoints HTTP
3. **Dependency Injection**: Inyección de dependencias
4. **Configuration Properties**: Configuración externalizada
5. **WebClient**: Cliente HTTP reactivo
6. **Records**: Clases de datos inmutables
7. **Ollama API**: Integración con modelos locales

## 🚀 Próximos Pasos

1. Agregar persistencia en base de datos
2. Implementar streaming de respuestas
3. Soportar múltiples modelos
4. Rate limiting y autenticación
5. Containerizar con Docker
