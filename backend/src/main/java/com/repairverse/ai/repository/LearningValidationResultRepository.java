package com.repairverse.ai.repository;

import com.repairverse.ai.entity.LearningValidationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningValidationResultRepository extends JpaRepository<LearningValidationResult, String> {
    List<LearningValidationResult> findAllByModelVersionId(String modelVersionId);
    List<LearningValidationResult> findAllByModelVersionIdOrderByValidatedAtDesc(String modelVersionId);
    List<LearningValidationResult> findAllByDecision(String decision);
}
