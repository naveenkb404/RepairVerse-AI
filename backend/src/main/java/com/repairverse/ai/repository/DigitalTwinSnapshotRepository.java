package com.repairverse.ai.repository;

import com.repairverse.ai.entity.DigitalTwinSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DigitalTwinSnapshotRepository extends JpaRepository<DigitalTwinSnapshot, String> {

    Optional<DigitalTwinSnapshot> findTopByDeviceIdOrderBySnapshotTimeDesc(String deviceId);

    Optional<DigitalTwinSnapshot> findFirstByDeviceIdAndUserIdOrderBySnapshotTimeDesc(String deviceId, String userId);

    List<DigitalTwinSnapshot> findByUserId(String userId);

    List<DigitalTwinSnapshot> findByUserIdOrderBySnapshotTimeDesc(String userId);

    List<DigitalTwinSnapshot> findByDeviceIdOrderBySnapshotTimeDesc(String deviceId);

    void deleteByDeviceId(String deviceId);
}
