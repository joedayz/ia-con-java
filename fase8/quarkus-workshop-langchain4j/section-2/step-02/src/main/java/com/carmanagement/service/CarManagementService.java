package com.carmanagement.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.carmanagement.agentic.workflow.CarProcessingWorkflow;
import com.carmanagement.model.CarConditions;
import com.carmanagement.model.CarInfo;
import com.carmanagement.model.CarStatus;

/**
 * Service for managing car returns from various operations.
 */
@ApplicationScoped
public class CarManagementService {
    @Inject
    CarProcessingWorkflow carProcessingWorkflow;

    /**
     * Process a car return from any operation.
     *
     * @param carNumber The car number
     * @param feedback Optional feedback
     * @return Result of the processing
     */
    @Transactional
    public String processCarReturn(Integer carNumber, String feedback) {
        CarInfo carInfo = CarInfo.findById(carNumber);
        if (carInfo == null) {
            return "Car not found with number: " + carNumber;
        }

        // Process the car return using the workflow
        CarConditions carConditions = carProcessingWorkflow.processCarReturn(carInfo, carNumber, feedback);

        // Update the car's condition with the result from CarConditionFeedbackAgent
        carInfo.condition = carConditions.generalCondition();

        // Set the status explicitly here. The CleaningTool runs on a separate
        // (parallel) thread/transaction, so any status it writes would be
        // overwritten when this transaction flushes the stale carInfo entity.
        if (carConditions.cleaningRequired()) {
            carInfo.status = CarStatus.AT_CLEANING;
        } else {
            carInfo.status = CarStatus.AVAILABLE;
        }

        carInfo.persist();

        return carConditions.generalCondition();
    }
}
