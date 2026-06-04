package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CarConditionFeedbackAgent;
import dev.springai.workshop.car.domain.CarAssignment;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Workflow secuencial: Feedback → asignación condicional → condición (equiv. {@code @SequenceAgent}).
 */
@Service
public class CarProcessingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CarProcessingWorkflow.class);

    private final FeedbackWorkflow feedbackWorkflow;
    private final CarAssignmentWorkflow carAssignmentWorkflow;
    private final CarConditionFeedbackAgent carConditionFeedbackAgent;

    public CarProcessingWorkflow(FeedbackWorkflow feedbackWorkflow,
                                 CarAssignmentWorkflow carAssignmentWorkflow,
                                 CarConditionFeedbackAgent carConditionFeedbackAgent) {
        this.feedbackWorkflow = feedbackWorkflow;
        this.carAssignmentWorkflow = carAssignmentWorkflow;
        this.carConditionFeedbackAgent = carConditionFeedbackAgent;
    }

    public CarConditions processCarReturn(CarInfo carInfo, Integer carNumber, String feedback) {
        FeedbackWorkflow.FeedbackResult feedbackResult =
                feedbackWorkflow.analyzeFeedback(carInfo, carNumber, feedback);

        carAssignmentWorkflow.processAction(
                carInfo, carNumber,
                feedbackResult.cleaningRequest(),
                feedbackResult.maintenanceRequest());

        log.info("CarConditionFeedbackAgent updating...");
        String carCondition = carConditionFeedbackAgent.analyzeForCondition(
                carInfo, carNumber,
                feedbackResult.cleaningRequest(),
                feedbackResult.maintenanceRequest());

        return toCarConditions(carCondition, feedbackResult.cleaningRequest(), feedbackResult.maintenanceRequest());
    }

    private static CarConditions toCarConditions(String carCondition, String cleaningRequest, String maintenanceRequest) {
        CarAssignment assignment;
        if (ActionRequired.isRequired(maintenanceRequest)) {
            assignment = CarAssignment.MAINTENANCE;
        } else if (ActionRequired.isRequired(cleaningRequest)) {
            assignment = CarAssignment.CLEANING;
        } else {
            assignment = CarAssignment.NONE;
        }
        return new CarConditions(carCondition, assignment);
    }
}
