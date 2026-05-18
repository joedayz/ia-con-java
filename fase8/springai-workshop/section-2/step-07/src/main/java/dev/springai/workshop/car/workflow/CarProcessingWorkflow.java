package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CarConditionFeedbackAgent;
import dev.springai.workshop.car.agent.CarImageAnalysisAgent;
import dev.springai.workshop.car.agent.FleetSupervisorAgent;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarImageInput;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Secuencia: imagen → análisis feedback → supervisor → condición (step-06 añade multimodal al inicio).
 */
@Service
public class CarProcessingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CarProcessingWorkflow.class);

    private final CarImageAnalysisAgent carImageAnalysisAgent;
    private final FeedbackAnalysisWorkflow feedbackAnalysisWorkflow;
    private final FleetSupervisorAgent fleetSupervisorAgent;
    private final CarConditionFeedbackAgent carConditionFeedbackAgent;

    public CarProcessingWorkflow(
            CarImageAnalysisAgent carImageAnalysisAgent,
            FeedbackAnalysisWorkflow feedbackAnalysisWorkflow,
            FleetSupervisorAgent fleetSupervisorAgent,
            CarConditionFeedbackAgent carConditionFeedbackAgent) {
        this.carImageAnalysisAgent = carImageAnalysisAgent;
        this.feedbackAnalysisWorkflow = feedbackAnalysisWorkflow;
        this.fleetSupervisorAgent = fleetSupervisorAgent;
        this.carConditionFeedbackAgent = carConditionFeedbackAgent;
    }

    public CarConditions processCarReturn(
            List<FeedbackTask> tasks,
            CarInfo carInfo,
            Integer carNumber,
            String feedback,
            CarImageInput carImage) {

        String enrichedFeedback = carImageAnalysisAgent.analyzeCarImage(feedback, carImage);
        log.debug("Enriched feedback: {}", enrichedFeedback);

        var analysisResults = feedbackAnalysisWorkflow.analyzeFeedback(
                tasks, carInfo, carNumber, enrichedFeedback);

        String supervisorDecision = fleetSupervisorAgent.superviseCarProcessing(
                carInfo, carNumber, enrichedFeedback, analysisResults);

        log.info("CarConditionFeedbackAgent updating...");
        CarConditions carConditions = carConditionFeedbackAgent.analyzeForCondition(
                carInfo, carNumber, analysisResults, supervisorDecision);

        log.debug("CarConditions: {} → {} ({})",
                carConditions.generalCondition(),
                carConditions.carAssignment(),
                carConditions.dispositionStatus());
        return carConditions;
    }
}
