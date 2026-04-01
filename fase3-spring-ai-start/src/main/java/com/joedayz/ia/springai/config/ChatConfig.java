package com.joedayz.ia.springai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
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
 * NOTA IMPORTANTE: Spring AI 1.0.0-M1 actualmente solo soporta InMemoryChatMemory.
 * JdbcChatMemory estará disponible en versiones futuras de Spring AI.
 * 
 * Para Lab 8 (persistencia), se puede implementar una solución custom
 * usando la interfaz ChatMemory y JDBC directamente.
 */
@Configuration
public class ChatConfig {

    @Value("${app.chat.provider:openai}")
    private String provider;

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
        // Mostrar todos los beans disponibles para debug
        System.out.println("🔍 ChatModels disponibles en el contexto:");
        chatModels.forEach((name, model) -> 
            System.out.println("   - " + name + " (" + model.getClass().getSimpleName() + ")")
        );

        // Intentar encontrar el bean por nombre conocido
        String[] possibleBeanNames = switch (provider.trim().toLowerCase()) {
            case "openai" -> new String[]{"openAiChatModel", "chatModel"};
            case "anthropic" -> new String[]{"anthropicChatModel", "chatModel"};
            case "vertex", "gemini", "vertexai" -> new String[]{"vertexAiGeminiChatModel", "vertexAiGeminiChat", "chatModel"};
            default -> throw new IllegalArgumentException(
                    "Proveedor no soportado en app.chat.provider: " + provider
                            + ". Valores validos: openai, anthropic, vertex");
        };

        // Intentar encontrar un bean que coincida
        for (String beanName : possibleBeanNames) {
            ChatModel selected = chatModels.get(beanName);
            if (selected != null) {
                System.out.println("✅ Proveedor de IA seleccionado: " + provider + " (bean=" + beanName + ")");
                return selected;
            }
        }

        // Si no encontró ninguno, usar el primero disponible
        if (!chatModels.isEmpty()) {
            Map.Entry<String, ChatModel> first = chatModels.entrySet().iterator().next();
            System.out.println("⚠️  No se encontró bean específico para '" + provider + "'");
            System.out.println("   Usando primer ChatModel disponible: " + first.getKey());
            return first.getValue();
        }

        // Si no hay ningún bean, lanzar error
        throw new IllegalStateException(
                "No se encontró ningún bean ChatModel. "
                        + "Verifica dependencias y credenciales del proveedor '" + provider + "'. "
                        + "Beans buscados: " + String.join(", ", possibleBeanNames));
    }
}
