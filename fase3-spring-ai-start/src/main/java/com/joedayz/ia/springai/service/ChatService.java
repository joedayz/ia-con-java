package com.joedayz.ia.springai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * Servicio principal del chatbot con memoria conversacional.
 * 
 * Lab 7: Chat simple con memoria
 * Lab 8: Chat multi-sesión con memoria persistente
 */
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    /**
     * TODO LAB 7: Constructor - inicializar ChatClient con memoria.
     * 
     * PISTAS:
     * 1. Guardar chatMemory en campo de instancia
     * 2. Crear ChatClient usando ChatClient.builder(chatModel)
     * 3. Agregar defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
     * 4. Llamar a .build()
     * 
     * @param chatModel modelo de chat inyectado (OpenAI/Anthropic/Gemini)
     * @param chatMemory memoria de chat (InMemory o Jdbc según configuración)
     */
    public ChatService(ChatModel chatModel, ChatMemory chatMemory) {
        // TODO: Implementar inicialización de ChatClient
        this.chatMemory = chatMemory;
        this.chatClient = null; // Cambiar esto
    }

    /**
     * TODO LAB 7: Chat simple con sesión por defecto.
     * 
     * Usa una única sesión "default" para todos los mensajes.
     * 
     * PISTAS:
     * 1. Llamar a chat(sessionId, mensaje) con "default" como sessionId
     * 
     * @param mensaje pregunta del usuario
     * @return respuesta del asistente
     */
    public String chat(String mensaje) {
        // TODO: Implementar usando chat("default", mensaje)
        throw new UnsupportedOperationException("TODO: Implementar método chat simple");
    }

    /**
     * TODO LAB 8 / RETO: Chat con sesión específica (multi-usuario).
     * 
     * Cada sessionId mantiene su propio historial de conversación.
     * Útil para aplicaciones con múltiples usuarios.
     * 
     * PISTAS:
     * 1. Usar chatClient.prompt()
     * 2. Agregar .advisors(new MessageChatMemoryAdvisor(chatMemory, sessionId, 20))
     *    - sessionId: identificador único de la sesión
     *    - 20: número máximo de mensajes a recordar (window size)
     * 3. Agregar .user(mensaje)
     * 4. Llamar a .call().content()
     * 
     * @param sessionId identificador de la sesión (ej: "user-123", "session-abc")
     * @param mensaje pregunta del usuario
     * @return respuesta del asistente
     */
    public String chat(String sessionId, String mensaje) {
        // TODO: Implementar chat con sessionId específica
        throw new UnsupportedOperationException("TODO: Implementar método chat con sessionId");
    }

    /**
     * Limpia el historial de una sesión específica.
     * 
     * @param sessionId identificador de la sesión a limpiar
     */
    public void clearSession(String sessionId) {
        chatMemory.clear(sessionId);
    }

    /**
     * Limpia todas las sesiones (útil para testing).
     */
    public void clearAllSessions() {
        // Nota: InMemoryChatMemory no tiene método clearAll()
        // Se puede extender o implementar según necesidad
    }
}
