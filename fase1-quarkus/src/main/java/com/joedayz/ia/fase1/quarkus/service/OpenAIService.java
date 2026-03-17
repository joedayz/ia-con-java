package com.joedayz.ia.fase1.quarkus.service;

import com.joedayz.ia.common.config.EnvConfig;
import com.joedayz.ia.fase1.quarkus.config.OpenAIConfig;
import com.joedayz.ia.fase1.quarkus.model.ChatRequest;
import com.joedayz.ia.fase1.quarkus.model.ChatResponse;
import com.joedayz.ia.fase1.quarkus.model.Message;
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
 * Servicio para interactuar con la API de OpenAI
 */
@ApplicationScoped
public class OpenAIService {

    private static final Logger LOG = Logger.getLogger(OpenAIService.class);

    @Inject
    OpenAIConfig config;

    /**
     * Envía un mensaje al modelo de OpenAI y obtiene la respuesta
     */
    public String chat(String userMessage) {
        return chat(userMessage, null);
    }

    /**
     * Envía un mensaje con un system prompt al modelo de OpenAI
     */
    public String chat(String userMessage, String systemPrompt) {
        String apiKey = resolveApiKey();
        validateConfig(apiKey);

        List<Message> messages = systemPrompt != null 
            ? List.of(Message.system(systemPrompt), Message.user(userMessage))
            : List.of(Message.user(userMessage));

        ChatRequest request = new ChatRequest(
            config.model(),
            messages,
            config.maxTokens()
        );

        LOG.debugf("Enviando request a OpenAI: model=%s, messages=%d", 
            config.model(), messages.size());

        try {
            // Parsear timeout (ej: "30s" -> 30 segundos)
            long timeoutSeconds = parseTimeout(config.timeout());
            
            Client client = ClientBuilder.newBuilder()
                .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .build();

            String url = config.base().endsWith("/") 
                ? config.base() + "chat/completions" 
                : config.base() + "/chat/completions";

            ChatResponse response = client
                .target(url)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .post(Entity.json(request), ChatResponse.class);

            String content = response.getContent();
            Integer totalTokens = response.usage() != null ? response.usage().totalTokens() : null;
            int tokensUsed = totalTokens != null ? totalTokens : 0;
            LOG.debugf("Respuesta recibida: %d tokens usados", tokensUsed);
            
            return content;

        } catch (Exception e) {
            LOG.error("Error llamando a la API de OpenAI", e);
            throw new RuntimeException("Error comunicándose con OpenAI: " + e.getMessage(), e);
        }
    }

    private String resolveApiKey() {
        String configured = config.key().orElse(null);
        if (configured != null && !configured.isBlank()) {
            return configured.trim();
        }

        String fromDotEnv = EnvConfig.get("OPENAI_API_KEY");
        return fromDotEnv != null ? fromDotEnv.trim() : null;
    }

    private void validateConfig(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                "OPENAI_API_KEY no está configurada. " +
                "Configúrala en la raíz del repo (.env) o como variable de entorno"
            );
        }
    }
    
    private long parseTimeout(String timeout) {
        if (timeout == null || timeout.isBlank()) {
            return 30;
        }
        // Parsear formatos como "30s", "1m", etc.
        timeout = timeout.trim().toLowerCase();
        if (timeout.endsWith("s")) {
            return Long.parseLong(timeout.substring(0, timeout.length() - 1));
        } else if (timeout.endsWith("m")) {
            return Long.parseLong(timeout.substring(0, timeout.length() - 1)) * 60;
        }
        return Long.parseLong(timeout);
    }
}
