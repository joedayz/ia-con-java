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
 * Lab 11: RAG Simple (contexto manual en el prompt)
 * Lab 12: RAG con QuestionAnswerAdvisor de Spring AI
 * Reto:   Asistente de documentación con archivos Markdown
 */
@RestController
@RequestMapping("/api/rag")
@Tag(name = "🧠 RAG", description = "Retrieval Augmented Generation - Labs 11, 12 y Reto")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    // ── Lab 11: RAG Simple ──────────────────────────────────────────────────────

    @PostMapping("/simple")
    @Operation(
        summary = "Lab 11: RAG Simple - Contexto manual en el prompt",
        description = """
            Pipeline manual: 
            1) Búsqueda semántica en SimpleVectorStore →
            2) Formatear contexto con citas [1],[2],[3] →
            3) Inyectar en prompt →
            4) Generar respuesta con LLM.
            Requiere haber cargado documentos con POST /api/buscar/demo o /api/rag/docs/cargar.""",
        responses = {
            @ApiResponse(responseCode = "200", description = "Respuesta generada con citas"),
            @ApiResponse(responseCode = "400", description = "Query vacío o sin documentos indexados")
        }
    )
    public ResponseEntity<RagResponse> ragSimple(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Pregunta para RAG simple",
                required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "Embeddings", value = "{\"query\": \"¿Qué son los embeddings?\", \"topK\": 3}"),
                    @ExampleObject(name = "Similitud", value = "{\"query\": \"Explica la similitud coseno\", \"topK\": 4}")
                })
            )
            @RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.answer(request));
    }

    // ── Lab 12: RAG con QuestionAnswerAdvisor ───────────────────────────────────

    @PostMapping("/advisor")
    @Operation(
        summary = "Lab 12: RAG con QuestionAnswerAdvisor de Spring AI",
        description = """
            Usa el advisor integrado de Spring AI que automatiza todo el pipeline:
            búsqueda semántica + inyección de contexto + generación.
            Compara el código con /api/rag/simple para ver la diferencia.
            Requiere haber cargado documentos previamente.""",
        responses = {
            @ApiResponse(responseCode = "200", description = "Respuesta generada con advisor"),
            @ApiResponse(responseCode = "400", description = "Query vacío o sin documentos")
        }
    )
    public ResponseEntity<RagResponse> ragAdvisor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Pregunta para RAG con advisor",
                required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "Spring AI", value = "{\"query\": \"¿Cómo funciona Spring AI?\", \"topK\": 4}"),
                    @ExampleObject(name = "Vector DB", value = "{\"query\": \"¿Qué bases vectoriales existen?\", \"topK\": 3}")
                })
            )
            @RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.answerWithAdvisor(request));
    }

    // ── Reto: Asistente de documentación con Markdown ───────────────────────────

    @PostMapping("/docs/cargar")
    @Operation(
        summary = "Reto: Cargar archivos Markdown para el asistente",
        description = """
            Carga todos los archivos .md de un directorio, los divide por secciones
            (encabezados ##) y los indexa en el vector store.
            Después usa /api/rag/simple o /api/rag/advisor para hacer preguntas.""",
        responses = {
            @ApiResponse(responseCode = "200", description = "Archivos indexados"),
            @ApiResponse(responseCode = "400", description = "Directorio no encontrado")
        }
    )
    public ResponseEntity<Map<String, Object>> cargarDocs(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Directorio con archivos .md",
                required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "Docs del curso", value = "{\"path\": \"./data/docs\"}"),
                    @ExampleObject(name = "Directorio custom", value = "{\"path\": \"/ruta/a/mis/docs\"}")
                })
            )
            @RequestBody Map<String, String> request) {
        String path = request.get("path");
        if (path == null || path.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "path es obligatorio"));
        }
        return ResponseEntity.ok(ragService.cargarDocumentosMarkdown(path));
    }

    @PostMapping("/docs/preguntar")
    @Operation(
        summary = "Reto: Preguntar al asistente de documentación",
        description = """
            Igual que /api/rag/advisor pero pensado para el asistente de docs.
            Primero carga los documentos con POST /api/rag/docs/cargar.""",
        responses = {
            @ApiResponse(responseCode = "200", description = "Respuesta del asistente"),
            @ApiResponse(responseCode = "400", description = "Sin documentos o query vacío")
        }
    )
    public ResponseEntity<RagResponse> preguntarDocs(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Pregunta sobre la documentación",
                required = true,
                content = @Content(examples = {
                    @ExampleObject(name = "Pregunta", value = "{\"query\": \"¿Cómo implementar RAG con Spring AI?\", \"topK\": 5}")
                })
            )
            @RequestBody RagRequest request) {
        return ResponseEntity.ok(ragService.answerWithAdvisor(request));
    }

    // ── Backward compatibility (mantener /api/rag original) ─────────────────────

    @PostMapping
    @Operation(
        summary = "RAG (alias de /api/rag/simple)",
        description = "Endpoint original. Redirige al pipeline simple del Lab 11.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Respuesta generada")
        }
    )
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

