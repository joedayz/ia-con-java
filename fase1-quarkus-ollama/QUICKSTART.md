# ⚡ Quick Start - Fase 1 Quarkus Ollama

## 🚀 En 5 Minutos

### 1. Verificar Requisitos (1 min)

#### Linux/Mac:
```bash
bash verificar-ollama.sh
```

#### Windows PowerShell:
```powershell
# Verificar que Ollama está instalado
ollama --version

# Verificar que está ejecutándose
curl.exe http://localhost:11434/api/version
```

Si falta algo:
- **Ollama no instalado**: Descarga desde [ollama.ai](https://ollama.ai)
- **Ollama no corriendo**: En otra terminal ejecuta `ollama serve`
- **Sin modelos**: Ejecuta `ollama pull llama3.2`

### 2. Iniciar Ollama (si no está corriendo)

#### Linux/Mac:
```bash
# Terminal 1: Ollama
ollama serve
```

#### Windows PowerShell:
```powershell
# Terminal 1: Ollama
ollama serve
```

### 3. Instalar Modelo (si es necesario)

#### Linux/Mac/Windows:
```bash
ollama pull llama3.2
# o si prefieres otro modelo:
ollama pull neural-chat
ollama list  # Ver modelos instalados
```

### 4. Ejecutar la Aplicación

#### Linux/Mac:
```bash
# Terminal 2: Quarkus
cd fase1-quarkus-ollama
mvn quarkus:dev
```

#### Windows PowerShell:
```powershell
# Terminal 2: Quarkus
cd fase1-quarkus-ollama
mvn quarkus:dev
```

La aplicación estará en: **http://localhost:8080**

### 5. Probar la API

#### Linux/Mac - Usar script automático:
```bash
# Terminal 3: Pruebas
bash test-api.sh
```

#### Windows PowerShell - Usar script automático:
```powershell
# Terminal 3: Pruebas
.\test-api.ps1
```

#### Manual con curl

**Health Check (Linux/Mac):**
```bash
curl http://localhost:8080/api/chat/health
```

**Health Check (Windows PowerShell):**
```powershell
curl.exe http://localhost:8080/api/chat/health
```

**GET Simple (Linux/Mac):**
```bash
curl "http://localhost:8080/api/chat?message=Hola"
```

**GET Simple (Windows PowerShell):**
```powershell
curl.exe "http://localhost:8080/api/chat?message=Hola"
```

**POST con System Prompt (Linux/Mac):**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿2+2?", "system_prompt": "Responde en una línea"}'
```

**POST con System Prompt (Windows PowerShell):**

**Opción 1 - Con curl.exe y archivo temporal (RECOMENDADO):**
```powershell
$json = '{"message": "¿2+2?", "system_prompt": "Responde en una línea"}'
[System.IO.File]::WriteAllText("$PWD\body.json", $json, [System.Text.Encoding]::UTF8)
curl.exe -X POST "http://localhost:8080/api/chat" -H "Content-Type: application/json" -d "@body.json"
```

**Opción 2 - Con Invoke-WebRequest y bytes UTF-8:**
```powershell
$bytes = [System.Text.Encoding]::UTF8.GetBytes('{"message": "¿2+2?", "system_prompt": "Responde en una línea"}')
Invoke-WebRequest -Uri "http://localhost:8080/api/chat" -Method POST -ContentType "application/json; charset=utf-8" -Body $bytes | Select-Object -ExpandProperty Content
```

**Nota:** En PowerShell, `curl` es un alias de `Invoke-WebRequest` y no acepta flags Unix como `-H` o `-d`. Usa siempre `curl.exe` para invocar el binario real.

---

## 🎯 Comandos Útiles

### Ver modelos instalados (todas plataformas):
```bash
ollama list
```

### Instalar un nuevo modelo (todas plataformas):
```bash
ollama pull neural-chat
ollama pull llama2
```

### Compilar JAR (todas plataformas):
```bash
cd fase1-quarkus-ollama
mvn clean package
```

### Ejecutar JAR (todas plataformas):
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

### Ver logs en vivo (todas plataformas):
```bash
mvn quarkus:dev
```

---

## 📊 Flujo de Ejecución

```
Terminal 1: ollama serve
    ↓ (http://localhost:11434)
    
Terminal 2: mvn quarkus:dev
    ↓ (http://localhost:8080)
    
Terminal 3: bash test-api.sh (o .\test-api.ps1 en Windows)
    ↓
GET/POST → Quarkus → Ollama → Respuesta JSON
```

---

## ⚠️ Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `Connection refused: localhost:11434` | Ollama no está ejecutándose | Ejecutar `ollama serve` en otra terminal |
| `Model not found` | No hay modelos instalados | Ejecutar `ollama pull llama3.2` |
| `Timeout after 2 minutes` | Modelo muy lento | Aumentar `ia.ollama.timeout` en `application.properties` |
| `Port 8080 already in use` | Otro proceso usando puerto | Cambiar puerto: `quarkus.http.port=8081` |

---

## 📝 Archivos Importantes

- `pom.xml` - Dependencias y configuración Maven
- `src/main/resources/application.properties` - Configuración de Ollama
- `src/main/java/.../service/IAService.java` - Lógica principal
- `src/main/java/.../resource/ChatResource.java` - Endpoints REST
- `test-api.sh` - Script de pruebas para bash
- `test-api.ps1` - Script de pruebas para PowerShell

---

## 🌐 Plataformas Soportadas

✅ **Linux** (Ubuntu, Debian, CentOS, etc.)
✅ **macOS** (Intel y Apple Silicon)
✅ **Windows** (PowerShell)

---

## 🎓 Próximos Pasos

1. ✅ Ejecutar el proyecto
2. ✅ Probar los endpoints
3. ✅ Leer `README.md` para más detalles
4. ✅ Modificar `IAService.java` según necesidades
5. ✅ Agregar más funcionalidades

---

**¡Listo! Ya estás listo para usar Quarkus con Ollama! 🎉**
