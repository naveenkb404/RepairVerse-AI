package com.repairverse.ai.service;

import com.repairverse.ai.dto.MaintenanceDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Phase 25 — Maintenance Calendar Service.
 *
 * Aggregates events from 4 data sources into a unified chronological calendar:
 *   1. Maintenance schedules (UPCOMING, DUE, OVERDUE)
 *   2. Repair shop bookings
 *   3. Repair action plan deadlines (inferred from creation date + priority)
 *   4. Lifecycle urgency alerts (IMMEDIATE / HIGH urgency devices)
 *
 * Event type color tags:
 *   MAINTENANCE     → amber
 *   BOOKING         → cyan
 *   REPAIR_ACTION   → emerald
 *   LIFECYCLE_ALERT → red
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceCalendarService {

    private final MaintenanceScheduleRepository maintenanceRepository;
    private final BookingRepository bookingRepository;
    private final RepairActionPlanRepository actionPlanRepository;
    private final DevicePredictionRepository predictionRepository;
    private final DeviceRepository deviceRepository;

    /**
     * Returns unified chronological calendar events for the authenticated user.
     */
    @Transactional(readOnly = true)
    public List<MaintenanceCalendarResponse> getCalendarEvents(String userId) {
        List<MaintenanceCalendarResponse> events = new ArrayList<>();

        // ── Source 1: Maintenance schedules ──────────────────────────────
        List<String> activeStatuses = List.of("UPCOMING", "DUE", "OVERDUE");
        maintenanceRepository.findByUserIdAndStatusInOrderByDueDateAsc(userId, activeStatuses)
                .forEach(ms -> events.add(new MaintenanceCalendarResponse(
                        ms.getId(),
                        "MAINTENANCE",
                        ms.getTitle(),
                        ms.getDescription(),
                        ms.getDueDate(),
                        ms.getPriority(),
                        ms.getDeviceId(),
                        ms.getDeviceName(),
                        "/maintenance",
                        priorityToColor(ms.getPriority(), "amber")
                )));

        // ── Source 2: Repair shop bookings ───────────────────────────────
        bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(b -> "SCHEDULED".equalsIgnoreCase(b.getBookingStatus()))
                .forEach(booking -> {
                    LocalDate bookingDate = parseBookingDate(booking.getBookingDate());
                    if (bookingDate != null && !bookingDate.isBefore(LocalDate.now().minusDays(1))) {
                        events.add(new MaintenanceCalendarResponse(
                                "booking-" + booking.getId(),
                                "BOOKING",
                                "Repair Shop Appointment",
                                "Scheduled repair appointment at certified repair center.",
                                bookingDate,
                                "MEDIUM",
                                null,
                                null,
                                "/repair-shops",
                                "cyan"
                        ));
                    }
                });

        // ── Source 3: Active repair action plans ─────────────────────────
        actionPlanRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus()))
                .forEach(plan -> {
                    LocalDate actionDate = plan.getCreatedAt().toLocalDate().plusDays(
                            actionPlanPriorityDays(plan.getPriorityLevel()));
                    if (!actionDate.isBefore(LocalDate.now())) {
                        String devName = deviceRepository.findById(plan.getDeviceId())
                                .map(Device::getDeviceName).orElse("Device");
                        events.add(new MaintenanceCalendarResponse(
                                "action-" + plan.getId(),
                                "REPAIR_ACTION",
                                "Repair Action Plan Deadline: " + devName,
                                "Strategy: " + plan.getOverallStrategy() + " — complete within recommended window.",
                                actionDate,
                                plan.getPriorityLevel(),
                                plan.getDeviceId(),
                                devName,
                                "/devices/" + plan.getDeviceId(),
                                "emerald"
                        ));
                    }
                });

        // ── Source 4: Lifecycle urgency alerts ───────────────────────────
        deviceRepository.findByUserId(userId).forEach(device -> {
            predictionRepository.findByDeviceId(device.getId()).ifPresent(pred -> {
                if ("CRITICAL".equalsIgnoreCase(pred.getRiskLevel()) ||
                        "HIGH".equalsIgnoreCase(pred.getRiskLevel())) {
                    events.add(new MaintenanceCalendarResponse(
                            "lifecycle-" + device.getId(),
                            "LIFECYCLE_ALERT",
                            "Lifecycle Alert: " + device.getDeviceName(),
                            "Risk level: " + pred.getRiskLevel() + ". Immediate device care recommended.",
                            LocalDate.now(),
                            "CRITICAL".equalsIgnoreCase(pred.getRiskLevel()) ? "CRITICAL" : "HIGH",
                            device.getId(),
                            device.getDeviceName(),
                            "/devices/" + device.getId(),
                            "red"
                    ));
                }
            });
        });

        // Sort chronologically ascending
        events.sort(Comparator.comparing(MaintenanceCalendarResponse::eventDate));
        return events;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String priorityToColor(String priority, String defaultColor) {
        return switch (priority.toUpperCase()) {
            case "CRITICAL" -> "red";
            case "HIGH"     -> "amber";
            case "MEDIUM"   -> "cyan";
            default         -> defaultColor;
        };
    }

    private LocalDate parseBookingDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    private int actionPlanPriorityDays(String priority) {
        return switch (priority != null ? priority.toUpperCase() : "MEDIUM") {
            case "CRITICAL" -> 3;
            case "HIGH"     -> 7;
            case "MEDIUM"   -> 14;
            default         -> 30;
        };
    }
}
