package com.repairverse.ai.repository;

import com.repairverse.ai.entity.LearningFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningFeedbackRepository extends JpaRepository<LearningFeedback, String> {
    List<LearningFeedback> findAllByModelVersionId(String modelVersionId);
    List<LearningFeedback> findAllByDecisionReference(String decisionReference);
    List<LearningFeedback> findAllByFeedbackType(String feedbackType);
}
