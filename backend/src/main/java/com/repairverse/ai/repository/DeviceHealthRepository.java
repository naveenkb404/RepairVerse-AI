package com.repairverse.ai.repository;

import com.repairverse.ai.entity.DeviceHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceHealthRepository extends JpaRepository<DeviceHealth, String> {
    Optional<DeviceHealth> findByDeviceId(String deviceId);
    void deleteByDeviceId(String deviceId);
}
