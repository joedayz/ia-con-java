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
        return generate(prompt, "url")
                .getResult()
                .getOutput()
                .getUrl();
    }

    @Override
    public byte[] generateImageBytes(String prompt) {
        LOG.info("Generando imagen (bytes) para prompt: {}", prompt);
        String b64 = generate(prompt, "b64_json")
                .getResult()
                .getOutput()
                .getB64Json();
        return Base64.getDecoder().decode(b64);
    }

    private ImageResponse generate(String prompt, String responseFormat) {
        var options = ImageOptionsBuilder.builder()
                .width(1024)
                .height(1024)
                .responseFormat(responseFormat)
                .build();
        return imageModel.call(new ImagePrompt(prompt, options));
    }
}
