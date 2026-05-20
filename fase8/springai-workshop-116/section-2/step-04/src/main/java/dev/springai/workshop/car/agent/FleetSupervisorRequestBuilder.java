package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackAnalysisResults;

final class FleetSupervisorRequestBuilder {

    private FleetSupervisorRequestBuilder() {
    }

    static String build(CarInfo carInfo, Integer carNumber, FeedbackAnalysisResults feedbackAnalysisResults) {
        boolean dispositionRequired = feedbackAnalysisResults.dispositionAnalysis() != null
                && feedbackAnalysisResults.dispositionAnalysis().toUpperCase().contains("DISPOSITION_REQUIRED");

        String noDispositionMessage = """
               No disposition has been requested.

                INSTRUCTIONS:
                - DO NOT invoke PricingAgent
                - DO NOT invoke DispositionAgent
                - Only invoke MaintenanceAgent if maintenance needed
                - Only invoke CleaningAgent if cleaning needed
               """;

        String dispositionMessage = """
            The car has to be disposed.

            STEP 1: Invoke PricingAgent to get car value
            STEP 2: Invoke DispositionAgent to decide disposition action (SCRAP/SELL/DONATE/KEEP)
            STEP 3: If DispositionAgent decides KEEP:
                    - Invoke MaintenanceAgent if maintenance needed
                    - Invoke CleaningAgent if cleaning needed

            IMPORTANT: When invoking DispositionAgent:
            - Pass carValue as a STRING with dollar sign (e.g., "$10,710" not 10710)
            - Use the EXACT format from PricingAgent's response

            Follow the decision logic in your system message carefully.
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

            Cleaning Analysis: %s
            Maintenance Analysis: %s
            Disposition Analysis: %s

            In particular, you have to follow these steps:

            %s
            """.formatted(
                carInfo.getYear(), carInfo.getMake(), carInfo.getModel(), carNumber, condition,
                feedbackAnalysisResults.cleaningAnalysis(),
                feedbackAnalysisResults.maintenanceAnalysis(),
                feedbackAnalysisResults.dispositionAnalysis(),
                dispositionRequired ? dispositionMessage : noDispositionMessage);
    }
}
