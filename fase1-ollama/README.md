# 🦙 Fase 1 Ollama - Modelos Locales de IA

## 🎯 Objetivo
Primera llamada a un modelo de IA **local** usando **Ollama** - sin costo, sin API keys, totalmente privado.

## ¿Qué es Ollama?

Ollama es una herramienta que permite ejecutar modelos de IA (Mistral, Llama, CodeLlama, etc.) directamente en tu laptop, sin necesidad de servicios en la nube.

### Ventajas 🎉
- ✅ **Gratis e ilimitado** - Sin costos por token
- ✅ **Privacidad total** - Los datos no salen de tu máquina
- ✅ **Sin internet** - Funciona offline una vez descargado
- ✅ **API compatible con OpenAI** - El código es casi idéntico
- ✅ **Sin API keys** - No necesitas registrarte en ningún lado

### Desventajas ⚠️
- ⏱️ Más lento que APIs en la nube (depende de tu CPU/GPU)
- 💾 Requiere espacio en disco (2-8 GB por modelo)
- 🧠 Calidad ligeramente inferior a GPT-4 (pero muy buena)
- 💻 Requiere RAM suficiente (mínimo 8 GB)

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

## 🚀 Configuración

### 1. Iniciar el servidor (si no se inició automáticamente)
```bash
ollama serve
```

El servidor se ejecutará en `http://localhost:11434`

### 2. Descargar modelos

```bash
# Mistral 7B (4.1 GB) - RECOMENDADO para empezar
ollama pull mistral

# Llama 3.2 (2 GB) - Más rápido, bueno para laptops modestas
ollama pull llama3.2

# Phi-3 (2.3 GB) - Eficiente, bueno para español
ollama pull phi3

# CodeLlama (3.8 GB) - Especializado en código
ollama pull codellama

# Gemma (2.9 GB) - Por Google, muy bueno
ollama pull gemma
```

### 3. Verificar instalación
```bash
# Listar modelos instalados
ollama list

# Probar desde terminal
ollama run mistral "Hola desde la terminal"

# Verificar API
curl http://localhost:11434/api/version

# O usar el script de verificación
./verificar-ollama.sh
```

## 📚 Modelos Recomendados

| Modelo | Tamaño | RAM Mínima | Velocidad | Calidad | Mejor Para |
|--------|---------|------------|-----------|---------|------------|
| **mistral** | 4.1 GB | 8 GB | Media | ⭐⭐⭐⭐ | Propósito general, español |
| **llama3.2** | 2 GB | 4 GB | Rápida | ⭐⭐⭐ | Respuestas rápidas, laptops modestas |
| **phi3** | 2.3 GB | 4 GB | Rápida | ⭐⭐⭐ | Laptops modestas, español |
| **codellama** | 3.8 GB | 8 GB | Media | ⭐⭐⭐⭐ | Generación de código |
| **gemma** | 2.9 GB | 8 GB | Media | ⭐⭐⭐⭐ | Por Google, muy balanceado |
| **llama2** | 3.8 GB | 8 GB | Media | ⭐⭐⭐ | Bueno en español |

Ver catálogo completo: https://ollama.com/library

## 💻 Uso del Código

### Ejecución básica
```bash
# Compilar y ejecutar con prompt por defecto
mvn clean compile exec:java

# Con prompt personalizado
mvn exec:java -Dexec.args="Explica qué es un record en Java"
mvn exec:java -Dexec.args="¿Cuál es la diferencia entre ArrayList y LinkedList?"
```

### Seleccionar modelo específico
```bash
mvn exec:java -Dexec.args="--model llama3.2 Hola mundo"
mvn exec:java -Dexec.args="--model phi3 Explica los streams en Java"
```

### Comparar modelos (función BONUS)
```bash
# Ejecuta el mismo prompt en todos los modelos instalados
mvn exec:java -Dexec.args="--compare ¿Qué es Java?"
```

### Variables de entorno (opcional)
```bash
# Cambiar URL de Ollama (si no es localhost)
export OLLAMA_BASE_URL=http://192.168.1.100:11434

# Definir modelo por defecto
export OLLAMA_MODEL=llama3.2

mvn exec:java
```

## 🔍 Estructura del Código

```java
PrimeraLlamadaOllama.java
│
├── main() - Flujo principal
│   ├── isOllamaRunning() - Verifica si Ollama está disponible
│   ├── listarModelos() - GET /api/tags - Lista modelos instalados
│   ├── determinarModelo() - Lógica para seleccionar modelo
│   ├── extraerPrompt() - Parsea argumentos
│   ├── enviarChat() - POST /v1/chat/completions (compatible OpenAI)
│   └── compararModelos() - BONUS: Compara velocidad de modelos
│
├── escapeJson() - Escapa caracteres especiales
└── extraerContenido() - Parsea respuesta JSON
```

## 🆚 Comparación con OpenAI

