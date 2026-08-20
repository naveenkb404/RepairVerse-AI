package com.repairverse.ai.service;

import com.repairverse.ai.dto.CarbonDto.CarbonDashboardResponse;
import com.repairverse.ai.entity.AIRecommendation;
import com.repairverse.ai.entity.CarbonImpact;
import com.repairverse.ai.entity.DiagnosisReport;
import com.repairverse.ai.repository.AIRecommendationRepository;
import com.repairverse.ai.repository.CarbonImpactRepository;
import com.repairverse.ai.repository.DiagnosisReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarbonServiceTest {

    @Mock
    private CarbonImpactRepository carbonImpactRepository;

    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @Mock
    private AIRecommendationRepository recommendationRepository;

    @InjectMocks
    private CarbonService carbonService;

    private String userId;

    @BeforeEach
    void setUp() {
        userId = "usr-123";
    }

    @Test
    @DisplayName("Should return demo data when user has no diagnosis or carbon records")
    void getCarbonDashboard_DemoMode() {
        when(diagnosisReportRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Collections.emptyList());
        when(carbonImpactRepository.findByUserId(userId)).thenReturn(Optional.empty());

        CarbonDashboardResponse response = carbonService.getCarbonDashboard(userId);

        assertThat(response.success()).isTrue();
        assertThat(response.data().isDemoData()).isTrue();
        assertThat(response.data().impact().co2Saved()).isGreaterThan(0);
        assertThat(response.data().sustainabilityScore()).isEqualTo(88);
    }

    @Test
    @DisplayName("Should calculate carbon impact deterministically from user diagnosis reports")
    void getCarbonDashboard_LiveUserReports() {
        DiagnosisReport report = DiagnosisReport.builder()
                .id("diag-1")
                .userId(userId)
                .deviceCategory("smartphone")
                .brand("Apple")
                .model("iPhone 13")
                .probableIssue("Screen Shattered")
                .createdAt(LocalDateTime.now())
                .build();

        AIRecommendation rec = AIRecommendation.builder()
                .id("rec-1")
                .diagnosisId("diag-1")
                .carbonSaved(24.5)
                .moneySaved(150.0)
                .action("REPAIR")
                .build();

        when(diagnosisReportRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(report));
        when(recommendationRepository.findByDiagnosisId("diag-1")).thenReturn(Optional.of(rec));
        when(carbonImpactRepository.findByUserId(userId)).thenReturn(Optional.empty());

        CarbonDashboardResponse response = carbonService.getCarbonDashboard(userId);

        assertThat(response.success()).isTrue();
        assertThat(response.data().isDemoData()).isFalse();
        assertThat(response.data().impact().co2Saved()).isEqualTo(24.5);
        assertThat(response.data().impact().moneySaved()).isEqualTo(150.0);
        assertThat(response.data().impact().repairCount()).isEqualTo(1);
        assertThat(response.data().recentActivity()).hasSize(1);
    }
}
