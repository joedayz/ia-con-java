# 🧠 Fase 3: Chatbots con Memoria

## 📋 Descripción

En esta fase aprendemos a implementar **memoria conversacional** en chatbots, permitiendo que el LLM recuerde el contexto de conversaciones anteriores.

Sin memoria, cada pregunta es tratada de forma independiente. Con memoria, el chatbot mantiene el contexto y puede responder de forma coherente a lo largo de la conversación.

---

## 🎯 Objetivos de Aprendizaje

- ✅ Entender qué es la memoria conversacional y por qué es importante
- ✅ Implementar buffer memory usando `List<Mensaje>`
- ✅ Guardar conversaciones en archivos JSON (persistencia)
- ✅ Gestionar múltiples sesiones de usuario
- ✅ Aplicar diferentes estrategias de memoria (buffer, window, persistent)

---

## 📚 Contenido

### Demos Introductorias

| Archivo | Descripción | Lab |
|---------|-------------|-----|
| `ChatbotSinMemoria.java` | Demuestra el problema: sin contexto | Intro |
| `ComparacionMemoria.java` | Compara respuestas con/sin memoria | Demo |
| `ChatbotMultiProveedor.java` | Chatbot con soporte OpenAI/Anthropic/Gemini | Demo |

### Laboratorios

| Lab | Archivo | Descripción | Concepto |
|-----|---------|-------------|----------|
| **Lab 7** | `ChatbotConMemoria.java` | Memoria en lista (buffer) | Buffer memory |
| **Lab 8** | `ChatbotConMemoriaPersistente.java` | Memoria guardada en JSON | Persistent memory |

### Reto

| Archivo | Descripción | Nivel |
|---------|-------------|-------|
| `GestorMultiSesion.java` | Gestión de múltiples usuarios/sesiones | Avanzado |

---

## 🚀 Ejecución Rápida

### Método 1: Script Interactivo (Recomendado)

```bash
cd fase3
chmod +x test-fase3.sh
./test-fase3.sh
```

### Método 2: Ejecutar Labs Individualmente

```bash
# Demo del problema (sin memoria)
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotSinMemoria"

# Comparación con/sin memoria
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ComparacionMemoria"

# Demo multi-proveedor
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiProveedor"
# Con Anthropic
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiProveedor" \
  -Dexec.args="anthropic"
# Con Gemini
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiProveedor" \
  -Dexec.args="gemini"

# Lab 7: Buffer memory
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoria"

# Lab 8: Memoria persistente
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoriaPersistente"

# Reto: Multi-sesión
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.GestorMultiSesion"
```

---

## 🧪 Detalles de los Laboratorios

### Lab 7: Buffer Memory

**Objetivo:** Implementar memoria conversacional básica usando `List<Mensaje>`.

**Concepto clave:** Buffer Memory (guarda todos los mensajes)

```java
List<ServicioIA.Mensaje> historial = new ArrayList<>();

// System prompt inicial
historial.add(new Mensaje("system", "Eres un asistente..."));

// Flujo de conversación
while (true) {
    String pregunta = leerPregunta();
    historial.add(Mensaje.usuario(pregunta));
    
    String respuesta = servicio.chatConHistorial(historial, "");
    historial.add(Mensaje.asistente(respuesta));
}
```

**Prueba esto:**
```
Tú: Hola, me llamo Juan y me gustan las pizzas
Bot: Hola Juan, qué interesante que te gusten las pizzas...

Tú: ¿Cuál es mi nombre y qué me gusta?
Bot: Tu nombre es Juan y te gustan las pizzas ✅
```

**Comandos especiales:**
- `/historial` - Ver todos los mensajes guardados
- `/limpiar` - Borrar historial y empezar de nuevo
- `salir` - Terminar

---

### Lab 8: Memoria Persistente

**Objetivo:** Guardar conversaciones en archivos JSON para recuperarlas después.

**Concepto clave:** Persistent Memory (sobrevive a reinicios)

**Estructura de archivos:**
```
fase3/
  sessions/
    usuario-123.json
    maria.json
    session-abc.json
```

**Formato JSON:**
```json
[
  { "rol": "system", "contenido": "Eres un asistente..." },
  { "rol": "user", "contenido": "Hola, me llamo Juan" },
  { "rol": "assistant", "contenido": "Hola Juan..." }
]
```

**Flujo de trabajo:**

1. **Primera sesión:**
```bash
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoriaPersistente"

Ingresa un ID de sesión: juan
📝 Nueva sesión creada: juan

Tú: Me llamo Juan y trabajo en tecnología
Bot: Hola Juan, interesante que trabajes en tecnología...

Tú: salir
💾 Conversación guardada
```

2. **Retomar sesión:**
```bash
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoriaPersistente"

Ingresa un ID de sesión: juan
📂 Sesión cargada: juan (2 mensajes previos)

Tú: ¿A qué me dedico?
Bot: Trabajas en tecnología ✅
```

