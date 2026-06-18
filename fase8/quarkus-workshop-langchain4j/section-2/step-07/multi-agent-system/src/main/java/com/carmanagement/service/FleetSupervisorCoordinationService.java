package com.carmanagement.service;

import com.carmanagement.agentic.agents.CleaningAgent;
import com.carmanagement.agentic.agents.DispositionAgent;
import com.carmanagement.agentic.agents.DispositionProposalAgent;
import com.carmanagement.service.HumanApprovalService;
import com.carmanagement.agentic.agents.MaintenanceAgent;
import com.carmanagement.agentic.agents.PricingAgent;
import com.carmanagement.model.CarInfo;
import com.carmanagement.model.FeedbackAnalysisResults;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Deterministic fleet orchestration for value-based routing and Human-in-the-Loop approval.
 * High-value vehicles always pause for human approval before maintenance or disposition actions.
 */
@ApplicationScoped
public class FleetSupervisorCoordinationService {

    @Inject
    PricingAgent pricingAgent;

    @Inject
    DispositionProposalAgent dispositionProposalAgent;

    @Inject
    DispositionAgent dispositionAgent;

    @Inject
    MaintenanceAgent maintenanceAgent;

    @Inject
    CleaningAgent cleaningAgent;

    @Inject
    HumanApprovalService humanApprovalService;

    @ConfigProperty(name = "car-management.approval.threshold", defaultValue = "15000")
    int approvalThreshold;

    public String supervise(
            CarInfo carInfo,
            Integer carNumber,
            String feedback,
            FeedbackAnalysisResults results) {

        boolean dispositionRequired = isDispositionRequired(results);
        boolean maintenanceRequired = isMaintenanceRequired(results);
        boolean cleaningRequired = isCleaningRequired(results);

        if (dispositionRequired || maintenanceRequired) {
            String carValue = pricingAgent.estimateValue(
                    carInfo.make, carInfo.model, carInfo.year, carInfo.condition);
            int value = parseValue(carValue);

            Log.infof("FleetSupervisorAgent orchestrating... Estimated value %s (threshold $%d)",
                    carValue, approvalThreshold);

            if (value > approvalThreshold) {
                Log.info("Value check: above threshold -> Human approval required");
                return handleHighValueHitl(
                        carInfo, carNumber, feedback, results, carValue,
                        dispositionRequired, maintenanceRequired, cleaningRequired);
            }

            if (dispositionRequired) {
                return handleLowValueDisposition(
                        carInfo, carNumber, feedback, results, carValue,
                        maintenanceRequired, cleaningRequired);
            }
        }

        return handleSimplePath(carInfo, carNumber, results, maintenanceRequired, cleaningRequired);
    }

    private String handleHighValueHitl(
            CarInfo carInfo,
            Integer carNumber,
            String feedback,
            FeedbackAnalysisResults results,
            String carValue,
            boolean dispositionRequired,
            boolean maintenanceRequired,
            boolean cleaningRequired) {

        String proposal = dispositionProposalAgent.createDispositionProposal(
                carInfo.make, carInfo.model, carInfo.year, carNumber,
                carInfo.condition, carValue, feedback);

        String approval = humanApprovalService.reviewDispositionProposal(
                carInfo.make, carInfo.model, carInfo.year, carNumber,
                carValue, proposal, carInfo.condition, feedback);

        boolean rejected = approval.toUpperCase().contains("REJECTED");
        boolean proposesDispose = proposesDisposition(proposal);
        boolean proposesKeep = proposal.toUpperCase().contains("__KEEP__");

        StringBuilder decision = new StringBuilder();
        decision.append(approval).append("\n");
        decision.append("Disposition Proposal: ").append(proposal).append("\n");
        decision.append("Estimated Value: ").append(carValue).append("\n");

        if (rejected) {
            if (dispositionRequired) {
                if (proposesKeep) {
                    decision.append("DISPOSE_CAR");
                } else {
                    decision.append("KEEP_CAR");
                    appendMaintenanceAndCleaning(decision, carInfo, carNumber, results,
                            maintenanceRequired, cleaningRequired);
                }
            } else {
                decision.append("KEEP_CAR");
                appendMaintenanceAndCleaning(decision, carInfo, carNumber, results,
                        maintenanceRequired, cleaningRequired);
            }
        } else if (proposesDispose) {
            decision.append("DISPOSE_CAR");
        } else {
            decision.append("KEEP_CAR");
            appendMaintenanceAndCleaning(decision, carInfo, carNumber, results,
                    maintenanceRequired, cleaningRequired);
        }

        return decision.toString();
    }

