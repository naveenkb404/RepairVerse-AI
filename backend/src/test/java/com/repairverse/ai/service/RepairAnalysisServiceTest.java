package com.repairverse.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.RecommendationRequest;
import com.repairverse.ai.dto.RecommendationResponseDto.RecommendationResponse;
import com.repairverse.ai.entity.AIRecommendation;
import com.repairverse.ai.entity.DiagnosisReport;
import com.repairverse.ai.exception.DiagnosisNotFoundException;
import com.repairverse.ai.repository.AIRecommendationRepository;
import com.repairverse.ai.repository.DiagnosisReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairAnalysisServiceTest {

    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @Mock
    private AIRecommendationRepository recommendationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private RepairAnalysisService repairAnalysisService;

    private DiagnosisReport sampleDiagnosis;

    @BeforeEach
    void setUp() {
        repairAnalysisService = new RepairAnalysisService(diagnosisReportRepository, recommendationRepository, objectMapper);

        sampleDiagnosis = DiagnosisReport.builder()
                .id("diag-101")
                .userId("usr-1")
                .deviceId("dev-1")
                .deviceCategory("Smartphone")
                .brand("Apple")
                .model("iPhone 13")
                .imageUrl("https://cloudinary.com/sample.jpg")
                .symptoms("Cracked display and flickering")
                .probableIssue("Digitizer & Display Panel Fracture")
                .confidenceScore(92)
                .repairDifficulty("Moderate")
                .repairTime("45-90 mins")
                .repairCost(85.0)
                .safetyWarning("Handle glass carefully.")
                .observations("[\"Visual fracture detected across digitizer.\"]")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should generate high repair score and REPAIR action for manageable display replacement")
    void testGenerateRecommendationRepair() {
        when(diagnosisReportRepository.findById("diag-101")).thenReturn(Optional.of(sampleDiagnosis));
        when(recommendationRepository.findByDiagnosisId("diag-101")).thenReturn(Optional.empty());
        when(recommendationRepository.save(any(AIRecommendation.class))).thenAnswer(invocation -> {
            AIRecommendation rec = invocation.getArgument(0);
            rec.setId("rec-101");
            return rec;
        });

        RecommendationResponse response = repairAnalysisService.generateRecommendation(new RecommendationRequest("diag-101"));

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("REPAIR", response.data().action());
        assertTrue(response.data().repairScore() >= 70);
        assertEquals(100 - response.data().repairScore(), response.data().replaceScore());
        assertEquals(615.0, response.data().decision().moneySaved()); // $700 baseline - $85 repair
        assertEquals(6.5, response.data().decision().carbonSaved());
        assertNotNull(response.data().plan().summary());
        assertFalse(response.data().plan().steps().isEmpty());
        assertFalse(response.data().plan().parts().isEmpty());
        assertFalse(response.data().plan().tools().isEmpty());

        verify(recommendationRepository, times(1)).save(any(AIRecommendation.class));
    }

    @Test
    @DisplayName("Should recommend PROFESSIONAL_SERVICE for severe high-voltage hazard on Hard repair")
    void testProfessionalServiceForHighVoltage() {
        DiagnosisReport hazardousDiagnosis = DiagnosisReport.builder()
                .id("diag-haz")
                .deviceCategory("Laptop")
                .probableIssue("Burnt AC Power Circuit & Short-Circuit on SMD Rails")
                .confidenceScore(85)
                .repairDifficulty("Hard")
                .repairCost(120.0)
                .safetyWarning("High voltage power supply danger! Disconnect AC power.")
                .createdAt(LocalDateTime.now())
                .build();

        when(diagnosisReportRepository.findById("diag-haz")).thenReturn(Optional.of(hazardousDiagnosis));
        when(recommendationRepository.findByDiagnosisId("diag-haz")).thenReturn(Optional.empty());
        when(recommendationRepository.save(any(AIRecommendation.class))).thenAnswer(i -> {
            AIRecommendation rec = i.getArgument(0);
            rec.setId("rec-haz");
            return rec;
        });

        RecommendationResponse response = repairAnalysisService.generateRecommendation(new RecommendationRequest("diag-haz"));

        assertNotNull(response);
        assertEquals("PROFESSIONAL_SERVICE", response.data().action());
    }

    @Test
    @DisplayName("Should recommend MONITOR when diagnostic confidence is low")
    void testMonitorForLowConfidence() {
        DiagnosisReport lowConfDiagnosis = DiagnosisReport.builder()
                .id("diag-low")
                .deviceCategory("Smartphone")
                .probableIssue("Intermittent Software or Hardware Glitch")
                .confidenceScore(45)
                .repairDifficulty("Moderate")
                .repairCost(60.0)
                .createdAt(LocalDateTime.now())
                .build();

        when(diagnosisReportRepository.findById("diag-low")).thenReturn(Optional.of(lowConfDiagnosis));
        when(recommendationRepository.findByDiagnosisId("diag-low")).thenReturn(Optional.empty());
        when(recommendationRepository.save(any(AIRecommendation.class))).thenAnswer(i -> {
            AIRecommendation rec = i.getArgument(0);
            rec.setId("rec-low");
            return rec;
        });

        RecommendationResponse response = repairAnalysisService.generateRecommendation(new RecommendationRequest("diag-low"));

        assertNotNull(response);
        assertEquals("MONITOR", response.data().action());
    }

    @Test
    @DisplayName("Should recommend REPLACE when repair cost exceeds economic viability")
    void testReplaceForHighCostComplexRepair() {
        DiagnosisReport uneconomicDiagnosis = DiagnosisReport.builder()
                .id("diag-unec")
                .deviceCategory("Smartphone") // $700 baseline
                .probableIssue("Total Mainboard Submersion & Multi-Chip Failure")
                .confidenceScore(80)
                .repairDifficulty("Complex")
                .repairCost(580.0) // > 60% of replacement cost
                .createdAt(LocalDateTime.now())
                .build();

        when(diagnosisReportRepository.findById("diag-unec")).thenReturn(Optional.of(uneconomicDiagnosis));
        when(recommendationRepository.findByDiagnosisId("diag-unec")).thenReturn(Optional.empty());
        when(recommendationRepository.save(any(AIRecommendation.class))).thenAnswer(i -> {
            AIRecommendation rec = i.getArgument(0);
            rec.setId("rec-unec");
            return rec;
        });

        RecommendationResponse response = repairAnalysisService.generateRecommendation(new RecommendationRequest("diag-unec"));

        assertNotNull(response);
        assertEquals("REPLACE", response.data().action());
    }

    @Test
    @DisplayName("Should throw DiagnosisNotFoundException on nonexistent diagnosis ID")
    void testDiagnosisNotFound() {
        when(diagnosisReportRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(DiagnosisNotFoundException.class, () ->
                repairAnalysisService.generateRecommendation(new RecommendationRequest("nonexistent")));

        verify(recommendationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should clamp repair score between 5 and 98")
    void testScoreBounds() {
        int score = repairAnalysisService.calculateRepairScore(sampleDiagnosis, 700.0);
        assertTrue(score >= 5 && score <= 98);
    }
}
