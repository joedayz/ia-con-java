package dev.springai.workshop.agent;

import dev.springai.workshop.rag.RagRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Step 06: sin QuestionAnswerAdvisor; la augmentation la hace {@link RagRetriever} antes del prompt.
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
    private final RagRetriever ragRetriever;

    public CustomerSupportAgent(ChatModel chatModel, ChatMemory chatMemory, RagRetriever ragRetriever) {
        this.chatMemory = chatMemory;
        this.ragRetriever = ragRetriever;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_MESSAGE)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public Flux<String> chatStream(String sessionId, String userMessage) {
        String augmentedMessage = ragRetriever.augmentUserMessage(userMessage);

        return chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(sessionId)
                        .build())
                .user(augmentedMessage)
                .stream()
                .content();
    }
}
