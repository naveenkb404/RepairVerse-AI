package com.repairverse.ai.service;

import com.repairverse.ai.dto.FederatedLearningDto.LearningFeedbackRequest;
import com.repairverse.ai.entity.IntelligenceModelVersion;
import com.repairverse.ai.entity.LearningFeedback;
import com.repairverse.ai.repository.IntelligenceModelVersionRepository;
import com.repairverse.ai.repository.LearningFeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Phase 35: Learning Feedback Service.
 * Collects aggregated human feedback (AGREE / DISAGREE / UNSURE) to calibrate learning weights.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LearningFeedbackService {

    private final LearningFeedbackRepository feedbackRepository;
    private final IntelligenceModelVersionRepository modelRepository;

    @Transactional
    public LearningFeedback recordFeedback(LearningFeedbackRequest request) {
        String versionStr = request.modelVersion() != null ? request.modelVersion() : "R35.4";
        IntelligenceModelVersion model = modelRepository.findByVersion(versionStr)
                .orElseGet(() -> modelRepository.findFirstByStatusOrderByActivatedAtDesc("ACTIVE").orElse(null));

        if (model == null) {
            throw new IllegalArgumentException("No valid model version found for feedback: " + versionStr);
        }

        double quality = 1.0;
        if ("DISAGREE".equalsIgnoreCase(request.feedbackType())) {
            quality = 0.0;
        } else if ("UNSURE".equalsIgnoreCase(request.feedbackType())) {
            quality = 0.5;
        }

        LearningFeedback feedback = LearningFeedback.builder()
                .modelVersion(model)
                .decisionReference(request.decisionReference() != null ? request.decisionReference() : "dec-ref-gen")
                .feedbackType(request.feedbackType() != null ? request.feedbackType() : "AGREE")
                .outcomeQuality(request.outcomeQuality() != null ? request.outcomeQuality() : quality)
                .feedbackWeight(1.0)
                .build();

        log.info("Learning feedback recorded for model '{}': type '{}'", versionStr, feedback.getFeedbackType());
        return feedbackRepository.save(feedback);
    }

    public List<LearningFeedback> getFeedbackForModel(String modelVersionId) {
        return feedbackRepository.findAllByModelVersionId(modelVersionId);
    }
}
