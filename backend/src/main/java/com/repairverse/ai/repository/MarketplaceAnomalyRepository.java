package com.repairverse.ai.repository;

import com.repairverse.ai.entity.MarketplaceAnomaly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketplaceAnomalyRepository extends JpaRepository<MarketplaceAnomaly, String> {

    List<MarketplaceAnomaly> findByRepairShopId(String repairShopId);

    List<MarketplaceAnomaly> findByStatus(String status);

    List<MarketplaceAnomaly> findBySeverity(String severity);

    List<MarketplaceAnomaly> findByStatusAndSeverity(String status, String severity);

    List<MarketplaceAnomaly> findByRepairShopIdAndStatus(String repairShopId, String status);

    @Query("SELECT a FROM MarketplaceAnomaly a WHERE a.status = 'OPEN' OR a.status = 'UNDER_REVIEW' ORDER BY a.riskScore DESC, a.detectedAt DESC")
    List<MarketplaceAnomaly> findActiveAnomalies();

    @Query("SELECT a FROM MarketplaceAnomaly a WHERE a.repairShopId = :shopId ORDER BY a.detectedAt DESC")
    List<MarketplaceAnomaly> findByShopIdOrdered(@Param("shopId") String shopId);

    @Query("SELECT COUNT(a) FROM MarketplaceAnomaly a WHERE a.status = 'OPEN'")
    long countOpenAnomalies();

    @Query("SELECT COUNT(a) FROM MarketplaceAnomaly a WHERE a.repairShopId = :shopId AND (a.status = 'OPEN' OR a.status = 'UNDER_REVIEW')")
    long countActiveByShopId(@Param("shopId") String shopId);

    @Query("SELECT a FROM MarketplaceAnomaly a WHERE a.severity = 'CRITICAL' OR a.severity = 'HIGH' ORDER BY a.riskScore DESC")
    List<MarketplaceAnomaly> findHighSeverityAnomalies();
}
