package dev.springai.workshop.car.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceAgent {

    private static final String SYSTEM_PROMPT = """
            You handle intake for the car maintenance department of a car rental company.
            Based on the maintenance request, determine what specific services are needed and provide a detailed maintenance plan.
            Be specific about what services are needed based on the maintenance request.

            Available maintenance services include:
            - Oil change
            - Tire rotation
            - Brake service
            - Engine service
            - Transmission service
            - Body work (dent repair, paint, collision repair)

            For body damage like dents, scratches, or collision damage, include body work in your plan.

            Provide your response as a structured maintenance plan listing the specific services needed.
            """;

    private final ChatClient chatClient;

    public MaintenanceAgent(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM_PROMPT).build();
    }

    public String processMaintenance(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String maintenanceRequest) {
        return chatClient.prompt()
                .user(u -> u.text("""
                        Car Information:
                        Make: {carMake}
                        Model: {carModel}
                        Year: {carYear}
                        Car Number: {carNumber}

                        Maintenance Request:
                        {maintenanceRequest}
                        """)
                        .param("carMake", carMake)
                        .param("carModel", carModel)
                        .param("carYear", carYear)
                        .param("carNumber", carNumber)
                        .param("maintenanceRequest", maintenanceRequest))
                .call()
                .content();
    }
}
