package com.joedayz.ia.fase1.springboot.start.controller;

import com.joedayz.ia.fase1.springboot.start.config.IAConfig;
import com.joedayz.ia.fase1.springboot.start.service.IAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API para chat con Ollama (modelos de IA locales)
 *
 * Endpoints:
 * - GET /api/chat?message=texto
 * - POST /api/chat con JSON: {"message": "...", "system_prompt": "..."}
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
     * GET /api/chat?message=Hola
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> chatGet(
            @RequestParam(name = "message") String message) {

        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("GET /api/chat - message: {}", message);

        try {
            String response = iaService.chat(message, "ollama");
            return ResponseEntity.ok(Map.of(
                "response", response,
                "provider", "ollama",
                "model", config.getOllama().getModel(),
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
     * Body: {"message": "Hola", "system_prompt": "..."}
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> chatPost(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String systemPrompt = request.get("system_prompt");
        
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        
        log.info("POST /api/chat - message: {}", message);

        try {
            String response = iaService.chat(message, systemPrompt, "ollama");
            return ResponseEntity.ok(Map.of(
                "response", response,
                "provider", "ollama",
                "model", config.getOllama().getModel(),
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
            "provider", "ollama",
            "model", config.getOllama().getModel(),
            "base_url", config.getOllama().getBase()
        ));
    }
}
