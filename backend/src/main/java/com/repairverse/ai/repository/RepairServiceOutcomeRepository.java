package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairServiceOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepairServiceOutcomeRepository extends JpaRepository<RepairServiceOutcome, String> {

    List<RepairServiceOutcome> findByRepairShopId(String repairShopId);

    List<RepairServiceOutcome> findByUserId(String userId);

    List<RepairServiceOutcome> findByDeviceId(String deviceId);

    List<RepairServiceOutcome> findByRepairShopIdAndRepairCategory(String repairShopId, String repairCategory);

    List<RepairServiceOutcome> findByRepairShopIdAndCompletedAtBetween(
        String repairShopId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT COUNT(o) FROM RepairServiceOutcome o WHERE o.repairShopId = :shopId AND o.repairSuccessful = true")
    long countSuccessfulByShopId(@Param("shopId") String shopId);

    @Query("SELECT COUNT(o) FROM RepairServiceOutcome o WHERE o.repairShopId = :shopId AND o.repairSuccessful = false")
    long countFailedByShopId(@Param("shopId") String shopId);

    @Query("SELECT COUNT(o) FROM RepairServiceOutcome o WHERE o.repairShopId = :shopId AND o.repeatRepairRequired = true")
    long countRepeatRepairsByShopId(@Param("shopId") String shopId);

    @Query("SELECT COUNT(o) FROM RepairServiceOutcome o WHERE o.repairShopId = :shopId AND o.warrantyClaimed = true")
    long countWarrantyClaimsByShopId(@Param("shopId") String shopId);

    @Query("SELECT AVG(o.customerSatisfaction) FROM RepairServiceOutcome o WHERE o.repairShopId = :shopId")
    Double avgSatisfactionByShopId(@Param("shopId") String shopId);

    @Query("SELECT AVG(o.repairCost) FROM RepairServiceOutcome o WHERE o.repairShopId = :shopId")
    Double avgRepairCostByShopId(@Param("shopId") String shopId);

    @Query("SELECT o.repairCategory, COUNT(o) FROM RepairServiceOutcome o GROUP BY o.repairCategory ORDER BY COUNT(o) DESC")
    List<Object[]> countByCategory();

    @Query("SELECT o.repairCategory, AVG(CASE WHEN o.repairSuccessful = true THEN 1.0 ELSE 0.0 END) FROM RepairServiceOutcome o GROUP BY o.repairCategory")
    List<Object[]> successRateByCategory();

    @Query("SELECT COUNT(o) FROM RepairServiceOutcome o WHERE o.repairShopId = :shopId")
    long countByRepairShopId(@Param("shopId") String shopId);
}
