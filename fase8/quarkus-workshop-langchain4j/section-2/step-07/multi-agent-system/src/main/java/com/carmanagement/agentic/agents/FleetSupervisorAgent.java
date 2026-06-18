package com.carmanagement.agentic.agents;

import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackAnalysisResults;
import dev.langchain4j.agentic.declarative.SupervisorAgent;
import dev.langchain4j.agentic.declarative.SupervisorRequest;

/**
 * Supervisor agent that orchestrates the entire car processing workflow.
 * Delegates to FleetSupervisorExecutor for deterministic value-based routing
 * and Human-in-the-Loop approval on high-value vehicles.
 */
public interface FleetSupervisorAgent {

    @SupervisorAgent(
        outputKey = "supervisorDecision",
        subAgents = { FleetSupervisorExecutor.class }
    )
    String superviseCarProcessing(
        CarInfo carInfo,
        Integer carNumber,
        String feedback,
        FeedbackAnalysisResults feedbackAnalysisResults
    );

    @SupervisorRequest()
    static String request(
        CarInfo carInfo,
        Integer carNumber,
        String feedback,
        FeedbackAnalysisResults feedbackAnalysisResults
    ) {
        return """
            Invoke FleetSupervisorExecutor exactly once with all provided car data.
            The executor coordinates pricing, disposition proposal, human approval,
            maintenance, and cleaning. Do not invoke any other agents directly.

            Car: """ + carInfo.year + " " + carInfo.make + " " + carInfo.model + " (#" + carNumber + ")" + """

            Current Condition: """ + carInfo.condition + """

            Feedback: """ + feedback + """

            Cleaning Analysis: """ + feedbackAnalysisResults.cleaningAnalysis() + """

            Maintenance Analysis: """ + feedbackAnalysisResults.maintenanceAnalysis() + """

            Disposition Analysis: """ + feedbackAnalysisResults.dispositionAnalysis();
    }
}
