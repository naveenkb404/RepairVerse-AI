package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairHistoryRepository extends JpaRepository<RepairHistory, String> {

    List<RepairHistory> findByUserIdOrderByRepairDateDesc(String userId);

    List<RepairHistory> findByDeviceIdOrderByRepairDateDesc(String deviceId);

    Optional<RepairHistory> findByIdAndUserId(String id, String userId);

    long countByUserId(String userId);

    long countByUserIdAndStatus(String userId, String status);
}
