package com.repairverse.ai.repository;

import com.repairverse.ai.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {

    List<ActivityLog> findByUserIdOrderByCreatedAtDesc(String userId);

    List<ActivityLog> findTop20ByUserIdOrderByCreatedAtDesc(String userId);
}
