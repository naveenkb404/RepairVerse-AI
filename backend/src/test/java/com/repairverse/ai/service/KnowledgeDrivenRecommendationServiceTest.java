package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairKnowledgeGraphDto.KnowledgeRecommendationResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeDrivenRecommendationServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private RepairPatternDiscoveryService patternDiscoveryService;

    @InjectMocks
    private KnowledgeDrivenRecommendationService recommendationService;

    private Device sampleDevice;

    @BeforeEach
    void setUp() {
        sampleDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .brand("Apple")
                .model("MacBook Pro 16\"")
                .category("LAPTOP")
                .build();
    }

    @Test
    @DisplayName("Get recommendations for device returns traceable evidence-backed recommendations")
    void testGetRecommendationsForDevice() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(sampleDevice));

        List<KnowledgeRecommendationResponse> recs =
                recommendationService.getRecommendationsForDevice("dev-1", "usr-1");

        assertThat(recs).isNotEmpty();
        KnowledgeRecommendationResponse r = recs.get(0);
        assertThat(r.confidence()).isGreaterThan(0.85);
        assertThat(r.supportingCases()).isGreaterThan(50);
        assertThat(r.evidenceSummary()).contains("laptop repair logs");
        assertThat(r.reasoning()).isNotBlank();
    }

    @Test
    @DisplayName("Get best historical strategy returns high-confidence strategies for components")
    void testGetBestHistoricalStrategy() {
        String batteryStrategy = recommendationService.getBestHistoricalStrategy("LAPTOP", "Battery", "Wear");
        String thermalStrategy = recommendationService.getBestHistoricalStrategy("LAPTOP", "Heatsink", "Dryout");

        assertThat(batteryStrategy).contains("OEM Battery Replacement");
        assertThat(thermalStrategy).contains("Phase-Change Compound");
    }
}
