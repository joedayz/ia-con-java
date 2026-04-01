package com.joedayz.ia.springai.controller;

import com.joedayz.ia.springai.dto.RagRequest;
import com.joedayz.ia.springai.dto.RagResponse;
import com.joedayz.ia.springai.service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint RAG basico: retrieval + prompt grounding + generation con citas.
 */
@RestController
@RequestMapping("/api/rag")
@Tag(name = "🧠 RAG", description = "Retrieval Augmented Generation - Respuestas basadas en documentos indexados")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    @Operation(
        summary = "RAG: Respuesta con contexto de documentos",
        description = "Búsqueda semántica + generación con LLM. Recupera fragmentos relevantes del vector store y genera una respuesta citando las fuentes [1], [2], [3]. Requiere haber cargado documentos previamente.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Respuesta generada con citas"),
            @ApiResponse(responseCode = "400", description = "Query vacío o sin documentos indexados")
        }
    )
    public ResponseEntity<RagResponse> rag(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Pregunta del usuario",
                required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "Embeddings", value = "{\"query\": \"¿Qué son los embeddings?\", \"topK\": 3}"),
                    @ExampleObject(name = "Similitud", value = "{\"query\": \"Explica la similitud coseno\", \"topK\": 4}")
                })
            )
            @RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.answer(request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}

