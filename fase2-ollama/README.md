# Fase 2 Ollama - Prompt Engineering Local
Proyecto equivalente a `fase2`, pero usando modelos locales de Ollama en lugar de APIs en la nube.

> Importante: ejecuta Maven desde la raiz del repo `ia-con-java` para evitar errores de dependencias del modulo `fase2-ollama`.

## Requisitos
- Java 21
- Maven 3.6+
- Ollama instalado y corriendo
- Al menos un modelo descargado (ejemplo: `mistral`)

Variables opcionales:
- `OLLAMA_BASE_URL` (default `http://localhost:11434`)
- `OLLAMA_MODEL` (default `mistral`)

## Configuracion rapida

### Linux/macOS
```bash
# 1) Levantar Ollama (si no esta activo)
ollama serve

# 2) Instalar un modelo (si no tienes ninguno)
ollama pull mistral

# 3) Verificar instalacion
./fase2-ollama/verificar-ollama.sh
```

### Windows (PowerShell)
```powershell
# 1) Levantar Ollama (si no esta activo)
ollama serve

# 2) Instalar un modelo (si no tienes ninguno)
ollama pull mistral

# 3) Verificar instalacion
.\fase2-ollama\verificar-ollama.ps1
```

## Ejecutar labs
Desde la raiz del repo `ia-con-java`.

### Linux/macOS
```bash
# Lab 5: chatbot interactivo
mvn -U -pl fase2-ollama -am exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ollama.PromptEngineering

# Lab 6: clasificador few-shot
mvn -U -pl fase2-ollama -am exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ollama.ClasificadorSentimiento

# Bonus: chain of thought
mvn -U -pl fase2-ollama -am exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ollama.ChainOfThought

# Bonus: salida estructurada
mvn -U -pl fase2-ollama -am exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ollama.SalidaEstructurada

# Demo comparativa
mvn -U -pl fase2-ollama -am exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ollama.ComparacionZeroShotVsFewShot
```

### Windows (PowerShell)
```powershell
# Lab 5: chatbot interactivo
mvn -U -pl fase2-ollama -am exec:java "-Dexec.mainClass=com.joedayz.ia.fase2.ollama.PromptEngineering"

# Lab 6: clasificador few-shot
mvn -U -pl fase2-ollama -am exec:java "-Dexec.mainClass=com.joedayz.ia.fase2.ollama.ClasificadorSentimiento"

# Bonus: chain of thought
mvn -U -pl fase2-ollama -am exec:java "-Dexec.mainClass=com.joedayz.ia.fase2.ollama.ChainOfThought"

# Bonus: salida estructurada
mvn -U -pl fase2-ollama -am exec:java "-Dexec.mainClass=com.joedayz.ia.fase2.ollama.SalidaEstructurada"

# Demo comparativa
mvn -U -pl fase2-ollama -am exec:java "-Dexec.mainClass=com.joedayz.ia.fase2.ollama.ComparacionZeroShotVsFewShot"
```

Con modelo especifico:

```bash
mvn -pl fase2-ollama exec:java -Dexec.args="--model=llama3.2"
```

```powershell
mvn -pl fase2-ollama exec:java "-Dexec.args=--model=llama3.2"
```

## Scripts

### Linux/macOS
```bash
# menu interactivo
./fase2-ollama/test-fase2-ollama.sh

# ejecutar un lab concreto
./fase2-ollama/test-fase2-ollama.sh 2
```

### Windows (PowerShell)
```powershell
.\fase2-ollama\test-fase2-ollama.ps1
.\fase2-ollama\test-fase2-ollama.ps1 2
```
