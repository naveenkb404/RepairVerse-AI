package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairNetworkIntelligenceDto.*;
import com.repairverse.ai.entity.RepairServiceOutcome;
import com.repairverse.ai.repository.RepairServiceOutcomeRepository;
import com.repairverse.ai.repository.RepairShopQualitySnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairOutcomeAnalyticsServiceTest {

    @Mock RepairServiceOutcomeRepository outcomeRepository;
    @Mock RepairShopQualitySnapshotRepository snapshotRepository;
    @InjectMocks RepairOutcomeAnalyticsService service;

    @Test
    void getShopOutcomes_emptyDataset_returnsZeroMetrics() {
        when(outcomeRepository.findByRepairShopId("shop-1")).thenReturn(List.of());

        RepairOutcomeAnalyticsResponse resp = service.getShopOutcomes("shop-1");

        assertThat(resp.totalRepairs()).isZero();
        assertThat(resp.successRate()).isZero();
    }

    @Test
    void getShopOutcomes_withMixedData_calculatesCorrectRates() {
        RepairServiceOutcome success = RepairServiceOutcome.builder()
            .id("rso-1").repairShopId("shop-2").userId("usr-1").deviceId("dev-1")
            .repairCategory("Smartphone").repairSuccessful(true)
            .repeatRepairRequired(false).repairCost(79.0).estimatedCost(75.0)
            .completedAt(LocalDateTime.now()).build();

        RepairServiceOutcome failed = RepairServiceOutcome.builder()
            .id("rso-2").repairShopId("shop-2").userId("usr-1").deviceId("dev-2")
            .repairCategory("Smartphone").repairSuccessful(false)
            .repeatRepairRequired(false).repairCost(60.0).estimatedCost(75.0)
            .completedAt(LocalDateTime.now()).build();

        when(outcomeRepository.findByRepairShopId("shop-2")).thenReturn(List.of(success, failed));

        RepairOutcomeAnalyticsResponse resp = service.getShopOutcomes("shop-2");

        assertThat(resp.totalRepairs()).isEqualTo(2);
        assertThat(resp.successfulRepairs()).isEqualTo(1);
        assertThat(resp.successRate()).isEqualTo(0.5);
        assertThat(resp.failureRate()).isEqualTo(0.5);
    }

    @Test
    void getShopTrends_returns6Periods() {
        List<QualityTrendResponse> trends = service.getShopTrends("shop-1");
        assertThat(trends).hasSize(6);
        assertThat(trends.get(5).qualityScore()).isGreaterThan(trends.get(0).qualityScore());
    }
}
