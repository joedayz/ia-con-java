package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarImageInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

/**
 * Agente multimodal: enriquece el feedback con observaciones visuales (equiv. {@code CarImageAnalysisAgent}).
 */
@Service
public class CarImageAnalysisAgent {

    private static final Logger log = LoggerFactory.getLogger(CarImageAnalysisAgent.class);

    private static final String SYSTEM_PROMPT = """
            You are a car image analyst for a car rental company.
            You will receive the current rental feedback for a car being returned.
            If an image of the car is provided, analyze it and rewrite the rental feedback taking count of
            your visual observations about the car's condition (e.g., visible damage, scratches, dents,
            cleanliness issues, tire condition, etc.).
            Avoid appending your visual observations in a separated section of the response, but combine
            the existing rental feedback, if present, with what you can see from the image in a single response.
            If no image is provided, or the image is empty or it doesn't seem related to a car,
            simply return the rental feedback exactly as it is, without any modification.
            Your response must always include the original rental feedback text followed by your observations if any.
            In any cases the returned response MUST be a single sentence.
            """;

    private final ChatClient chatClient;

    public CarImageAnalysisAgent(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).defaultSystem(SYSTEM_PROMPT).build();
    }

    public String analyzeCarImage(String feedback, CarImageInput carImage) {
        String safeFeedback = feedback != null ? feedback : "";

        if (carImage == null || !carImage.hasImage()) {
            return safeFeedback;
        }

        log.info("CarImageAnalysisAgent: analyzing uploaded image ({} bytes)", carImage.data().length);

        MimeType mimeType = MimeTypeUtils.parseMimeType(carImage.mimeType());

        return chatClient.prompt()
                .user(u -> u.text("Feedback: {feedback}")
                        .param("feedback", safeFeedback)
                        .media(mimeType, carImage.resource()))
                .call()
                .content();
    }
}
