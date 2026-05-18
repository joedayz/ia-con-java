package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class CleaningFeedbackAgent {

    private static final String SYSTEM_PROMPT = """
            You are a cleaning analyzer for a car rental company. Your job is to determine if a car needs cleaning based on feedback.
            Analyze the feedback and car information to decide if a cleaning is needed.
            If the feedback mentions dirt, mud, stains, or anything that suggests the car is dirty, recommend a cleaning.
            Be specific about what type of cleaning is needed (exterior, interior, detailing, waxing).
            IMPORTANT: If no interior or exterior car cleaning services are needed, you MUST start your response with the exact keyword "CLEANING_NOT_REQUIRED" followed by a brief reason.
            Do NOT use any other phrasing (e.g. "not required", "no cleaning needed") when cleaning is not needed — always use "CLEANING_NOT_REQUIRED", regardless of the language of the feedback provided.
            Include the reason for your choice but keep your response short.
            """;

    private final ChatClient chatClient;

    public CleaningFeedbackAgent(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM_PROMPT).build();
    }

    public String analyzeForCleaning(CarInfo carInfo, Integer carNumber, String feedback) {
        String previousCondition = carInfo.getCondition() != null ? carInfo.getCondition() : "Unknown";
        return chatClient.prompt()
                .user(u -> u.text("""
                        Car Information:
                        Make: {make}
                        Model: {model}
                        Year: {year}
                        Previous Condition: {previousCondition}

                        Feedback: {feedback}
                        """)
                        .param("make", carInfo.getMake())
                        .param("model", carInfo.getModel())
                        .param("year", carInfo.getYear())
                        .param("previousCondition", previousCondition)
                        .param("feedback", feedback))
                .call()
                .content();
    }
}
