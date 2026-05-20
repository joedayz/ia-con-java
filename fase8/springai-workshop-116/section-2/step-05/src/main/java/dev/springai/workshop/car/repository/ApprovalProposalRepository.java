package dev.springai.workshop.car.repository;

import dev.springai.workshop.car.domain.ApprovalProposal;
import dev.springai.workshop.car.domain.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalProposalRepository extends JpaRepository<ApprovalProposal, Long> {

    Optional<ApprovalProposal> findByCarNumberAndStatus(Integer carNumber, ApprovalStatus status);

    List<ApprovalProposal> findByStatus(ApprovalStatus status);
}
