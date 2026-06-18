package com.carmanagement.agentic.agents;

import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackAnalysisResults;
import com.carmanagement.service.FleetSupervisorCoordinationBridge;
import dev.langchain4j.agentic.Agent;

/**
 * Deterministic fleet orchestration executor invoked by the supervisor.
 * Implemented as a concrete class so LangChain4j can register the static @Agent method.
 */
public final class FleetSupervisorExecutor {

    private FleetSupervisorExecutor() {
    }

    @Agent(
            outputKey = "supervisorDecision",
            description = "Executes fleet orchestration with value-based Human-in-the-Loop routing")
    public static String execute(
            CarInfo carInfo,
            Integer carNumber,
            String feedback,
            FeedbackAnalysisResults feedbackAnalysisResults) {

        return FleetSupervisorCoordinationBridge.supervise(
                carInfo, carNumber, feedback, feedbackAnalysisResults);
    }
}
