# 🦙 Fase 1 Ollama Start - Modelos Locales de IA

## 🎯 Objetivo
Aprender a usar **Ollama** para ejecutar modelos de IA localmente, sin costo y sin API keys.

## ¿Qué es Ollama?

Ollama es una herramienta que permite:
- ✅ Ejecutar modelos de IA en tu máquina (Mistral, Llama, CodeLlama, etc.)
- ✅ API compatible con OpenAI (el código es casi idéntico)
- ✅ Totalmente gratis e ilimitado
- ✅ Sin necesidad de internet una vez descargado
- ✅ Privacidad total (datos no salen de tu máquina)

## 📋 Instalación de Ollama

### macOS
```bash
brew install ollama
```

### Linux
```bash
curl -fsSL https://ollama.com/install.sh | sh
```

### Windows
Descargar desde: https://ollama.com/download

## 🚀 Usar Ollama

### 1. Iniciar el servidor (si no se inició automáticamente)
```bash
ollama serve
```

### 2. Descargar y ejecutar un modelo
```bash
# Mistral (4.1 GB) - Recomendado para empezar
ollama run mistral

# Otros modelos populares:
ollama run llama3.2      # 2 GB - Muy rápido
ollama run phi3          # 2.3 GB - Eficiente
ollama run codellama     # 3.8 GB - Para código
```

### 3. Verificar que funciona
```bash
curl http://localhost:11434/v1/models
```

## 📚 Modelos Recomendados

| Modelo | Tamaño | RAM Mínima | Mejor Para |
|--------|---------|------------|------------|
| **mistral** | 4.1 GB | 8 GB | Propósito general, español |
| **llama3.2** | 2 GB | 4 GB | Respuestas rápidas |
| **phi3** | 2.3 GB | 4 GB | Laptops modestas |
| **codellama** | 3.8 GB | 8 GB | Generación de código |
| **gemma** | 2.9 GB | 8 GB | Por Google, muy bueno |

Ver todos: https://ollama.com/library

## 🔧 Pasos en Clase

### Paso 1: Verificar que Ollama está corriendo
El código verificará automáticamente si Ollama está disponible en `http://localhost:11434`

### Paso 2: Entender la API de Ollama
Ollama usa la misma API que OpenAI:
- Endpoint: `http://localhost:11434/v1/chat/completions`
- Request: Idéntico a OpenAI
- Response: Idéntico a OpenAI
- **SIN API KEY** ✨

### Paso 3: Implementar el código
Construiremos juntos:
1. Método para verificar si Ollama está disponible
2. Método para listar modelos instalados
3. Método para enviar chat (reutilizando código de fase1-start)
4. Manejo de errores específicos de Ollama

### Paso 4: Ejecutar
```bash
# Sin argumentos (usa modelo por defecto)
mvn clean compile exec:java

# Con prompt personalizado
mvn exec:java -Dexec.args="Explica qué es Ollama"

# Con modelo específico
mvn exec:java -Dexec.args="--model llama3.2 Hola mundo"
```

## 🆚 Ollama vs OpenAI/Anthropic

### Ventajas de Ollama
- ✅ Gratis e ilimitado
- ✅ Privacidad total
- ✅ No necesita internet (después de descargar)
- ✅ Sin cuotas ni rate limits
- ✅ Bueno para desarrollo y testing

### Ventajas de APIs en la nube
- ✅ Modelos más potentes (GPT-4, Claude)
- ✅ Respuestas más rápidas (si tienes CPU lenta)
- ✅ No consume recursos de tu máquina
- ✅ Acceso a modelos especializados

### ¿Cuándo usar cada uno?

**Usa Ollama cuando:**
- Estés aprendiendo y experimentando
- Necesites privacidad (datos sensibles)
- Quieras evitar costos
- Tengas buena máquina (8GB+ RAM)

**Usa APIs en la nube cuando:**
- Necesites la mejor calidad de respuestas
- Estés en producción
- Tu máquina sea modesta
- Necesites modelos especializados

## 🔍 Diferencias en el Código

### OpenAI
```java
String url = "https://api.openai.com/v1/chat/completions";
request.header("Authorization", "Bearer " + apiKey); // ← API KEY
```

### Ollama
```java
String url = "http://localhost:11434/v1/chat/completions";
// ¡No se necesita Authorization header! 🎉
```

## ⚙️ Configuración Opcional

Crear `.env` en la raíz (opcional):
```bash
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=mistral
```

Si no existe, usará valores por defecto.

## 🆘 Troubleshooting

### "Connection refused"
```bash
# Iniciar Ollama
ollama serve
```

### "Model not found"
```bash
# Descargar el modelo primero
ollama pull mistral
```

### Ollama consume mucha RAM
```bash
# Usar un modelo más pequeño
ollama run phi3
```

### Ver logs de Ollama
```bash
# En terminal aparte
ollama logs
```

## 🎓 Para Estudiantes

**Tarea post-clase:**
1. Instalar Ollama
2. Descargar al menos 2 modelos diferentes
3. Completar el código con los TODOs
4. Comparar respuestas de diferentes modelos
5. Medir tiempos de respuesta

**Reto adicional:**
Modificar el código para:
1. Permitir seleccionar modelo por línea de comando
2. Agregar temperatura configurable
3. Mostrar tokens usados en la respuesta
4. Guardar conversación en archivo

## 📖 Recursos Adicionales

- Documentación Ollama: https://github.com/ollama/ollama
- Lista de modelos: https://ollama.com/library
- API reference: https://github.com/ollama/ollama/blob/main/docs/api.md
- Comparativa de modelos: https://ollama.com/blog

---

**Próxima clase:** Integraremos Ollama en aplicaciones Quarkus y Spring Boot.
