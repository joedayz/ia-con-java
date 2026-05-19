# Fase 6 - MCP con proveedor y consumidor

Esta demo agrega una fase de MCP con dos apps:

- `fase5-mcp-provider`: expone capacidades MCP con `@McpResource`, `@McpPrompt` y tools MCP.
- `fase5-mcp-consumer`: consume MCP por SSE y usa `ChatClient + ToolCallbackProvider`.

Además, el consumer incluye una demo multimodal para analizar imágenes con Ollama.

## Estructura

- `fase5-mcp/fase5-mcp-provider`
- `fase5-mcp/fase5-mcp-consumer`

## 0) Compilación (opcional)

### Linux / macOS

```bash
mvn -pl fase5-mcp/fase5-mcp-provider,fase5-mcp/fase5-mcp-consumer test -DskipTests=false
```

### Windows PowerShell

```powershell
mvn -pl fase5-mcp/fase5-mcp-provider,fase5-mcp/fase5-mcp-consumer test -DskipTests=false
```

## 1) Levantar proveedor (puerto 8091)

### Linux / macOS

```bash
mvn -pl fase5-mcp/fase5-mcp-provider spring-boot:run
```

### Windows PowerShell

```powershell
mvn -pl fase5-mcp/fase5-mcp-provider spring-boot:run
```

Capacidades expuestas por el provider:

- Recurso `curso://cronograma/{clase}`
- Recurso `curso://modulo/{fase}`
- Prompt `actividad-fase5` con argumentos `tema` y `nivel`
- Tool `consultarCronograma(clase)`
- Tool `consultarModulo(fase)`
- Tool `generarActividadFase5(tema, nivel)`

## 2) Levantar consumidor (puerto 8092)

En otra terminal:

### Linux / macOS

```bash
mvn -pl fase5-mcp/fase5-mcp-consumer spring-boot:run
```

### Windows PowerShell

```powershell
mvn -pl fase5-mcp/fase5-mcp-consumer spring-boot:run
```

## 3) Swagger del consumidor

Con el consumer arriba, abre:

- `http://localhost:8092/swagger-ui.html`
- `http://localhost:8092/v3/api-docs`

## 4) Probar desde HTTP contra el consumidor

### Linux / macOS

```bash
curl -s -X POST http://localhost:8092/api/mcp/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"Que se ve en la clase 5 del curso?"}' | jq .

curl -s http://localhost:8092/api/mcp/demo/cronograma/5 | jq .
curl -s http://localhost:8092/api/mcp/demo/modulo/fase5 | jq .
curl -s -X POST http://localhost:8092/api/mcp/demo/actividad \
  -H "Content-Type: application/json" \
  -d '{"tema":"@McpResource y @McpPrompt","nivel":"intermedio"}' | jq .

curl -s http://localhost:8092/api/multimodal/status | jq .

curl -s -X POST http://localhost:8092/api/multimodal/analyze \
  -F "image=@/ruta/a/tu/imagen.png" \
  -F "prompt=Describe la escena y elementos principales" | jq .
```

### Windows PowerShell

```powershell
$body = @{ message = "Que se ve en la clase 5 del curso?" } | ConvertTo-Json -Compress

Invoke-RestMethod -Uri "http://localhost:8092/api/mcp/chat" `
  -Method Post `
  -ContentType "application/json" `
  -Body $body | ConvertTo-Json -Depth 8

Invoke-RestMethod -Uri "http://localhost:8092/api/mcp/demo/cronograma/5" | ConvertTo-Json -Depth 8
Invoke-RestMethod -Uri "http://localhost:8092/api/mcp/demo/modulo/fase5" | ConvertTo-Json -Depth 8

$body = @{ tema = "@McpResource y @McpPrompt"; nivel = "intermedio" } | ConvertTo-Json -Compress

Invoke-RestMethod -Uri "http://localhost:8092/api/mcp/demo/actividad" `
  -Method Post `
  -ContentType "application/json" `
  -Body $body | ConvertTo-Json -Depth 8

Invoke-RestMethod -Uri "http://localhost:8092/api/multimodal/status" | ConvertTo-Json -Depth 8

$form = @{
  image = Get-Item "C:/ruta/a/tu/imagen.png"
  prompt = "Describe la escena y elementos principales"
}

Invoke-RestMethod -Uri "http://localhost:8092/api/multimodal/analyze" `
  -Method Post `
  -Form $form | ConvertTo-Json -Depth 8
```

## Notas

- Si cambias host/puerto del provider, ajusta `spring.ai.mcp.client.sse.connections.fase5-provider.url` en `fase5-mcp-consumer/src/main/resources/application.yaml`.
- El consumer usa un modelo local de Ollama por defecto (`llama3.2:3b`).
- Para multimodal, usa un modelo de visión como `llava`:

### Linux / macOS

```bash
ollama pull llava
export OLLAMA_CHAT_MODEL=llava
```

### Windows PowerShell

```powershell
ollama pull llava
$env:OLLAMA_CHAT_MODEL="llava"
```

- Asegúrate de que Ollama esté corriendo antes de ejecutar el consumer:

### Linux / macOS

```bash
ollama serve
```

### Windows PowerShell

```powershell
# En el directorio de Ollama o en PATH:
ollama serve
```

