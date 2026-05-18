package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackTask;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class FeedbackAnalysisAgent {

    private final ChatModel chatModel;

    public FeedbackAnalysisAgent(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String analyzeFeedback(FeedbackTask task, CarInfo carInfo, Integer carNumber, String feedback) {
        String previousCondition = carInfo.getCondition() != null ? carInfo.getCondition() : "Unknown";
        return ChatClient.builder(chatModel)
                .build()
                .prompt()
                .system(task.systemInstructions())
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
