package com.repairverse.ai.repository;

import com.repairverse.ai.entity.AiDecisionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiDecisionRecordRepository extends JpaRepository<AiDecisionRecord, String> {

    List<AiDecisionRecord> findAllByUserIdOrderByCreatedAtDesc(String userId);

    List<AiDecisionRecord> findByDeviceIdAndUserId(String deviceId, String userId);

    List<AiDecisionRecord> findByUserIdAndSourceSystem(String userId, String sourceSystem);

    long countByUserIdAndTrustScoreLessThan(String userId, Integer trustScore);

    List<AiDecisionRecord> findByUserIdAndStatus(String userId, String status);

    List<AiDecisionRecord> findByDeviceIdAndUserIdOrderByCreatedAtDesc(String deviceId, String userId);

    long countByUserId(String userId);

    long countByUserIdAndTrustTier(String userId, String trustTier);

    long countByUserIdAndUserFeedback(String userId, String userFeedback);
}
