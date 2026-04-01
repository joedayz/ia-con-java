package com.joedayz.ia.springai.dto;

/**
 * DTO para response de chat.
 * 
 * Ejemplo JSON:
 * {
 *   "response": "Hola, ¿en qué puedo ayudarte?",
 *   "sessionId": "user-123"
 * }
 */
public class ChatResponse {
    private String response;
    private String sessionId;

    public ChatResponse() {
    }

    public ChatResponse(String response) {
        this.response = response;
    }

    public ChatResponse(String response, String sessionId) {
        this.response = response;
        this.sessionId = sessionId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "response='" + response + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
