package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackAnalysisResults;
import dev.springai.workshop.car.tool.SupervisorTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * Supervisor que orquesta sub-agentes vía tools (equiv. {@code @SupervisorAgent}).
 */
@Service
public class FleetSupervisorAgent {

    private static final Logger log = LoggerFactory.getLogger(FleetSupervisorAgent.class);

    private static final String SYSTEM_PROMPT = """
            You are a fleet operations supervisor. Use the provided tools to invoke action agents.
            Only call tools that are appropriate for the situation described in the user message.
            When invoking DispositionAgent, pass the original customer feedback as the feedback parameter.
            Summarize which agents you invoked and their outcomes in your final response.
            """;

    private final ChatClient supervisorClient;

    public FleetSupervisorAgent(ChatModel chatModel, SupervisorTools supervisorTools) {
        this.supervisorClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(supervisorTools)
                .build();
    }

    public String superviseCarProcessing(
            CarInfo carInfo,
            Integer carNumber,
            FeedbackAnalysisResults feedbackAnalysisResults,
            String customerFeedback) {
        log.info("FleetSupervisorAgent orchestrating actions...");
        String request = FleetSupervisorRequestBuilder.build(carInfo, carNumber, feedbackAnalysisResults);
        return supervisorClient.prompt()
                .user(request + "\n\nOriginal customer feedback for disposition: " + customerFeedback)
                .call()
                .content();
    }
}
