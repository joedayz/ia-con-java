package com.joedayz.ia.fase1.springboot.start.controller;

import com.joedayz.ia.fase1.springboot.start.config.IAConfig;
import com.joedayz.ia.fase1.springboot.start.service.IAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API para chat con múltiples proveedores de IA
 * 
 * Endpoints:
 * - GET /api/chat?message=texto&provider=openai
 * - POST /api/chat con JSON: {"message": "...", "provider": "openai"}
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final IAService iaService;
    private final IAConfig config;

    public ChatController(IAService iaService, IAConfig config) {
        this.iaService = iaService;
        this.config = config;
    }

    /**
     * GET /api/chat?message=Hola&provider=openai
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> chatGet(
            @RequestParam(name = "message") String message,
            @RequestParam(name = "provider", defaultValue = "openai") String provider) {
        
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("GET /api/chat - provider: {}, message: {}", provider, message);
        
        try {
            String response = iaService.chat(message, provider);
            return ResponseEntity.ok(Map.of(
                "response", response,
                "provider", provider,
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.error("Error en chat GET", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/chat
     * Body: {"message": "Hola", "provider": "openai", "system_prompt": "..."}
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> chatPost(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String provider = request.getOrDefault("provider", "openai");
        String systemPrompt = request.get("system_prompt");
        
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("POST /api/chat - provider: {}, message: {}", provider, message);
        
        try {
            String response = iaService.chat(message, systemPrompt, provider);
            return ResponseEntity.ok(Map.of(
                "response", response,
                "provider", provider,
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.error("Error en chat POST", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "openai_model", config.getOpenai().getModel(),
            "anthropic_model", config.getAnthropic().getModel()
        ));
    }
}
