package dev.springai.workshop.car.tool;

import dev.springai.workshop.car.domain.CarStatus;
import dev.springai.workshop.car.repository.CarInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tool para solicitar limpieza (equivalente a {@code CleaningTool} en Quarkus step-01).
 */
@Component
public class CleaningTool {

    private static final Logger log = LoggerFactory.getLogger(CleaningTool.class);

    private final CarInfoRepository carInfoRepository;

    public CleaningTool(CarInfoRepository carInfoRepository) {
        this.carInfoRepository = carInfoRepository;
    }

    @Tool(description = "Requests a cleaning with the specified options")
    @Transactional
    public String requestCleaning(
            Integer carNumber,
            String carMake,
            String carModel,
            Integer carYear,
            boolean exteriorWash,
            boolean interiorCleaning,
            boolean detailing,
            boolean waxing,
            String requestText) {

        carInfoRepository.findById(carNumber.longValue()).ifPresent(car -> {
            car.setStatus(CarStatus.AT_CLEANING);
            carInfoRepository.save(car);
        });

        String result = generateCleaningSummary(carNumber, carMake, carModel, carYear,
                exteriorWash, interiorCleaning, detailing, waxing, requestText);
        log.info("🚗 CleaningTool result: {}", result);
        return result;
    }

    private static String generateCleaningSummary(
            Integer carNumber,
            String carMake,
            String carModel,
            Integer carYear,
            boolean exteriorWash,
            boolean interiorCleaning,
            boolean detailing,
            boolean waxing,
            String requestText) {

        var summary = new StringBuilder();
        summary.append("Cleaning requested for ").append(carMake).append(" ")
                .append(carModel).append(" (").append(carYear).append("), Car #")
                .append(carNumber).append(":\n");

        if (exteriorWash) {
            summary.append("- Exterior wash\n");
        }
        if (interiorCleaning) {
            summary.append("- Interior cleaning\n");
        }
        if (detailing) {
            summary.append("- Detailing\n");
        }
        if (waxing) {
            summary.append("- Waxing\n");
        }
        if (requestText != null && !requestText.isEmpty()) {
            summary.append("Additional notes: ").append(requestText);
        }

        return summary.toString();
    }
}
