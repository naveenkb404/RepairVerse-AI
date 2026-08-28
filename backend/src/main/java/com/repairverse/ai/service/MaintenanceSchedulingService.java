package com.repairverse.ai.service;

import com.repairverse.ai.dto.MaintenanceDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Phase 25 — Deterministic Maintenance Scheduling Service.
 *
 * Generates proactive care tasks from existing device health, predictive risk,
 * repair history, and lifecycle data. All scheduling rules are deterministic
 * and fully documented. Gemini is NEVER consulted for scheduling decisions.
 *
 * Scheduling rules:
 *   HEALTHY (score > 85, risk LOW/HEALTHY) → Quarterly inspection (90 days, LOW priority)
 *   MEDIUM  (score 60–85)                  → Preventive maintenance (30 days, MEDIUM) +
 *                                            battery/component check (45 days, MEDIUM)
 *   HIGH    (risk HIGH)                    → Professional inspection (14 days, HIGH) +
 *                                            thermal/component check (7 days, HIGH)
 *   CRITICAL (risk CRITICAL)               → Immediate repair action (3 days, CRITICAL)
 *   Battery < 70%                          → Battery check (14 days, MEDIUM)
 *   Last service > 6 months ago            → Cleaning + SW maintenance (60 days, LOW)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceSchedulingService {

    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final DevicePredictionRepository devicePredictionRepository;
    private final RepairHistoryRepository repairHistoryRepository;
    private final MaintenanceScheduleRepository maintenanceRepository;
    private final NotificationRepository notificationRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns all maintenance schedules for the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<MaintenanceScheduleResponse> getUserSchedules(String userId) {
        return maintenanceRepository.findByUserIdOrderByDueDateAsc(userId)
                .stream().map(this::mapToResponse).toList();
    }

    /**
     * Returns maintenance schedules for a specific device owned by the user.
     */
    @Transactional(readOnly = true)
    public List<MaintenanceScheduleResponse> getDeviceSchedules(String deviceId, String userId) {
        validateDeviceOwnership(deviceId, userId);
        return maintenanceRepository.findByUserIdAndDeviceIdOrderByDueDateAsc(userId, deviceId)
                .stream().map(this::mapToResponse).toList();
    }

    /**
     * Generates or refreshes deterministic maintenance schedules for a device.
     * Deduplication prevents creating duplicate tasks within a ±7-day window.
     */
    @Transactional
    public List<MaintenanceScheduleResponse> generateSchedules(String deviceId, String userId) {
        Device device = validateDeviceOwnership(deviceId, userId);
        log.info("Generating deterministic maintenance schedules for deviceId='{}', user='{}'", deviceId, userId);

        Optional<DeviceHealth> healthOpt = deviceHealthRepository.findByDeviceId(deviceId);
        Optional<DevicePrediction> predOpt = devicePredictionRepository.findByDeviceId(deviceId);

        int healthScore = predOpt.map(DevicePrediction::getPredictionScore)
                .orElseGet(() -> healthOpt.map(DeviceHealth::getHealthScore).orElse(80));
        int batteryHealth = healthOpt.map(h -> h.getBatteryHealth() != null ? h.getBatteryHealth() : 85).orElse(85);
        String riskLevel = predOpt.map(DevicePrediction::getRiskLevel).orElse("LOW");
        String lastService = healthOpt.map(DeviceHealth::getLastService).orElse(null);

        List<MaintenanceSchedule> tasksToCreate = new ArrayList<>();

        // ── Rule 1: CRITICAL risk ──────────────────────────────────────────
        if ("CRITICAL".equalsIgnoreCase(riskLevel)) {
            addIfNotDuplicate(tasksToCreate, device, userId,
                    "Immediate Professional Repair Action",
                    "Critical hardware risk detected. Immediate certified technician intervention required to prevent total device failure.",
                    "PROFESSIONAL_SERVICE", "CRITICAL", 3, 250.0, 60, 5.0);

        // ── Rule 2: HIGH risk ────────────────────────────────────────────
        } else if ("HIGH".equalsIgnoreCase(riskLevel)) {
            addIfNotDuplicate(tasksToCreate, device, userId,
                    "Professional Hardware Inspection",
                    "High failure probability identified. Certified technician inspection recommended within 14 days to assess component wear.",
                    "PROFESSIONAL_SERVICE", "HIGH", 14, 120.0, 90, 3.5);

            addIfNotDuplicate(tasksToCreate, device, userId,
                    "Thermal System & Component Check",
                    "Inspect thermal compound, heat dissipation pathways, and major wear components.",
                    "INSPECTION", "HIGH", 7, 35.0, 45, 1.2);

        // ── Rule 3: MEDIUM risk ──────────────────────────────────────────
        } else if ("MEDIUM".equalsIgnoreCase(riskLevel) || (healthScore >= 60 && healthScore < 85)) {
            addIfNotDuplicate(tasksToCreate, device, userId,
                    "Preventive Maintenance Service",
                    "Moderate component degradation detected. Proactive maintenance will prevent escalation to critical failure.",
                    "PREVENTIVE_REPAIR", "MEDIUM", 30, 65.0, 75, 2.8);

            addIfNotDuplicate(tasksToCreate, device, userId,
                    "Battery & Component Wear Assessment",
                    "Evaluate battery health, charging cycle wear, and primary wear components to plan proactive replacement.",
                    "BATTERY_CHECK", "MEDIUM", 45, 25.0, 30, 1.0);

        // ── Rule 4: HEALTHY / LOW risk ────────────────────────────────────
        } else {
            addIfNotDuplicate(tasksToCreate, device, userId,
                    "Quarterly Hardware Inspection",
                    "Routine quarterly inspection to ensure continued optimal device performance and longevity.",
                    "INSPECTION", "LOW", 90, 20.0, 30, 0.8);
        }

        // ── Rule 5: Battery health < 70% ─────────────────────────────────
        if (batteryHealth < 70) {
            addIfNotDuplicate(tasksToCreate, device, userId,
                    "Battery Health Restoration",
                    String.format("Battery capacity at %d%%. Calibration, conditioning, or replacement evaluation recommended.", batteryHealth),
                    "BATTERY_CHECK", "MEDIUM", 14, 45.0, 30, 1.5);
        }

        // ── Rule 6: Last service > 6 months ──────────────────────────────
        boolean serviceOverdue = isServiceOverdue(lastService, 180);
        if (serviceOverdue) {
            addIfNotDuplicate(tasksToCreate, device, userId,
                    "Deep Cleaning & Port Maintenance",
                    "Device has not been serviced for over 6 months. Dust removal, port cleaning, and contact restoration recommended.",
                    "CLEANING", "LOW", 60, 15.0, 45, 0.5);

            addIfNotDuplicate(tasksToCreate, device, userId,
                    "Software & Firmware Optimization",
                    "Update firmware, clear software cache, disable background drain processes, and optimize power management.",
                    "SOFTWARE_MAINTENANCE", "LOW", 30, 0.0, 20, 0.2);
        }

        List<MaintenanceSchedule> saved = maintenanceRepository.saveAll(tasksToCreate);
        log.info("Generated {} maintenance schedules for deviceId='{}'", saved.size(), deviceId);

        // ── Dispatch notification for critical/high tasks ─────────────────
        for (MaintenanceSchedule schedule : saved) {
            if ("CRITICAL".equalsIgnoreCase(schedule.getPriority())) {
                dispatchMaintenanceNotification(userId, schedule, "CRITICAL", 24);
            } else if ("HIGH".equalsIgnoreCase(schedule.getPriority())) {
                dispatchMaintenanceNotification(userId, schedule, "HIGH", 168); // 7 days
            }
        }

        return saved.stream().map(this::mapToResponse).toList();
    }

    /**
     * Updates the status of a maintenance schedule (with validated transitions).
     */
    @Transactional
    public MaintenanceScheduleResponse updateStatus(String scheduleId, String userId, String newStatus) {
        MaintenanceSchedule schedule = maintenanceRepository.findByIdAndUserId(scheduleId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Maintenance schedule not found or not owned by user: " + scheduleId));

        validateStatusTransition(schedule.getStatus(), newStatus);

        schedule.setStatus(newStatus);
        if ("COMPLETED".equalsIgnoreCase(newStatus)) {
            schedule.setCompletedAt(LocalDateTime.now());
        }

        MaintenanceSchedule updated = maintenanceRepository.save(schedule);
        log.info("Maintenance schedule '{}' status updated to '{}' by user '{}'", scheduleId, newStatus, userId);
        return mapToResponse(updated);
    }

    /**
     * Returns a compact summary of maintenance statistics for the user.
     */
    @Transactional(readOnly = true)
    public MaintenanceSummaryResponse getSummary(String userId) {
        List<MaintenanceSchedule> allSchedules = maintenanceRepository.findByUserIdOrderByDueDateAsc(userId);

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        int upcoming = 0, due = 0, overdue = 0, critical = 0, completedMonth = 0;
        double totalCostSavings = 0.0, totalCarbonSavings = 0.0;

        for (MaintenanceSchedule s : allSchedules) {
            String status = deriveCurrentStatus(s, today);

            switch (status) {
                case "UPCOMING" -> upcoming++;
                case "DUE"      -> due++;
                case "OVERDUE"  -> overdue++;
            }

            if ("CRITICAL".equalsIgnoreCase(s.getPriority()) &&
                    !List.of("COMPLETED", "SKIPPED", "CANCELLED").contains(s.getStatus())) {
                critical++;
            }

            if ("COMPLETED".equalsIgnoreCase(s.getStatus()) &&
                    s.getCompletedAt() != null &&
                    s.getCompletedAt().toLocalDate().isAfter(monthStart.minusDays(1))) {
                completedMonth++;
            }

            if (!List.of("COMPLETED", "SKIPPED", "CANCELLED").contains(s.getStatus())) {
                totalCostSavings += s.getEstimatedCost();
                totalCarbonSavings += s.getEstimatedCarbonSavings();
            }
        }

        return new MaintenanceSummaryResponse(
                upcoming, due, overdue, critical, completedMonth,
                Math.round(totalCostSavings * 100.0) / 100.0,
                Math.round(totalCarbonSavings * 100.0) / 100.0,
                false
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void addIfNotDuplicate(
            List<MaintenanceSchedule> accumulator,
            Device device, String userId,
            String title, String description,
            String type, String priority,
            int dueDays, double estimatedCost, int durationMins, double carbonSavings) {

        LocalDate dueDate = LocalDate.now().plusDays(dueDays);
        LocalDate windowStart = dueDate.minusDays(7);
        LocalDate windowEnd = dueDate.plusDays(7);

        List<String> activeStatuses = List.of("UPCOMING", "DUE", "OVERDUE");

        boolean alreadyExists = maintenanceRepository
                .existsByDeviceIdAndMaintenanceTypeAndDueDateBetweenAndStatusIn(
                        device.getId(), type, windowStart, windowEnd, activeStatuses);

        if (alreadyExists) {
            log.debug("Skipping duplicate {} schedule for device '{}' (window ±7 days)", type, device.getId());
            return;
        }

        accumulator.add(MaintenanceSchedule.builder()
                .id("ms-" + UUID.randomUUID().toString().substring(0, 8))
                .userId(userId)
                .deviceId(device.getId())
                .deviceName(device.getDeviceName())
                .deviceCategory(device.getCategory())
                .title(title)
                .description(description)
                .maintenanceType(type)
                .priority(priority)
                .scheduledDate(LocalDate.now())
                .dueDate(dueDate)
                .status("UPCOMING")
                .estimatedCost(estimatedCost)
                .estimatedDurationMinutes(durationMins)
                .estimatedCarbonSavings(carbonSavings)
                .build());
    }

    private void dispatchMaintenanceNotification(
            String userId, MaintenanceSchedule schedule, String urgency, long deduplicationHours) {

        LocalDateTime cutoff = LocalDateTime.now().minusHours(deduplicationHours);
        boolean alreadyNotified = notificationRepository
                .existsByUserIdAndTitleContainingAndCreatedAtAfter(userId, schedule.getTitle(), cutoff);

        if (!alreadyNotified) {
            Notification notif = Notification.builder()
                    .id("notif-" + UUID.randomUUID().toString().substring(0, 8))
                    .userId(userId)
                    .title(urgency + " Maintenance: " + schedule.getDeviceName())
                    .message(schedule.getTitle() + " — due " + schedule.getDueDate() + ". " + schedule.getDescription())
                    .type("MAINTENANCE")
                    .isRead(false)
                    .actionUrl("/maintenance")
                    .actionLabel("View Maintenance Center")
                    .iconColor("CRITICAL".equalsIgnoreCase(urgency) ? "red" : "amber")
                    .build();
            notificationRepository.save(notif);
            log.info("Dispatched {} maintenance notification for user '{}'", urgency, userId);
        }
    }

    private void validateStatusTransition(String current, String next) {
        List<String> activeStatuses = List.of("UPCOMING", "DUE", "OVERDUE");
        List<String> allowedTargets = List.of("COMPLETED", "SKIPPED", "CANCELLED");

        if (!activeStatuses.contains(current)) {
            throw new IllegalStateException(
                    "Cannot transition from status '" + current + "'. Only UPCOMING, DUE, and OVERDUE schedules can be updated.");
        }
        if (!allowedTargets.contains(next)) {
            throw new IllegalStateException(
                    "Invalid target status '" + next + "'. Allowed transitions: COMPLETED, SKIPPED, CANCELLED.");
        }
    }

    private String deriveCurrentStatus(MaintenanceSchedule schedule, LocalDate today) {
        if (List.of("COMPLETED", "SKIPPED", "CANCELLED").contains(schedule.getStatus())) {
            return schedule.getStatus();
        }
        if (schedule.getDueDate().isBefore(today)) return "OVERDUE";
        if (!schedule.getDueDate().isAfter(today.plusDays(3))) return "DUE";
        return "UPCOMING";
    }

    private boolean isServiceOverdue(String lastServiceStr, int thresholdDays) {
        if (lastServiceStr == null || lastServiceStr.isBlank()) return true;
        try {
            LocalDate lastService = LocalDate.parse(lastServiceStr);
            return ChronoUnit.DAYS.between(lastService, LocalDate.now()) > thresholdDays;
        } catch (Exception e) {
            return true;
        }
    }

    Device validateDeviceOwnership(String deviceId, String userId) {
        return deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Device not found or not owned by user: " + deviceId));
    }

    public MaintenanceScheduleResponse mapToResponse(MaintenanceSchedule s) {
        LocalDate today = LocalDate.now();
        return new MaintenanceScheduleResponse(
                s.getId(), s.getUserId(), s.getDeviceId(), s.getDeviceName(),
                s.getDeviceCategory(), s.getTitle(), s.getDescription(),
                s.getMaintenanceType(), s.getPriority(), s.getScheduledDate(),
                s.getDueDate(), deriveCurrentStatus(s, today),
                s.getEstimatedCost(), s.getEstimatedDurationMinutes(),
                s.getEstimatedCarbonSavings(),
                s.getCreatedAt(), s.getUpdatedAt(), s.getCompletedAt(), false
        );
    }
}
