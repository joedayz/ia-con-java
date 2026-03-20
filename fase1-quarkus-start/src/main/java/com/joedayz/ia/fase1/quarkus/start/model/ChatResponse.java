package com.joedayz.ia.fase1.quarkus.start.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Response que recibimos de la API de IA.
 * 
 * OpenAI responde:
 * {
 *   "choices": [{
 *     "message": {"role": "assistant", "content": "..."}
 *   }],
 *   "usage": {"total_tokens": 123}
 * }
 * 
 * Anthropic responde:
 * {
 *   "content": [{
 *     "text": "..."
 *   }],
 *   "usage": {"input_tokens": 10, "output_tokens": 20}
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignorar campos que no mapeamos
public record ChatResponse(
    // TODO: Agregar campos según el proveedor
    // Para OpenAI: List<Choice> choices
    // Para Anthropic: List<Content> content
    // Usage usage (común para ambos)
) {
    
    // TODO: Crear record interno Choice para OpenAI
    // @JsonIgnoreProperties(ignoreUnknown = true)
    // public record Choice(Message message) {}
    
    // TODO: Crear record interno Content para Anthropic
    // @JsonIgnoreProperties(ignoreUnknown = true)
    // public record Content(String text) {}
    
    // TODO: Crear record interno Usage
    // @JsonIgnoreProperties(ignoreUnknown = true)
    // public record Usage(Integer totalTokens, Integer inputTokens, Integer outputTokens) {}
    
    // TODO: Crear método para extraer contenido según el proveedor
    // public String getContent(String provider) {
    //     if ("openai".equals(provider)) {
    //         return choices != null && !choices.isEmpty() 
    //             ? choices.get(0).message().content() 
    //             : "";
    //     } else if ("anthropic".equals(provider)) {
    //         return content != null && !content.isEmpty() 
    //             ? content.get(0).text() 
    //             : "";
    //     }
    //     return "";
    // }
}
