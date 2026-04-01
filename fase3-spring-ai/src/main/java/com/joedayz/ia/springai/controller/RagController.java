package com.joedayz.ia.springai.controller;

import com.joedayz.ia.springai.dto.RagRequest;
import com.joedayz.ia.springai.dto.RagResponse;
import com.joedayz.ia.springai.service.RagService;
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
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    public ResponseEntity<RagResponse> rag(@RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.answer(request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}

