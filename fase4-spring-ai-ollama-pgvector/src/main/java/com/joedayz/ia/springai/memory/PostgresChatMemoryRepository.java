package com.joedayz.ia.springai.memory;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * Repositorio JDBC para persistir el historial conversacional en PostgreSQL.
 *
 * Compatible con PostgreSQL (usa TEXT en vez de CLOB).
 */
public class PostgresChatMemoryRepository implements ChatMemoryRepository {

    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS chat_memory_messages (
                conversation_id VARCHAR(255) NOT NULL,
                message_order INTEGER NOT NULL,
                message_type VARCHAR(32) NOT NULL,
                text_content TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                PRIMARY KEY (conversation_id, message_order)
            )
            """;

    private static final String FIND_CONVERSATION_IDS_SQL = """
            SELECT DISTINCT conversation_id
            FROM chat_memory_messages
            ORDER BY conversation_id
            """;

    private static final String FIND_MESSAGES_SQL = """
            SELECT message_type, text_content
            FROM chat_memory_messages
            WHERE conversation_id = ?
            ORDER BY message_order ASC
            """;

    private static final String DELETE_MESSAGES_SQL = """
            DELETE FROM chat_memory_messages
            WHERE conversation_id = ?
            """;

    private static final String INSERT_MESSAGE_SQL = """
            INSERT INTO chat_memory_messages (conversation_id, message_order, message_type, text_content)
            VALUES (?, ?, ?, ?)
            """;

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public PostgresChatMemoryRepository(DataSource dataSource, PlatformTransactionManager transactionManager) {
        this(new JdbcTemplate(dataSource), transactionManager);
    }

    public PostgresChatMemoryRepository(JdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        initializeSchema();
    }

    private void initializeSchema() {
        jdbcTemplate.execute(CREATE_TABLE_SQL);
    }

    @Override
    public List<String> findConversationIds() {
        return jdbcTemplate.queryForList(FIND_CONVERSATION_IDS_SQL, String.class);
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        return jdbcTemplate.query(
                FIND_MESSAGES_SQL,
                (rs, rowNum) -> toMessage(rs.getString("message_type"), rs.getString("text_content")),
                conversationId);
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        transactionTemplate.executeWithoutResult(status -> {
            jdbcTemplate.update(DELETE_MESSAGES_SQL, conversationId);

            if (messages == null || messages.isEmpty()) {
                return;
            }

            for (int index = 0; index < messages.size(); index++) {
                Message message = messages.get(index);
                int messageOrder = index;
                validateSupportedMessage(message);
                jdbcTemplate.update(INSERT_MESSAGE_SQL,
                        conversationId, messageOrder, message.getMessageType().name(), message.getText());
            }
        });
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        jdbcTemplate.update(DELETE_MESSAGES_SQL, conversationId);
    }

    private Message toMessage(String rawType, String text) {
        MessageType messageType = MessageType.valueOf(rawType);
        return switch (messageType) {
            case USER -> new UserMessage(text);
            case ASSISTANT -> new AssistantMessage(text);
            case SYSTEM -> new SystemMessage(text);
            case TOOL -> throw new IllegalStateException(
                    "Los mensajes TOOL no están soportados por este repositorio simplificado.");
        };
    }

    private void validateSupportedMessage(Message message) {
        if (message.getMessageType() == MessageType.TOOL) {
            throw new IllegalStateException(
                    "Este proyecto no persiste mensajes TOOL. "
                            + "Solo soporta mensajes USER, ASSISTANT y SYSTEM.");
        }
    }
}
