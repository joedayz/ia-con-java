package com.joedayz.ia.springai.controller;

import com.joedayz.ia.springai.dto.ChatRequest;
import com.joedayz.ia.springai.dto.ChatResponse;
import com.joedayz.ia.springai.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para el chatbot.
 * 
 * Endpoints:
 * - POST /api/chat - Chat simple (sesión por defecto)
 * - POST /api/chat/{sessionId} - Chat multi-sesión (RETO)
 * - DELETE /api/chat/{sessionId} - Limpiar sesión
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * TODO LAB 7: Endpoint simple de chat (sesión única).
     * 
     * PISTAS:
     * 1. Extraer mensaje de request.getMessage()
     * 2. Llamar a chatService.chat(mensaje)
     * 3. Devolver ChatResponse con la respuesta
     * 
     * Ejemplo de uso:
     * POST http://localhost:8080/api/chat
     * Body: {"message": "Hola, me llamo Carlos"}
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        // TODO: Implementar endpoint simple
        throw new UnsupportedOperationException("TODO: Implementar endpoint /api/chat");
    }

    /**
     * TODO RETO: Endpoint de chat multi-sesión.
     * 
     * Cada sessionId mantiene su propia conversación independiente.
     * 
     * PISTAS:
     * 1. Extraer sessionId del path variable
     * 2. Extraer mensaje de request.getMessage()
     * 3. Llamar a chatService.chat(sessionId, mensaje)
     * 4. Devolver ChatResponse con la respuesta
     * 
     * Ejemplo de uso:
     * POST http://localhost:8080/api/chat/user-123
     * Body: {"message": "Hola, me llamo Carlos"}
     * 
     * POST http://localhost:8080/api/chat/user-456
     * Body: {"message": "Hola, me llamo Ana"}
     * 
     * Cada usuario tendrá su propio historial.
     */
    @PostMapping("/{sessionId}")
    public ResponseEntity<ChatResponse> chatWithSession(
            @PathVariable String sessionId,
            @RequestBody ChatRequest request) {
        // TODO: Implementar endpoint con sessionId
        throw new UnsupportedOperationException("TODO: Implementar endpoint /api/chat/{sessionId}");
    }

    /**
     * Limpia el historial de una sesión.
     * 
     * DELETE http://localhost:8080/api/chat/user-123
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Map<String, String>> clearSession(@PathVariable String sessionId) {
        chatService.clearSession(sessionId);
        return ResponseEntity.ok(Map.of(
            "message", "Sesión limpiada",
            "sessionId", sessionId
        ));
    }
}
