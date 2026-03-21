package com.joedayz.ia.fase1.springboot.api;

/**
 * Response del endpoint /api/chat
 */
public record ChatApiResponse(
    String response,
    String model,
    Long timestamp
) {
    public static ChatApiResponse of(String response, String model) {
        return new ChatApiResponse(response, model, System.currentTimeMillis());
    }
}
