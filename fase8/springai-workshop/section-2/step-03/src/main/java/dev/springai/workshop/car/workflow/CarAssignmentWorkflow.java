package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CleaningAgent;
import dev.springai.workshop.car.agent.MaintenanceAgent;
import dev.springai.workshop.car.domain.CarInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Workflow condicional: MaintenanceAgent o CleaningAgent según el análisis (equiv. {@code @ConditionalAgent}).
 */
@Service
public class CarAssignmentWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CarAssignmentWorkflow.class);

    private final MaintenanceAgent maintenanceAgent;
    private final CleaningAgent cleaningAgent;

    public CarAssignmentWorkflow(MaintenanceAgent maintenanceAgent, CleaningAgent cleaningAgent) {
        this.maintenanceAgent = maintenanceAgent;
        this.cleaningAgent = cleaningAgent;
    }

    public String processAction(CarInfo carInfo, Integer carNumber,
                                String cleaningRequest, String maintenanceRequest) {
        log.info("CarAssignmentWorkflow evaluating conditions...");

        if (ActionRequired.isRequired(maintenanceRequest)) {
            log.info("  └─ MaintenanceAgent activated");
            return maintenanceAgent.processMaintenance(carInfo, carNumber, maintenanceRequest);
        }
        if (ActionRequired.isRequired(cleaningRequest)) {
            log.info("  └─ CleaningAgent activated");
            return cleaningAgent.processCleaning(carInfo, carNumber, cleaningRequest);
        }
        return "";
    }
}
