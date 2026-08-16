package com.repairverse.ai.controller;

import com.repairverse.ai.dto.DiagnosisResponseDto.DiagnosisReportDto;
import com.repairverse.ai.dto.DiagnosisResponseDto.DiagnosisResponse;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.exception.InvalidFileException;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.DiagnosisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DiagnosisController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class DiagnosisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiagnosisService diagnosisService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private DiagnosisReportDto sampleReportDto;

    @BeforeEach
    void setUp() {
        sampleReportDto = DiagnosisReportDto.builder()
                .id("diag-999")
                .deviceId("dev-1")
                .deviceCategory("Smartphone")
                .brand("Apple")
                .model("iPhone 13")
                .imageUrl("https://cloudinary.com/sample.jpg")
                .symptoms("Cracked display and flickering")
                .probableIssue("OLED Panel Fracture")
                .confidenceScore(94)
                .repairDifficulty("Moderate")
                .repairTime("1-2 hours")
                .repairCost(85.0)
                .safetyWarning("Handle glass carefully.")
                .observations(List.of("Fracture on top left corner"))
                .createdAt("2026-08-16T12:00:00")
                .build();
    }

    @Test
    @DisplayName("POST /diagnosis - 201 Created on valid multipart upload")
    void testDiagnoseSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "crack.jpg",
                "image/jpeg",
                "image data".getBytes()
        );

        DiagnosisResponse response = new DiagnosisResponse(true, "AI hardware diagnosis completed successfully", sampleReportDto);
        when(diagnosisService.diagnoseDevice(any(), any(), any(), any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(multipart("/diagnosis")
                        .file(file)
                        .param("deviceId", "dev-1")
                        .param("deviceCategory", "Smartphone")
                        .param("brand", "Apple")
                        .param("model", "iPhone 13")
                        .param("symptoms", "Cracked display and flickering")
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("diag-999"))
                .andExpect(jsonPath("$.data.probableIssue").value("OLED Panel Fracture"))
                .andExpect(jsonPath("$.data.confidenceScore").value(94));
    }

    @Test
    @DisplayName("POST /diagnosis - 422 Unprocessable Entity when file or symptoms invalid")
    void testDiagnoseInvalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "document.pdf",
                "application/pdf",
                "pdf data".getBytes()
        );

        when(diagnosisService.diagnoseDevice(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new InvalidFileException("Unsupported file format. Supported formats: JPEG, PNG, WEBP."));

        mockMvc.perform(multipart("/diagnosis")
                        .file(file)
                        .param("symptoms", "Cracked display")
                        .with(csrf()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_FILE"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /diagnosis/{id} - 200 OK on existing report")
    void testGetReport() throws Exception {
        when(diagnosisService.getDiagnosisReport("diag-999")).thenReturn(sampleReportDto);

        mockMvc.perform(get("/diagnosis/diag-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("diag-999"))
                .andExpect(jsonPath("$.probableIssue").value("OLED Panel Fracture"));
    }
}
