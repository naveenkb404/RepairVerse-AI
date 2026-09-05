package com.repairverse.ai.repository;

import com.repairverse.ai.entity.AiDecisionEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiDecisionEvidenceRepository extends JpaRepository<AiDecisionEvidence, String> {

    List<AiDecisionEvidence> findAllByDecisionRecordId(String decisionRecordId);

    void deleteByDecisionRecordId(String decisionRecordId);
}
