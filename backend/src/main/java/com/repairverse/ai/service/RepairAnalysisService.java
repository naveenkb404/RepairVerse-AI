package com.repairverse.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.DiagnosisResponseDto.DiagnosisReportDto;
import com.repairverse.ai.dto.RecommendationRequest;
import com.repairverse.ai.dto.RecommendationResponseDto.*;
import com.repairverse.ai.entity.AIRecommendation;
import com.repairverse.ai.entity.DiagnosisReport;
import com.repairverse.ai.exception.DiagnosisNotFoundException;
import com.repairverse.ai.exception.RecommendationNotFoundException;
import com.repairverse.ai.repository.AIRecommendationRepository;
import com.repairverse.ai.repository.DiagnosisReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairAnalysisService {

    private final DiagnosisReportRepository diagnosisReportRepository;
    private final AIRecommendationRepository recommendationRepository;
    private final ObjectMapper objectMapper;

    // Reference baseline replacement costs (USD) by device category
    private static final Map<String, Double> REPLACEMENT_COST_BASELINES = Map.of(
            "smartphone", 700.0,
            "laptop", 1100.0,
            "tablet", 500.0,
            "gaming console", 450.0,
            "smartwatch", 350.0,
            "audio device", 200.0,
            "other", 400.0
    );

    // Reference lifecycle CO2 emissions avoided (kg CO2e) by repairing instead of manufacturing new device
    private static final Map<String, Double> CARBON_AVOIDED_BASELINES = Map.of(
            "smartphone", 6.5,
            "laptop", 24.0,
            "tablet", 12.5,
            "gaming console", 18.0,
            "smartwatch", 3.8,
            "audio device", 2.5,
            "other", 8.0
    );

    /**
     * Generates or retrieves a structured Repair vs. Replace Recommendation based on a diagnosis report.
     */
    @Transactional
    public RecommendationResponse generateRecommendation(RecommendationRequest request) {
        String diagnosisId = request.diagnosisId();
        DiagnosisReport report = diagnosisReportRepository.findById(diagnosisId)
                .orElseThrow(() -> new DiagnosisNotFoundException("Diagnosis report not found with ID: " + diagnosisId));

        // Check if recommendation was already generated
        Optional<AIRecommendation> existing = recommendationRepository.findByDiagnosisId(diagnosisId);
        if (existing.isPresent()) {
            return new RecommendationResponse(true, "Existing recommendation retrieved", mapToDto(existing.get(), report));
        }

        // 1. Calculate Scores & Action
        String category = report.getDeviceCategory() != null ? report.getDeviceCategory().toLowerCase() : "other";
        double replacementCost = REPLACEMENT_COST_BASELINES.getOrDefault(category, 400.0);
        double carbonAvoided = CARBON_AVOIDED_BASELINES.getOrDefault(category, 8.0);
        double repairCost = report.getRepairCost() != null ? report.getRepairCost() : 65.0;

        int repairScore = calculateRepairScore(report, replacementCost);
        int replaceScore = 100 - repairScore;

        String action = determineAction(report, repairScore);
        double moneySaved = Math.max(0.0, Math.round((replacementCost - repairCost) * 100.0) / 100.0);

        String rationale = generateRationale(action, report, moneySaved, carbonAvoided);
        RepairPlanDto plan = generateRepairPlan(report);

        // 2. Serialize plan to JSON for database persistence
        String stepsJson = "[]";
        String partsJson = "[]";
        String toolsJson = "[]";
        try {
            stepsJson = objectMapper.writeValueAsString(plan.steps());
            partsJson = objectMapper.writeValueAsString(plan.parts());
            toolsJson = objectMapper.writeValueAsString(plan.tools());
        } catch (Exception e) {
            log.warn("Failed to serialize repair plan components: {}", e.getMessage());
        }

        // 3. Persist AIRecommendation Entity
        AIRecommendation recommendationEntity = AIRecommendation.builder()
                .diagnosisId(diagnosisId)
                .recommendation(action)
                .action(action)
                .repairScore(repairScore)
                .replaceScore(replaceScore)
                .moneySaved(moneySaved)
                .carbonSaved(carbonAvoided)
                .rationale(rationale)
                .planSummary(plan.summary())
                .stepsJson(stepsJson)
                .partsJson(partsJson)
                .toolsJson(toolsJson)
                .createdAt(LocalDateTime.now())
                .build();

        AIRecommendation saved = recommendationRepository.save(recommendationEntity);
        log.info("Saved AI Recommendation id={} for diagnosisId={}", saved.getId(), diagnosisId);

        return new RecommendationResponse(true, "Repair recommendation generated successfully", mapToDto(saved, report));
    }

    /**
     * Retrieve an existing recommendation by diagnosis ID.
     */
    @Transactional(readOnly = true)
    public RecommendationResponse getRecommendationByDiagnosisId(String diagnosisId) {
        AIRecommendation recommendation = recommendationRepository.findByDiagnosisId(diagnosisId)
                .orElseThrow(() -> new RecommendationNotFoundException("Recommendation not found for diagnosis ID: " + diagnosisId));

        DiagnosisReport report = diagnosisReportRepository.findById(diagnosisId)
                .orElseThrow(() -> new DiagnosisNotFoundException("Diagnosis report not found with ID: " + diagnosisId));

        return new RecommendationResponse(true, "Recommendation retrieved", mapToDto(recommendation, report));
    }

    /**
     * Deterministic Repair Feasibility Score Calculation (0 - 100).
     */
    public int calculateRepairScore(DiagnosisReport report, double replacementCost) {
        int score = 75; // Baseline

        // 1. Confidence adjustment
        int confidence = report.getConfidenceScore() != null ? report.getConfidenceScore() : 80;
        if (confidence >= 90) score += 10;
        else if (confidence >= 75) score += 5;
        else if (confidence < 60) score -= 15;

        // 2. Repair Difficulty adjustment
        String difficulty = report.getRepairDifficulty() != null ? report.getRepairDifficulty() : "Moderate";
        switch (difficulty.toLowerCase()) {
            case "easy" -> score += 15;
            case "moderate" -> score += 5;
            case "hard" -> score -= 15;
            case "complex" -> score -= 30;
        }

        // 3. Economic ratio adjustment (repairCost / replacementCost)
        double repairCost = report.getRepairCost() != null ? report.getRepairCost() : 65.0;
        double ratio = repairCost / replacementCost;
        if (ratio < 0.20) score += 15;
        else if (ratio < 0.40) score += 5;
        else if (ratio < 0.60) score -= 15;
        else score -= 35;

        // 4. Critical Safety Warning adjustment
        String warning = report.getSafetyWarning() != null ? report.getSafetyWarning().toLowerCase() : "";
        if (warning.contains("high voltage") || warning.contains("ac power") || warning.contains("puncture")) {
            score -= 20;
        }

        return Math.max(5, Math.min(98, score));
    }

    /**
     * Deterministic Recommended Action Decision.
     */
    public String determineAction(DiagnosisReport report, int repairScore) {
        String warning = report.getSafetyWarning() != null ? report.getSafetyWarning().toLowerCase() : "";
        String difficulty = report.getRepairDifficulty() != null ? report.getRepairDifficulty().toLowerCase() : "";

        // Severe hazard + complex repair requires certified technician
        if ((warning.contains("high voltage") || warning.contains("ac power") || warning.contains("short-circuit"))
                && (difficulty.contains("hard") || difficulty.contains("complex"))) {
            return "PROFESSIONAL_SERVICE";
        }

        int confidence = report.getConfidenceScore() != null ? report.getConfidenceScore() : 80;
        if (confidence < 60) {
            return "MONITOR";
        }

        if (repairScore >= 60) {
            return "REPAIR";
        }

        return "REPLACE";
    }

    /**
     * Generates a clear rationale explaining the decision.
     */
    private String generateRationale(String action, DiagnosisReport report, double moneySaved, double carbonSaved) {
        return switch (action) {
            case "REPAIR" -> String.format(
                    "Self-repair is strongly recommended. Resolving the %s costs approximately $%.0f, saving $%.0f compared to a new purchase while preventing %.1f kg of CO₂ emissions.",
                    report.getProbableIssue(),
                    report.getRepairCost() != null ? report.getRepairCost() : 65.0,
                    moneySaved,
                    carbonSaved
            );
            case "PROFESSIONAL_SERVICE" -> String.format(
                    "Professional technician service is recommended. While repairing saves $%.0f and %.1f kg CO₂, the repair involves high-voltage circuitry or delicate micro-soldering requiring specialized workshop equipment.",
                    moneySaved,
                    carbonSaved
            );
            case "MONITOR" -> String.format(
                    "Monitoring device behavior is advised. Initial diagnostic confidence is %d%%. Re-evaluate if symptoms persist before committing to hardware component replacement.",
                    report.getConfidenceScore() != null ? report.getConfidenceScore() : 60
            );
            case "REPLACE" -> String.format(
                    "Replacement is advised. The estimated repair cost of $%.0f approaches the current market value, or structural board damage makes lasting repair economically unviable.",
                    report.getRepairCost() != null ? report.getRepairCost() : 150.0
            );
            default -> "Proceed with recommended repair procedure to maximize device lifespan and reduce electronic waste.";
        };
    }

    /**
     * Generates structured step-by-step repair plan, parts, and tools.
     */
    private RepairPlanDto generateRepairPlan(DiagnosisReport report) {
        String issue = report.getProbableIssue() != null ? report.getProbableIssue().toLowerCase() : "";
        String category = report.getDeviceCategory() != null ? report.getDeviceCategory() : "Device";

        List<RepairStepDto> steps = new ArrayList<>();
        List<RequiredPartDto> parts = new ArrayList<>();
        List<RequiredToolDto> tools = new ArrayList<>();

        if (issue.contains("screen") || issue.contains("display") || issue.contains("digitizer") || issue.contains("fracture")) {
            steps.add(new RepairStepDto(1, "Power Off & Apply Perimeter Heat", "Completely shut down device. Use heat gun/heating pad around perimeter for 2 minutes to soften screen adhesive.", "Do not exceed 80°C to prevent thermal stress on internal battery.", 10));
            steps.add(new RepairStepDto(2, "Apply Suction Cup & Slice Adhesive", "Attach suction cup near bottom bezel. Insert plastic opening pick and slice perimeter adhesive.", "Slice shallowly on connector edge to avoid severing flex cables.", 15));
            steps.add(new RepairStepDto(3, "Disconnect Battery & Display Brackets", "Remove EMI shield screws. Disconnect battery connector first, then disconnect display flex ribbons.", "Always disconnect battery before display cables.", 10));
            steps.add(new RepairStepDto(4, "Install Replacement Display Assembly", "Connect replacement panel flex cables, secure EMI shield bracket, and test touch registration.", null, 20));
            steps.add(new RepairStepDto(5, "Seal Bezel Adhesive & Reassemble", "Apply new waterproof adhesive gasket and gently clamp perimeter edges.", null, 10));

            parts.add(new RequiredPartDto("OEM Replacement Display Assembly", 1, report.getRepairCost() != null ? Math.max(30.0, report.getRepairCost() - 20.0) : 65.0, "DISP-OEM-" + category.toUpperCase()));
            tools.add(new RequiredToolDto("Precision Screwdriver Set (P2/Y000/PH000)", "Precision Drivers", true));
            tools.add(new RequiredToolDto("Thermal Heat Gun / Heating Mat", "Thermal Equipment", true));
            tools.add(new RequiredToolDto("Suction Cup & Plastic Opening Picks", "Prying Tools", true));
        } else if (issue.contains("battery") || issue.contains("power") || issue.contains("drain")) {
            steps.add(new RepairStepDto(1, "Discharge Device & Remove Back Housing", "Discharge battery below 25%. Remove housing screws and unclip chassis rear panel.", "Do not puncture lithium-ion battery.", 15));
            steps.add(new RepairStepDto(2, "Disconnect Power Connector", "Use non-conductive plastic spudger to detach battery power connector from mainboard.", "Never use metal tools near battery terminals.", 5));
            steps.add(new RepairStepDto(3, "Pull Adhesive Stretch Tabs", "Slowly pull battery adhesive pull-tabs at a flat angle to release battery pack.", null, 15));
            steps.add(new RepairStepDto(4, "Install Fresh Battery & Reconnect", "Place new adhesive strips, position replacement battery, and reconnect terminal bracket.", null, 15));

            parts.add(new RequiredPartDto("OEM Replacement Li-ion Battery Pack", 1, report.getRepairCost() != null ? report.getRepairCost() : 45.0, "BATT-OEM-" + category.toUpperCase()));
            tools.add(new RequiredToolDto("Non-Conductive Plastic Spudger", "Prying Tools", true));
            tools.add(new RequiredToolDto("Precision Driver Bit Set", "Precision Drivers", true));
            tools.add(new RequiredToolDto("Battery Adhesive Pull Tabs", "Adhesives", true));
        } else {
            steps.add(new RepairStepDto(1, "Diagnostic Teardown & Inspection", "Open chassis and inspect internal sub-assemblies for damaged SMD components or loose ribbons.", "Disconnect power source before handling internal board components.", 20));
            steps.add(new RepairStepDto(2, "Component Replacement / Soldering", "Replace designated modular sub-assembly or reflow defective solder joint.", null, 30));
            steps.add(new RepairStepDto(3, "Post-Repair Testing & Calibration", "Verify component functionality and reassemble housing.", null, 15));

            parts.add(new RequiredPartDto("Replacement Modular Hardware Component", 1, report.getRepairCost() != null ? report.getRepairCost() : 50.0, "PART-" + category.toUpperCase()));
            tools.add(new RequiredToolDto("Precision Electronic Toolkit", "Drivers & Prying", true));
            tools.add(new RequiredToolDto("Anti-Static ESD Wrist Strap", "Safety Gear", true));
        }

        String summary = String.format("Standard %s repair procedure. Requires standard precision electronic tools and basic ESD protection.", report.getProbableIssue());

        return new RepairPlanDto(summary, steps, parts, tools);
    }

    private RepairRecommendationDto mapToDto(AIRecommendation entity, DiagnosisReport report) {
        String createdAtIso = entity.getCreatedAt() != null
                ? entity.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        DiagnosisReportDto reportDto = DiagnosisReportDto.builder()
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
                .createdAt(report.getCreatedAt() != null ? report.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : createdAtIso)
                .build();

        List<RepairStepDto> steps = deserialize(entity.getStepsJson(), new TypeReference<>() {});
        List<RequiredPartDto> parts = deserialize(entity.getPartsJson(), new TypeReference<>() {});
        List<RequiredToolDto> tools = deserialize(entity.getToolsJson(), new TypeReference<>() {});

        RepairPlanDto planDto = RepairPlanDto.builder()
                .summary(entity.getPlanSummary())
                .steps(steps)
                .parts(parts)
                .tools(tools)
                .build();

        RepairVsReplaceDecisionDto decisionDto = RepairVsReplaceDecisionDto.builder()
                .repairScore(entity.getRepairScore())
                .replaceScore(entity.getReplaceScore())
                .recommendation(entity.getAction())
                .moneySaved(entity.getMoneySaved())
                .carbonSaved(entity.getCarbonSaved())
                .rationale(entity.getRationale())
                .build();

        return RepairRecommendationDto.builder()
                .id(entity.getId())
                .diagnosisId(entity.getDiagnosisId())
                .diagnosisReport(reportDto)
                .action(entity.getAction())
                .repairScore(entity.getRepairScore())
                .replaceScore(entity.getReplaceScore())
                .plan(planDto)
                .decision(decisionDto)
                .createdAt(createdAtIso)
                .build();
    }

    private <T> List<T> deserialize(String json, TypeReference<List<T>> typeReference) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.warn("Failed to deserialize repair plan JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
