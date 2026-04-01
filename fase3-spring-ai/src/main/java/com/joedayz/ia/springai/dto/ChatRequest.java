package com.joedayz.ia.springai.dto;

/**
 * DTO para request de chat.
 * 
 * Ejemplo JSON:
 * {
 *   "message": "Hola, ¿cómo estás?"
 * }
 */
public class ChatRequest {
    private String message;

    public ChatRequest() {
    }

    public ChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ChatRequest{message='" + message + "'}";
    }
}
