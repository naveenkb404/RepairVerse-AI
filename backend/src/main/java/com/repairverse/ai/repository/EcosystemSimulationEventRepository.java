package com.repairverse.ai.repository;

import com.repairverse.ai.entity.EcosystemSimulationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EcosystemSimulationEventRepository extends JpaRepository<EcosystemSimulationEvent, String> {

    List<EcosystemSimulationEvent> findByDeviceIdOrderByProjectedMonthOffsetAsc(String deviceId);

    List<EcosystemSimulationEvent> findByDeviceIdAndUserIdOrderByProjectedMonthOffsetAsc(String deviceId, String userId);

    List<EcosystemSimulationEvent> findByUserIdOrderByCreatedAtDesc(String userId);

    void deleteByDeviceId(String deviceId);
}
