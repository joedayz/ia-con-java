package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CarConditionFeedbackAgent;
import dev.springai.workshop.car.agent.FleetSupervisorAgent;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Secuencia: análisis → supervisor → condición final (equiv. {@code @SequenceAgent}).
 */
@Service
public class CarProcessingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CarProcessingWorkflow.class);

    private final FeedbackAnalysisWorkflow feedbackAnalysisWorkflow;
    private final FleetSupervisorAgent fleetSupervisorAgent;
    private final CarConditionFeedbackAgent carConditionFeedbackAgent;

    public CarProcessingWorkflow(
            FeedbackAnalysisWorkflow feedbackAnalysisWorkflow,
            FleetSupervisorAgent fleetSupervisorAgent,
            CarConditionFeedbackAgent carConditionFeedbackAgent) {
        this.feedbackAnalysisWorkflow = feedbackAnalysisWorkflow;
        this.fleetSupervisorAgent = fleetSupervisorAgent;
        this.carConditionFeedbackAgent = carConditionFeedbackAgent;
    }

    public CarConditions processCarReturn(
            List<FeedbackTask> tasks,
            CarInfo carInfo,
            Integer carNumber,
            String feedback) {

        var analysisResults = feedbackAnalysisWorkflow.analyzeFeedback(tasks, carInfo, carNumber, feedback);

        String supervisorDecision = fleetSupervisorAgent.superviseCarProcessing(
                carInfo, carNumber, analysisResults, feedback);

        log.info("CarConditionFeedbackAgent updating...");
        CarConditions carConditions = carConditionFeedbackAgent.analyzeForCondition(
                carInfo, carNumber, analysisResults, supervisorDecision);

        log.debug("CarConditions: {} → {}", carConditions.generalCondition(), carConditions.carAssignment());
        return carConditions;
    }
}
