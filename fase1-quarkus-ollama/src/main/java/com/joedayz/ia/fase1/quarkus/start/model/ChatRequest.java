package com.joedayz.ia.fase1.quarkus.start.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Request para la API compatible con OpenAI de Ollama.
 */
public record ChatRequest(
    String model,
    List<Message> messages,
    @JsonProperty("max_tokens") Integer maxTokens
) {
}
