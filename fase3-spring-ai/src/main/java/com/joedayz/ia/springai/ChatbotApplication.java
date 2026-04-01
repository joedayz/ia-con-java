package com.joedayz.ia.springai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot para chatbot con memoria usando Spring AI.
 * 
 * SOLUCIÓN COMPLETA:
 * - Lab 7: Memoria en RAM (InMemoryChatMemory)
 * - Lab 8: Memoria persistente (JdbcChatMemory con H2)
 * - Reto: API REST multi-sesión (/chat/{sessionId})
 * 
 * Soporta múltiples proveedores:
 * - OpenAI (GPT-3.5/GPT-4)
 * - Anthropic (Claude)
 * - Google Vertex AI (Gemini)
 */
@SpringBootApplication
public class ChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║  Chatbot Spring AI - Solución Completa            ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("✅ Aplicación iniciada exitosamente");
        System.out.println();
        System.out.println("📍 Endpoints disponibles:");
        System.out.println("   POST http://localhost:8080/api/chat");
        System.out.println("        → Chat simple (sesión única)");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/chat/{sessionId}");
        System.out.println("        → Chat multi-sesión (RETO)");
        System.out.println();
        System.out.println("   DELETE http://localhost:8080/api/chat/{sessionId}");
        System.out.println("        → Limpiar historial de sesión");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/buscar/demo");
        System.out.println("        → Cargar documentos teóricos en SimpleVectorStore");
        System.out.println();
        System.out.println("   GET  http://localhost:8080/api/buscar?query=...&topK=4");
        System.out.println("        → Buscar documentos similares (Lab 10)");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/buscar/pdf");
        System.out.println("        → Cargar PDF con TikaDocumentReader (Reto)");
        System.out.println();
        System.out.println("   POST http://localhost:8080/api/rag");
        System.out.println("        → RAG básico: retrieval + prompt + generación con citas");
        System.out.println();
        System.out.println("🗄️  H2 Console:");
        System.out.println("   http://localhost:8080/h2-console");
        System.out.println("   JDBC URL: jdbc:h2:file:./data/chatbot-memory");
        System.out.println("   User: sa | Password: (vacío)");
        System.out.println();
        System.out.println("💡 Tip: Usa ./test-api.sh para probar todos los endpoints");
        System.out.println();
    }
}
