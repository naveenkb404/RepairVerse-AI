package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairNetworkIntelligenceDto.*;
import com.repairverse.ai.repository.RepairServiceOutcomeRepository;
import com.repairverse.ai.repository.RepairShopQualitySnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairQualityScoringServiceTest {

    @Mock RepairServiceOutcomeRepository outcomeRepository;
    @Mock RepairShopQualitySnapshotRepository snapshotRepository;
    @InjectMocks RepairQualityScoringService service;

    @Test
    void whenNoOutcomeData_returnsHeuristicBaseline() {
        when(outcomeRepository.countByRepairShopId("shop-1")).thenReturn(0L);

        RepairShopQualityResponse resp = service.calculateShopQuality("shop-1", "Test Shop");

        assertThat(resp.shopId()).isEqualTo("shop-1");
        assertThat(resp.overallQualityScore()).isGreaterThan(0).isLessThanOrEqualTo(100);
        assertThat(resp.qualityTier()).isNotBlank();
        assertThat(resp.factorBreakdown()).hasSize(6);
    }

    @Test
    void withHighSuccessRate_returnsEliteTier() {
        when(outcomeRepository.countByRepairShopId("shop-2")).thenReturn(200L);
        when(outcomeRepository.countSuccessfulByShopId("shop-2")).thenReturn(195L);
        when(outcomeRepository.countFailedByShopId("shop-2")).thenReturn(5L);
        when(outcomeRepository.countRepeatRepairsByShopId("shop-2")).thenReturn(2L);
        when(outcomeRepository.countWarrantyClaimsByShopId("shop-2")).thenReturn(3L);
        when(outcomeRepository.avgSatisfactionByShopId("shop-2")).thenReturn(4.9);
        when(snapshotRepository.findLatestByRepairShopId("shop-2")).thenReturn(Optional.empty());
        when(snapshotRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RepairShopQualityResponse resp = service.calculateShopQuality("shop-2", "Elite Shop");

        assertThat(resp.overallQualityScore()).isGreaterThanOrEqualTo(80);
        assertThat(resp.qualityTier()).isIn("ELITE", "EXCELLENT");
    }

    @Test
    void classifyTier_boundaryValues() {
        assertThat(service.classifyTier(90)).isEqualTo("ELITE");
        assertThat(service.classifyTier(89)).isEqualTo("EXCELLENT");
        assertThat(service.classifyTier(80)).isEqualTo("EXCELLENT");
        assertThat(service.classifyTier(70)).isEqualTo("TRUSTED");
        assertThat(service.classifyTier(50)).isEqualTo("STANDARD");
        assertThat(service.classifyTier(49)).isEqualTo("NEEDS_IMPROVEMENT");
        assertThat(service.classifyTier(0)).isEqualTo("NEEDS_IMPROVEMENT");
    }
}
