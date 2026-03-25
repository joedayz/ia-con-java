# 🎯 Fase 2 - Prompt Engineering

**Objetivo:** Dominar las técnicas de prompt engineering para obtener mejores respuestas de los LLMs.

---

## 📚 Conceptos Clave

### System Prompts
Define el comportamiento, personalidad y restricciones del modelo:
- **Rol:** "Eres un asistente técnico", "Eres un profesor de matemáticas"
- **Restricciones:** "Responde siempre en español", "Sé conciso"
- **Formato:** "Devuelve solo código sin explicaciones"

### Técnicas de Prompting

#### 1. Zero-Shot
Pregunta directa sin ejemplos:
```
❌ Malo:   "Clasifica esto"
✅ Bueno:  "Clasifica el siguiente texto como POSITIVO, NEGATIVO o NEUTRO.
            Responde solo con la clasificación en mayúsculas."
```

#### 2. Few-Shot
Incluir 2-3 ejemplos en el prompt:
```
Clasifica el sentimiento de textos:

Texto: "Me encantó la película" → POSITIVO
Texto: "Fue una pérdida de tiempo" → NEGATIVO
Texto: "Estuvo normal" → NEUTRO

Texto: "{input}" → 
```

#### 3. Chain of Thought (CoT)
Pedir al modelo que piense paso a paso:
```
"Resuelve este problema paso a paso, mostrando tu razonamiento:
¿Cuánto es 15% de 240?"
```

#### 4. Salida Estructurada
Pedir respuestas en formato específico (JSON, Markdown, listas):
```
"Analiza el siguiente texto y responde en JSON:
{
  "sentimiento": "POSITIVO|NEGATIVO|NEUTRO",
  "confianza": 0.0-1.0,
  "palabrasClave": ["palabra1", "palabra2"]
}"
```

---

## � Soporte Multi-Proveedor

Todos los labs de Fase 2 soportan **tres proveedores de IA**:
- 🟢 **OpenAI** (GPT-3.5 Turbo)
- 🟣 **Anthropic** (Claude 3 Haiku)
- 🔵 **Google Gemini** (Gemini 2.5 Flash)

### Configuración

#### 1. Variables de Entorno (Recomendado para instructores)

**Para Mac/Linux (Bash/Zsh):**
```bash
# Crear archivo ~/.api-keys (fuera del proyecto)
nano ~/.api-keys

# Agregar tus API keys:
export OPENAI_API_KEY="sk-proj-..."
export ANTHROPIC_API_KEY="sk-ant-..."
export GEMINI_API_KEY="AIza..."

# Guardar y dar permisos restrictivos
chmod 600 ~/.api-keys

# Cargar en cada sesión de terminal
source ~/.api-keys

# Opcional: Auto-cargar en cada terminal nueva
echo 'source ~/.api-keys' >> ~/.zshrc
```

**Para Windows (PowerShell):**
```powershell
# Editar perfil de PowerShell
notepad $PROFILE

# Agregar estas líneas:
$env:OPENAI_API_KEY = "sk-proj-..."
$env:ANTHROPIC_API_KEY = "sk-ant-..."
$env:GEMINI_API_KEY = "AIza..."

# Guardar y recargar
. $PROFILE

# O configurar temporalmente (solo sesión actual):
$env:OPENAI_API_KEY = "sk-proj-..."
```

#### 2. Archivo `.env` en la raíz del proyecto (Para estudiantes)

Configura al menos una de estas claves API:

```properties
# OpenAI (por defecto si no se especifica proveedor)
OPENAI_API_KEY=sk-...
OPENAI_API_BASE=https://api.openai.com/v1

# Anthropic Claude
ANTHROPIC_API_KEY=sk-ant-...

# Google Gemini
GEMINI_API_KEY=AIza...
```

#### 3. Variable de entorno `AI_PROVIDER` (opcional)

Para configurar un proveedor por defecto:

```bash
# En .env
AI_PROVIDER=anthropic  # o 'openai' o 'gemini'
```

#### 3. Argumento por línea de comandos `--provider=X`

Puedes especificar el proveedor al ejecutar cada lab:

```bash
# Mac/Linux - Usar OpenAI
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento \
    -Dexec.args="--provider=openai"

# Windows PowerShell - Usar Anthropic
mvn -pl fase2 exec:java "-Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento" `
    "-Dexec.args=--provider=anthropic"

# Usar Google Gemini
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento \
    -Dexec.args="--provider=gemini"
```

### Prioridad de Configuración

El sistema determina qué proveedor usar en este orden:

1. **Argumento `--provider=X`** (mayor prioridad)
2. **Variable `AI_PROVIDER` en .env**
3. **Auto-detección** (busca en orden: OpenAI → Gemini → Anthropic)

### Script Interactivo

Scripts disponibles para ambos sistemas operativos:

**Mac/Linux:**
```bash
# Menú interactivo con selector de proveedor
./test-fase2.sh

