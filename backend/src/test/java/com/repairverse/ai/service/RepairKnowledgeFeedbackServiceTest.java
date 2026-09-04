package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairKnowledgeGraphDto.KnowledgeFeedbackRequest;
import com.repairverse.ai.dto.RepairKnowledgeGraphDto.PatternInsightResponse;
import com.repairverse.ai.entity.RepairKnowledgeFeedback;
import com.repairverse.ai.entity.RepairPatternInsight;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.RepairKnowledgeFeedbackRepository;
import com.repairverse.ai.repository.RepairPatternInsightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairKnowledgeFeedbackServiceTest {

    @Mock
    private RepairKnowledgeFeedbackRepository feedbackRepository;

    @Mock
    private RepairPatternInsightRepository insightRepository;

    @InjectMocks
    private RepairKnowledgeFeedbackService feedbackService;

    private RepairPatternInsight sampleInsight;

    @BeforeEach
    void setUp() {
        sampleInsight = RepairPatternInsight.builder()
                .id("insight-1")
                .insightType("COMMON_FAILURE")
                .title("Battery Degradation Pattern")
                .description("Telemetry analysis")
                .confidence(0.90)
                .impactScore(80)
                .supportingObservations(100)
                .status("ACTIVE")
                .generatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Submit HELPFUL feedback increases confidence and saves feedback")
    void testSubmitHelpfulFeedback() {
        when(insightRepository.findById("insight-1")).thenReturn(Optional.of(sampleInsight));
        when(insightRepository.save(any(RepairPatternInsight.class))).thenAnswer(i -> i.getArgument(0));
        when(feedbackRepository.save(any(RepairKnowledgeFeedback.class))).thenAnswer(i -> i.getArgument(0));
        when(feedbackRepository.countByInsightIdAndFeedbackType("insight-1", "HELPFUL")).thenReturn(1L);
        when(feedbackRepository.countByInsightIdAndFeedbackType("insight-1", "INACCURATE")).thenReturn(0L);

        KnowledgeFeedbackRequest req = new KnowledgeFeedbackRequest("HELPFUL", 5, "Spot on!");
        PatternInsightResponse res = feedbackService.submitFeedback("insight-1", "usr-1", req);

        assertThat(res.confidence()).isEqualTo(0.91);
        assertThat(res.impactScore()).isEqualTo(81);
        assertThat(res.helpfulVotes()).isEqualTo(1L);
        verify(feedbackRepository, times(1)).save(any(RepairKnowledgeFeedback.class));
    }

    @Test
    @DisplayName("Submit feedback for non-existent insight throws ResourceNotFoundException")
    void testSubmitFeedbackNotFound() {
        when(insightRepository.findById("missing-id")).thenReturn(Optional.empty());

        KnowledgeFeedbackRequest req = new KnowledgeFeedbackRequest("HELPFUL", 5, null);
        assertThatThrownBy(() -> feedbackService.submitFeedback("missing-id", "usr-1", req))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
