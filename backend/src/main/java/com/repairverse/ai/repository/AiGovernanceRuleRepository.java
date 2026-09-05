package com.repairverse.ai.repository;

import com.repairverse.ai.entity.AiGovernanceRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiGovernanceRuleRepository extends JpaRepository<AiGovernanceRule, String> {

    List<AiGovernanceRule> findAllByIsActiveTrue();

    Optional<AiGovernanceRule> findByRuleName(String ruleName);
}
