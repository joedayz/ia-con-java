package com.joedayz.ia.fase1.springboot.controller;

import com.joedayz.ia.fase1.springboot.api.ChatApiRequest;
import com.joedayz.ia.fase1.springboot.api.ChatApiResponse;
import com.joedayz.ia.fase1.springboot.config.OpenAIConfig;
import com.joedayz.ia.fase1.springboot.service.OpenAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API para interactuar con OpenAI
 * 
 * Endpoints disponibles:
 * - GET /api/chat?message=texto : Envía un mensaje simple
 * - POST /api/chat : Envía un mensaje con opcional system prompt
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final OpenAIService openAIService;
    private final OpenAIConfig config;

    public ChatController(OpenAIService openAIService, OpenAIConfig config) {
        this.openAIService = openAIService;
        this.config = config;
    }

    /**
     * Endpoint GET simple: /api/chat?message=Hola
     */
    @GetMapping
    public ResponseEntity<ChatApiResponse> chatGet(@RequestParam(name = "message") String message) {
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("GET /api/chat - message: {}", message);
        String response = openAIService.chat(message);
        return ResponseEntity.ok(ChatApiResponse.of(response, config.getModel()));
    }

    /**
     * Endpoint POST con body JSON:
     * {
     *   "message": "Explica qué es un LLM",
     *   "system_prompt": "Eres un profesor de IA"
     * }
     */
    @PostMapping
    public ResponseEntity<ChatApiResponse> chatPost(@RequestBody ChatApiRequest request) {
        if (request == null || request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("POST /api/chat - message: {}, systemPrompt: {}", 
            request.message(), 
            request.systemPrompt() != null ? "presente" : "ausente");
        
        String response = openAIService.chat(request.message(), request.systemPrompt());
        return ResponseEntity.ok(ChatApiResponse.of(response, config.getModel()));
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat API está funcionando - Modelo: " + config.getModel());
    }
}
