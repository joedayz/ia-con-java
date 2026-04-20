package com.joedayz.ia.fase5.mcp.provider.capabilities;

import com.logaritex.mcp.annotation.McpResource;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceRequest;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CursoResourceProvider {

    private static final Map<String, String> CRONOGRAMA = Map.ofEntries(
        Map.entry("1", "Clase 1: Fundamentos de IA y primera llamada a OpenAI."),
        Map.entry("2", "Clase 2: Arquitectura multi-proveedor y APIs REST."),
        Map.entry("3", "Clase 3: Prompt engineering y salida estructurada."),
        Map.entry("4", "Clase 4: Memoria en chatbots y multi-sesion."),
        Map.entry("5", "Clase 5: Embeddings y busqueda semantica."),
        Map.entry("6", "Clase 6: RAG completo en Java y Spring AI."),
        Map.entry("7", "Clase 7: Tool calling con Spring AI y LangChain4j."),
        Map.entry("8", "Clase 8: AI Agents y patron ReAct."),
        Map.entry("9", "Clase 9: Arquitectura completa y streaming SSE."),
        Map.entry("10", "Clase 10: Proyecto final integrador."),
        Map.entry("11", "Clase 11: MCP avanzado con @McpResource, @McpPrompt y consumidor SSE.")
    );

    private static final Map<String, String> MODULOS = Map.of(
        "fase1", "Introduccion a LLMs y llamadas a APIs.",
        "fase2", "Prompt engineering y clasificacion.",
        "fase3", "Memoria, sesiones y chat persistente.",
        "fase4", "RAG y tool calling.",
        "fase5", "MCP: proveedor y consumidor de recursos/prompts."
    );

    @McpResource(
        uri = "curso://cronograma/{clase}",
        name = "Cronograma por clase",
        description = "Devuelve el temario de una clase especifica del curso"
    )
    public ReadResourceResult cronogramaPorClase(ReadResourceRequest request, String clase) {
        String contenido = CRONOGRAMA.getOrDefault(clase, "Clase no encontrada en el cronograma.");

        return new ReadResourceResult(
            List.of(new TextResourceContents(request.uri(), "text/plain", contenido))
        );
    }

    @McpResource(
        uri = "curso://modulo/{fase}",
        name = "Resumen por modulo",
        description = "Devuelve el resumen de una fase del curso"
    )
    public String resumenModulo(String fase) {
        return MODULOS.getOrDefault(
            fase.toLowerCase(Locale.ROOT),
            "Modulo no encontrado. Prueba con fase1..fase5."
        );
    }
}

