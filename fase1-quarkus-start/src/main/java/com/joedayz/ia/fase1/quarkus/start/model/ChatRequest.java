package com.joedayz.ia.fase1.quarkus.start.model;

import java.util.List;

/**
 * Request que enviaremos a la API de IA (OpenAI o Anthropic).
 * 
 * Ambas APIs usan un formato similar:
 * {
 *   "model": "gpt-3.5-turbo",
 *   "messages": [{"role": "user", "content": "..."}],
 *   "max_tokens": 500
 * }
 */
public record ChatRequest(
    // TODO: Agregar campo 'model' de tipo String
    // TODO: Agregar campo 'messages' de tipo List<Message>
    // TODO: Agregar campo 'max_tokens' de tipo Integer (usar nombre maxTokens)
) {
    
    // NOTA: Jackson automáticamente convertirá maxTokens a max_tokens en el JSON
    // usando la anotación @JsonProperty o naming strategy
}