### Similaridades
- API idéntica (endpoints, formato JSON)
- Mismo flujo de código
- Mismos conceptos (chat, mensajes, roles)

### Diferencias Clave

| Aspecto | OpenAI | Ollama |
|---------|--------|--------|
| **Autenticación** | Requiere API key (`Authorization: Bearer sk-...`) | Sin autenticación |
| **URL** | `https://api.openai.com/v1/chat/completions` | `http://localhost:11434/v1/chat/completions` |
| **Modelos** | gpt-3.5-turbo, gpt-4 | mistral, llama3.2, phi3, etc. |
| **Velocidad** | 1-2 segundos | 3-10 segundos (depende de CPU) |
| **Costo** | $0.002 por 1K tokens | $0 (solo electricidad) |
| **Calidad** | Excelente (GPT-4) | Buena (Mistral/Llama) |
| **Internet** | Requerido | No requerido (post-instalación) |

### Código Side-by-Side

#### OpenAI
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
    .header("Content-Type", "application/json")
    .header("Authorization", "Bearer " + apiKey)  // ← Requiere API key
    .timeout(Duration.ofSeconds(30))
    .POST(...)
    .build();
```

#### Ollama
```java
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:11434/v1/chat/completions"))
    .header("Content-Type", "application/json")
    // ← Sin header Authorization 🎉
    .timeout(Duration.ofMinutes(2))  // ← Timeout más largo
    .POST(...)
    .build();
```

## 🔧 Troubleshooting

### "Connection refused"
**Problema:** Ollama no está corriendo.

**Solución:**
```bash
ollama serve
```

### "Model not found"
**Problema:** El modelo no está instalado.

**Solución:**
```bash
ollama pull mistral
```

### Ollama muy lento
**Problema:** Tu máquina no tiene suficiente recursos.

**Soluciones:**
1. Usa un modelo más pequeño: `ollama pull llama3.2`
2. Cierra otras aplicaciones
3. Considera actualizar tu hardware (SSD, más RAM)
4. En producción, usa GPU dedicada

### "Out of memory"
**Problema:** No hay suficiente RAM.

**Soluciones:**
1. Usa modelo más pequeño (phi3, llama3.2)
2. Cierra otras aplicaciones
3. Considera actualizar RAM (mínimo 8 GB, recomendado 16 GB)

### No responde / Timeout
**Problema:** El modelo está tardando mucho.

**Soluciones:**
1. Primera ejecución siempre es más lenta (el modelo se carga en memoria)
2. Modelos grandes requieren más tiempo
3. Aumenta el timeout en el código (ya está en 2 minutos)

## 🎓 Casos de Uso

### Desarrollo y Aprendizaje
- ✅ Experimentar con diferentes modelos
- ✅ Prototipar sin gastar dinero
- ✅ Aprender sobre LLMs sin límites de uso

### Producción (con caveats)
- ✅ Aplicaciones internas (privacidad crítica)
- ✅ Procesamiento batch en servidores potentes
- ⚠️ No recomendado para producción de alta demanda (usa APIs cloud)

### Educación
- ✅ Cursos y talleres (sin consumir créditos)
- ✅ Tareas y proyectos estudiantiles
- ✅ Demostración de conceptos

## 📊 Benchmarks (en MacBook Pro M1, 16 GB RAM)

| Modelo | Primera Llamada | Llamadas Subsecuentes | Tokens/seg |
|--------|----------------|----------------------|------------|
| mistral | ~8 segundos | ~3-4 segundos | 15-20 |
| llama3.2 | ~5 segundos | ~2-3 segundos | 25-30 |
| phi3 | ~4 segundos | ~2 segundos | 30-35 |

**Nota:** Con GPU dedicada (NVIDIA RTX), las velocidades pueden ser 3-5x más rápidas.

## 🚀 Siguientes Pasos

1. **Experimentar con diferentes modelos** - Cada uno tiene fortalezas
2. **Agregar system prompts** - Para comportamiento personalizado
3. **Implementar streaming** - Para respuestas en tiempo real
4. **Usar embeddings locales** - Para búsqueda semántica
5. **Integrar con RAG** - Sistema completo local

## 📚 Recursos Adicionales

- Documentación oficial: https://ollama.com/docs
- Catálogo de modelos: https://ollama.com/library
- API Reference: https://github.com/ollama/ollama/blob/main/docs/api.md
- Ollama GitHub: https://github.com/ollama/ollama
- Guía completa del curso: `docs/OLLAMA-GUIA-COMPLETA.md`

## 💡 Tips

1. **Primera ejecución lenta es normal** - El modelo se carga en memoria
2. **Cierra otras apps** - Libera RAM para el modelo
3. **Usa SSD** - Mejora velocidad de carga del modelo
4. **Modelos pequeños para desarrollo** - Cambiar a grandes para producción
5. **Combina con OpenAI** - Ollama para dev, OpenAI para prod

---

**Versión:** Solution completa (sin TODOs)  
**Última actualización:** Marzo 2026
