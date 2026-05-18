package dev.springai.workshop.car.service;

import dev.springai.workshop.car.agent.CleaningAgent;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.CarStatus;
import dev.springai.workshop.car.repository.CarInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarManagementService {

    private final CleaningAgent cleaningAgent;
    private final CarInfoRepository carInfoRepository;

    public CarManagementService(CleaningAgent cleaningAgent, CarInfoRepository carInfoRepository) {
        this.cleaningAgent = cleaningAgent;
        this.carInfoRepository = carInfoRepository;
    }

    @Transactional
    public String processCarReturn(Integer carNumber, String feedback) {
        CarInfo carInfo = carInfoRepository.findById(carNumber.longValue())
                .orElse(null);
        if (carInfo == null) {
            return "Car not found with number: " + carNumber;
        }

        String result = cleaningAgent.processCleaning(carInfo, carNumber, feedback);

        if (result.toUpperCase().contains("CLEANING_NOT_REQUIRED")) {
            carInfo.setStatus(CarStatus.AVAILABLE);
            carInfoRepository.save(carInfo);
        }

        return result;
    }
}
