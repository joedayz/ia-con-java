package dev.springai.workshop.car.service;

import dev.springai.workshop.car.domain.ApprovalProposal;
import dev.springai.workshop.car.domain.ApprovalStatus;
import dev.springai.workshop.car.domain.CarStatus;
import dev.springai.workshop.car.repository.ApprovalProposalRepository;
import dev.springai.workshop.car.repository.CarInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Transacciones independientes para HITL. Debe ser un bean aparte para que
 * {@code REQUIRES_NEW} se aplique vía proxy (no funciona con auto-invocación en ApprovalService).
 */
@Service
public class ApprovalTxService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalTxService.class);

    private final ApprovalProposalRepository approvalProposalRepository;
    private final CarInfoRepository carInfoRepository;

    public ApprovalTxService(ApprovalProposalRepository approvalProposalRepository,
                             CarInfoRepository carInfoRepository) {
        this.approvalProposalRepository = approvalProposalRepository;
        this.carInfoRepository = carInfoRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ApprovalProposal createProposal(
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
        return proposal;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCarAwaitingApproval(Integer carNumber) {
        carInfoRepository.findById(carNumber.longValue()).ifPresent(car -> {
            car.setStatus(CarStatus.PENDING_DISPOSITION);
            carInfoRepository.saveAndFlush(car);
            log.info("Car #{} marked PENDING_DISPOSITION while awaiting human approval", carNumber);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ApprovalProposal saveDecision(Long proposalId, boolean approved, String reason, String approvedBy) {
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

        return approvalProposalRepository.save(proposal);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public List<ApprovalProposal> findPendingProposals() {
        return approvalProposalRepository.findByStatus(ApprovalStatus.PENDING);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<ApprovalProposal> findPendingForCar(Integer carNumber) {
        return approvalProposalRepository.findByCarNumberAndStatus(carNumber, ApprovalStatus.PENDING);
    }
}
