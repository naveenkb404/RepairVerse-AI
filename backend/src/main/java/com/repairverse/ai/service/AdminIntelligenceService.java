package com.repairverse.ai.service;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.entity.DevicePrediction;
import com.repairverse.ai.entity.User;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Admin-facing intelligence service.
 * Aggregates platform-wide predictive health data for the admin dashboard.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminIntelligenceService {

    private final DevicePredictionRepository devicePredictionRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminIntelligenceSummary getSummary() {
        long totalPredictions = devicePredictionRepository.count();
        long critical  = devicePredictionRepository.countByRiskLevel("CRITICAL");
        long high      = devicePredictionRepository.countByRiskLevel("HIGH");

        Double avgScore = devicePredictionRepository.findPlatformAveragePredictionScore();
        double platformAvg = avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 82.0;

        Double totalFailCost  = devicePredictionRepository.sumTotalEstimatedRepairCost();
        Double totalPrevSave  = devicePredictionRepository.sumTotalPreventiveSavings();

        // CO₂ impact: sum across all predictions
        List<DevicePrediction> allPredictions = devicePredictionRepository.findAll();
        double totalCo2 = allPredictions.stream()
                .mapToDouble(dp -> dp.getCo2SavingsKg() != null ? dp.getCo2SavingsKg() : 0)
                .sum();

        // Top failing categories
        Map<String, String> deviceCategories = new HashMap<>();
        deviceRepository.findAll().forEach(d -> deviceCategories.put(d.getId(), d.getCategory()));

        Map<String, List<DevicePrediction>> byCategory = allPredictions.stream()
                .collect(Collectors.groupingBy(dp -> deviceCategories.getOrDefault(dp.getDeviceId(), "Other")));

        List<TopFailingCategory> topCategories = byCategory.entrySet().stream().map(e -> {
            long total  = e.getValue().size();
            long atRisk = e.getValue().stream()
                    .filter(dp -> dp.getRiskLevel().equals("CRITICAL") || dp.getRiskLevel().equals("HIGH"))
                    .count();
            double pct = total > 0 ? Math.round((atRisk * 100.0 / total) * 10.0) / 10.0 : 0;
            String faultType = e.getValue().stream()
                    .filter(dp -> dp.getPrimaryFaultType() != null)
                    .findFirst()
                    .map(DevicePrediction::getPrimaryFaultType)
                    .orElse("General Wear");
            return new TopFailingCategory(e.getKey(), total, atRisk, pct, faultType);
        }).sorted(Comparator.comparingDouble(TopFailingCategory::riskPercentage).reversed())
                .limit(5)
                .collect(Collectors.toList());

        // Recent high-risk devices
        Map<String, String> userEmails = new HashMap<>();
        userRepository.findAll().forEach(u -> userEmails.put(u.getId(), u.getEmail()));

        Map<String, String> deviceNames = new HashMap<>();
        deviceRepository.findAll().forEach(d -> deviceNames.put(d.getId(), d.getDeviceName()));

        List<RecentHighRiskDevice> recentHighRisk = devicePredictionRepository
                .findHighAndCriticalRiskDevices()
                .stream()
                .limit(10)
                .map(dp -> new RecentHighRiskDevice(
                        dp.getDeviceId(),
                        deviceNames.getOrDefault(dp.getDeviceId(), "Unknown Device"),
                        dp.getUserId(),
                        userEmails.getOrDefault(dp.getUserId(), "unknown@example.com"),
                        dp.getRiskLevel(),
                        dp.getPredictionScore(),
                        dp.getPrimaryFaultType() != null ? dp.getPrimaryFaultType() : "General Wear",
                        dp.getEvaluatedAt() != null ? dp.getEvaluatedAt().toString() : LocalDateTime.now().toString()
                ))
                .collect(Collectors.toList());

        boolean isDemo = totalPredictions == 0;

        return new AdminIntelligenceSummary(
                isDemo ? 1450 : totalPredictions,
                isDemo ? 23  : critical,
                isDemo ? 87  : high,
                isDemo ? 76.4 : platformAvg,
                isDemo ? 48500.0 : (totalFailCost != null ? Math.round(totalFailCost * 100.0) / 100.0 : 0),
                isDemo ? 19400.0 : (totalPrevSave != null ? Math.round(totalPrevSave * 100.0) / 100.0 : 0),
                isDemo ? 18420.0 : Math.round(totalCo2 * 100.0) / 100.0,
                isDemo ? buildDemoTopCategories() : topCategories,
                isDemo ? buildDemoHighRiskDevices() : recentHighRisk,
                isDemo
        );
    }

    @Transactional(readOnly = true)
    public PredictiveFleetOverview getPlatformFleetOverview() {
        long total    = devicePredictionRepository.count();
        long critical = devicePredictionRepository.countByRiskLevel("CRITICAL");
        long high     = devicePredictionRepository.countByRiskLevel("HIGH");
        long medium   = devicePredictionRepository.countByRiskLevel("MEDIUM");
        long low      = devicePredictionRepository.countByRiskLevel("LOW");
        long healthy  = devicePredictionRepository.countByRiskLevel("HEALTHY");

        Double avgScore = devicePredictionRepository.findPlatformAveragePredictionScore();
        Double failCost = devicePredictionRepository.sumTotalEstimatedRepairCost();
        Double prevSave = devicePredictionRepository.sumTotalPreventiveSavings();

        List<DevicePrediction> all = devicePredictionRepository.findAll();
        double co2Total = all.stream().mapToDouble(dp -> dp.getCo2SavingsKg() != null ? dp.getCo2SavingsKg() : 0).sum();

        boolean isDemo = total == 0;

        List<RiskDistributionEntry> dist = buildDistribution(
                isDemo ? 1450 : total,
                isDemo ? 23 : critical,
                isDemo ? 87 : high,
                isDemo ? 312 : medium,
                isDemo ? 680 : low,
                isDemo ? 348 : healthy
        );

        return new PredictiveFleetOverview(
                isDemo ? 1450 : total,
                isDemo ? 23 : critical,
                isDemo ? 87 : high,
                isDemo ? 312 : medium,
                isDemo ? 680 : low,
                isDemo ? 348 : healthy,
                isDemo ? 76.4 : (avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0),
                isDemo ? 48500.0 : (failCost != null ? Math.round(failCost * 100.0) / 100.0 : 0),
                isDemo ? 19400.0 : (prevSave != null ? Math.round(prevSave * 100.0) / 100.0 : 0),
                isDemo ? 18420.0 : Math.round(co2Total * 100.0) / 100.0,
                dist,
                isDemo
        );
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private List<RiskDistributionEntry> buildDistribution(
            long total, long critical, long high, long medium, long low, long healthy) {
        return List.of(
                new RiskDistributionEntry("CRITICAL", critical, pct(critical, total)),
                new RiskDistributionEntry("HIGH",     high,     pct(high, total)),
                new RiskDistributionEntry("MEDIUM",   medium,   pct(medium, total)),
                new RiskDistributionEntry("LOW",      low,      pct(low, total)),
                new RiskDistributionEntry("HEALTHY",  healthy,  pct(healthy, total))
        );
    }

    private double pct(long part, long total) {
        return total > 0 ? Math.round(part * 1000.0 / total) / 10.0 : 0;
    }

    private List<TopFailingCategory> buildDemoTopCategories() {
        return List.of(
                new TopFailingCategory("Smartphone",     480, 87, 18.1, "Battery Degradation"),
                new TopFailingCategory("Laptop",         320, 52, 16.3, "Thermal Paste Degradation"),
                new TopFailingCategory("Gaming Console", 210, 29, 13.8, "Overheating"),
                new TopFailingCategory("Tablet",         180, 18, 10.0, "Charging Port Failure"),
                new TopFailingCategory("Smartwatch",     90,  7,  7.8,  "Battery Swelling")
        );
    }

    private List<RecentHighRiskDevice> buildDemoHighRiskDevices() {
        String now = LocalDateTime.now().toString();
        return List.of(
                new RecentHighRiskDevice("dev-demo-001", "Samsung Galaxy S21",    "usr-demo-001", "user1@demo.com", "CRITICAL", 28, "Battery Degradation",          now),
                new RecentHighRiskDevice("dev-demo-002", "Dell XPS 15 (2021)",    "usr-demo-002", "user2@demo.com", "CRITICAL", 31, "Thermal Paste Degradation",    now),
                new RecentHighRiskDevice("dev-demo-003", "iPhone 12 Pro",         "usr-demo-003", "user3@demo.com", "HIGH",     42, "Charging Port Failure",        now),
                new RecentHighRiskDevice("dev-demo-004", "PlayStation 5",         "usr-demo-004", "user4@demo.com", "HIGH",     48, "Overheating",                  now),
                new RecentHighRiskDevice("dev-demo-005", "Surface Pro 8",         "usr-demo-005", "user5@demo.com", "HIGH",     51, "SSD Performance Degradation",  now)
        );
    }
}
