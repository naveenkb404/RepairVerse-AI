package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairKnowledgeGraphDto.KnowledgeFeedbackRequest;
import com.repairverse.ai.dto.RepairKnowledgeGraphDto.PatternInsightResponse;
import com.repairverse.ai.entity.RepairKnowledgeFeedback;
import com.repairverse.ai.entity.RepairPatternInsight;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.RepairKnowledgeFeedbackRepository;
import com.repairverse.ai.repository.RepairPatternInsightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairKnowledgeFeedbackService {

    private final RepairKnowledgeFeedbackRepository feedbackRepository;
    private final RepairPatternInsightRepository insightRepository;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Submit user feedback on a pattern insight.
     */
    @Transactional
    public PatternInsightResponse submitFeedback(String insightId, String userId, KnowledgeFeedbackRequest request) {
        RepairPatternInsight insight = insightRepository.findById(insightId)
                .orElseThrow(() -> new ResourceNotFoundException("Pattern insight not found with ID: " + insightId));

        String feedbackType = request.feedbackType() != null ? request.feedbackType().toUpperCase() : "HELPFUL";

        // Save feedback entry
        RepairKnowledgeFeedback feedback = RepairKnowledgeFeedback.builder()
                .insightId(insightId)
                .userId(userId)
                .feedbackType(feedbackType)
                .rating(request.rating())
                .comment(request.comment())
                .build();
        feedbackRepository.save(feedback);

        // Deterministically adjust insight score
        if ("HELPFUL".equals(feedbackType) || "ACCURATE".equals(feedbackType)) {
            insight.setConfidence(Math.min(0.99, insight.getConfidence() + 0.01));
            insight.setImpactScore(Math.min(100, insight.getImpactScore() + 1));
            insight.setSupportingObservations(insight.getSupportingObservations() + 1);
        } else if ("NOT_HELPFUL".equals(feedbackType) || "INACCURATE".equals(feedbackType)) {
            insight.setConfidence(Math.max(0.50, insight.getConfidence() - 0.02));
            insight.setImpactScore(Math.max(10, insight.getImpactScore() - 2));
        }

        insight.setUpdatedAt(LocalDateTime.now());
        RepairPatternInsight saved = insightRepository.save(insight);

        long helpfulVotes = feedbackRepository.countByInsightIdAndFeedbackType(saved.getId(), "HELPFUL");
        long inaccurateVotes = feedbackRepository.countByInsightIdAndFeedbackType(saved.getId(), "INACCURATE");

        log.info("Recorded feedback '{}' for insight '{}' by user '{}'", feedbackType, insightId, userId);

        return new PatternInsightResponse(
                saved.getId(),
                saved.getInsightType(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getConfidence(),
                saved.getImpactScore(),
                saved.getSupportingObservations(),
                saved.getDeviceCategory(),
                saved.getStatus(),
                saved.getGeneratedAt() != null ? saved.getGeneratedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER),
                helpfulVotes,
                inaccurateVotes
        );
    }
}
