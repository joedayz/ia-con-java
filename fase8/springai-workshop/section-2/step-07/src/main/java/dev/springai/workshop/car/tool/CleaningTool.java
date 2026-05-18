package dev.springai.workshop.car.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

/**
 * Tool de limpieza (step-03: no actualiza estado; lo hace {@code CarManagementService}).
 */
@Component
public class CleaningTool {

    private static final Logger log = LoggerFactory.getLogger(CleaningTool.class);

    @Tool(description = "Requests a cleaning with the specified options")
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

        String result = generateCleaningSummary(carNumber, carMake, carModel, carYear,
                exteriorWash, interiorCleaning, detailing, waxing, requestText);
        log.debug("🚗 CleaningTool result: {}", result);
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
