package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairKnowledgeGraphDto.SimilarRepairCaseResponse;
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
class SimilarRepairCaseServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private SimilarRepairCaseService similarCaseService;

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
    @DisplayName("Deterministic similarity calculation handles exact and partial matches")
    void testCalculateSimilarityScore() {
        double exactMatch = similarCaseService.calculateSimilarityScore(
                "LAPTOP", "LAPTOP",
                "MacBook Pro 16\"", "MacBook Pro 16\"",
                "Battery", "Battery",
                "Fast Drain", "Fast Drain",
                "Wear", "Wear"
        );

        double partialMatch = similarCaseService.calculateSimilarityScore(
                "LAPTOP", "LAPTOP",
                "ThinkPad X1", "Dell XPS 15",
                "Keyboard", "Heatsink",
                "Key Sticking", "Overheating",
                "Debris", "Thermal Dryout"
        );

        assertThat(exactMatch).isGreaterThan(95.0);
        assertThat(partialMatch).isLessThan(exactMatch);
        assertThat(partialMatch).isBetween(0.0, 100.0);
    }

    @Test
    @DisplayName("Find similar cases for device returns anonymized historical records")
    void testFindSimilarCasesForDevice() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(sampleDevice));

        List<SimilarRepairCaseResponse> cases = similarCaseService.findSimilarCasesForDevice("dev-1", "usr-1");

        assertThat(cases).isNotEmpty();
        SimilarRepairCaseResponse c = cases.get(0);
        assertThat(c.deviceCategory()).isEqualTo("LAPTOP");
        assertThat(c.outcomeStatus()).isEqualTo("FULLY_RESOLVED");
        assertThat(c.lessonLearned()).isNotBlank();
        assertThat(c.caseId()).startsWith("case-eco-");
    }
}
