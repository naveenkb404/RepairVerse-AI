package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairKnowledgeGraphDto.PatternInsightResponse;
import com.repairverse.ai.dto.RepairKnowledgeGraphDto.RepairSuccessPatternResponse;
import com.repairverse.ai.entity.RepairPatternInsight;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairPatternDiscoveryServiceTest {

    @Mock
    private RepairPatternInsightRepository insightRepository;

    @Mock
    private RepairKnowledgeFeedbackRepository feedbackRepository;

    @InjectMocks
    private RepairPatternDiscoveryService patternDiscoveryService;

    private RepairPatternInsight sampleInsight;

    @BeforeEach
    void setUp() {
        sampleInsight = RepairPatternInsight.builder()
                .id("insight-1")
                .insightType("COMMON_FAILURE")
                .title("Battery Degradation Beyond 600 Cycles")
                .description("Internal resistance surges causing unexpected shutdowns.")
                .confidence(0.95)
                .impactScore(88)
                .supportingObservations(240)
                .deviceCategory("LAPTOP")
                .status("ACTIVE")
                .generatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Get active insights returns mapped list with feedback counts")
    void testGetActiveInsights() {
        when(insightRepository.count()).thenReturn(1L);
        when(insightRepository.findByStatusOrderByImpactScoreDesc("ACTIVE"))
                .thenReturn(List.of(sampleInsight));
        when(feedbackRepository.countByInsightIdAndFeedbackType("insight-1", "HELPFUL")).thenReturn(12L);
        when(feedbackRepository.countByInsightIdAndFeedbackType("insight-1", "INACCURATE")).thenReturn(1L);

        List<PatternInsightResponse> results = patternDiscoveryService.getActiveInsights(null, null);

        assertThat(results).hasSize(1);
        PatternInsightResponse res = results.get(0);
        assertThat(res.title()).isEqualTo("Battery Degradation Beyond 600 Cycles");
        assertThat(res.helpfulVotes()).isEqualTo(12L);
        assertThat(res.inaccurateVotes()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Get repair success patterns returns benchmarked strategies")
    void testGetRepairSuccessPatterns() {
        List<RepairSuccessPatternResponse> patterns = patternDiscoveryService.getRepairSuccessPatterns();

        assertThat(patterns).isNotEmpty();
        assertThat(patterns.get(0).successRate()).isGreaterThan(90.0);
    }

    @Test
    @DisplayName("Get insight by ID returns valid insight")
    void testGetInsightById() {
        when(insightRepository.findById("insight-1")).thenReturn(Optional.of(sampleInsight));
        when(feedbackRepository.countByInsightIdAndFeedbackType("insight-1", "HELPFUL")).thenReturn(5L);
        when(feedbackRepository.countByInsightIdAndFeedbackType("insight-1", "INACCURATE")).thenReturn(0L);

        PatternInsightResponse res = patternDiscoveryService.getInsightById("insight-1");

        assertThat(res.id()).isEqualTo("insight-1");
        assertThat(res.impactScore()).isEqualTo(88);
    }
}
