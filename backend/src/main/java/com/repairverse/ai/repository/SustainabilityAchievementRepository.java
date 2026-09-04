package com.repairverse.ai.repository;

import com.repairverse.ai.entity.SustainabilityAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SustainabilityAchievementRepository extends JpaRepository<SustainabilityAchievement, String> {

    List<SustainabilityAchievement> findByUserIdOrderByUnlockedAtDesc(String userId);

    Optional<SustainabilityAchievement> findByUserIdAndAchievementCode(String userId, String achievementCode);

    boolean existsByUserIdAndAchievementCode(String userId, String achievementCode);

    long countByUserId(String userId);
}
