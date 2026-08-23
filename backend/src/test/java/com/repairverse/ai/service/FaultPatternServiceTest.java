package com.repairverse.ai.service;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.entity.FaultPattern;
import com.repairverse.ai.repository.FaultPatternRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FaultPatternServiceTest {

    @Mock
    private FaultPatternRepository faultPatternRepository;

    @InjectMocks
    private FaultPatternService faultPatternService;

    @Test
    @DisplayName("Should return all active fault patterns")
    void getActivePatterns_Success() {
        FaultPattern fp = FaultPattern.builder()
                .id("fp-1")
                .deviceCategory("Smartphone")
                .faultType("Battery Degradation")
                .riskWeight(7)
                .isActive(true)
                .preventiveActions("Limit charge cycles|Avoid high heat")
                .build();

        when(faultPatternRepository.findByIsActiveTrueOrderByRiskWeightDesc()).thenReturn(List.of(fp));

        List<FaultPatternDto> result = faultPatternService.getActivePatterns();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).faultType()).isEqualTo("Battery Degradation");
        assertThat(result.get(0).preventiveActions()).hasSize(2);
    }

    @Test
    @DisplayName("Should return fault patterns by category")
    void getPatternsByCategory_Success() {
        FaultPattern fp = FaultPattern.builder()
                .id("fp-2")
                .deviceCategory("Laptop")
                .faultType("Thermal Paste Degradation")
                .riskWeight(8)
                .isActive(true)
                .build();

        when(faultPatternRepository.findActiveByCategory("Laptop")).thenReturn(List.of(fp));

        List<FaultPatternDto> result = faultPatternService.getPatternsByCategory("Laptop");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).faultType()).isEqualTo("Thermal Paste Degradation");
    }
}
