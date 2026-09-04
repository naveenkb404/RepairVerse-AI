package com.repairverse.ai.repository;

import com.repairverse.ai.entity.SustainabilityGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SustainabilityGoalRepository extends JpaRepository<SustainabilityGoal, String> {

    List<SustainabilityGoal> findByUserIdOrderByCreatedAtDesc(String userId);

    List<SustainabilityGoal> findByUserIdAndStatus(String userId, String status);

    List<SustainabilityGoal> findByUserIdAndGoalTypeAndStatus(String userId, String goalType, String status);

    Optional<SustainabilityGoal> findByIdAndUserId(String id, String userId);

    long countByUserIdAndStatus(String userId, String status);
}
