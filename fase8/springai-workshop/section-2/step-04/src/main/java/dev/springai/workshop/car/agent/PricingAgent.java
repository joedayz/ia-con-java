package dev.springai.workshop.car.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PricingAgent {

    private static final String SYSTEM_PROMPT = """
            You are a vehicle pricing specialist with expertise in market valuations.

            Use these pricing guidelines:

            Brand Base Values (new current-year models):
            - Luxury brands (Mercedes-Benz, BMW, Audi): $50,000-$70,000
            - Premium trucks (Ford F-150): $45,000-$60,000
            - Mainstream brands (Toyota, Honda, Chevrolet): $28,000-$42,000
            - Economy brands (Nissan): $22,000-$35,000

            Depreciation (calculate age as: current year - vehicle year):
            - Age 1 year (nearly new): -12%% from base value
            - Age 2 years: -15%% additional (27%% total depreciation)
            - Age 3 years: -12%% additional (39%% total depreciation)
            - Age 4 years: -10%% additional (49%% total depreciation)
            - Age 5+ years: -8%% per additional year

            Condition Adjustments (apply after depreciation):
            - Excellent/Like new: +5%% to depreciated value
            - Good/Recently serviced: No adjustment
            - Fair/Minor issues: -10%% from depreciated value
            - Poor/Needs work: -20%% from depreciated value

            Provide:
            1. Estimated market value (single dollar amount with comma separator)
            2. Brief justification (2-3 sentences explaining age, condition, and brand factors)

            Format your response as:
            Estimated Value: $XX,XXX
            Justification: [Your reasoning including vehicle age]
            """;

    private final ChatClient chatClient;

    public PricingAgent(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM_PROMPT).build();
    }

    public String estimateValue(String carMake, String carModel, Integer carYear, String carCondition) {
        return chatClient.prompt()
                .user(u -> u.text("""
                        Today is {currentDate}.

                        Estimate the current market value of this vehicle:
                        - Make: {carMake}
                        - Model: {carModel}
                        - Year: {carYear}
                        - Condition: {carCondition}
                        """)
                        .param("currentDate", LocalDate.now().toString())
                        .param("carMake", carMake)
                        .param("carModel", carModel)
                        .param("carYear", carYear)
                        .param("carCondition", carCondition))
                .call()
                .content();
    }
}
