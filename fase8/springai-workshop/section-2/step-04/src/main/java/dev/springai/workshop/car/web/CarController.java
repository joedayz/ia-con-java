package dev.springai.workshop.car.web;

import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.repository.CarInfoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarInfoRepository carInfoRepository;

    public CarController(CarInfoRepository carInfoRepository) {
        this.carInfoRepository = carInfoRepository;
    }

    @GetMapping
    public List<CarInfo> getAllCars() {
        return carInfoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarInfo> getCarById(@PathVariable Long id) {
        return carInfoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
