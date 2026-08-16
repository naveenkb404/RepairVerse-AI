package com.repairverse.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.DiagnosisResponseDto.DiagnosisReportDto;
import com.repairverse.ai.dto.DiagnosisResponseDto.DiagnosisResponse;
import com.repairverse.ai.dto.GeminiVisionResponse;
import com.repairverse.ai.entity.DiagnosisReport;
import com.repairverse.ai.exception.InvalidFileException;
import com.repairverse.ai.repository.DiagnosisReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiagnosisService {

    private final CloudinaryService cloudinaryService;
    private final AiVisionService aiVisionService;
    private final DiagnosisReportRepository diagnosisReportRepository;
    private final ObjectMapper objectMapper;

    /**
     * Executes the complete AI Visual Diagnosis Pipeline:
     * 1. Validates and uploads device image to Cloudinary storage.
     * 2. Submits photo and symptoms to Google Gemini 1.5 AI Vision engine.
     * 3. Normalizes and persists diagnosis report in PostgreSQL.
     * 4. Returns formatted DiagnosisReport response for frontend.
     */
    @Transactional
    public DiagnosisResponse diagnoseDevice(
            MultipartFile image,
            String deviceId,
            String deviceCategory,
            String brand,
            String model,
            String symptoms,
            String userId
    ) {
        if (symptoms == null || symptoms.trim().isEmpty()) {
            throw new InvalidFileException("Device symptoms description is required.");
        }

        log.info("Starting AI Diagnosis for user={} deviceId={} category={} brand={} model={}",
                userId, deviceId, deviceCategory, brand, model);

        // 1. Upload photo to Cloudinary
        String imageUrl = cloudinaryService.uploadImage(image);

        // 2. Analyze hardware with Gemini AI Vision
        GeminiVisionResponse aiResult = aiVisionService.analyzeDevice(
                image,
                deviceCategory,
                brand,
                model,
                symptoms
        );

        // 3. Serialize observations list to JSON for persistence
        String observationsJson = "[]";
        try {
            if (aiResult.getObservations() != null) {
                observationsJson = objectMapper.writeValueAsString(aiResult.getObservations());
            }
        } catch (Exception e) {
            log.warn("Failed to serialize observations to JSON: {}", e.getMessage());
        }

        // 4. Save DiagnosisReport entity
        DiagnosisReport report = DiagnosisReport.builder()
                .userId(userId)
                .deviceId(deviceId)
                .deviceCategory(deviceCategory != null ? deviceCategory : "Electronic Device")
                .brand(brand)
                .model(model)
                .imageUrl(imageUrl)
                .symptoms(symptoms.trim())
                .probableIssue(aiResult.getProbableIssue())
                .confidenceScore(aiResult.getConfidenceScore() != null ? aiResult.getConfidenceScore() : 85)
                .repairDifficulty(aiResult.getRepairDifficulty() != null ? aiResult.getRepairDifficulty() : "Moderate")
                .repairTime(aiResult.getRepairTime() != null ? aiResult.getRepairTime() : "1-2 hours")
                .repairCost(aiResult.getRepairCost() != null ? aiResult.getRepairCost() : 75.0)
                .safetyWarning(aiResult.getSafetyWarning())
                .observations(observationsJson)
                .createdAt(LocalDateTime.now())
                .build();

        DiagnosisReport saved = diagnosisReportRepository.save(report);
        log.info("Successfully completed and saved AI Diagnosis report id={}", saved.getId());

        // 5. Build and return response DTO
        DiagnosisReportDto reportDto = mapToDto(saved, aiResult.getObservations());

        return new DiagnosisResponse(true, "AI hardware diagnosis completed successfully", reportDto);
    }

    /**
     * Retrieve a diagnosis report by ID.
     */
    @Transactional(readOnly = true)
    public DiagnosisReportDto getDiagnosisReport(String id) {
        DiagnosisReport report = diagnosisReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Diagnosis report not found with id: " + id));

        List<String> observations = parseObservations(report.getObservations());
        return mapToDto(report, observations);
    }

    private DiagnosisReportDto mapToDto(DiagnosisReport report, List<String> observations) {
        String createdAtIso = report.getCreatedAt() != null
                ? report.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return DiagnosisReportDto.builder()
                .id(report.getId())
                .deviceId(report.getDeviceId())
                .deviceCategory(report.getDeviceCategory())
                .brand(report.getBrand())
                .model(report.getModel())
                .imageUrl(report.getImageUrl())
                .symptoms(report.getSymptoms())
                .probableIssue(report.getProbableIssue())
                .confidenceScore(report.getConfidenceScore())
                .repairDifficulty(report.getRepairDifficulty())
                .repairTime(report.getRepairTime())
                .repairCost(report.getRepairCost())
                .safetyWarning(report.getSafetyWarning())
                .observations(observations)
                .createdAt(createdAtIso)
                .build();
    }

    private List<String> parseObservations(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.singletonList(json);
        }
    }
}
