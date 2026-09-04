package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.*;
import com.repairverse.ai.entity.*;
import com.repairverse.ai.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcosystemSimulationService {

    private final DigitalTwinSnapshotRepository snapshotRepository;
    private final DigitalTwinForecastRepository forecastRepository;
    private final DigitalTwinScenarioRepository scenarioRepository;
    private final DigitalTwinOptimizationResultRepository optimizationRepository;
    private final EcosystemSimulationEventRepository eventRepository;
    private final DeviceRepository deviceRepository;

    private final DigitalTwinStateService stateService;
    private final DeviceTrajectoryForecastService forecastService;
    private final DigitalTwinScenarioSimulationService scenarioService;
    private final RepairStrategyOptimizationService optimizationService;
    private final SimulationInsightService insightService;

    @Transactional
    public DigitalTwinDashboardResponse getDigitalTwin(String userId, String deviceId) {
        Device device = validateAndGetDevice(userId, deviceId);

        DigitalTwinSnapshot snapshot = snapshotRepository.findTopByDeviceIdOrderBySnapshotTimeDesc(deviceId)
                .orElseGet(() -> refreshDigitalTwinData(userId, device));

        List<ForecastResponse> forecasts = forecastRepository.findBySnapshotIdOrderByForecastHorizonMonthsAsc(snapshot.getId())
                .stream()
                .map(forecastService::mapToForecastResponse)
                .collect(Collectors.toList());

        if (forecasts.isEmpty()) {
            forecasts = forecastService.generateAndSaveForecasts(snapshot, device);
        }

        List<ScenarioResponse> scenarios = scenarioRepository.findByDeviceIdOrderByOverallOutcomeScoreDesc(deviceId)
                .stream()
                .map(scenarioService::mapToScenarioResponse)
                .collect(Collectors.toList());

        if (scenarios.isEmpty()) {
            scenarios = scenarioService.simulateAndSaveScenarios(userId, device, snapshot);
        }

        DigitalTwinOptimizationResult optimization = optimizationRepository.findTopByDeviceIdOrderByCreatedAtDesc(deviceId)
                .orElseGet(() -> {
                    OptimizationResponse optResp = optimizationService.optimizeAndSaveStrategy(userId, device, snapshot, null);
                    return optimizationRepository.findById(optResp.id()).orElse(null);
                });

        OptimizationResponse optResponse = mapToOptimizationResponse(optimization);

        List<SimulationEventResponse> events = getOrGenerateEvents(userId, device, snapshot, forecasts);
        List<SimulationInsight> insights = insightService.generateInsights(snapshot, scenarios, optimization, forecasts);

        return new DigitalTwinDashboardResponse(
                device.getId(),
                device.getDeviceName(),
                device.getCategory() != null ? device.getCategory() : "OTHER",
                mapToSnapshotResponse(snapshot, device),
                forecasts,
                scenarios,
                optResponse,
                events,
                insights,
                false
        );
    }

    @Transactional
    public DigitalTwinDashboardResponse refreshDigitalTwin(String userId, String deviceId) {
        Device device = validateAndGetDevice(userId, deviceId);
        DigitalTwinSnapshot snapshot = refreshDigitalTwinData(userId, device);

        List<ForecastResponse> forecasts = forecastService.generateAndSaveForecasts(snapshot, device);
        List<ScenarioResponse> scenarios = scenarioService.simulateAndSaveScenarios(userId, device, snapshot);
        OptimizationResponse optResponse = optimizationService.optimizeAndSaveStrategy(userId, device, snapshot, null);
        DigitalTwinOptimizationResult optimization = optimizationRepository.findById(optResponse.id()).orElse(null);

        List<SimulationEventResponse> events = getOrGenerateEvents(userId, device, snapshot, forecasts);
        List<SimulationInsight> insights = insightService.generateInsights(snapshot, scenarios, optimization, forecasts);

        return new DigitalTwinDashboardResponse(
                device.getId(),
                device.getDeviceName(),
                device.getCategory() != null ? device.getCategory() : "OTHER",
                mapToSnapshotResponse(snapshot, device),
                forecasts,
                scenarios,
                optResponse,
                events,
                insights,
                false
        );
    }

    @Transactional
    public DigitalTwinDashboardResponse simulateCustomScenario(String userId, String deviceId, RunSimulationRequest request) {
        Device device = validateAndGetDevice(userId, deviceId);
        DigitalTwinSnapshot snapshot = snapshotRepository.findTopByDeviceIdOrderBySnapshotTimeDesc(deviceId)
                .orElseGet(() -> refreshDigitalTwinData(userId, device));

        List<ForecastResponse> forecasts = forecastRepository.findBySnapshotIdOrderByForecastHorizonMonthsAsc(snapshot.getId())
                .stream()
                .map(forecastService::mapToForecastResponse)
                .collect(Collectors.toList());

        if (forecasts.isEmpty()) {
            forecasts = forecastService.generateAndSaveForecasts(snapshot, device);
        }

        List<ScenarioResponse> scenarios = scenarioService.simulateAndSaveScenarios(userId, device, snapshot);

        OptimizationRequest optReq = null;
        if (request != null) {
            optReq = new OptimizationRequest(
                    request.budget(),
                    request.targetLifespanMonths(),
                    request.prioritizeSustainability(),
                    request.prioritizeReliability(),
                    request.maxDowntimeDays()
            );
        }

        OptimizationResponse optResponse = optimizationService.optimizeAndSaveStrategy(userId, device, snapshot, optReq);
        DigitalTwinOptimizationResult optimization = optimizationRepository.findById(optResponse.id()).orElse(null);

        List<SimulationEventResponse> events = getOrGenerateEvents(userId, device, snapshot, forecasts);
        List<SimulationInsight> insights = insightService.generateInsights(snapshot, scenarios, optimization, forecasts);

        return new DigitalTwinDashboardResponse(
                device.getId(),
                device.getDeviceName(),
                device.getCategory() != null ? device.getCategory() : "OTHER",
                mapToSnapshotResponse(snapshot, device),
                forecasts,
                scenarios,
                optResponse,
                events,
                insights,
                true
        );
    }

    @Transactional(readOnly = true)
    public List<ForecastResponse> getForecasts(String userId, String deviceId) {
        validateAndGetDevice(userId, deviceId);
        return forecastRepository.findByDeviceIdOrderByForecastHorizonMonthsAsc(deviceId)
                .stream()
                .map(forecastService::mapToForecastResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeviceTrajectoryResponse getTrajectory(String userId, String deviceId) {
        Device device = validateAndGetDevice(userId, deviceId);
        List<DigitalTwinForecast> forecasts = forecastRepository.findByDeviceIdOrderByForecastHorizonMonthsAsc(deviceId);

        DigitalTwinSnapshot snapshot = snapshotRepository.findTopByDeviceIdOrderBySnapshotTimeDesc(deviceId)
                .orElse(null);

        List<DeviceTrajectoryPoint> points = new ArrayList<>();
        // Month 0 (Current)
        if (snapshot != null) {
            points.add(new DeviceTrajectoryPoint(
                    0,
                    snapshot.getHealthScore(),
                    snapshot.getFailureRiskScore(),
                    snapshot.getPredictedRepairCost(),
                    snapshot.getPredictedValue()
            ));
        }

        for (DigitalTwinForecast f : forecasts) {
            points.add(new DeviceTrajectoryPoint(
                    f.getForecastHorizonMonths(),
                    f.getPredictedHealthScore(),
                    f.getPredictedFailureRisk(),
                    f.getPredictedRepairCost(),
                    f.getPredictedDeviceValue()
            ));
        }

        return new DeviceTrajectoryResponse(device.getId(), device.getDeviceName(), points);
    }

    @Transactional(readOnly = true)
    public List<ScenarioResponse> getScenarios(String userId, String deviceId) {
        validateAndGetDevice(userId, deviceId);
        return scenarioRepository.findByDeviceIdOrderByOverallOutcomeScoreDesc(deviceId)
                .stream()
                .map(scenarioService::mapToScenarioResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OptimizationResponse optimizeStrategy(String userId, String deviceId, OptimizationRequest request) {
        Device device = validateAndGetDevice(userId, deviceId);
        DigitalTwinSnapshot snapshot = snapshotRepository.findTopByDeviceIdOrderBySnapshotTimeDesc(deviceId)
                .orElseGet(() -> refreshDigitalTwinData(userId, device));

        return optimizationService.optimizeAndSaveStrategy(userId, device, snapshot, request);
    }

    @Transactional(readOnly = true)
    public List<SimulationEventResponse> getSimulationEvents(String userId, String deviceId) {
        validateAndGetDevice(userId, deviceId);
        return eventRepository.findByDeviceIdOrderByProjectedMonthOffsetAsc(deviceId)
                .stream()
                .map(this::mapToEventResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EcosystemMetricsResponse getUserEcosystemDashboard(String userId) {
        List<Device> userDevices = deviceRepository.findByUserId(userId);
        if (userDevices.isEmpty()) {
            return new EcosystemMetricsResponse(0, 0.0, 0, 0.0, 100, 0);
        }

        List<DigitalTwinSnapshot> snapshots = snapshotRepository.findByUserId(userId);
        List<DigitalTwinOptimizationResult> optimizations = optimizationRepository.findByUserId(userId);

        int totalMonitored = userDevices.size();
        double totalSavings = optimizations.stream()
                .mapToDouble(o -> o.getEstimatedSavings() != null ? o.getEstimatedSavings() : 0.0)
                .sum();
        double totalCo2 = optimizations.stream()
                .mapToDouble(o -> o.getEstimatedCo2Savings() != null ? o.getEstimatedCo2Savings() : 0.0)
                .sum();
        int failuresPrevented = (int) snapshots.stream()
                .filter(s -> s.getFailureRiskScore() != null && s.getFailureRiskScore() > 40)
                .count();

        double avgHealth = snapshots.stream()
                .mapToInt(s -> s.getHealthScore() != null ? s.getHealthScore() : 80)
                .average()
                .orElse(85.0);

        return new EcosystemMetricsResponse(
                totalMonitored,
                Math.round(totalSavings * 100.0) / 100.0,
                Math.max(1, failuresPrevented),
                Math.round(totalCo2 * 10.0) / 10.0,
                (int) Math.round(avgHealth),
                snapshots.size()
        );
    }

    private DigitalTwinSnapshot refreshDigitalTwinData(String userId, Device device) {
        return stateService.buildAndSaveSnapshot(userId, device);
    }

    private List<SimulationEventResponse> getOrGenerateEvents(String userId, Device device, DigitalTwinSnapshot snapshot, List<ForecastResponse> forecasts) {
        List<EcosystemSimulationEvent> existing = eventRepository.findByDeviceIdOrderByProjectedMonthOffsetAsc(device.getId());
        if (!existing.isEmpty()) {
            return existing.stream().map(this::mapToEventResponse).collect(Collectors.toList());
        }

        List<EcosystemSimulationEvent> generated = new ArrayList<>();

        // Event 1: Today/Immediate
        generated.add(EcosystemSimulationEvent.builder()
                .userId(userId)
                .deviceId(device.getId())
                .eventType("OPTIMAL_INTERVENTION")
                .severity("INFO")
                .title("Digital Twin Initialized")
                .description("Baseline simulation calibrated against device health metrics and historical failure profiles.")
                .projectedMonthOffset(0)
                .estimatedFinancialImpact(0.0)
                .mitigationStrategy("PREVENTIVE_MAINTENANCE")
                .createdAt(LocalDateTime.now())
                .build());

        // Event 2: 3-6 Months Maintenance Check
        generated.add(EcosystemSimulationEvent.builder()
                .userId(userId)
                .deviceId(device.getId())
                .eventType("MAINTENANCE_DUE")
                .severity("MEDIUM")
                .title("Scheduled Component Inspection")
                .description("Thermal interface and mechanical wear threshold projected to reach maintenance interval.")
                .projectedMonthOffset(3)
                .estimatedFinancialImpact(1200.0)
                .mitigationStrategy("PREVENTIVE_MAINTENANCE")
                .createdAt(LocalDateTime.now())
                .build());

        // Event 3: 6-12 Months Failure Risk Escalation
        int risk = snapshot.getFailureRiskScore() != null ? snapshot.getFailureRiskScore() : 35;
        generated.add(EcosystemSimulationEvent.builder()
                .userId(userId)
                .deviceId(device.getId())
                .eventType("FAILURE_RISK_INCREASE")
                .severity(risk > 50 ? "HIGH" : "MEDIUM")
                .title("Primary Component Wear Warning")
                .description(String.format("Unattended operation is projected to escalate component failure probability by +%d%%.", Math.min(40, risk + 15)))
                .projectedMonthOffset(8)
                .estimatedFinancialImpact(3500.0)
                .mitigationStrategy("REPAIR_NOW")
                .createdAt(LocalDateTime.now())
                .build());

        // Event 4: 18-24 Months End-of-Life / Refurbishment Horizon
        generated.add(EcosystemSimulationEvent.builder()
                .userId(userId)
                .deviceId(device.getId())
                .eventType("SUSTAINABILITY_OPPORTUNITY")
                .severity("LOW")
                .title("Refurbishment / Circular Upgrade Window")
                .description("Opportunity to refurbish key modular components and extend operating lifespan by 18+ months.")
                .projectedMonthOffset(18)
                .estimatedFinancialImpact(4800.0)
                .mitigationStrategy("REFURBISH_DEVICE")
                .createdAt(LocalDateTime.now())
                .build());

        eventRepository.saveAll(generated);
        return generated.stream().map(this::mapToEventResponse).collect(Collectors.toList());
    }

    private Device validateAndGetDevice(String userId, String deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new IllegalArgumentException("Device not found: " + deviceId));
        if (!device.getUserId().equals(userId)) {
            throw new SecurityException("Unauthorized: Device does not belong to user");
        }
        return device;
    }

    private DigitalTwinSnapshotResponse mapToSnapshotResponse(DigitalTwinSnapshot snapshot, Device device) {
        return new DigitalTwinSnapshotResponse(
                snapshot.getId(),
                snapshot.getDeviceId(),
                device.getDeviceName(),
                device.getCategory() != null ? device.getCategory() : "OTHER",
                snapshot.getHealthScore(),
                snapshot.getFailureRiskScore(),
                snapshot.getMaintenanceScore(),
                snapshot.getRepairEconomicsScore(),
                snapshot.getLongevityScore(),
                snapshot.getSustainabilityScore(),
                snapshot.getPredictedValue(),
                snapshot.getPredictedRepairCost(),
                snapshot.getPredictedFailureProbability(),
                snapshot.getSimulationConfidence(),
                snapshot.getOverallEcosystemScore(),
                snapshot.getSnapshotTime() != null ? snapshot.getSnapshotTime().toString() : LocalDateTime.now().toString()
        );
    }

    private OptimizationResponse mapToOptimizationResponse(DigitalTwinOptimizationResult opt) {
        if (opt == null) {
            return new OptimizationResponse(
                    UUID.randomUUID().toString(),
                    "unknown",
                    "PREVENTIVE_MAINTENANCE",
                    85, 80, 85, 90, 85,
                    2500.0, 12, 18.5,
                    "Optimal multi-factor outcome balancing cost, reliability, and circular sustainability.",
                    LocalDateTime.now().toString()
            );
        }
        return new OptimizationResponse(
                opt.getId(),
                opt.getDeviceId(),
                opt.getRecommendedStrategy(),
                opt.getCostScore(),
                opt.getReliabilityScore(),
                opt.getLongevityScore(),
                opt.getSustainabilityScore(),
                opt.getOptimizationScore(),
                opt.getEstimatedSavings(),
                opt.getEstimatedLifespanGain(),
                opt.getEstimatedCo2Savings(),
                opt.getDecisionReason(),
                opt.getCreatedAt() != null ? opt.getCreatedAt().toString() : LocalDateTime.now().toString()
        );
    }

    private SimulationEventResponse mapToEventResponse(EcosystemSimulationEvent e) {
        return new SimulationEventResponse(
                e.getId(),
                e.getDeviceId(),
                e.getEventType(),
                e.getSeverity(),
                e.getTitle(),
                e.getDescription(),
                e.getProjectedMonthOffset(),
                e.getEstimatedFinancialImpact(),
                e.getMitigationStrategy(),
                e.getCreatedAt() != null ? e.getCreatedAt().toString() : LocalDateTime.now().toString()
        );
    }
}
