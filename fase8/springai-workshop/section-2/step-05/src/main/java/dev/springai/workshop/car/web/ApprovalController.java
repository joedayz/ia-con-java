package dev.springai.workshop.car.web;

import dev.springai.workshop.car.domain.ApprovalProposal;
import dev.springai.workshop.car.service.ApprovalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private static final Logger log = LoggerFactory.getLogger(ApprovalController.class);

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/pending")
    public List<ApprovalProposal> getPendingProposals() {
        List<ApprovalProposal> proposals = approvalService.getPendingProposals();
        if (!proposals.isEmpty()) {
            log.info("GET /api/approvals/pending → {} pending proposal(s)", proposals.size());
        } else if (log.isDebugEnabled()) {
            log.debug("GET /api/approvals/pending → 0 proposals");
        }
        return proposals;
    }

    @GetMapping("/{proposalId}")
    public ResponseEntity<ApprovalProposal> getProposal(@PathVariable Long proposalId) {
        ApprovalProposal proposal = approvalService.getProposal(proposalId);
        if (proposal == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(proposal);
    }

    @PostMapping("/{proposalId}/approve")
    public ResponseEntity<?> approveProposal(
            @PathVariable Long proposalId,
            @RequestBody Map<String, String> request) {
        return processDecision(proposalId, true, request);
    }

    @PostMapping("/{proposalId}/reject")
    public ResponseEntity<?> rejectProposal(
            @PathVariable Long proposalId,
            @RequestBody Map<String, String> request) {
        return processDecision(proposalId, false, request);
    }

    @PostMapping("/{proposalId}/decide")
    public ResponseEntity<?> decideProposal(
            @PathVariable Long proposalId,
            @RequestBody Map<String, String> request) {
        String decision = request.get("decision");
        if (decision == null || (!decision.equals("KEEP_CAR") && !decision.equals("DISPOSE_CAR"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Decision must be either KEEP_CAR or DISPOSE_CAR"));
        }

        String reason = request.getOrDefault("reason", "Decision by human reviewer");
        String approvedBy = request.getOrDefault("approvedBy", "Workshop User");
        String fullReason = decision + ": " + reason;

        log.info("Decision '{}' received for proposal {} by {}", decision, proposalId, approvedBy);

        try {
            ApprovalProposal proposal = approvalService.processDecision(proposalId, true, fullReason, approvedBy);
            return ResponseEntity.ok(proposal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error processing decision", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error processing decision: " + e.getMessage()));
        }
    }

    private ResponseEntity<?> processDecision(Long proposalId, boolean approved, Map<String, String> request) {
        try {
            String reason = request.getOrDefault("reason",
                    approved ? "Approved by human reviewer" : "Rejected by human reviewer");
            String approvedBy = request.getOrDefault("approvedBy", "Workshop User");
            ApprovalProposal proposal = approvalService.processDecision(proposalId, approved, reason, approvedBy);
            return ResponseEntity.ok(proposal);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error processing approval", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error processing approval: " + e.getMessage()));
        }
    }
}
