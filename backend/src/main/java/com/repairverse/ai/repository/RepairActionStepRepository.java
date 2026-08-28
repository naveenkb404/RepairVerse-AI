package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairActionStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairActionStepRepository extends JpaRepository<RepairActionStep, String> {

    /** Find ordered steps for an action plan */
    List<RepairActionStep> findByActionPlanIdOrderByStepOrderAsc(String actionPlanId);

    /** Delete all steps for an action plan */
    void deleteByActionPlanId(String actionPlanId);
}
