package com.joedayz.ia.springai.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joedayz.ia.springai.dto.RagRequest;
import com.joedayz.ia.springai.dto.RagResponse;
import com.joedayz.ia.springai.service.RagService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/rag")
@Tag(name = "🧠 RAG", description = "Retrieval Augmented Generation con OpenAI + PgVector")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/simple")
    @Operation(
            summary = "RAG simple - contexto manual en el prompt",
            description = "Pipeline manual: búsqueda semántica en PgVector, construcción del contexto y generación de respuesta con citas.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuesta generada con citas"),
                    @ApiResponse(responseCode = "400", description = "Query vacío o sin documentos indexados")
            })
    public ResponseEntity<RagResponse> ragSimple(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Pregunta para RAG simple",
                    required = true,
                    content = @Content(examples = {
                            @ExampleObject(name = "Embeddings", value = "{\"query\": \"¿Qué son los embeddings?\", \"topK\": 3}"),
                            @ExampleObject(name = "PgVector", value = "{\"query\": \"¿Qué es PgVector?\", \"topK\": 4}")
                    }))
            @RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.answer(request));
    }

    @PostMapping("/advisor")
    @Operation(
            summary = "RAG con QuestionAnswerAdvisor",
            description = "Usa el advisor de Spring AI para automatizar búsqueda en PgVector, inyección de contexto y generación.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuesta generada con advisor"),
                    @ApiResponse(responseCode = "400", description = "Query vacío o sin documentos")
            })
    public ResponseEntity<RagResponse> ragAdvisor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Pregunta para RAG con advisor",
                    required = true,
                    content = @Content(examples = {
                            @ExampleObject(name = "Spring AI", value = "{\"query\": \"¿Cómo funciona Spring AI?\", \"topK\": 4}"),
                            @ExampleObject(name = "RAG", value = "{\"query\": \"¿Qué es RAG?\", \"topK\": 3}")
                    }))
            @RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.answerWithAdvisor(request));
    }

    @PostMapping("/docs/cargar")
    @Operation(
            summary = "Cargar archivos Markdown en PgVector",
            description = "Carga todos los archivos .md de un directorio, los divide por secciones y los indexa en PgVector.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Archivos indexados en PgVector"),
                    @ApiResponse(responseCode = "400", description = "Directorio no encontrado")
            })
    public ResponseEntity<Map<String, Object>> cargarDocs(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Directorio con archivos .md",
                    required = true,
                    content = @Content(examples = {
                            @ExampleObject(name = "Docs del curso", value = "{\"path\": \"./data/docs\"}"),
                            @ExampleObject(name = "Directorio custom", value = "{\"path\": \"/ruta/a/mis/docs\"}")
                    }))
            @RequestBody Map<String, String> request) {
        String path = request.get("path");
        if (path == null || path.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "path es obligatorio"));
        }
        return ResponseEntity.ok(ragService.cargarDocumentosMarkdown(path));
    }

    @PostMapping("/docs/preguntar")
    @Operation(
            summary = "Preguntar al asistente de documentación",
            description = "Hace RAG con QuestionAnswerAdvisor sobre la documentación previamente cargada en PgVector.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuesta del asistente"),
                    @ApiResponse(responseCode = "400", description = "Sin documentos o query vacío")
            })
    public ResponseEntity<RagResponse> preguntarDocs(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Pregunta sobre la documentación",
                    required = true,
                    content = @Content(examples = {
                            @ExampleObject(name = "Pregunta", value = "{\"query\": \"¿Cómo implementar RAG con Spring AI?\", \"topK\": 5}")
                    }))
            @RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.answerWithAdvisor(request));
    }

    @PostMapping
    @Operation(
            summary = "RAG (alias de /api/rag/simple)",
            description = "Endpoint original. Redirige al pipeline manual de RAG.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Respuesta generada")
            })
    public ResponseEntity<RagResponse> rag(@RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.answer(request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleServerError(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
