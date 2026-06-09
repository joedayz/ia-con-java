package dev.springai.workshop.car.service;

import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackTask;
import dev.springai.workshop.car.repository.CarInfoRepository;
import dev.springai.workshop.car.workflow.CarProcessingWorkflow;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarManagementService {

    private final CarProcessingWorkflow carProcessingWorkflow;
    private final CarInfoRepository carInfoRepository;
    private final CarReturnTxService carReturnTxService;

    public CarManagementService(CarProcessingWorkflow carProcessingWorkflow,
                                CarInfoRepository carInfoRepository,
                                CarReturnTxService carReturnTxService) {
        this.carProcessingWorkflow = carProcessingWorkflow;
        this.carInfoRepository = carInfoRepository;
        this.carReturnTxService = carReturnTxService;
    }

    /**
     * Sin transacción durante el workflow: HITL puede tardar minutos y las propuestas
     * deben ser visibles en otras peticiones HTTP antes de que termine este método.
     */
    public String processCarReturn(Integer carNumber, String feedback) {
        CarInfo carInfo = carInfoRepository.findById(carNumber.longValue())
                .orElse(null);
        if (carInfo == null) {
            return "Car not found with number: " + carNumber;
        }

        List<FeedbackTask> tasks = List.of(
                FeedbackTask.cleaning(),
                FeedbackTask.maintenance(),
                FeedbackTask.disposition());

        CarConditions carConditions = carProcessingWorkflow.processCarReturn(
                tasks, carInfo, carNumber, feedback);

        carReturnTxService.persistReturnResult(carNumber, carConditions);
        return carConditions.generalCondition();
    }
}
