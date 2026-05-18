package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * Agente que actualiza la condición del coche según el feedback (equiv. {@code CarConditionFeedbackAgent}).
 */
@Service
public class CarConditionFeedbackAgent {

    private static final String SYSTEM_PROMPT = """
            You are a car condition analyzer for a car rental company. Your job is to determine the current condition of a car based on feedback.
            Analyze all feedback and the previous car condition to provide an updated condition description.
            Always provide a concise condition description, even if there's minimal feedback.
            Do not add any headers or prefixes to your response.
            """;

    private final ChatClient chatClient;

    public CarConditionFeedbackAgent(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM_PROMPT).build();
    }

    public String analyzeForCondition(CarInfo carInfo, Integer carNumber, String feedback) {
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
