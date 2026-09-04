package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairShopQualitySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairShopQualitySnapshotRepository extends JpaRepository<RepairShopQualitySnapshot, String> {

    @Query("SELECT s FROM RepairShopQualitySnapshot s WHERE s.repairShopId = :shopId ORDER BY s.calculatedAt DESC LIMIT 1")
    Optional<RepairShopQualitySnapshot> findLatestByRepairShopId(@Param("shopId") String shopId);

    @Query("SELECT s FROM RepairShopQualitySnapshot s WHERE s.repairShopId = :shopId ORDER BY s.calculatedAt DESC")
    List<RepairShopQualitySnapshot> findHistoricalByRepairShopId(@Param("shopId") String shopId);

    @Query("SELECT s FROM RepairShopQualitySnapshot s WHERE s.qualityTier = :tier ORDER BY s.overallQualityScore DESC")
    List<RepairShopQualitySnapshot> findByQualityTier(@Param("tier") String qualityTier);

    @Query("SELECT s FROM RepairShopQualitySnapshot s WHERE s.repairShopId IN :shopIds ORDER BY s.calculatedAt DESC")
    List<RepairShopQualitySnapshot> findLatestForShops(@Param("shopIds") List<String> shopIds);

    @Query("SELECT s FROM RepairShopQualitySnapshot s WHERE s.overallQualityScore >= :minScore ORDER BY s.overallQualityScore DESC LIMIT :limit")
    List<RepairShopQualitySnapshot> findTopRankedShops(@Param("minScore") int minScore, @Param("limit") int limit);

    @Query("SELECT s FROM RepairShopQualitySnapshot s WHERE s.trustScore >= :minTrust ORDER BY s.trustScore DESC LIMIT :limit")
    List<RepairShopQualitySnapshot> findMostTrustedShops(@Param("minTrust") int minTrust, @Param("limit") int limit);

    @Query("SELECT s FROM RepairShopQualitySnapshot s ORDER BY s.serviceSpeedScore DESC LIMIT :limit")
    List<RepairShopQualitySnapshot> findFastestShops(@Param("limit") int limit);

    @Query("SELECT s FROM RepairShopQualitySnapshot s ORDER BY s.priceFairnessScore DESC LIMIT :limit")
    List<RepairShopQualitySnapshot> findBestValueShops(@Param("limit") int limit);
}
