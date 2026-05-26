package dev.springai.workshop.websocket;

import dev.springai.workshop.agent.CustomerSupportAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.UncheckedIOException;

@Component
public class CustomerSupportAgentWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomerSupportAgentWebSocketHandler.class);
    private static final String WELCOME = "Welcome to Miles of Smiles! How can I help you today?";

    private final CustomerSupportAgent customerSupportAgent;

    public CustomerSupportAgentWebSocketHandler(CustomerSupportAgent customerSupportAgent) {
        this.customerSupportAgent = customerSupportAgent;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(WELCOME));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.debug("WebSocket [{}] user -> {}", session.getId(), message.getPayload());
        customerSupportAgent.chatStream(session.getId(), message.getPayload())
                .subscribe(
                        chunk -> sendChunk(session, chunk),
                        error -> {
                            log.error("Streaming error for session {}", session.getId(), error);
                            sendChunk(session, "Sorry, something went wrong. Please try again.");
                        });
    }

    private void sendChunk(WebSocketSession session, String chunk) {
        synchronized (session) {
            if (!session.isOpen()) {
                return;
            }
            try {
                session.sendMessage(new TextMessage(chunk));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Memoria por sesión en ChatMemory hasta reiniciar la app.
    }
}
