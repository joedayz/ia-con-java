package com.carmanagement.agentic.agents;

import com.carmanagement.service.HumanApprovalService;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.declarative.HumanInTheLoop;
import io.quarkus.arc.Arc;

public interface HumanApprovalAgent {

    @Agent(outputKey = "approvalDecision", description = "Coordinates human approval for high-value vehicle dispositions using the requestHumanApproval tool")
    @HumanInTheLoop(outputKey = "approvalDecision", description = "Coordinates human approval for high-value vehicle dispositions using the requestHumanApproval tool")
    static String reviewDispositionProposal(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String carValue,
            String dispositionProposal,
            String carCondition,
            String feedback
    ) {
        return Arc.container().instance(HumanApprovalService.class).get()
                .reviewDispositionProposal(
                        carMake, carModel, carYear, carNumber,
                        carValue, dispositionProposal, carCondition, feedback);
    }
}
