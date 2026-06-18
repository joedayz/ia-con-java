package com.carmanagement.service;

import com.carmanagement.model.ApprovalProposal;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Service implementing Human-in-the-Loop approval logic.
 * Used by HumanApprovalAgent and programmatic fleet orchestration.
 */
@ApplicationScoped
public class HumanApprovalService {

    @Inject
    ApprovalService approvalService;

    public String reviewDispositionProposal(
            String carMake,
            String carModel,
            Integer carYear,
            Integer carNumber,
            String carValue,
            String dispositionProposal,
            String carCondition,
            String feedback) {

        String dispositionReason = extractReasoning(dispositionProposal);

        Log.infof("🛑 HITL Tool: Creating approval proposal for car %d - %s %s %s",
                carNumber, carYear, carMake, carModel);
        Log.info("⏸️  WORKFLOW PAUSED - Waiting for human approval decision via UI");

        try {
            CompletableFuture<ApprovalProposal> approvalFuture =
                    approvalService.createProposalAndWaitForDecision(
                            carNumber, carMake, carModel, carYear, carValue,
                            dispositionProposal, dispositionReason, carCondition, feedback);

            ApprovalProposal result = approvalFuture.get(5, TimeUnit.MINUTES);

            Log.infof("▶️  WORKFLOW RESUMED - Human decision received: %s", result.decision);

            return String.format("""
                Human Decision: %s
                Reason: %s
                Approved By: %s
                Decision Time: %s
                """,
                    result.decision,
                    result.approvalReason != null ? result.approvalReason : "No reason provided",
                    result.approvedBy != null ? result.approvedBy : "Unknown",
                    result.decidedAt != null ? result.decidedAt.toString() : "Unknown"
            );

        } catch (TimeoutException e) {
            Log.error("⏱️  TIMEOUT: No human decision received within 5 minutes, defaulting to REJECTED");
            return """
                Human Decision: REJECTED
                Reason: Timeout - No human decision received within 5 minutes. Defaulting to rejection for safety.
                Approved By: System (Timeout)
                """;
        } catch (Exception e) {
            Log.errorf(e, "❌ ERROR: Failed to get human approval for car %d", carNumber);
            return String.format("""
                Human Decision: REJECTED
                Reason: Error occurred while waiting for human approval: %s
                Approved By: System (Error)
                """, e.getMessage());
        }
    }

    private static String extractReasoning(String dispositionProposal) {
        int idx = dispositionProposal.indexOf("Reasoning:");
        if (idx >= 0) {
            return dispositionProposal.substring(idx + "Reasoning:".length()).trim();
        }
        return dispositionProposal;
    }
}
