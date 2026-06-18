package com.carmanagement.service;

import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackAnalysisResults;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

/**
 * CDI bridge for invoking fleet orchestration from LangChain4j static agent methods
 * running on worker threads where direct Arc lookups may be unavailable.
 */
@ApplicationScoped
public class FleetSupervisorCoordinationBridge {

    private static volatile FleetSupervisorCoordinationBridge instance;

    private final FleetSupervisorCoordinationService coordinationService;

    @Inject
    FleetSupervisorCoordinationBridge(FleetSupervisorCoordinationService coordinationService) {
        this.coordinationService = coordinationService;
    }

    @PostConstruct
    void init() {
        instance = this;
    }

    public static String supervise(
            CarInfo carInfo,
            Integer carNumber,
            String feedback,
            FeedbackAnalysisResults feedbackAnalysisResults) {
        FleetSupervisorCoordinationBridge bridge = instance;
        if (bridge == null) {
            bridge = CDI.current().select(FleetSupervisorCoordinationBridge.class).get();
        }
        return bridge.coordinationService.supervise(
                carInfo, carNumber, feedback, feedbackAnalysisResults);
    }
}
