package dev.springai.workshop.car.service;

import dev.springai.workshop.car.domain.CarAssignment;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.CarStatus;
import dev.springai.workshop.car.repository.CarInfoRepository;
import dev.springai.workshop.car.workflow.CarProcessingWorkflow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarManagementService {

    private final CarProcessingWorkflow carProcessingWorkflow;
    private final CarInfoRepository carInfoRepository;

    public CarManagementService(CarProcessingWorkflow carProcessingWorkflow,
                                CarInfoRepository carInfoRepository) {
        this.carProcessingWorkflow = carProcessingWorkflow;
        this.carInfoRepository = carInfoRepository;
    }

    @Transactional
    public String processCarReturn(Integer carNumber, String feedback) {
        CarInfo carInfo = carInfoRepository.findById(carNumber.longValue())
                .orElse(null);
        if (carInfo == null) {
            return "Car not found with number: " + carNumber;
        }

        CarConditions carConditions = carProcessingWorkflow.processCarReturn(carInfo, carNumber, feedback);

        carInfo.setCondition(carConditions.generalCondition());

        switch (carConditions.carAssignment()) {
            case MAINTENANCE -> carInfo.setStatus(CarStatus.IN_MAINTENANCE);
            case CLEANING -> carInfo.setStatus(CarStatus.AT_CLEANING);
            case NONE -> carInfo.setStatus(CarStatus.AVAILABLE);
        }

        carInfoRepository.save(carInfo);

        return carConditions.generalCondition();
    }
}
