package com.joedayz.ia.langchain4j.tools.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud de chat con tool calling")
public record ChatRequest(
        @Schema(description = "Mensaje del usuario",
                example = "¿Cuánto es 125 multiplicado por 37?")
        String message
) {}
