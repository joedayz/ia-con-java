package dev.springai.workshop.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Agente de soporte. Step 04: system message (equivalente a {@code @SystemMessage} en Quarkus).
 */
@Service
public class CustomerSupportAgent {

    private static final String SYSTEM_MESSAGE = """
            You are a customer support agent of a car rental company 'Miles of Smiles'.
            You are friendly, polite and concise.
            If the question is unrelated to car rental, you should politely redirect the customer to the right department.
            """;

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public CustomerSupportAgent(ChatModel chatModel, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_MESSAGE)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public Flux<String> chatStream(String sessionId, String userMessage) {
        return chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(sessionId)
                        .build())
                .user(userMessage)
                .stream()
                .content();
    }
}
