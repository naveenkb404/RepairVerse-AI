package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairNetworkIntelligenceDto.*;
import com.repairverse.ai.repository.RepairShopQualitySnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairNetworkRankingServiceTest {

    @Mock RepairShopQualitySnapshotRepository snapshotRepository;
    @InjectMocks RepairNetworkRankingService service;

    @Test
    void getLeaderboard_noSnapshots_returnsHeuristicData() {
        when(snapshotRepository.findTopRankedShops(anyInt(), anyInt())).thenReturn(List.of());

        List<NetworkLeaderboardResponse> leaderboard = service.getLeaderboard("BEST_OVERALL", 5);

        assertThat(leaderboard).isNotEmpty();
        assertThat(leaderboard.get(0).rank()).isEqualTo(1);
        assertThat(leaderboard.get(0).qualityScore()).isGreaterThan(0);
        assertThat(leaderboard.get(0).badge()).contains("#1");
    }

    @Test
    void getNetworkHealth_returnsCorrectStatus() {
        NetworkHealthResponse health = service.getNetworkHealth(
            42, 5, 12, 18, 6, 1, 2L, 0L
        );
        assertThat(health.overallStatus()).isEqualTo("HEALTHY");
        assertThat(health.totalShops()).isEqualTo(42);
        assertThat(health.eliteShops()).isEqualTo(5);
    }

    @Test
    void getNetworkHealth_withCriticalAnomalies_returnsDegraded() {
        NetworkHealthResponse health = service.getNetworkHealth(
            10, 1, 2, 4, 2, 1, 5L, 2L
        );
        assertThat(health.overallStatus()).isEqualTo("DEGRADED");
    }
}
