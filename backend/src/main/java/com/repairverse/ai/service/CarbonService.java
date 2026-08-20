package com.repairverse.ai.service;

import com.repairverse.ai.dto.CarbonDto.*;
import com.repairverse.ai.entity.AIRecommendation;
import com.repairverse.ai.entity.CarbonImpact;
import com.repairverse.ai.entity.DiagnosisReport;
import com.repairverse.ai.repository.AIRecommendationRepository;
import com.repairverse.ai.repository.CarbonImpactRepository;
import com.repairverse.ai.repository.DiagnosisReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonService {

    private final CarbonImpactRepository carbonImpactRepository;
    private final DiagnosisReportRepository diagnosisReportRepository;
    private final AIRecommendationRepository recommendationRepository;

    private static final Map<String, Double> EWASTE_WEIGHT_KG = Map.of(
            "smartphone", 0.24,
            "laptop", 2.10,
            "tablet", 0.45,
            "gaming console", 3.20,
            "smartwatch", 0.08,
            "audio device", 0.25,
            "other", 0.50
    );

    @Transactional(readOnly = true)
    public CarbonDashboardResponse getCarbonDashboard(String userId) {
        // Fetch user diagnosis reports
        List<DiagnosisReport> userReports = diagnosisReportRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Fetch user carbon impact record if stored directly
        Optional<CarbonImpact> carbonEntityOpt = carbonImpactRepository.findByUserId(userId);

        if (userReports.isEmpty() && carbonEntityOpt.isEmpty()) {
            log.info("No live carbon records for user '{}'. Returning reference sample carbon dashboard.", userId);
            return new CarbonDashboardResponse(true, "Sample carbon data loaded (Demo/Offline Mode)", getSampleDashboardData(true));
        }

        double totalCo2 = 0.0;
        double totalEwaste = 0.0;
        double totalMoney = 0.0;
        int repairCount = 0;

        List<CarbonRepairActivity> activities = new ArrayList<>();

        for (DiagnosisReport report : userReports) {
            Optional<AIRecommendation> recOpt = recommendationRepository.findByDiagnosisId(report.getId());
            if (recOpt.isPresent()) {
                AIRecommendation rec = recOpt.get();
                totalCo2 += rec.getCarbonSaved();
                totalMoney += rec.getMoneySaved();
                repairCount++;

                String category = report.getDeviceCategory() != null ? report.getDeviceCategory().toLowerCase() : "other";
                double ewaste = EWASTE_WEIGHT_KG.getOrDefault(category, 0.50);
                totalEwaste += ewaste;

                String deviceName = (report.getBrand() != null ? report.getBrand() + " " : "") +
                        (report.getModel() != null ? report.getModel() : "Device");
                String dateStr = report.getCreatedAt() != null ?
                        report.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE) : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

                activities.add(new CarbonRepairActivity(
                        "act-" + report.getId().substring(0, Math.min(8, report.getId().length())),
                        deviceName,
                        report.getProbableIssue() != null ? report.getProbableIssue() : "Device Repair",
                        dateStr,
                        Math.round(rec.getCarbonSaved() * 10.0) / 10.0,
                        Math.round(ewaste * 100.0) / 100.0,
                        Math.round(rec.getMoneySaved() * 100.0) / 100.0
                ));
            }
        }

        // Merge with carbon_impact record if present
        if (carbonEntityOpt.isPresent()) {
            CarbonImpact entity = carbonEntityOpt.get();
            if (totalCo2 == 0.0) totalCo2 = entity.getCo2Saved();
            if (totalEwaste == 0.0) totalEwaste = entity.getEwasteReduced();
            if (totalMoney == 0.0) totalMoney = entity.getMoneySaved();
            if (repairCount == 0) repairCount = entity.getRepairCount();
        }

        // Format rounded values
        totalCo2 = Math.round(totalCo2 * 10.0) / 10.0;
        totalEwaste = Math.round(totalEwaste * 100.0) / 100.0;
        totalMoney = Math.round(totalMoney * 100.0) / 100.0;

        // Calculate Sustainability Score (0 - 100)
        int sustainabilityScore = Math.min(100, Math.max(30, 50 + (int) (totalCo2 * 0.4) + (repairCount * 5)));

        // Generate 6-month trend breakdown
        List<CarbonTrendPoint> trend = generateTrend(totalCo2, totalMoney);

        CarbonImpactData impactData = new CarbonImpactData(totalCo2, totalEwaste, totalMoney, repairCount);
        CarbonDashboardData data = new CarbonDashboardData(impactData, trend, activities, sustainabilityScore, false);

        return new CarbonDashboardResponse(true, "Live carbon impact data loaded", data);
    }

    private List<CarbonTrendPoint> generateTrend(double currentCo2, double currentMoney) {
        String[] months = {"Sep", "Oct", "Nov", "Dec", "Jan", "Feb"};
        List<CarbonTrendPoint> trend = new ArrayList<>();

        for (int i = 0; i < months.length; i++) {
            double factor = (i + 1.0) / months.length;
            trend.add(new CarbonTrendPoint(
                    months[i],
                    Math.round(currentCo2 * factor * 10.0) / 10.0,
                    Math.round(currentMoney * factor * 10.0) / 10.0
            ));
        }

        return trend;
    }

    public static CarbonDashboardData getSampleDashboardData(boolean isDemo) {
        CarbonImpactData impact = new CarbonImpactData(142.8, 4.85, 1250.0, 8);
        List<CarbonTrendPoint> trend = List.of(
                new CarbonTrendPoint("Sep", 12.4, 120.0),
                new CarbonTrendPoint("Oct", 28.1, 250.0),
                new CarbonTrendPoint("Nov", 49.3, 480.0),
                new CarbonTrendPoint("Dec", 78.6, 710.0),
                new CarbonTrendPoint("Jan", 110.2, 990.0),
                new CarbonTrendPoint("Feb", 142.8, 1250.0)
        );

        List<CarbonRepairActivity> activity = List.of(
                new CarbonRepairActivity("act-1", "iPhone 13 Pro", "OLED Screen & Battery Replacement", "2026-02-10", 58.2, 0.24, 680.0),
                new CarbonRepairActivity("act-2", "MacBook Pro 16\" (M1)", "Logic Board Capacitor Micro-soldering", "2026-01-18", 64.5, 2.10, 450.0),
                new CarbonRepairActivity("act-3", "Sony WH-1000XM4", "ANC Hinge & Left Driver Repair", "2025-12-04", 20.1, 0.25, 120.0)
        );

        return new CarbonDashboardData(impact, trend, activity, 88, isDemo);
    }
}
