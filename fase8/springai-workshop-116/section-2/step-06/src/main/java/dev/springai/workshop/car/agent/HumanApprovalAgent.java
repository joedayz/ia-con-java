package dev.springai.workshop.car.agent;

import dev.springai.workshop.car.domain.ApprovalProposal;
import dev.springai.workshop.car.service.ApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Human-in-the-loop: bloquea el workflow hasta decisión humana (equiv. {@code @HumanInTheLoop}).
 */
@Component
public class HumanApprovalAgent {

    private static final Logger log = LoggerFactory.getLogger(HumanApprovalAgent.class);
    private static final Pattern ACTION_PATTERN = Pattern.compile("__(KEEP|SCRAP|SELL|DONATE)__");

    private final ApprovalService approvalService;

    public HumanApprovalAgent(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    public String reviewDispositionProposal(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String carValue,
            String dispositionProposal,
            String dispositionReason,
            String carCondition,
            String feedback) {

        log.info("HITL: Creating approval proposal for car {} - {} {} {}", carNumber, carYear, carMake, carModel);
        log.info("WORKFLOW PAUSED - Waiting for human approval decision via UI");

        String proposedDisposition = extractProposedAction(dispositionProposal);

        try {
            ApprovalProposal result = approvalService.createProposalAndWaitForDecision(
                    carNumber, carMake, carModel, carYear, carValue,
                    proposedDisposition, dispositionReason, carCondition, feedback
            ).get(5, TimeUnit.MINUTES);

            log.info("WORKFLOW RESUMED - Human decision received: {}", result.getApprovalReason());

            return """
                    Human Decision: %s
                    Reason: %s
                    Approved By: %s
                    Decision Time: %s
                    """.formatted(
                    result.getDecision(),
                    result.getApprovalReason() != null ? result.getApprovalReason() : "No reason provided",
                    result.getApprovedBy() != null ? result.getApprovedBy() : "Unknown",
                    result.getDecidedAt() != null ? result.getDecidedAt().toString() : "Unknown");

        } catch (TimeoutException e) {
            log.error("TIMEOUT: No human decision within 5 minutes, defaulting to REJECTED");
            return """
                    Human Decision: REJECTED
                    Reason: Timeout - No human decision received within 5 minutes. Defaulting to rejection for safety.
                    Approved By: System (Timeout)
                    """;
        } catch (Exception e) {
            log.error("ERROR: Failed to get human approval for car {}", carNumber, e);
            return """
                    Human Decision: REJECTED
                    Reason: Error occurred while waiting for human approval: %s
                    Approved By: System (Error)
                    """.formatted(e.getMessage());
        }
    }

    private static String extractProposedAction(String dispositionProposal) {
        if (dispositionProposal == null) {
            return "UNKNOWN";
        }
        Matcher matcher = ACTION_PATTERN.matcher(dispositionProposal.toUpperCase());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return dispositionProposal.length() > 50
                ? dispositionProposal.substring(0, 50)
                : dispositionProposal;
    }
}
