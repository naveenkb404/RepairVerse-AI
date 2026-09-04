package com.repairverse.ai.repository;

import com.repairverse.ai.entity.DeviceDecisionSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceDecisionSnapshotRepository extends JpaRepository<DeviceDecisionSnapshot, String> {

    List<DeviceDecisionSnapshot> findByDeviceIdAndUserIdOrderByCreatedAtDesc(String deviceId, String userId);

    Optional<DeviceDecisionSnapshot> findFirstByDeviceIdAndUserIdOrderByCreatedAtDesc(String deviceId, String userId);

    List<DeviceDecisionSnapshot> findByUserIdOrderByCreatedAtDesc(String userId);

    void deleteByDeviceIdAndUserId(String deviceId, String userId);
}
