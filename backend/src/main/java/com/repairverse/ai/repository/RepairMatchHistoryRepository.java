package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairMatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairMatchHistoryRepository extends JpaRepository<RepairMatchHistory, String> {

    List<RepairMatchHistory> findByUserIdAndDeviceIdOrderByCreatedAtDesc(String userId, String deviceId);

    List<RepairMatchHistory> findByUserIdOrderByCreatedAtDesc(String userId);

    List<RepairMatchHistory> findByRepairShopIdOrderByCreatedAtDesc(String repairShopId);
}
