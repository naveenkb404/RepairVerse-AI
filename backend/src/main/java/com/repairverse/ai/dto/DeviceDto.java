package com.repairverse.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceDto {

    @Builder
    public record DeviceDetailDto(
            String id,
            String userId,
            String deviceName,
            String category,
            String brand,
            String model,
            String serialNumber,
            String purchaseDate,
            String warrantyExpiry,
            Double purchasePrice,
            String currentCondition,
            String createdAt
    ) {}

    @Builder
    public record DeviceHealthDto(
            String id,
            String deviceId,
            Integer batteryHealth,
            Integer healthScore,
            String lastService,
            String maintenanceDue,
            String aiPrediction
    ) {}

    public record CreateDeviceRequest(
            @NotBlank(message = "Device name is required")
            @Size(max = 100, message = "Device name cannot exceed 100 characters")
            String deviceName,

            @NotBlank(message = "Category is required")
            @Size(max = 50, message = "Category cannot exceed 50 characters")
            String category,

            @NotBlank(message = "Brand is required")
            @Size(max = 50, message = "Brand cannot exceed 50 characters")
            String brand,

            @NotBlank(message = "Model is required")
            @Size(max = 100, message = "Model cannot exceed 100 characters")
            String model,

            @Size(max = 100, message = "Serial number cannot exceed 100 characters")
            String serialNumber,

            String purchaseDate,
            String warrantyExpiry,
            Double purchasePrice,
            String currentCondition
    ) {}

    public record UpdateDeviceRequest(
            @Size(max = 100, message = "Device name cannot exceed 100 characters")
            String deviceName,

            @Size(max = 50, message = "Category cannot exceed 50 characters")
            String category,

            @Size(max = 50, message = "Brand cannot exceed 50 characters")
            String brand,

            @Size(max = 100, message = "Model cannot exceed 100 characters")
            String model,

            @Size(max = 100, message = "Serial number cannot exceed 100 characters")
            String serialNumber,

            String purchaseDate,
            String warrantyExpiry,
            Double purchasePrice,
            String currentCondition
    ) {}

    public record DeviceResponse(
            boolean success,
            String message,
            DeviceDetailDto data
    ) {
        public static DeviceResponse of(DeviceDetailDto data) {
            return new DeviceResponse(true, null, data);
        }

        public static DeviceResponse of(String message, DeviceDetailDto data) {
            return new DeviceResponse(true, message, data);
        }
    }

    public record DeviceListResponse(
            boolean success,
            String message,
            List<DeviceDetailDto> data
    ) {
        public static DeviceListResponse of(List<DeviceDetailDto> data) {
            return new DeviceListResponse(true, null, data);
        }
    }
}
