package com.example.boardgamebuddy;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.model.SimpleApiKey;
import org.springframework.ai.model.openai.autoconfigure.OpenAIAutoConfigurationUtil;
import org.springframework.ai.model.openai.autoconfigure.OpenAiConnectionProperties;
import org.springframework.ai.model.openai.autoconfigure.OpenAiImageProperties;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

/**
 * ImageModel sin {@code response_format} en defaults (gpt-image-1 lo rechaza con HTTP 400).
 * Evita que {@code spring.ai.openai.image.options.response-format} del entorno se fusione en la petición.
 */
@Configuration
public class OpenAiImageModelConfig {

  @Bean
  OpenAiImageModel openAiImageModel(
      OpenAiConnectionProperties commonProperties,
      OpenAiImageProperties imageProperties,
      ObjectProvider<RestClient.Builder> restClientBuilderProvider,
      RetryTemplate retryTemplate,
      ResponseErrorHandler responseErrorHandler,
      ObjectProvider<ObservationRegistry> observationRegistry) {

    var resolved = OpenAIAutoConfigurationUtil.resolveConnectionProperties(
        commonProperties, imageProperties, "image");

    var api = OpenAiImageApi.builder()
        .baseUrl(resolved.baseUrl())
        .apiKey(new SimpleApiKey(resolved.apiKey()))
        .headers(resolved.headers())
        .imagesPath(imageProperties.getImagesPath())
        .restClientBuilder(restClientBuilderProvider.getIfAvailable(RestClient::builder))
        .responseErrorHandler(responseErrorHandler)
        .build();

    String model = imageProperties.getOptions().getModel();
    if (model == null || model.isBlank()) {
      model = "gpt-image-1";
    }

    OpenAiImageOptions options = OpenAiImageOptions.builder()
        .model(model)
        .build();

    return new OpenAiImageModel(api, options, retryTemplate,
        observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP));
  }
}
