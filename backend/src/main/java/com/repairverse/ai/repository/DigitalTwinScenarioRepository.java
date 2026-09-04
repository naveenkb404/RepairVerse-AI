package com.repairverse.ai.repository;

import com.repairverse.ai.entity.DigitalTwinScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalTwinScenarioRepository extends JpaRepository<DigitalTwinScenario, String> {

    List<DigitalTwinScenario> findByDeviceIdOrderByOverallOutcomeScoreDesc(String deviceId);

    List<DigitalTwinScenario> findByDeviceIdAndUserIdOrderByOverallOutcomeScoreDesc(String deviceId, String userId);

    List<DigitalTwinScenario> findByDeviceId(String deviceId);

    void deleteByDeviceId(String deviceId);
}
