package com.joedayz.ia.springai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Map;

/**
 * Configuración de memoria para el chatbot.
 * 
 * Lab 7: InMemoryChatMemory (memoria volátil en RAM)
 * 
 * Spring AI actualmente solo soporta InMemoryChatMemory out-of-the-box.
 * JdbcChatMemory está en desarrollo para versiones futuras.
 */
@Configuration
public class ChatConfig {

    @Value("${app.chat.provider:openai}")
    private String provider;

    /**
     * ✅ SOLUCIÓN LAB 7: Bean de ChatMemory con InMemoryChatMemory.
     * 
     * InMemoryChatMemory almacena el historial de conversaciones en RAM.
     * Ventajas:
     * - Sin dependencias de base de datos
     * - Rápido
     * - Simple para desarrollo
     * 
     * Desventajas:
     * - Se pierde al reiniciar la aplicación
     * - No escala para múltiples instancias
     * 
     * NOTA: Para persistencia en base de datos, se puede implementar una
     * solución custom basada en la interfaz ChatMemory (Lab 8 opcional).
     */
    @Bean
    public ChatMemory chatMemory() {
        System.out.println("🔧 Configurando MessageWindowChatMemory + InMemoryChatMemoryRepository");
        System.out.println("   📝 La memoria se perderá al reiniciar la aplicación");
        System.out.println("   💡 Para persistencia, implementar custom ChatMemory con JDBC");
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
    }

    /**
     * Selecciona un unico ChatModel como primario para evitar ambiguedades
     * cuando hay varios starters de Spring AI en el classpath.
     */
    @Bean
    @Primary
    public ChatModel selectedChatModel(Map<String, ChatModel> chatModels) {
        String beanName = switch (provider.trim().toLowerCase()) {
            case "openai" -> "openAiChatModel";
            case "anthropic" -> "anthropicChatModel";
            case "vertex", "gemini", "vertexai" -> "vertexAiGeminiChat";
            default -> throw new IllegalArgumentException(
                    "Proveedor no soportado en app.chat.provider: " + provider
                            + ". Valores validos: openai, anthropic, vertex");
        };

        ChatModel selected = chatModels.get(beanName);
        if (selected == null) {
            throw new IllegalStateException(
                    "No se encontro el bean ChatModel '" + beanName + "'. "
                            + "Verifica dependencias y credenciales del proveedor '" + provider + "'.");
        }

        System.out.println("🤖 Proveedor de IA seleccionado: " + provider + " (bean=" + beanName + ")");
        return selected;
    }
}
