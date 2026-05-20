package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackAnalysisResults;
import dev.springai.workshop.car.domain.FeedbackTask;

import java.util.List;

/** Estado para {@link dev.springai.workshop.agentic.ChainWorkflow#chainSteps} en steps 04+. */
public record FleetProcessingContext(
        List<FeedbackTask> tasks,
        CarInfo carInfo,
        Integer carNumber,
        String feedback,
        FeedbackAnalysisResults analysisResults,
        String supervisorDecision,
        CarConditions carConditions) {

    public static FleetProcessingContext initial(
            List<FeedbackTask> tasks, CarInfo carInfo, Integer carNumber, String feedback) {
        return new FleetProcessingContext(tasks, carInfo, carNumber, feedback, null, null, null);
    }

    public FleetProcessingContext withAnalysis(FeedbackAnalysisResults analysisResults) {
        return new FleetProcessingContext(tasks, carInfo, carNumber, feedback, analysisResults, supervisorDecision, carConditions);
    }

    public FleetProcessingContext withSupervisorDecision(String supervisorDecision) {
        return new FleetProcessingContext(tasks, carInfo, carNumber, feedback, analysisResults, supervisorDecision, carConditions);
    }

    public FleetProcessingContext withCarConditions(CarConditions carConditions) {
        return new FleetProcessingContext(tasks, carInfo, carNumber, feedback, analysisResults, supervisorDecision, carConditions);
    }
}
