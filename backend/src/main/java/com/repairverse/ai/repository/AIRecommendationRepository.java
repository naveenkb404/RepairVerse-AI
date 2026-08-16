package com.repairverse.ai.repository;

import com.repairverse.ai.entity.AIRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AIRecommendationRepository extends JpaRepository<AIRecommendation, String> {
    Optional<AIRecommendation> findByDiagnosisId(String diagnosisId);
    boolean existsByDiagnosisId(String diagnosisId);
}
