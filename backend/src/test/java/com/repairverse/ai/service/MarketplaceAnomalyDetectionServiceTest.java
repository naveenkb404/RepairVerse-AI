package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairNetworkIntelligenceDto.*;
import com.repairverse.ai.entity.MarketplaceAnomaly;
import com.repairverse.ai.repository.MarketplaceAnomalyRepository;
import com.repairverse.ai.repository.RepairServiceOutcomeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketplaceAnomalyDetectionServiceTest {

    @Mock MarketplaceAnomalyRepository anomalyRepository;
    @Mock RepairServiceOutcomeRepository outcomeRepository;
    @InjectMocks MarketplaceAnomalyDetectionService service;

    @Test
    void detectAnomalies_insufficientData_returnsNoAnomalies() {
        when(outcomeRepository.countByRepairShopId("shop-1")).thenReturn(3L);
        when(outcomeRepository.countSuccessfulByShopId("shop-1")).thenReturn(2L);
        when(outcomeRepository.countRepeatRepairsByShopId("shop-1")).thenReturn(1L);
        when(anomalyRepository.countActiveByShopId("shop-1")).thenReturn(0L);
        when(anomalyRepository.saveAll(any())).thenReturn(List.of());

        List<MarketplaceAnomalyResponse> result = service.detectAndSaveAnomalies("shop-1");
        assertThat(result).isEmpty();
    }

    @Test
    void detectAnomalies_lowSuccessRate_flagsAnomaly() {
        when(outcomeRepository.countByRepairShopId("shop-2")).thenReturn(50L);
        when(outcomeRepository.countSuccessfulByShopId("shop-2")).thenReturn(25L);   // 50%
        when(outcomeRepository.countRepeatRepairsByShopId("shop-2")).thenReturn(5L);
        when(anomalyRepository.countActiveByShopId("shop-2")).thenReturn(0L);
        when(anomalyRepository.saveAll(any())).thenAnswer(inv -> {
            List<MarketplaceAnomaly> saved = inv.getArgument(0);
            saved.forEach(a -> { if (a.getId() == null) a.setId("anom-" + System.nanoTime()); });
            return saved;
        });

        List<MarketplaceAnomalyResponse> result = service.detectAndSaveAnomalies("shop-2");
        assertThat(result).anyMatch(a -> "LOW_SUCCESS_RATE".equals(a.anomalyType()));
    }

    @Test
    void updateAnomalyStatus_invalidTransition_throwsException() {
        MarketplaceAnomaly resolved = MarketplaceAnomaly.builder()
            .id("anom-1").repairShopId("shop-1").anomalyType("LOW_SUCCESS_RATE")
            .severity("HIGH").riskScore(70).description("Low rate")
            .status("RESOLVED").detectedAt(LocalDateTime.now()).build();

        when(anomalyRepository.findById("anom-1")).thenReturn(Optional.of(resolved));

        assertThatThrownBy(() -> service.updateAnomalyStatus("anom-1", "UNDER_REVIEW"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Invalid anomaly status transition");
    }

    @Test
    void getAnomalies_delegatesToRepository() {
        when(anomalyRepository.findByStatus("OPEN")).thenReturn(List.of());
        List<MarketplaceAnomalyResponse> result = service.getAnomalies("OPEN", null);
        assertThat(result).isEmpty();
        verify(anomalyRepository).findByStatus("OPEN");
    }
}
