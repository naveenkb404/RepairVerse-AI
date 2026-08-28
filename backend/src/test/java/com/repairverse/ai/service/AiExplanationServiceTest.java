package com.repairverse.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.config.AppProperties;
import com.repairverse.ai.dto.AiExplanationDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiExplanationServiceTest {

    @Mock
    private AppProperties appProperties;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceHealthRepository deviceHealthRepository;

    @Mock
    private DevicePredictionRepository devicePredictionRepository;

    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @Mock
    private AIRecommendationRepository aiRecommendationRepository;

    @Mock
    private RepairHistoryRepository repairHistoryRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AiExplanationService aiExplanationService;

    private Device testDevice;
    private DeviceHealth testHealth;
    private DevicePrediction testPrediction;
    private DiagnosisReport testDiagnosis;
    private AIRecommendation testRec;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("iPhone 14")
                .category("Smartphone")
                .brand("Apple")
                .model("A2882")
                .purchaseDate("2023-01-15")
                .build();

        testHealth = DeviceHealth.builder()
                .id("dh-1")
                .deviceId("dev-1")
                .healthScore(82)
                .batteryHealth(88)
                .build();

        testPrediction = DevicePrediction.builder()
                .id("dp-1")
                .deviceId("dev-1")
                .predictionScore(82)
                .riskLevel("LOW")
                .primaryFaultType("Battery Degradation")
                .build();

        testDiagnosis = DiagnosisReport.builder()
                .id("diag-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .deviceCategory("Smartphone")
                .brand("Apple")
                .model("iPhone 14")
                .probableIssue("Display Glitch")
                .confidenceScore(89)
                .repairDifficulty("Moderate")
                .repairCost(90.0)
                .observations("Display connector ribbon cable loose")
                .build();

        testRec = AIRecommendation.builder()
                .id("rec-1")
                .diagnosisId("diag-1")
                .action("REPAIR")
                .repairScore(85)
                .replaceScore(30)
                .moneySaved(400.0)
                .carbonSaved(45.0)
                .rationale("High repair viability")
                .build();
    }

    @Test
    @DisplayName("Should generate device prediction explanation (heuristic fallback when no API key)")
    void explainDevicePrediction_Success() {
        AppProperties.Gemini geminiProps = new AppProperties.Gemini();
        geminiProps.setApiKey(""); // No API Key
        when(appProperties.getGemini()).thenReturn(geminiProps);

        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(testDevice));
        when(deviceHealthRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(testHealth));
        when(devicePredictionRepository.findByDeviceId("dev-1")).thenReturn(Optional.of(testPrediction));

        DeviceRiskExplanationResponse res = aiExplanationService.explainDevicePrediction("dev-1", "usr-1");

        assertThat(res).isNotNull();
        assertThat(res.deviceId()).isEqualTo("dev-1");
        assertThat(res.predictionScore()).isEqualTo(82);
        assertThat(res.riskLevel()).isEqualTo("LOW");
        assertThat(res.keyContributingFactors()).isNotEmpty();
        assertThat(res.componentWearAssessment()).isNotEmpty();
        assertThat(res.isDemo()).isFalse();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if device not found or unowned")
    void explainDevicePrediction_NotFound() {
        when(deviceRepository.findByIdAndUserId("dev-none", "usr-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiExplanationService.explainDevicePrediction("dev-none", "usr-1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should generate diagnosis explanation")
    void explainDiagnosis_Success() {
        AppProperties.Gemini geminiProps = new AppProperties.Gemini();
        geminiProps.setApiKey("");
        when(appProperties.getGemini()).thenReturn(geminiProps);

        when(diagnosisReportRepository.findById("diag-1")).thenReturn(Optional.of(testDiagnosis));

        DiagnosisExplanationResponse res = aiExplanationService.explainDiagnosis("diag-1", "usr-1");

        assertThat(res).isNotNull();
        assertThat(res.diagnosisId()).isEqualTo("diag-1");
        assertThat(res.probableIssue()).isEqualTo("Display Glitch");
        assertThat(res.visualEvidenceAnalysis()).isNotEmpty();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if diagnosis belongs to another user")
    void explainDiagnosis_Unauthorized() {
        when(diagnosisReportRepository.findById("diag-1")).thenReturn(Optional.of(testDiagnosis));

        assertThatThrownBy(() -> aiExplanationService.explainDiagnosis("diag-1", "other-user"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should generate recommendation explanation")
    void explainRecommendation_Success() {
        AppProperties.Gemini geminiProps = new AppProperties.Gemini();
        geminiProps.setApiKey("");
        when(appProperties.getGemini()).thenReturn(geminiProps);

        when(aiRecommendationRepository.findById("rec-1")).thenReturn(Optional.of(testRec));
        when(diagnosisReportRepository.findById("diag-1")).thenReturn(Optional.of(testDiagnosis));

        RecommendationExplanationResponse res = aiExplanationService.explainRecommendation("rec-1", "usr-1");

        assertThat(res).isNotNull();
        assertThat(res.recommendationId()).isEqualTo("rec-1");
        assertThat(res.recommendedAction()).isEqualTo("REPAIR");
        assertThat(res.costBenefitRationale()).isNotEmpty();
    }

    @Test
    @DisplayName("Should generate sustainability impact storytelling")
    void explainSustainabilityImpact_Success() {
        AppProperties.Gemini geminiProps = new AppProperties.Gemini();
        geminiProps.setApiKey("");
        when(appProperties.getGemini()).thenReturn(geminiProps);

        RepairHistory rh = RepairHistory.builder()
                .id("rh-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .co2SavedKg(55.0)
                .ewasteReducedKg(2.5)
                .moneySaved(650.0)
                .build();

        when(repairHistoryRepository.findByUserIdOrderByRepairDateDesc("usr-1")).thenReturn(List.of(rh));

        SustainabilityNarrativeResponse res = aiExplanationService.explainSustainabilityImpact("usr-1");

        assertThat(res).isNotNull();
        assertThat(res.totalCo2SavedKg()).isEqualTo(55.0);
        assertThat(res.devicesExtended()).isEqualTo(1);
        assertThat(res.storytellingNarrative()).isNotEmpty();
    }

    @Test
    @DisplayName("Should return demo explanations")
    void demoExplanations_Success() {
        DeviceRiskExplanationResponse demoRisk = aiExplanationService.getDemoDeviceRiskExplanation("dev-demo");
        assertThat(demoRisk.isDemo()).isTrue();

        DiagnosisExplanationResponse demoDiag = aiExplanationService.getDemoDiagnosisExplanation("diag-demo");
        assertThat(demoDiag.isDemo()).isTrue();

        RecommendationExplanationResponse demoRec = aiExplanationService.getDemoRecommendationExplanation("rec-demo");
        assertThat(demoRec.isDemo()).isTrue();

        SustainabilityNarrativeResponse demoSust = aiExplanationService.getDemoSustainabilityNarrative("usr-demo");
        assertThat(demoSust.isDemo()).isTrue();
    }
}
