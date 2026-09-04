package com.repairverse.ai.repository;

import com.repairverse.ai.entity.AutonomousActionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutonomousActionPlanRepository extends JpaRepository<AutonomousActionPlan, String> {

    Optional<AutonomousActionPlan> findByInterventionId(String interventionId);

    void deleteByInterventionId(String interventionId);
}
