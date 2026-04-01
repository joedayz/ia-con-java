package com.joedayz.ia.springai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Spring Boot para chatbot con memoria usando Spring AI.
 * 
 * Labs incluidos:
 * - Lab 7: Memoria en RAM (ChatService con InMemoryChatMemory)
 * - Lab 8: Memoria persistente (JdbcChatMemory con H2)
 * - Lab 9: Búsqueda semántica (SimpleVectorStore)
 * - Lab 10: Endpoint /api/buscar
 * - Reto: API REST multi-sesión (/chat/{sessionId})
 * - Reto extra: Carga de PDF con TikaDocumentReader
 */
@SpringBootApplication
public class ChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
        
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║  Chatbot Spring AI - Labs 7, 8, 9 y 10            ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("✅ Aplicación iniciada");
        System.out.println();
        System.out.println("📍 Endpoints disponibles:");
        System.out.println("   POST http://localhost:8080/api/chat");
        System.out.println("   POST http://localhost:8080/api/chat/{sessionId}");
        System.out.println("   POST http://localhost:8080/api/buscar/demo");
        System.out.println("   GET  http://localhost:8080/api/buscar?query=...&topK=4");
        System.out.println("   POST http://localhost:8080/api/buscar/pdf");
        System.out.println("   POST http://localhost:8080/api/rag");
        System.out.println();
        System.out.println("🗄️  H2 Console:");
        System.out.println("   http://localhost:8080/h2-console");
        System.out.println("   JDBC URL: jdbc:h2:file:./data/chatbot-memory");
        System.out.println();
    }
}
