package com.joedayz.ia.fase5.mcp.provider.capabilities;

import java.util.Locale;
import java.util.Map;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class CursoToolProvider {

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

    @Tool(description = "Consulta el cronograma del curso por numero de clase")
    public String consultarCronograma(String clase) {
        return CRONOGRAMA.getOrDefault(clase, "Clase no encontrada en el cronograma.");
    }

    @Tool(description = "Consulta un resumen rapido por modulo (fase1, fase2, fase3, fase4 o fase5)")
    public String consultarModulo(String fase) {
        return MODULOS.getOrDefault(
            fase.toLowerCase(Locale.ROOT),
            "Modulo no encontrado. Prueba con fase1..fase5."
        );
    }

    @Tool(description = "Genera una actividad para fase5 en base a tema y nivel")
    public String generarActividadFase5(String tema, String nivel) {
        String nivelFinal = (nivel == null || nivel.isBlank()) ? "intermedio" : nivel;
        return "Actividad sugerida para fase5. Tema: " + tema + ", nivel: " + nivelFinal + ". "
            + "Objetivo: integrar proveedor/consumidor MCP. Pasos: 1) definir @McpResource y @McpPrompt, "
            + "2) registrar specs, 3) consumir por SSE con ChatClient. Evaluacion: endpoint funcional y demo en Swagger.";
    }
}

