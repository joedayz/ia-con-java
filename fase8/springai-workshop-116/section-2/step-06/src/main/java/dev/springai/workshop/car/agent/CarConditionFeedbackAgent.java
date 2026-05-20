package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackAnalysisResults;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class CarConditionFeedbackAgent {

    private static final String SYSTEM_PROMPT = """
            Analyze car processing results and output a JSON summary.

            Output format:
            {
              "generalCondition": "concise description (max 200 chars)",
              "carAssignment": "DISPOSITION|MAINTENANCE|CLEANING|NONE",
              "dispositionStatus": "DISPOSITION_APPROVED|DISPOSITION_REJECTED|DISPOSITION_NOT_REQUIRED",
              "dispositionReason": "reason or null"
            }

            Rules:
            - carAssignment: DISPOSE_CAR→DISPOSITION, KEEP_CAR+maintenance→MAINTENANCE, KEEP_CAR+cleaning→CLEANING, KEEP_CAR+none→NONE
            - dispositionStatus: KEEP_CAR in supervisor→DISPOSITION_APPROVED, DISPOSE_CAR→DISPOSITION_REJECTED, else→DISPOSITION_NOT_REQUIRED
            - generalCondition: Summarize the action and reason
            """;

    private final ChatClient chatClient;

    public CarConditionFeedbackAgent(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM_PROMPT).build();
    }

    public CarConditions analyzeForCondition(
            CarInfo carInfo,
            Integer carNumber,
            FeedbackAnalysisResults feedbackAnalysisResults,
            String supervisorDecision) {
        return chatClient.prompt()
                .user(u -> u.text("""
                        Car: {year} {make} {model} (#{carNumber})

                        Supervisor Decision: {supervisorDecision}

                        Feedback Analysis Results:
                        - Disposition: {dispositionAnalysis}
                        - Maintenance: {maintenanceAnalysis}
                        - Cleaning: {cleaningAnalysis}
                        """)
                        .param("year", carInfo.getYear())
                        .param("make", carInfo.getMake())
                        .param("model", carInfo.getModel())
                        .param("carNumber", carNumber)
                        .param("supervisorDecision", supervisorDecision)
                        .param("dispositionAnalysis", feedbackAnalysisResults.dispositionAnalysis())
                        .param("maintenanceAnalysis", feedbackAnalysisResults.maintenanceAnalysis())
                        .param("cleaningAnalysis", feedbackAnalysisResults.cleaningAnalysis()))
                .call()
                .entity(CarConditions.class);
    }
}
