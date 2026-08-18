package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceDto.DeviceDetailDto;
import com.repairverse.ai.dto.DevicePassportDto.DevicePassportResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.entity.DiagnosisReport;
import com.repairverse.ai.exception.DeviceNotFoundException;
import com.repairverse.ai.repository.DeviceHealthRepository;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.DiagnosisReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DevicePassportServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceHealthRepository deviceHealthRepository;

    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DevicePassportService devicePassportService;

    private Device sampleDevice;
    private DeviceHealth sampleHealth;
    private DiagnosisReport sampleDiagnosis;
    private DeviceDetailDto sampleDetailDto;

    @BeforeEach
    void setUp() {
        sampleDevice = Device.builder()
                .id("dev-1")
                .userId("usr-123")
                .deviceName("Personal iPhone 14 Pro")
                .category("Smartphone")
                .brand("Apple")
                .model("iPhone 14 Pro")
                .purchaseDate("2023-01-15")
                .currentCondition("Good")
                .createdAt(LocalDateTime.now())
                .build();

        sampleHealth = DeviceHealth.builder()
                .id("hlth-1")
                .deviceId("dev-1")
                .healthScore(86)
                .batteryHealth(88)
                .lastService("2024-02-10")
                .maintenanceDue("2024-11-15")
                .aiPrediction("Battery degradation detected (88%).")
                .build();

        sampleDiagnosis = DiagnosisReport.builder()
                .id("diag-1")
                .deviceId("dev-1")
                .probableIssue("Display Panel Fracture")
                .confidenceScore(92)
                .repairDifficulty("Moderate")
                .repairCost(85.0)
                .createdAt(LocalDateTime.now())
                .build();

        sampleDetailDto = DeviceDetailDto.builder()
                .id("dev-1")
                .userId("usr-123")
                .deviceName("Personal iPhone 14 Pro")
                .category("Smartphone")
                .brand("Apple")
                .model("iPhone 14 Pro")
                .purchaseDate("2023-01-15")
                .currentCondition("Good")
                .build();
    }

    @Test
    @DisplayName("Should generate complete Digital Health Passport with diagnosis history")
    void testGetPassportWithDiagnosis() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-123")).thenReturn(Optional.of(sampleDevice));
        when(deviceHealthRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(sampleHealth));
        when(diagnosisReportRepository.findByDeviceIdOrderByCreatedAtDesc("dev-1")).thenReturn(List.of(sampleDiagnosis));
        when(deviceService.mapToDetailDto(sampleDevice)).thenReturn(sampleDetailDto);

        DevicePassportResponse response = devicePassportService.getDevicePassport("dev-1", "usr-123");

        assertNotNull(response);
        assertTrue(response.success());
        assertNotNull(response.data());
        assertEquals("Personal iPhone 14 Pro", response.data().device().deviceName());
        assertEquals(88, response.data().health().batteryHealth());
        assertNotNull(response.data().diagnosisSummary());
        assertEquals("Display Panel Fracture", response.data().diagnosisSummary().probableIssue());
        assertEquals(92, response.data().diagnosisSummary().confidenceScore());
        assertNotNull(response.data().carbonSummary());
        assertTrue(response.data().carbonSummary().co2SavedKg() > 0);
        assertNotNull(response.data().lifecycleTimeline());
        assertFalse(response.data().lifecycleTimeline().isEmpty());
    }

    @Test
    @DisplayName("Should generate valid Passport when no diagnosis reports exist")
    void testGetPassportWithoutDiagnosis() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-123")).thenReturn(Optional.of(sampleDevice));
        when(deviceHealthRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(sampleHealth));
        when(diagnosisReportRepository.findByDeviceIdOrderByCreatedAtDesc("dev-1")).thenReturn(Collections.emptyList());
        when(deviceService.mapToDetailDto(sampleDevice)).thenReturn(sampleDetailDto);

        DevicePassportResponse response = devicePassportService.getDevicePassport("dev-1", "usr-123");

        assertNotNull(response);
        assertTrue(response.success());
        assertNull(response.data().diagnosisSummary());
        assertEquals(0, response.data().repairSummary().repairsCompleted());
        assertTrue(response.data().health().healthScore() > 0);
    }

    @Test
    @DisplayName("Should throw DeviceNotFoundException when device does not exist")
    void testGetPassportDeviceNotFound() {
        when(deviceRepository.findByIdAndUserId("nonexistent", "usr-123")).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () ->
                devicePassportService.getDevicePassport("nonexistent", "usr-123"));
    }
}
