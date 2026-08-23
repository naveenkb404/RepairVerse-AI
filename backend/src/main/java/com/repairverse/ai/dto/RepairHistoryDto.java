package com.repairverse.ai.dto;

import java.util.List;

public class RepairHistoryDto {

    public record RepairDeviceSummary(
            String id,
            String name,
            String brand,
            String model,
            String category,
            String serialNumber
    ) {}

    public record RepairTechnicianSummary(
            String id,
            String name,
            String role,
            String phone,
            String shopName,
            Boolean isVerified
    ) {}

    public record RepairShopSummary(
            String id,
            String name,
            String address,
            String phone,
            Double rating,
            Double latitude,
            Double longitude
    ) {}

    public record RepairPartDto(
            String id,
            String name,
            int quantity,
            double cost,
            String partNumber
    ) {}

    public record RepairTimelineStageDto(
            String id,
            String date,
            String title,
            String status,
            String description
    ) {}

    public record RepairHistoryItemResponse(
            String id,
            String deviceId,
            RepairDeviceSummary device,
            String repairType,
            String repairDate,
            String status,
            String description,
            String diagnosisIssue,
            Integer diagnosisConfidence,
            RepairTechnicianSummary technician,
            RepairShopSummary shop,
            List<RepairPartDto> parts,
            double partsCost,
            double laborCost,
            double totalCost,
            String repairDuration,
            String warrantyPeriod,
            String warrantyUntil,
            Boolean isWarrantyActive,
            Double co2SavedKg,
            Double ewasteReducedKg,
            Double moneySaved,
            String notes,
            List<RepairTimelineStageDto> timeline
    ) {}

    public record CreateRepairHistoryRequest(
            String deviceId,
            String repairType,
            String repairDate,
            String status,
            String description,
            String diagnosisIssue,
            Integer diagnosisConfidence,
            String technicianName,
            String technicianRole,
            String shopName,
            String shopAddress,
            List<RepairPartDto> parts,
            double partsCost,
            double laborCost,
            double totalCost,
            String repairDuration,
            String warrantyPeriod,
            String warrantyUntil,
            Boolean isWarrantyActive,
            Double co2SavedKg,
            Double ewasteReducedKg,
            Double moneySaved,
            String notes,
            List<RepairTimelineStageDto> timeline
    ) {}
}
