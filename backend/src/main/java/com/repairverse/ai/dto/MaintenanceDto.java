package com.repairverse.ai.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Phase 25 — Proactive Device Care DTO records.
 * All records are immutable value objects.
 */
public final class MaintenanceDto {

    private MaintenanceDto() {}

    /**
     * Full maintenance schedule response returned by GET /maintenance endpoints.
     */
    public record MaintenanceScheduleResponse(
            String id,
            String userId,
            String deviceId,
            String deviceName,
            String deviceCategory,
            String title,
            String description,
            String maintenanceType,
            String priority,
            LocalDate scheduledDate,
            LocalDate dueDate,
            String status,
            Double estimatedCost,
            Integer estimatedDurationMinutes,
            Double estimatedCarbonSavings,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime completedAt,
            boolean isDemo
    ) {}

    /**
     * Compact task card format for dashboard and timeline widgets.
     */
    public record MaintenanceTaskResponse(
            String id,
            String deviceId,
            String deviceName,
            String title,
            String maintenanceType,
            String priority,
            LocalDate dueDate,
            String status,
            Double estimatedCost,
            boolean isOverdue,
            int daysUntilDue
    ) {}

    /**
     * Unified calendar event — aggregates maintenance, bookings, action plans, lifecycle alerts.
     */
    public record MaintenanceCalendarResponse(
            String eventId,
            String eventType,       // MAINTENANCE | BOOKING | REPAIR_ACTION | LIFECYCLE_ALERT
            String title,
            String description,
            LocalDate eventDate,
            String priority,        // CRITICAL | HIGH | MEDIUM | LOW
            String deviceId,
            String deviceName,
            String actionUrl,
            String colorTag         // amber | cyan | emerald | red
    ) {}

    /**
     * Aggregated maintenance summary for dashboard cards and summary endpoint.
     */
    public record MaintenanceSummaryResponse(
            int totalUpcoming,
            int totalDue,
            int totalOverdue,
            int totalCritical,
            int completedThisMonth,
            Double totalEstimatedSavingsIfCompleted,
            Double totalCarbonSavingsIfCompleted,
            boolean isDemo
    ) {}

    /**
     * Optional client-provided overrides for maintenance generation.
     * All fields are nullable — deterministic rules apply when absent.
     */
    public record CreateMaintenanceRequest(
            String preferredDate,
            Boolean includeOptional
    ) {}

    /**
     * Status transition request body.
     * Allowed: UPCOMING/DUE/OVERDUE → COMPLETED, SKIPPED, CANCELLED
     */
    public record UpdateMaintenanceStatusRequest(
            String status
    ) {}

    /**
     * Priority engine response — deterministic score + rationale.
     */
    public record MaintenancePriorityResponse(
            String deviceId,
            String deviceName,
            int priorityScore,
            String priorityLevel,
            String reason,
            String recommendedAction,
            String riskContributor,
            LocalDateTime evaluatedAt,
            boolean isDemo
    ) {}
}
