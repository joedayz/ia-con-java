package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;

/**
 * Procesamiento de devolución de vehículo (equiv. {@code CarProcessingWorkflow} Quarkus step-02).
 */
public interface CarProcessingWorkflow {

    CarConditions processCarReturn(CarInfo carInfo, Integer carNumber, String feedback);
}
