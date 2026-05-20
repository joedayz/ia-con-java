package dev.springai.workshop.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Agente de soporte. Step 03: {@link #chatStream} devuelve tokens conforme los genera el modelo
 * (equivalente a {@code Multi<String>} en Quarkus).
 */
@Service
public class CustomerSupportAgent {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public CustomerSupportAgent(ChatModel chatModel, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    public Flux<String> chatStream(String sessionId, String userMessage) {
        return chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .user(userMessage)
                .stream()
                .content();
    }
}
