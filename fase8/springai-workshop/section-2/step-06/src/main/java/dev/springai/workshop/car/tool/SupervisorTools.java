package dev.springai.workshop.car.tool;

import dev.springai.workshop.car.agent.CleaningAgent;
import dev.springai.workshop.car.agent.DispositionAgent;
import dev.springai.workshop.car.agent.DispositionProposalAgent;
import dev.springai.workshop.car.agent.HumanApprovalAgent;
import dev.springai.workshop.car.agent.MaintenanceAgent;
import dev.springai.workshop.car.agent.PricingAgent;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class SupervisorTools {

    private final PricingAgent pricingAgent;
    private final DispositionProposalAgent dispositionProposalAgent;
    private final HumanApprovalAgent humanApprovalAgent;
    private final DispositionAgent dispositionAgent;
    private final MaintenanceAgent maintenanceAgent;
    private final CleaningAgent cleaningAgent;

    public SupervisorTools(
            PricingAgent pricingAgent,
            DispositionProposalAgent dispositionProposalAgent,
            HumanApprovalAgent humanApprovalAgent,
            DispositionAgent dispositionAgent,
            MaintenanceAgent maintenanceAgent,
            CleaningAgent cleaningAgent) {
        this.pricingAgent = pricingAgent;
        this.dispositionProposalAgent = dispositionProposalAgent;
        this.humanApprovalAgent = humanApprovalAgent;
        this.dispositionAgent = dispositionAgent;
        this.maintenanceAgent = maintenanceAgent;
        this.cleaningAgent = cleaningAgent;
    }

    @Tool(description = "Pricing specialist: estimates vehicle market value")
    public String invokePricingAgent(String carMake, String carModel, Integer carYear, String carCondition) {
        return pricingAgent.estimateValue(carMake, carModel, carYear, carCondition);
    }

    @Tool(description = "Creates a disposition proposal for high-value vehicles (use before human approval)")
    public String invokeDispositionProposalAgent(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String carCondition,
            String carValue,
            String feedback) {
        return dispositionProposalAgent.createDispositionProposal(
                carMake, carModel, carYear, carNumber, carCondition, carValue, feedback);
    }

    @Tool(description = "Pauses workflow until a human approves or rejects via the UI (Human-in-the-Loop)")
    public String invokeHumanApprovalAgent(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String carValue,
            String dispositionProposal,
            String dispositionReason,
            String carCondition,
            String feedback) {
        return humanApprovalAgent.reviewDispositionProposal(
                carMake, carModel, carYear, carNumber, carValue,
                dispositionProposal, dispositionReason, carCondition, feedback);
    }

    @Tool(description = "Disposition specialist for low-value vehicles: SCRAP, SELL, DONATE, or KEEP")
    public String invokeDispositionAgent(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String carCondition,
            String carValue,
            String feedback) {
        return dispositionAgent.processDisposition(
                carMake, carModel, carYear, carNumber, carCondition, carValue, feedback);
    }

    @Tool(description = "Maintenance specialist: creates a maintenance plan")
    public String invokeMaintenanceAgent(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String maintenanceRequest) {
        return maintenanceAgent.processMaintenance(carMake, carModel, carYear, carNumber, maintenanceRequest);
    }

    @Tool(description = "Cleaning specialist: requests cleaning services when needed")
    public String invokeCleaningAgent(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String cleaningRequest) {
        return cleaningAgent.processCleaning(carMake, carModel, carYear, carNumber, cleaningRequest);
    }
}
