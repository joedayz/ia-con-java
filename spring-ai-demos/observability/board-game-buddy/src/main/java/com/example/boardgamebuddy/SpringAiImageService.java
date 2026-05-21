package com.example.boardgamebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;

@Service
public class SpringAiImageService implements ImageService {

  private static final Logger LOG =
      LoggerFactory.getLogger(SpringAiImageService.class);

  private final ImageModel imageModel;
  private final String imageModelName;
  private final RestClient restClient;

  public SpringAiImageService(
      ImageModel imageModel,
      RestClient.Builder restClientBuilder,
      @Value("${spring.ai.openai.image.options.model:gpt-image-1}") String imageModelName) {
    this.imageModel = imageModel;
    this.imageModelName = imageModelName;
    this.restClient = restClientBuilder.build();
  }

  @Override
  public String generateImageForUrl(String instructions) {
    Image image = generateImage(instructions);
    String url = image.getUrl();
    if (url != null && !url.isBlank()) {
      return url;
    }
    String b64 = image.getB64Json();
    if (b64 != null && !b64.isBlank()) {
      return "data:image/png;base64," + b64;
    }
    throw new IllegalStateException("OpenAI image response had no url or b64_json");
  }

  @Override
  public byte[] generateImageForImageBytes(String instructions) {
    return toImageBytes(generateImage(instructions));
  }

  /**
   * Do not set {@code response_format}: rejected by gpt-image-1 and newer OpenAI image models.
   * Spring AI merges runtime + default options; sending response_format causes HTTP 400.
   */
  private Image generateImage(String instructions) {
    LOG.info("Image prompt instructions: {} (model={})", instructions, imageModelName);

    var options = ImageOptionsBuilder.builder()
        .model(imageModelName)
        .width(1024)
        .height(1024)
        .build();

    ImageResponse response = imageModel.call(new ImagePrompt(instructions, options));
    return response.getResult().getOutput();
  }

  private byte[] toImageBytes(Image image) {
    String b64 = image.getB64Json();
    if (b64 != null && !b64.isBlank()) {
      return Base64.getDecoder().decode(b64);
    }
    String url = image.getUrl();
    if (url != null && !url.isBlank()) {
      return restClient.get().uri(url).retrieve().body(byte[].class);
    }
    throw new IllegalStateException("OpenAI image response had no url or b64_json");
  }
}
