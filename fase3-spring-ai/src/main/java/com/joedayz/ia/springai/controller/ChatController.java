package com.joedayz.ia.springai.controller;

import com.joedayz.ia.springai.dto.ChatRequest;
import com.joedayz.ia.springai.dto.ChatResponse;
import com.joedayz.ia.springai.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ✅ SOLUCIÓN COMPLETA: Controlador REST para el chatbot.
 * 
 * Endpoints implementados:
 * - POST /api/chat - Chat simple (Lab 7)
 * - POST /api/chat/{sessionId} - Chat multi-sesión (RETO)
 * - DELETE /api/chat/{sessionId} - Limpiar sesión
 * - GET /api/status - Info del servicio
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * ✅ SOLUCIÓN LAB 7: Endpoint simple de chat (sesión única).
     * 
     * Usa una sesión por defecto ("default") para todos los mensajes.
     * Ideal para prototipos o chatbots de un solo usuario.
     * 
     * Ejemplo de uso:
     * curl -X POST http://localhost:8080/api/chat \
     *   -H "Content-Type: application/json" \
     *   -d '{"message": "Hola, me llamo Carlos"}'
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String mensaje = request.getMessage();
        
        if (mensaje == null || mensaje.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse("Error: El mensaje no puede estar vacío"));
        }
        
        String respuesta = chatService.chat(mensaje);
        return ResponseEntity.ok(new ChatResponse(respuesta, "default"));
    }

    /**
     * ✅ SOLUCIÓN RETO: Endpoint de chat multi-sesión.
     * 
     * Cada sessionId mantiene su propia conversación independiente.
     * Perfecto para aplicaciones multi-usuario o múltiples contextos.
     * 
     * Casos de uso:
     * - Chat multi-usuario: cada usuario tiene su sessionId
     * - Múltiples conversaciones: un usuario puede tener varias sesiones
     * - A/B testing: diferentes configuraciones por sesión
     * 
     * Ejemplo de uso:
     * # Usuario 1
     * curl -X POST http://localhost:8080/api/chat/user-123 \
     *   -H "Content-Type: application/json" \
     *   -d '{"message": "Hola, me llamo Carlos"}'
     * 
     * # Usuario 2
     * curl -X POST http://localhost:8080/api/chat/user-456 \
     *   -H "Content-Type: application/json" \
     *   -d '{"message": "Hola, me llamo Ana"}'
     */
    @PostMapping("/{sessionId}")
    public ResponseEntity<ChatResponse> chatWithSession(
            @PathVariable String sessionId,
            @RequestBody ChatRequest request) {
        
        String mensaje = request.getMessage();
        
        if (sessionId == null || sessionId.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse("Error: sessionId no puede estar vacío"));
        }
        
        if (mensaje == null || mensaje.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse("Error: El mensaje no puede estar vacío"));
        }
        
        String respuesta = chatService.chat(sessionId, mensaje);
        return ResponseEntity.ok(new ChatResponse(respuesta, sessionId));
    }

    /**
     * Limpia el historial de una sesión específica.
     * Útil para "empezar de nuevo" o implementar botón de reset.
     * 
     * Ejemplo:
     * curl -X DELETE http://localhost:8080/api/chat/user-123
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Map<String, String>> clearSession(@PathVariable String sessionId) {
        chatService.clearSession(sessionId);
        return ResponseEntity.ok(Map.of(
            "message", "Sesión limpiada exitosamente",
            "sessionId", sessionId
        ));
    }

    /**
     * Endpoint de status/health check.
     * Útil para monitoring y debugging.
     * 
     * Ejemplo:
     * curl http://localhost:8080/api/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "status", "running",
            "service", chatService.getStatus()
        ));
    }
}
