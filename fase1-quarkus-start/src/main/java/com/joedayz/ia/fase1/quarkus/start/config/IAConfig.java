package com.joedayz.ia.fase1.quarkus.start.config;

import io.smallrye.config.ConfigMapping;
import java.util.Optional;

/**
 * Configuración type-safe para las APIs de IA.
 * 
 * En clase veremos:
 * 1. Cómo usar @ConfigMapping
 * 2. Cómo leer variables de entorno
 * 3. Cómo organizar configuración por proveedor
 */
@ConfigMapping(prefix = "ia")
public interface IAConfig {
    
    // TODO: Crear interfaz interna para OpenAI
    // OpenAIConfig openai();
    // 
    // interface OpenAIConfig {
    //     Optional<String> key();
    //     String base();
    //     String model();
    //     Integer maxTokens();
    //     String timeout();
    // }
    
    // TODO: Crear interfaz interna para Anthropic
    // AnthropicConfig anthropic();
    // 
    // interface AnthropicConfig {
    //     Optional<String> key();
    //     String base();
    //     String model();
    //     Integer maxTokens();
    //     String timeout();
    //     String version();
    // }
}
