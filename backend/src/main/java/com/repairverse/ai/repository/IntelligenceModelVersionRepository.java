package com.repairverse.ai.repository;

import com.repairverse.ai.entity.IntelligenceModelVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntelligenceModelVersionRepository extends JpaRepository<IntelligenceModelVersion, String> {
    Optional<IntelligenceModelVersion> findByVersion(String version);
    Optional<IntelligenceModelVersion> findFirstByStatusOrderByActivatedAtDesc(String status);
    List<IntelligenceModelVersion> findAllByOrderByCreatedAtDesc();
    List<IntelligenceModelVersion> findAllByStatus(String status);
}
