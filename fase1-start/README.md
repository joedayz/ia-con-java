# Fase 1 Start - Primera Llamada a IA (Multi-Provider)

## 🎯 Objetivo
Construir paso a paso una aplicación Java que se conecte a APIs de IA (OpenAI y Anthropic).

## 📋 Pasos a Seguir en Clase

### Paso 1: Configurar variables de entorno
```bash
# Crear archivo .env en la raíz del proyecto
OPENAI_API_KEY=tu-api-key-aqui
OPENAI_API_BASE=https://api.openai.com/v1

# Opcional: para Anthropic
ANTHROPIC_API_KEY=tu-api-key-aqui
ANTHROPIC_API_BASE=https://api.anthropic.com/v1
```

### Paso 2: Entender la estructura del request HTTP
- Método: POST
- Endpoint: `/chat/completions` (OpenAI) o `/messages` (Anthropic)
- Headers: `Authorization`, `Content-Type`
- Body: JSON con model, messages, max_tokens

### Paso 3: Implementar el código
Construiremos juntos en clase:
1. Método para escape de JSON
2. Método para extraer contenido de la respuesta
3. Método principal para enviar el chat
4. Soporte para ambos proveedores

### Paso 4: Ejecutar
```bash
mvn clean compile
mvn exec:java -Dexec.args="Explica qué es un LLM en una frase"
```

## 🔧 TODOs en el código
Busca los comentarios `// TODO:` en el código para saber qué implementar.
