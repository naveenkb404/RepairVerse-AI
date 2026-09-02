package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairReviewRepository extends JpaRepository<RepairReview, String> {

    List<RepairReview> findByRepairShopIdOrderByCreatedAtDesc(String repairShopId);

    List<RepairReview> findByUserIdOrderByCreatedAtDesc(String userId);

    boolean existsByUserIdAndRepairShopIdAndBookingId(String userId, String repairShopId, String bookingId);

    boolean existsByUserIdAndRepairShopId(String userId, String repairShopId);

    long countByRepairShopId(String repairShopId);

    @Query("SELECT AVG(r.rating) FROM RepairReview r WHERE r.repairShopId = :repairShopId")
    Double calculateAverageRatingByShopId(@Param("repairShopId") String repairShopId);

    @Query("SELECT r.rating, COUNT(r) FROM RepairReview r WHERE r.repairShopId = :repairShopId GROUP BY r.rating")
    List<Object[]> countReviewsByRatingDistribution(@Param("repairShopId") String repairShopId);
}
