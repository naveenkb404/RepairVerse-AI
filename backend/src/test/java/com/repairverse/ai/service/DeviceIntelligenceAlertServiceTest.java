package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceIntelligenceDto.DeviceIntelligenceAlertResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceIntelligenceAlert;
import com.repairverse.ai.repository.DeviceIntelligenceAlertRepository;
import com.repairverse.ai.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceIntelligenceAlertServiceTest {

    @Mock
    private DeviceIntelligenceAlertRepository alertRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceIntelligenceAlertService alertService;

    private Device sampleDevice;

    @BeforeEach
    void setUp() {
        sampleDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("iPhone 14 Pro")
                .category("smartphone")
                .build();
    }

    @Test
    @DisplayName("Evaluate and generate failure risk alert when risk is high")
    void testGenerateFailureRiskAlert() {
        when(alertRepository.findFirstByDeviceIdAndUserIdAndAlertTypeAndIsReadFalse("dev-1", "usr-1", "FAILURE_RISK"))
                .thenReturn(Optional.empty());

        alertService.evaluateAndGenerateAlerts(sampleDevice, "usr-1", 35, 75, "REPAIR_NOW", 80, 70);

        verify(alertRepository, atLeastOnce()).save(any(DeviceIntelligenceAlert.class));
    }

    @Test
    @DisplayName("Prevent duplicate active unread alerts for same type")
    void testPreventDuplicateAlerts() {
        DeviceIntelligenceAlert existing = DeviceIntelligenceAlert.builder()
                .id("alt-1")
                .deviceId("dev-1")
                .userId("usr-1")
                .alertType("FAILURE_RISK")
                .isRead(false)
                .build();

        when(alertRepository.findFirstByDeviceIdAndUserIdAndAlertTypeAndIsReadFalse("dev-1", "usr-1", "FAILURE_RISK"))
                .thenReturn(Optional.of(existing));

        alertService.evaluateAndGenerateAlerts(sampleDevice, "usr-1", 35, 75, "REPAIR_NOW", 80, 70);

        // Should not save duplicate FAILURE_RISK alert
        verify(alertRepository, never()).save(argThat(a -> "FAILURE_RISK".equals(a.getAlertType())));
    }

    @Test
    @DisplayName("Mark alert as read transitions isRead flag to true")
    void testMarkAlertAsRead() {
        DeviceIntelligenceAlert alert = DeviceIntelligenceAlert.builder()
                .id("alt-1")
                .deviceId("dev-1")
                .userId("usr-1")
                .alertType("MAINTENANCE_REQUIRED")
                .title("Maintenance Overdue")
                .message("Check ports")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(alertRepository.findByIdAndUserId("alt-1", "usr-1")).thenReturn(Optional.of(alert));
        when(alertRepository.save(any())).thenReturn(alert);
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(sampleDevice));

        DeviceIntelligenceAlertResponse response = alertService.markAlertAsRead("alt-1", "usr-1");

        assertThat(response.isRead()).isTrue();
        assertThat(alert.getIsRead()).isTrue();
    }
}
