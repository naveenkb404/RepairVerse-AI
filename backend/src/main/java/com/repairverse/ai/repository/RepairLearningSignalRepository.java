package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairLearningSignal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairLearningSignalRepository extends JpaRepository<RepairLearningSignal, String> {
    List<RepairLearningSignal> findAllByBatchId(String batchId);
    List<RepairLearningSignal> findAllByDeviceCategory(String deviceCategory);
    List<RepairLearningSignal> findAllByComponentType(String componentType);
    List<RepairLearningSignal> findAllBySignalType(String signalType);
    List<RepairLearningSignal> findAllByOrderByObservationCountDesc();
}
