package com.joedayz.ia.fase1.quarkus.config;

import io.smallrye.config.ConfigMapping;

import java.util.Optional;

/**
 * Configuración de la API de OpenAI desde application.properties
 */
@ConfigMapping(prefix = "openai.api")
public interface OpenAIConfig {
    
    /**
     * API Key de OpenAI (obligatoria)
     */
    Optional<String> key();
    
    /**
     * URL base de la API
     */
    String base();
    
    /**
     * Modelo a utilizar (ej: gpt-3.5-turbo, gpt-4)
     */
    String model();
    
    /**
     * Máximo de tokens en la respuesta
     */
    Integer maxTokens();
    
    /**
     * Timeout para las peticiones
     */
    String timeout();
}
