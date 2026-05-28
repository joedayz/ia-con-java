package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CarConditionFeedbackAgent;
import dev.springai.workshop.car.agent.CleaningAgent;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Workflow secuencial (por defecto): CleaningAgent → CarConditionFeedbackAgent.
 */
@Service
@ConditionalOnProperty(name = "app.car-workflow.parallel", havingValue = "false", matchIfMissing = true)
public class SequentialCarProcessingWorkflow implements CarProcessingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(SequentialCarProcessingWorkflow.class);

    private final CleaningAgent cleaningAgent;
    private final CarConditionFeedbackAgent carConditionFeedbackAgent;

    public SequentialCarProcessingWorkflow(CleaningAgent cleaningAgent,
                                           CarConditionFeedbackAgent carConditionFeedbackAgent) {
        this.cleaningAgent = cleaningAgent;
        this.carConditionFeedbackAgent = carConditionFeedbackAgent;
    }

    @Override
    public CarConditions processCarReturn(CarInfo carInfo, Integer carNumber, String feedback) {
        log.info("Starting sequential workflow: CleaningAgent → CarConditionFeedbackAgent (car #{})", carNumber);

        String cleaningAgentResult = cleaningAgent.processCleaning(carInfo, carNumber, feedback);
        log.debug("CleaningAgent result: {}", cleaningAgentResult);

        String carCondition = carConditionFeedbackAgent.analyzeForCondition(carInfo, carNumber, feedback);
        log.info("[CarConditionFeedbackAgent response]: {}", carCondition);

        return toCarConditions(carCondition, cleaningAgentResult);
    }

    static CarConditions toCarConditions(String carCondition, String cleaningAgentResult) {
        boolean cleaningRequired = !cleaningAgentResult.toUpperCase().contains("NOT_REQUIRED");
        return new CarConditions(carCondition, cleaningRequired);
    }
}
