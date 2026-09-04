package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairNetworkIntelligenceDto.*;
import com.repairverse.ai.repository.MarketplaceAnomalyRepository;
import com.repairverse.ai.repository.RepairServiceOutcomeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairTrustIntelligenceServiceTest {

    @Mock RepairServiceOutcomeRepository outcomeRepository;
    @Mock MarketplaceAnomalyRepository anomalyRepository;
    @InjectMocks RepairTrustIntelligenceService service;

    @Test
    void withNoData_returnsBaseline50Score() {
        when(outcomeRepository.countByRepairShopId("shop-1")).thenReturn(0L);
        when(outcomeRepository.countSuccessfulByShopId("shop-1")).thenReturn(0L);
        when(outcomeRepository.countRepeatRepairsByShopId("shop-1")).thenReturn(0L);
        when(outcomeRepository.countWarrantyClaimsByShopId("shop-1")).thenReturn(0L);
        when(outcomeRepository.countFailedByShopId("shop-1")).thenReturn(0L);
        when(anomalyRepository.countActiveByShopId("shop-1")).thenReturn(0L);
        when(outcomeRepository.avgSatisfactionByShopId("shop-1")).thenReturn(null);

        TrustScoreResponse resp = service.calculateTrustScore("shop-1", "Test Shop");

        assertThat(resp.trustScore()).isEqualTo(50);
        assertThat(resp.trustTier()).isEqualTo("MODERATE");
    }

    @Test
    void withHighPerformance_returnsExceptionalTier() {
        when(outcomeRepository.countByRepairShopId("shop-2")).thenReturn(150L);
        when(outcomeRepository.countSuccessfulByShopId("shop-2")).thenReturn(142L);
        when(outcomeRepository.countRepeatRepairsByShopId("shop-2")).thenReturn(5L);
        when(outcomeRepository.countWarrantyClaimsByShopId("shop-2")).thenReturn(4L);
        when(outcomeRepository.countFailedByShopId("shop-2")).thenReturn(8L);
        when(anomalyRepository.countActiveByShopId("shop-2")).thenReturn(0L);
        when(outcomeRepository.avgSatisfactionByShopId("shop-2")).thenReturn(4.8);

        TrustScoreResponse resp = service.calculateTrustScore("shop-2", "Elite Shop");

        assertThat(resp.trustScore()).isGreaterThanOrEqualTo(75);
        assertThat(resp.positiveSignals()).isNotEmpty();
    }

    @Test
    void classifyTrustTier_boundaries() {
        assertThat(service.classifyTrustTier(90)).isEqualTo("EXCEPTIONAL");
        assertThat(service.classifyTrustTier(75)).isEqualTo("HIGH");
        assertThat(service.classifyTrustTier(60)).isEqualTo("ESTABLISHED");
        assertThat(service.classifyTrustTier(40)).isEqualTo("MODERATE");
        assertThat(service.classifyTrustTier(39)).isEqualTo("LOW");
    }

    @Test
    void withActiveAnomalies_reducesScore() {
        when(outcomeRepository.countByRepairShopId("shop-3")).thenReturn(0L);
        when(outcomeRepository.countSuccessfulByShopId("shop-3")).thenReturn(0L);
        when(outcomeRepository.countRepeatRepairsByShopId("shop-3")).thenReturn(0L);
        when(outcomeRepository.countWarrantyClaimsByShopId("shop-3")).thenReturn(0L);
        when(outcomeRepository.countFailedByShopId("shop-3")).thenReturn(0L);
        when(anomalyRepository.countActiveByShopId("shop-3")).thenReturn(3L);
        when(outcomeRepository.avgSatisfactionByShopId("shop-3")).thenReturn(null);

        TrustScoreResponse resp = service.calculateTrustScore("shop-3", "Flagged Shop");

        assertThat(resp.trustScore()).isLessThan(50);
        assertThat(resp.riskSignals()).isNotEmpty();
    }
}
