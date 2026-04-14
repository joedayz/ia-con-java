package com.joedayz.ia.fase1.quarkus.start.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Response de la API compatible con OpenAI de Ollama.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatResponse(
    String id,
    String model,
    List<Choice> choices,
    Usage usage
) {
    public String getContent() {
        if (choices != null && !choices.isEmpty() && choices.get(0).message() != null) {
            return choices.get(0).message().content();
        }
        return "";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(
        Integer index,
        Message message,
        @JsonProperty("finish_reason") String finishReason
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
        @JsonProperty("prompt_tokens") Integer promptTokens,
        @JsonProperty("completion_tokens") Integer completionTokens,
        @JsonProperty("total_tokens") Integer totalTokens
    ) {}
}
