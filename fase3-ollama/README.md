# Fase 3 Ollama: Chatbots con Memoria

## Descripcion

Este modulo es la version de Fase 3 orientada a **Ollama local**.

Aprenderas a:
- Entender el problema de un chatbot sin memoria
- Implementar memoria conversacional en RAM (buffer memory)
- Persistir sesiones en JSON
- Gestionar multiples sesiones de usuarios

## Requisitos

- Java 21
- Maven
- Ollama instalado: <https://ollama.com>
- Al menos un modelo descargado (ejemplo `llama3.2`)

## Configuracion de Ollama

### Linux/macOS

```bash
# 1) Levantar Ollama
ollama serve

# 2) En otra terminal, descargar modelo de chat
ollama pull llama3.2
```

### Windows (PowerShell)

```powershell
# 1) Levantar Ollama
ollama serve

# 2) En otra terminal, descargar modelo de chat
ollama pull llama3.2
```

## Variables opcionales

Puedes configurar en `.env` o variables de entorno:

- `OLLAMA_BASE_URL` (default: `http://localhost:11434`)
- `OLLAMA_MODEL` (default sugerido: `llama3.2`)

### Linux/macOS

```bash
export OLLAMA_BASE_URL=http://localhost:11434
export OLLAMA_MODEL=llama3.2
```

### Windows (PowerShell)

```powershell
$env:OLLAMA_BASE_URL = "http://localhost:11434"
$env:OLLAMA_MODEL = "llama3.2"
```

## Compilar

### Linux/macOS

```bash
# Desde la raiz del repo
mvn -pl common install -DskipTests
cd fase3-ollama
mvn clean compile
```

### Windows (PowerShell)

```powershell
# Desde la raiz del repo
mvn -pl common install -DskipTests
Set-Location fase3-ollama
mvn clean compile
```

## Ejecutar demos

> Nota: todos los comandos se ejecutan dentro de `fase3-ollama`.

### 1) Chatbot sin memoria

#### Linux/macOS

```bash
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotSinMemoria"
```

#### Windows (PowerShell)

```powershell
mvn exec:java "-Dexec.mainClass=com.joedayz.ia.fase3.ChatbotSinMemoria"
```

### 2) Chatbot con memoria (Lab 7)

#### Linux/macOS

```bash
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoria"
```

#### Windows (PowerShell)

```powershell
mvn exec:java "-Dexec.mainClass=com.joedayz.ia.fase3.ChatbotConMemoria"
```

### 3) Memoria persistente JSON (Lab 8)

#### Linux/macOS

```bash
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ChatbotConMemoriaPersistente"
```

#### Windows (PowerShell)

```powershell
mvn exec:java "-Dexec.mainClass=com.joedayz.ia.fase3.ChatbotConMemoriaPersistente"
```

### 4) Comparacion CON vs SIN memoria

#### Linux/macOS

```bash
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.ComparacionMemoria"
```

#### Windows (PowerShell)

```powershell
mvn exec:java "-Dexec.mainClass=com.joedayz.ia.fase3.ComparacionMemoria"
```

### 5) Gestor multi-sesion (reto)

#### Linux/macOS

```bash
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.GestorMultiSesion"
```

#### Windows (PowerShell)

```powershell
mvn exec:java "-Dexec.mainClass=com.joedayz.ia.fase3.GestorMultiSesion"
```

### 6) Demo rapido del servicio

#### Linux/macOS

```bash
mvn exec:java -Dexec.mainClass="com.joedayz.ia.fase3.DemoServicioIA"
```

#### Windows (PowerShell)

```powershell
mvn exec:java "-Dexec.mainClass=com.joedayz.ia.fase3.DemoServicioIA"
```

## Estructura principal

- `src/main/java/com/joedayz/ia/fase3/ChatbotSinMemoria.java`
- `src/main/java/com/joedayz/ia/fase3/ChatbotConMemoria.java`
- `src/main/java/com/joedayz/ia/fase3/ChatbotConMemoriaPersistente.java`
- `src/main/java/com/joedayz/ia/fase3/ComparacionMemoria.java`
- `src/main/java/com/joedayz/ia/fase3/GestorMultiSesion.java`
- `src/main/java/com/joedayz/ia/fase3/DemoServicioIA.java`
- `src/main/java/com/joedayz/ia/fase3/ollama/ServicioIAOllama.java`

## Solucion de problemas

### Error: Ollama no esta corriendo

### Linux/macOS

```bash
ollama serve
```

### Windows (PowerShell)

```powershell
ollama serve
```

### Error: no hay modelos instalados

### Linux/macOS

```bash
ollama pull llama3.2
```

### Windows (PowerShell)

```powershell
ollama pull llama3.2
```

### Ver modelos instalados

### Linux/macOS

```bash
ollama list
```

### Windows (PowerShell)

```powershell
ollama list
```

## Nota

Este README es intencionalmente **Ollama-only** para `fase3-ollama`.
