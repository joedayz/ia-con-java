package com.joedayz.ia.fase1.quarkus.start.model;

/**
 * Representa un mensaje en la conversación con la IA.
 * 
 * En clase construiremos este record paso a paso.
 * 
 * Ejemplo de uso:
 * Message userMsg = new Message("user", "Hola");
 * Message systemMsg = new Message("system", "Eres un asistente");
 */
public record Message(
    // TODO: Agregar campo 'role' de tipo String
    // TODO: Agregar campo 'content' de tipo String
) {
    
    // TODO: Crear método factory para mensajes de usuario
    // public static Message user(String content) {
    //     return new Message("user", content);
    // }
    
    // TODO: Crear método factory para mensajes de sistema
    // public static Message system(String content) {
    //     return new Message("system", content);
    // }
    
    // TODO: Crear método factory para mensajes del asistente
    // public static Message assistant(String content) {
    //     return new Message("assistant", content);
    // }
}
