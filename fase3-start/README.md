# 🧠 Fase 3 Start: Chatbots con Memoria

## 📋 Descripción

**Punto de partida** para implementar chatbots con memoria conversacional.

En este módulo aprenderás a:
- ✅ Entender por qué los chatbots necesitan memoria
- ✅ Implementar buffer memory usando `List<Mensaje>`
- ✅ Guardar conversaciones en archivos JSON
- ✅ Gestionar múltiples sesiones de usuario

---

## 🎯 Laboratorios a Completar

### Lab 7: Buffer Memory ⭐⭐

**Archivo:** `ChatbotConMemoria.java`  
**Dificultad:** Intermedio  
**Tiempo estimado:** 20-30 minutos

**Objetivo:** Implementar un chatbot que recuerda toda la conversación usando `List<Mensaje>`.

**TODOs a completar:**
1. ✅ Inicializar `ServicioIA` y el historial
2. ✅ Agregar system prompt inicial
3. ✅ Implementar flujo: agregar mensaje → enviar → guardar respuesta
4. ✅ Implementar comandos `/historial` y `/limpiar`
5. ✅ Implementar método `enviarConHistorial()`
6. ✅ Implementar método `mostrarHistorial()`

**Prueba de éxito:**
```
Tú: Hola, me llamo Juan y me gustan las pizzas
Bot: Hola Juan, qué interesante que te gusten las pizzas...

Tú: ¿Cuál es mi nombre y qué me gusta?
Bot: Tu nombre es Juan y te gustan las pizzas ✅
```

---

### Lab 8: Memoria Persistente ⭐⭐⭐

**Archivo:** `ChatbotConMemoriaPersistente.java`  
**Dificultad:** Avanzado  
**Tiempo estimado:** 30-45 minutos

**Objetivo:** Guardar conversaciones en archivos JSON para recuperarlas después.

**TODOs a completar:**
1. ✅ Crear directorio de sesiones
2. ✅ Solicitar session ID al usuario
3. ✅ Implementar `cargarSesion()` - leer desde archivo
4. ✅ Implementar `guardarSesion()` - escribir a archivo
5. ✅ Implementar `convertirAJson()` - serialización
6. ✅ (BONUS) Implementar `parsearJson()` - deserialización

**Prueba de éxito:**
```bash
# Primera ejecución
Ingresa un ID: juan
Tú: Me llamo Juan
Bot: Hola Juan...
Tú: salir
💾 Conversación guardada

# Segunda ejecución (mismo ID)
Ingresa un ID: juan
📂 Sesión cargada: juan (2 mensajes)
Tú: ¿Cuál es mi nombre?
Bot: Tu nombre es Juan ✅
```

---

## 🚀 Cómo Empezar

### 1. Configurar API Key

```bash
# En la raíz del proyecto
export OPENAI_API_KEY=sk-tu-clave

# O crear archivo .env
echo "OPENAI_API_KEY=sk-tu-clave" > ../.env
```

### 2. Ejecutar el Demo del Problema

Primero, observa el problema que vamos a resolver:

```bash
cd fase3-start
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotSinMemoria"
```

**Observa:** El bot NO recuerda tu nombre cuando se lo preguntas.

### 3. Completar Lab 7

Abre `ChatbotConMemoria.java` y completa todos los TODOs.

**Pistas:**
- Usa `List<Mensaje> historial = new ArrayList<>();`
- Cada mensaje tiene un rol: "system", "user", "assistant"
- Envía TODO el historial en cada request
- El método `servicio.chatConHistorial()` ya está implementado en `common`

**Verificar:**
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoria"
```

### 4. Completar Lab 8

Abre `ChatbotConMemoriaPersistente.java` y completa todos los TODOs.

**Pistas:**
- Usa `Files.readString()` y `Files.writeString()`
- Formato JSON: `[{"rol":"user","contenido":"Hola"},...]`
- Escapa caracteres especiales con `escapeJson()`
- El parseo de JSON es BONUS (complejo)

**Verificar:**
```bash
mkdir -p sessions
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoriaPersistente"
```

---

## 📖 Conceptos Clave

### ¿Qué es la memoria conversacional?

Los LLMs son **stateless** (sin estado). Cada request es independiente:

```
❌ SIN MEMORIA:
Request 1: "Me llamo Ana" → OK
Request 2: "¿Mi nombre?" → "No lo sé"

✅ CON MEMORIA:
Request: [
  {user: "Me llamo Ana"},
  {assistant: "Hola Ana"},
  {user: "¿Mi nombre?"}
]
→ "Tu nombre es Ana"
```

### Buffer Memory

Estrategia más simple: **guardar todos los mensajes** en una lista.

```java
List<Mensaje> historial = new ArrayList<>();

// System prompt
historial.add(new Mensaje("system", "Eres un asistente..."));

// Conversación
historial.add(Mensaje.usuario("Hola"));
historial.add(Mensaje.asistente("¿En qué puedo ayudarte?"));
historial.add(Mensaje.usuario("Mi nombre es Juan"));
historial.add(Mensaje.asistente("Hola Juan"));

// Siguiente pregunta - envía TODO el historial
String respuesta = servicio.chatConHistorial(historial, "¿Cuál es mi nombre?");
// → "Tu nombre es Juan" ✅
```

**Ventajas:**
- ✅ Simple de implementar
- ✅ Contexto completo siempre disponible

**Desventajas:**
- ❌ Crece infinitamente
- ❌ Puede superar el context window
- ❌ Más tokens = mayor costo

### Persistent Memory

Guardar el historial en disco para sobrevivir a reinicios.

**Casos de uso:**
- Aplicaciones multi-usuario
- Recuperar conversaciones previas
- Análisis histórico
- Backup y seguridad

**Estructura de archivos:**
```
fase3-start/
  sessions/
    juan.json
    maria.json
    usuario-123.json
