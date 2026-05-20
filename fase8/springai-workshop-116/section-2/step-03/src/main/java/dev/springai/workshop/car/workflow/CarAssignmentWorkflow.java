package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CleaningAgent;
import dev.springai.workshop.car.agent.MaintenanceAgent;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.agentic.RoutingWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Enrutamiento con {@link RoutingWorkflow#selectRoute} y despacho al agente correspondiente.
 */
@Service
public class CarAssignmentWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CarAssignmentWorkflow.class);

    private final MaintenanceAgent maintenanceAgent;
    private final CleaningAgent cleaningAgent;
    private final RoutingWorkflow routingWorkflow;

    public CarAssignmentWorkflow(
            MaintenanceAgent maintenanceAgent,
            CleaningAgent cleaningAgent,
            RoutingWorkflow routingWorkflow) {
        this.maintenanceAgent = maintenanceAgent;
        this.cleaningAgent = cleaningAgent;
        this.routingWorkflow = routingWorkflow;
    }

    public String processAction(CarInfo carInfo, Integer carNumber,
                                String cleaningRequest, String maintenanceRequest) {
        log.info("CarAssignmentWorkflow evaluating (RoutingWorkflow)...");

        if (!ActionRequired.isRequired(maintenanceRequest) && !ActionRequired.isRequired(cleaningRequest)) {
            return "";
        }

        String routingInput = """
                Cleaning analysis:
                %s

                Maintenance analysis:
                %s
                """.formatted(
                nullToEmpty(cleaningRequest),
                nullToEmpty(maintenanceRequest));

        String route = routingWorkflow.selectRoute(routingInput, List.of("maintenance", "cleaning")).trim();

        if (route.equalsIgnoreCase("maintenance") && ActionRequired.isRequired(maintenanceRequest)) {
            log.info("  └─ MaintenanceAgent activated (route: {})", route);
            return maintenanceAgent.processMaintenance(carInfo, carNumber, maintenanceRequest);
        }
        if (ActionRequired.isRequired(cleaningRequest)) {
            log.info("  └─ CleaningAgent activated (route: {})", route);
            return cleaningAgent.processCleaning(carInfo, carNumber, cleaningRequest);
        }
        if (ActionRequired.isRequired(maintenanceRequest)) {
            log.info("  └─ MaintenanceAgent activated (fallback)");
            return maintenanceAgent.processMaintenance(carInfo, carNumber, maintenanceRequest);
        }
        return "";
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
