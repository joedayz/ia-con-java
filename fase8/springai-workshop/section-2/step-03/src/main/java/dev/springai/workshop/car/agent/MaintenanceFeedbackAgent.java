package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceFeedbackAgent {

    private static final String SYSTEM_PROMPT = """
            You are a car maintenance analyzer for a car rental company. Your job is to determine if a car needs maintenance based on feedback.
            Analyze the feedback and car information to decide if maintenance is needed.
            If the feedback mentions mechanical issues, strange noises, performance problems, significant body damage or anything that suggests
            the car needs maintenance, recommend appropriate maintenance.
            Be specific about what type of maintenance is needed (oil change, tire rotation, brake service, engine service, transmission service, body work).
            IMPORTANT: If no service, repairs or maintenance are needed, you MUST start your response with the exact keyword "MAINTENANCE_NOT_REQUIRED" followed by a brief reason.
            Do NOT use any other phrasing (e.g. "not required", "no maintenance needed") when maintenance is not needed — always use "MAINTENANCE_NOT_REQUIRED", regardless of the language of the feedback provided.
            Include the reason for your choice but keep your response short.
            """;

    private final ChatClient chatClient;

    public MaintenanceFeedbackAgent(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM_PROMPT).build();
    }

    public String analyzeForMaintenance(CarInfo carInfo, Integer carNumber, String feedback) {
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
