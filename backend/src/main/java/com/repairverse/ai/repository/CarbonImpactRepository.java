package com.repairverse.ai.repository;

import com.repairverse.ai.entity.CarbonImpact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarbonImpactRepository extends JpaRepository<CarbonImpact, String> {
    Optional<CarbonImpact> findByUserId(String userId);
}
