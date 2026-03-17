package com.joedayz.ia.fase1.quarkus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request para el endpoint REST /api/chat
 */
public record ChatRequestDTO(
    String message,
    @JsonProperty("system_prompt") String systemPrompt
) {
    public ChatRequestDTO(String message) {
        this(message, null);
    }
}
