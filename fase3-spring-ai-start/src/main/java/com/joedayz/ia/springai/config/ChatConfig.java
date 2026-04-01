package com.joedayz.ia.springai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuración de memoria para el chatbot.
 * 
 * Lab 7: InMemoryChatMemory (memoria volátil en RAM)
 * 
 * NOTA IMPORTANTE: Spring AI 1.0.0-M1 actualmente solo soporta InMemoryChatMemory.
 * JdbcChatMemory estará disponible en versiones futuras de Spring AI.
 * 
 * Para Lab 8 (persistencia), se puede implementar una solución custom
 * usando la interfaz ChatMemory y JDBC directamente.
 */
@Configuration
public class ChatConfig {

    /**
     * TODO LAB 7: Bean de ChatMemory con InMemoryChatMemory.
     * 
     * Esta memoria se pierde al reiniciar la aplicación.
     * Útil para desarrollo y testing.
     * 
     * PISTAS:
     * 1. Crear instancia de InMemoryChatMemory
     * 2. Retornarla como bean de tipo ChatMemory
     */
    @Bean
    public ChatMemory chatMemory() {
        // TODO: Implementar InMemoryChatMemory
        throw new UnsupportedOperationException("TODO: Implementar InMemoryChatMemory");
    }
}
