package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CarConditionFeedbackAgent;
import dev.springai.workshop.car.agent.CleaningAgent;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Workflow paralelo: CleaningAgent + CarConditionFeedbackAgent (equiv. {@code @ParallelAgent} Quarkus step-02).
 */
@Service
public class CarProcessingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CarProcessingWorkflow.class);

    private final CleaningAgent cleaningAgent;
    private final CarConditionFeedbackAgent carConditionFeedbackAgent;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public CarProcessingWorkflow(CleaningAgent cleaningAgent,
                                 CarConditionFeedbackAgent carConditionFeedbackAgent) {
        this.cleaningAgent = cleaningAgent;
        this.carConditionFeedbackAgent = carConditionFeedbackAgent;
    }

    public CarConditions processCarReturn(CarInfo carInfo, Integer carNumber, String feedback) {
        log.info("Starting parallel workflow: CleaningAgent + CarConditionFeedbackAgent (car #{})", carNumber);

        CompletableFuture<String> cleaningFuture = CompletableFuture.supplyAsync(
                () -> cleaningAgent.processCleaning(carInfo, carNumber, feedback), executor);
        CompletableFuture<String> conditionFuture = CompletableFuture.supplyAsync(
                () -> carConditionFeedbackAgent.analyzeForCondition(carInfo, carNumber, feedback), executor);

        CompletableFuture.allOf(cleaningFuture, conditionFuture).join();

        String cleaningAgentResult = cleaningFuture.join();
        String carCondition = conditionFuture.join();
        log.info("[CarConditionFeedbackAgent response]: {}", carCondition);
        log.debug("CleaningAgent result: {}", cleaningAgentResult);
        boolean cleaningRequired = !cleaningAgentResult.toUpperCase().contains("NOT_REQUIRED");
        return new CarConditions(carCondition, cleaningRequired);
    }
}
