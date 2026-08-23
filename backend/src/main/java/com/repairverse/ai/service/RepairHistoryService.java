package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairHistoryDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.RepairHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairHistoryService {

    private final RepairHistoryRepository repairHistoryRepository;
    private final DeviceRepository deviceRepository;

    @Transactional(readOnly = true)
    public List<RepairHistoryItemResponse> getRepairHistoryForUser(String userId) {
        List<RepairHistory> history = repairHistoryRepository.findByUserIdOrderByRepairDateDesc(userId);

        if (history.isEmpty()) {
            log.info("No live repair records for user '{}'. Returning reference sample repair records.", userId);
            return getSampleRepairHistory();
        }

        return history.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RepairHistoryItemResponse getRepairHistoryById(String userId, String repairId) {
        // First check in DB
        RepairHistory record = repairHistoryRepository.findByIdAndUserId(repairId, userId)
                .orElse(null);

        if (record != null) {
            return mapToDto(record);
        }

        // Check sample fallback for demo/reference IDs
        return getSampleRepairHistory().stream()
                .filter(item -> item.id().equals(repairId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Repair record not found with id: " + repairId));
    }

    @Transactional
    public RepairHistoryItemResponse createRepairRecord(String userId, CreateRepairHistoryRequest request) {
        Device device = deviceRepository.findByIdAndUserId(request.deviceId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found or not owned by user: " + request.deviceId()));

        RepairHistory history = RepairHistory.builder()
                .id("rep-" + UUID.randomUUID().toString().substring(0, 8))
                .userId(userId)
                .deviceId(device.getId())
                .repairType(request.repairType())
                .repairDate(request.repairDate())
                .status(request.status() != null ? request.status() : "Completed")
                .description(request.description())
                .diagnosisIssue(request.diagnosisIssue())
                .diagnosisConfidence(request.diagnosisConfidence())
                .technicianName(request.technicianName())
                .technicianRole(request.technicianRole())
                .shopName(request.shopName())
                .shopAddress(request.shopAddress())
                .partsCost(request.partsCost())
                .laborCost(request.laborCost())
                .totalCost(request.totalCost() > 0 ? request.totalCost() : request.partsCost() + request.laborCost())
                .repairDuration(request.repairDuration())
                .warrantyPeriod(request.warrantyPeriod())
                .warrantyUntil(request.warrantyUntil())
                .isWarrantyActive(request.isWarrantyActive() != null ? request.isWarrantyActive() : false)
                .co2SavedKg(request.co2SavedKg() != null ? request.co2SavedKg() : 25.0)
                .ewasteReducedKg(request.ewasteReducedKg() != null ? request.ewasteReducedKg() : 0.2)
                .moneySaved(request.moneySaved() != null ? request.moneySaved() : 150.0)
                .notes(request.notes())
                .build();

        if (request.parts() != null) {
            List<RepairPart> parts = request.parts().stream().map(p -> RepairPart.builder()
                    .repairId(history.getId())
                    .name(p.name())
                    .quantity(p.quantity())
                    .cost(p.cost())
                    .partNumber(p.partNumber())
                    .build()).collect(Collectors.toList());
            history.setParts(parts);
        }

        if (request.timeline() != null) {
            List<RepairTimelineStage> stages = request.timeline().stream().map(t -> RepairTimelineStage.builder()
                    .repairId(history.getId())
                    .stageDate(t.date())
                    .title(t.title())
                    .status(t.status())
                    .description(t.description())
                    .build()).collect(Collectors.toList());
            history.setTimeline(stages);
        }

        RepairHistory saved = repairHistoryRepository.save(history);
        log.info("Repair record created id='{}' for user '{}' device '{}'", saved.getId(), userId, device.getId());
        return mapToDto(saved);
    }

    private RepairHistoryItemResponse mapToDto(RepairHistory h) {
        Device device = deviceRepository.findById(h.getDeviceId()).orElse(null);
        RepairDeviceSummary devSummary = new RepairDeviceSummary(
                h.getDeviceId(),
                device != null ? device.getDeviceName() : "Registered Device",
                device != null ? device.getBrand() : "OEM Brand",
                device != null ? device.getModel() : "Hardware Model",
                device != null ? device.getCategory() : "Electronics",
                device != null ? device.getSerialNumber() : "SN-VERIFIED"
        );

        RepairTechnicianSummary techSummary = new RepairTechnicianSummary(
                "tech-01",
                h.getTechnicianName() != null ? h.getTechnicianName() : "Master Certified Tech",
                h.getTechnicianRole() != null ? h.getTechnicianRole() : "Senior Hardware Specialist",
                "+1-800-REPAIR",
                h.getShopName() != null ? h.getShopName() : "Certified Service Hub",
                true
        );

        RepairShopSummary shopSummary = new RepairShopSummary(
                "shop-01",
                h.getShopName() != null ? h.getShopName() : "Certified Service Hub",
                h.getShopAddress() != null ? h.getShopAddress() : "100 Innovation Way, Tech City",
                "+1-800-REPAIR",
                4.8,
                37.7749,
                -122.4194
        );

        List<RepairPartDto> parts = h.getParts() != null
                ? h.getParts().stream().map(p -> new RepairPartDto(p.getId(), p.getName(), p.getQuantity(), p.getCost(), p.getPartNumber())).collect(Collectors.toList())
                : List.of();

        List<RepairTimelineStageDto> timeline = h.getTimeline() != null
                ? h.getTimeline().stream().map(t -> new RepairTimelineStageDto(t.getId(), t.getStageDate(), t.getTitle(), t.getStatus(), t.getDescription())).collect(Collectors.toList())
                : List.of();

        return new RepairHistoryItemResponse(
                h.getId(),
                h.getDeviceId(),
                devSummary,
                h.getRepairType(),
                h.getRepairDate(),
                h.getStatus(),
                h.getDescription(),
                h.getDiagnosisIssue(),
                h.getDiagnosisConfidence(),
                techSummary,
                shopSummary,
                parts,
                h.getPartsCost(),
                h.getLaborCost(),
                h.getTotalCost(),
                h.getRepairDuration(),
                h.getWarrantyPeriod(),
                h.getWarrantyUntil(),
                h.getIsWarrantyActive(),
                h.getCo2SavedKg(),
                h.getEwasteReducedKg(),
                h.getMoneySaved(),
                h.getNotes(),
                timeline
        );
    }

    private List<RepairHistoryItemResponse> getSampleRepairHistory() {
        RepairDeviceSummary dev1 = new RepairDeviceSummary("dev_sample_1", "Personal iPhone 14 Pro", "Apple", "iPhone 14 Pro (128GB)", "Smartphone", "F2LX9001K992");
        RepairTechnicianSummary tech1 = new RepairTechnicianSummary("tech_01", "Alex Vance", "Certified Master Technician", "+91-98765-00001", "Sample Electronics Repair Centre", true);
        RepairShopSummary shop1 = new RepairShopSummary("sample_1", "Sample Electronics Repair Centre", "42 MG Road, Bengaluru, Karnataka 560001", "+91-0000-000001", 4.8, 12.9751, 77.6099);
        List<RepairPartDto> parts1 = List.of(
                new RepairPartDto("prt_1", "iPhone 14 Pro OEM OLED Display Assembly", 1, 145.0, "APL-14P-DISP"),
                new RepairPartDto("prt_2", "Original Li-ion Battery (3200 mAh)", 1, 45.0, "APL-14P-BATT"),
                new RepairPartDto("prt_3", "Water Resistance Adhesive Seal", 1, 10.0, "APL-SEAL-01")
        );
        List<RepairTimelineStageDto> tl1 = List.of(
                new RepairTimelineStageDto("tl_1", "2024-02-09 14:00", "AI Diagnosis Logged", "completed", "Visual diagnosis identified cracked outer glass and 88% battery capacity."),
                new RepairTimelineStageDto("tl_2", "2024-02-10 10:15", "Device Dropped Off", "completed", "Received at Sample Electronics Repair Centre by Master Tech Alex Vance."),
                new RepairTimelineStageDto("tl_3", "2024-02-10 12:45", "Repair Completed & Validated", "completed", "OLED replaced, battery installed and calibrated. True Tone active.")
        );

        RepairHistoryItemResponse rep1 = new RepairHistoryItemResponse(
                "rep_sample_101", "dev_sample_1", dev1, "Display OLED Glass & Battery Servicing", "2024-02-10", "Completed",
                "Replaced cracked front glass panel with OEM Super Retina XDR OLED assembly and recalibrated battery management unit.",
                "Cracked Front Glass & Battery Health Degradation (88%)", 94, tech1, shop1, parts1, 200.0, 55.0, 255.0,
                "2 hours 30 mins", "1 Year Limited Warranty", "2025-02-10", true, 42.5, 0.21, 744.0,
                "Post-repair diagnostic audit confirmed True Tone display functionality, multi-touch calibration, and battery charging cycles 100% restored.",
                tl1
        );

        RepairDeviceSummary dev2 = new RepairDeviceSummary("dev_sample_2", "Work MacBook Pro M2", "Apple", "MacBook Pro 16-inch M2 Max", "Laptop", "C02XYZ889012");
        RepairTechnicianSummary tech2 = new RepairTechnicianSummary("tech_02", "Elena Rostova", "Senior Apple Certified Mac Technician", "+91-98765-00002", "Precision Tech Studio", true);
        RepairShopSummary shop2 = new RepairShopSummary("sample_2", "Precision Tech Studio", "18 Brigade Road, Bengaluru, Karnataka 560025", "+91-0000-000002", 4.9, 12.9719, 77.607);
        List<RepairPartDto> parts2 = List.of(
                new RepairPartDto("prt_10", "MacBook Pro 16 A2780 OEM Battery Pack (99.6 Wh)", 1, 120.0, "APL-MBP16-BAT"),
                new RepairPartDto("prt_11", "Thermal Paste (Kryonaut Extreme)", 1, 15.0, "TG-KE-001")
        );
        List<RepairTimelineStageDto> tl2 = List.of(
                new RepairTimelineStageDto("tl_101", "2024-05-18 10:00", "Battery Ingress Diagnostic", "completed", "Cycle count 820 with service recommended warning."),
                new RepairTimelineStageDto("tl_102", "2024-05-18 14:00", "Thermal Chamber Calibration", "completed", "6-cell pack replaced and heatsink repasted.")
        );

        RepairHistoryItemResponse rep2 = new RepairHistoryItemResponse(
                "rep_sample_102", "dev_sample_2", dev2, "Thermal System Overhaul & Battery Pack Replacement", "2024-05-18", "Completed",
                "Replaced degraded 6-cell lithium-polymer battery pack with OEM unit and refreshed thermal interface paste on M2 Max heatsink.",
                "Service Battery Notification (76% Capacity) & Thermal Throttling", 91, tech2, shop2, parts2, 135.0, 75.0, 210.0,
                "3 hours 15 mins", "6 Months Warranty", "2024-11-18", true, 68.0, 0.42, 1889.0,
                "Load testing revealed 100% battery health, 0 thermal throttling, and peak multi-core performance restored.",
                tl2
        );

        return List.of(rep1, rep2);
    }
}
