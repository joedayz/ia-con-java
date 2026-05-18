package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackAnalysisResults;

final class FleetSupervisorRequestBuilder {

    private FleetSupervisorRequestBuilder() {
    }

    static String build(
            CarInfo carInfo,
            Integer carNumber,
            String feedback,
            FeedbackAnalysisResults feedbackAnalysisResults) {
        boolean dispositionRequired = feedbackAnalysisResults.dispositionAnalysis() != null
                && feedbackAnalysisResults.dispositionAnalysis().toUpperCase().contains("DISPOSITION_REQUIRED");

        String noDispositionMessage = """
            Disposition is not required.
            Proceed with normal maintenance and cleaning workflow.
            If cleaning or maintenance is required, invoke the appropriate agents.
                """;

        String dispositionMessage = """
           DISPOSITION_REQUIRED

           Follow these steps:

           1. Get value from invokePricingAgent (keep $ format)
           2. IF value > $15,000 (HIGH-VALUE):
              - Invoke invokeDispositionProposalAgent then invokeHumanApprovalAgent (workflow pauses)
              - Human decision in approvalReason contains KEEP_CAR or DISPOSE_CAR
           3. IF value ≤ $15,000 (LOW-VALUE):
              - Invoke invokeDispositionAgent directly
              - KEEP→end with KEEP_CAR, SCRAP/SELL/DONATE→end with DISPOSE_CAR
           4. IF outcome is KEEP_CAR: invoke maintenance/cleaning agents as needed

           CRITICAL: End your final summary with KEEP_CAR or DISPOSE_CAR
           """;

        String condition = carInfo.getCondition() != null ? carInfo.getCondition() : "Unknown";

        return """
            You are a fleet supervisor for a car rental company. You coordinate action agents based on feedback analysis.

            The feedback has already been analyzed and you have these inputs:
            - cleaningAnalysis: What cleaning is needed (or "CLEANING_NOT_REQUIRED")
            - maintenanceAnalysis: What maintenance is needed (or "MAINTENANCE_NOT_REQUIRED")
            - dispositionAnalysis: Whether severe damage requires disposition (or "DISPOSITION_NOT_REQUIRED")

            Your job is to invoke the appropriate ACTION agents for this car using the available tools.

            Car: %d %s %s (#%d)
            Current Condition: %s
            Feedback: %s

            Cleaning Analysis: %s
            Maintenance Analysis: %s
            Disposition Analysis: %s

            In particular, you have to follow these steps:

            %s
            """.formatted(
                carInfo.getYear(), carInfo.getMake(), carInfo.getModel(), carNumber, condition, feedback,
                feedbackAnalysisResults.cleaningAnalysis(),
                feedbackAnalysisResults.maintenanceAnalysis(),
                feedbackAnalysisResults.dispositionAnalysis(),
                dispositionRequired ? dispositionMessage : noDispositionMessage);
    }
}
