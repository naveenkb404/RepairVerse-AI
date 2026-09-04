package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairKnowledgeGraphDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepairKnowledgeGraphService {

    private final RepairKnowledgeNodeRepository nodeRepository;
    private final RepairKnowledgeRelationshipRepository relationshipRepository;
    private final RepairPatternInsightRepository insightRepository;
    private final RepairKnowledgeFeedbackRepository feedbackRepository;
    private final DeviceRepository deviceRepository;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Fetch complete knowledge graph with nodes, relationships, and stats.
     */
    @Transactional
    public KnowledgeGraphResponse getKnowledgeGraph() {
        ensureInitialGraphData();

        List<RepairKnowledgeNode> nodes = nodeRepository.findAll();
        List<RepairKnowledgeRelationship> relationships = relationshipRepository.findAll();

        Map<String, RepairKnowledgeNode> nodeMap = nodes.stream()
                .collect(Collectors.toMap(RepairKnowledgeNode::getId, n -> n));

        List<KnowledgeNodeResponse> nodeResponses = nodes.stream()
                .map(this::mapToNodeResponse)
                .toList();

        List<KnowledgeRelationshipResponse> relationshipResponses = relationships.stream()
                .map(r -> mapToRelationshipResponse(r, nodeMap))
                .toList();

        KnowledgeGraphStatisticsResponse stats = getGraphStatistics();

        return new KnowledgeGraphResponse(
                nodeResponses,
                relationshipResponses,
                stats,
                LocalDateTime.now().format(ISO_FORMATTER)
        );
    }

    /**
     * Compute platform-wide knowledge graph statistics.
     */
    @Transactional(readOnly = true)
    public KnowledgeGraphStatisticsResponse getGraphStatistics() {
        long totalNodes = nodeRepository.count();
        long totalRelationships = relationshipRepository.count();
        long totalInsights = insightRepository.count();

        List<RepairKnowledgeNode> allNodes = nodeRepository.findAll();
        double avgConfidence = allNodes.stream()
                .mapToDouble(RepairKnowledgeNode::getConfidenceScore)
                .average()
                .orElse(0.85);

        Map<String, Long> nodeTypeDistribution = allNodes.stream()
                .collect(Collectors.groupingBy(RepairKnowledgeNode::getNodeType, Collectors.counting()));

        Map<String, Long> relTypeDistribution = relationshipRepository.findAll().stream()
                .collect(Collectors.groupingBy(RepairKnowledgeRelationship::getRelationshipType, Collectors.counting()));

        long totalObservations = allNodes.stream()
                .mapToLong(RepairKnowledgeNode::getObservationCount)
                .sum();

        return new KnowledgeGraphStatisticsResponse(
                totalNodes,
                totalRelationships,
                totalInsights,
                totalObservations,
                Math.round(avgConfidence * 100.0) / 100.0,
                nodeTypeDistribution,
                relTypeDistribution
        );
    }

    /**
     * Deterministic relationship strength calculation (0–100).
     * Frequency (30%) + Outcome Quality (25%) + Recency (15%) + Confidence (20%) + User Feedback (10%)
     */
    public double calculateRelationshipStrength(
            int observationCount,
            double outcomeQualityScore, // 0-100
            LocalDateTime lastObservedAt,
            double confidence, // 0.0 - 1.0
            double feedbackScore // 0-100
    ) {
        double frequencyScore = Math.min(100.0, observationCount * 10.0);
        long daysSinceObserved = lastObservedAt != null ? ChronoUnit.DAYS.between(lastObservedAt, LocalDateTime.now()) : 0;
        double recencyScore = Math.max(0.0, 100.0 - (daysSinceObserved * 1.5));
        double confidencePct = confidence * 100.0;

        double weightedScore = (frequencyScore * 0.30)
                + (outcomeQualityScore * 0.25)
                + (recencyScore * 0.15)
                + (confidencePct * 0.20)
                + (feedbackScore * 0.10);

        return Math.min(100.0, Math.max(0.0, Math.round(weightedScore * 10.0) / 10.0));
    }

    /**
     * Upsert a knowledge node deterministically.
     */
    @Transactional
    public RepairKnowledgeNode getOrCreateNode(String nodeType, String nodeKey, String displayName, String description) {
        return nodeRepository.findByNodeTypeAndNodeKey(nodeType, nodeKey)
                .map(existing -> {
                    existing.setObservationCount(existing.getObservationCount() + 1);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return nodeRepository.save(existing);
                })
                .orElseGet(() -> nodeRepository.save(RepairKnowledgeNode.builder()
                        .nodeType(nodeType)
                        .nodeKey(nodeKey)
                        .displayName(displayName)
                        .description(description)
                        .confidenceScore(0.85)
                        .observationCount(1)
                        .build()));
    }

    /**
     * Upsert a weighted relationship between two nodes.
     */
    @Transactional
    public RepairKnowledgeRelationship recordOrUpdateRelationship(
            RepairKnowledgeNode sourceNode,
            RepairKnowledgeNode targetNode,
            String relationshipType,
            double outcomeQuality,
            double feedbackScore
    ) {
        Optional<RepairKnowledgeRelationship> existingOpt =
                relationshipRepository.findBySourceNodeIdAndTargetNodeIdAndRelationshipType(
                        sourceNode.getId(), targetNode.getId(), relationshipType);

        LocalDateTime now = LocalDateTime.now();

        if (existingOpt.isPresent()) {
            RepairKnowledgeRelationship rel = existingOpt.get();
            rel.setObservationCount(rel.getObservationCount() + 1);
            rel.setLastObservedAt(now);
            double newStrength = calculateRelationshipStrength(
                    rel.getObservationCount(), outcomeQuality, now, rel.getConfidence(), feedbackScore);
            rel.setStrength(newStrength);
            rel.setUpdatedAt(now);
            return relationshipRepository.save(rel);
        } else {
            double initialStrength = calculateRelationshipStrength(
                    1, outcomeQuality, now, 0.85, feedbackScore);
            return relationshipRepository.save(RepairKnowledgeRelationship.builder()
                    .sourceNodeId(sourceNode.getId())
                    .targetNodeId(targetNode.getId())
                    .relationshipType(relationshipType)
                    .strength(initialStrength)
                    .confidence(0.85)
                    .observationCount(1)
                    .firstObservedAt(now)
                    .lastObservedAt(now)
                    .build());
        }
    }

    /**
     * Get knowledge profile for a specific device.
     */
    @Transactional(readOnly = true)
    public DeviceKnowledgeProfileResponse getDeviceKnowledgeProfile(String deviceId, String userId) {
        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found or unauthorized: " + deviceId));

        String category = device.getCategory() != null ? device.getCategory().toUpperCase() : "GENERAL";
        String modelKey = "MODEL:" + (device.getModel() != null ? device.getModel().toUpperCase().replaceAll("\\s+", "_") : "GENERIC");

        List<RepairKnowledgeNode> matchedNodes = new ArrayList<>();
        nodeRepository.findByNodeTypeAndNodeKey("DEVICE_MODEL", modelKey).ifPresent(matchedNodes::add);
        nodeRepository.findByNodeTypeAndNodeKey("DEVICE_CATEGORY", "CAT:" + category).ifPresent(matchedNodes::add);

        List<PatternInsightResponse> directInsights = insightRepository.findByDeviceCategoryAndStatusOrderByImpactScoreDesc(category, "ACTIVE")
                .stream()
                .map(this::mapToInsightResponse)
                .toList();

        return new DeviceKnowledgeProfileResponse(
                device.getId(),
                (device.getBrand() != null ? device.getBrand() + " " : "") + (device.getModel() != null ? device.getModel() : "Device"),
                category,
                matchedNodes.stream().map(this::mapToNodeResponse).toList(),
                directInsights,
                List.of(), // populated by similar case service in controller/facade
                List.of(), // populated by recommendation service in controller/facade
                directInsights.size()
        );
    }

    /**
     * Seeds initial knowledge graph structure if empty.
     */
    @Transactional
    public void ensureInitialGraphData() {
        if (nodeRepository.count() > 0) {
            return;
        }

        log.info("Seeding initial Repair Knowledge Graph domain model...");

        // Device Models & Categories
        RepairKnowledgeNode catLaptop = getOrCreateNode("DEVICE_CATEGORY", "CAT:LAPTOP", "Laptop Computer", "Portable computing hardware with integrated display and battery.");
        RepairKnowledgeNode catPhone = getOrCreateNode("DEVICE_CATEGORY", "CAT:SMARTPHONE", "Smartphone", "Mobile cellular device with capacitive OLED/LCD display.");
        RepairKnowledgeNode catAudio = getOrCreateNode("DEVICE_CATEGORY", "CAT:AUDIO", "Audio Equipment", "Headphones, earbuds, and acoustic reproduction units.");

        RepairKnowledgeNode modMacBook = getOrCreateNode("DEVICE_MODEL", "MODEL:MACBOOK_PRO_16", "MacBook Pro 16\"", "High performance laptop with Apple Silicon / M-series SoC.");
        RepairKnowledgeNode modIPhone = getOrCreateNode("DEVICE_MODEL", "MODEL:IPHONE_14_PRO", "iPhone 14 Pro", "Premium flagship smartphone with dynamic island and ceramic shield.");
        RepairKnowledgeNode modSony = getOrCreateNode("DEVICE_MODEL", "MODEL:SONY_WH1000XM4", "Sony WH-1000XM4", "Over-ear noise-cancelling headphones.");

        // Components
        RepairKnowledgeNode compBattery = getOrCreateNode("COMPONENT", "COMP:BATTERY_PACK", "Lithium-Ion Battery Pack", "Rechargeable electrochemical cell energy storage.");
        RepairKnowledgeNode compDisplay = getOrCreateNode("COMPONENT", "COMP:OLED_DISPLAY", "OLED Display Assembly", "High-density organic light-emitting diode touch digitizer panel.");
        RepairKnowledgeNode compThermal = getOrCreateNode("COMPONENT", "COMP:THERMAL_HEATSINK", "Thermal Heatsink & Fan Assembly", "Vapor chamber and cooling fan assembly.");
        RepairKnowledgeNode compEarpads = getOrCreateNode("COMPONENT", "COMP:EARPAD_CUSHIONS", "Acoustic Earpad Cushions", "Synthetic protein leather sound-isolating cushions.");

        // Symptoms
        RepairKnowledgeNode sympDrain = getOrCreateNode("SYMPTOM", "SYMP:FAST_BATTERY_DRAIN", "Rapid Battery Depletion", "Device operating runtime degraded by >40% below factory baseline.");
        RepairKnowledgeNode sympOverheat = getOrCreateNode("SYMPTOM", "SYMP:SYSTEM_OVERHEATING", "Thermal Throttling & High Heat", "Sustained chassis temperature exceeding 85°C with performance drops.");
        RepairKnowledgeNode sympTouchLag = getOrCreateNode("SYMPTOM", "SYMP:GHOST_TOUCH_MICROFRACTURE", "Display Ghost Touches / Cracks", "Intermittent touchscreen input errors following impact.");

        // Failure Modes
        RepairKnowledgeNode failDegradation = getOrCreateNode("FAILURE_MODE", "FAIL:ELECTROCHEMICAL_DEGRADATION", "Battery Electrochemical Wear", "Cell capacity dropped below 70% state of health with impedance surge.");
        RepairKnowledgeNode failThermalPaste = getOrCreateNode("FAILURE_MODE", "FAIL:THERMAL_INTERFACE_DRYOUT", "Thermal Paste Desiccation", "Thermal interface compound crystallized, impeding heat transfer.");
        RepairKnowledgeNode failSealFailure = getOrCreateNode("FAILURE_MODE", "FAIL:IP68_SEAL_BREACH", "Chassis Environmental Seal Breach", "Gasket adhesive dried or fractured, compromising ingress protection.");

        // Repair Actions
        RepairKnowledgeNode actBatterySwap = getOrCreateNode("REPAIR_ACTION", "ACT:OEM_BATTERY_REPLACEMENT", "OEM Battery Replacement", "Safe removal of spent battery pack and calibration of new OEM cell.");
        RepairKnowledgeNode actThermalRepaste = getOrCreateNode("REPAIR_ACTION", "ACT:THERMAL_REPASTE_CLEAN", "Thermal Repaste & Dust Removal", "Ultrasonic cleaning of cooling fins and application of high-conductivity paste.");
        RepairKnowledgeNode actScreenReseal = getOrCreateNode("REPAIR_ACTION", "ACT:DIGITIZER_UV_RESEAL", "Optical UV Glass Reseal", "Precision cleanroom reseal of digitizer glass and frame alignment.");

        // Outcomes
        RepairKnowledgeNode outRestored = getOrCreateNode("REPAIR_OUTCOME", "OUT:100_RESTORED", "Full Restoration (95-100% Health)", "Device fully restored to factory operating performance.");
        RepairKnowledgeNode outExtended = getOrCreateNode("REPAIR_OUTCOME", "OUT:LIFESPAN_EXTENDED_2YRS", "Lifespan Extended +24 Months", "Major components stabilized for additional 2+ years of reliable service.");

        // Build Relationships
        recordOrUpdateRelationship(modMacBook, compBattery, "HAS_COMPONENT", 95.0, 90.0);
        recordOrUpdateRelationship(modMacBook, compThermal, "HAS_COMPONENT", 90.0, 85.0);
        recordOrUpdateRelationship(modIPhone, compDisplay, "HAS_COMPONENT", 98.0, 95.0);
        recordOrUpdateRelationship(modSony, compEarpads, "HAS_COMPONENT", 92.0, 90.0);

        recordOrUpdateRelationship(compBattery, failDegradation, "INDICATES_FAILURE", 94.0, 90.0);
        recordOrUpdateRelationship(compThermal, failThermalPaste, "INDICATES_FAILURE", 88.0, 85.0);
        recordOrUpdateRelationship(failDegradation, sympDrain, "EXHIBITS_SYMPTOM", 96.0, 92.0);
        recordOrUpdateRelationship(failThermalPaste, sympOverheat, "EXHIBITS_SYMPTOM", 92.0, 88.0);

        recordOrUpdateRelationship(failDegradation, actBatterySwap, "RESOLVED_BY", 94.0, 95.0);
        recordOrUpdateRelationship(failThermalPaste, actThermalRepaste, "RESOLVED_BY", 91.0, 90.0);
        recordOrUpdateRelationship(actBatterySwap, outRestored, "RESULTED_IN", 96.0, 95.0);
        recordOrUpdateRelationship(actThermalRepaste, outExtended, "RESULTED_IN", 90.0, 88.0);

        log.info("Seeded initial knowledge nodes and weighted relationships successfully.");
    }

    public KnowledgeNodeResponse mapToNodeResponse(RepairKnowledgeNode node) {
        return new KnowledgeNodeResponse(
                node.getId(),
                node.getNodeType(),
                node.getNodeKey(),
                node.getDisplayName(),
                node.getDescription(),
                node.getMetadata(),
                node.getConfidenceScore(),
                node.getObservationCount()
        );
    }

    public KnowledgeRelationshipResponse mapToRelationshipResponse(
            RepairKnowledgeRelationship r, Map<String, RepairKnowledgeNode> nodeMap) {
        RepairKnowledgeNode source = nodeMap.get(r.getSourceNodeId());
        RepairKnowledgeNode target = nodeMap.get(r.getTargetNodeId());

        return new KnowledgeRelationshipResponse(
                r.getId(),
                r.getSourceNodeId(),
                source != null ? source.getDisplayName() : "Unknown Source",
                source != null ? source.getNodeType() : "UNKNOWN",
                r.getTargetNodeId(),
                target != null ? target.getDisplayName() : "Unknown Target",
                target != null ? target.getNodeType() : "UNKNOWN",
                r.getRelationshipType(),
                r.getStrength(),
                r.getConfidence(),
                r.getObservationCount()
        );
    }

    public PatternInsightResponse mapToInsightResponse(RepairPatternInsight i) {
        long helpfulVotes = feedbackRepository.countByInsightIdAndFeedbackType(i.getId(), "HELPFUL");
        long inaccurateVotes = feedbackRepository.countByInsightIdAndFeedbackType(i.getId(), "INACCURATE");

        return new PatternInsightResponse(
                i.getId(),
                i.getInsightType(),
                i.getTitle(),
                i.getDescription(),
                i.getConfidence(),
                i.getImpactScore(),
                i.getSupportingObservations(),
                i.getDeviceCategory(),
                i.getStatus(),
                i.getGeneratedAt() != null ? i.getGeneratedAt().format(ISO_FORMATTER) : LocalDateTime.now().format(ISO_FORMATTER),
                helpfulVotes,
                inaccurateVotes
        );
    }
}
