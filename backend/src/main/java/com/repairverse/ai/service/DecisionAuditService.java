package com.repairverse.ai.service;

import com.repairverse.ai.dto.TrustEngineDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Phase 34: Master orchestrator — records AI decisions, triggers evidence collection,
 * governance evaluation, trust scoring, and explainability generation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionAuditService {

    private final AiDecisionRecordRepository decisionRecordRepository;
    private final AiDecisionEvidenceRepository evidenceRepository;
    private final AiGovernanceViolationRepository violationRepository;

    private final TrustScoreService trustScoreService;
    private final EvidenceTraceService evidenceTraceService;
    private final ExplainabilityService explainabilityService;
    private final GovernanceRuleService governanceRuleService;

    /**
     * Record a new AI decision: collect evidence → evaluate governance → score trust → explain.
     */
    @Transactional
    public AiDecisionRecord recordDecision(String userId, String deviceId,
                                            String sourceSystem, String decisionType,
                                            String sourceRecordId, String decisionOutput,
                                            int confidenceScore, String riskLevel) {
        // 1. Build initial record
        AiDecisionRecord record = AiDecisionRecord.builder()
                .userId(userId)
                .deviceId(deviceId)
                .sourceSystem(sourceSystem)
                .decisionType(decisionType)
                .sourceRecordId(sourceRecordId)
                .decisionOutput(decisionOutput)
                .confidenceScore(confidenceScore)
                .riskLevel(riskLevel != null ? riskLevel : "LOW")
                .status("ACTIVE")
                .build();
        record = decisionRecordRepository.save(record);
        log.info("Created AI decision record '{}' for user '{}', system '{}'",
                record.getId(), userId, sourceSystem);

        // 2. Collect evidence
        List<AiDecisionEvidence> evidenceList = evidenceTraceService
                .collectEvidence(sourceSystem, sourceRecordId, deviceId);
        for (AiDecisionEvidence e : evidenceList) {
            e.setDecisionRecordId(record.getId());
        }
        evidenceRepository.saveAll(evidenceList);

        // 3. Evaluate governance rules
        List<AiGovernanceViolation> violations = governanceRuleService
                .evaluateRules(record, evidenceList);
        violationRepository.saveAll(violations);

        // 4. Compute trust score
        int trustScore = trustScoreService.computeTrustScore(record, evidenceList, violations);
        String trustTier = trustScoreService.determineTrustTier(trustScore);
        record.setTrustScore(trustScore);
        record.setTrustTier(trustTier);

        // 5. Generate explanations
        explainabilityService.generateExplanations(record, evidenceList);

        // 6. Persist final state
        record = decisionRecordRepository.save(record);
        log.info("Decision '{}' fully processed: trustScore={}, tier={}, violations={}",
                record.getId(), trustScore, trustTier, violations.size());
        return record;
    }

    /**
     * Retrieve audit log for a user, ordered most-recent-first.
     */
    public List<DecisionSummaryResponse> getDecisionAuditLog(String userId) {
        List<AiDecisionRecord> records = decisionRecordRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId);
        return records.stream().map(this::toSummary).toList();
    }

    /**
     * Retrieve audit log for a specific device.
     */
    public List<DecisionSummaryResponse> getDeviceDecisionLog(String userId, String deviceId) {
        List<AiDecisionRecord> records = decisionRecordRepository
                .findByDeviceIdAndUserIdOrderByCreatedAtDesc(deviceId, userId);
        return records.stream().map(this::toSummary).toList();
    }

    /**
     * Get full audit detail for a single decision including evidence, violations, breakdown.
     */
    public DecisionAuditResponse getDecisionById(String decisionId, String userId) {
        AiDecisionRecord record = decisionRecordRepository.findById(decisionId)
                .orElseThrow(() -> new IllegalArgumentException("Decision not found: " + decisionId));
        if (!record.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized access to decision " + decisionId);
        }
        return toAuditResponse(record);
    }

    /**
     * Mark a decision as reviewed by the user.
     */
    @Transactional
    public DecisionAuditResponse markReviewed(String decisionId, String userId) {
        AiDecisionRecord record = decisionRecordRepository.findById(decisionId)
                .orElseThrow(() -> new IllegalArgumentException("Decision not found: " + decisionId));
        if (!record.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized access to decision " + decisionId);
        }
        record.setUserReviewed(true);
        record = decisionRecordRepository.save(record);
        return toAuditResponse(record);
    }

    // ─── Mapping helpers ────────────────────────────────────────────────

    private DecisionSummaryResponse toSummary(AiDecisionRecord r) {
        return new DecisionSummaryResponse(
                r.getId(), r.getDeviceId(), r.getSourceSystem(), r.getDecisionType(),
                r.getConfidenceScore(), r.getTrustScore(), r.getTrustTier(),
                r.getRiskLevel(), r.getStatus(), r.getUserReviewed(), r.getUserFeedback(),
                r.getCreatedAt() != null ? r.getCreatedAt().toString() : null
        );
    }

    private DecisionAuditResponse toAuditResponse(AiDecisionRecord r) {
        List<AiDecisionEvidence> evidenceList = evidenceRepository.findAllByDecisionRecordId(r.getId());
        List<AiGovernanceViolation> violations = violationRepository.findAllByDecisionRecordId(r.getId());
        TrustScoreBreakdown breakdown = trustScoreService.buildBreakdown(r, evidenceList, violations);

        List<EvidenceTraceResponse> evidenceResponses = evidenceList.stream()
                .map(e -> new EvidenceTraceResponse(
                        e.getId(), e.getEvidenceType(), e.getEvidenceKey(),
                        e.getEvidenceValue(), e.getEvidenceWeight(), e.getEvidenceSource()))
                .toList();

        List<GovernanceViolationResponse> violationResponses = violations.stream()
                .map(v -> new GovernanceViolationResponse(
                        v.getId(), v.getDecisionRecordId(), v.getRuleId(), null,
                        v.getViolationMessage(), v.getSeverity(), v.getAutoResolved(),
                        v.getCreatedAt() != null ? v.getCreatedAt().toString() : null))
                .toList();

        return new DecisionAuditResponse(
                r.getId(), r.getUserId(), r.getDeviceId(), r.getSourceSystem(),
                r.getDecisionType(), r.getSourceRecordId(), r.getDecisionOutput(),
                r.getConfidenceScore(), r.getTrustScore(), r.getTrustTier(),
                r.getRiskLevel(), r.getStatus(), r.getUserReviewed(), r.getUserFeedback(),
                r.getWhyExplanation(), r.getHowExplanation(),
                r.getWhatIfExplanation(), r.getImpactExplanation(),
                breakdown, evidenceResponses, violationResponses,
                r.getCreatedAt() != null ? r.getCreatedAt().toString() : null,
                r.getUpdatedAt() != null ? r.getUpdatedAt().toString() : null
        );
    }
}
