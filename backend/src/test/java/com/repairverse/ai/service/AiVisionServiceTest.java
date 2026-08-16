package com.repairverse.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.config.AppProperties;
import com.repairverse.ai.dto.GeminiVisionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiVisionServiceTest {

    @Mock
    private AppProperties appProperties;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AiVisionService aiVisionService;

    private AppProperties.Gemini geminiProps;

    @BeforeEach
    void setUp() {
        geminiProps = new AppProperties.Gemini();
        geminiProps.setApiKey("");
        geminiProps.setApiUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent");
    }

    @Test
    @DisplayName("Should generate accurate heuristic diagnosis for screen display fracture")
    void testScreenHeuristic() {
        GeminiVisionResponse response = aiVisionService.generateHeuristicFallback(
                "Smartphone",
                "Apple",
                "iPhone 13",
                "Screen is cracked and touch is unresponsive in upper corner"
        );

        assertNotNull(response);
        assertEquals("Digitizer & Display Panel Fracture", response.getProbableIssue());
        assertTrue(response.getConfidenceScore() >= 80);
        assertEquals("Moderate", response.getRepairDifficulty());
        assertEquals(85.0, response.getRepairCost());
        assertNotNull(response.getSafetyWarning());
        assertFalse(response.getObservations().isEmpty());
    }

    @Test
    @DisplayName("Should generate safety warning for battery drain and power faults")
    void testBatteryHeuristic() {
        GeminiVisionResponse response = aiVisionService.generateHeuristicFallback(
                "Laptop",
                "Dell",
                "XPS 15",
                "Battery drains instantly and device shuts down on AC disconnect"
        );

        assertNotNull(response);
        assertTrue(response.getProbableIssue().contains("Battery"));
        assertTrue(response.getSafetyWarning().contains("lithium-ion"));
        assertEquals("Easy", response.getRepairDifficulty());
        assertEquals(45.0, response.getRepairCost());
    }

    @Test
    @DisplayName("Should generate ultrasonic board cleaning recommendation for liquid damage")
    void testLiquidDamageHeuristic() {
        GeminiVisionResponse response = aiVisionService.generateHeuristicFallback(
                "Smartphone",
                "Samsung",
                "Galaxy S22",
                "Dropped in water and won't turn on"
        );

        assertNotNull(response);
        assertTrue(response.getProbableIssue().contains("Corrosion"));
        assertEquals("Hard", response.getRepairDifficulty());
        assertTrue(response.getSafetyWarning().contains("AC power"));
    }

    @Test
    @DisplayName("Should gracefully fall back to heuristic when Gemini API key is absent")
    void testAnalyzeDeviceWithoutKey() {
        MockMultipartFile file = new MockMultipartFile("image", "screen.jpg", "image/jpeg", "image bytes".getBytes());
        when(appProperties.getGemini()).thenReturn(geminiProps);

        GeminiVisionResponse response = aiVisionService.analyzeDevice(
                file,
                "Smartphone",
                "Google",
                "Pixel 7",
                "Glass broken"
        );

        assertNotNull(response);
        assertNotNull(response.getProbableIssue());
        assertTrue(response.getConfidenceScore() > 0);
    }
}
