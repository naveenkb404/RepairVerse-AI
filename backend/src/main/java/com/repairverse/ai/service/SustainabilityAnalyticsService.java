package com.repairverse.ai.service;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.RepairHistory;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.RepairHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Calculates sustainability & environmental impact metrics for a user's device history.
 * CO₂ avoided, e-waste reduced, money saved — aggregated monthly and per device.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SustainabilityAnalyticsService {

    private final RepairHistoryRepository repairHistoryRepository;
    private final DeviceRepository deviceRepository;

    /** Average CO₂ per new electronic device manufactured (kg) — industry estimate */
    private static final double CO2_PER_NEW_DEVICE_KG = 80.0;
    /** Average e-waste per device if discarded (kg) */
    private static final double EWASTE_PER_DEVICE_KG = 1.2;
    /** CO₂ per km driven by an average petrol car (kg) */
    private static final double CO2_PER_CAR_KM = 0.12;
    /** CO₂ absorbed per tree per year (kg) */
    private static final double CO2_PER_TREE_PER_YEAR = 22.0;

    @Transactional(readOnly = true)
    public SustainabilityAnalytics getAnalyticsForUser(String userId) {
        List<RepairHistory> repairs = repairHistoryRepository.findByUserIdOrderByRepairDateDesc(userId);
        List<Device> devices = deviceRepository.findByUserIdOrderByCreatedAtDesc(userId);

        if (repairs.isEmpty()) {
            return buildEmptyAnalytics(false);
        }

        double totalCo2    = repairs.stream().mapToDouble(r -> r.getCo2SavedKg()      != null ? r.getCo2SavedKg()      : 0).sum();
        double totalEwaste = repairs.stream().mapToDouble(r -> r.getEwasteReducedKg() != null ? r.getEwasteReducedKg() : 0).sum();
        double totalMoney  = repairs.stream().mapToDouble(r -> r.getMoneySaved()       != null ? r.getMoneySaved()       : 0).sum();
        int extendedDevices = (int) repairs.stream()
                .filter(r -> r.getMoneySaved() != null && r.getMoneySaved() > 50)
                .map(RepairHistory::getDeviceId).distinct().count();

        double co2Trees  = totalCo2 / CO2_PER_TREE_PER_YEAR;
        double co2CarKm  = totalCo2 / CO2_PER_CAR_KM;

        List<MonthlyImpactEntry> monthly = buildMonthlyImpact(repairs);
        List<DeviceImpactEntry> topDevices = buildTopDevices(repairs, devices);

        return new SustainabilityAnalytics(
                round2(totalCo2),
                round2(totalEwaste),
                round2(totalMoney),
                extendedDevices,
                round2(co2Trees),
                round2(co2CarKm),
                monthly,
                topDevices,
                false
        );
    }

    public SustainabilityAnalytics getDemoAnalytics() {
        return new SustainabilityAnalytics(
                127.4,
                8.4,
                2340.0,
                4,
                5.79,
                1061.67,
                List.of(
                        new MonthlyImpactEntry("Feb 2026", 18.2, 1.2, 310.0),
                        new MonthlyImpactEntry("Mar 2026", 22.5, 1.4, 420.0),
                        new MonthlyImpactEntry("Apr 2026", 14.1, 0.8, 185.0),
                        new MonthlyImpactEntry("May 2026", 31.0, 1.8, 620.0),
                        new MonthlyImpactEntry("Jun 2026", 19.6, 1.2, 350.0),
                        new MonthlyImpactEntry("Jul 2026", 22.0, 2.0, 455.0)
                ),
                List.of(
                        new DeviceImpactEntry("dev_sample_2", "Work MacBook Pro 16", 52.1, 3.5, 1100.0, 3),
                        new DeviceImpactEntry("dev_sample_1", "Personal iPhone 14 Pro", 38.8, 2.4, 720.0, 4),
                        new DeviceImpactEntry("dev_sample_3", "Living Room Gaming Console", 21.0, 1.6, 350.0, 2),
                        new DeviceImpactEntry("dev_sample_4", "Study iPad Air", 15.5, 0.9, 170.0, 1)
                ),
                true
        );
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private List<MonthlyImpactEntry> buildMonthlyImpact(List<RepairHistory> repairs) {
        Map<String, List<RepairHistory>> grouped = repairs.stream()
                .filter(r -> r.getRepairDate() != null && r.getRepairDate().length() >= 7)
                .collect(Collectors.groupingBy(r -> r.getRepairDate().substring(0, 7)));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy");
        List<MonthlyImpactEntry> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String key = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String label = month.format(fmt);
            List<RepairHistory> bucket = grouped.getOrDefault(key, List.of());
            double co2   = bucket.stream().mapToDouble(r -> r.getCo2SavedKg()      != null ? r.getCo2SavedKg()      : 0).sum();
            double ew    = bucket.stream().mapToDouble(r -> r.getEwasteReducedKg() != null ? r.getEwasteReducedKg() : 0).sum();
            double money = bucket.stream().mapToDouble(r -> r.getMoneySaved()       != null ? r.getMoneySaved()       : 0).sum();
            result.add(new MonthlyImpactEntry(label, round2(co2), round2(ew), round2(money)));
        }
        return result;
    }

    private List<DeviceImpactEntry> buildTopDevices(List<RepairHistory> repairs, List<Device> devices) {
        Map<String, String> deviceNames = devices.stream()
                .collect(Collectors.toMap(Device::getId, Device::getDeviceName));

        Map<String, List<RepairHistory>> byDevice = repairs.stream()
                .collect(Collectors.groupingBy(RepairHistory::getDeviceId));

        return byDevice.entrySet().stream().map(e -> {
            double co2   = e.getValue().stream().mapToDouble(r -> r.getCo2SavedKg()      != null ? r.getCo2SavedKg()      : 0).sum();
            double ew    = e.getValue().stream().mapToDouble(r -> r.getEwasteReducedKg() != null ? r.getEwasteReducedKg() : 0).sum();
            double money = e.getValue().stream().mapToDouble(r -> r.getMoneySaved()       != null ? r.getMoneySaved()       : 0).sum();
            String name  = deviceNames.getOrDefault(e.getKey(), "Unknown Device");
            return new DeviceImpactEntry(e.getKey(), name, round2(co2), round2(ew), round2(money), e.getValue().size());
        }).sorted(Comparator.comparingDouble(DeviceImpactEntry::co2SavedKg).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }

    private SustainabilityAnalytics buildEmptyAnalytics(boolean demo) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy");
        List<MonthlyImpactEntry> emptyMonths = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            emptyMonths.add(new MonthlyImpactEntry(now.minusMonths(i).format(fmt), 0, 0, 0));
        }
        return new SustainabilityAnalytics(0, 0, 0, 0, 0, 0, emptyMonths, List.of(), demo);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
