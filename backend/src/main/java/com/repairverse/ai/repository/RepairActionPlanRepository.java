package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairActionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairActionPlanRepository extends JpaRepository<RepairActionPlan, String> {

    /** Find all action plans for a user ordered by creation date descending */
    List<RepairActionPlan> findByUserIdOrderByCreatedAtDesc(String userId);

    /** Find all action plans for a specific device */
    List<RepairActionPlan> findByDeviceIdOrderByCreatedAtDesc(String deviceId);

    /** Find the latest active action plan for a specific device and user */
    Optional<RepairActionPlan> findFirstByDeviceIdAndUserIdOrderByCreatedAtDesc(String deviceId, String userId);

    /** Find the latest active action plan for a device */
    Optional<RepairActionPlan> findFirstByDeviceIdOrderByCreatedAtDesc(String deviceId);

    /** Find action plan by id and user id to enforce strict tenant isolation */
    Optional<RepairActionPlan> findByIdAndUserId(String id, String userId);

    /** Count active plans by strategy for a user */
    long countByUserIdAndOverallStrategy(String userId, String overallStrategy);

    /** Count active high priority or critical plans for a user */
    @Query("SELECT COUNT(p) FROM RepairActionPlan p WHERE p.userId = :userId AND p.priorityLevel IN ('HIGH', 'CRITICAL') AND p.status = 'ACTIVE'")
    long countHighPriorityPlansForUser(@Param("userId") String userId);
}
