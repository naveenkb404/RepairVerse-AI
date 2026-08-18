package com.repairverse.ai.service;

import com.repairverse.ai.dto.DeviceDto.DeviceDetailDto;
import com.repairverse.ai.dto.DeviceDto.DeviceHealthDto;
import com.repairverse.ai.dto.DevicePassportDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.entity.DiagnosisReport;
import com.repairverse.ai.exception.DeviceNotFoundException;
import com.repairverse.ai.repository.DeviceHealthRepository;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.DiagnosisReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DevicePassportService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final DiagnosisReportRepository diagnosisReportRepository;
    private final DeviceService deviceService;

    @Transactional(readOnly = true)
    public DevicePassportResponse getDevicePassport(String deviceId, String userId) {
        log.info("Generating digital health passport for device '{}' (user: '{}')", deviceId, userId);

        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with ID: " + deviceId));

        DeviceHealth health = deviceHealthRepository.findByDeviceId(deviceId)
                .orElseGet(() -> DeviceHealth.builder()
                        .deviceId(device.getId())
                        .healthScore(80)
                        .batteryHealth(90)
                        .aiPrediction("Device operating within standard specifications.")
                        .build());

        // Retrieve AI Diagnosis history for this device
        List<DiagnosisReport> diagnoses = diagnosisReportRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId);

        // Build summaries and calculate updated health metrics
        DiagnosisSummaryDto diagnosisSummary = buildDiagnosisSummary(diagnoses);
        RepairSummaryDto repairSummary = buildRepairSummary(diagnoses);
        CarbonSummaryDto carbonSummary = calculateCarbonSummary(device, diagnoses);
        int computedHealthScore = calculateDynamicHealthScore(device, health, diagnoses);

        // Update health score in DTO
        DeviceHealthDto healthDto = DeviceHealthDto.builder()
                .id(health.getId())
                .deviceId(health.getDeviceId())
                .batteryHealth(health.getBatteryHealth())
                .healthScore(computedHealthScore)
                .lastService(health.getLastService())
                .maintenanceDue(health.getMaintenanceDue())
                .aiPrediction(health.getAiPrediction() != null ? health.getAiPrediction() : generateAiPrediction(computedHealthScore, diagnoses))
                .build();

        DeviceDetailDto deviceDto = deviceService.mapToDetailDto(device);
        List<LifecycleEventDto> timeline = buildLifecycleTimeline(device, health, diagnoses);

        DevicePassportData passportData = DevicePassportData.builder()
                .device(deviceDto)
                .health(healthDto)
                .diagnosisSummary(diagnosisSummary)
                .repairSummary(repairSummary)
                .carbonSummary(carbonSummary)
                .lifecycleTimeline(timeline)
                .build();

        return DevicePassportResponse.of(passportData);
    }

    private DiagnosisSummaryDto buildDiagnosisSummary(List<DiagnosisReport> diagnoses) {
        if (diagnoses.isEmpty()) {
            return null;
        }

        DiagnosisReport latest = diagnoses.get(0);
        String formattedDate = latest.getCreatedAt() != null
                ? latest.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : null;

        return DiagnosisSummaryDto.builder()
                .probableIssue(latest.getProbableIssue())
                .confidenceScore(latest.getConfidenceScore())
                .repairDifficulty(latest.getRepairDifficulty())
                .repairCost(latest.getRepairCost() != null ? latest.getRepairCost() : 0.0)
                .lastDiagnosisDate(formattedDate)
                .build();
    }

    private RepairSummaryDto buildRepairSummary(List<DiagnosisReport> diagnoses) {
        if (diagnoses.isEmpty()) {
            return RepairSummaryDto.builder()
                    .repairsCompleted(0)
                    .lastRecommendedAction("All systems operational. Routine inspection recommended.")
                    .build();
        }

        DiagnosisReport latest = diagnoses.get(0);
        String action = switch (latest.getRepairDifficulty() != null ? latest.getRepairDifficulty().toLowerCase() : "") {
            case "complex", "hard" -> "Schedule certified technician servicing";
            case "moderate" -> "Component replacement recommended within 30 days";
            case "easy" -> "Self-guided DIY repair feasible";
            default -> "Monitor hardware performance";
        };

        String lastDate = latest.getCreatedAt() != null
                ? latest.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE)
                : null;

        return RepairSummaryDto.builder()
                .repairsCompleted(Math.max(1, diagnoses.size() - 1))
                .lastRepairDate(lastDate)
                .lastRecommendedAction(action)
                .build();
    }

    private CarbonSummaryDto calculateCarbonSummary(Device device, List<DiagnosisReport> diagnoses) {
        String category = device.getCategory() != null ? device.getCategory().toLowerCase() : "other";

        double baseCo2 = switch (category) {
            case "laptop" -> 120.0;
            case "gaming console" -> 65.4;
            case "smartphone" -> 42.5;
            case "tablet" -> 38.0;
            case "smartwatch" -> 18.2;
            case "audio device" -> 14.5;
            default -> 35.0;
        };

        double baseEwaste = switch (category) {
            case "laptop" -> 2.15;
            case "gaming console" -> 4.50;
            case "tablet" -> 0.46;
            case "smartphone" -> 0.21;
            case "smartwatch" -> 0.08;
            case "audio device" -> 0.15;
            default -> 0.50;
        };

        double baseSavings = device.getPurchasePrice() != null && device.getPurchasePrice() > 0
                ? device.getPurchasePrice() * 0.75
                : switch (category) {
                    case "laptop" -> 1200.0;
                    case "smartphone" -> 700.0;
                    case "gaming console" -> 450.0;
                    case "tablet" -> 400.0;
                    default -> 300.0;
                };

        int multiplier = Math.max(1, diagnoses.size());

        return CarbonSummaryDto.builder()
                .co2SavedKg(Math.round(baseCo2 * multiplier * 10.0) / 10.0)
                .ewasteReducedKg(Math.round(baseEwaste * multiplier * 100.0) / 100.0)
                .moneySaved(Math.round(baseSavings * multiplier * 10.0) / 10.0)
                .build();
    }

    private int calculateDynamicHealthScore(Device device, DeviceHealth health, List<DiagnosisReport> diagnoses) {
        int score = health.getHealthScore() != null ? health.getHealthScore() : 80;

        if (health.getBatteryHealth() != null && health.getBatteryHealth() < 80) {
            score -= 10;
        }

        if (!diagnoses.isEmpty()) {
            DiagnosisReport latest = diagnoses.get(0);
            String diff = latest.getRepairDifficulty() != null ? latest.getRepairDifficulty().toLowerCase() : "";
            switch (diff) {
                case "complex" -> score = Math.min(score, 50);
                case "hard" -> score = Math.min(score, 65);
                case "moderate" -> score = Math.min(score, 78);
                case "easy" -> score = Math.min(score, 88);
            }
        }

        // Clamp between 10 and 99
        return Math.max(10, Math.min(99, score));
    }

    private String generateAiPrediction(int score, List<DiagnosisReport> diagnoses) {
        if (!diagnoses.isEmpty()) {
            DiagnosisReport latest = diagnoses.get(0);
            return "Active issue identified: " + latest.getProbableIssue() + ". Confidence rating: " + latest.getConfidenceScore() + "%.";
        }
        if (score >= 90) {
            return "Thermal efficiency optimal. Battery condition prime. No hardware defects detected.";
        } else if (score >= 75) {
            return "Hardware operating within expected operational parameters. Periodic checkup suggested.";
        } else {
            return "Hardware degradation detected. Run full AI diagnosis to assess component integrity.";
        }
    }

    private List<LifecycleEventDto> buildLifecycleTimeline(Device device, DeviceHealth health, List<DiagnosisReport> diagnoses) {
        List<LifecycleEventDto> events = new ArrayList<>();

        // 1. Purchase / Registration event
        String regDate = device.getPurchaseDate() != null ? device.getPurchaseDate()
                : (device.getCreatedAt() != null ? device.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) : "2024-01-01");

        events.add(LifecycleEventDto.builder()
                .id("evt_reg_" + device.getId())
                .date(regDate)
                .title("Device Registered")
                .type("purchase")
                .description("Enrolled " + device.getBrand() + " " + device.getModel() + " in RepairVerse Digital Health Passport.")
                .build());

        // 2. Diagnosis events
        for (int i = 0; i < diagnoses.size(); i++) {
            DiagnosisReport diag = diagnoses.get(i);
            String diagDate = diag.getCreatedAt() != null
                    ? diag.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE)
                    : "2024-01-01";

            events.add(LifecycleEventDto.builder()
                    .id("evt_diag_" + diag.getId())
                    .date(diagDate)
                    .title("AI Visual Diagnosis")
                    .type("diagnosis")
                    .description("Identified: " + diag.getProbableIssue() + " (" + diag.getConfidenceScore() + "% confidence).")
                    .build());
        }

        // 3. Service / Inspection event if lastService is present
        if (health.getLastService() != null && !health.getLastService().isBlank()) {
            events.add(LifecycleEventDto.builder()
                    .id("evt_srv_" + device.getId())
                    .date(health.getLastService())
                    .title("Service Maintenance")
                    .type("service")
                    .description("Routine hardware maintenance inspection recorded.")
                    .build());
        }

        return events;
    }
}
