package dev.springai.workshop.guardrail;

/**
 * Equivalente a {@code InputGuardrailException} en Quarkus cuando falla el guardrail.
 */
public class PromptInjectionBlockedException extends RuntimeException {

    public PromptInjectionBlockedException(String message) {
        super(message);
    }
}
