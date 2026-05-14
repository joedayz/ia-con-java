package com.joedayz.ia.fase5.mcp.consumer.service;

import java.io.IOException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MultimodalService {

    private final ChatClient chatClient;
    private final String configuredModel;
    private final String defaultPrompt;
    private final String recommendedModel;

    public MultimodalService(
        ChatClient.Builder chatClientBuilder,
        @Value("${spring.ai.ollama.chat.options.model:llama3.2:3b}") String configuredModel,
        @Value("${app.multimodal.default-prompt:Describe en espanol lo que ves en la imagen.}") String defaultPrompt,
        @Value("${app.multimodal.recommended-model:llava}") String recommendedModel
    ) {
        this.chatClient = chatClientBuilder.build();
        this.configuredModel = configuredModel;
        this.defaultPrompt = defaultPrompt;
        this.recommendedModel = recommendedModel;
    }

    public String analyzeImage(MultipartFile image, String prompt) {
        String finalPrompt = (prompt == null || prompt.isBlank()) ? defaultPrompt : prompt;

        try {
            MimeType mimeType = resolveMimeType(image);
            ByteArrayResource resource = asResource(image);

            return chatClient.prompt()
                .user(user -> user
                    .text(finalPrompt)
                    .media(mimeType, resource))
                .call()
                .content();
        } catch (IOException ex) {
            throw new IllegalArgumentException("No se pudo leer la imagen enviada", ex);
        }
    }

    public String getConfiguredModel() {
        return configuredModel;
    }

    public String getRecommendedModel() {
        return recommendedModel;
    }

    private MimeType resolveMimeType(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return MimeTypeUtils.IMAGE_JPEG;
        }
        try {
            return MimeTypeUtils.parseMimeType(contentType);
        } catch (IllegalArgumentException ignored) {
            return MimeTypeUtils.IMAGE_JPEG;
        }
    }

    private ByteArrayResource asResource(MultipartFile image) throws IOException {
        return new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                String originalFilename = image.getOriginalFilename();
                return (originalFilename == null || originalFilename.isBlank()) ? "upload-image" : originalFilename;
            }
        };
    }
}
