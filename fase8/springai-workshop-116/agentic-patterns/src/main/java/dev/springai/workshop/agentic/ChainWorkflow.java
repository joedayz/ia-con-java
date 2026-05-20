package dev.springai.workshop.agentic;

import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Prompt chaining: secuencia de llamadas LLM donde cada paso consume la salida del anterior.
 * Basado en {@code com.example.agentic.ChainWorkflow} de
 * <a href="https://github.com/spring-projects/spring-ai-examples/tree/main/agentic-patterns/chain-workflow">spring-ai-examples</a>.
 *
 * @see <a href="https://docs.spring.io/spring-ai/reference/api/effective-agents.html">Building Effective Agents</a>
 */
public class ChainWorkflow {

    private final ChatClient chatClient;
    private final String[] systemPrompts;

    public ChainWorkflow(ChatClient chatClient) {
        this(chatClient, new String[0]);
    }

    public ChainWorkflow(ChatClient chatClient, String[] systemPrompts) {
        this.chatClient = chatClient;
        this.systemPrompts = systemPrompts;
    }

    /**
     * Ejecuta la cadena de prompts LLM sobre {@code userInput}.
     */
    public String chain(String userInput) {
        String response = userInput;
        for (String prompt : systemPrompts) {
            String input = String.format("{%s}\n {%s}", prompt, response);
            response = chatClient.prompt(input).call().content();
        }
        return response;
    }

    /**
     * Encadena pasos de orquestación (agentes, servicios) de forma secuencial.
     * Equivalente conceptual al patrón Chain aplicado a la lógica del workshop.
     */
    public <T> T chainSteps(T initial, List<UnaryOperator<T>> steps) {
        T state = initial;
        for (UnaryOperator<T> step : steps) {
            state = step.apply(state);
        }
        return state;
    }
}
