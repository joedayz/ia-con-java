package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarImageInput;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackAnalysisResults;
import dev.springai.workshop.car.domain.FeedbackTask;

import java.util.List;

public record FleetProcessingContext(
        List<FeedbackTask> tasks,
        CarInfo carInfo,
        Integer carNumber,
        String feedback,
        CarImageInput carImage,
        String workingFeedback,
        FeedbackAnalysisResults analysisResults,
        String supervisorDecision,
        CarConditions carConditions) {

    public static FleetProcessingContext initial(
            List<FeedbackTask> tasks,
            CarInfo carInfo,
            Integer carNumber,
            String feedback,
            CarImageInput carImage) {
        return new FleetProcessingContext(
                tasks, carInfo, carNumber, feedback, carImage, feedback, null, null, null);
    }

    public FleetProcessingContext withWorkingFeedback(String workingFeedback) {
        return new FleetProcessingContext(
                tasks, carInfo, carNumber, feedback, carImage, workingFeedback, analysisResults, supervisorDecision, carConditions);
    }

    public FleetProcessingContext withAnalysis(FeedbackAnalysisResults analysisResults) {
        return new FleetProcessingContext(
                tasks, carInfo, carNumber, feedback, carImage, workingFeedback, analysisResults, supervisorDecision, carConditions);
    }

    public FleetProcessingContext withSupervisorDecision(String supervisorDecision) {
        return new FleetProcessingContext(
                tasks, carInfo, carNumber, feedback, carImage, workingFeedback, analysisResults, supervisorDecision, carConditions);
    }

    public FleetProcessingContext withCarConditions(CarConditions carConditions) {
        return new FleetProcessingContext(
                tasks, carInfo, carNumber, feedback, carImage, workingFeedback, analysisResults, supervisorDecision, carConditions);
    }
}
