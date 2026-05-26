package dev.springai.workshop.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Step 05: RAG con {@link QuestionAnswerAdvisor} (augmentation) + memoria + system message.
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
    private final VectorStore vectorStore;
    private final int maxResults;

    public CustomerSupportAgent(
            ChatModel chatModel,
            ChatMemory chatMemory,
            VectorStore vectorStore,
            @Value("${app.rag.max-results:3}") int maxResults) {
        this.chatMemory = chatMemory;
        this.vectorStore = vectorStore;
        this.maxResults = maxResults;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_MESSAGE)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    public Flux<String> chatStream(String sessionId, String userMessage) {
        var ragAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().topK(maxResults).build())
                .build();

        return chatClient.prompt()
                .advisors(
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId(sessionId)
                                .build(),
                        ragAdvisor)
                .user(userMessage)
                .stream()
                .content();
    }
}
