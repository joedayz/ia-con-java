package com.joedayz.ia.springai.tools.function;

/**
 * Respuesta de la función consultarPais con datos reales de restcountries.com.
 */
public record PaisResponse(
        String nombre,
        String capital,
        String region,
        long poblacion,
        String idiomas
) {}
