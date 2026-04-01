package com.joedayz.ia.springai.controller;

import com.joedayz.ia.springai.dto.ChatRequest;
import com.joedayz.ia.springai.dto.ChatResponse;
import com.joedayz.ia.springai.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "💬 Chat", description = "Endpoints de chatbot con memoria conversacional")
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
    @Operation(
        summary = "Chat simple con sesión única",
        description = "Envía un mensaje al chatbot. Usa memoria conversacional en la sesión 'default'. El contexto se mantiene entre llamadas.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Respuesta exitosa del chatbot"),
            @ApiResponse(responseCode = "400", description = "Mensaje vacío o inválido")
        }
    )
    public ResponseEntity<ChatResponse> chat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Mensaje del usuario",
                required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "Saludo", value = "{\"message\": \"Hola, me llamo Carlos\"}"),
                    @ExampleObject(name = "Pregunta", value = "{\"message\": \"¿Cómo te llamas?\"}"),
                    @ExampleObject(name = "Seguimiento", value = "{\"message\": \"¿Recuerdas mi nombre?\"}")
                })
            )
            @RequestBody ChatRequest request) {
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
    @Operation(
        summary = "Chat multi-sesión (RETO)",
        description = "Envía un mensaje usando un ID de sesión específico. Cada sesión mantiene su propio historial conversacional independiente. Ideal para aplicaciones multi-usuario.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Respuesta exitosa del chatbot"),
            @ApiResponse(responseCode = "400", description = "SessionId o mensaje inválido")
        }
    )
    public ResponseEntity<ChatResponse> chatWithSession(
            @Parameter(description = "ID único de sesión (ej: user-123, conversacion-abc)", example = "user-123")
            @PathVariable String sessionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Mensaje del usuario",
                required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "Usuario 1", value = "{\"message\": \"Hola, me llamo Carlos\"}"),
                    @ExampleObject(name = "Usuario 2", value = "{\"message\": \"Hola, me llamo Ana\"}")
                })
            )
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
