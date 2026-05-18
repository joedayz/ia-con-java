package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.tool.CleaningTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * Agente de limpieza (equiv. {@code CleaningAgent} en Quarkus step-02).
 */
@Service
public class CleaningAgent {

    private static final String SYSTEM_PROMPT = """
            You handle intake for the cleaning department of a car rental company.
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
                        Taking into account all provided feedback, determine if the car needs a cleaning.
                        If the feedback indicates the car is dirty, has stains, or any other cleanliness issues,
                        call the provided tool and recommend appropriate cleaning services (exterior wash, interior cleaning, waxing, detailing).
                        Be specific about what services are needed.
                        If no cleaning is needed based on the feedback, respond with "CLEANING_NOT_REQUIRED".

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
