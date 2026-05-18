package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CleaningFeedbackAgent;
import dev.springai.workshop.car.agent.MaintenanceFeedbackAgent;
import dev.springai.workshop.car.domain.CarInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Workflow paralelo: CleaningFeedbackAgent + MaintenanceFeedbackAgent (equiv. {@code @ParallelAgent}).
 */
@Service
public class FeedbackWorkflow {

    private static final Logger log = LoggerFactory.getLogger(FeedbackWorkflow.class);

    public record FeedbackResult(String cleaningRequest, String maintenanceRequest) {
    }

    private final CleaningFeedbackAgent cleaningFeedbackAgent;
    private final MaintenanceFeedbackAgent maintenanceFeedbackAgent;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public FeedbackWorkflow(CleaningFeedbackAgent cleaningFeedbackAgent,
                            MaintenanceFeedbackAgent maintenanceFeedbackAgent) {
        this.cleaningFeedbackAgent = cleaningFeedbackAgent;
        this.maintenanceFeedbackAgent = maintenanceFeedbackAgent;
    }

    public FeedbackResult analyzeFeedback(CarInfo carInfo, Integer carNumber, String feedback) {
        log.info("FeedbackWorkflow executing...");
        log.info("  ├─ CleaningFeedbackAgent analyzing...");
        log.info("  └─ MaintenanceFeedbackAgent analyzing...");

        CompletableFuture<String> cleaningFuture = CompletableFuture.supplyAsync(
                () -> cleaningFeedbackAgent.analyzeForCleaning(carInfo, carNumber, feedback), executor);
        CompletableFuture<String> maintenanceFuture = CompletableFuture.supplyAsync(
                () -> maintenanceFeedbackAgent.analyzeForMaintenance(carInfo, carNumber, feedback), executor);

        CompletableFuture.allOf(cleaningFuture, maintenanceFuture).join();

        return new FeedbackResult(cleaningFuture.join(), maintenanceFuture.join());
    }
}
