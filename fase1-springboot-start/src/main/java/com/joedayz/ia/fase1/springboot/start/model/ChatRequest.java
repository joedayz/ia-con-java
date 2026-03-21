package com.joedayz.ia.fase1.springboot.start.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request para las APIs de IA (OpenAI/Anthropic)
 */
public record ChatRequest(
    String model,
    List<Message> messages,
    @JsonProperty("max_tokens") Integer maxTokens,
    Double temperature
) {
    public ChatRequest(String model, List<Message> messages, Integer maxTokens) {
        this(model, messages, maxTokens, null);
    }
    
    public ChatRequest(String model, List<Message> messages) {
        this(model, messages, 500, null);
    }
}
