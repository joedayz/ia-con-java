package dev.springai.workshop.car.service;

import dev.springai.workshop.car.domain.CarAssignment;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.CarStatus;
import dev.springai.workshop.car.repository.CarInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarReturnTxService {

    private static final Logger log = LoggerFactory.getLogger(CarReturnTxService.class);

    private final CarInfoRepository carInfoRepository;

    public CarReturnTxService(CarInfoRepository carInfoRepository) {
        this.carInfoRepository = carInfoRepository;
    }

    @Transactional
    public void persistReturnResult(Integer carNumber, CarConditions carConditions) {
        CarInfo carInfo = carInfoRepository.findById(carNumber.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + carNumber));

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
    }
}
