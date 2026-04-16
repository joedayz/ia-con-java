package com.joedayz.ia.springai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración base del chatbot usando solo Ollama.
 *
 * La memoria conversacional se mantiene en RAM usando una ventana de mensajes.
 * Es ideal para demos y laboratorios locales.
 */
@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory() {
        System.out.println("🔧 Configurando MessageWindowChatMemory + InMemoryChatMemoryRepository");
        System.out.println("   📝 La memoria se perderá al reiniciar la aplicación");
        System.out.println("   🤖 Proveedor configurado: Ollama");
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
    }
}
