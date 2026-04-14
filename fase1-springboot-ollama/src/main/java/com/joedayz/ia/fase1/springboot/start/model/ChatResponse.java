package com.joedayz.ia.fase1.springboot.start.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response de las APIs de IA (OpenAI/Anthropic)
 */
public record ChatResponse(
    String id,
    String object,
    Long created,
    String model,
    List<Choice> choices,
    @JsonProperty("content") List<Content> contentList,
    Usage usage
) {
    public String getContent() {
        // OpenAI format
        if (choices != null && !choices.isEmpty() && choices.get(0).message() != null) {
            return choices.get(0).message().content();
        }
        // Anthropic format
        if (contentList != null && !contentList.isEmpty()) {
            return contentList.get(0).text();
        }
        return "";
    }
    
    public record Choice(
        Integer index,
        Message message,
        @JsonProperty("finish_reason") String finishReason
    ) {}
    
    public record Content(
        String type,
        String text
    ) {}
    
    public record Usage(
        @JsonProperty("prompt_tokens") Integer promptTokens,
        @JsonProperty("completion_tokens") Integer completionTokens,
        @JsonProperty("total_tokens") Integer totalTokens
    ) {}
}
