package com.carmanagement.service;

import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.carmanagement.agentic.workflow.CarProcessingWorkflow;
import com.carmanagement.model.CarConditions;
import com.carmanagement.model.CarInfo;
import com.carmanagement.model.CarStatus;
import com.carmanagement.model.FeedbackTask;
import dev.langchain4j.data.message.ImageContent;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;

import java.util.List;

import static dev.langchain4j.agentic.observability.HtmlReportGenerator.generateReport;

/**
 * Service for managing car returns from various operations.
 * Uses async processing to handle Human-in-the-Loop workflow pauses.
 */
@ApplicationScoped
public class CarManagementService {

    @Inject
    CarProcessingWorkflow carProcessingWorkflow;

    @Inject
    FleetSupervisorCoordinationBridge fleetSupervisorCoordinationBridge;

    @Inject
    CarManagementService self;

    /**
     * Process a car return from any operation.
     * This method runs asynchronously to handle workflow pauses for human approval.
     */
    public Uni<String> processCarReturn(Integer carNumber, String feedback, ImageContent carImage) {

        return Uni.createFrom().item(() -> self.processCarReturnSync(carNumber, feedback, carImage))
                .runSubscriptionOn(io.smallrye.mutiny.infrastructure.Infrastructure.getDefaultWorkerPool());
    }

    @ActivateRequestContext
    String processCarReturnSync(Integer carNumber, String feedback, ImageContent carImage) {
        CarInfo carInfo = findCarInfo(carNumber);
        if (carInfo == null) {
            return "Car not found with number: " + carNumber;
        }

        List<FeedbackTask> tasks = List.of(
                FeedbackTask.cleaning(),
                FeedbackTask.maintenance(),
                FeedbackTask.disposition()
        );

        CarConditions carConditions = carProcessingWorkflow.processCarReturn(
                tasks,
                carInfo,
                carNumber,
                feedback,
                carImage);

        Log.info("CarConditionFeedbackAgent updating...");

        carInfo.condition = carConditions.generalCondition();

        switch (carConditions.carAssignment()) {
            case DISPOSITION:
                carInfo.status = CarStatus.PENDING_DISPOSITION;
                Log.info("Car marked for disposition - awaiting final decision");
                break;
            case MAINTENANCE:
                carInfo.status = CarStatus.IN_MAINTENANCE;
                break;
            case CLEANING:
                carInfo.status = CarStatus.AT_CLEANING;
                break;
            case NONE:
                carInfo.status = CarStatus.AVAILABLE;
                break;
        }

        updateCarInfo(carInfo);

        return carConditions.generalCondition();
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    CarInfo findCarInfo(Integer carNumber) {
        return CarInfo.findById(carNumber);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void updateCarInfo(CarInfo carInfo) {
        CarInfo.getEntityManager().merge(carInfo);
    }

    public String report() {
        return generateReport(carProcessingWorkflow.agentMonitor());
    }
}
