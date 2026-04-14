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
 * Servicio para interactuar con Ollama (modelos de IA locales)
 *
 * Ollama proporciona una API compatible con OpenAI, pero:
 * - NO requiere API key
 * - Se ejecuta localmente en http://localhost:11434
 * - Soporta modelos como Mistral, Llama, etc.
 */
@Service
public class IAService {

    private static final Logger log = LoggerFactory.getLogger(IAService.class);

    private final IAConfig config;
    private final WebClient ollamaClient;

    public IAService(IAConfig config) {
        this.config = config;
        
        // Cliente para Ollama (compatible con API de OpenAI)
        this.ollamaClient = WebClient.builder()
                .baseUrl(config.getOllama().getBase())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Envía un mensaje a Ollama
     * @param message Mensaje del usuario
     * @param provider Parámetro ignorado (siempre usa Ollama)
     */
    public String chat(String message, String provider) {
        return chat(message, null, provider);
    }

    /**
     * Envía un mensaje con system prompt opcional a Ollama
     * @param message Mensaje del usuario
     * @param systemPrompt Prompt del sistema (opcional)
     * @param provider Parámetro ignorado (siempre usa Ollama)
     */
    public String chat(String message, String systemPrompt, String provider) {
        return chatOllama(message, systemPrompt);
    }

    private String chatOllama(String userMessage, String systemPrompt) {

        List<Message> messages = systemPrompt != null 
            ? List.of(Message.system(systemPrompt), Message.user(userMessage))
            : List.of(Message.user(userMessage));

        ChatRequest request = new ChatRequest(
            config.getOllama().getModel(),
            messages,
            config.getOllama().getMaxTokens()
        );

        log.debug("Llamando a Ollama: model={}", config.getOllama().getModel());

        try {
            Duration timeout = parseTimeout(config.getOllama().getTimeout());

            ChatResponse response = ollamaClient.post()
                    .uri("/v1/chat/completions")
                    // NO agregar Authorization header - Ollama no lo requiere
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(ChatResponse.class)
                    .timeout(timeout)
                    .block();

            if (response == null) {
                throw new RuntimeException("No se recibió respuesta de Ollama");
            }

            return response.getContent();

        } catch (Exception e) {
            log.error("Error llamando a Ollama", e);
            throw new RuntimeException("Error con Ollama: " + e.getMessage(), e);
        }
    }

    private Duration parseTimeout(String timeout) {
        if (timeout == null || timeout.isBlank()) {
            return Duration.ofMinutes(2); // Modelos locales pueden ser lentos
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
