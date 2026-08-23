package com.repairverse.ai.repository;

import com.repairverse.ai.entity.DevicePrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DevicePredictionRepository extends JpaRepository<DevicePrediction, String> {

    /** Latest prediction for a specific device */
    Optional<DevicePrediction> findByDeviceId(String deviceId);

    /** All predictions for a specific user */
    List<DevicePrediction> findByUserIdOrderByEvaluatedAtDesc(String userId);

    /** All predictions at a specific risk level */
    List<DevicePrediction> findByRiskLevelOrderByPredictionScoreAsc(String riskLevel);

    /** Predictions for a user at a specific risk level */
    List<DevicePrediction> findByUserIdAndRiskLevel(String userId, String riskLevel);

    /** Count devices at a risk level platform-wide */
    long countByRiskLevel(String riskLevel);

    /** Count devices at a risk level for a user */
    long countByUserIdAndRiskLevel(String userId, String riskLevel);

    /** Check for recent HIGH/CRITICAL notifications to enforce 24-hour dedup window */
    @Query("""
            SELECT COUNT(dp) > 0 FROM DevicePrediction dp
            WHERE dp.deviceId = :deviceId
              AND dp.riskLevel IN ('HIGH', 'CRITICAL')
              AND dp.notificationSent = true
              AND dp.evaluatedAt >= :since
            """)
    boolean existsRecentHighRiskNotification(
            @Param("deviceId") String deviceId,
            @Param("since") LocalDateTime since
    );

    /** Fleet overview aggregates for admin/dashboard */
    @Query("""
            SELECT AVG(dp.predictionScore) FROM DevicePrediction dp
            WHERE dp.userId = :userId
            """)
    Double findAveragePredictionScoreByUserId(@Param("userId") String userId);

    @Query("SELECT AVG(dp.predictionScore) FROM DevicePrediction dp")
    Double findPlatformAveragePredictionScore();

    /** Devices ordered by risk (lowest score first) for admin views */
    @Query("""
            SELECT dp FROM DevicePrediction dp
            WHERE dp.riskLevel IN ('CRITICAL', 'HIGH')
            ORDER BY dp.predictionScore ASC, dp.evaluatedAt DESC
            """)
    List<DevicePrediction> findHighAndCriticalRiskDevices();

    /** Sum of estimated repair costs across all predictions */
    @Query("SELECT COALESCE(SUM(dp.estimatedRepairCost), 0.0) FROM DevicePrediction dp")
    Double sumTotalEstimatedRepairCost();

    /** Sum of preventive savings across all predictions */
    @Query("SELECT COALESCE(SUM(dp.preventiveSavings), 0.0) FROM DevicePrediction dp")
    Double sumTotalPreventiveSavings();
}
