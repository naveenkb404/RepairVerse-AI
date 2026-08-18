package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.exception.DeviceNotFoundException;
import com.repairverse.ai.repository.DeviceHealthRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceHealthRepository deviceHealthRepository;

    @InjectMocks
    private DeviceService deviceService;

    private Device sampleDevice;
    private DeviceHealth sampleHealth;

    @BeforeEach
    void setUp() {
        sampleDevice = Device.builder()
                .id("dev-1")
                .userId("usr-123")
                .deviceName("Personal iPhone 14")
                .category("Smartphone")
                .brand("Apple")
                .model("iPhone 14 (128GB)")
                .serialNumber("F2LX9001K992")
                .purchaseDate("2023-01-15")
                .warrantyExpiry("2024-01-15")
                .purchasePrice(999.0)
                .currentCondition("Good")
                .createdAt(LocalDateTime.now())
                .build();

        sampleHealth = DeviceHealth.builder()
                .id("hlth-1")
                .deviceId("dev-1")
                .healthScore(85)
                .batteryHealth(88)
                .lastService("2023-01-15")
                .aiPrediction("Device operating normally.")
                .build();
    }

    @Test
    @DisplayName("Should create device and initialize health record")
    void testCreateDevice() {
        CreateDeviceRequest request = new CreateDeviceRequest(
                "Personal iPhone 14",
                "Smartphone",
                "Apple",
                "iPhone 14 (128GB)",
                "F2LX9001K992",
                "2023-01-15",
                "2024-01-15",
                999.0,
                "Good"
        );

        when(deviceRepository.save(any(Device.class))).thenAnswer(i -> {
            Device d = i.getArgument(0);
            return d;
        });
        when(deviceHealthRepository.save(any(DeviceHealth.class))).thenAnswer(i -> i.getArgument(0));

        DeviceResponse response = deviceService.createDevice(request, "usr-123");

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("Personal iPhone 14", response.data().deviceName());
        assertEquals("Apple", response.data().brand());
        assertEquals("Smartphone", response.data().category());

        verify(deviceRepository, times(1)).save(any(Device.class));
        verify(deviceHealthRepository, times(1)).save(any(DeviceHealth.class));
    }

    @Test
    @DisplayName("Should get device by ID for valid owner")
    void testGetDeviceByIdSuccess() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-123")).thenReturn(Optional.of(sampleDevice));

        DeviceResponse response = deviceService.getDeviceById("dev-1", "usr-123");

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("dev-1", response.data().id());
        assertEquals("Apple", response.data().brand());
    }

    @Test
    @DisplayName("Should throw DeviceNotFoundException when device does not exist or user is not owner")
    void testGetDeviceByIdNotFound() {
        when(deviceRepository.findByIdAndUserId("nonexistent", "usr-123")).thenReturn(Optional.empty());

        assertThrows(DeviceNotFoundException.class, () -> deviceService.getDeviceById("nonexistent", "usr-123"));
    }

    @Test
    @DisplayName("Should list all devices for user")
    void testGetUserDevices() {
        when(deviceRepository.findByUserIdOrderByCreatedAtDesc("usr-123")).thenReturn(List.of(sampleDevice));

        DeviceListResponse response = deviceService.getUserDevices("usr-123");

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals(1, response.data().size());
        assertEquals("dev-1", response.data().get(0).id());
    }

    @Test
    @DisplayName("Should update device and recalculate health score when condition changes")
    void testUpdateDevice() {
        UpdateDeviceRequest request = new UpdateDeviceRequest(
                "Updated iPhone 14",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "Excellent"
        );

        when(deviceRepository.findByIdAndUserId("dev-1", "usr-123")).thenReturn(Optional.of(sampleDevice));
        when(deviceHealthRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(sampleHealth));
        when(deviceRepository.save(any(Device.class))).thenAnswer(i -> i.getArgument(0));

        DeviceResponse response = deviceService.updateDevice("dev-1", request, "usr-123");

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("Updated iPhone 14", response.data().deviceName());
        assertEquals("Excellent", response.data().currentCondition());

        verify(deviceHealthRepository, times(1)).save(any(DeviceHealth.class));
        assertEquals(95, sampleHealth.getHealthScore());
    }

    @Test
    @DisplayName("Should delete device and cascade health record")
    void testDeleteDevice() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-123")).thenReturn(Optional.of(sampleDevice));
        doNothing().when(deviceHealthRepository).deleteByDeviceId("dev-1");
        doNothing().when(deviceRepository).delete(sampleDevice);

        assertDoesNotThrow(() -> deviceService.deleteDevice("dev-1", "usr-123"));

        verify(deviceHealthRepository, times(1)).deleteByDeviceId("dev-1");
        verify(deviceRepository, times(1)).delete(sampleDevice);
    }
}
