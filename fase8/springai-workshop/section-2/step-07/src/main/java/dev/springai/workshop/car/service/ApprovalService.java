package dev.springai.workshop.car.service;

import dev.springai.workshop.car.domain.ApprovalProposal;
import dev.springai.workshop.car.domain.ApprovalStatus;
import dev.springai.workshop.car.repository.ApprovalProposalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ApprovalService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalService.class);

    private final ApprovalProposalRepository approvalProposalRepository;
    private final Map<Integer, CompletableFuture<ApprovalProposal>> pendingApprovals = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public ApprovalService(ApprovalProposalRepository approvalProposalRepository) {
        this.approvalProposalRepository = approvalProposalRepository;
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

        approvalProposalRepository.findByCarNumberAndStatus(carNumber, ApprovalStatus.PENDING)
                .ifPresent(existing -> log.warn("Proposal already exists for car {}", carNumber));

        CompletableFuture<ApprovalProposal> future = new CompletableFuture<>();
        pendingApprovals.put(carNumber, future);

        executor.submit(() -> {
            try {
                createProposalInNewTransaction(carNumber, carMake, carModel, carYear, carValue,
                        proposedDisposition, dispositionReason, carCondition, rentalFeedback);
                log.info("Proposal creation committed - visible to UI queries");
            } catch (Exception e) {
                log.error("Failed to create proposal for car {}", carNumber, e);
                future.completeExceptionally(e);
                pendingApprovals.remove(carNumber);
            }
        });

        return future;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createProposalInNewTransaction(
            Integer carNumber,
            String carMake,
            String carModel,
            Integer carYear,
            String carValue,
            String proposedDisposition,
            String dispositionReason,
            String carCondition,
            String rentalFeedback) {

        ApprovalProposal proposal = new ApprovalProposal();
        proposal.setCarNumber(carNumber);
        proposal.setCarMake(carMake);
        proposal.setCarModel(carModel);
        proposal.setCarYear(carYear);
        proposal.setCarValue(carValue);
        proposal.setProposedDisposition(proposedDisposition);
        proposal.setDispositionReason(dispositionReason);
        proposal.setCarCondition(carCondition);
        proposal.setRentalFeedback(rentalFeedback);
        proposal.setStatus(ApprovalStatus.PENDING);
        proposal.setCreatedAt(LocalDateTime.now());

        approvalProposalRepository.saveAndFlush(proposal);

        log.info("Created approval proposal ID={} for car {} - {} {} {} (Value: {}, Proposed: {})",
                proposal.getId(), carNumber, carYear, carMake, carModel, carValue, proposedDisposition);
        log.info("WORKFLOW PAUSED - Waiting for human approval decision");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ApprovalProposal processDecision(Long proposalId, boolean approved, String reason, String approvedBy) {
        ApprovalProposal proposal = approvalProposalRepository.findById(proposalId)
                .orElseThrow(() -> new IllegalArgumentException("Proposal not found: " + proposalId));

        if (proposal.getStatus() != ApprovalStatus.PENDING) {
            throw new IllegalStateException("Proposal is not pending: " + proposalId);
        }

        proposal.setStatus(approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        proposal.setDecision(approved ? "APPROVED" : "REJECTED");
        proposal.setApprovalReason(reason);
        proposal.setApprovedBy(approvedBy);
        proposal.setDecidedAt(LocalDateTime.now());

        approvalProposalRepository.save(proposal);

        log.info("Human decision received for car {}: {} - {}",
                proposal.getCarNumber(), proposal.getDecision(), reason);
        log.info("WORKFLOW RESUMED - Continuing with approval decision");

        CompletableFuture<ApprovalProposal> future = pendingApprovals.remove(proposal.getCarNumber());
        if (future != null) {
            future.complete(proposal);
        }

        return proposal;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<ApprovalProposal> getPendingProposals() {
        return approvalProposalRepository.findByStatus(ApprovalStatus.PENDING);
    }

    public ApprovalProposal getProposal(Long proposalId) {
        return approvalProposalRepository.findById(proposalId).orElse(null);
    }
}
