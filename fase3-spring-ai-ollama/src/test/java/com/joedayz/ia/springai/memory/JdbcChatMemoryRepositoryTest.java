package com.joedayz.ia.springai.memory;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcChatMemoryRepositoryTest {

    @Test
    void shouldPersistMessagesAcrossMemoryInstances() {
        DataSource dataSource = createDataSource();
        var transactionManager = new DataSourceTransactionManager(dataSource);

        ChatMemory firstMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new JdbcChatMemoryRepository(dataSource, transactionManager))
                .maxMessages(20)
                .build();

        firstMemory.add("session-a", List.of(
                new UserMessage("Hola, me llamo Carlos"),
                new AssistantMessage("Encantado, Carlos")
        ));

        ChatMemory reloadedMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new JdbcChatMemoryRepository(dataSource, transactionManager))
                .maxMessages(20)
                .build();

        List<Message> messages = reloadedMemory.get("session-a");

        assertEquals(2, messages.size());
        assertEquals("Hola, me llamo Carlos", messages.get(0).getText());
        assertEquals("Encantado, Carlos", messages.get(1).getText());
    }

    @Test
    void shouldDeletePersistedConversation() {
        DataSource dataSource = createDataSource();
        var transactionManager = new DataSourceTransactionManager(dataSource);

        ChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new JdbcChatMemoryRepository(dataSource, transactionManager))
                .maxMessages(20)
                .build();

        memory.add("session-b", List.of(
                new UserMessage("Hola"),
                new AssistantMessage("Hola, ¿en qué puedo ayudarte?")
        ));

        memory.clear("session-b");

        assertTrue(memory.get("session-b").isEmpty());
    }

    @Test
    void shouldRespectMessageWindowWhenPersisting() {
        DataSource dataSource = createDataSource();
        var transactionManager = new DataSourceTransactionManager(dataSource);

        ChatMemory memory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new JdbcChatMemoryRepository(dataSource, transactionManager))
                .maxMessages(3)
                .build();

        memory.add("session-c", new UserMessage("uno"));
        memory.add("session-c", new AssistantMessage("dos"));
        memory.add("session-c", new UserMessage("tres"));
        memory.add("session-c", new AssistantMessage("cuatro"));

        List<Message> messages = memory.get("session-c");

        assertEquals(3, messages.size());
        assertEquals(List.of("dos", "tres", "cuatro"), messages.stream().map(Message::getText).toList());
    }

    private DataSource createDataSource() {
        String dbName = "chat_memory_" + UUID.randomUUID();
        return new DriverManagerDataSource("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1", "sa", "");
    }
}
