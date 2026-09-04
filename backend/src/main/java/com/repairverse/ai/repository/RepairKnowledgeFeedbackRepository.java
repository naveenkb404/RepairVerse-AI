package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairKnowledgeFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairKnowledgeFeedbackRepository extends JpaRepository<RepairKnowledgeFeedback, String> {

    List<RepairKnowledgeFeedback> findByInsightId(String insightId);

    List<RepairKnowledgeFeedback> findByUserId(String userId);

    long countByInsightIdAndFeedbackType(String insightId, String feedbackType);
}
