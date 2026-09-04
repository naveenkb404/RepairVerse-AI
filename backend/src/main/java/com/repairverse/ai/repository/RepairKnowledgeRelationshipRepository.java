package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairKnowledgeRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairKnowledgeRelationshipRepository extends JpaRepository<RepairKnowledgeRelationship, String> {

    Optional<RepairKnowledgeRelationship> findBySourceNodeIdAndTargetNodeIdAndRelationshipType(
            String sourceNodeId, String targetNodeId, String relationshipType);

    List<RepairKnowledgeRelationship> findBySourceNodeId(String sourceNodeId);

    List<RepairKnowledgeRelationship> findByTargetNodeId(String targetNodeId);

    List<RepairKnowledgeRelationship> findByRelationshipType(String relationshipType);

    List<RepairKnowledgeRelationship> findBySourceNodeIdAndRelationshipTypeOrderByStrengthDesc(
            String sourceNodeId, String relationshipType);

    List<RepairKnowledgeRelationship> findTop100ByOrderByStrengthDesc();
}
