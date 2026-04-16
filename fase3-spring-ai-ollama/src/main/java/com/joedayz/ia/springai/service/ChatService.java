package com.joedayz.ia.springai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * ✅ SOLUCIÓN COMPLETA: Servicio principal del chatbot con memoria conversacional.
 *
 * Este servicio utiliza Spring AI para:
 * 1. Gestionar conversaciones con memoria (InMemory o Jdbc)
 * 2. Soportar múltiples sesiones simultáneas
 * 3. Integrar conversaciones sobre un modelo local servido por Ollama
 *
 * Conceptos clave:
 * - ChatClient: Cliente fluido para interactuar con el LLM
 * - MessageChatMemoryAdvisor: Gestiona automáticamente la memoria
 * - ChatMemory: Almacena el historial (InMemory o Jdbc)
 */
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    private static final int DEFAULT_WINDOW_SIZE = 20; // Últimos 20 mensajes
    private static final String SPANISH_OUTPUT_PROMPT =
            "Responde siempre en espanol correcto. "
                    + "Usa tildes y signos de apertura (\u00BF, \u00A1) cuando corresponda. "
                    + "Nunca uses secuencias corruptas como \u00C2\u00BF o \u00C2\u00A1.";

    /**
     * ✅ SOLUCIÓN LAB 7: Constructor que inicializa ChatClient con memoria.
     *
     * El ChatClient se configura con:
     * 1. El modelo de chat expuesto por Ollama
     * 2. MessageChatMemoryAdvisor para gestionar la memoria automáticamente
     *
     * @param chatModel modelo de chat inyectado por Spring Boot
     * @param chatMemory memoria de chat (InMemory o Jdbc según perfil)
     */
    public ChatService(ChatModel chatModel, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;

        // Configurar ChatClient con advisor de memoria por defecto
        this.chatClient = ChatClient.builder(chatModel)
                .build();

        System.out.println("✅ ChatService inicializado correctamente");
        System.out.println("   📝 Memoria: " + chatMemory.getClass().getSimpleName());
        System.out.println("   🤖 Modelo: " + chatModel.getClass().getSimpleName());
    }

    /**
     * ✅ SOLUCIÓN LAB 7: Chat simple con sesión por defecto.
     *
     * Usa una única sesión "default" para todos los mensajes.
     * Útil para chatbots simples de un solo usuario.
     *
     * @param mensaje pregunta del usuario
     * @return respuesta del asistente
     */
    public String chat(String mensaje) {
        return chat("default", mensaje);
    }

    /**
     * ✅ SOLUCIÓN LAB 8 / RETO: Chat con sesión específica (multi-usuario).
     *
     * Cada sessionId mantiene su propio historial de conversación independiente.
     * Esto permite tener múltiples usuarios/conversaciones simultáneas.
     *
     * El MessageChatMemoryAdvisor se encarga de:
     * 1. Recuperar el historial previo de chatMemory
     * 2. Agregarlo al contexto antes de enviar al LLM
     * 3. Guardar el nuevo mensaje y respuesta en chatMemory
     *
     * @param sessionId identificador único de la sesión (ej: "user-123", "session-abc")
     * @param mensaje pregunta del usuario
     * @return respuesta del asistente
     */
    public String chat(String sessionId, String mensaje) {
        return chatClient.prompt()
                .system(SPANISH_OUTPUT_PROMPT)
                // En 1.1.x el advisor se construye por conversacion.
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(sessionId)
                        .build())
                .user(mensaje)
                .call()
                .content();
    }

    /**
     * Limpia el historial de una sesión específica.
     * Útil para "empezar de nuevo" o implementar comando /reset.
     *
     * @param sessionId identificador de la sesión a limpiar
     */
    public void clearSession(String sessionId) {
        chatMemory.clear(sessionId);
        System.out.println("🧹 Sesión limpiada: " + sessionId);
    }

    /**
     * Obtiene información sobre el estado actual del servicio.
     * Útil para debugging y monitoring.
     */
    public String getStatus() {
        return String.format(
                "ChatService [memoria=%s, modelo=configurado]",
                chatMemory.getClass().getSimpleName()
        );
    }
}
