package com.repairverse.ai.repository;

import com.repairverse.ai.entity.DeviceIntelligenceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceIntelligenceAlertRepository extends JpaRepository<DeviceIntelligenceAlert, String> {

    List<DeviceIntelligenceAlert> findByUserIdOrderByCreatedAtDesc(String userId);

    List<DeviceIntelligenceAlert> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(String userId);

    List<DeviceIntelligenceAlert> findByDeviceIdAndUserIdOrderByCreatedAtDesc(String deviceId, String userId);

    Optional<DeviceIntelligenceAlert> findByIdAndUserId(String id, String userId);

    Optional<DeviceIntelligenceAlert> findFirstByDeviceIdAndUserIdAndAlertTypeAndIsReadFalse(String deviceId, String userId, String alertType);

    long countByUserIdAndIsReadFalse(String userId);
}
