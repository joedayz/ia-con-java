package dev.springai.workshop.websocket;

import dev.springai.workshop.agent.CustomerSupportAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String reply = customerSupportAgent.chat(session.getId(), message.getPayload());
            session.sendMessage(new TextMessage(reply));
        } catch (Exception e) {
            log.error("Chat error for session {}", session.getId(), e);
            session.sendMessage(new TextMessage(
                    "Sorry, something went wrong: " + e.getMessage()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Memoria por sesión hasta reiniciar la app.
    }
}
