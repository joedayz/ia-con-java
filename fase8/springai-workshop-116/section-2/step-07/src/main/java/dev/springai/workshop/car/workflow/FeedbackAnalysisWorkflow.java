package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.FeedbackAnalysisAgent;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackAnalysisResults;
import dev.springai.workshop.car.domain.FeedbackTask;
import dev.springai.workshop.agentic.ParallelizationWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Análisis paralelo con {@link ParallelizationWorkflow#parallelExecute}.
 */
@Service
public class FeedbackAnalysisWorkflow {

    private static final Logger log = LoggerFactory.getLogger(FeedbackAnalysisWorkflow.class);

    private final FeedbackAnalysisAgent feedbackAnalysisAgent;
    private final ParallelizationWorkflow parallelizationWorkflow;

    public FeedbackAnalysisWorkflow(
            FeedbackAnalysisAgent feedbackAnalysisAgent,
            ParallelizationWorkflow parallelizationWorkflow) {
        this.feedbackAnalysisAgent = feedbackAnalysisAgent;
        this.parallelizationWorkflow = parallelizationWorkflow;
    }

    public FeedbackAnalysisResults analyzeFeedback(
            List<FeedbackTask> tasks,
            CarInfo carInfo,
            Integer carNumber,
            String feedback) {
        log.info("FeedbackAnalysisWorkflow executing {} tasks (ParallelizationWorkflow)...", tasks.size());

        List<Callable<String>> callables = tasks.stream()
                .<Callable<String>>map(task -> () -> feedbackAnalysisAgent.analyzeFeedback(
                        task, carInfo, carNumber, feedback))
                .toList();

        List<String> results = parallelizationWorkflow.parallelExecute(callables, tasks.size());

        return new FeedbackAnalysisResults(results.get(0), results.get(1), results.get(2));
    }
}
