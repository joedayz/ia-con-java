package com.joedayz.ia.springai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.joedayz.ia.springai.memory.PostgresChatMemoryRepository;

import javax.sql.DataSource;

import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configuración del chatbot usando OpenAI + PostgreSQL.
 *
 * Permite alternar entre memoria en RAM y memoria persistente en PostgreSQL.
 */
@Configuration
public class ChatConfig {

    @Value("${app.chat.memory.repository:jdbc}")
    private String repositoryType;

    @Value("${app.chat.memory.max-messages:20}")
    private int maxMessages;

    @Bean
    public ChatMemory chatMemory(DataSource dataSource, PlatformTransactionManager transactionManager) {
        ChatMemoryRepository repository = switch (repositoryType.trim().toLowerCase()) {
            case "jdbc", "persistent", "persistente", "postgres", "postgresql" ->
                    new PostgresChatMemoryRepository(dataSource, transactionManager);
            case "in-memory", "memory", "ram" -> new InMemoryChatMemoryRepository();
            default -> throw new IllegalArgumentException(
                    "Valor inválido para app.chat.memory.repository: " + repositoryType
                            + ". Usa 'jdbc' o 'in-memory'.");
        };

        System.out.println("🔧 Configurando MessageWindowChatMemory");
        System.out.println("   🧠 Repositorio: " + repository.getClass().getSimpleName());
        System.out.println("   🪟 Ventana máxima: " + maxMessages + " mensajes");
        if (repository instanceof InMemoryChatMemoryRepository) {
            System.out.println("   📝 La memoria se perderá al reiniciar la aplicación");
        } else {
            System.out.println("   💾 La memoria persistirá en PostgreSQL");
        }
        System.out.println("   🤖 Proveedor configurado: OpenAI + PgVector");

        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(maxMessages)
                .build();
    }
}
