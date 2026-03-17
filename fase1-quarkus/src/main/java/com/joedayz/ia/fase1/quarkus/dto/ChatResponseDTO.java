package com.joedayz.ia.fase1.quarkus.dto;

/**
 * Response del endpoint REST /api/chat
 */
public record ChatResponseDTO(
    String response,
    String error
) {
    public static ChatResponseDTO success(String response) {
        return new ChatResponseDTO(response, null);
    }
    
    public static ChatResponseDTO error(String error) {
        return new ChatResponseDTO(null, error);
    }
}
