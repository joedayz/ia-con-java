package com.joedayz.ia.springai.controller;

import com.joedayz.ia.springai.dto.BuscarResponse;
import com.joedayz.ia.springai.dto.CargarPdfRequest;
import com.joedayz.ia.springai.service.SemanticSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Labs 9 y 10 (START): endpoints para busqueda semantica.
 */
@RestController
@RequestMapping("/api/buscar")
public class BusquedaController {

    private final SemanticSearchService semanticSearchService;

    public BusquedaController(SemanticSearchService semanticSearchService) {
        this.semanticSearchService = semanticSearchService;
    }

    /**
     * TODO LAB 9:
     * - Cargar documentos teoricos en memoria para pruebas de similitud.
     */
    @PostMapping("/demo")
    public ResponseEntity<Map<String, Object>> cargarDemo() {
        int total = semanticSearchService.cargarDocumentosDemo();
        return ResponseEntity.ok(Map.of("documentos", total));
    }

    /**
     * TODO LAB 10:
     * - Implementar busqueda por similitud semantica.
     */
    @GetMapping
    public ResponseEntity<BuscarResponse> buscar(
            @RequestParam String query,
            @RequestParam(defaultValue = "4") int topK) {

        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(semanticSearchService.buscar(query, topK));
    }

    /**
     * TODO RETO:
     * - Cargar PDF con TikaDocumentReader e indexar su contenido.
     */
    @PostMapping("/pdf")
    public ResponseEntity<Map<String, Object>> cargarPdf(@RequestBody CargarPdfRequest request) {
        if (request == null || request.getPath() == null || request.getPath().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "path es obligatorio"));
        }

        int total = semanticSearchService.cargarPdf(request.getPath(), request.getSourceId());
        return ResponseEntity.ok(Map.of("documentos", total, "path", request.getPath()));
    }
}

