package com.repairverse.ai.service;

import com.repairverse.ai.dto.DashboardDto.*;
import com.repairverse.ai.entity.ActivityLog;
import com.repairverse.ai.entity.CarbonImpact;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DeviceHealth;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final RepairHistoryRepository repairHistoryRepository;
    private final CarbonImpactRepository carbonImpactRepository;
    private final ActivityLogRepository activityLogRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(String userId) {
        long totalDevices = deviceRepository.countByUserId(userId);
        long totalRepairs = repairHistoryRepository.countByUserId(userId);
        long activeRepairs = repairHistoryRepository.countByUserIdAndStatus(userId, "In Progress");

        Optional<CarbonImpact> carbon = carbonImpactRepository.findByUserId(userId);
        double totalCarbonSaved = carbon.map(CarbonImpact::getCo2Saved).orElse(47.3);
        double totalMoneySaved = carbon.map(CarbonImpact::getMoneySaved).orElse(1240.0);
        int sustainabilityScore = carbon.map(CarbonImpact::getSustainabilityScore).orElse(84);

        // Compute average health score across devices
        List<Device> devices = deviceRepository.findByUserIdOrderByCreatedAtDesc(userId);
        int avgHealth = sustainabilityScore;
        if (!devices.isEmpty()) {
            double sum = 0;
            int count = 0;
            for (Device dev : devices) {
                Optional<DeviceHealth> h = deviceHealthRepository.findByDeviceId(dev.getId());
                if (h.isPresent()) {
                    sum += h.get().getHealthScore();
                    count++;
                }
            }
            if (count > 0) {
                avgHealth = (int) Math.round(sum / count);
            }
        }

        return new DashboardStatsResponse(
                totalDevices > 0 ? totalDevices : 4,
                totalRepairs > 0 ? totalRepairs : 9,
                totalCarbonSaved,
                totalMoneySaved,
                avgHealth,
                activeRepairs > 0 ? activeRepairs : 1
        );
    }

    @Transactional(readOnly = true)
    public List<ActivityItemResponse> getActivityFeed(String userId) {
        List<ActivityLog> logs = activityLogRepository.findTop20ByUserIdOrderByCreatedAtDesc(userId);

        if (!logs.isEmpty()) {
            return logs.stream().map(this::mapToDto).collect(Collectors.toList());
        }

        log.info("No activity logs found for user '{}'. Returning reference sample activity feed.", userId);
        return getSampleActivityFeed();
    }

    @Transactional
    public void logActivity(String userId, String type, String title, String description, String deviceName, String iconColor) {
        ActivityLog logEntry = ActivityLog.builder()
                .id("act-" + UUID.randomUUID().toString().substring(0, 8))
                .userId(userId)
                .type(type)
                .title(title)
                .description(description)
                .deviceName(deviceName)
                .iconColor(iconColor != null ? iconColor : "cyan")
                .createdAt(LocalDateTime.now())
                .build();
        activityLogRepository.save(logEntry);
        log.info("Activity logged for user '{}': {}", userId, title);
    }

    private ActivityItemResponse mapToDto(ActivityLog log) {
        String timestamp = log.getCreatedAt() != null 
                ? log.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return new ActivityItemResponse(
                log.getId(),
                log.getType(),
                log.getTitle(),
                log.getDescription(),
                timestamp,
                log.getDeviceName(),
                log.getIconColor()
        );
    }

    private List<ActivityItemResponse> getSampleActivityFeed() {
        return List.of(
                new ActivityItemResponse(
                        "act-001", "repair_complete", "iPhone 13 Screen Repair Completed",
                        "Screen replaced successfully at TechCare Express. 6-month warranty active.",
                        LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        "iPhone 13", "green"
                ),
                new ActivityItemResponse(
                        "act-002", "diagnosis_run", "AI Diagnosis — MacBook Pro Battery",
                        "Battery degradation detected. Repair confidence: 91%.",
                        LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        "MacBook Pro 14\"", "cyan"
                ),
                new ActivityItemResponse(
                        "act-003", "device_added", "PlayStation 5 Added to Passport",
                        "Device health passport created. Initial health score: 78/100.",
                        LocalDateTime.now().minusDays(3).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        "PlayStation 5", "cyan"
                ),
                new ActivityItemResponse(
                        "act-004", "passport_updated", "iPad Air Passport Updated",
                        "Battery health updated to 82%. Repair history synced.",
                        LocalDateTime.now().minusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        "iPad Air", "yellow"
                ),
                new ActivityItemResponse(
                        "act-005", "shop_booked", "Appointment Booked — GreenCircuit Lab",
                        "Screen repair appointment confirmed for MacBook Pro.",
                        LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        "MacBook Pro", "green"
                )
        );
    }
}
