package com.repairverse.ai.service;

import com.repairverse.ai.dto.FederatedLearningDto.DeviceLearningProfileResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.IntelligenceModelVersion;
import com.repairverse.ai.entity.RepairLearningSignal;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.RepairLearningSignalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearningDecisionIntelligenceServiceTest {

    @Mock
    private LearningModelVersionService modelVersionService;

    @Mock
    private RepairLearningSignalRepository signalRepository;

    @Mock
    private DeviceRepository deviceRepository;

    private LearningDecisionIntelligenceService decisionIntelligenceService;

    @BeforeEach
    void setUp() {
        decisionIntelligenceService = new LearningDecisionIntelligenceService(
                modelVersionService,
                signalRepository,
                deviceRepository
        );
    }

    @Test
    @DisplayName("Should generate privacy-preserving device learning profile")
    void testGetDeviceLearningProfile() {
        Device device = Device.builder()
                .id("dev-123")
                .category("LAPTOP")
                .build();

        IntelligenceModelVersion activeModel = IntelligenceModelVersion.builder()
                .version("R35.4")
                .build();

        RepairLearningSignal signal = RepairLearningSignal.builder()
                .id("sig-1")
                .signalType("REPAIR_SUCCESS")
                .deviceCategory("LAPTOP")
                .componentType("THERMAL_SYSTEM")
                .failureMode("OVERHEATING")
                .repairAction("THERMAL_CLEAN")
                .outcomeClass("HIGH_SUCCESS_REPAIR")
                .aggregatedFrequency(10)
                .successRate(0.94)
                .averageCost(1200.0)
                .averageLifespanGain(30)
                .sustainabilityScore(92.0)
                .confidence(0.95)
                .observationCount(25)
                .createdAt(LocalDateTime.now())
                .build();

        when(deviceRepository.findById("dev-123")).thenReturn(Optional.of(device));
        when(modelVersionService.getActiveModel()).thenReturn(activeModel);
        when(signalRepository.findAllByDeviceCategory("LAPTOP")).thenReturn(List.of(signal));

        DeviceLearningProfileResponse profile = decisionIntelligenceService.getDeviceLearningProfile("dev-123");

        assertNotNull(profile);
        assertEquals("LAPTOP", profile.deviceCategory());
        assertEquals("R35.4", profile.activeModelVersion());
        assertEquals(25, profile.matchingEcosystemObservations());
        assertEquals(0.94, profile.ecosystemSuccessRate());
        assertEquals(30, profile.expectedLifespanGainMonths());
        assertTrue(profile.privacyNotice().contains("privacy-filtered"));
    }
}
