package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairPlanningDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairJourneyServiceTest {

    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;
    @Mock
    private DevicePredictionRepository devicePredictionRepository;
    @Mock
    private AIRecommendationRepository recommendationRepository;
    @Mock
    private RepairActionPlanRepository actionPlanRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private RepairHistoryRepository repairHistoryRepository;

    @InjectMocks
    private RepairJourneyService repairJourneyService;

    private Device testDevice;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
            .id("dev-watch-1")
            .userId("usr-1")
            .deviceName("Apple Watch Series 7")
            .createdAt(LocalDateTime.now().minusMonths(6))
            .build();
    }

    @Test
    @DisplayName("Maps unified 9-stage repair journey with accurate stage progression")
    void testRepairJourneyPipelineProgression() {
        when(deviceRepository.findByIdAndUserId("dev-watch-1", "usr-1")).thenReturn(Optional.of(testDevice));

        DiagnosisReport diag = DiagnosisReport.builder()
            .id("diag-w1")
            .deviceId("dev-watch-1")
            .probableIssue("OLED Impact Fracture")
            .createdAt(LocalDateTime.now().minusDays(5))
            .build();
        when(diagnosisReportRepository.findTopByDeviceIdOrderByCreatedAtDesc("dev-watch-1")).thenReturn(Optional.of(diag));

        DevicePrediction pred = DevicePrediction.builder()
            .deviceId("dev-watch-1")
            .predictionScore(40)
            .riskLevel("HIGH")
            .evaluatedAt(LocalDateTime.now().minusDays(4))
            .build();
        when(devicePredictionRepository.findByDeviceId("dev-watch-1")).thenReturn(Optional.of(pred));

        when(recommendationRepository.findByDiagnosisId("diag-w1")).thenReturn(Optional.empty());
        when(actionPlanRepository.findFirstByDeviceIdAndUserIdOrderByCreatedAtDesc("dev-watch-1", "usr-1")).thenReturn(Optional.empty());
        when(bookingRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(Collections.emptyList());
        when(repairHistoryRepository.findByDeviceIdOrderByRepairDateDesc("dev-watch-1")).thenReturn(Collections.emptyList());

        RepairJourneyResponse journey = repairJourneyService.getRepairJourney("dev-watch-1", "usr-1");

        assertThat(journey).isNotNull();
        assertThat(journey.totalStages()).isEqualTo(9);
        assertThat(journey.stages()).hasSize(9);
        assertThat(journey.stages().get(0).isCompleted()).isTrue(); // Registered
        assertThat(journey.stages().get(1).isCompleted()).isTrue(); // Diagnosis
        assertThat(journey.stages().get(2).isCompleted()).isTrue(); // Risk analyzed
        assertThat(journey.stages().get(3).isCompleted()).isFalse(); // Recommendation
        assertThat(journey.nextRecommendedAction()).isNotEmpty();
    }

    @Test
    @DisplayName("Rejects cross-user repair journey queries")
    void testCrossUserUnauthorizedAccess() {
        when(deviceRepository.findByIdAndUserId("dev-watch-1", "intruder")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> repairJourneyService.getRepairJourney("dev-watch-1", "intruder"))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
