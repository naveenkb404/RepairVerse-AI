package com.repairverse.ai.repository;

import com.repairverse.ai.entity.FederatedLearningBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FederatedLearningBatchRepository extends JpaRepository<FederatedLearningBatch, String> {
    Optional<FederatedLearningBatch> findByBatchReference(String batchReference);
    List<FederatedLearningBatch> findAllByOrderByCreatedAtDesc();
    List<FederatedLearningBatch> findAllByStatus(String status);
    Optional<FederatedLearningBatch> findTopByStatusOrderByCreatedAtDesc(String status);
}