# O ejecutar lab específico directamente
./test-fase2.sh 1  # Lab 5: Chatbot Interactivo
./test-fase2.sh 2  # Lab 6: Clasificador Sentimientos
./test-fase2.sh 3  # Bonus: Chain of Thought
./test-fase2.sh 4  # Bonus: Salida Estructurada
./test-fase2.sh 5  # Demo: Zero-Shot vs Few-Shot
```

**Windows PowerShell:**
```powershell
# Primero permitir ejecución de scripts (una sola vez)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Menú interactivo con selector de proveedor
.\test-fase2.ps1

# O ejecutar lab específico directamente
.\test-fase2.ps1 1  # Lab 5: Chatbot Interactivo
.\test-fase2.ps1 2  # Lab 6: Clasificador Sentimientos
.\test-fase2.ps1 3  # Bonus: Chain of Thought
.\test-fase2.ps1 4  # Bonus: Salida Estructurada
.\test-fase2.ps1 5  # Demo: Zero-Shot vs Few-Shot
```

El script detecta automáticamente si tienes las API keys en:
1. **Variables de entorno** (prioridad 1) - `source ~/.api-keys` o `$PROFILE`
2. **Archivo `.env`** (prioridad 2) - En la raíz del proyecto

### Diferencias entre Proveedores

| Característica | OpenAI | Anthropic | Gemini |
|----------------|--------|-----------|--------|
| **Velocidad** | Rápido | Medio | Muy rápido |
| **Precisión** | Alta | Muy alta | Alta |
| **Costo** | Medio | Alto | Bajo/Gratis |
| **Formato JSON** | Excelente | Bueno | Muy bueno |
| **Few-Shot** | Excelente | Excelente | Bueno |
| **Chain of Thought** | Bueno | Excelente | Bueno |

### Ejemplos de Uso

#### Comparar proveedores en el mismo texto

```bash
# Ejecutar con los 3 proveedores y comparar resultados
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento \
    -Dexec.args="--provider=openai"
    
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento \
    -Dexec.args="--provider=anthropic"
    
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento \
    -Dexec.args="--provider=gemini"
```

#### Usar proveedor específico para tarea específica

```bash
# Gemini para clasificación rápida (más económico)
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ComparacionZeroShotVsFewShot \
    -Dexec.args="--provider=gemini"

# Claude para razonamiento complejo (mejor calidad)
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ChainOfThought \
    -Dexec.args="--provider=anthropic"
```

---

## �🧪 Laboratorios

### Lab 5: Chatbot Interactivo con System Prompt

**Archivo:** `PromptEngineering.java`

Ejecutar:
```bash
mvn -pl fase2 exec:java
```

**Qué hace:**
- Chatbot interactivo por consola
- Usa un system prompt predefinido: "Eres un asistente técnico conciso"
- Demuestra cómo el system prompt afecta el comportamiento

**Prueba:**
1. Pregunta técnica: "¿Qué es Java?"
2. Pregunta casual: "¿Qué tiempo hace?"
3. Observa cómo mantiene el tono técnico y conciso

**Ejercicio:**
Modifica el `SYSTEM_PROMPT` para que sea:
- Un pirata: "Eres un pirata que habla solo en español con jerga pirata"
- Un poeta: "Eres un poeta que responde siempre en versos"

---

### Lab 6: Clasificador de Sentimientos (Few-Shot)

**Archivo:** `ClasificadorSentimiento.java`

Ejecutar:
```bash
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento
```

**Qué hace:**
- Clasificador de sentimientos usando few-shot learning
- Incluye 3 ejemplos en el prompt
- Muestra cómo los ejemplos mejoran la precisión

**Prueba con:**
- "El producto es excelente y superó mis expectativas"
- "No funcionó como esperaba, muy decepcionante"
- "Es un producto normal, nada especial"
- "¡Increíble! Lo recomiendo totalmente"

---

### Lab Bonus: Chain of Thought

**Archivo:** `ChainOfThought.java`

Ejecutar:
```bash
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ChainOfThought
```

**Qué hace:**
- Demuestra la técnica Chain of Thought
- Pide al modelo que piense paso a paso
- Muestra el razonamiento completo

**Prueba con:**
- Problemas matemáticos
- Problemas lógicos
- Toma de decisiones

---

### Lab Bonus: Salida JSON Estructurada

**Archivo:** `SalidaEstructurada.java`

Ejecutar:
```bash
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.SalidaEstructurada
```

**Qué hace:**
- Solicita al LLM que responda en formato JSON
- Parsea el JSON con Java Records
- Muestra cómo estructurar datos de salida

---

### Demo: Comparación Zero-Shot vs Few-Shot

**Archivo:** `ComparacionZeroShotVsFewShot.java`

Ejecutar:
```bash
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ComparacionZeroShotVsFewShot
```

**Qué hace:**
- Ejecuta el mismo conjunto de textos con ambas técnicas
- Compara los resultados lado a lado
- Muestra métricas de tiempo y precisión
- Demuestra cuándo usar cada técnica

**Ideal para:**
- Entender las diferencias prácticas entre técnicas
- Ver casos donde Few-Shot es superior
- Aprender a elegir la técnica correcta

---

## 🎯 Retos

### Reto 1: Comparar Zero-Shot vs Few-Shot
Crea dos versiones del clasificador:
1. Sin ejemplos (zero-shot)
2. Con ejemplos (few-shot)

Prueba con el mismo texto y compara la calidad de las respuestas.

### Reto 2: System Prompt Avanzado
Crea un chatbot con system prompt complejo:
```
"Eres un asistente técnico especializado en Java.
REGLAS:
1. Responde siempre en español
2. Si te preguntan sobre código, incluye ejemplos
3. Sé conciso pero preciso
4. Si no sabes algo, admítelo
5. Usa bullets (•) para listar conceptos"
```

### Reto 3: JSON con Spring AI
Si usas Spring Boot, implementa un endpoint que devuelva un POJO:
```java
record AnalisisSentimiento(
    String sentimiento,
    double confianza,
    List<String> palabrasClave,
    String razonamiento
) {}

