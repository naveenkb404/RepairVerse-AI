package com.repairverse.ai.repository;

import com.repairverse.ai.entity.AiGovernanceViolation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiGovernanceViolationRepository extends JpaRepository<AiGovernanceViolation, String> {

    List<AiGovernanceViolation> findAllByDecisionRecordId(String decisionRecordId);

    List<AiGovernanceViolation> findAllBySeverityAndAutoResolvedFalse(String severity);

    long countByDecisionRecordId(String decisionRecordId);
}
