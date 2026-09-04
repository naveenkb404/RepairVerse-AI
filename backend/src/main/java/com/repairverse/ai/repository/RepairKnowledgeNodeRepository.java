package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairKnowledgeNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairKnowledgeNodeRepository extends JpaRepository<RepairKnowledgeNode, String> {

    Optional<RepairKnowledgeNode> findByNodeTypeAndNodeKey(String nodeType, String nodeKey);

    List<RepairKnowledgeNode> findByNodeType(String nodeType);

    List<RepairKnowledgeNode> findByNodeTypeOrderByObservationCountDesc(String nodeType);

    List<RepairKnowledgeNode> findTop50ByOrderByObservationCountDesc();
}
