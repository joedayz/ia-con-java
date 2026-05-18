package dev.springai.workshop.car.web;

import dev.springai.workshop.car.domain.CarImageInput;
import dev.springai.workshop.car.service.CarManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/car-management")
public class CarManagementController {

    private static final Logger log = LoggerFactory.getLogger(CarManagementController.class);

    private final CarManagementService carManagementService;

    public CarManagementController(CarManagementService carManagementService) {
        this.carManagementService = carManagementService;
    }

    @PostMapping(value = "/return/{carNumber}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> processReturnMultipart(
            @PathVariable Integer carNumber,
            @RequestParam(required = false) String feedback,
            @RequestParam(value = "carImage", required = false) MultipartFile carImage) {
        return doProcessReturn(carNumber, feedback, carImage);
    }

    @PostMapping(value = "/return/{carNumber}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> processReturnForm(
            @PathVariable Integer carNumber,
            @RequestParam(required = false) String feedback) {
        return doProcessReturn(carNumber, feedback, null);
    }

    private ResponseEntity<String> doProcessReturn(
            Integer carNumber, String feedback, MultipartFile carImage) {
        try {
            CarImageInput imageInput = CarImageInput.from(carImage);
            String result = carManagementService.processCarReturn(
                    carNumber, feedback != null ? feedback : "", imageInput);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing return", e);
            return ResponseEntity.internalServerError()
                    .body("Error processing return: " + e.getMessage());
        }
    }
}
