package com.repairverse.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agent_execution_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentExecutionHistory {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "device_id", length = 36)
    private String deviceId;

    @Column(name = "intervention_id", length = 36)
    private String interventionId;

    @Column(name = "action_step_id", length = 36)
    private String actionStepId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "execution_status", nullable = false, length = 30)
    @Builder.Default
    private String executionStatus = "COMPLETED";

    @Column(name = "result_summary", columnDefinition = "TEXT")
    private String resultSummary;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (executedAt == null) {
            executedAt = LocalDateTime.now();
        }
        if (executionStatus == null) {
            executionStatus = "COMPLETED";
        }
    }
}
