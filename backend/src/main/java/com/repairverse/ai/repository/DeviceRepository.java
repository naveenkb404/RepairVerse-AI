package com.repairverse.ai.repository;

import com.repairverse.ai.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    List<Device> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Device> findByIdAndUserId(String id, String userId);
    boolean existsByIdAndUserId(String id, String userId);
}
