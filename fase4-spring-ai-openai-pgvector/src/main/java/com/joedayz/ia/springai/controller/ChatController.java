package com.joedayz.ia.springai.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joedayz.ia.springai.dto.ChatRequest;
import com.joedayz.ia.springai.dto.ChatResponse;
import com.joedayz.ia.springai.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "💬 Chat", description = "Endpoints de chatbot con memoria conversacional en PostgreSQL")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(
            summary = "Chat simple con sesión única",
            description = "Envía un mensaje al chatbot. Usa memoria conversacional en la sesión 'default'.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuesta exitosa del chatbot"),
                    @ApiResponse(responseCode = "400", description = "Mensaje vacío o inválido")
            })
    public ResponseEntity<ChatResponse> chat(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Mensaje del usuario",
                    required = true,
                    content = @Content(examples = {
                            @ExampleObject(name = "Saludo", value = "{\"message\": \"Hola, me llamo Carlos\"}"),
                            @ExampleObject(name = "Pregunta", value = "{\"message\": \"¿Cómo te llamas?\"}"),
                            @ExampleObject(name = "Seguimiento", value = "{\"message\": \"¿Recuerdas mi nombre?\"}")
                    }))
            @RequestBody ChatRequest request) {
        String mensaje = request.getMessage();

        if (mensaje == null || mensaje.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse("Error: El mensaje no puede estar vacío"));
        }
        String respuesta = chatService.chat(mensaje);
        return ResponseEntity.ok(new ChatResponse(respuesta, "default"));
    }

    @PostMapping("/{sessionId}")
    @Operation(
            summary = "Chat multi-sesión",
            description = "Envía un mensaje usando un ID de sesión específico. Cada sesión mantiene su propio historial.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuesta exitosa del chatbot"),
                    @ApiResponse(responseCode = "400", description = "SessionId o mensaje inválido")
            })
    public ResponseEntity<ChatResponse> chatWithSession(
            @Parameter(description = "ID único de sesión", example = "user-123")
            @PathVariable String sessionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Mensaje del usuario",
                    required = true,
                    content = @Content(examples = {
                            @ExampleObject(name = "Usuario 1", value = "{\"message\": \"Hola, me llamo Carlos\"}"),
                            @ExampleObject(name = "Usuario 2", value = "{\"message\": \"Hola, me llamo Ana\"}")
                    }))
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

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Map<String, String>> clearSession(@PathVariable String sessionId) {
        chatService.clearSession(sessionId);
        return ResponseEntity.ok(Map.of(
                "message", "Sesión limpiada exitosamente",
                "sessionId", sessionId));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "running",
                "service", chatService.getStatus()));
    }
}
