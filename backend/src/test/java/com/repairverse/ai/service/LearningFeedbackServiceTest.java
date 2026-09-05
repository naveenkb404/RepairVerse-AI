package com.repairverse.ai.service;

import com.repairverse.ai.dto.FederatedLearningDto.LearningFeedbackRequest;
import com.repairverse.ai.entity.IntelligenceModelVersion;
import com.repairverse.ai.entity.LearningFeedback;
import com.repairverse.ai.repository.IntelligenceModelVersionRepository;
import com.repairverse.ai.repository.LearningFeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningFeedbackServiceTest {

    @Mock
    private LearningFeedbackRepository feedbackRepository;

    @Mock
    private IntelligenceModelVersionRepository modelRepository;

    private LearningFeedbackService feedbackService;

    @BeforeEach
    void setUp() {
        feedbackService = new LearningFeedbackService(feedbackRepository, modelRepository);
    }

    @Test
    @DisplayName("Should record learning feedback with appropriate outcome quality weight")
    void testRecordFeedback() {
        IntelligenceModelVersion model = IntelligenceModelVersion.builder()
                .version("R35.4")
                .build();

        when(modelRepository.findByVersion("R35.4")).thenReturn(Optional.of(model));
        when(feedbackRepository.save(any(LearningFeedback.class))).thenAnswer(inv -> inv.getArgument(0));

        LearningFeedbackRequest req = new LearningFeedbackRequest("R35.4", "dec-001", "AGREE", 1.0);
        LearningFeedback feedback = feedbackService.recordFeedback(req);

        assertEquals("AGREE", feedback.getFeedbackType());
        assertEquals(1.0, feedback.getOutcomeQuality());
        verify(feedbackRepository, times(1)).save(any(LearningFeedback.class));
    }
}
