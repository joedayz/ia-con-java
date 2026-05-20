package dev.springai.workshop.car.domain;

public record CarConditions(
        String generalCondition,
        CarAssignment carAssignment,
        String dispositionStatus,
        String dispositionReason) {

    public CarConditions(String generalCondition, CarAssignment carAssignment) {
        this(generalCondition, carAssignment, "DISPOSITION_NOT_REQUIRED", null);
    }
}
