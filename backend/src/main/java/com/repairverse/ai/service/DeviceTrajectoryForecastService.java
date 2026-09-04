package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.*;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DigitalTwinForecast;
import com.repairverse.ai.entity.DigitalTwinSnapshot;
import com.repairverse.ai.repository.DigitalTwinForecastRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceTrajectoryForecastService {

    private final DigitalTwinForecastRepository forecastRepository;

    private static final int[] HORIZONS = {3, 6, 12, 18, 24};

    /**
     * Generate multi-horizon trajectory forecasts for a snapshot and device.
     */
    @Transactional
    public List<ForecastResponse> generateAndSaveForecasts(DigitalTwinSnapshot snapshot, Device device) {
        forecastRepository.deleteByDeviceId(snapshot.getDeviceId());
        List<DigitalTwinForecast> forecasts = generateForecasts(snapshot);
        return forecasts.stream().map(this::mapToForecastResponse).collect(Collectors.toList());
    }

    /**
     * Generate multi-horizon trajectory forecasts for a snapshot.
     */
    @Transactional
    public List<DigitalTwinForecast> generateForecasts(DigitalTwinSnapshot snapshot) {
        List<DigitalTwinForecast> forecasts = new ArrayList<>();

        int baseHealth = snapshot.getHealthScore() != null ? snapshot.getHealthScore() : 80;
        int baseRisk = snapshot.getFailureRiskScore() != null ? snapshot.getFailureRiskScore() : 20;
        double baseCost = snapshot.getPredictedRepairCost() != null ? snapshot.getPredictedRepairCost() : 3500.0;
        double baseValue = snapshot.getPredictedValue() != null ? snapshot.getPredictedValue() : 50000.0;

        for (int months : HORIZONS) {
            int predHealth = Math.max(15, (int) Math.round(baseHealth - (months * 1.8)));
            int predRisk = Math.min(95, (int) Math.round(baseRisk + (months * 2.2)));
            double predCost = Math.round((baseCost * (1.0 + (months * 0.065))) * 10.0) / 10.0;
            double predValue = Math.max(400.0, Math.round((baseValue * Math.pow(0.95, months / 2.0)) * 10.0) / 10.0);
            int predLifespan = Math.max(3, 36 - months);
            double co2Impact = Math.round((predRisk * 0.12 + (months * 0.45)) * 10.0) / 10.0;
            double eWasteImpact = Math.round((predRisk > 70 ? 1.8 : 0.4) * 10.0) / 10.0;
            double confidence = Math.max(0.70, Math.round((0.95 - (months * 0.008)) * 100.0) / 100.0);

            DigitalTwinForecast f = DigitalTwinForecast.builder()
                    .snapshotId(snapshot.getId())
                    .deviceId(snapshot.getDeviceId())
                    .forecastHorizonMonths(months)
                    .predictedHealthScore(predHealth)
                    .predictedFailureRisk(predRisk)
                    .predictedRepairCost(predCost)
                    .predictedDeviceValue(predValue)
                    .predictedRemainingLifespanMonths(predLifespan)
                    .predictedCo2Impact(co2Impact)
                    .predictedEWasteImpact(eWasteImpact)
                    .forecastConfidence(confidence)
                    .build();

            forecasts.add(f);
        }

        List<DigitalTwinForecast> saved = forecastRepository.saveAll(forecasts);
        log.info("Generated {} multi-horizon forecasts for device '{}'", saved.size(), snapshot.getDeviceId());
        return saved;
    }

    /**
     * Build discrete trajectory points for charts (0, 3, 6, 12, 18, 24 months).
     */
    public DeviceTrajectoryResponse buildTrajectoryResponse(
            DigitalTwinSnapshot snapshot, List<DigitalTwinForecast> forecasts, String deviceName) {

        List<DeviceTrajectoryPoint> points = new ArrayList<>();

        // Month 0 (Current State)
        points.add(new DeviceTrajectoryPoint(
                0,
                snapshot.getHealthScore(),
                snapshot.getFailureRiskScore(),
                snapshot.getPredictedRepairCost(),
                snapshot.getPredictedValue()
        ));

        for (DigitalTwinForecast f : forecasts) {
            points.add(new DeviceTrajectoryPoint(
                    f.getForecastHorizonMonths(),
                    f.getPredictedHealthScore(),
                    f.getPredictedFailureRisk(),
                    f.getPredictedRepairCost(),
                    f.getPredictedDeviceValue()
            ));
        }

        return new DeviceTrajectoryResponse(
                snapshot.getDeviceId(),
                deviceName,
                points
        );
    }

    public ForecastResponse mapToForecastResponse(DigitalTwinForecast f) {
        return new ForecastResponse(
                f.getId(),
                f.getSnapshotId(),
                f.getDeviceId(),
                f.getForecastHorizonMonths(),
                f.getPredictedHealthScore(),
                f.getPredictedFailureRisk(),
                f.getPredictedRepairCost(),
                f.getPredictedDeviceValue(),
                f.getPredictedRemainingLifespanMonths(),
                f.getPredictedCo2Impact(),
                f.getPredictedEWasteImpact(),
                f.getForecastConfidence()
        );
    }
}
