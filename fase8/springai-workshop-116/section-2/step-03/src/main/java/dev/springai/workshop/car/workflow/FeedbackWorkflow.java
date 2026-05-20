package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CleaningFeedbackAgent;
import dev.springai.workshop.car.agent.MaintenanceFeedbackAgent;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.agentic.ParallelizationWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Workflow paralelo con {@link ParallelizationWorkflow#parallelExecute} (patrón Parallelization).
 */
@Service
public class FeedbackWorkflow {

    private static final Logger log = LoggerFactory.getLogger(FeedbackWorkflow.class);

    public record FeedbackResult(String cleaningRequest, String maintenanceRequest) {
    }

    private final CleaningFeedbackAgent cleaningFeedbackAgent;
    private final MaintenanceFeedbackAgent maintenanceFeedbackAgent;
    private final ParallelizationWorkflow parallelizationWorkflow;

    public FeedbackWorkflow(
            CleaningFeedbackAgent cleaningFeedbackAgent,
            MaintenanceFeedbackAgent maintenanceFeedbackAgent,
            ParallelizationWorkflow parallelizationWorkflow) {
        this.cleaningFeedbackAgent = cleaningFeedbackAgent;
        this.maintenanceFeedbackAgent = maintenanceFeedbackAgent;
        this.parallelizationWorkflow = parallelizationWorkflow;
    }

    public FeedbackResult analyzeFeedback(CarInfo carInfo, Integer carNumber, String feedback) {
        log.info("FeedbackWorkflow executing (ParallelizationWorkflow)...");

        List<Callable<String>> tasks = List.of(
                () -> {
                    log.info("  ├─ CleaningFeedbackAgent analyzing...");
                    return cleaningFeedbackAgent.analyzeForCleaning(carInfo, carNumber, feedback);
                },
                () -> {
                    log.info("  └─ MaintenanceFeedbackAgent analyzing...");
                    return maintenanceFeedbackAgent.analyzeForMaintenance(carInfo, carNumber, feedback);
                });

        List<String> results = parallelizationWorkflow.parallelExecute(tasks, 2);
        return new FeedbackResult(results.get(0), results.get(1));
    }
}
