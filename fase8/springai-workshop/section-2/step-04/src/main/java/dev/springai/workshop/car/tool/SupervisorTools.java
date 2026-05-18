package dev.springai.workshop.car.tool;

import dev.springai.workshop.car.agent.CleaningAgent;
import dev.springai.workshop.car.agent.DispositionAgent;
import dev.springai.workshop.car.agent.MaintenanceAgent;
import dev.springai.workshop.car.agent.PricingAgent;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * Tools expuestos al supervisor para invocar sub-agentes (equiv. subAgents de {@code @SupervisorAgent}).
 */
@Component
public class SupervisorTools {

    private final PricingAgent pricingAgent;
    private final DispositionAgent dispositionAgent;
    private final MaintenanceAgent maintenanceAgent;
    private final CleaningAgent cleaningAgent;

    public SupervisorTools(
            PricingAgent pricingAgent,
            DispositionAgent dispositionAgent,
            MaintenanceAgent maintenanceAgent,
            CleaningAgent cleaningAgent) {
        this.pricingAgent = pricingAgent;
        this.dispositionAgent = dispositionAgent;
        this.maintenanceAgent = maintenanceAgent;
        this.cleaningAgent = cleaningAgent;
    }

    @Tool(description = "Pricing specialist: estimates vehicle market value based on make, model, year, and condition")
    public String invokePricingAgent(String carMake, String carModel, Integer carYear, String carCondition) {
        return pricingAgent.estimateValue(carMake, carModel, carYear, carCondition);
    }

    @Tool(description = "Disposition specialist: determines SCRAP, SELL, DONATE, or KEEP based on value and damage")
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

    @Tool(description = "Maintenance specialist: creates a maintenance plan from the maintenance analysis")
    public String invokeMaintenanceAgent(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String maintenanceRequest) {
        return maintenanceAgent.processMaintenance(carMake, carModel, carYear, carNumber, maintenanceRequest);
    }

    @Tool(description = "Cleaning specialist: requests cleaning services via requestCleaning tool when needed")
    public String invokeCleaningAgent(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String cleaningRequest) {
        return cleaningAgent.processCleaning(carMake, carModel, carYear, carNumber, cleaningRequest);
    }
}
