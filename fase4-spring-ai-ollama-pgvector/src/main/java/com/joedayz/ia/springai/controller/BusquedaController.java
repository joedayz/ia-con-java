package com.joedayz.ia.springai.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joedayz.ia.springai.dto.BuscarResponse;
import com.joedayz.ia.springai.dto.CargarPdfRequest;
import com.joedayz.ia.springai.service.SemanticSearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/buscar")
@Tag(name = "🔍 Búsqueda Semántica", description = "Búsqueda por similitud usando embeddings de Ollama sobre PgVector")
public class BusquedaController {

    private final SemanticSearchService semanticSearchService;

    public BusquedaController(SemanticSearchService semanticSearchService) {
        this.semanticSearchService = semanticSearchService;
    }

    @GetMapping("/status")
    @Operation(
            summary = "Estado del vector store (PgVector)",
            description = "Verifica el estado del PgVectorStore y si el modelo de embeddings de Ollama está disponible.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estado del vector store")
            })
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok(semanticSearchService.getStatus());
    }

    @PostMapping("/demo")
    @Operation(
            summary = "Cargar documentos teóricos de demostración",
            description = "Indexa documentos de ejemplo en PgVector para probar la búsqueda semántica y RAG.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Documentos indexados exitosamente en PgVector")
            })
    public ResponseEntity<Map<String, Object>> cargarDemo() {
        int total = semanticSearchService.cargarDocumentosDemo();
        return ResponseEntity.ok(Map.of(
                "message", "Documentos teóricos indexados en PgVector",
                "documentos", total,
                "persistent", true,
                "tip", "Los documentos persisten en PostgreSQL. Usa GET /api/buscar?query=similitud%20coseno"));
    }

    @GetMapping
    @Operation(
            summary = "Buscar documentos por similitud semántica",
            description = "Busca los documentos más relevantes usando embeddings de Ollama y similitud coseno en PgVector.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resultados encontrados"),
                    @ApiResponse(responseCode = "400", description = "Query vacío")
            })
    public ResponseEntity<BuscarResponse> buscar(
            @Parameter(description = "Texto de búsqueda", example = "similitud coseno")
            @RequestParam String query,
            @Parameter(description = "Número de resultados (máx 20)", example = "4")
            @RequestParam(defaultValue = "4") int topK) {

        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        int safeTopK = Math.max(1, Math.min(topK, 20));
        return ResponseEntity.ok(semanticSearchService.buscar(query, safeTopK));
    }

    @PostMapping("/pdf")
    @Operation(
            summary = "Indexar PDF con Tika en PgVector",
            description = "Carga y procesa un archivo PDF local. Los embeddings se almacenan en PgVector de forma persistente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF indexado en PgVector"),
                    @ApiResponse(responseCode = "400", description = "Path inválido o archivo no encontrado")
            })
    public ResponseEntity<Map<String, Object>> cargarPdf(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Ruta del archivo PDF",
                    required = true,
                    content = @Content(examples = {
                            @ExampleObject(name = "PDF Local", value = "{\"path\": \"./data/documento.pdf\", \"sourceId\": \"mi-documento\"}")
                    }))
            @RequestBody CargarPdfRequest request) {
        if (request == null || request.getPath() == null || request.getPath().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "path es obligatorio"));
        }

        int total = semanticSearchService.cargarPdf(request.getPath(), request.getSourceId());
        return ResponseEntity.ok(Map.of(
                "message", "PDF indexado en PgVector",
                "documentos", total,
                "path", request.getPath(),
                "persistent", true));
    }
}
