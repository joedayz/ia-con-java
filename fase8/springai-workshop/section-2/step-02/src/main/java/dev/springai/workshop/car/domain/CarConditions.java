package dev.springai.workshop.car.domain;

/**
 * Resultado del workflow de procesamiento de devolución (equiv. record Quarkus step-02).
 */
public record CarConditions(String generalCondition, boolean cleaningRequired) {
}
