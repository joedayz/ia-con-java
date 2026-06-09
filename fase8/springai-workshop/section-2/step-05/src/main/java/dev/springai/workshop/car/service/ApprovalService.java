package dev.springai.workshop.car.service;

import dev.springai.workshop.car.domain.ApprovalProposal;
import dev.springai.workshop.car.repository.ApprovalProposalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);

    private final ApprovalProposalRepository approvalProposalRepository;
    private final ApprovalTxService approvalTxService;
    private final ReturnJobTracker returnJobTracker;
    private final Map<Integer, CompletableFuture<ApprovalProposal>> pendingApprovals = new ConcurrentHashMap<>();

    public ApprovalService(ApprovalProposalRepository approvalProposalRepository,
                           ApprovalTxService approvalTxService,
                           ReturnJobTracker returnJobTracker) {
        this.approvalProposalRepository = approvalProposalRepository;
        this.approvalTxService = approvalTxService;
        this.returnJobTracker = returnJobTracker;
    }

    public CompletableFuture<ApprovalProposal> createProposalAndWaitForDecision(
            Integer carNumber,
            String carMake,
            String carModel,
            Integer carYear,
            String carValue,
            String proposedDisposition,
            String dispositionReason,
            String carCondition,
            String rentalFeedback) {

        CompletableFuture<ApprovalProposal> existingFuture = pendingApprovals.get(carNumber);
        if (existingFuture != null && !existingFuture.isDone()) {
            log.warn("Reusing in-flight approval wait for car {}", carNumber);
            return existingFuture;
        }

        Optional<ApprovalProposal> existingProposal = approvalTxService.findPendingForCar(carNumber);
        if (existingProposal.isPresent()) {
            log.warn("Proposal already exists for car {} (id={})", carNumber, existingProposal.get().getId());
            CompletableFuture<ApprovalProposal> future = new CompletableFuture<>();
            pendingApprovals.put(carNumber, future);
            returnJobTracker.awaitingApproval(carNumber);
            return future;
        }

        CompletableFuture<ApprovalProposal> future = new CompletableFuture<>();
        pendingApprovals.put(carNumber, future);

        try {
            approvalTxService.createProposal(carNumber, carMake, carModel, carYear, carValue,
                    proposedDisposition, dispositionReason, carCondition, rentalFeedback);
            approvalTxService.markCarAwaitingApproval(carNumber);
            returnJobTracker.awaitingApproval(carNumber);
            log.info("WORKFLOW PAUSED - Waiting for human approval decision");
            log.info("Proposal creation committed - visible to UI queries");
        } catch (Exception e) {
            log.error("Failed to create proposal for car {}", carNumber, e);
            pendingApprovals.remove(carNumber);
            future.completeExceptionally(e);
        }

        return future;
    }

    public ApprovalProposal processDecision(Long proposalId, boolean approved, String reason, String approvedBy) {
        ApprovalProposal proposal = approvalTxService.saveDecision(proposalId, approved, reason, approvedBy);

        log.info("Human decision received for car {}: {} - {}",
                proposal.getCarNumber(), proposal.getDecision(), reason);
        log.info("WORKFLOW RESUMED - Continuing with approval decision");

        CompletableFuture<ApprovalProposal> future = pendingApprovals.remove(proposal.getCarNumber());
        if (future != null) {
            future.complete(proposal);
        } else {
            log.warn("No in-memory future for car {} when processing decision on proposal {}",
                    proposal.getCarNumber(), proposalId);
        }

        return proposal;
    }

    public List<ApprovalProposal> getPendingProposals() {
        return approvalTxService.findPendingProposals();
    }

    public ApprovalProposal getProposal(Long proposalId) {
        return approvalProposalRepository.findById(proposalId).orElse(null);
    }
}
