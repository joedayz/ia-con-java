package dev.springai.workshop.car.service;

import dev.springai.workshop.car.domain.CarAssignment;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarImageInput;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.CarStatus;
import dev.springai.workshop.car.domain.FeedbackTask;
import dev.springai.workshop.car.repository.CarInfoRepository;
import dev.springai.workshop.car.workflow.CarProcessingWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarManagementService {

    private static final Logger log = LoggerFactory.getLogger(CarManagementService.class);

    private final CarProcessingWorkflow carProcessingWorkflow;
    private final CarInfoRepository carInfoRepository;

    public CarManagementService(CarProcessingWorkflow carProcessingWorkflow,
                                CarInfoRepository carInfoRepository) {
        this.carProcessingWorkflow = carProcessingWorkflow;
        this.carInfoRepository = carInfoRepository;
    }

    @Transactional
    public String processCarReturn(Integer carNumber, String feedback, CarImageInput carImage) {
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
                tasks, carInfo, carNumber, feedback, carImage);

        carInfo.setCondition(carConditions.generalCondition());

        switch (carConditions.carAssignment()) {
            case DISPOSITION -> {
                carInfo.setStatus(CarStatus.PENDING_DISPOSITION);
                log.info("Car marked for disposition - awaiting final decision");
            }
            case MAINTENANCE -> carInfo.setStatus(CarStatus.IN_MAINTENANCE);
            case CLEANING -> carInfo.setStatus(CarStatus.AT_CLEANING);
            case NONE -> carInfo.setStatus(CarStatus.AVAILABLE);
        }

        carInfoRepository.save(carInfo);

        return carConditions.generalCondition();
    }
}
