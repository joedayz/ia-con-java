package dev.springai.workshop.car.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_proposal")
public class ApprovalProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer carNumber;

    private String carMake;
    private String carModel;
    private Integer carYear;
    @Column(length = 64)
    private String carValue;

    @Column(nullable = false, length = 255)
    private String proposedDisposition;

    @Column(length = 2000)
    private String dispositionReason;

    @Column(length = 1000)
    private String carCondition;

    @Column(length = 2000)
    private String rentalFeedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING;

    private String decision;

    @Column(length = 1000)
    private String approvalReason;

    private String approvedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime decidedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(Integer carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public Integer getCarYear() {
        return carYear;
    }

    public void setCarYear(Integer carYear) {
        this.carYear = carYear;
    }

    public String getCarValue() {
        return carValue;
    }

    public void setCarValue(String carValue) {
        this.carValue = carValue;
    }

    public String getProposedDisposition() {
        return proposedDisposition;
    }

    public void setProposedDisposition(String proposedDisposition) {
        this.proposedDisposition = proposedDisposition;
    }

    public String getDispositionReason() {
        return dispositionReason;
    }

    public void setDispositionReason(String dispositionReason) {
        this.dispositionReason = dispositionReason;
    }

    public String getCarCondition() {
        return carCondition;
    }

    public void setCarCondition(String carCondition) {
        this.carCondition = carCondition;
    }

    public String getRentalFeedback() {
        return rentalFeedback;
    }

    public void setRentalFeedback(String rentalFeedback) {
        this.rentalFeedback = rentalFeedback;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }
}
