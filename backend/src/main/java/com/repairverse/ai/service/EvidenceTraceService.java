package com.repairverse.ai.service;

import com.repairverse.ai.entity.*;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Phase 34: Collects evidence signals from every upstream intelligence system
 * that contributed to an AI decision.  Each collector inspects the original
 * source record (diagnosis, prediction, digital twin snapshot, etc.) and
 * converts its key metrics into {@link AiDecisionEvidence} entries.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EvidenceTraceService {

    private final DeviceHealthRepository deviceHealthRepository;
    private final DevicePredictionRepository devicePredictionRepository;
    private final DigitalTwinSnapshotRepository digitalTwinSnapshotRepository;
    private final AutonomousInterventionRepository autonomousInterventionRepository;
    private final RepairKnowledgeNodeRepository repairKnowledgeNodeRepository;
    private final DiagnosisReportRepository diagnosisReportRepository;

    /**
     * Collect evidence from the originating intelligence system.
     *
     * @param sourceSystem    e.g. DIAGNOSIS, PREDICTIVE, DIGITAL_TWIN …
     * @param sourceRecordId  FK to the original record (may be null)
     * @param deviceId        device context
     * @return list of evidence entries (not yet persisted — caller persists)
     */
    public List<AiDecisionEvidence> collectEvidence(String sourceSystem,
                                                     String sourceRecordId,
                                                     String deviceId) {
        return switch (sourceSystem) {
            case "DIAGNOSIS" -> collectDiagnosisEvidence(sourceRecordId, deviceId);
            case "PREDICTIVE" -> collectPredictiveEvidence(deviceId);
            case "DEVICE_INTELLIGENCE" -> collectDeviceIntelligenceEvidence(deviceId);
            case "AUTONOMOUS_AGENT" -> collectAutonomousAgentEvidence(sourceRecordId);
            case "KNOWLEDGE_GRAPH" -> collectKnowledgeGraphEvidence(sourceRecordId);
            case "DIGITAL_TWIN" -> collectDigitalTwinEvidence(deviceId);
            case "CIRCULAR_ECONOMY" -> collectCircularEconomyEvidence(deviceId);
            default -> {
                log.warn("Unknown source system '{}' — returning empty evidence list.", sourceSystem);
                yield List.of();
            }
        };
    }

    // ─── Per-system collectors ──────────────────────────────────────────

    private List<AiDecisionEvidence> collectDiagnosisEvidence(String sourceRecordId, String deviceId) {
        List<AiDecisionEvidence> evidence = new ArrayList<>();
        if (sourceRecordId != null) {
            diagnosisReportRepository.findById(sourceRecordId).ifPresent(report -> {
                evidence.add(buildEvidence("DIAGNOSIS_SCORE", "Diagnosis Confidence",
                        String.valueOf(report.getConfidenceScore()), 0.9, "diagnosis_reports"));
                evidence.add(buildEvidence("FAULT_SEVERITY", "Fault Severity",
                        report.getSeverity() != null ? report.getSeverity() : "MEDIUM", 0.8, "diagnosis_reports"));
            });
        }
        addHealthEvidence(evidence, deviceId);
        return evidence;
    }

    private List<AiDecisionEvidence> collectPredictiveEvidence(String deviceId) {
        List<AiDecisionEvidence> evidence = new ArrayList<>();
        List<DevicePrediction> predictions = devicePredictionRepository
                .findByDeviceIdOrderByCreatedAtDesc(deviceId);
        if (!predictions.isEmpty()) {
            DevicePrediction latest = predictions.get(0);
            evidence.add(buildEvidence("FAILURE_PROBABILITY", "Predicted Failure Probability",
                    String.valueOf(latest.getFailureProbability()), 0.9, "device_predictions"));
            evidence.add(buildEvidence("RISK_LEVEL", "Predicted Risk Level",
                    latest.getRiskLevel() != null ? latest.getRiskLevel() : "MEDIUM", 0.7, "device_predictions"));
        }
        addHealthEvidence(evidence, deviceId);
        return evidence;
    }

    private List<AiDecisionEvidence> collectDeviceIntelligenceEvidence(String deviceId) {
        List<AiDecisionEvidence> evidence = new ArrayList<>();
        addHealthEvidence(evidence, deviceId);
        return evidence;
    }

    private List<AiDecisionEvidence> collectAutonomousAgentEvidence(String sourceRecordId) {
        List<AiDecisionEvidence> evidence = new ArrayList<>();
        if (sourceRecordId != null) {
            autonomousInterventionRepository.findById(sourceRecordId).ifPresent(intervention -> {
                evidence.add(buildEvidence("INTERVENTION_TYPE", "Intervention Type",
                        intervention.getInterventionType() != null ? intervention.getInterventionType() : "UNKNOWN",
                        0.8, "autonomous_interventions"));
                evidence.add(buildEvidence("URGENCY_SCORE", "Urgency Score",
                        String.valueOf(intervention.getUrgencyScore()), 0.9, "autonomous_interventions"));
                evidence.add(buildEvidence("INTERVENTION_STATUS", "Intervention Status",
                        intervention.getStatus() != null ? intervention.getStatus() : "PENDING",
                        0.5, "autonomous_interventions"));
            });
        }
        return evidence;
    }

    private List<AiDecisionEvidence> collectKnowledgeGraphEvidence(String sourceRecordId) {
        List<AiDecisionEvidence> evidence = new ArrayList<>();
        if (sourceRecordId != null) {
            repairKnowledgeNodeRepository.findById(sourceRecordId).ifPresent(node -> {
                evidence.add(buildEvidence("KNOWLEDGE_NODE", "Knowledge Node Type",
                        node.getNodeType() != null ? node.getNodeType() : "UNKNOWN",
                        0.7, "repair_knowledge_nodes"));
                evidence.add(buildEvidence("CONFIDENCE_SCORE", "Knowledge Confidence",
                        String.valueOf(node.getConfidenceScore()), 0.8, "repair_knowledge_nodes"));
            });
        }
        return evidence;
    }

    private List<AiDecisionEvidence> collectDigitalTwinEvidence(String deviceId) {
        List<AiDecisionEvidence> evidence = new ArrayList<>();
        Optional<DigitalTwinSnapshot> latest =
                digitalTwinSnapshotRepository.findTopByDeviceIdOrderBySnapshotTimeDesc(deviceId);
        latest.ifPresent(snapshot -> {
            evidence.add(buildEvidence("HEALTH_SCORE", "Digital Twin Health Score",
                    String.valueOf(snapshot.getHealthScore()), 0.9, "digital_twin_snapshots"));
            evidence.add(buildEvidence("FAILURE_RISK", "Twin Failure Risk Score",
                    String.valueOf(snapshot.getFailureRiskScore()), 0.8, "digital_twin_snapshots"));
            evidence.add(buildEvidence("SIMULATION_CONFIDENCE", "Simulation Confidence",
                    String.valueOf(snapshot.getSimulationConfidence()), 0.7, "digital_twin_snapshots"));
            evidence.add(buildEvidence("ECOSYSTEM_SCORE", "Overall Ecosystem Score",
                    String.valueOf(snapshot.getOverallEcosystemScore()), 0.6, "digital_twin_snapshots"));
        });
        return evidence;
    }

    private List<AiDecisionEvidence> collectCircularEconomyEvidence(String deviceId) {
        List<AiDecisionEvidence> evidence = new ArrayList<>();
        addHealthEvidence(evidence, deviceId);
        return evidence;
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    private void addHealthEvidence(List<AiDecisionEvidence> evidence, String deviceId) {
        deviceHealthRepository.findByDeviceId(deviceId).ifPresent(health -> {
            evidence.add(buildEvidence("HEALTH_SCORE", "Device Health Score",
                    String.valueOf(health.getHealthScore()), 0.85, "device_health"));
            evidence.add(buildEvidence("BATTERY_HEALTH", "Battery Health",
                    String.valueOf(health.getBatteryHealth()), 0.5, "device_health"));
        });
    }

    private AiDecisionEvidence buildEvidence(String type, String key, String value,
                                              double weight, String source) {
        return AiDecisionEvidence.builder()
                .evidenceType(type)
                .evidenceKey(key)
                .evidenceValue(value)
                .evidenceWeight(weight)
                .evidenceSource(source)
                .build();
    }
}
