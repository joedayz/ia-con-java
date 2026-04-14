package com.joedayz.ia.fase1.quarkus.start.service;

import com.joedayz.ia.fase1.quarkus.start.config.IAConfig;
import com.joedayz.ia.fase1.quarkus.start.model.ChatRequest;
import com.joedayz.ia.fase1.quarkus.start.model.ChatResponse;
import com.joedayz.ia.fase1.quarkus.start.model.Message;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Servicio para interactuar con Ollama (modelos de IA locales).
 *
 * Ollama proporciona una API compatible con OpenAI en http://localhost:11434
 * NO requiere API key.
 */
@ApplicationScoped
public class IAService {

    private static final Logger LOG = Logger.getLogger(IAService.class);

    @Inject
    IAConfig config;

    public String chat(String userMessage) {
        return chat(userMessage, null);
    }

    public String chat(String userMessage, String systemPrompt) {
        if (userMessage == null || userMessage.isBlank()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío");
        }

        List<Message> messages = systemPrompt != null
            ? List.of(Message.system(systemPrompt), Message.user(userMessage))
            : List.of(Message.user(userMessage));

        ChatRequest request = new ChatRequest(
            config.ollama().model(),
            messages,
            config.ollama().maxTokens()
        );

        LOG.debugf("Llamando a Ollama: model=%s, messages=%d",
            config.ollama().model(), messages.size());

        try {
            long timeoutSeconds = parseTimeout(config.ollama().timeout());

            try (Client client = ClientBuilder.newBuilder()
                    .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                    .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                    .build()) {

                String url = config.ollama().base().endsWith("/")
                    ? config.ollama().base() + "v1/chat/completions"
                    : config.ollama().base() + "/v1/chat/completions";

                ChatResponse response = client
                    .target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(request), ChatResponse.class);

                if (response == null) {
                    throw new RuntimeException("No se recibió respuesta de Ollama");
                }

                String content = response.getContent();
                LOG.debugf("Respuesta recibida de Ollama");
                return content;
            }

        } catch (Exception e) {
            LOG.error("Error llamando a Ollama", e);
            throw new RuntimeException("Error con Ollama: " + e.getMessage(), e);
        }
    }

    private long parseTimeout(String timeout) {
        if (timeout == null || timeout.isBlank()) {
            return 120; // 2 minutos por defecto para modelos locales
        }

        timeout = timeout.trim().toLowerCase();

        if (timeout.endsWith("m")) {
            return Long.parseLong(timeout.substring(0, timeout.length() - 1)) * 60;
        } else if (timeout.endsWith("s")) {
            return Long.parseLong(timeout.substring(0, timeout.length() - 1));
        }

        return Long.parseLong(timeout);
    }
}
