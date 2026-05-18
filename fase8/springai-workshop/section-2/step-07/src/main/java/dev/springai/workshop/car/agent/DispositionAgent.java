package dev.springai.workshop.car.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class DispositionAgent {

    private static final String SYSTEM_PROMPT = """
            You are a car disposition specialist for a car rental company.
            Your job is to determine the best disposition action based on the car's value, condition, age, and damage.

            Disposition Options:
            - SCRAP: Car is beyond economical repair or has severe safety concerns
            - SELL: Car has value but is aging out of the fleet or has moderate damage
            - DONATE: Car has minimal value but could serve a charitable purpose
            - KEEP: Car is worth keeping in the fleet

            Decision Criteria:
            - If estimated repair cost > 50%% of car value: Consider SCRAP or SELL
            - If car is over 5 years old with significant damage: SCRAP
            - If car is 3-5 years old in fair condition: SELL
            - If car has low value (<$5,000) but functional: DONATE
            - If car is valuable and damage is minor: KEEP

            Provide your recommendation with a clear explanation of the reasoning.
            Start your response with the decision word: SCRAP, SELL, DONATE, or KEEP.
            """;

    private final ChatClient chatClient;

    public DispositionAgent(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM_PROMPT).build();
    }

    public String processDisposition(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String carCondition,
            String carValue,
            String feedback) {
        return chatClient.prompt()
                .user(u -> u.text("""
                        Determine the disposition for this vehicle:
                        - Make: {carMake}
                        - Model: {carModel}
                        - Year: {carYear}
                        - Car Number: {carNumber}
                        - Current Condition: {carCondition}
                        - Estimated Value: {carValue}
                        - Damage/Feedback: {feedback}

                        Provide your disposition recommendation (SCRAP/SELL/DONATE/KEEP) and explanation.
                        """)
                        .param("carMake", carMake)
                        .param("carModel", carModel)
                        .param("carYear", carYear)
                        .param("carNumber", carNumber)
                        .param("carCondition", carCondition)
                        .param("carValue", carValue)
                        .param("feedback", feedback))
                .call()
                .content();
    }
}
