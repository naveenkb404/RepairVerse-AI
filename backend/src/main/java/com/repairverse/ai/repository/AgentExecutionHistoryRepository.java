package com.repairverse.ai.repository;

import com.repairverse.ai.entity.AgentExecutionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentExecutionHistoryRepository extends JpaRepository<AgentExecutionHistory, String> {

    List<AgentExecutionHistory> findByUserIdOrderByExecutedAtDesc(String userId);

    List<AgentExecutionHistory> findByDeviceIdAndUserIdOrderByExecutedAtDesc(String deviceId, String userId);

    List<AgentExecutionHistory> findByInterventionIdOrderByExecutedAtDesc(String interventionId);

    long countByUserId(String userId);

    long countByUserIdAndExecutionStatus(String userId, String executionStatus);
}
