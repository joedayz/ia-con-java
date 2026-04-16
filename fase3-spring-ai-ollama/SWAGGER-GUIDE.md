# Guía de Swagger UI - Fase 3 Spring AI + Ollama

## Abrir la app

### Linux / macOS
```bash
ollama serve
ollama pull llama3.2
ollama pull mxbai-embed-large
cd fase3-spring-ai-ollama
mvn spring-boot:run
```

### Windows
```powershell
ollama serve
ollama pull llama3.2
ollama pull mxbai-embed-large
cd fase3-spring-ai-ollama
mvn spring-boot:run
```

## Abrir Swagger UI

```text
http://localhost:8080/swagger-ui.html
```

## Secciones principales

### Chat
- `POST /api/chat`
- `POST /api/chat/{sessionId}`
- `DELETE /api/chat/{sessionId}`
- `GET /api/chat/status`

### Búsqueda semántica
- `GET /api/buscar/status`
- `POST /api/buscar/demo`
- `GET /api/buscar`
- `POST /api/buscar/pdf`

### RAG
- `POST /api/rag`
- `POST /api/rag/simple`
- `POST /api/rag/advisor`
- `POST /api/rag/docs/cargar`
- `POST /api/rag/docs/preguntar`

## Flujo recomendado de demo

1. Probar `POST /api/chat`
2. Probar `POST /api/chat/{sessionId}` con dos sesiones distintas
3. Ejecutar `POST /api/buscar/demo`
4. Probar `GET /api/buscar?query=similitud coseno&topK=3`
5. Probar `POST /api/rag/simple`
6. Probar `POST /api/rag/advisor`
7. Cargar docs con `POST /api/rag/docs/cargar`
8. Preguntar con `POST /api/rag/docs/preguntar`

## Ejemplos rápidos

### Chat
```json
{
  "message": "Hola, me llamo Carlos"
}
```

### RAG simple
```json
{
  "query": "¿Qué son los embeddings?",
  "topK": 3
}
```

### Cargar documentación
```json
{
  "path": "./data/docs"
}
```

## Troubleshooting

### No carga Swagger UI
- Verifica que la app esté arriba en `http://localhost:8080/actuator/health`
- Revisa logs de Spring Boot

### Falla búsqueda semántica o RAG
- Verifica que Ollama esté corriendo
- Verifica que exista el modelo de embeddings:

```bash
ollama list
```

Debe aparecer `mxbai-embed-large`.

### Error de conexión a Ollama
Confirma que el servicio responde en:

```text
http://localhost:11434
```
