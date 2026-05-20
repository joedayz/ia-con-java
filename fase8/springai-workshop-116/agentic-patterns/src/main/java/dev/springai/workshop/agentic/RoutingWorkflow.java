package dev.springai.workshop.agentic;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Clasifica la entrada y la envía al prompt especializado correspondiente.
 * Basado en {@code com.example.agentic.RoutingWorkflow} de spring-ai-examples.
 */
public class RoutingWorkflow {

    private final ChatClient chatClient;

    public RoutingWorkflow(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String route(String input, Map<String, String> routes) {
        Assert.notNull(input, "Input text cannot be null");
        Assert.notEmpty(routes, "Routes map cannot be null or empty");

        String routeKey = selectRoute(input, routes.keySet());
        String selectedPrompt = routes.get(routeKey);
        if (selectedPrompt == null) {
            throw new IllegalArgumentException("Selected route '" + routeKey + "' not found in routes map");
        }
        return chatClient.prompt(selectedPrompt + "\nInput: " + input).call().content();
    }

    /**
     * Solo clasificación (sin segunda llamada LLM). Útil para enrutar a agentes Java del workshop.
     */
    public String selectRoute(String input, Iterable<String> availableRoutes) {
        Assert.notNull(input, "Input text cannot be null");

        String selectorPrompt = String.format("""
                Analyze the input and select the most appropriate option from: %s
                First explain your reasoning, then provide your selection in this JSON format:

                \\{
                  "reasoning": "Brief explanation of why this option fits best.",
                  "selection": "The chosen option name exactly as listed"
                \\}

                Input: %s""", availableRoutes, input);

        RoutingResponse routingResponse = chatClient.prompt(selectorPrompt).call().entity(RoutingResponse.class);
        return routingResponse.selection();
    }
}
