package dev.springai.workshop.car.repository;

import dev.springai.workshop.car.domain.CarInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarInfoRepository extends JpaRepository<CarInfo, Long> {
}
