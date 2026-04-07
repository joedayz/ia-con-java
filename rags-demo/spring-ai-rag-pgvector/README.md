# Spring AI RAG Demo

This project demonstrates a simple Retrieval Augmented Generation (RAG) pipeline using Spring AI, PGVector as the vector store, and OpenAI-compatible models.

[Read the full tutorial here.](https://www.sohamkamani.com/java/spring-ai-rag-application/)

Or watch the tutorial on [YouTube](https://www.youtube.com/watch?v=7TdOwFcLV5s&ab_channel=SohamKamani).

## Components

*   **Spring Boot Application:** The main application that orchestrates the RAG pipeline.
*   **PGVector:** A PostgreSQL extension used as a vector database to store and retrieve document embeddings.
*   **OpenAI Embedding Model:** Used to convert text documents and user queries into numerical vector representations.
*   **OpenAI Chat Model:** Used to generate responses based on the user's query and retrieved relevant information.
*   **DocumentLoader:** A Spring component that reads `docs/BASES+CLOUDF_20260224_175315_332.pdf`, splits it into chunks, and loads them into PGVector on startup.
*   **RagService:** A service that handles the RAG logic:
    *   Takes a user query.
    *   Performs a similarity search in the PGVector store to find relevant documents.
    *   Constructs a prompt for the OpenAI chat model, incorporating the original query and the retrieved document content.
    *   Returns the generated response from the OpenAI chat model.
*   **RagController:** A REST controller that exposes an endpoint (`/ai/rag`) to interact with the `RagService`.
*   **`rag-prompt.st`:** A prompt template used by the `RagService` to guide the AI model's response, ensuring it uses the provided information.

## How to Run

### Prerequisites

1.  **Java 21:** Ensure you have Java 21 installed.
2.  **Gradle:** This project uses Gradle for dependency management and building.
3.  **PostgreSQL with PGVector Extension:**
    *   Install PostgreSQL.
    *   Install the `pgvector` extension. You might need to compile it from source or use a pre-built image (e.g., `pgvector/pgvector`).
    *   Create a database (e.g., `rag_demo`). `CREATE DATABASE rag_demo;`
    *   Enable the `vector` extension in your database (after connecting to your database `\c rag_demo`):
        ```sql
        CREATE EXTENSION IF NOT EXISTS vector;
        CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

        CREATE TABLE IF NOT EXISTS vector_store (
            id uuid DEFAULT uuid_generate_v4() PRIMARY KEY,
            content text,
            embedding vector(768) 
        );

        CREATE INDEX ON vector_store USING HNSW (embedding vector_cosine_ops);
        ```
4.  **API keys file (`/Users/josediaz/.api-keys`):** Create a file with your keys/credentials.

### Configuration

1.  **`application.properties`:**
    *   PostgreSQL is configured directly in `src/main/resources/application.properties`:
        * `spring.datasource.url=jdbc:postgresql://localhost:5432/rag_demo`
        * `spring.datasource.username=postgres`
        * `spring.datasource.password=postgres`
    *   API keys are loaded from `/Users/josediaz/.api-keys`:
        * `OPENAI_API_KEY` for chat model calls and (if needed) embedding auth.
    *   This project is configured for OpenAI official endpoints (no local `127.0.0.1:1234` required).
2.  **Example `/Users/josediaz/.api-keys`:**
    ```bash
    export OPENAI_API_KEY="your_openai_or_compatible_key"
    ```

### Build and Run

Use `just` tasks (from `Justfile`):

1.  **See available tasks:**
    ```bash
    just
    ```
2.  **Build:**
    ```bash
    just build
    ```
3.  **Start/validate PostgreSQL:**
    ```bash
    just db-up
    ```
    `db-up` uses Podman or Docker (`pgvector/pgvector:pg16`) if available (Podman is auto-detected first). If no container engine is available, it tries local PostgreSQL and, on macOS with Homebrew, attempts `brew services start postgresql@16` before validating connectivity.

    You can force the engine explicitly:

    ```bash
    CONTAINER_ENGINE=podman just db-up
    CONTAINER_ENGINE=docker just db-up
    ```

    If your Homebrew service name is different, override it for the command:

    ```bash
    BREW_POSTGRES_SERVICE=postgresql just db-up
    ```
4.  **Initialize extensions/table/index (idempotent):**
    ```bash
    just db-init
    ```
5.  **Run (loads `/Users/josediaz/.api-keys` automatically):**
    ```bash
    just run
    ```
    The `DocumentLoader` will automatically load chunks from `docs/BASES+CLOUDF_20260224_175315_332.pdf` into your PGVector database on startup.

If you already have PostgreSQL running locally (without Docker), `just db-up` will just validate it, then continue with `just db-init` + `just run`.

### Test the RAG Endpoint

Once the application is running, you can access the RAG endpoint:

```bash
just query "Cuales son los temas principales del documento?"
```

Or call the endpoint directly:

```bash
curl -X POST "http://localhost:8080/ai/rag" \
  -H "Content-Type: application/json" \
  -d '{"message":"Cuales son los temas principales del documento?"}'
```

You should receive a response generated by the OpenAI chat model, augmented with information retrieved from your PGVector store.

### Troubleshooting

If you see `Connection refused` for `http://127.0.0.1:1234/v1/embeddings`, your app is still pointing to a local OpenAI-compatible server. Ensure `src/main/resources/application.properties` does not set `spring.ai.openai.base-url` and run again.
