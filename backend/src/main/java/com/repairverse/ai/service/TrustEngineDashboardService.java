package com.repairverse.ai.service;

import com.repairverse.ai.dto.TrustEngineDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Phase 34: Assembles the full Trust Engine Dashboard response for a user.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrustEngineDashboardService {

    private final AiDecisionRecordRepository decisionRecordRepository;
    private final AiGovernanceViolationRepository violationRepository;
    private final DecisionAuditService decisionAuditService;
    private final UserConsentControlService consentControlService;

    /**
     * Build a comprehensive trust dashboard for the user.
     */
    public TrustDashboardResponse getDashboard(String userId) {
        List<AiDecisionRecord> allDecisions = decisionRecordRepository
                .findAllByUserIdOrderByCreatedAtDesc(userId);

        int total = allDecisions.size();
        int verifiedCount = (int) allDecisions.stream().filter(d -> "VERIFIED".equals(d.getTrustTier())).count();
        int reliableCount = (int) allDecisions.stream().filter(d -> "RELIABLE".equals(d.getTrustTier())).count();
        int cautionCount = (int) allDecisions.stream().filter(d -> "CAUTION".equals(d.getTrustTier())).count();
        int reviewCount = (int) allDecisions.stream().filter(d -> "REVIEW_REQUIRED".equals(d.getTrustTier())).count();
        double avgTrust = total > 0
                ? Math.round(allDecisions.stream().mapToInt(AiDecisionRecord::getTrustScore).average().orElse(0) * 10.0) / 10.0
                : 0.0;
        int reviewedByUser = (int) allDecisions.stream().filter(d -> Boolean.TRUE.equals(d.getUserReviewed())).count();

        // Active (unresolved) violations across all user decisions
        List<GovernanceViolationResponse> activeViolationsList = new ArrayList<>();
        for (AiDecisionRecord record : allDecisions) {
            List<AiGovernanceViolation> violations = violationRepository.findAllByDecisionRecordId(record.getId());
            for (AiGovernanceViolation v : violations) {
                if (!Boolean.TRUE.equals(v.getAutoResolved())) {
                    activeViolationsList.add(new GovernanceViolationResponse(
                            v.getId(), v.getDecisionRecordId(), v.getRuleId(), null,
                            v.getViolationMessage(), v.getSeverity(), v.getAutoResolved(),
                            v.getCreatedAt() != null ? v.getCreatedAt().toString() : null
                    ));
                }
            }
        }

        // Per-system trust stats
        List<SystemTrustStats> systemStats = buildSystemStats(allDecisions);

        // Recent decisions (top 10)
        List<DecisionSummaryResponse> recentDecisions = allDecisions.stream()
                .limit(10)
                .map(r -> new DecisionSummaryResponse(
                        r.getId(), r.getDeviceId(), r.getSourceSystem(), r.getDecisionType(),
                        r.getConfidenceScore(), r.getTrustScore(), r.getTrustTier(),
                        r.getRiskLevel(), r.getStatus(), r.getUserReviewed(), r.getUserFeedback(),
                        r.getCreatedAt() != null ? r.getCreatedAt().toString() : null
                ))
                .toList();

        // User autonomy preferences
        UserAutonomyPreferencesResponse autonomy = consentControlService.getPreferences(userId);

        return new TrustDashboardResponse(
                userId, total, verifiedCount, reliableCount, cautionCount, reviewCount,
                avgTrust, activeViolationsList.size(), reviewedByUser,
                systemStats, recentDecisions, activeViolationsList, autonomy
        );
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    private List<SystemTrustStats> buildSystemStats(List<AiDecisionRecord> allDecisions) {
        Map<String, List<AiDecisionRecord>> bySystem = allDecisions.stream()
                .collect(Collectors.groupingBy(AiDecisionRecord::getSourceSystem));

        return bySystem.entrySet().stream().map(entry -> {
            String system = entry.getKey();
            List<AiDecisionRecord> records = entry.getValue();
            int count = records.size();
            double avgTrust = records.stream().mapToInt(AiDecisionRecord::getTrustScore).average().orElse(0);
            int avgConfidence = (int) Math.round(records.stream().mapToInt(AiDecisionRecord::getConfidenceScore).average().orElse(0));

            // Dominant tier
            Map<String, Long> tierCounts = records.stream()
                    .collect(Collectors.groupingBy(AiDecisionRecord::getTrustTier, Collectors.counting()));
            String dominantTier = tierCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("RELIABLE");

            int agreeCount = (int) records.stream().filter(r -> "AGREE".equals(r.getUserFeedback())).count();
            int disagreeCount = (int) records.stream().filter(r -> "DISAGREE".equals(r.getUserFeedback())).count();

            return new SystemTrustStats(system, count,
                    Math.round(avgTrust * 10.0) / 10.0, avgConfidence,
                    dominantTier, agreeCount, disagreeCount);
        }).toList();
    }
}
