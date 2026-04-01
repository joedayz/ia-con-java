package com.joedayz.ia.springai.controller;

import com.joedayz.ia.springai.dto.RagRequest;
import com.joedayz.ia.springai.dto.RagResponse;
import com.joedayz.ia.springai.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * RAG (START): endpoint para conectar retrieval con generation.
 */
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * TODO RAG:
     * - Validar request.question
     * - Invocar RagService
     * - Retornar respuesta con citas
     */
    @PostMapping
    public ResponseEntity<RagResponse> rag(@RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.answer(request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}

