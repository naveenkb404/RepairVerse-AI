package com.repairverse.ai.repository;

import com.repairverse.ai.entity.PrivacyAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrivacyAuditEventRepository extends JpaRepository<PrivacyAuditEvent, String> {
    List<PrivacyAuditEvent> findAllByBatchId(String batchId);
    List<PrivacyAuditEvent> findAllByOrderByCreatedAtDesc();
    List<PrivacyAuditEvent> findAllByEventType(String eventType);
}
