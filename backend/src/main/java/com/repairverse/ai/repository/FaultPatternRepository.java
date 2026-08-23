package com.repairverse.ai.repository;

import com.repairverse.ai.entity.FaultPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaultPatternRepository extends JpaRepository<FaultPattern, String> {

    /** Active patterns for a specific device category */
    List<FaultPattern> findByDeviceCategoryAndIsActiveTrueOrderByRiskWeightDesc(String deviceCategory);

    /** All active patterns regardless of category */
    List<FaultPattern> findByIsActiveTrueOrderByRiskWeightDesc();

    /** Active patterns matching a category or applicable to all categories */
    @Query("""
            SELECT fp FROM FaultPattern fp
            WHERE fp.isActive = true
              AND (fp.deviceCategory = :category OR fp.deviceCategory IS NULL)
            ORDER BY fp.riskWeight DESC
            """)
    List<FaultPattern> findActiveByCategory(@Param("category") String category);

    /** Active patterns matching category and brand */
    @Query("""
            SELECT fp FROM FaultPattern fp
            WHERE fp.isActive = true
              AND (fp.deviceCategory = :category OR fp.deviceCategory IS NULL)
              AND (fp.deviceBrand = :brand OR fp.deviceBrand IS NULL)
            ORDER BY fp.riskWeight DESC
            """)
    List<FaultPattern> findActiveByCategoryAndBrand(
            @Param("category") String category,
            @Param("brand") String brand
    );
}
