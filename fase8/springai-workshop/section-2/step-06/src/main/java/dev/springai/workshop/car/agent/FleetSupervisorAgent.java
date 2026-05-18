package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackAnalysisResults;
import dev.springai.workshop.car.tool.SupervisorTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class FleetSupervisorAgent {

    private static final Logger log = LoggerFactory.getLogger(FleetSupervisorAgent.class);

    private static final String SYSTEM_PROMPT = """
            You are a fleet operations supervisor. Use the provided tools to invoke action agents.
            Only call tools appropriate for the situation in the user message.
            For high-value dispositions (over $15,000), use disposition proposal then human approval tools.
            End your final response with KEEP_CAR or DISPOSE_CAR when disposition was evaluated.
            Summarize which agents you invoked and their outcomes.
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
            String feedback,
            FeedbackAnalysisResults feedbackAnalysisResults) {
        log.info("FleetSupervisorAgent orchestrating actions (HITL enabled)...");
        String request = FleetSupervisorRequestBuilder.build(carInfo, carNumber, feedback, feedbackAnalysisResults);
        return supervisorClient.prompt().user(request).call().content();
    }
}
