package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairShopProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairShopProfileRepository extends JpaRepository<RepairShopProfile, String> {

    Optional<RepairShopProfile> findByRepairShopId(String repairShopId);

    List<RepairShopProfile> findByVerificationStatus(String verificationStatus);

    List<RepairShopProfile> findByAverageRatingGreaterThanEqual(Double minRating);

    boolean existsByRepairShopId(String repairShopId);
}
