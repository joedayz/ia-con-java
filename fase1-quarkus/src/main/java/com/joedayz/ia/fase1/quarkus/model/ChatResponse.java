package com.joedayz.ia.fase1.quarkus.model;

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
        String finishReason
    ) {}
    
    public record Usage(
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens
    ) {}
}