---

### Reto: Gestor Multi-Sesión

**Objetivo:** Manejar múltiples usuarios simultáneamente.

**Comandos disponibles:**

| Comando | Descripción | Ejemplo |
|---------|-------------|---------|
| `/nueva [id]` | Crear/cambiar sesión | `/nueva maria` |
| `/listar` | Ver todas las sesiones | `/listar` |
| `/info` | Info de sesión actual | `/info` |
| `/borrar [id]` | Eliminar sesión | `/borrar maria` |
| `/historial` | Ver mensajes | `/historial` |
| `salir` | Guardar y salir | `salir` |

**Ejemplo de uso:**
```
[sin sesión] ⚠️  Comando: /nueva maria
📝 Nueva sesión creada: maria

[maria] 💬 Tú: Hola, soy María
🤖 Bot: Hola María...

[maria] 💬 Tú: /nueva pedro
💾 Sesión guardada: maria
📝 Nueva sesión creada: pedro

[pedro] 💬 Tú: Hola, soy Pedro
🤖 Bot: Hola Pedro...

[pedro] 💬 Tú: /nueva maria
💾 Sesión guardada: pedro
📂 Sesión cargada: maria (2 mensajes)

[maria] 💬 Tú: ¿Cuál es mi nombre?
🤖 Bot: Tu nombre es María ✅
```

---

## 💡 Conceptos Clave

### 1. ¿Por qué necesitamos memoria?

Los LLMs son **stateless** (sin estado). Cada request es independiente:

```
❌ SIN MEMORIA:
Request 1: "Me llamo Ana" → OK
Request 2: "¿Cuál es mi nombre?" → "No lo sé"

✅ CON MEMORIA:
Request: [
  {user: "Me llamo Ana"},
  {assistant: "Hola Ana"},
  {user: "¿Cuál es mi nombre?"}
]
→ "Tu nombre es Ana"
```

### 2. Tipos de memoria

| Tipo | Estrategia | Caso de uso | Ventaja | Desventaja |
|------|-----------|-------------|---------|------------|
| **Buffer** | Guarda todo | Conversaciones cortas | Simple | Crece infinitamente |
| **Window** | Últimos N mensajes | Chats largos | Límite de tokens | Pierde contexto antiguo |
| **Summary** | Resume mensajes antiguos | Conversaciones muy largas | Mantiene contexto | Costo adicional (LLM resume) |
| **Persistent** | Guarda en disco/DB | Multi-sesión, recuperación | Sobrevive reinicios | I/O overhead |

### 3. Window Memory (ventana deslizante)

Mantiene solo los últimos N mensajes para evitar superar el context window:

```java
private static final int MAX_MENSAJES = 10;

List<Mensaje> obtenerVentana(List<Mensaje> historial) {
    if (historial.size() <= MAX_MENSAJES) {
        return historial;
    }
    // Retornar solo los últimos MAX_MENSAJES
    return historial.subList(historial.size() - MAX_MENSAJES, historial.size());
}
```

### 4. Context Window Limits

Diferentes modelos tienen diferentes límites:

| Modelo | Context Window | Aprox. tokens |
|--------|---------------|---------------|
| GPT-3.5-turbo | 4K-16K tokens | ~3K-12K palabras |
| GPT-4 | 8K-128K tokens | ~6K-96K palabras |
| GPT-4-turbo | 128K tokens | ~96K palabras |
| Claude 3 | 200K tokens | ~150K palabras |
| Gemini 1.5 Pro | 2M tokens | ~1.5M palabras |

**Regla general:** 1 token ≈ 0.75 palabras en inglés

---

## 🔧 Configuración

### Requisitos previos

1. **API Key configurada:**
```bash
# Opción 1: Variable de entorno
export OPENAI_API_KEY=sk-tu-clave

# Opción 2: Archivo .env (raíz del proyecto)
echo "OPENAI_API_KEY=sk-tu-clave" > ../.env
```

2. **Compilar el proyecto:**
```bash
cd ..
mvn clean install -DskipTests
cd fase3
```

3. **Crear directorio de sesiones:**
```bash
mkdir -p sessions
```

---

## 🧩 Multi-Provider Support

Los ejemplos funcionan con **OpenAI, Anthropic (Claude) y Gemini**:

### OpenAI (default)

mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiProveedor" \
  -Dexec.args="anthropic"
```

### Google Gemini

```bash
export GEMINI_API_KEY=tu-clave-gemini
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotMultiProveedor" \
  -Dexec.args="gemini"
