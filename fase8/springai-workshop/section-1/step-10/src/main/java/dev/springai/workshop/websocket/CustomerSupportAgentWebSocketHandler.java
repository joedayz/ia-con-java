package dev.springai.workshop.websocket;

import dev.springai.workshop.agent.CustomerSupportAgent;
import dev.springai.workshop.guardrail.PromptInjectionBlockedException;
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
    private static final String GUARDRAIL_FAILURE = """
            Sorry, I am unable to process your request at the moment. It's not something I'm allowed to do.""";
    private static final String GENERIC_FAILURE = "I ran into some problems. Please try again.";

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
        } catch (PromptInjectionBlockedException e) {
            log.error("Guardrail blocked request: {}", e.getMessage());
            session.sendMessage(new TextMessage(GUARDRAIL_FAILURE));
        } catch (Exception e) {
            log.error("Error calling the LLM", e);
            session.sendMessage(new TextMessage(GENERIC_FAILURE));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Memoria por sesión hasta reiniciar la app.
    }
}
