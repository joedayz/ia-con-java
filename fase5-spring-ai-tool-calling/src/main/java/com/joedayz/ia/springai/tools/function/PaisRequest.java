package com.joedayz.ia.springai.tools.function;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Request para la función consultarPais (Reto: API REST real).
 */
public record PaisRequest(
        @JsonProperty(required = true)
        @JsonPropertyDescription("Nombre del país a consultar, por ejemplo: Perú, Colombia, España, Japan")
        String pais
) {}
