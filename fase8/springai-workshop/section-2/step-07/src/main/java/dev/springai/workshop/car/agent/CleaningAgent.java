package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.tool.CleaningTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

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

    public String processCleaning(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String cleaningRequest) {
        return chatClient.prompt()
                .user(u -> u.text("""
                        Taking into account all provided feedback, determine if the car needs a cleaning.
                        If the feedback indicates the car is dirty, has stains, or any other cleanliness issues,
                        call the provided tool and recommend appropriate cleaning services (exterior wash, interior cleaning, waxing, detailing).
                        Be specific about what services are needed.
                        If no specific cleaning request is provided, request a standard exterior wash.

                        Car Information:
                        Make: {carMake}
                        Model: {carModel}
                        Year: {carYear}
                        Car Number: {carNumber}

                        Cleaning Request:
                        {cleaningRequest}
                        """)
                        .param("carMake", carMake)
                        .param("carModel", carModel)
                        .param("carYear", carYear)
                        .param("carNumber", carNumber)
                        .param("cleaningRequest", cleaningRequest))
                .call()
                .content();
    }
}
