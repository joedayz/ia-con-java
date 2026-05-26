package dev.springai.workshop.car.web;

import dev.springai.workshop.car.domain.CarImageInput;
import dev.springai.workshop.car.service.CarManagementService;
import dev.springai.workshop.car.service.ReturnJobTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/car-management")
public class CarManagementController {

    private static final Logger log = LoggerFactory.getLogger(CarManagementController.class);

    private final CarManagementService carManagementService;
    private final ReturnJobTracker returnJobTracker;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public CarManagementController(CarManagementService carManagementService,
                                   ReturnJobTracker returnJobTracker) {
        this.carManagementService = carManagementService;
        this.returnJobTracker = returnJobTracker;
    }

    @GetMapping("/return/{carNumber}/status")
    public ReturnJobTracker.Status returnStatus(@PathVariable Integer carNumber) {
        return returnJobTracker.get(carNumber);
    }

    @PostMapping(value = "/return/{carNumber}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> processReturnMultipart(
            @PathVariable Integer carNumber,
            @RequestParam(required = false) String feedback,
            @RequestParam(value = "carImage", required = false) MultipartFile carImage) {
        return acceptReturn(carNumber, feedback, carImage);
    }

    @PostMapping(value = "/return/{carNumber}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Map<String, Object>> processReturnForm(
            @PathVariable Integer carNumber,
            @RequestParam(required = false) String feedback) {
        return acceptReturn(carNumber, feedback, null);
    }

    private ResponseEntity<Map<String, Object>> acceptReturn(
            Integer carNumber, String feedback, MultipartFile carImage) {
        String safeFeedback = feedback != null ? feedback : "";
        final CarImageInput imageInput;
        try {
            imageInput = CarImageInput.from(carImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        returnJobTracker.started(carNumber);

        executor.submit(() -> {
            try {
                log.info("Background return workflow started for car #{}", carNumber);
                String result = carManagementService.processCarReturn(carNumber, safeFeedback, imageInput);
                returnJobTracker.completed(carNumber, result);
                log.info("Background return workflow finished for car #{}: {}", carNumber, result);
            } catch (Exception e) {
                log.error("Background return failed for car #{}", carNumber, e);
                returnJobTracker.failed(carNumber, e.getMessage());
            }
        });

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "status", "ACCEPTED",
                "carNumber", carNumber,
                "message", "Return processing started. Approve in the dialog when it appears."));
    }
}
