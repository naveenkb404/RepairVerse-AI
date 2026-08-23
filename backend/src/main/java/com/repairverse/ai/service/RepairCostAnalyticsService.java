package com.repairverse.ai.service;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
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
 * Calculates historical and projected repair cost analytics for a user.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepairCostAnalyticsService {

    private final RepairHistoryRepository repairHistoryRepository;
    private final DeviceRepository deviceRepository;

    @Transactional(readOnly = true)
    public RepairCostAnalytics getAnalyticsForUser(String userId) {
        List<RepairHistory> repairs = repairHistoryRepository.findByUserIdOrderByRepairDateDesc(userId);

        if (repairs.isEmpty()) {
            return buildEmptyAnalytics(false);
        }

        double totalSpent   = repairs.stream().mapToDouble(r -> r.getTotalCost()  != null ? r.getTotalCost()  : 0).sum();
        double totalParts   = repairs.stream().mapToDouble(r -> r.getPartsCost()  != null ? r.getPartsCost()  : 0).sum();
        double totalLabor   = repairs.stream().mapToDouble(r -> r.getLaborCost()  != null ? r.getLaborCost()  : 0).sum();
        double avgCost      = repairs.isEmpty() ? 0 : totalSpent / repairs.size();
        double projectedNext= avgCost * 1.08; // 8% inflation adjustment
        double preventiveSavings = projectedNext * 0.35;

        List<MonthlyCostEntry> monthly = buildMonthlyCostTrend(repairs);
        List<CategoryCostEntry> byCategory = buildCategoryBreakdown(repairs, userId);

        return new RepairCostAnalytics(
                Math.round(totalSpent * 100.0) / 100.0,
                Math.round(avgCost * 100.0) / 100.0,
                Math.round(totalParts * 100.0) / 100.0,
                Math.round(totalLabor * 100.0) / 100.0,
                Math.round(projectedNext * 100.0) / 100.0,
                Math.round(preventiveSavings * 100.0) / 100.0,
                monthly,
                byCategory,
                false
        );
    }

    public RepairCostAnalytics getDemoAnalytics() {
        return new RepairCostAnalytics(
                1240.50,
                155.06,
                620.25,
                620.25,
                167.46,
                58.61,
                List.of(
                        new MonthlyCostEntry("Feb 2026", 180.0, 90.0, 90.0),
                        new MonthlyCostEntry("Mar 2026", 220.0, 110.0, 110.0),
                        new MonthlyCostEntry("Apr 2026", 95.0, 47.5, 47.5),
                        new MonthlyCostEntry("May 2026", 310.0, 155.0, 155.0),
                        new MonthlyCostEntry("Jun 2026", 140.0, 70.0, 70.0),
                        new MonthlyCostEntry("Jul 2026", 295.50, 147.75, 147.75)
                ),
                List.of(
                        new CategoryCostEntry("Smartphone", 520.0, 3, 173.33),
                        new CategoryCostEntry("Laptop", 420.0, 2, 210.0),
                        new CategoryCostEntry("Tablet", 180.50, 1, 180.5),
                        new CategoryCostEntry("Gaming Console", 120.0, 2, 60.0)
                ),
                true
        );
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private List<MonthlyCostEntry> buildMonthlyCostTrend(List<RepairHistory> repairs) {
        // Group by YYY-MM
        Map<String, List<RepairHistory>> grouped = repairs.stream()
                .filter(r -> r.getRepairDate() != null && !r.getRepairDate().isBlank())
                .collect(Collectors.groupingBy(r -> {
                    try {
                        // repairDate may be "YYYY-MM-DD"
                        return r.getRepairDate().substring(0, 7);
                    } catch (Exception e) {
                        return "unknown";
                    }
                }));

        // Last 6 months
        DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM yyyy");
        List<MonthlyCostEntry> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String key = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            String label = month.format(monthFmt);
            List<RepairHistory> monthRepairs = grouped.getOrDefault(key, List.of());
            double total  = monthRepairs.stream().mapToDouble(r -> r.getTotalCost()  != null ? r.getTotalCost()  : 0).sum();
            double parts  = monthRepairs.stream().mapToDouble(r -> r.getPartsCost()  != null ? r.getPartsCost()  : 0).sum();
            double labor  = monthRepairs.stream().mapToDouble(r -> r.getLaborCost()  != null ? r.getLaborCost()  : 0).sum();
            result.add(new MonthlyCostEntry(label, total, parts, labor));
        }
        return result;
    }

    private List<CategoryCostEntry> buildCategoryBreakdown(List<RepairHistory> repairs, String userId) {
        // Enrich with device category via device lookup
        Map<String, String> deviceCategoryMap = new HashMap<>();
        deviceRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .forEach(d -> deviceCategoryMap.put(d.getId(), d.getCategory()));

        Map<String, List<RepairHistory>> grouped = repairs.stream()
                .collect(Collectors.groupingBy(r -> {
                    String cat = deviceCategoryMap.get(r.getDeviceId());
                    return cat != null ? cat : "Other";
                }));

        return grouped.entrySet().stream().map(e -> {
            double total = e.getValue().stream().mapToDouble(r -> r.getTotalCost() != null ? r.getTotalCost() : 0).sum();
            long count = e.getValue().size();
            return new CategoryCostEntry(e.getKey(), Math.round(total * 100.0) / 100.0,
                    count, count > 0 ? Math.round(total / count * 100.0) / 100.0 : 0);
        }).sorted(Comparator.comparingDouble(CategoryCostEntry::totalCost).reversed())
                .collect(Collectors.toList());
    }

    private RepairCostAnalytics buildEmptyAnalytics(boolean demo) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy");
        List<MonthlyCostEntry> emptyMonths = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            emptyMonths.add(new MonthlyCostEntry(now.minusMonths(i).format(fmt), 0, 0, 0));
        }
        return new RepairCostAnalytics(0, 0, 0, 0, 0, 0, emptyMonths, List.of(), demo);
    }
}
