package com.repairverse.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.config.AppProperties;
import com.repairverse.ai.dto.AiExplanationDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for Phase 23: Generative AI Repair Intelligence & Explainable AI.
 *
 * <p>Translates deterministic telemetry, failure predictions, diagnosis observations,
 * and repair-vs-replace metrics into explainable narratives using Google Gemini 1.5.
 *
 * <p>Deterministic scoring and security rules remain strictly authoritative;
 * Gemini only provides textual narrative explanations and safety enrichment.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiExplanationService {

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;
    private final DeviceRepository deviceRepository;
    private final DeviceHealthRepository deviceHealthRepository;
    private final DevicePredictionRepository devicePredictionRepository;
    private final DiagnosisReportRepository diagnosisReportRepository;
    private final AIRecommendationRepository aiRecommendationRepository;
    private final RepairHistoryRepository repairHistoryRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String GEMINI_MODEL_LABEL = "Gemini 1.5 Flash (Explainable AI)";
    private static final String HEURISTIC_MODEL_LABEL = "Deterministic Heuristic XAI Engine";

    // ─── 1. Explain Device Failure & Risk Prediction ──────────────────────────

    @Transactional(readOnly = true)
    public DeviceRiskExplanationResponse explainDevicePrediction(String deviceId, String userId) {
        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found or not owned by user: " + deviceId));

        DeviceHealth health = deviceHealthRepository.findByDeviceId(deviceId).orElse(null);
        DevicePrediction prediction = devicePredictionRepository.findByDeviceId(deviceId).orElse(null);

        int score = prediction != null ? prediction.getPredictionScore() : (health != null ? health.getHealthScore() : 80);
        String risk = prediction != null ? prediction.getRiskLevel() : "LOW";
        String primaryFault = prediction != null ? prediction.getPrimaryFaultType() : "General Component Aging";

        String apiKey = appProperties.getGemini().getApiKey();
        if (StringUtils.hasText(apiKey)) {
            try {
                return callGeminiForDevicePrediction(device, health, prediction, score, risk, primaryFault);
            } catch (Exception e) {
                log.warn("Gemini call for device prediction explanation failed: {}. Falling back to deterministic XAI engine.", e.getMessage());
            }
        }

        return generateHeuristicDevicePredictionExplanation(device, health, prediction, score, risk, primaryFault, false);
    }

    // ─── 2. Explain Diagnosis Reasoning & Evidence Breakdown ─────────────────

    @Transactional(readOnly = true)
    public DiagnosisExplanationResponse explainDiagnosis(String diagnosisId, String userId) {
        DiagnosisReport report = diagnosisReportRepository.findById(diagnosisId)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis report not found: " + diagnosisId));

        if (!report.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Diagnosis report not found for user: " + diagnosisId);
        }

        String apiKey = appProperties.getGemini().getApiKey();
        if (StringUtils.hasText(apiKey)) {
            try {
                return callGeminiForDiagnosisExplanation(report);
            } catch (Exception e) {
                log.warn("Gemini call for diagnosis explanation failed: {}. Falling back to deterministic XAI engine.", e.getMessage());
            }
        }

        return generateHeuristicDiagnosisExplanation(report, false);
    }

    // ─── 3. Explain Repair vs. Replace Recommendation Rationale ──────────────

    @Transactional(readOnly = true)
    public RecommendationExplanationResponse explainRecommendation(String recommendationId, String userId) {
        AIRecommendation recommendation = aiRecommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("AI Recommendation not found: " + recommendationId));

        DiagnosisReport report = diagnosisReportRepository.findById(recommendation.getDiagnosisId())
                .orElse(null);

        if (report != null && !report.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Recommendation not found for user: " + recommendationId);
        }

        String apiKey = appProperties.getGemini().getApiKey();
        if (StringUtils.hasText(apiKey)) {
            try {
                return callGeminiForRecommendationExplanation(recommendation, report);
            } catch (Exception e) {
                log.warn("Gemini call for recommendation explanation failed: {}. Falling back to deterministic XAI engine.", e.getMessage());
            }
        }

        return generateHeuristicRecommendationExplanation(recommendation, report, false);
    }

    // ─── 4. Explain Sustainability & Environmental Storytelling ───────────────

    @Transactional(readOnly = true)
    public SustainabilityNarrativeResponse explainSustainabilityImpact(String userId) {
        List<RepairHistory> repairs = repairHistoryRepository.findByUserIdOrderByRepairDateDesc(userId);

        double totalCo2 = repairs.stream().mapToDouble(r -> r.getCo2SavedKg() != null ? r.getCo2SavedKg() : 0).sum();
        double totalEwaste = repairs.stream().mapToDouble(r -> r.getEwasteReducedKg() != null ? r.getEwasteReducedKg() : 0).sum();
        double totalMoney = repairs.stream().mapToDouble(r -> r.getMoneySaved() != null ? r.getMoneySaved() : 0).sum();
        int extended = (int) repairs.stream().map(RepairHistory::getDeviceId).distinct().count();

        String apiKey = appProperties.getGemini().getApiKey();
        if (StringUtils.hasText(apiKey) && totalCo2 > 0) {
            try {
                return callGeminiForSustainability(userId, totalCo2, totalEwaste, totalMoney, extended);
            } catch (Exception e) {
                log.warn("Gemini call for sustainability storytelling failed: {}. Falling back to deterministic XAI engine.", e.getMessage());
            }
        }

        return generateHeuristicSustainabilityNarrative(userId, totalCo2, totalEwaste, totalMoney, extended, false);
    }

    // ─── Demo Mode Generators (Offline Reference) ─────────────────────────────

    public DeviceRiskExplanationResponse getDemoDeviceRiskExplanation(String deviceId) {
        Device mockDevice = Device.builder()
                .id(deviceId)
                .deviceName("Work MacBook Pro 16 (M2 Max)")
                .category("Laptop")
                .brand("Apple")
                .model("MacBook Pro 16")
                .purchaseDate("2023-04-10")
                .build();

        DeviceHealth mockHealth = DeviceHealth.builder()
                .healthScore(84)
                .batteryHealth(89)
                .lastService("2024-02-15")
                .build();

        return generateHeuristicDevicePredictionExplanation(mockDevice, mockHealth, null, 84, "LOW", "Thermal Compound Aging", true);
    }

    public DiagnosisExplanationResponse getDemoDiagnosisExplanation(String diagnosisId) {
        DiagnosisReport mockReport = DiagnosisReport.builder()
                .id(diagnosisId)
                .deviceCategory("Smartphone")
                .brand("Apple")
                .model("iPhone 14 Pro")
                .probableIssue("OLED Subpixel Retention & Display Digitizer Degradation")
                .confidenceScore(91)
                .repairDifficulty("Moderate")
                .repairCost(140.0)
                .safetyWarning("Handle lithium-ion battery with caution during display adhesive separation.")
                .observations("Micro-fissures near lower bezel; slight thermal discoloration along display controller flex.")
                .build();

        return generateHeuristicDiagnosisExplanation(mockReport, true);
    }

    public RecommendationExplanationResponse getDemoRecommendationExplanation(String recommendationId) {
        AIRecommendation mockRec = AIRecommendation.builder()
                .id(recommendationId)
                .action("REPAIR")
                .repairScore(85)
                .replaceScore(35)
                .moneySaved(850.0)
                .carbonSaved(64.2)
                .rationale("Repairing screen and battery yields 85% lifecycle restoration at ~15% of replacement cost.")
                .build();

        DiagnosisReport mockReport = DiagnosisReport.builder()
                .deviceCategory("Laptop")
                .brand("Apple")
                .model("MacBook Pro 14")
                .repairCost(220.0)
                .build();

        return generateHeuristicRecommendationExplanation(mockRec, mockReport, true);
    }

    public SustainabilityNarrativeResponse getDemoSustainabilityNarrative(String userId) {
        return generateHeuristicSustainabilityNarrative(userId, 127.4, 8.4, 2340.0, 4, true);
    }

    // ─── Gemini Remote API Orchestration ──────────────────────────────────────

    private DeviceRiskExplanationResponse callGeminiForDevicePrediction(
            Device device, DeviceHealth health, DevicePrediction prediction, int score, String risk, String primaryFault) throws Exception {

        String prompt = String.format("""
                You are the RepairVerse Explainable AI Hardware Engineer.
                Provide an authoritative, transparent explanation for a device's predictive maintenance assessment.

                DEVICE CONTEXT:
                - Name: %s
                - Category: %s (%s %s)
                - Purchase Date: %s
                - Battery Health: %s%%
                - Predictive Health Score: %d/100
                - Risk Classification: %s
                - Primary Fault Mode: %s

                OUTPUT SCHEMA (Strict JSON):
                {
                  "executiveSummary": "Concise 2-sentence executive summary of device risk and condition",
                  "rootCauseAnalysis": "Detailed technical analysis of why this failure mode emerges and physical wear mechanisms",
                  "keyContributingFactors": [
                    {
                      "factorName": "Device Age",
                      "severity": "LOW",
                      "explanation": "Explanation of age contribution",
                      "impactOnLifespan": "Impact statement"
                    }
                  ],
                  "componentWearAssessment": [
                    {
                      "component": "Battery",
                      "status": "Good",
                      "wearMechanisms": "Lithium degradation",
                      "estimatedRemainingLife": "18-24 months"
                    }
                  ],
                  "economicJustification": "Clear justification of why preventive repair saves money vs waiting for catastrophic failure",
                  "urgencyRating": "High / Medium / Low priority timing guideline",
                  "safetyPrecautions": ["Safety tip 1", "Safety tip 2"],
                  "preventiveActionRoadmap": ["Step 1", "Step 2", "Step 3"]
                }
                """,
                device.getDeviceName(),
                device.getCategory(),
                device.getBrand(),
                device.getModel() != null ? device.getModel() : "",
                device.getPurchaseDate() != null ? device.getPurchaseDate() : "Unknown",
                health != null && health.getBatteryHealth() != null ? health.getBatteryHealth().toString() : "N/A",
                score,
                risk,
                primaryFault
        );

        JsonNode json = executeGeminiPrompt(prompt);
        return new DeviceRiskExplanationResponse(
                device.getId(),
                device.getDeviceName(),
                score,
                risk,
                json.path("executiveSummary").asText("Device is operating within expected parameters."),
                json.path("rootCauseAnalysis").asText("Normal thermal cycling and component aging."),
                parseRiskFactors(json.path("keyContributingFactors")),
                parseComponentWear(json.path("componentWearAssessment")),
                json.path("economicJustification").asText("Proactive maintenance prevents compound board failures."),
                json.path("urgencyRating").asText("Medium priority maintenance within 60 days."),
                parseStringList(json.path("safetyPrecautions")),
                parseStringList(json.path("preventiveActionRoadmap")),
                GEMINI_MODEL_LABEL,
                false,
                LocalDateTime.now().toString()
        );
    }

    private DiagnosisExplanationResponse callGeminiForDiagnosisExplanation(DiagnosisReport report) throws Exception {
        String prompt = String.format("""
                You are the RepairVerse AI Hardware Diagnostics Specialist.
                Explain the reasoning behind this hardware diagnosis.

                DIAGNOSIS CONTEXT:
                - Device: %s %s (%s)
                - Diagnosed Issue: %s
                - AI Confidence: %d%%
                - Difficulty: %s
                - Estimated Cost: $%s
                - Visual Observations: %s
                - Safety Warning: %s

                OUTPUT SCHEMA (Strict JSON):
                {
                  "visualEvidenceAnalysis": "How visual inspection indicators support this diagnosis",
                  "symptomCorrelation": "How reported symptoms correlate with this hardware component defect",
                  "differentialDiagnoses": ["Alternative possibility 1", "Alternative possibility 2"],
                  "repairFeasibilityRationale": "Why this repair is feasible for user or local repair shop",
                  "requiredToolsRationale": ["Reason for tool 1", "Reason for tool 2"],
                  "safetyWarningContext": "Expanded safety explanation regarding batteries, sharp glass, or voltage"
                }
                """,
                report.getBrand(),
                report.getModel(),
                report.getDeviceCategory(),
                report.getProbableIssue(),
                report.getConfidenceScore() != null ? report.getConfidenceScore() : 85,
                report.getRepairDifficulty() != null ? report.getRepairDifficulty() : "Moderate",
                report.getRepairCost() != null ? report.getRepairCost().toString() : "85",
                report.getObservations() != null ? report.getObservations() : "None reported",
                report.getSafetyWarning() != null ? report.getSafetyWarning() : "Standard electronics precautions."
        );

        JsonNode json = executeGeminiPrompt(prompt);
        return new DiagnosisExplanationResponse(
                report.getId(),
                report.getBrand() + " " + report.getModel(),
                report.getProbableIssue(),
                report.getConfidenceScore() != null ? report.getConfidenceScore() : 85,
                json.path("visualEvidenceAnalysis").asText("Visible surface indicators match known failure modes."),
                json.path("symptomCorrelation").asText("Reported symptoms align directly with primary component failure."),
                parseStringList(json.path("differentialDiagnoses")),
                json.path("repairFeasibilityRationale").asText("Standard component replacement with moderate tools."),
                parseStringList(json.path("requiredToolsRationale")),
                json.path("safetyWarningContext").asText("Disconnect battery and discharge capacitors before repair."),
                GEMINI_MODEL_LABEL,
                false,
                LocalDateTime.now().toString()
        );
    }

    private RecommendationExplanationResponse callGeminiForRecommendationExplanation(
            AIRecommendation rec, DiagnosisReport report) throws Exception {

        String prompt = String.format("""
                You are the RepairVerse AI Circular Economy & Economics Analyst.
                Explain the decision matrix behind this Repair vs. Replace recommendation.

                RECOMMENDATION CONTEXT:
                - Action Recommended: %s
                - Repair Viability Score: %d/100
                - Replacement Index: %d/100
                - Money Saved by Repairing: $%s
                - Carbon Emissions Avoided: %s kg CO2
                - Primary Rationale: %s

                OUTPUT SCHEMA (Strict JSON):
                {
                  "costBenefitRationale": "Comprehensive economic breakdown comparing repair cost against new device amortization",
                  "lifespanExtensionAnalysis": "How many months/years this repair extends the operational lifespan",
                  "environmentalTradeoffNarrative": "Embodied carbon avoided by keeping device in circulation vs manufacturing footprint",
                  "salvageValueAssessment": "Assessment of residual hardware and secondary market value",
                  "riskAdjustedNextSteps": ["Step 1", "Step 2", "Step 3"]
                }
                """,
                rec.getAction(),
                rec.getRepairScore() != null ? rec.getRepairScore() : 80,
                rec.getReplaceScore() != null ? rec.getReplaceScore() : 30,
                rec.getMoneySaved() != null ? rec.getMoneySaved().toString() : "450",
                rec.getCarbonSaved() != null ? rec.getCarbonSaved().toString() : "45",
                rec.getRationale() != null ? rec.getRationale() : "Repair is economically superior."
        );

        JsonNode json = executeGeminiPrompt(prompt);
        return new RecommendationExplanationResponse(
                rec.getId(),
                report != null ? report.getBrand() + " " + report.getModel() : "Electronic Device",
                rec.getAction() != null ? rec.getAction() : "REPAIR",
                report != null && report.getRepairCost() != null ? report.getRepairCost() : 120.0,
                800.0,
                json.path("costBenefitRationale").asText("Repairing restores full utility at a fraction of replacement cost."),
                json.path("lifespanExtensionAnalysis").asText("Extends operational lifespan by 24 to 36 months."),
                json.path("environmentalTradeoffNarrative").asText("Prevents raw material extraction and electronics manufacturing carbon spike."),
                json.path("salvageValueAssessment").asText("Device retains strong residual value post-repair."),
                parseStringList(json.path("riskAdjustedNextSteps")),
                GEMINI_MODEL_LABEL,
                false,
                LocalDateTime.now().toString()
        );
    }

    private SustainabilityNarrativeResponse callGeminiForSustainability(
            String userId, double totalCo2, double totalEwaste, double totalMoney, int extended) throws Exception {

        String prompt = String.format("""
                You are the RepairVerse AI Sustainability Storyteller.
                Create an inspiring, scientifically grounded sustainability narrative.

                USER IMPACT METRICS:
                - Total CO2 Emissions Avoided: %.1f kg
                - Total E-Waste Prevented: %.2f kg
                - Total Financial Savings: $%.2f
                - Number of Devices Extended: %d

                OUTPUT SCHEMA (Strict JSON):
                {
                  "impactHeadline": "Bold, inspiring 1-sentence impact milestone",
                  "storytellingNarrative": "A compelling 3-paragraph narrative celebrating the user's circular economy leadership",
                  "tangibleRealWorldEquivalents": "Tangible analogies (e.g. driving km, urban tree absorption, lightbulb hours)",
                  "circularEconomyAchievements": ["Achievement badge 1", "Achievement badge 2", "Achievement badge 3"],
                  "futureImpactProjection": "Projected environmental milestone if maintenance cadence continues for 2 more years"
                }
                """,
                totalCo2, totalEwaste, totalMoney, extended
        );

        JsonNode json = executeGeminiPrompt(prompt);
        return new SustainabilityNarrativeResponse(
                userId,
                Math.round(totalCo2 * 100.0) / 100.0,
                Math.round(totalEwaste * 100.0) / 100.0,
                Math.round(totalMoney * 100.0) / 100.0,
                extended,
                json.path("impactHeadline").asText("Championing the Right-to-Repair Movement!"),
                json.path("storytellingNarrative").asText("By repairing instead of discarding, you have significantly mitigated carbon spikes."),
                json.path("tangibleRealWorldEquivalents").asText(String.format("Equivalent to planting %.1f mature trees!", totalCo2 / 22.0)),
                parseStringList(json.path("circularEconomyAchievements")),
                json.path("futureImpactProjection").asText("Continuing this repair cadence will avert over 300 kg of greenhouse emissions by 2028."),
                GEMINI_MODEL_LABEL,
                false,
                LocalDateTime.now().toString()
        );
    }

    private JsonNode executeGeminiPrompt(String prompt) throws Exception {
        String apiKey = appProperties.getGemini().getApiKey();
        String apiUrl = appProperties.getGemini().getApiUrl();
        if (!apiUrl.contains("?key=")) {
            apiUrl = apiUrl + "?key=" + apiKey;
        }

        Map<String, Object> textPart = Map.of("text", prompt);
        List<Map<String, Object>> parts = List.of(textPart);
        Map<String, Object> contents = Map.of("parts", parts);
        Map<String, Object> generationConfig = Map.of(
                "temperature", 0.2,
                "response_mime_type", "application/json"
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(contents),
                "generationConfig", generationConfig
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                String rawText = candidates.get(0).path("content").path("parts").get(0).path("text").asText();
                return objectMapper.readTree(rawText);
            }
        }
        throw new RuntimeException("Empty or invalid response from Gemini API");
    }

    // ─── Deterministic Heuristic Fallbacks ────────────────────────────────────

    private DeviceRiskExplanationResponse generateHeuristicDevicePredictionExplanation(
            Device device, DeviceHealth health, DevicePrediction prediction, int score, String risk, String primaryFault, boolean isDemo) {

        String summary = String.format(
                "%s is currently rated at %s risk (%d/100) with primary vulnerability centered around %s.",
                device.getDeviceName(), risk, score, primaryFault);

        String rootCause = String.format(
                "Telemetry indicates that %s is experiencing gradual hardware wear characteristic of its operational cycle. " +
                "Lithium electrolyte resistance increases over charge cycles while thermal paste undergoes drying, resulting in elevated junction temperatures.",
                device.getDeviceName());

        List<RiskFactorExplanation> factors = List.of(
                new RiskFactorExplanation("Health Score", score < 70 ? "HIGH" : "LOW", "Composite diagnostic health score is " + score + "/100.", "Directly impacts system stability under load"),
                new RiskFactorExplanation("Battery Degradation", health != null && health.getBatteryHealth() != null && health.getBatteryHealth() < 80 ? "HIGH" : "LOW", "Current battery capacity is " + (health != null && health.getBatteryHealth() != null ? health.getBatteryHealth() : 88) + "%.", "Accelerates voltage sag and unexpected reboots"),
                new RiskFactorExplanation("Thermal Profile", "MEDIUM", "Thermal dissipation efficiency decreases over 18+ months of usage.", "Causes CPU thermal throttling and component stress")
        );

        List<ComponentWearDetail> wear = List.of(
                new ComponentWearDetail("Battery", health != null && health.getBatteryHealth() != null && health.getBatteryHealth() < 80 ? "Degraded" : "Good", "Chemical SEI layer thickening", "12-18 months"),
                new ComponentWearDetail("Thermal Interface", "Moderate Wear", "Thermal paste dry-out and dust accumulation", "6-12 months"),
                new ComponentWearDetail("Flash Storage (NVMe)", "Healthy", "Wear leveling within normal TBW limits", "36+ months")
        );

        List<String> precautions = List.of(
                "Avoid operating device in ambient temperatures exceeding 35°C",
                "Ensure charging cables are certified OEM to prevent voltage transients",
                "Back up essential data regularly prior to preventive servicing"
        );

        List<String> roadmap = List.of(
                "Step 1: Clean air intake and fan exhaust ports using compressed air",
                "Step 2: Calibrate battery charge cycle by cycling between 20% and 80%",
                "Step 3: Schedule professional thermal repaste if temperatures exceed 85°C"
        );

        return new DeviceRiskExplanationResponse(
                device.getId(),
                device.getDeviceName(),
                score,
                risk,
                summary,
                rootCause,
                factors,
                wear,
                "Proactive maintenance avoids sudden board-level failure and saves an estimated 60% compared to emergency board replacement.",
                risk.equals("CRITICAL") ? "Immediate attention required within 7 days" : "Moderate priority maintenance within 60 days",
                precautions,
                roadmap,
                HEURISTIC_MODEL_LABEL,
                isDemo,
                LocalDateTime.now().toString()
        );
    }

    private DiagnosisExplanationResponse generateHeuristicDiagnosisExplanation(DiagnosisReport report, boolean isDemo) {
        return new DiagnosisExplanationResponse(
                report.getId(),
                (report.getBrand() != null ? report.getBrand() : "") + " " + (report.getModel() != null ? report.getModel() : "Device"),
                report.getProbableIssue(),
                report.getConfidenceScore() != null ? report.getConfidenceScore() : 85,
                "Inspection reveals localized physical and electrical signatures consistent with " + report.getProbableIssue() + ". " +
                (report.getObservations() != null ? report.getObservations() : "Surface and connection integrity checked."),
                "Reported symptoms correlate directly with the primary power delivery and signal interconnect traces of the component.",
                List.of("Secondary flex ribbon cable misalignment", "Connector pin oxidation or micro-fracture"),
                "High repair feasibility: Component is modular and can be replaced with standard precision screwdriver sets and spudgers.",
                List.of("Precision Torx / Pentalobe drivers for chassis access", "Anti-static nylon spudger to safely disconnect battery flex"),
                report.getSafetyWarning() != null ? report.getSafetyWarning() : "Always disconnect battery prior to touching internal components.",
                HEURISTIC_MODEL_LABEL,
                isDemo,
                LocalDateTime.now().toString()
        );
    }

    private RecommendationExplanationResponse generateHeuristicRecommendationExplanation(
            AIRecommendation rec, DiagnosisReport report, boolean isDemo) {

        String action = rec.getAction() != null ? rec.getAction() : "REPAIR";
        double repairCost = report != null && report.getRepairCost() != null ? report.getRepairCost() : 120.0;
        double saved = rec.getMoneySaved() != null ? rec.getMoneySaved() : 500.0;

        return new RecommendationExplanationResponse(
                rec.getId(),
                report != null ? report.getBrand() + " " + report.getModel() : "Electronic Device",
                action,
                repairCost,
                850.0,
                String.format("Repairing this unit at $%s costs only %.0f%% of a new comparable replacement, preserving $%s in direct financial savings.",
                        repairCost, (repairCost / 850.0) * 100, saved),
                "Targeted component replacement restores functional baseline and extends usable service life by 2 to 3 years.",
                String.format("Averting replacement prevents %.1f kg of manufacturing greenhouse gas emissions and eliminates hazardous electronic waste.",
                        rec.getCarbonSaved() != null ? rec.getCarbonSaved() : 45.0),
                "Chassis, logic board, and display retain over 70% residual value once the failed modular component is refreshed.",
                List.of("Acquire certified replacement parts with minimum 90-day warranty", "Follow ESD-safe replacement guidelines", "Perform full hardware post-repair diagnostics"),
                HEURISTIC_MODEL_LABEL,
                isDemo,
                LocalDateTime.now().toString()
        );
    }

    private SustainabilityNarrativeResponse generateHeuristicSustainabilityNarrative(
            String userId, double totalCo2, double totalEwaste, double totalMoney, int extended, boolean isDemo) {

        double trees = totalCo2 / 22.0;
        double carKm = totalCo2 / 0.12;

        return new SustainabilityNarrativeResponse(
                userId,
                Math.round(totalCo2 * 100.0) / 100.0,
                Math.round(totalEwaste * 100.0) / 100.0,
                Math.round(totalMoney * 100.0) / 100.0,
                extended,
                String.format("You've diverted %.1f kg of e-waste and averted %.1f kg of CO₂ emissions!", totalEwaste, totalCo2),
                String.format("Your commitment to circular electronics has extended the lifespan of %d device(s) and kept $%.2f in your wallet. " +
                        "By repairing hardware instead of purchasing new replacements, you prevent energy-intensive mineral extraction and semiconductor fabrication emissions.",
                        extended, totalMoney),
                String.format("Your environmental impact is equivalent to planting %.1f mature trees absorbing carbon for a year, or eliminating %.0f kilometers of gasoline vehicle driving!",
                        trees, carKm),
                List.of("Zero E-Waste Pioneer", "Carbon Reduction Vanguard", "Right-to-Repair Advocate"),
                String.format("Maintaining this repair cadence will prevent another %.0f kg of carbon and save over $%.0f over the next 24 months.", totalCo2 * 1.5, totalMoney * 1.5),
                HEURISTIC_MODEL_LABEL,
                isDemo,
                LocalDateTime.now().toString()
        );
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private List<RiskFactorExplanation> parseRiskFactors(JsonNode node) {
        List<RiskFactorExplanation> list = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                list.add(new RiskFactorExplanation(
                        item.path("factorName").asText("Hardware Risk"),
                        item.path("severity").asText("MEDIUM"),
                        item.path("explanation").asText("Wear factor"),
                        item.path("impactOnLifespan").asText("Moderate impact")
                ));
            }
        }
        return list;
    }

    private List<ComponentWearDetail> parseComponentWear(JsonNode node) {
        List<ComponentWearDetail> list = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                list.add(new ComponentWearDetail(
                        item.path("component").asText("General Component"),
                        item.path("status").asText("Operational"),
                        item.path("wearMechanisms").asText("Standard aging"),
                        item.path("estimatedRemainingLife").asText("12-24 months")
                ));
            }
        }
        return list;
    }

    private List<String> parseStringList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                list.add(item.asText());
            }
        }
        return list;
    }
}
