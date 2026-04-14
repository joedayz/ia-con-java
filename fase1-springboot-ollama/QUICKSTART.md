# ⚡ Quick Start - Fase 1 Spring Boot Ollama

## 🚀 En 5 Minutos

### 1. Verificar Requisitos (1 min)

```bash
# En una terminal, verificar Ollama
bash verificar-ollama.sh
```

Si falta algo:
- **Ollama no instalado**: Descarga desde [ollama.ai](https://ollama.ai)
- **Ollama no corriendo**: En otra terminal ejecuta `ollama serve`
- **Sin modelos**: Ejecuta `ollama pull mistral`

### 2. Iniciar Ollama (si no está corriendo)

```bash
# Terminal 1: Ollama
ollama serve
```

### 3. Ejecutar la Aplicación

```bash
# Terminal 2: Spring Boot
mvn spring-boot:run
```

La aplicación estará en: **http://localhost:8080**

### 4. Probar la API

#### En Linux/Mac:
```bash
bash test-api.sh
```

#### En PowerShell (Windows):
```powershell
.\test-api.ps1
```

#### Manual con curl:
```bash
# Health check
curl http://localhost:8080/api/chat/health

# GET simple
curl "http://localhost:8080/api/chat?message=Hola"

# POST con system prompt
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "¿2+2?", "system_prompt": "Responde en una línea"}'
```

## 🎯 Comandos Útiles

```bash
# Listar modelos instalados
ollama list

# Instalar un modelo
ollama pull neural-chat

# Ver logs en vivo
mvn spring-boot:run -X

# Empaquetar JAR
mvn clean package

# Ejecutar JAR
java -jar target/fase1-springboot-ollama-1.0.0.jar
```

## ⚠️ Errores Comunes

| Error | Solución |
|-------|----------|
| `Connection refused: localhost:11434` | Ejecutar `ollama serve` |
| `No models found` | Ejecutar `ollama pull mistral` |
| `Timeout after 2 minutes` | Aumentar timeout en `application.properties` |
| `Port 8080 already in use` | Cambiar puerto: `--server.port=8081` |

## 📊 Flujo de Ejecución

```
Terminal 1: ollama serve (http://localhost:11434)
    ↓
Terminal 2: mvn spring-boot:run (http://localhost:8080)
    ↓
Terminal 3: curl o test-api.sh
    ↓
GET/POST → Spring Boot → Ollama → Respuesta
```

---

**¡Listo! Ya estás listo para usar Spring Boot con Ollama! 🎉**

