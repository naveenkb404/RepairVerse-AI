package com.repairverse.ai.repository;

import com.repairverse.ai.entity.CircularImpactEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CircularImpactEventRepository extends JpaRepository<CircularImpactEvent, String> {

    List<CircularImpactEvent> findByUserIdOrderByEventDateDesc(String userId);

    List<CircularImpactEvent> findByUserId(String userId);

    List<CircularImpactEvent> findByDeviceId(String deviceId);

    List<CircularImpactEvent> findByDeviceIdOrderByEventDateDesc(String deviceId);

    List<CircularImpactEvent> findByUserIdAndEventDateBetween(String userId, LocalDateTime start, LocalDateTime end);

    List<CircularImpactEvent> findByUserIdAndEventType(String userId, String eventType);

    long countByUserId(String userId);

    @Query("SELECT COALESCE(SUM(e.carbonSavedKg), 0.0) FROM CircularImpactEvent e WHERE e.userId = :userId")
    Double sumCarbonSavedByUserId(@Param("userId") String userId);

    @Query("SELECT COALESCE(SUM(e.ewastePreventedKg), 0.0) FROM CircularImpactEvent e WHERE e.userId = :userId")
    Double sumEwastePreventedByUserId(@Param("userId") String userId);

    @Query("SELECT COALESCE(SUM(e.moneySaved), 0.0) FROM CircularImpactEvent e WHERE e.userId = :userId")
    Double sumMoneySavedByUserId(@Param("userId") String userId);

    @Query("SELECT COALESCE(SUM(e.deviceLifeExtensionDays), 0) FROM CircularImpactEvent e WHERE e.userId = :userId")
    Long sumLifeExtensionDaysByUserId(@Param("userId") String userId);

    @Query("SELECT COALESCE(SUM(e.carbonSavedKg), 0.0) FROM CircularImpactEvent e")
    Double sumPlatformCarbonSaved();

    @Query("SELECT COALESCE(SUM(e.ewastePreventedKg), 0.0) FROM CircularImpactEvent e")
    Double sumPlatformEwastePrevented();

    @Query("SELECT COALESCE(SUM(e.moneySaved), 0.0) FROM CircularImpactEvent e")
    Double sumPlatformMoneySaved();

    @Query("SELECT COUNT(DISTINCT e.userId) FROM CircularImpactEvent e")
    long countDistinctUsers();

    @Query("SELECT COUNT(DISTINCT e.deviceId) FROM CircularImpactEvent e WHERE e.deviceId IS NOT NULL")
    long countDistinctDevices();
}
