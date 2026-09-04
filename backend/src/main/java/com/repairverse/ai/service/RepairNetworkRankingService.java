package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairNetworkIntelligenceDto.*;
import com.repairverse.ai.entity.RepairShopQualitySnapshot;
import com.repairverse.ai.repository.RepairShopQualitySnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Phase 28 — Network Leaderboard & Ranking Service.
 *
 * Ranking categories:
 *   BEST_OVERALL, MOST_TRUSTED, HIGHEST_QUALITY,
 *   BEST_VALUE, FASTEST, MOST_RELIABLE, MOST_SUSTAINABLE
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepairNetworkRankingService {

    private final RepairShopQualitySnapshotRepository snapshotRepository;

    public List<NetworkLeaderboardResponse> getLeaderboard(String rankingType, int limit) {
        List<RepairShopQualitySnapshot> snapshots = switch (rankingType.toUpperCase()) {
            case "MOST_TRUSTED"     -> snapshotRepository.findMostTrustedShops(0, limit);
            case "BEST_VALUE"       -> snapshotRepository.findBestValueShops(limit);
            case "FASTEST"          -> snapshotRepository.findFastestShops(limit);
            case "HIGHEST_QUALITY"  -> snapshotRepository.findTopRankedShops(0, limit);
            case "MOST_RELIABLE"    -> snapshotRepository.findTopRankedShops(0, limit).stream()
                .sorted(Comparator.comparingInt(RepairShopQualitySnapshot::getReliabilityScore).reversed())
                .limit(limit).collect(Collectors.toList());
            default                 -> snapshotRepository.findTopRankedShops(0, limit); // BEST_OVERALL
        };

        // Fallback heuristic leaderboard when no snapshots exist
        if (snapshots.isEmpty()) {
            return buildHeuristicLeaderboard(rankingType, limit);
        }

        AtomicInteger rank = new AtomicInteger(1);
        return snapshots.stream().limit(limit).map(s -> {
            String badge = resolveBadge(rankingType, rank.get());
            double successRate = s.getTotalRepairs() == 0 ? 0.91
                : (double) s.getSuccessfulRepairs() / s.getTotalRepairs();
            return new NetworkLeaderboardResponse(
                rank.getAndIncrement(),
                s.getRepairShopId(),
                "Provider " + s.getRepairShopId().substring(0, Math.min(6, s.getRepairShopId().length())),
                s.getOverallQualityScore(),
                s.getTrustScore(),
                successRate,
                s.getAverageRating(),
                "STABLE",
                badge
            );
        }).collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String resolveBadge(String rankingType, int rank) {
        String prefix = rank == 1 ? "🏆 #1 " : rank == 2 ? "🥈 #" + rank + " " : "🥉 #" + rank + " ";
        return prefix + switch (rankingType.toUpperCase()) {
            case "MOST_TRUSTED"    -> "Most Trusted";
            case "BEST_VALUE"      -> "Best Value";
            case "FASTEST"         -> "Fastest Service";
            case "HIGHEST_QUALITY" -> "Highest Quality";
            case "MOST_RELIABLE"   -> "Most Reliable";
            case "MOST_SUSTAINABLE"-> "Most Sustainable";
            default                -> "Best Overall";
        };
    }

    private List<NetworkLeaderboardResponse> buildHeuristicLeaderboard(String rankingType, int limit) {
        // Realistic heuristic demo leaderboard
        record DemoShop(String id, String name, int quality, int trust, double success, double rating) {}
        List<DemoShop> shops = List.of(
            new DemoShop("shop-elite-01", "PrecisionFix Pro",      95, 94, 0.97, 4.9),
            new DemoShop("shop-elite-02", "TrueRepair Certified",  91, 92, 0.95, 4.8),
            new DemoShop("shop-excel-01", "QuickMend Station",     87, 85, 0.92, 4.7),
            new DemoShop("shop-excel-02", "EcoRepair Hub",         84, 88, 0.90, 4.7),
            new DemoShop("shop-trust-01", "CityTech Repair",       79, 80, 0.87, 4.5),
            new DemoShop("shop-trust-02", "Reliable Fix Co.",      76, 78, 0.85, 4.4)
        );

        return shops.stream().limit(limit).map(s -> new NetworkLeaderboardResponse(
            shops.indexOf(s) + 1,
            s.id(), s.name(), s.quality(), s.trust(), s.success(), s.rating(),
            "STABLE", resolveBadge(rankingType, shops.indexOf(s) + 1)
        )).collect(Collectors.toList());
    }

    public NetworkHealthResponse getNetworkHealth(int totalShops,
                                                   int eliteShops, int excellentShops,
                                                   int trustedShops, int standardShops,
                                                   int needsImprovementShops,
                                                   long openAnomalies, long criticalAnomalies) {
        double platformQuality = computePlatformScore(eliteShops, excellentShops,
            trustedShops, standardShops, needsImprovementShops, totalShops);
        String status = criticalAnomalies > 0 ? "DEGRADED"
            : openAnomalies > 5 ? "MONITORING" : "HEALTHY";

        return new NetworkHealthResponse(
            status, totalShops, eliteShops, excellentShops,
            trustedShops, standardShops, needsImprovementShops,
            openAnomalies, criticalAnomalies,
            82.0, platformQuality, 0.91
        );
    }

    private double computePlatformScore(int elite, int excellent, int trusted,
                                         int standard, int needsImprovement, int total) {
        if (total == 0) return 80.0;
        return (elite * 95.0 + excellent * 85.0 + trusted * 75.0
                + standard * 60.0 + needsImprovement * 40.0) / total;
    }
}