```

**Formato JSON:**
```json
[
  { "rol": "system", "contenido": "Eres un asistente..." },
  { "rol": "user", "contenido": "Me llamo Juan" },
  { "rol": "assistant", "contenido": "Hola Juan" }
]
```

---

## 💡 Pistas y Ayuda

### Lab 7: Buffer Memory

**Inicialización:**
```java
ServicioIA servicio = new ServicioIA();
List<Mensaje> historial = new ArrayList<>();
historial.add(new Mensaje("system", "Eres un asistente amigable..."));
```

**Flujo de conversación:**
```java
// 1. Usuario escribe
historial.add(Mensaje.usuario(entrada));

// 2. Enviamos TODO el historial
String respuesta = enviarConHistorial(servicio, historial);

// 3. Guardamos respuesta
historial.add(Mensaje.asistente(respuesta));
```

**Método enviarConHistorial:**
```java
private static String enviarConHistorial(ServicioIA servicio, List<Mensaje> historial) {
    // Separar el último mensaje (del usuario)
    Mensaje ultimo = historial.get(historial.size() - 1);
    List<Mensaje> previo = historial.subList(0, historial.size() - 1);
    
    // Enviar
    return servicio.chatConHistorial(previo, ultimo.contenido());
}
```

### Lab 8: Memoria Persistente

**Cargar sesión:**
```java
private static List<Mensaje> cargarSesion(String sessionId) {
    Path archivo = obtenerArchivo(sessionId);
    
    if (!Files.exists(archivo)) {
        return new ArrayList<>(); // Nueva sesión
    }
    
    String json = Files.readString(archivo);
    return parsearJson(json); // Convertir de JSON
}
```

**Guardar sesión:**
```java
private static void guardarSesion(String sessionId, List<Mensaje> historial) {
    Path archivo = obtenerArchivo(sessionId);
    String json = convertirAJson(historial);
    Files.writeString(archivo, json);
}
```

**Convertir a JSON:**
```java
private static String convertirAJson(List<Mensaje> mensajes) {
    StringBuilder json = new StringBuilder("[\n");
    
    for (int i = 0; i < mensajes.size(); i++) {
        Mensaje m = mensajes.get(i);
        json.append("  {")
            .append("\"rol\":\"").append(escapeJson(m.rol())).append("\",")
            .append("\"contenido\":\"").append(escapeJson(m.contenido())).append("\"")
            .append("}");
        
        if (i < mensajes.size() - 1) {
            json.append(",");
        }
        json.append("\n");
    }
    
    json.append("]");
    return json.toString();
}
```

---

## ✅ Checklist de Completado

Marca cuando completes cada tarea:

### Lab 7: ChatbotConMemoria.java
- [ ] Inicializar `ServicioIA`
- [ ] Crear lista `historial`
- [ ] Agregar system prompt inicial
- [ ] Implementar flujo: agregar → enviar → guardar
- [ ] Implementar comando `/historial`
- [ ] Implementar comando `/limpiar`
- [ ] Implementar método `enviarConHistorial()`
- [ ] Implementar método `mostrarHistorial()`
- [ ] Probar: bot recuerda tu nombre ✅

### Lab 8: ChatbotConMemoriaPersistente.java
- [ ] Crear directorio `sessions/`
- [ ] Solicitar session ID
- [ ] Implementar `cargarSesion()`
- [ ] Implementar `guardarSesion()`
- [ ] Implementar `convertirAJson()`
- [ ] Guardar automáticamente después de cada mensaje
- [ ] Probar: crear sesión "juan"
- [ ] Probar: salir y volver a entrar con "juan"
- [ ] Verificar: historial se recupera ✅
- [ ] (BONUS) Implementar `parsearJson()`

---

## 🐛 Solución de Problemas

### Error: "Falta OPENAI_API_KEY"

```bash
# Verificar
echo $OPENAI_API_KEY

# Configurar
export OPENAI_API_KEY=sk-tu-clave
```

### Error: "Cannot find symbol: ServicioIA"

```bash
# Compilar desde la raíz
cd ..
mvn clean install -DskipTests
cd fase3-start
```

### El historial no se guarda (Lab 8)

```bash
# Verificar que existe el directorio
ls -la sessions/

# Si no existe:
mkdir -p sessions
```

### NullPointerException

- ¿Inicializaste `servicio`?
- ¿Inicializaste `historial`?
- ¿Agregaste el system prompt?

---

## 🎓 Comparar con la Solución

Después de completar los labs, compara tu código con la solución oficial:

```bash
# Ver la solución completa
cd ../fase3

# Comparar archivos
diff fase3-start/src/.../ChatbotConMemoria.java \
     fase3/src/.../ChatbotConMemoria.java
```

---

## 🚀 Siguiente Paso

Una vez completes estos labs, estarás listo para:

- **Fase 4:** Embeddings y Vector Databases
- **Fase 5:** RAG (Retrieval Augmented Generation)
- **Fase 6:** Tool Calling
- **Fase 7:** AI Agents

---

## 📚 Recursos Adicionales

- [OpenAI Chat Completions](https://platform.openai.com/docs/guides/chat)
- [Java NIO File Operations](https://docs.oracle.com/javase/tutorial/essential/io/file.html)
- [JSON Specification](https://www.json.org/)
- [Prompt Engineering Guide](https://www.promptingguide.ai/)

---

**¡Buena suerte con los labs! 🚀**

Si tienes dudas, consulta al instructor o revisa la documentación en `fase3/README.md`.
