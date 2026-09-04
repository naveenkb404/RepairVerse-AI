package com.repairverse.ai.repository;

import com.repairverse.ai.entity.DeviceDecisionScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceDecisionScenarioRepository extends JpaRepository<DeviceDecisionScenario, String> {

    List<DeviceDecisionScenario> findByDeviceIdAndUserIdOrderByCreatedAtDesc(String deviceId, String userId);

    List<DeviceDecisionScenario> findByDeviceIdAndUserId(String deviceId, String userId);

    Optional<DeviceDecisionScenario> findByDeviceIdAndUserIdAndScenarioType(String deviceId, String userId, String scenarioType);

    void deleteByDeviceIdAndUserId(String deviceId, String userId);
}
