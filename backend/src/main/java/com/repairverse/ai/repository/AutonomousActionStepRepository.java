package com.repairverse.ai.repository;

import com.repairverse.ai.entity.AutonomousActionStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutonomousActionStepRepository extends JpaRepository<AutonomousActionStep, String> {

    List<AutonomousActionStep> findByPlanIdOrderByStepOrderAsc(String planId);

    List<AutonomousActionStep> findByPlanIdAndStatus(String planId, String status);

    Optional<AutonomousActionStep> findFirstByPlanIdAndStatusOrderByStepOrderAsc(String planId, String status);
}
