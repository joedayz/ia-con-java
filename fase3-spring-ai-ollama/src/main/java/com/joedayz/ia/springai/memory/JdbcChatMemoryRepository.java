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
import java.sql.Types;
import java.util.List;

/**
 * Repositorio JDBC sencillo para persistir el historial conversacional en H2.
 *
 * Guarda el contenido mínimo necesario para este proyecto: tipo de mensaje y texto.
 * Es suficiente para el flujo actual de chat con Ollama y MessageChatMemoryAdvisor.
 */
public class JdbcChatMemoryRepository implements ChatMemoryRepository {

    private static final String CREATE_TABLE_SQL = """
            create table if not exists chat_memory_messages (
                conversation_id varchar(255) not null,
                message_order integer not null,
                message_type varchar(32) not null,
                text_content clob,
                created_at timestamp default current_timestamp not null,
                primary key (conversation_id, message_order)
            )
            """;

    private static final String FIND_CONVERSATION_IDS_SQL = """
            select distinct conversation_id
            from chat_memory_messages
            order by conversation_id
            """;

    private static final String FIND_MESSAGES_SQL = """
            select message_type, text_content
            from chat_memory_messages
            where conversation_id = ?
            order by message_order asc
            """;

    private static final String DELETE_MESSAGES_SQL = """
            delete from chat_memory_messages
            where conversation_id = ?
            """;

    private static final String INSERT_MESSAGE_SQL = """
            insert into chat_memory_messages (conversation_id, message_order, message_type, text_content)
            values (?, ?, ?, ?)
            """;

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    public JdbcChatMemoryRepository(DataSource dataSource, PlatformTransactionManager transactionManager) {
        this(new JdbcTemplate(dataSource), transactionManager);
    }

    public JdbcChatMemoryRepository(JdbcTemplate jdbcTemplate, PlatformTransactionManager transactionManager) {
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
                conversationId
        );
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
                jdbcTemplate.update(connection -> {
                    var ps = connection.prepareStatement(INSERT_MESSAGE_SQL);
                    ps.setString(1, conversationId);
                    ps.setInt(2, messageOrder);
                    ps.setString(3, message.getMessageType().name());
                    ps.setObject(4, message.getText(), Types.CLOB);
                    return ps;
                });
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
                    "Este proyecto no persiste mensajes TOOL todavía. "
                            + "Actualmente solo usa mensajes USER, ASSISTANT y SYSTEM.");
        }
    }
}

