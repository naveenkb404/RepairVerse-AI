package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairShopRepository extends JpaRepository<RepairShop, String> {
    List<RepairShop> findByRatingGreaterThanEqual(Double minRating);
    List<RepairShop> findByEcoCertifiedTrue();
}
