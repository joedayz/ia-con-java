package com.joedayz.ia.springai.tools.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud de chat con tool calling")
public record ChatRequest(
        @Schema(description = "Mensaje del usuario",
                example = "¿Cómo está el clima en Lima?")
        String message
) {}
