package com.joedayz.ia.fase1.quarkus.start.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

/**
 * Configuración type-safe para Ollama.
 */
@ConfigMapping(prefix = "ia")
public interface IAConfig {

    @WithName("ollama")
    OllamaConfig ollama();

    interface OllamaConfig {
        String base();
        String model();
        @WithName("max-tokens")
        Integer maxTokens();
        String timeout();
    }
}
