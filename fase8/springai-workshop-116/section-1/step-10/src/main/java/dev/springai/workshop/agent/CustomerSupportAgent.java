package dev.springai.workshop.agent;

import dev.springai.workshop.guardrail.PromptInjectionGuard;
import dev.springai.workshop.rag.RagRetriever;
import dev.springai.workshop.resilience.ResilientLlmInvoker;
import dev.springai.workshop.tools.BookingTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CustomerSupportAgent {

    private static final String SYSTEM_MESSAGE_TEMPLATE = """
            You are a customer support agent of a car rental company 'Miles of Smiles'.
            You are friendly, polite and concise.
            If the question is unrelated to car rental, you should politely redirect the customer to the right department.

            When calling tools or functions, strictly use JSON objects,
            do not wrap in quotes or use plain strings.

            When a customer asks about weather for a rental location, use the weather MCP tools
            to retrieve the forecast and advise on equipment (for example snow chains).

            Today is %s.
            """;

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final RagRetriever ragRetriever;
    private final BookingTools bookingTools;
    private final ToolCallbackProvider mcpTools;
    private final PromptInjectionGuard promptInjectionGuard;
    private final ResilientLlmInvoker resilientLlmInvoker;

    public CustomerSupportAgent(
            ChatModel chatModel,
            ChatMemory chatMemory,
            RagRetriever ragRetriever,
            BookingTools bookingTools,
            ToolCallbackProvider mcpTools,
            PromptInjectionGuard promptInjectionGuard,
            ResilientLlmInvoker resilientLlmInvoker) {
        this.chatMemory = chatMemory;
        this.ragRetriever = ragRetriever;
        this.bookingTools = bookingTools;
        this.mcpTools = mcpTools;
        this.promptInjectionGuard = promptInjectionGuard;
        this.resilientLlmInvoker = resilientLlmInvoker;
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    public String chat(String sessionId, String userMessage) {
        promptInjectionGuard.validate(userMessage);
        return resilientLlmInvoker.invoke(() -> invokeLlm(sessionId, userMessage));
    }

    private String invokeLlm(String sessionId, String userMessage) {
        String augmentedMessage = ragRetriever.augmentUserMessage(userMessage);

        return chatClient.prompt()
                .system(SYSTEM_MESSAGE_TEMPLATE.formatted(LocalDate.now()))
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, sessionId))
                .tools(bookingTools)
                .toolCallbacks(mcpTools)
                .user(augmentedMessage)
                .call()
                .content();
    }
}
