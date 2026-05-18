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
              "carAssignment": "DISPOSITION|MAINTENANCE|CLEANING|NONE"
            }

            Rules:
            - carAssignment: Check the ACTUAL DispositionAgent decision in supervisorDecision, not just the analysis
            - If supervisorDecision mentions SCRAP/SELL/DONATE (but NOT KEEP) → DISPOSITION
            - Else if maintenanceAnalysis ≠ "MAINTENANCE_NOT_REQUIRED" → MAINTENANCE
            - Else if cleaningAnalysis ≠ "CLEANING_NOT_REQUIRED" → CLEANING
            - Else → NONE
            - IMPORTANT: If DispositionAgent decided KEEP, do NOT assign DISPOSITION - check maintenance/cleaning instead
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
