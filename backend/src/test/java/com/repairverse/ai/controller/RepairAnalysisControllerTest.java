package com.repairverse.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.DiagnosisResponseDto.DiagnosisReportDto;
import com.repairverse.ai.dto.RecommendationRequest;
import com.repairverse.ai.dto.RecommendationResponseDto.*;
import com.repairverse.ai.exception.DiagnosisNotFoundException;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.RepairAnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RepairAnalysisController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RepairAnalysisService repairAnalysisService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private RepairRecommendationDto sampleRecommendationDto;

    @BeforeEach
    void setUp() {
        DiagnosisReportDto diagDto = DiagnosisReportDto.builder()
                .id("diag-101")
                .deviceCategory("Smartphone")
                .probableIssue("Display Panel Fracture")
                .build();

        RepairPlanDto planDto = RepairPlanDto.builder()
                .summary("Screen replacement procedure")
                .steps(List.of(new RepairStepDto(1, "Power off", "Turn off device", null, 5)))
                .parts(List.of(new RequiredPartDto("Display Panel", 1, 65.0, "DISP-1")))
                .tools(List.of(new RequiredToolDto("Precision Driver", "Drivers", true)))
                .build();

        RepairVsReplaceDecisionDto decisionDto = RepairVsReplaceDecisionDto.builder()
                .repairScore(92)
                .replaceScore(8)
                .recommendation("REPAIR")
                .moneySaved(635.0)
                .carbonSaved(6.5)
                .rationale("Repair saves money and carbon emissions.")
                .build();

        sampleRecommendationDto = RepairRecommendationDto.builder()
                .id("rec-101")
                .diagnosisId("diag-101")
                .diagnosisReport(diagDto)
                .action("REPAIR")
                .repairScore(92)
                .replaceScore(8)
                .plan(planDto)
                .decision(decisionDto)
                .createdAt("2026-08-16T12:00:00")
                .build();
    }

    @Test
    @DisplayName("POST /repair-analysis - 201 Created on valid request")
    void testAnalyzeSuccess() throws Exception {
        RecommendationResponse response = new RecommendationResponse(true, "Repair recommendation generated successfully", sampleRecommendationDto);
        when(repairAnalysisService.generateRecommendation(any(RecommendationRequest.class))).thenReturn(response);

        RecommendationRequest request = new RecommendationRequest("diag-101");

        mockMvc.perform(post("/repair-analysis")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("rec-101"))
                .andExpect(jsonPath("$.data.action").value("REPAIR"))
                .andExpect(jsonPath("$.data.repairScore").value(92))
                .andExpect(jsonPath("$.data.decision.moneySaved").value(635.0));
    }

    @Test
    @DisplayName("POST /repair-analysis - 404 Not Found on nonexistent diagnosisId")
    void testAnalyzeDiagnosisNotFound() throws Exception {
        when(repairAnalysisService.generateRecommendation(any(RecommendationRequest.class)))
                .thenThrow(new DiagnosisNotFoundException("Diagnosis report not found with ID: missing-diag"));

        RecommendationRequest request = new RecommendationRequest("missing-diag");

        mockMvc.perform(post("/repair-analysis")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("DIAGNOSIS_NOT_FOUND"));
    }

    @Test
    @DisplayName("POST /repair-analysis - 422 Unprocessable Entity on blank diagnosisId")
    void testAnalyzeBlankId() throws Exception {
        RecommendationRequest request = new RecommendationRequest("");

        mockMvc.perform(post("/repair-analysis")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("GET /repair-analysis/{diagnosisId} - 200 OK")
    void testGetByDiagnosisId() throws Exception {
        RecommendationResponse response = new RecommendationResponse(true, "Recommendation retrieved", sampleRecommendationDto);
        when(repairAnalysisService.getRecommendationByDiagnosisId("diag-101")).thenReturn(response);

        mockMvc.perform(get("/repair-analysis/diag-101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("rec-101"));
    }
}
