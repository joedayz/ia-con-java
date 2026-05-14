package com.joedayz.ia.springai.tools.function;

/**
 * Respuesta de la función obtenerClima.
 */
public record ClimaResponse(
        String ciudad,
        String temperatura,
        String condicion,
        String humedad
) {}
