package com.joedayz.fase6.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class OpenAiImageService implements ImageService {

    private static final Logger LOG = LoggerFactory.getLogger(OpenAiImageService.class);

    private final ImageModel imageModel;

    public OpenAiImageService(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @Override
    public String generateImageUrl(String prompt) {
        LOG.info("Generando imagen (URL) para prompt: {}", prompt);
        var output = generate(prompt).getResult().getOutput();
        // gpt-image-1 devuelve base64; construimos data URI para el caller
        if (output.getUrl() != null && !output.getUrl().isBlank()) {
            return output.getUrl();
        }
        return "data:image/png;base64," + output.getB64Json();
    }

    @Override
    public byte[] generateImageBytes(String prompt) {
        LOG.info("Generando imagen (bytes) para prompt: {}", prompt);
        var output = generate(prompt).getResult().getOutput();
        if (output.getB64Json() != null && !output.getB64Json().isBlank()) {
            return Base64.getDecoder().decode(output.getB64Json());
        }
        try {
            return new java.net.URI(output.getUrl()).toURL().openStream().readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Error descargando imagen desde URL: " + e.getMessage(), e);
        }
    }

    private ImageResponse generate(String prompt) {
        var options = ImageOptionsBuilder.builder()
                .width(1024)
                .height(1024)
                .build();
        return imageModel.call(new ImagePrompt(prompt, options));
    }
}
