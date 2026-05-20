package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CarConditionFeedbackAgent;
import dev.springai.workshop.car.agent.CleaningAgent;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.agentic.ParallelizationWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Workflow paralelo con {@link ParallelizationWorkflow} (patrón Parallelization).
 */
@Service
public class CarProcessingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CarProcessingWorkflow.class);

    private final CleaningAgent cleaningAgent;
    private final CarConditionFeedbackAgent carConditionFeedbackAgent;
    private final ParallelizationWorkflow parallelizationWorkflow;

    public CarProcessingWorkflow(
            CleaningAgent cleaningAgent,
            CarConditionFeedbackAgent carConditionFeedbackAgent,
            ParallelizationWorkflow parallelizationWorkflow) {
        this.cleaningAgent = cleaningAgent;
        this.carConditionFeedbackAgent = carConditionFeedbackAgent;
        this.parallelizationWorkflow = parallelizationWorkflow;
    }

    public CarConditions processCarReturn(CarInfo carInfo, Integer carNumber, String feedback) {
        log.info("Starting parallel workflow (ParallelizationWorkflow, car #{})", carNumber);

        List<String> results = parallelizationWorkflow.parallelExecute(
                List.of(
                        (Callable<String>) () -> cleaningAgent.processCleaning(carInfo, carNumber, feedback),
                        (Callable<String>) () -> carConditionFeedbackAgent.analyzeForCondition(
                                carInfo, carNumber, feedback)),
                2);

        String cleaningAgentResult = results.get(0);
        String carCondition = results.get(1);
        log.info("[CarConditionFeedbackAgent response]: {}", carCondition);
        log.debug("CleaningAgent result: {}", cleaningAgentResult);
        boolean cleaningRequired = !cleaningAgentResult.toUpperCase().contains("NOT_REQUIRED");
        return new CarConditions(carCondition, cleaningRequired);
    }
}
