package com.joedayz.ia.springai.tools.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joedayz.ia.springai.tools.dto.ChatRequest;
import com.joedayz.ia.springai.tools.dto.ChatResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller de Tool Calling con Spring AI.
 *
 * Demuestra cómo el LLM decide automáticamente cuándo invocar
 * las herramientas registradas como @Bean (Function callbacks).
 */
@RestController
@RequestMapping("/api/tool-calling")
@Tag(name = "Tool Calling", description = "Endpoints de Tool Calling con Spring AI")
public class ToolCallingController {

    private static final Logger log = LoggerFactory.getLogger(ToolCallingController.class);

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            Eres un asistente útil que responde siempre en español.
            Tienes acceso a herramientas (tools) para:
            1. Consultar el clima de una ciudad (obtenerClima)
            2. Consultar información de un país usando una API REST real (consultarPais)

            Cuando el usuario pregunte sobre el clima, temperatura o condiciones meteorológicas de una ciudad, \
            usa la herramienta obtenerClima.
            Cuando el usuario pregunte sobre un país (capital, población, idiomas, región), \
            usa la herramienta consultarPais.
            Siempre incluye los datos obtenidos de las herramientas en tu respuesta.
            Si la pregunta no requiere herramientas, responde normalmente.
            """;

    public ToolCallingController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @PostMapping("/chat")
    @Operation(summary = "Chat con Tool Calling",
            description = "Envía un mensaje y el LLM decide automáticamente si usar herramientas "
                    + "(obtenerClima, consultarPais) para responder. "
                    + "Ejemplos: '¿Cómo está el clima en Lima?' o 'Cuéntame sobre Japón'")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        log.info("📨 Mensaje recibido: {}", request.message());

        String response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .toolNames("obtenerClima", "consultarPais")
                .user(request.message())
                .call()
                .content();

        log.info("📤 Respuesta generada");
        return new ChatResponse(response);
    }
}
