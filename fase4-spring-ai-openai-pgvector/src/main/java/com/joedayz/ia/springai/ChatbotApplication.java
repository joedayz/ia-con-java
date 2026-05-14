package com.joedayz.ia.springai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot para chatbot con RAG usando Spring AI + OpenAI + PgVector.
 *
 * Incluye chat con memoria, búsqueda semántica persistente en PostgreSQL,
 * RAG manual, RAG con QuestionAnswerAdvisor y carga de documentación.
 */
@SpringBootApplication
public class ChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);

        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Chatbot Spring AI + OpenAI + PgVector             ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("✅ Aplicación iniciada exitosamente");
        System.out.println();
        System.out.println("📍 Endpoints disponibles:");
        System.out.println("   POST http://localhost:8080/api/chat");
        System.out.println("        → Chat simple con memoria");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/chat/{sessionId}");
        System.out.println("        → Chat multi-sesión");
        System.out.println();
        System.out.println("   DELETE http://localhost:8080/api/chat/{sessionId}");
        System.out.println("        → Limpiar historial de sesión");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/buscar/demo");
        System.out.println("        → Indexar documentos demo en PgVector");
        System.out.println();
        System.out.println("   GET  http://localhost:8080/api/buscar?query=...&topK=4");
        System.out.println("        → Búsqueda semántica en PgVector");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/buscar/pdf");
        System.out.println("        → Indexar contenido de PDF en PgVector");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/rag/simple");
        System.out.println("        → RAG manual con citas");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/rag/advisor");
        System.out.println("        → RAG con QuestionAnswerAdvisor");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/rag/docs/cargar");
        System.out.println("   POST http://localhost:8080/api/rag/docs/preguntar");
        System.out.println();
        System.out.println("📚 Swagger UI:");
        System.out.println("   http://localhost:8080/swagger-ui.html");
        System.out.println();
        System.out.println("🤖 OpenAI:");
        System.out.println("   Variables: OPENAI_API_KEY, OPENAI_CHAT_MODEL, OPENAI_EMBEDDING_MODEL");
        System.out.println("   Chat (defecto): gpt-4o-mini");
        System.out.println("   Embeddings (defecto): text-embedding-3-small (1536 dims)");
        System.out.println();
        System.out.println("🐘 PostgreSQL + PgVector:");
        System.out.println("   URL: jdbc:postgresql://localhost:5433/fase4_openai_ragdb");
        System.out.println("   Vector store persistente con índice HNSW");
        System.out.println();
    }
}
