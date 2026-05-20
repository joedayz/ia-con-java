package dev.springai.workshop.car.domain;

public record FeedbackAnalysisResults(
        String cleaningAnalysis,
        String maintenanceAnalysis,
        String dispositionAnalysis) {
}
