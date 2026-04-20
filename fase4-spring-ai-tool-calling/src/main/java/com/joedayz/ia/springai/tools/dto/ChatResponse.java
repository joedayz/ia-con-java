package com.joedayz.ia.springai.tools.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del chat con tool calling")
public record ChatResponse(
        @Schema(description = "Respuesta del asistente")
        String response
) {}
