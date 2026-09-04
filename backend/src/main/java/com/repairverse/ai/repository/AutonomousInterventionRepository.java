package com.repairverse.ai.repository;

import com.repairverse.ai.entity.AutonomousIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutonomousInterventionRepository extends JpaRepository<AutonomousIntervention, String> {

    List<AutonomousIntervention> findByUserIdOrderByCreatedAtDesc(String userId);

    List<AutonomousIntervention> findByUserIdAndStatusOrderByCreatedAtDesc(String userId, String status);

    List<AutonomousIntervention> findByDeviceIdAndUserIdOrderByCreatedAtDesc(String deviceId, String userId);

    Optional<AutonomousIntervention> findByIdAndUserId(String id, String userId);

    Optional<AutonomousIntervention> findFirstByDeviceIdAndUserIdAndInterventionTypeAndStatusIn(
            String deviceId, String userId, String interventionType, List<String> statuses
    );

    long countByUserIdAndStatus(String userId, String status);

    long countByUserIdAndStatusIn(String userId, List<String> statuses);

    void deleteByDeviceIdAndUserId(String deviceId, String userId);
}
