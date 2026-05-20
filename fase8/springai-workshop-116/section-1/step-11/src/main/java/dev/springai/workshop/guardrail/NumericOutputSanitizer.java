package dev.springai.workshop.guardrail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Output guardrail equivalente a {@code NumericOutputSanitizerGuard} en Quarkus step-11.
 * Modelos locales pequeños a menudo devuelven texto además del número esperado.
 */
@Component
public class NumericOutputSanitizer {

    private static final Logger log = LoggerFactory.getLogger(NumericOutputSanitizer.class);

    public double sanitizeToDouble(String llmResponse) {
        if (llmResponse == null || llmResponse.isBlank()) {
            throw new IllegalArgumentException("Empty LLM response");
        }

        String trimmed = llmResponse.trim();
        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException ignored) {
            // fall through
        }

        log.debug("LLM output for expected numeric result: {}", llmResponse);

        String extracted = extractNumber(trimmed);
        if (extracted != null) {
            log.info("Extracted number: {}", extracted);
            try {
                return Double.parseDouble(extracted);
            } catch (NumberFormatException ignored) {
                // fall through
            }
        }

        throw new IllegalArgumentException("Unable to extract a number from LLM response: " + llmResponse);
    }

    private static String extractNumber(String text) {
        int lastDigitPosition = text.length() - 1;
        while (lastDigitPosition >= 0) {
            if (Character.isDigit(text.charAt(lastDigitPosition))) {
                break;
            }
            lastDigitPosition--;
        }
        if (lastDigitPosition < 0) {
            return null;
        }
        int numberBegin = lastDigitPosition;
        while (numberBegin >= 0) {
            char c = text.charAt(numberBegin);
            if (!Character.isDigit(c) && c != '.') {
                break;
            }
            numberBegin--;
        }
        return text.substring(numberBegin + 1, lastDigitPosition + 1);
    }
}
