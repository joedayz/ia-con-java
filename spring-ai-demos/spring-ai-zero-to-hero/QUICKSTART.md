# Quickstart Guide - Spring AI Zero to Hero

This guide provides a fast path to get the demos up and running.

If you want a teaching-friendly sequence to run demos one by one with Ollama first, use **[INDICE-DEMOS-OLLAMA.md](./INDICE-DEMOS-OLLAMA.md)**.

## 1. Prerequisites (Mandatory)

Before running any code, you must have these services ready:

### Ollama (Local LLM)
1. **Install Ollama**: Download from [ollama.com](https://ollama.com).
2. **Start Ollama**: Run the application.
3. **Pull Models**: Open your terminal and run:
   ```bash
   ollama pull llama3.2
   ollama pull mxbai-embed-large
   ```

### Java 21+
Verify with: `java -version`

---

## 2. Start External Services (Optional / Advanced)

Required for RAG (PostgreSQL/pgvector) or Observability demos. Requires Docker or Podman.

- **PostgreSQL + pgvector**:
  ```bash
  cd docker/postgres
  ./pg start
  cd ../..
  ```
- **Observability Stack** (Prometheus/Grafana):
  ```bash
  cd docker/observability-stack
  ./ostack start
  cd ../..
  ```

---

## 3. Running the Demos

This is a multi-module project. **Do not run `mvn spring-boot:run` from the root.**

### Step 1: Install Dependencies
Since this is a multi-module project, you must install the common components first. Use the `-U` flag to force Maven to update its snapshot metadata and resolve internal dependencies properly:
```bash
./mvnw clean install -DskipTests -U
```
This ensures that internal modules like `chat`, `rag`, etc., are available to the main application and overrides any cached resolution failures.

### Step 2: Start the Provider Application

```bash
./mvnw spring-boot:run -pl applications/provider-ollama
```
*If port 8080 is busy, use: `-Dspring-boot.run.arguments="--server.port=8081"`*

### Step 3: Try a Demo (New Terminal)
Once the provider is running, you can call the endpoints.

**Basic Chat (01):**
```bash
curl http://localhost:8080/chat/01/joke
```

**RAG with Advisors (02):**
```bash
# 1. Load documents
curl http://localhost:8080/rag/02/load
# 2. Ask question
curl "http://localhost:8080/rag/02/query?topic=What%20is%20the%20main%20topic?"
```

---

## 4. Specialized Demos

These run as independent applications. **Stop the previous application first if they share ports.**

### Model Context Protocol (MCP)
```bash
./mvnw spring-boot:run -pl mcp/03-basic-mcp-client
```

### Agentic Systems (CLI)
```bash
./mvnw spring-boot:run -pl agentic-system/01-inner-monologue/inner-monologue-cli
```

---

## 5. Summary of Common Errors

| Error | Solution |
|-------|----------|
| `ConnectException: ...:11434` | Start Ollama and pull `llama3.2`. |
| `HTTP 400 - context length` | Document too large. (Fixed with `TokenTextSplitter(200,...)`). |
| `Port 8080 was already in use` | Change port with `--server.port=8081`. |
| `Could not find artifact com.example:...` | Run `./mvnw clean install -DskipTests -U` (use `-U` to force update snapshots). |
| `Unable to find a suitable main class` | You are running in the root. Use `-pl`. |

For more details, see [ROADMAP.md](./ROADMAP.md).
