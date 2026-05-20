package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.domain.CarAssignment;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;

/**
 * Estado que recorre la cadena secuencial del workflow de devolución (patrón Chain).
 */
public record CarReturnContext(
        CarInfo carInfo,
        Integer carNumber,
        String feedback,
        FeedbackWorkflow.FeedbackResult feedbackResult,
        String assignmentOutcome,
        CarConditions carConditions) {

    public static CarReturnContext initial(CarInfo carInfo, Integer carNumber, String feedback) {
        return new CarReturnContext(carInfo, carNumber, feedback, null, null, null);
    }

    public CarReturnContext withFeedbackResult(FeedbackWorkflow.FeedbackResult feedbackResult) {
        return new CarReturnContext(carInfo, carNumber, feedback, feedbackResult, assignmentOutcome, carConditions);
    }

    public CarReturnContext withAssignmentOutcome(String assignmentOutcome) {
        return new CarReturnContext(carInfo, carNumber, feedback, feedbackResult, assignmentOutcome, carConditions);
    }

    public CarReturnContext withCarConditions(CarConditions carConditions) {
        return new CarReturnContext(carInfo, carNumber, feedback, feedbackResult, assignmentOutcome, carConditions);
    }

    public CarAssignment resolveAssignment() {
        if (feedbackResult == null) {
            return CarAssignment.NONE;
        }
        if (ActionRequired.isRequired(feedbackResult.maintenanceRequest())) {
            return CarAssignment.MAINTENANCE;
        }
        if (ActionRequired.isRequired(feedbackResult.cleaningRequest())) {
            return CarAssignment.CLEANING;
        }
        return CarAssignment.NONE;
    }
}
