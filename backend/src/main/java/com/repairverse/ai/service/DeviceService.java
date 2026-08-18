package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.exception.DeviceNotFoundException;
import com.repairverse.ai.repository.DeviceHealthRepository;
import com.repairverse.ai.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;

    @Transactional
    public DeviceResponse createDevice(CreateDeviceRequest request, String userId) {
        log.info("Creating device '{}' for user '{}'", request.deviceName(), userId);

        Device device = Device.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .deviceName(request.deviceName().trim())
                .category(request.category().trim())
                .brand(request.brand().trim())
                .model(request.model().trim())
                .serialNumber(request.serialNumber() != null ? request.serialNumber().trim() : null)
                .purchaseDate(request.purchaseDate())
                .warrantyExpiry(request.warrantyExpiry())
                .purchasePrice(request.purchasePrice())
                .currentCondition(request.currentCondition() != null ? request.currentCondition() : "Good")
                .createdAt(LocalDateTime.now())
                .build();

        Device savedDevice = deviceRepository.save(device);

        // Initialize default DeviceHealth record
        int initialScore = calculateInitialHealthScore(savedDevice.getCurrentCondition());
        int initialBattery = calculateInitialBatteryHealth(savedDevice.getCurrentCondition());

        DeviceHealth health = DeviceHealth.builder()
                .id(UUID.randomUUID().toString())
                .deviceId(savedDevice.getId())
                .healthScore(initialScore)
                .batteryHealth(initialBattery)
                .lastService(savedDevice.getPurchaseDate())
                .maintenanceDue(null)
                .aiPrediction("Initial passport generated. Run AI diagnosis for deeper metrics.")
                .build();

        deviceHealthRepository.save(health);

        return DeviceResponse.of("Device registered successfully", mapToDetailDto(savedDevice));
    }

    @Transactional(readOnly = true)
    public DeviceResponse getDeviceById(String id, String userId) {
        Device device = deviceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + id));

        return DeviceResponse.of(mapToDetailDto(device));
    }

    @Transactional(readOnly = true)
    public DeviceListResponse getUserDevices(String userId) {
        List<Device> devices = deviceRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<DeviceDetailDto> dtos = devices.stream()
                .map(this::mapToDetailDto)
                .toList();

        return DeviceListResponse.of(dtos);
    }

    @Transactional
    public DeviceResponse updateDevice(String id, UpdateDeviceRequest request, String userId) {
        log.info("Updating device '{}' for user '{}'", id, userId);

        Device device = deviceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + id));

        if (request.deviceName() != null && !request.deviceName().isBlank()) {
            device.setDeviceName(request.deviceName().trim());
        }
        if (request.category() != null && !request.category().isBlank()) {
            device.setCategory(request.category().trim());
        }
        if (request.brand() != null && !request.brand().isBlank()) {
            device.setBrand(request.brand().trim());
        }
        if (request.model() != null && !request.model().isBlank()) {
            device.setModel(request.model().trim());
        }
        if (request.serialNumber() != null) {
            device.setSerialNumber(request.serialNumber().trim());
        }
        if (request.purchaseDate() != null) {
            device.setPurchaseDate(request.purchaseDate());
        }
        if (request.warrantyExpiry() != null) {
            device.setWarrantyExpiry(request.warrantyExpiry());
        }
        if (request.purchasePrice() != null) {
            device.setPurchasePrice(request.purchasePrice());
        }
        if (request.currentCondition() != null && !request.currentCondition().isBlank()) {
            device.setCurrentCondition(request.currentCondition());
            // Update health score if condition changed
            deviceHealthRepository.findByDeviceId(id).ifPresent(health -> {
                health.setHealthScore(calculateInitialHealthScore(request.currentCondition()));
                deviceHealthRepository.save(health);
            });
        }

        device.setUpdatedAt(LocalDateTime.now());
        Device updatedDevice = deviceRepository.save(device);

        return DeviceResponse.of("Device updated successfully", mapToDetailDto(updatedDevice));
    }

    @Transactional
    public void deleteDevice(String id, String userId) {
        log.info("Deleting device '{}' for user '{}'", id, userId);

        Device device = deviceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + id));

        deviceHealthRepository.deleteByDeviceId(id);
        deviceRepository.delete(device);
    }

    public DeviceDetailDto mapToDetailDto(Device device) {
        return DeviceDetailDto.builder()
                .id(device.getId())
                .userId(device.getUserId())
                .deviceName(device.getDeviceName())
                .category(device.getCategory())
                .brand(device.getBrand())
                .model(device.getModel())
                .serialNumber(device.getSerialNumber())
                .purchaseDate(device.getPurchaseDate())
                .warrantyExpiry(device.getWarrantyExpiry())
                .purchasePrice(device.getPurchasePrice())
                .currentCondition(device.getCurrentCondition())
                .createdAt(device.getCreatedAt() != null ? device.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME) : null)
                .build();
    }

    public DeviceHealthDto mapToHealthDto(DeviceHealth health) {
        return DeviceHealthDto.builder()
                .id(health.getId())
                .deviceId(health.getDeviceId())
                .batteryHealth(health.getBatteryHealth())
                .healthScore(health.getHealthScore())
                .lastService(health.getLastService())
                .maintenanceDue(health.getMaintenanceDue())
                .aiPrediction(health.getAiPrediction())
                .build();
    }

    private int calculateInitialHealthScore(String condition) {
        if (condition == null) return 80;
        return switch (condition.toLowerCase()) {
            case "excellent" -> 95;
            case "good" -> 85;
            case "fair" -> 70;
            case "needs attention" -> 55;
            case "needs repair" -> 40;
            default -> 80;
        };
    }

    private int calculateInitialBatteryHealth(String condition) {
        if (condition == null) return 90;
        return switch (condition.toLowerCase()) {
            case "excellent" -> 96;
            case "good" -> 88;
            case "fair" -> 80;
            case "needs attention" -> 70;
            case "needs repair" -> 55;
            default -> 90;
        };
    }
}
