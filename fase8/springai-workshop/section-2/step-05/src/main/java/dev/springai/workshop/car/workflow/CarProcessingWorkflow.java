package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CarConditionFeedbackAgent;
import dev.springai.workshop.car.agent.DispositionProposalAgent;
import dev.springai.workshop.car.agent.FleetSupervisorAgent;
import dev.springai.workshop.car.agent.HumanApprovalAgent;
import dev.springai.workshop.car.agent.PricingAgent;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackAnalysisResults;
import dev.springai.workshop.car.domain.FeedbackTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CarProcessingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CarProcessingWorkflow.class);

    /** Vehículos por encima de este valor exigen aprobación humana antes de disponerse. */
    private static final double HIGH_VALUE_THRESHOLD = 15_000.0;
    private static final Pattern ESTIMATED_VALUE_PATTERN =
            Pattern.compile("(?i)Estimated Value:\\s*\\$?\\s*([0-9][0-9,]*(?:\\.[0-9]+)?)");
    private static final Pattern ANY_DOLLAR_PATTERN =
            Pattern.compile("\\$\\s*([0-9][0-9,]*(?:\\.[0-9]+)?)");
    private static final Pattern REASONING_PATTERN =
            Pattern.compile("(?is)Reasoning:\\s*(.+)");

    private final FeedbackAnalysisWorkflow feedbackAnalysisWorkflow;
    private final FleetSupervisorAgent fleetSupervisorAgent;
    private final CarConditionFeedbackAgent carConditionFeedbackAgent;
    private final PricingAgent pricingAgent;
    private final DispositionProposalAgent dispositionProposalAgent;
    private final HumanApprovalAgent humanApprovalAgent;

    public CarProcessingWorkflow(
            FeedbackAnalysisWorkflow feedbackAnalysisWorkflow,
            FleetSupervisorAgent fleetSupervisorAgent,
            CarConditionFeedbackAgent carConditionFeedbackAgent,
            PricingAgent pricingAgent,
            DispositionProposalAgent dispositionProposalAgent,
            HumanApprovalAgent humanApprovalAgent) {
        this.feedbackAnalysisWorkflow = feedbackAnalysisWorkflow;
        this.fleetSupervisorAgent = fleetSupervisorAgent;
        this.carConditionFeedbackAgent = carConditionFeedbackAgent;
        this.pricingAgent = pricingAgent;
        this.dispositionProposalAgent = dispositionProposalAgent;
        this.humanApprovalAgent = humanApprovalAgent;
    }

    public CarConditions processCarReturn(
            List<FeedbackTask> tasks,
            CarInfo carInfo,
            Integer carNumber,
            String feedback) {

        var analysisResults = feedbackAnalysisWorkflow.analyzeFeedback(tasks, carInfo, carNumber, feedback);

        String supervisorDecision = decideDisposition(carInfo, carNumber, feedback, analysisResults);

        log.info("CarConditionFeedbackAgent updating...");
        CarConditions carConditions = carConditionFeedbackAgent.analyzeForCondition(
                carInfo, carNumber, analysisResults, supervisorDecision);

        log.debug("CarConditions: {} → {} ({})",
                carConditions.generalCondition(),
                carConditions.carAssignment(),
                carConditions.dispositionStatus());
        return carConditions;
    }

    /**
     * Gate Human-in-the-Loop determinista: si el análisis exige disposición y el vehículo
     * supera {@link #HIGH_VALUE_THRESHOLD}, SIEMPRE se crea la propuesta y se pausa el flujo
     * esperando decisión humana. No se delega esa decisión al LLM supervisor porque su
     * llamada a herramientas no es fiable y a veces decidía autónomamente (sin abrir el modal).
     */
    private String decideDisposition(
            CarInfo carInfo,
            Integer carNumber,
            String feedback,
            FeedbackAnalysisResults analysisResults) {

        boolean dispositionRequired = analysisResults.dispositionAnalysis() != null
                && analysisResults.dispositionAnalysis().toUpperCase().contains("DISPOSITION_REQUIRED");

        if (dispositionRequired) {
            String condition = carInfo.getCondition() != null ? carInfo.getCondition() : "Unknown";
            String pricingResult = pricingAgent.estimateValue(
                    carInfo.getMake(), carInfo.getModel(), carInfo.getYear(), condition);
            double value = parseEstimatedValue(pricingResult);
            String carValue = formatValue(value);
            log.info("HITL gate for car #{}: disposition required, estimated value {} (parsed {})",
                    carNumber, carValue, value);

            if (value > HIGH_VALUE_THRESHOLD) {
                log.info("HIGH-VALUE disposition (> ${}) → forcing human approval for car #{}",
                        HIGH_VALUE_THRESHOLD, carNumber);
                String proposal = dispositionProposalAgent.createDispositionProposal(
                        carInfo.getMake(), carInfo.getModel(), carInfo.getYear(),
                        carNumber, condition, carValue, feedback);
                String dispositionReason = extractReasoning(proposal);
                return humanApprovalAgent.reviewDispositionProposal(
                        carInfo.getMake(), carInfo.getModel(), carInfo.getYear(),
                        carNumber, carValue, proposal, dispositionReason, condition, feedback);
            }
        }

        log.info("FleetSupervisorAgent handling car #{} (no high-value HITL gate triggered)", carNumber);
        return fleetSupervisorAgent.superviseCarProcessing(carInfo, carNumber, feedback, analysisResults);
    }

    private static double parseEstimatedValue(String pricingResult) {
        if (pricingResult == null || pricingResult.isBlank()) {
            return 0;
        }
        Matcher labeled = ESTIMATED_VALUE_PATTERN.matcher(pricingResult);
        if (labeled.find()) {
            return toDouble(labeled.group(1));
        }
        Matcher anyDollar = ANY_DOLLAR_PATTERN.matcher(pricingResult);
        if (anyDollar.find()) {
            return toDouble(anyDollar.group(1));
        }
        return 0;
    }

    private static String formatValue(double value) {
        return String.format("$%,.0f", value);
    }

    private static double toDouble(String raw) {
        try {
            return Double.parseDouble(raw.replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String extractReasoning(String proposal) {
        if (proposal == null || proposal.isBlank()) {
            return "High-value vehicle disposition requires human review.";
        }
        Matcher matcher = REASONING_PATTERN.matcher(proposal);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return proposal.trim();
    }
}
