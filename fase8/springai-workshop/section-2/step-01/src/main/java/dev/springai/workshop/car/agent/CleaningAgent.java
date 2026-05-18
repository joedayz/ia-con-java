package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.tool.CleaningTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * Agente que decide qué limpieza solicitar (equivalente a {@code @Agent CleaningAgent} en Quarkus).
 */
@Service
public class CleaningAgent {

    private static final String SYSTEM_PROMPT = """
            You handle intake for the cleaning department of a car rental company.
            It is your job to submit a request to the provided requestCleaning function to take action based on the provided feedback.
            Be specific about what services are needed.
            If no cleaning is needed based on the feedback, respond with "CLEANING_NOT_REQUIRED".
            """;

    private final ChatClient chatClient;

    public CleaningAgent(ChatModel chatModel, CleaningTool cleaningTool) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(cleaningTool)
                .build();
    }

    public String processCleaning(CarInfo carInfo, Integer carNumber, String feedback) {
        return chatClient.prompt()
                .user(u -> u.text("""
                        Car Information:
                        Make: {make}
                        Model: {model}
                        Year: {year}
                        Car Number: {carNumber}

                        Feedback: {feedback}
                        """)
                        .param("make", carInfo.getMake())
                        .param("model", carInfo.getModel())
                        .param("year", carInfo.getYear())
                        .param("carNumber", carNumber)
                        .param("feedback", feedback))
                .call()
                .content();
    }
}
