package dev.springai.workshop.websocket;

import dev.springai.workshop.agent.CustomerSupportAgent;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class CustomerSupportAgentWebSocketHandler extends TextWebSocketHandler {

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
        String reply = customerSupportAgent.chat(session.getId(), message.getPayload());
        session.sendMessage(new TextMessage(reply));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // La memoria de la sesión queda en ChatMemory hasta reiniciar la app (step-01).
    }
}