    private String handleLowValueDisposition(
            CarInfo carInfo,
            Integer carNumber,
            String feedback,
            FeedbackAnalysisResults results,
            String carValue,
            boolean maintenanceRequired,
            boolean cleaningRequired) {

        String disposition = dispositionAgent.processDisposition(
                carInfo.make, carInfo.model, carInfo.year, carNumber,
                carInfo.condition, carValue, feedback);

        StringBuilder decision = new StringBuilder(disposition);

        if (disposition.toUpperCase().contains("KEEP")) {
            decision.append("\nKEEP_CAR");
            appendMaintenanceAndCleaning(decision, carInfo, carNumber, results,
                    maintenanceRequired, cleaningRequired);
        } else {
            decision.append("\nDISPOSE_CAR");
        }

        return decision.toString();
    }

    private String handleSimplePath(
            CarInfo carInfo,
            Integer carNumber,
            FeedbackAnalysisResults results,
            boolean maintenanceRequired,
            boolean cleaningRequired) {

        StringBuilder decision = new StringBuilder();

        if (maintenanceRequired) {
            decision.append(maintenanceAgent.processMaintenance(
                    carInfo, carNumber, results.maintenanceAnalysis()));
        }
        if (cleaningRequired) {
            if (!decision.isEmpty()) {
                decision.append("\n");
            }
            decision.append(cleaningAgent.processCleaning(
                    carInfo, carNumber, results.cleaningAnalysis()));
        }
        if (decision.isEmpty()) {
            decision.append("No action required.");
        }
        decision.append("\nKEEP_CAR");
        return decision.toString();
    }

    private void appendMaintenanceAndCleaning(
            StringBuilder decision,
            CarInfo carInfo,
            Integer carNumber,
            FeedbackAnalysisResults results,
            boolean maintenanceRequired,
            boolean cleaningRequired) {

        if (maintenanceRequired) {
            decision.append("\n");
            decision.append(maintenanceAgent.processMaintenance(
                    carInfo, carNumber, results.maintenanceAnalysis()));
        }
        if (cleaningRequired) {
            decision.append("\n");
            decision.append(cleaningAgent.processCleaning(
                    carInfo, carNumber, results.cleaningAnalysis()));
        }
    }

    private static boolean isDispositionRequired(FeedbackAnalysisResults results) {
        return results.dispositionAnalysis() != null
                && results.dispositionAnalysis().toUpperCase().contains("DISPOSITION_REQUIRED");
    }

    private static boolean isMaintenanceRequired(FeedbackAnalysisResults results) {
        return results.maintenanceAnalysis() != null
                && !results.maintenanceAnalysis().toUpperCase().contains("MAINTENANCE_NOT_REQUIRED");
    }

    private static boolean isCleaningRequired(FeedbackAnalysisResults results) {
        return results.cleaningAnalysis() != null
                && !results.cleaningAnalysis().toUpperCase().contains("CLEANING_NOT_REQUIRED");
    }

    private static boolean proposesDisposition(String proposal) {
        String upper = proposal.toUpperCase();
        return upper.contains("__SCRAP__") || upper.contains("__SELL__") || upper.contains("__DONATE__");
    }

    private static int parseValue(String carValue) {
        if (carValue == null) {
            return 0;
        }
        String digits = carValue.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }
}
