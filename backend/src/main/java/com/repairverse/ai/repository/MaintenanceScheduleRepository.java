package com.repairverse.ai.repository;

import com.repairverse.ai.entity.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for deterministic maintenance schedule persistence and ownership-safe retrieval.
 */
@Repository
public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, String> {

    /** All schedules for a user, ordered soonest due first. */
    List<MaintenanceSchedule> findByUserIdOrderByDueDateAsc(String userId);

    /** All schedules for a specific device owned by a user. */
    List<MaintenanceSchedule> findByUserIdAndDeviceIdOrderByDueDateAsc(String userId, String deviceId);

    /** Schedules matching one or more statuses, ordered soonest first. */
    List<MaintenanceSchedule> findByUserIdAndStatusInOrderByDueDateAsc(String userId, List<String> statuses);

    /** Overdue detection: all schedules past due date in given statuses. */
    List<MaintenanceSchedule> findByDueDateBeforeAndStatusIn(LocalDate date, List<String> statuses);

    /** Ownership-safe single schedule retrieval. */
    Optional<MaintenanceSchedule> findByIdAndUserId(String id, String userId);

    /**
     * Deduplication guard: prevents generating duplicate maintenance entries.
     * Checks for a matching schedule within a date window for the same device and type.
     */
    boolean existsByDeviceIdAndMaintenanceTypeAndDueDateBetweenAndStatusIn(
            String deviceId,
            String maintenanceType,
            LocalDate dueDateStart,
            LocalDate dueDateEnd,
            List<String> statuses
    );

    /** Count of active (non-completed/non-cancelled) schedules for a device. */
    long countByDeviceIdAndStatusIn(String deviceId, List<String> statuses);

    /** Schedules created for a device in any status. */
    List<MaintenanceSchedule> findByDeviceIdOrderByDueDateAsc(String deviceId);
}
