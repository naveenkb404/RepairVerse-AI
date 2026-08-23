package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairTimelineStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairTimelineStageRepository extends JpaRepository<RepairTimelineStage, String> {

    List<RepairTimelineStage> findByRepairIdOrderByStageDateAsc(String repairId);

    void deleteByRepairId(String repairId);
}
