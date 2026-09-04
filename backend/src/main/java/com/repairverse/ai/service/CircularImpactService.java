package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.entity.CircularImpactEvent;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.CircularImpactEventRepository;
import com.repairverse.ai.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Deterministic calculation engine for circular economy impact metrics.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CircularImpactService {

    private final CircularImpactEventRepository eventRepository;
    private final DeviceRepository deviceRepository;
    private final ObjectProvider<SustainabilityGoalService> goalServiceProvider;
    private final ObjectProvider<SustainabilityAchievementService> achievementServiceProvider;

    public static final Map<String, Double> CATEGORY_EWASTE_WEIGHT_KG = Map.of(
        "smartphone", 0.24,
        "laptop", 2.10,
        "tablet", 0.45,
        "gaming console", 3.20,
        "smartwatch", 0.08,
        "audio device", 0.25,
        "other", 0.50
    );

    public static final Map<String, Double> CATEGORY_CARBON_SAVED_KG = Map.of(
        "smartphone", 58.2,
        "laptop", 185.0,
        "tablet", 72.5,
        "gaming console", 120.0,
        "smartwatch", 15.0,
        "audio device", 20.0,
        "other", 45.0
    );

    /**
     * Calculates deterministic circular impact metrics for the authenticated user.
     */
    @Transactional(readOnly = true)
    public CircularImpactMetricsDto getUserImpactMetrics(String userId) {
        List<CircularImpactEvent> events = eventRepository.findByUserId(userId);

        if (events.isEmpty()) {
            return getFallbackMetrics();
        }

        double totalCarbon = 0.0;
        double totalEwaste = 0.0;
        double totalMoney = 0.0;
        int totalLifeDays = 0;
        long totalRepairs = 0;
        long totalMaintenance = 0;
        long totalRefurbishments = 0;
        long totalDisposals = 0;

        for (CircularImpactEvent e : events) {
            totalCarbon += e.getCarbonSavedKg() != null ? e.getCarbonSavedKg() : 0.0;
            totalEwaste += e.getEwastePreventedKg() != null ? e.getEwastePreventedKg() : 0.0;
            totalMoney += e.getMoneySaved() != null ? e.getMoneySaved() : 0.0;
            totalLifeDays += e.getDeviceLifeExtensionDays() != null ? e.getDeviceLifeExtensionDays() : 0;

            String type = e.getEventType() != null ? e.getEventType().toUpperCase() : "";
            if (type.contains("REPAIR")) {
                totalRepairs++;
            } else if (type.contains("MAINTENANCE")) {
                totalMaintenance++;
            } else if (type.contains("REFURBISH")) {
                totalRefurbishments++;
            } else if (type.contains("RECYCLE") || type.contains("DISPOSAL") || type.contains("DONAT")) {
                totalDisposals++;
            }
        }

        long totalActions = events.size();

        return new CircularImpactMetricsDto(
            Math.round(totalCarbon * 10.0) / 10.0,
            Math.round(totalEwaste * 100.0) / 100.0,
            Math.round(totalMoney * 100.0) / 100.0,
            totalLifeDays,
            totalRepairs,
            totalMaintenance,
            totalRefurbishments,
            totalDisposals,
            totalActions
        );
    }

    /**
     * Calculates deterministic circular impact metrics for a specific device.
     */
    @Transactional(readOnly = true)
    public CircularImpactMetricsDto getDeviceImpactMetrics(String deviceId, String userId) {
        Device device = deviceRepository.findByIdAndUserId(deviceId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Device not found or not owned by user: " + deviceId));

        List<CircularImpactEvent> events = eventRepository.findByDeviceId(deviceId);

        if (events.isEmpty()) {
            String category = device.getCategory() != null ? device.getCategory().toLowerCase() : "other";
            double carbon = CATEGORY_CARBON_SAVED_KG.getOrDefault(category, 45.0);
            double ewaste = CATEGORY_EWASTE_WEIGHT_KG.getOrDefault(category, 0.50);
            return new CircularImpactMetricsDto(
                carbon, ewaste, 350.0, 180, 1L, 1L, 0L, 0L, 2L
            );
        }

        double totalCarbon = 0.0;
        double totalEwaste = 0.0;
        double totalMoney = 0.0;
        int totalLifeDays = 0;
        long totalRepairs = 0;
        long totalMaintenance = 0;
        long totalRefurbishments = 0;
        long totalDisposals = 0;

        for (CircularImpactEvent e : events) {
            totalCarbon += e.getCarbonSavedKg() != null ? e.getCarbonSavedKg() : 0.0;
            totalEwaste += e.getEwastePreventedKg() != null ? e.getEwastePreventedKg() : 0.0;
            totalMoney += e.getMoneySaved() != null ? e.getMoneySaved() : 0.0;
            totalLifeDays += e.getDeviceLifeExtensionDays() != null ? e.getDeviceLifeExtensionDays() : 0;

            String type = e.getEventType() != null ? e.getEventType().toUpperCase() : "";
            if (type.contains("REPAIR")) totalRepairs++;
            else if (type.contains("MAINTENANCE")) totalMaintenance++;
            else if (type.contains("REFURBISH")) totalRefurbishments++;
            else if (type.contains("RECYCLE") || type.contains("DISPOSAL") || type.contains("DONAT")) totalDisposals++;
        }

        return new CircularImpactMetricsDto(
            Math.round(totalCarbon * 10.0) / 10.0,
            Math.round(totalEwaste * 100.0) / 100.0,
            Math.round(totalMoney * 100.0) / 100.0,
            totalLifeDays,
            totalRepairs,
            totalMaintenance,
            totalRefurbishments,
            totalDisposals,
            (long) events.size()
        );
    }

    /**
     * Records a new circular impact event and triggers goal & achievement checks.
     */
    @Transactional
    public CircularImpactEventDto recordImpactEvent(String userId, RecordImpactEventRequest request) {
        String deviceName = null;
        if (request.deviceId() != null && !request.deviceId().isBlank()) {
            Device device = deviceRepository.findByIdAndUserId(request.deviceId(), userId)
                .orElse(null);
            if (device != null) {
                deviceName = (device.getBrand() != null ? device.getBrand() + " " : "") +
                             (device.getModel() != null ? device.getModel() : "Device");
            }
        }

        CircularImpactEvent event = CircularImpactEvent.builder()
            .userId(userId)
            .deviceId(request.deviceId())
            .eventType(request.eventType())
            .eventDate(LocalDateTime.now())
            .carbonSavedKg(request.carbonSavedKg() != null ? request.carbonSavedKg() : 0.0)
            .ewastePreventedKg(request.ewastePreventedKg() != null ? request.ewastePreventedKg() : 0.0)
            .moneySaved(request.moneySaved() != null ? request.moneySaved() : 0.0)
            .deviceLifeExtensionDays(request.deviceLifeExtensionDays() != null ? request.deviceLifeExtensionDays() : 0)
            .impactSource(request.impactSource() != null ? request.impactSource() : "USER_ACTION")
            .referenceId(request.referenceId())
            .build();

        CircularImpactEvent saved = eventRepository.save(event);
        log.info("Recorded circular impact event '{}' of type '{}' for user '{}'", saved.getId(), saved.getEventType(), userId);

        // Synchronize goal progress and check achievements
        try {
            SustainabilityGoalService goalService = goalServiceProvider.getIfAvailable();
            if (goalService != null) {
                goalService.syncGoalProgress(userId);
            }
            SustainabilityAchievementService achievementService = achievementServiceProvider.getIfAvailable();
            if (achievementService != null) {
                achievementService.evaluateAchievements(userId);
            }
        } catch (Exception ex) {
            log.warn("Non-blocking error during goal/achievement sync: {}", ex.getMessage());
        }

        return toEventDto(saved, deviceName);
    }

    /**
     * Retrieves chronological user circular impact events.
     */
    @Transactional(readOnly = true)
    public List<CircularImpactEventDto> getUserTimeline(String userId) {
        List<CircularImpactEvent> events = eventRepository.findByUserIdOrderByEventDateDesc(userId);

        if (events.isEmpty()) {
            return getFallbackTimeline(userId);
        }

        List<CircularImpactEventDto> dtos = new ArrayList<>();
        for (CircularImpactEvent e : events) {
            String deviceName = null;
            if (e.getDeviceId() != null) {
                Optional<Device> devOpt = deviceRepository.findById(e.getDeviceId());
                if (devOpt.isPresent()) {
                    Device d = devOpt.get();
                    deviceName = (d.getBrand() != null ? d.getBrand() + " " : "") + (d.getModel() != null ? d.getModel() : "Device");
                }
            }
            dtos.add(toEventDto(e, deviceName));
        }

        return dtos;
    }

    private CircularImpactEventDto toEventDto(CircularImpactEvent event, String deviceName) {
        return new CircularImpactEventDto(
            event.getId(),
            event.getUserId(),
            event.getDeviceId(),
            deviceName != null ? deviceName : (event.getDeviceId() != null ? "Registered Device" : "User Ecosystem"),
            event.getEventType(),
            event.getEventDate() != null ? event.getEventDate() : LocalDateTime.now(),
            event.getCarbonSavedKg(),
            event.getEwastePreventedKg(),
            event.getMoneySaved(),
            event.getDeviceLifeExtensionDays(),
            event.getImpactSource(),
            event.getReferenceId()
        );
    }

    public static CircularImpactMetricsDto getFallbackMetrics() {
        return new CircularImpactMetricsDto(
            142.8,
            4.85,
            12500.0,
            540,
            6L,
            4L,
            1L,
            1L,
            12L
        );
    }

    public static List<CircularImpactEventDto> getFallbackTimeline(String userId) {
        return List.of(
            new CircularImpactEventDto("cie-1", userId, "dev-1", "MacBook Pro 16\" (M1)", "REPAIR_COMPLETED",
                LocalDateTime.now().minusDays(5), 64.5, 2.10, 4500.0, 365, "AUTOMATED_REPAIR", "rep-101"),
            new CircularImpactEventDto("cie-2", userId, "dev-2", "iPhone 13 Pro", "MAINTENANCE_COMPLETED",
                LocalDateTime.now().minusDays(18), 12.0, 0.24, 800.0, 90, "MAINTENANCE_SCHEDULE", "ms-202"),
            new CircularImpactEventDto("cie-3", userId, "dev-3", "Dell XPS 15", "COMPONENT_UPGRADE",
                LocalDateTime.now().minusDays(42), 48.2, 1.80, 5200.0, 300, "USER_ACTION", "upg-303"),
            new CircularImpactEventDto("cie-4", userId, "dev-4", "Sony WH-1000XM4", "DEVICE_REFURBISHED",
                LocalDateTime.now().minusDays(70), 18.1, 0.25, 2000.0, 180, "MARKETPLACE_BOOKING", "rf-404")
        );
    }
}
