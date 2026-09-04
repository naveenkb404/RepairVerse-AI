package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairPatternInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairPatternInsightRepository extends JpaRepository<RepairPatternInsight, String> {

    List<RepairPatternInsight> findByStatusOrderByImpactScoreDesc(String status);

    List<RepairPatternInsight> findByInsightTypeAndStatusOrderByImpactScoreDesc(String insightType, String status);

    List<RepairPatternInsight> findByDeviceCategoryAndStatusOrderByImpactScoreDesc(String deviceCategory, String status);
}
