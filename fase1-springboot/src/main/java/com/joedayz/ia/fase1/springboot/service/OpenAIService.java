package com.joedayz.ia.fase1.springboot.service;

import com.joedayz.ia.fase1.springboot.config.OpenAIConfig;
import com.joedayz.ia.fase1.springboot.model.ChatRequest;
import com.joedayz.ia.fase1.springboot.model.ChatResponse;
import com.joedayz.ia.fase1.springboot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

/**
 * Servicio para interactuar con la API de OpenAI
 */
@Service
public class OpenAIService {

    private static final Logger log = LoggerFactory.getLogger(OpenAIService.class);

    private final OpenAIConfig config;
    private final WebClient webClient;

    public OpenAIService(OpenAIConfig config) {
        this.config = config;
        
        // Parsear timeout (ej: "30s" -> 30 segundos)
        Duration timeout = parseTimeout(config.getTimeout());
        
        this.webClient = WebClient.builder()
                .baseUrl(config.getBase())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

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
            config.getModel(),
            messages,
            config.getMaxTokens()
        );

        log.debug("Enviando request a OpenAI: model={}, messages={}", 
            config.getModel(), messages.size());

        try {
            Duration timeout = parseTimeout(config.getTimeout());
            
            ChatResponse response = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .timeout(timeout)
                    .block();

            if (response == null) {
                throw new RuntimeException("No se recibió respuesta de OpenAI");
            }

            String content = response.getContent();
            Integer totalTokens = response.usage() != null ? response.usage().totalTokens() : null;
            int tokensUsed = totalTokens != null ? totalTokens : 0;
            log.debug("Respuesta recibida: {} tokens usados", tokensUsed);

            return content;

        } catch (Exception e) {
            log.error("Error llamando a la API de OpenAI", e);
            throw new RuntimeException("Error comunicándose con OpenAI: " + e.getMessage(), e);
        }
    }

    private String resolveApiKey() {
        String configured = config.getKey();
        if (configured != null && !configured.isBlank()) {
            return configured.trim();
        }

        String fromEnvironment = System.getenv("OPENAI_API_KEY");
        if (fromEnvironment != null && !fromEnvironment.isBlank()) {
            return fromEnvironment.trim();
        }

        throw new IllegalStateException(
            "API Key no configurada. Define OPENAI_API_KEY en variables de entorno o en application.properties");
    }

    private void validateConfig(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("API Key de OpenAI no puede estar vacía");
        }
        if (config.getBase() == null || config.getBase().isBlank()) {
            throw new IllegalStateException("Base URL de OpenAI no puede estar vacía");
        }
        if (config.getModel() == null || config.getModel().isBlank()) {
            throw new IllegalStateException("Modelo de OpenAI no puede estar vacío");
        }
    }

    private Duration parseTimeout(String timeout) {
        if (timeout == null || timeout.isBlank()) {
            return Duration.ofSeconds(30);
        }
        
        timeout = timeout.trim().toLowerCase();
        
        if (timeout.endsWith("s")) {
            return Duration.ofSeconds(Long.parseLong(timeout.substring(0, timeout.length() - 1)));
        } else if (timeout.endsWith("ms")) {
            return Duration.ofMillis(Long.parseLong(timeout.substring(0, timeout.length() - 2)));
        } else if (timeout.endsWith("m")) {
            return Duration.ofMinutes(Long.parseLong(timeout.substring(0, timeout.length() - 1)));
        }
        
        // Por defecto, asumir segundos
        return Duration.ofSeconds(Long.parseLong(timeout));
    }
}
