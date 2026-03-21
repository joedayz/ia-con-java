package com.joedayz.ia.fase1.springboot.start.service;

import com.joedayz.ia.fase1.springboot.start.config.IAConfig;
import com.joedayz.ia.fase1.springboot.start.model.ChatRequest;
import com.joedayz.ia.fase1.springboot.start.model.ChatResponse;
import com.joedayz.ia.fase1.springboot.start.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

/**
 * Servicio multi-provider para interactuar con APIs de IA
 * Soporta: OpenAI y Anthropic
 */
@Service
public class IAService {

    private static final Logger log = LoggerFactory.getLogger(IAService.class);

    private final IAConfig config;
    private final WebClient openAIClient;
    private final WebClient anthropicClient;

    public IAService(IAConfig config) {
        this.config = config;
        
        // Cliente para OpenAI
        this.openAIClient = WebClient.builder()
                .baseUrl(config.getOpenai().getBase())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        
        // Cliente para Anthropic
        this.anthropicClient = WebClient.builder()
                .baseUrl(config.getAnthropic().getBase())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Envía un mensaje a la IA especificada
     * @param message Mensaje del usuario
     * @param provider "openai" o "anthropic"
     */
    public String chat(String message, String provider) {
        return chat(message, null, provider);
    }

    /**
     * Envía un mensaje con system prompt opcional
     * @param message Mensaje del usuario
     * @param systemPrompt Prompt del sistema (opcional)
     * @param provider "openai" o "anthropic"
     */
    public String chat(String message, String systemPrompt, String provider) {
        if ("openai".equalsIgnoreCase(provider)) {
            return chatOpenAI(message, systemPrompt);
        } else if ("anthropic".equalsIgnoreCase(provider)) {
            return chatAnthropic(message, systemPrompt);
        } else {
            throw new IllegalArgumentException("Proveedor desconocido: " + provider);
        }
    }

    private String chatOpenAI(String userMessage, String systemPrompt) {
        String apiKey = resolveOpenAIKey();
        
        List<Message> messages = systemPrompt != null 
            ? List.of(Message.system(systemPrompt), Message.user(userMessage))
            : List.of(Message.user(userMessage));

        ChatRequest request = new ChatRequest(
            config.getOpenai().getModel(),
            messages,
            config.getOpenai().getMaxTokens()
        );

        log.debug("Llamando a OpenAI: model={}", config.getOpenai().getModel());

        try {
            Duration timeout = parseTimeout(config.getOpenai().getTimeout());
            
            ChatResponse response = openAIClient.post()
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

            return response.getContent();

        } catch (Exception e) {
            log.error("Error llamando a OpenAI", e);
            throw new RuntimeException("Error con OpenAI: " + e.getMessage(), e);
        }
    }

    private String chatAnthropic(String userMessage, String systemPrompt) {
        String apiKey = resolveAnthropicKey();
        
        List<Message> messages = List.of(Message.user(userMessage));

        ChatRequest request = new ChatRequest(
            config.getAnthropic().getModel(),
            messages,
            config.getAnthropic().getMaxTokens()
        );

        log.debug("Llamando a Anthropic: model={}", config.getAnthropic().getModel());

        try {
            Duration timeout = parseTimeout(config.getAnthropic().getTimeout());
            
            ChatResponse response = anthropicClient.post()
                    .uri("/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .timeout(timeout)
                    .block();

            if (response == null) {
                throw new RuntimeException("No se recibió respuesta de Anthropic");
            }

            return response.getContent();

        } catch (Exception e) {
            log.error("Error llamando a Anthropic", e);
            throw new RuntimeException("Error con Anthropic: " + e.getMessage(), e);
        }
    }

    private String resolveOpenAIKey() {
        String key = config.getOpenai().getKey();
        if (key != null && !key.isBlank()) return key.trim();
        
        String envKey = System.getenv("OPENAI_API_KEY");
        if (envKey != null && !envKey.isBlank()) return envKey.trim();
        
        throw new IllegalStateException("OPENAI_API_KEY no configurada");
    }

    private String resolveAnthropicKey() {
        String key = config.getAnthropic().getKey();
        if (key != null && !key.isBlank()) return key.trim();
        
        String envKey = System.getenv("ANTHROPIC_API_KEY");
        if (envKey != null && !envKey.isBlank()) return envKey.trim();
        
        throw new IllegalStateException("ANTHROPIC_API_KEY no configurada");
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
        
        return Duration.ofSeconds(Long.parseLong(timeout));
    }
}
