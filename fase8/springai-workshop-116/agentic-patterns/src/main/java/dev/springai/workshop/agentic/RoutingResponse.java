package dev.springai.workshop.agentic;

/**
 * Respuesta estructurada del clasificador de {@link RoutingWorkflow}.
 */
public record RoutingResponse(String reasoning, String selection) {
}
