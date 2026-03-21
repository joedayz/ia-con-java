package com.joedayz.ia.fase1.springboot.model;

/**
 * Mensaje individual en el chat (system, user, assistant)
 */
public record Message(
    String role,
    String content
) {
    public static Message system(String content) {
        return new Message("system", content);
    }
    
    public static Message user(String content) {
        return new Message("user", content);
    }
    
    public static Message assistant(String content) {
        return new Message("assistant", content);
    }
}
