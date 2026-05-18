package dev.springai.workshop.car.web;

import dev.springai.workshop.car.service.CarManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/car-management")
public class CarManagementController {

    private static final Logger log = LoggerFactory.getLogger(CarManagementController.class);

    private final CarManagementService carManagementService;

    public CarManagementController(CarManagementService carManagementService) {
        this.carManagementService = carManagementService;
    }

    @PostMapping("/return/{carNumber}")
    public ResponseEntity<String> processReturn(
            @PathVariable Integer carNumber,
            @RequestParam(required = false) String feedback) {
        try {
            String result = carManagementService.processCarReturn(
                    carNumber, feedback != null ? feedback : "");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing return", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing return: " + e.getMessage());
        }
    }
}
