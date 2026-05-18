package dev.springai.workshop.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * Agente de soporte al cliente (equivalente a {@code @RegisterAiService} en Quarkus).
 * Usa {@link ChatClient} con memoria por sesión de WebSocket.
 */
@Service
public class CustomerSupportAgent {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public CustomerSupportAgent(ChatModel chatModel, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    public String chat(String sessionId, String userMessage) {
        return chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(sessionId)
                        .build())
                .user(userMessage)
                .call()
                .content();
    }
}
