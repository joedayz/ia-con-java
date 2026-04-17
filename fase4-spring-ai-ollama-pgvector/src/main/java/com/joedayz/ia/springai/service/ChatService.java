package com.joedayz.ia.springai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * Servicio principal del chatbot con memoria conversacional.
 *
 * Utiliza Spring AI para:
 * 1. Gestionar conversaciones con memoria (InMemory o PostgreSQL)
 * 2. Soportar múltiples sesiones simultáneas
 * 3. Integrar conversaciones sobre un modelo local servido por Ollama
 */
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    private static final int DEFAULT_WINDOW_SIZE = 20;
    private static final String SPANISH_OUTPUT_PROMPT =
            "Responde siempre en espanol correcto. "
                    + "Usa tildes y signos de apertura (\u00BF, \u00A1) cuando corresponda. "
                    + "Nunca uses secuencias corruptas como \u00C2\u00BF o \u00C2\u00A1.";

    public ChatService(ChatModel chatModel, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = ChatClient.builder(chatModel).build();

        System.out.println("✅ ChatService inicializado correctamente");
        System.out.println("   📝 Memoria: " + chatMemory.getClass().getSimpleName());
        System.out.println("   🤖 Modelo: " + chatModel.getClass().getSimpleName());
    }

    public String chat(String mensaje) {
        return chat("default", mensaje);
    }

    public String chat(String sessionId, String mensaje) {
        return chatClient.prompt()
                .system(SPANISH_OUTPUT_PROMPT)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(sessionId)
                        .build())
                .user(mensaje)
                .call()
                .content();
    }

    public void clearSession(String sessionId) {
        chatMemory.clear(sessionId);
        System.out.println("🧹 Sesión limpiada: " + sessionId);
    }

    public String getStatus() {
        return String.format(
                "ChatService [memoria=%s, modelo=configurado, vectorStore=PgVector]",
                chatMemory.getClass().getSimpleName());
    }
}
