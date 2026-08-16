package com.repairverse.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.DiagnosisResponseDto.DiagnosisResponse;
import com.repairverse.ai.dto.GeminiVisionResponse;
import com.repairverse.ai.entity.DiagnosisReport;
import com.repairverse.ai.exception.InvalidFileException;
import com.repairverse.ai.repository.DiagnosisReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnosisServiceTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private AiVisionService aiVisionService;

    @Mock
    private DiagnosisReportRepository diagnosisReportRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private DiagnosisService diagnosisService;

    private GeminiVisionResponse sampleAiResponse;
    private DiagnosisReport sampleReport;

    @BeforeEach
    void setUp() {
        sampleAiResponse = GeminiVisionResponse.builder()
                .probableIssue("OLED Panel Fracture & Lithium Battery Degradation")
                .confidenceScore(94)
                .repairDifficulty("Moderate")
                .repairTime("1-2 hours")
                .repairCost(85.0)
                .safetyWarning("Handle cracked glass with protective eye-wear.")
                .observations(List.of("Fracture on left bezel", "Digitizer unresponsive"))
                .build();

        sampleReport = DiagnosisReport.builder()
                .id("diag-12345")
                .userId("usr-1")
                .deviceId("dev-1")
                .deviceCategory("Smartphone")
                .brand("Apple")
                .model("iPhone 13")
                .imageUrl("https://cloudinary.com/sample.jpg")
                .symptoms("Cracked display")
                .probableIssue("OLED Panel Fracture & Lithium Battery Degradation")
                .confidenceScore(94)
                .repairDifficulty("Moderate")
                .repairTime("1-2 hours")
                .repairCost(85.0)
                .safetyWarning("Handle cracked glass with protective eye-wear.")
                .observations("[\"Fracture on left bezel\", \"Digitizer unresponsive\"]")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully coordinate upload, AI diagnosis, and persistence")
    void testDiagnoseDeviceSuccess() {
        MockMultipartFile file = new MockMultipartFile("image", "phone.jpg", "image/jpeg", "image bytes".getBytes());

        when(cloudinaryService.uploadImage(file)).thenReturn("https://cloudinary.com/sample.jpg");
        when(aiVisionService.analyzeDevice(any(), any(), any(), any(), any())).thenReturn(sampleAiResponse);
        when(diagnosisReportRepository.save(any(DiagnosisReport.class))).thenReturn(sampleReport);

        DiagnosisResponse response = diagnosisService.diagnoseDevice(
                file,
                "dev-1",
                "Smartphone",
                "Apple",
                "iPhone 13",
                "Cracked display",
                "usr-1"
        );

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("diag-12345", response.data().id());
        assertEquals("OLED Panel Fracture & Lithium Battery Degradation", response.data().probableIssue());
        assertEquals(94, response.data().confidenceScore());
        assertEquals(85.0, response.data().repairCost());
        assertEquals("https://cloudinary.com/sample.jpg", response.data().imageUrl());
        assertFalse(response.data().observations().isEmpty());

        verify(cloudinaryService, times(1)).uploadImage(file);
        verify(aiVisionService, times(1)).analyzeDevice(any(), any(), any(), any(), any());
        verify(diagnosisReportRepository, times(1)).save(any(DiagnosisReport.class));
    }

    @Test
    @DisplayName("Should throw InvalidFileException if symptoms are empty")
    void testEmptySymptoms() {
        MockMultipartFile file = new MockMultipartFile("image", "phone.jpg", "image/jpeg", "bytes".getBytes());

        assertThrows(InvalidFileException.class, () -> diagnosisService.diagnoseDevice(
                file, "dev-1", "Smartphone", "Apple", "iPhone 13", "   ", "usr-1"
        ));

        verify(cloudinaryService, never()).uploadImage(any());
        verify(diagnosisReportRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should retrieve saved report by ID")
    void testGetReportById() {
        when(diagnosisReportRepository.findById("diag-12345")).thenReturn(Optional.of(sampleReport));

        var dto = diagnosisService.getDiagnosisReport("diag-12345");

        assertNotNull(dto);
        assertEquals("diag-12345", dto.id());
        assertEquals("OLED Panel Fracture & Lithium Battery Degradation", dto.probableIssue());
        assertEquals(2, dto.observations().size());
    }
}
