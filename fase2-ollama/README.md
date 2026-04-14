# Fase 2 Ollama - Prompt Engineering Local
Proyecto equivalente a `fase2`, pero usando modelos locales de Ollama en lugar de APIs en la nube.
## Requisitos
- Java 21
- Maven 3.6+
- Ollama instalado y corriendo
- Al menos un modelo descargado (ejemplo: `mistral`)
## Configuracion rapida
```bash
# 1) Levantar Ollama (si no esta activo)
ollama serve
# 2) Instalar un modelo (si no tienes ninguno)
ollama pull mistral
# 3) Verificar instalacion
./fase2-ollama/verificar-ollama.sh
```
Variables opcionales:
- `OLLAMA_BASE_URL` (default `http://localhost:11434`)
- `OLLAMA_MODEL` (default `mistral`)
## Ejecutar labs
Desde la raiz del repo:
```bash
# Lab 5: chatbot interactivo
mvn -pl fase2-ollama exec:java
# Lab 6: clasificador few-shot
mvn -pl fase2-ollama exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ollama.ClasificadorSentimiento
# Bonus: chain of thought
mvn -pl fase2-ollama exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ollama.ChainOfThought
# Bonus: salida estructurada
mvn -pl fase2-ollama exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ollama.SalidaEstructurada
# Demo comparativa
mvn -pl fase2-ollama exec:java -Dexec.mainClass=com.joedayz.ia.fase2.ollama.ComparacionZeroShotVsFewShot
```
Con modelo especifico:
```bash
mvn -pl fase2-ollama exec:java -Dexec.args="--model=llama3.2"
```
## Scripts
```bash
# menu interactivo
./fase2-ollama/test-fase2-ollama.sh
# ejecutar un lab concreto
./fase2-ollama/test-fase2-ollama.sh 2
```
PowerShell:
```powershell
.\fase2-ollama\test-fase2-ollama.ps1
.\fase2-ollama\test-fase2-ollama.ps1 2
```
