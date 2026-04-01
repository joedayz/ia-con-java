package com.joedayz.ia.springai.controller;

import com.joedayz.ia.springai.dto.BuscarResponse;
import com.joedayz.ia.springai.dto.CargarPdfRequest;
import com.joedayz.ia.springai.service.SemanticSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Lab 10 + Reto:
 * - /api/buscar para documentos similares
 * - carga de PDF con TikaDocumentReader
 */
@RestController
@RequestMapping("/api/buscar")
public class BusquedaController {

    private final SemanticSearchService semanticSearchService;

    public BusquedaController(SemanticSearchService semanticSearchService) {
        this.semanticSearchService = semanticSearchService;
    }

    @PostMapping("/demo")
    public ResponseEntity<Map<String, Object>> cargarDemo() {
        int total = semanticSearchService.cargarDocumentosDemo();
        return ResponseEntity.ok(Map.of(
                "message", "Documentos teoricos indexados en memoria",
                "documentos", total,
                "tip", "Ahora usa GET /api/buscar?query=similitud%20coseno"
        ));
    }

    @GetMapping
    public ResponseEntity<BuscarResponse> buscar(
            @RequestParam String query,
            @RequestParam(defaultValue = "4") int topK) {

        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        int safeTopK = Math.max(1, Math.min(topK, 20));
        return ResponseEntity.ok(semanticSearchService.buscar(query, safeTopK));
    }

    @PostMapping("/pdf")
    public ResponseEntity<Map<String, Object>> cargarPdf(@RequestBody CargarPdfRequest request) {
        if (request == null || request.getPath() == null || request.getPath().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "path es obligatorio"));
        }

        int total = semanticSearchService.cargarPdf(request.getPath(), request.getSourceId());
        return ResponseEntity.ok(Map.of(
                "message", "PDF indexado en memoria",
                "documentos", total,
                "path", request.getPath()
        ));
    }
}

