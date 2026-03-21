package com.joedayz.ia.fase1.springboot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response de la API de OpenAI /chat/completions
 */
public record ChatResponse(
    String id,
    String object,
    Long created,
    String model,
    List<Choice> choices,
    Usage usage
) {
    public String getContent() {
        if (choices != null && !choices.isEmpty()) {
            return choices.get(0).message().content();
        }
        return "";
    }
    
    public record Choice(
        Integer index,
        Message message,
        @JsonProperty("finish_reason") String finishReason
    ) {}
    
    public record Usage(
        @JsonProperty("prompt_tokens") Integer promptTokens,
        @JsonProperty("completion_tokens") Integer completionTokens,
        @JsonProperty("total_tokens") Integer totalTokens
    ) {}
}
