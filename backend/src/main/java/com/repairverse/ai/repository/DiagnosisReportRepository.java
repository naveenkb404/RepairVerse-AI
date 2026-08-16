package com.repairverse.ai.repository;

import com.repairverse.ai.entity.DiagnosisReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisReportRepository extends JpaRepository<DiagnosisReport, String> {
    List<DiagnosisReport> findByUserIdOrderByCreatedAtDesc(String userId);
    List<DiagnosisReport> findByDeviceIdOrderByCreatedAtDesc(String deviceId);
}