```

**Nota:** `ChatbotMultiProveedor.java` es un ejemplo que demuestra cómo cambiar entre proveedores dinámicamente. Los demás ejemplos usan OpenAI por defecto, pero puedes modificarlos para usar otros proveedores cambiando la inicialización de `ServicioIA`:
```bash
export GEMINI_API_KEY=tu-clave-gemini
# Crear instancia con Gemini endpoint
# ServicioIA servicio = new ServicioIA(
#     EnvConfig.getGeminiApiBase(),
#     EnvConfig.getGeminiApiKey(),
#     "gemini-pro"
# );
```

---

## 📊 Comparación de Estrategias

| Característica | Buffer | Window | Summary | Persistent |
|----------------|--------|--------|---------|------------|
| Simplicidad | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐ | ⭐⭐⭐ |
| Uso de memoria | ❌ Alto | ✅ Bajo | ✅ Bajo | ✅ Bajo |
| Contexto completo | ✅ Sí | ❌ Parcial | ⚠️ Resumido | ✅ Sí |
| Sobrevive reinicio | ❌ No | ❌ No | ❌ No | ✅ Sí |
| Multi-usuario | ❌ No | ❌ No | ❌ No | ✅ Sí |
| Costo (tokens) | ❌ Alto | ✅ Bajo | ⚠️ Medio | ⚠️ Variable |

---

## 🐛 Solución de Problemas

### Error: "API error 401"

```bash
# Verificar que la API key esté configurada
echo $OPENAI_API_KEY

# Si no está, configurar:
export OPENAI_API_KEY=sk-tu-clave
```

### Error: "Falta OPENAI_API_KEY"

```bash
# Opción 1: Exportar variable
export OPENAI_API_KEY=sk-tu-clave

# Opción 2: Crear archivo .env en la raíz
cd ..
echo "OPENAI_API_KEY=sk-tu-clave" > .env
cd fase3
```

### No se guardan las sesiones

```bash
# Verificar que existe el directorio
ls -la sessions/

# Si no existe, crear:
mkdir -p sessions
```

### Sesión corrupta (JSON inválido)

```bash
# Eliminar archivo corrupto y empezar de nuevo
rm sessions/tu-sesion.json
```

### Rate limit (429)

```bash
# Espera 1 minuto antes de reintentar
# O reduce la frecuencia de llamadas
```

---

## 🎓 Ejercicios Propuestos

### Ejercicio 1: Window Memory

Modifica `ChatbotConMemoria.java` para que solo mantenga los últimos 8 mensajes:

```java
private static final int MAX_MENSAJES = 8;

List<Mensaje> obtenerVentana(List<Mensaje> historial) {
    // Tu código aquí
}
```

### Ejercicio 2: Comando /exportar

Agrega un comando que exporte el historial a Markdown:

```markdown
# Conversación con IA

**Usuario:** Hola
**Asistente:** ¿En qué puedo ayudarte?
```

### Ejercicio 3: Timestamps

Agrega timestamp a cada mensaje:

```java
public record MensajeConTimestamp(
    String rol, 
    String contenido, 
    LocalDateTime timestamp
) {}
```

### Ejercicio 4: Chatbot Especializado

Crea un chatbot tutor de Java que recuerda los errores comunes del estudiante.

### Ejercicio 5: Summary Memory

Implementa una estrategia que resuma los primeros 15 mensajes cuando el historial supera 20 mensajes.

---

## 📖 Recursos Adicionales

### Documentación oficial

- [OpenAI Chat Completions](https://platform.openai.com/docs/guides/chat)
- [Anthropic Messages API](https://docs.anthropic.com/claude/reference/messages_post)
- [Google Gemini API](https://ai.google.dev/docs)

### Guías y tutoriales

- [Prompt Engineering Guide - Memory](https://www.promptingguide.ai/techniques/memory)
- [LangChain Memory Docs](https://python.langchain.com/docs/modules/memory/)
- [Building Conversational AI](https://platform.openai.com/docs/guides/chat/introduction)

### Papers y artículos

- [Attention Is All You Need](https://arxiv.org/abs/1706.03762) - Transformers
- [GPT-4 Technical Report](https://arxiv.org/abs/2303.08774)

---

## ✅ Checklist de Aprendizaje

Después de completar esta fase, deberías poder:

- [ ] Explicar qué es la memoria conversacional
- [ ] Implementar buffer memory con `List<Mensaje>`
- [ ] Guardar y cargar conversaciones desde JSON
- [ ] Gestionar múltiples sesiones de usuario
- [ ] Elegir la estrategia de memoria apropiada para cada caso
- [ ] Calcular cuántos tokens consume una conversación
- [ ] Depurar problemas de serialización JSON
- [ ] Manejar errores de API (401, 429, etc.)

---

## 🚀 Próximos Pasos

En la **Fase 4** aprenderemos sobre:

- 🧬 **Embeddings** y vectores
- 🔍 **Búsqueda semántica**
- 📚 **Vector databases**
- 🤖 **RAG** (Retrieval Augmented Generation)

---

## 📞 Soporte

¿Tienes dudas? 

- 💬 Pregunta en clase
- 📧 Email al instructor
- 🐛 Reporta issues en el repositorio

---

**¡Feliz aprendizaje! 🎉**
