package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairQuoteRepository extends JpaRepository<RepairQuote, String> {

    List<RepairQuote> findByUserIdOrderByCreatedAtDesc(String userId);

    List<RepairQuote> findByUserIdAndDeviceIdOrderByCreatedAtDesc(String userId, String deviceId);

    List<RepairQuote> findByUserIdAndStatusInOrderByCreatedAtDesc(String userId, List<String> statuses);

    List<RepairQuote> findByRepairShopIdOrderByCreatedAtDesc(String repairShopId);

    Optional<RepairQuote> findByIdAndUserId(String id, String userId);

    List<RepairQuote> findByIdInAndUserId(List<String> ids, String userId);

    long countByUserIdAndStatus(String userId, String status);
}
