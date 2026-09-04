package com.repairverse.ai.service;

import com.repairverse.ai.dto.DigitalTwinDto.ForecastResponse;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.DigitalTwinSnapshot;
import com.repairverse.ai.repository.DigitalTwinForecastRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceTrajectoryForecastServiceTest {

    @Mock
    private DigitalTwinForecastRepository forecastRepository;

    @InjectMocks
    private DeviceTrajectoryForecastService forecastService;

    private Device testDevice;
    private DigitalTwinSnapshot testSnapshot;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("MacBook Pro M1")
                .category("LAPTOP")
                .purchasePrice(80000.0)
                .build();

        testSnapshot = DigitalTwinSnapshot.builder()
                .id("snap-1")
                .userId("usr-1")
                .deviceId("dev-1")
                .healthScore(85)
                .failureRiskScore(25)
                .maintenanceScore(80)
                .predictedValue(75000.0)
                .predictedRepairCost(3000.0)
                .simulationConfidence(0.90)
                .overallEcosystemScore(85)
                .build();
    }

    @Test
    @DisplayName("Generate forecasts across all 5 horizons (3, 6, 12, 18, 24 months)")
    void testGenerateAndSaveForecasts() {
        when(forecastRepository.saveAll(anyList())).thenAnswer(i -> i.getArgument(0));

        List<ForecastResponse> forecasts = forecastService.generateAndSaveForecasts(testSnapshot, testDevice);

        assertThat(forecasts).hasSize(5);

        // Verify horizon ordering
        assertThat(forecasts.get(0).forecastHorizonMonths()).isEqualTo(3);
        assertThat(forecasts.get(1).forecastHorizonMonths()).isEqualTo(6);
        assertThat(forecasts.get(2).forecastHorizonMonths()).isEqualTo(12);
        assertThat(forecasts.get(3).forecastHorizonMonths()).isEqualTo(18);
        assertThat(forecasts.get(4).forecastHorizonMonths()).isEqualTo(24);

        // Health should decay over time, failure risk should increase
        assertThat(forecasts.get(0).predictedHealthScore()).isGreaterThan(forecasts.get(4).predictedHealthScore());
        assertThat(forecasts.get(0).predictedFailureRisk()).isLessThan(forecasts.get(4).predictedFailureRisk());

        // Device value depreciates over time
        assertThat(forecasts.get(0).predictedDeviceValue()).isGreaterThan(forecasts.get(4).predictedDeviceValue());

        verify(forecastRepository, times(1)).deleteByDeviceId("dev-1");
        verify(forecastRepository, times(1)).saveAll(anyList());
    }
}
