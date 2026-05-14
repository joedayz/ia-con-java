package com.joedayz.ia.springai.tools.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Request para la función obtenerClima.
 * Las anotaciones de Jackson ayudan al LLM a entender el esquema de la función.
 */
public record ClimaRequest(
        @JsonProperty(required = true)
        @JsonPropertyDescription("Nombre de la ciudad para consultar el clima, por ejemplo: Lima, Madrid, Bogotá")
        String ciudad
) {}
