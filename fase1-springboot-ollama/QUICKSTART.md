# ⚡ Quick Start - Fase 1 Spring Boot Ollama

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
curl http://localhost:11434/api/version
```

Si falta algo:
- **Ollama no instalado**: Descarga desde [ollama.ai](https://ollama.ai)
- **Ollama no corriendo**: En otra terminal ejecuta `ollama serve`
- **Sin modelos**: Ejecuta `ollama pull mistral`

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
ollama pull mistral
# o si prefieres otro modelo:
ollama pull neural-chat
ollama list  # Ver modelos instalados
```

### 4. Ejecutar la Aplicación

#### Linux/Mac:
```bash
# Terminal 2: Spring Boot
cd fase1-springboot-ollama
mvn spring-boot:run
```

#### Windows PowerShell:
```powershell
# Terminal 2: Spring Boot
cd fase1-springboot-ollama
mvn spring-boot:run
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

**Health Check (Linux/Mac/Windows):**
```bash
curl http://localhost:8080/api/chat/health
```

**GET Simple (Linux/Mac):**
```bash
curl "http://localhost:8080/api/chat?message=Hola"
```

**GET Simple (Windows PowerShell):**
```powershell
curl "http://localhost:8080/api/chat?message=Hola"
```

**POST con System Prompt (Linux/Mac):**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿2+2?", "system_prompt": "Responde en una línea"}'
```

**POST con System Prompt (Windows PowerShell):**
```powershell
$body = @{
    message = "¿2+2?"
    system_prompt = "Responde en una línea"
} | ConvertTo-Json

curl -X POST http://localhost:8080/api/chat `
  -H "Content-Type: application/json" `
  -Body $body
```

O alternativamente en PowerShell con Invoke-WebRequest:
```powershell
$body = '{"message": "¿2+2?", "system_prompt": "Responde en una línea"}'

Invoke-WebRequest -Uri "http://localhost:8080/api/chat" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

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
cd fase1-springboot-ollama
mvn clean package
```

### Ejecutar JAR (todas plataformas):
```bash
java -jar target/fase1-springboot-ollama-1.0.0.jar
```

### Ver logs en vivo (todas plataformas):
```bash
mvn spring-boot:run -X
```

---

## 📊 Flujo de Ejecución

```
Terminal 1: ollama serve
    ↓ (http://localhost:11434)
    
Terminal 2: mvn spring-boot:run
    ↓ (http://localhost:8080)
    
Terminal 3: bash test-api.sh (o .\test-api.ps1 en Windows)
    ↓
GET/POST → Spring Boot → Ollama → Respuesta JSON
```

---

## ⚠️ Errores Comunes

| Error | Causa | Solución |
|-------|-------|----------|
| `Connection refused: localhost:11434` | Ollama no está ejecutándose | Ejecutar `ollama serve` en otra terminal |
| `Model not found` | No hay modelos instalados | Ejecutar `ollama pull mistral` |
| `Timeout after 2 minutes` | Modelo muy lento | Aumentar `ia.ollama.timeout` en `application.properties` |
| `Port 8080 already in use` | Otro proceso usando puerto | Cambiar puerto: `server.port=8081` |
| `jq command not found` | jq no está instalado (solo bash) | `sudo apt-get install jq` o ignorar errores JSON |

---

## 📝 Archivos Importantes

- `pom.xml` - Dependencias y configuración Maven
- `src/main/resources/application.properties` - Configuración de Ollama
- `src/main/java/.../OllamaService.java` - Lógica principal
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
4. ✅ Modificar `OllamaService.java` según necesidades
5. ✅ Agregar más funcionalidades

---

**¡Listo! Ya estás listo para usar Spring Boot con Ollama! 🎉**

