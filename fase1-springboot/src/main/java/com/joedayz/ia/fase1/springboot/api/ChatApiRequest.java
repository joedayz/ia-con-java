package com.joedayz.ia.fase1.springboot.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request para el endpoint /api/chat
 */
public record ChatApiRequest(
    String message,
    @JsonProperty("system_prompt") String systemPrompt
) {
    public ChatApiRequest(String message) {
        this(message, null);
    }
}
