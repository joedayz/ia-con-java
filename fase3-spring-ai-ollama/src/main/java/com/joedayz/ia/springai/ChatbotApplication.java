package com.joedayz.ia.springai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot para chatbot con memoria usando Spring AI + Ollama.
 *
 * Incluye chat con memoria, búsqueda semántica, RAG manual,
 * RAG con advisor y carga de documentación Markdown/PDF.
 */
@SpringBootApplication
public class ChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║   Chatbot Spring AI + Ollama                      ║");
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
        System.out.println("        → Indexar documentos demo");
        System.out.println();
        System.out.println("   GET  http://localhost:8080/api/buscar?query=...&topK=4");
        System.out.println("        → Búsqueda semántica");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/buscar/pdf");
        System.out.println("        → Indexar contenido de PDF");
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
        System.out.println("🦙 Ollama:");
        System.out.println("   Base URL: http://localhost:11434");
        System.out.println("   Chat model: ${OLLAMA_CHAT_MODEL:llama3.2}");
        System.out.println("   Embedding model: ${OLLAMA_EMBEDDING_MODEL:mxbai-embed-large}");
        System.out.println();
    }
}
