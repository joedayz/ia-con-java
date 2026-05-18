package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.FeedbackAnalysisAgent;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackAnalysisResults;
import dev.springai.workshop.car.domain.FeedbackTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Análisis paralelo con el mismo agente y distintas tareas (equiv. {@code @ParallelMapperAgent}).
 */
@Service
public class FeedbackAnalysisWorkflow {

    private static final Logger log = LoggerFactory.getLogger(FeedbackAnalysisWorkflow.class);

    private final FeedbackAnalysisAgent feedbackAnalysisAgent;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public FeedbackAnalysisWorkflow(FeedbackAnalysisAgent feedbackAnalysisAgent) {
        this.feedbackAnalysisAgent = feedbackAnalysisAgent;
    }

    public FeedbackAnalysisResults analyzeFeedback(
            List<FeedbackTask> tasks,
            CarInfo carInfo,
            Integer carNumber,
            String feedback) {
        log.info("FeedbackAnalysisWorkflow executing {} tasks in parallel...", tasks.size());

        List<CompletableFuture<String>> futures = tasks.stream()
                .map(task -> CompletableFuture.supplyAsync(
                        () -> feedbackAnalysisAgent.analyzeFeedback(task, carInfo, carNumber, feedback),
                        executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        return new FeedbackAnalysisResults(
                futures.get(0).join(),
                futures.get(1).join(),
                futures.get(2).join());
    }
}
