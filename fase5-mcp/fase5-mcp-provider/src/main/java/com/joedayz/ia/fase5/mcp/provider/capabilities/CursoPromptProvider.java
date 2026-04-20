package com.joedayz.ia.fase5.mcp.provider.capabilities;

import com.logaritex.mcp.annotation.McpArg;
import com.logaritex.mcp.annotation.McpPrompt;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.Role;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CursoPromptProvider {

    @McpPrompt(
        name = "actividad-fase5",
        description = "Genera un prompt para una actividad practica de la fase 5"
    )
    public GetPromptResult actividadFase5(
        @McpArg(name = "tema", description = "Tema principal", required = true) String tema,
        @McpArg(name = "nivel", description = "Nivel: basico, intermedio o avanzado", required = false)
        String nivel
    ) {
        String nivelFinal = (nivel == null || nivel.isBlank()) ? "intermedio" : nivel;

        String contenido = "Crea una actividad practica para " + tema + " en nivel " + nivelFinal + ". "
            + "Incluye objetivo, pasos, criterio de evaluacion y una extension opcional.";

        return new GetPromptResult(
            "Actividad Fase 5",
            List.of(new PromptMessage(Role.USER, new TextContent(contenido)))
        );
    }
}

