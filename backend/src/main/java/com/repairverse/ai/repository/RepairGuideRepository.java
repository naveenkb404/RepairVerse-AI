package com.repairverse.ai.repository;

import com.repairverse.ai.entity.RepairGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairGuideRepository extends JpaRepository<RepairGuide, String> {

    List<RepairGuide> findByCategoryIgnoreCaseOrderByCreatedAtDesc(String category);

    List<RepairGuide> findByDifficultyIgnoreCaseOrderByCreatedAtDesc(String difficulty);

    List<RepairGuide> findAllByOrderByCreatedAtDesc();

    Optional<RepairGuide> findFirstByTitleContainingIgnoreCase(String keyword);
}