@GetMapping("/analizar")
public AnalisisSentimiento analizar(@RequestParam String texto) {
    return chatClient.prompt()
        .user("Analiza el sentimiento de: " + texto)
        .call()
        .entity(AnalisisSentimiento.class);
}
```

---

## 📊 Comparación de Técnicas

| Técnica | Cuándo Usarla | Ventajas | Desventajas |
|---------|---------------|----------|-------------|
| **Zero-Shot** | Tareas simples y claras | Rápido, menos tokens | Menos preciso |
| **Few-Shot** | Tareas que necesitan formato específico | Muy preciso | Más tokens/costo |
| **Chain of Thought** | Razonamiento complejo | Transparencia | Más lento |
| **Salida JSON** | Integración con apps | Estructurado | Requiere validación |

---

## 💡 Tips y Mejores Prácticas

### ✅ Buenas Prácticas
1. **Sé específico:** "Responde en 2 líneas" mejor que "Sé breve"
2. **Da contexto:** El system prompt debe dar todo el contexto necesario
3. **Usa delimitadores:** Usa `"""` o `###` para separar instrucciones de datos
4. **Itera:** Prueba y refina tus prompts

### ❌ Evita
1. **Prompts ambiguos:** "Haz algo con este texto"
2. **Demasiada información:** El model puede confundirse
3. **Contradicciones:** System prompt vs user prompt inconsistentes
4. **Asumir conocimiento:** El modelo no sabe del contexto externo

---

## 🔧 Comandos Útiles

```bash
# Mac/Linux: Menú interactivo con selector de proveedor (RECOMENDADO)
cd fase2
./test-fase2.sh

# O ejecutar directamente un lab específico:
./test-fase2.sh 1  # Lab 5: Chatbot Interactivo
./test-fase2.sh 2  # Lab 6: Clasificador Sentimientos
./test-fase2.sh 3  # Bonus: Chain of Thought
./test-fase2.sh 4  # Bonus: Salida Estructurada
./test-fase2.sh 5  # Demo: Zero-Shot vs Few-Shot
```

```powershell
# Windows PowerShell: Menú interactivo (RECOMENDADO)
cd fase2
.\test-fase2.ps1

# O ejecutar directamente un lab específico:
.\test-fase2.ps1 1  # Lab 5: Chatbot Interactivo
.\test-fase2.ps1 2  # Lab 6: Clasificador Sentimientos
.\test-fase2.ps1 3  # Bonus: Chain of Thought
.\test-fase2.ps1 4  # Bonus: Salida Estructurada
.\test-fase2.ps1 5  # Demo: Zero-Shot vs Few-Shot
```

**Comandos Maven individuales (multiplataforma):**

```bash
# Ejecutar el chatbot interactivo
mvn -pl fase2 exec:java  # USA: configuración por defecto
mvn -pl fase2 exec:java -Dexec.args="--provider=anthropic"  # USA: Claude

# Ejecutar el clasificador
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ClasificadorSentimiento \
    -Dexec.args="--provider=gemini"

# Ejecutar Chain of Thought
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ChainOfThought \
    -Dexec.args="--provider=anthropic"  # Claude es excelente para razonamiento

# Ejecutar Salida Estructurada
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.SalidaEstructurada \
    -Dexec.args="--provider=openai"  # GPT-3.5 maneja bien JSON

# Ejecutar Demo Comparativa
mvn -pl fase2 exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ComparacionZeroShotVsFewShot \
    -Dexec.args="--provider=gemini"  # Gemini es rápido y económico

# Compilar solo fase2
mvn -pl fase2 clean compile

# Empaquetar
mvn -pl fase2 clean package
```

---

## 📖 Recursos Adicionales

- [OpenAI Prompt Engineering Guide](https://platform.openai.com/docs/guides/prompt-engineering)
- [Anthropic Prompt Engineering](https://docs.anthropic.com/claude/docs/prompt-engineering)
- [Learn Prompting](https://learnprompting.org/)

---

## 🐛 Troubleshooting

### Problemas con Configuración

**Error: API Key no configurada**
```bash
# Verifica que el archivo .env existe en la raíz del proyecto
cat .env | grep OPENAI_API_KEY
cat .env | grep ANTHROPIC_API_KEY
cat .env | grep GEMINI_API_KEY

# Asegúrate de que al menos UNA clave API esté configurada
```

**Error: No se detecta ningún proveedor**
```
ERROR: No se encontró ninguna clave API configurada.
Configura al menos una de: OPENAI_API_KEY, ANTHROPIC_API_KEY, GEMINI_API_KEY
```
**Solución:** Agrega al menos una clave API en el archivo `.env` en la raíz del proyecto

**Error: Provider inválido**
```bash
# Los valores válidos son: openai, anthropic, gemini
mvn -pl fase2 exec:java -Dexec.args="--provider=openai"    # ✅ Correcto
mvn -pl fase2 exec:java -Dexec.args="--provider=claude"    # ❌ Error (usa 'anthropic')
mvn -pl fase2 exec:java -Dexec.args="--provider=google"    # ❌ Error (usa 'gemini')
```

### Problemas Específicos por Proveedor

**OpenAI: Rate Limit o Quota Exceeded**
- Espera unos segundos entre llamadas
- Verifica tu cuota en: https://platform.openai.com/account/usage
- Considera usar Gemini (tiene tier gratuito más generoso)

**Anthropic: Authentication Error**
- Verifica que tu clave comience con `sk-ant-`
- Las claves de Anthropic son diferentes a OpenAI
- Obtén tu clave en: https://console.anthropic.com/

**Gemini: 403 Forbidden o API Key Invalid**
- Verifica que tu clave comience con `AIza...`
- Habilita la API de Gemini en Google Cloud Console
- Obtén tu clave en: https://aistudio.google.com/app/apikey

### Problemas de Respuesta

**Respuestas inconsistentes**
- Ajusta `temperature` (0.0 = determinista, 1.0 = creativo)
- Usa `max_tokens` para limitar respuestas largas
- Prueba con `top_p` en lugar de `temperature`
- **Compara proveedores:** Claude suele ser más consistente que GPT-3.5

**JSON inválido en SalidaEstructurada**
- OpenAI y Gemini son más confiables para JSON
- Anthropic a veces agrega texto antes/después del JSON
- Agrega al prompt: "Responde SOLO con JSON puro, sin texto adicional"

**Few-Shot no funciona bien**
- Asegúrate de incluir al menos 3-5 ejemplos
- Los ejemplos deben ser claros y variados
- Claude (Anthropic) generalmente maneja mejor few-shot que otros

**Diferentes respuestas en diferentes proveedores**
- Esto es NORMAL - cada modelo tiene características únicas
- Para tareas críticas, ejecuta con múltiples proveedores y compara
- Usa el lab `ComparacionZeroShotVsFewShot` para evaluar diferencias

### Tips de Performance

**¿Qué proveedor usar según la tarea?**

| Tarea | Proveedor Recomendado | Razón |
|-------|----------------------|-------|
| Clasificación simple | Gemini | Rápido y gratuito |
| Análisis complejo | Anthropic (Claude) | Mejor razonamiento |
| JSON estructurado | OpenAI | Mejor formato |
| Few-shot learning | Anthropic o OpenAI | Ambos excelentes |
| Chain of Thought | Anthropic (Claude) | Razonamiento superior |
| Desarrollo/testing | Gemini | Generoso tier gratuito |

---

## 🎓 Para la Clase

**Duración estimada:** 45 minutos

1. **Teoría (15 min):** Conceptos de prompt engineering
2. **Lab 5 (10 min):** Ejecutar `PromptEngineering.java` 
3. **Lab 6 (20 min):** Desarrollar `ClasificadorSentimiento.java`

**Tarea para casa:**
Crear un clasificador de intención de usuario (PREGUNTA, QUEJA, SUGERENCIA, ELOGIO) usando few-shot learning.
