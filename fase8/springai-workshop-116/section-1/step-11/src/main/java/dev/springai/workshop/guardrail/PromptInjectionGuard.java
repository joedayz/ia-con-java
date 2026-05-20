package dev.springai.workshop.guardrail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Input guardrail (equivalente a {@code InputGuardrail} en Quarkus).
 */
@Component
public class PromptInjectionGuard {

    private final PromptInjectionDetectionService detectionService;
    private final double threshold;

    public PromptInjectionGuard(
            PromptInjectionDetectionService detectionService,
            @Value("${app.guardrail.prompt-injection.threshold:0.7}") double threshold) {
        this.detectionService = detectionService;
        this.threshold = threshold;
    }

    public void validate(String userMessage) {
        double score = detectionService.isInjection(userMessage);
        if (score > threshold) {
            throw new PromptInjectionBlockedException("Prompt injection detected");
        }
    }
}
