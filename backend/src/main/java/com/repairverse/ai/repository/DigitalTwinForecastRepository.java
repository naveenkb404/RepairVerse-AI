package com.repairverse.ai.repository;

import com.repairverse.ai.entity.DigitalTwinForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalTwinForecastRepository extends JpaRepository<DigitalTwinForecast, String> {

    List<DigitalTwinForecast> findBySnapshotIdOrderByForecastHorizonMonthsAsc(String snapshotId);

    List<DigitalTwinForecast> findByDeviceIdOrderByForecastHorizonMonthsAsc(String deviceId);

    void deleteByDeviceId(String deviceId);
}
