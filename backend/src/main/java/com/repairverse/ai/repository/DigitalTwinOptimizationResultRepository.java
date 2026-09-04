package com.repairverse.ai.repository;

import com.repairverse.ai.entity.DigitalTwinOptimizationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DigitalTwinOptimizationResultRepository extends JpaRepository<DigitalTwinOptimizationResult, String> {

    Optional<DigitalTwinOptimizationResult> findTopByDeviceIdOrderByCreatedAtDesc(String deviceId);

    Optional<DigitalTwinOptimizationResult> findFirstByDeviceIdAndUserIdOrderByCreatedAtDesc(String deviceId, String userId);

    List<DigitalTwinOptimizationResult> findByUserId(String userId);

    List<DigitalTwinOptimizationResult> findByUserIdOrderByCreatedAtDesc(String userId);

    void deleteByDeviceId(String deviceId);
}
