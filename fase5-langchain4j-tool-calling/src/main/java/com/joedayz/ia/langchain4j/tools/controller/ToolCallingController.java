package com.joedayz.ia.langchain4j.tools.controller;

import com.joedayz.ia.langchain4j.tools.dto.ChatRequest;
import com.joedayz.ia.langchain4j.tools.dto.ChatResponse;
import com.joedayz.ia.langchain4j.tools.service.Assistant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de Tool Calling con LangChain4j.
 *
 * Demuestra cómo el LLM decide automáticamente cuándo invocar
 * las herramientas registradas con @Tool a través del AI Service (Assistant).
 */
@RestController
@RequestMapping("/api/tool-calling")
@Tag(name = "Tool Calling", description = "Endpoints de Tool Calling con LangChain4j")
public class ToolCallingController {

    private static final Logger log = LoggerFactory.getLogger(ToolCallingController.class);

    private final Assistant assistant;

    public ToolCallingController(Assistant assistant) {
        this.assistant = assistant;
    }

    @PostMapping("/chat")
    @Operation(summary = "Chat con Tool Calling",
            description = "Envía un mensaje y el LLM decide automáticamente si usar herramientas "
                    + "(calculadora, fecha, consultarPais) para responder. "
                    + "Ejemplos: '¿Cuánto es 125 * 37?', '¿Qué fecha es hoy?', 'Cuéntame sobre Colombia'")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        log.info("📨 Mensaje recibido: {}", request.message());
        String response = assistant.chat(request.message());
        log.info("📤 Respuesta generada");
        return new ChatResponse(response);
    }
}
