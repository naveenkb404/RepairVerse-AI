package com.repairverse.ai.service;

import com.repairverse.ai.entity.AiDecisionRecord;
import com.repairverse.ai.repository.AiDecisionRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Phase 34: Handles user feedback (AGREE / DISAGREE / UNSURE) on AI decisions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionFeedbackService {

    private final AiDecisionRecordRepository decisionRecordRepository;

    /**
     * Submit user feedback on a decision.
     *
     * @param userId     the authenticated user
     * @param decisionId the decision to provide feedback on
     * @param feedback   AGREE, DISAGREE, or UNSURE
     */
    @Transactional
    public void submitFeedback(String userId, String decisionId, String feedback) {
        AiDecisionRecord record = decisionRecordRepository.findById(decisionId)
                .orElseThrow(() -> new IllegalArgumentException("Decision not found: " + decisionId));
        if (!record.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized access to decision " + decisionId);
        }
        if (!("AGREE".equals(feedback) || "DISAGREE".equals(feedback) || "UNSURE".equals(feedback))) {
            throw new IllegalArgumentException("Feedback must be AGREE, DISAGREE, or UNSURE");
        }
        record.setUserFeedback(feedback);
        record.setUserReviewed(true);
        decisionRecordRepository.save(record);
        log.info("User '{}' submitted feedback '{}' on decision '{}'", userId, feedback, decisionId);
    }

    /**
     * Calculate accuracy stats: how often users agree/disagree/unsure.
     */
    public AccuracyStats getSystemAccuracyStats(String userId) {
        long total = decisionRecordRepository.countByUserId(userId);
        long agree = decisionRecordRepository.countByUserIdAndUserFeedback(userId, "AGREE");
        long disagree = decisionRecordRepository.countByUserIdAndUserFeedback(userId, "DISAGREE");
        long unsure = decisionRecordRepository.countByUserIdAndUserFeedback(userId, "UNSURE");
        long noFeedback = total - agree - disagree - unsure;
        double accuracyRate = total > 0 ? ((double) agree / total) * 100.0 : 0.0;

        return new AccuracyStats(total, agree, disagree, unsure, noFeedback, Math.round(accuracyRate * 10.0) / 10.0);
    }

    public record AccuracyStats(
            long totalDecisions,
            long agreeCount,
            long disagreeCount,
            long unsureCount,
            long noFeedbackCount,
            double accuracyRate
    ) {}
}
