package com.repairverse.ai.service;

import com.repairverse.ai.dto.PredictiveMaintenanceDto.*;
import com.repairverse.ai.entity.FaultPattern;
import com.repairverse.ai.repository.FaultPatternRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing and querying the fault pattern library.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FaultPatternService {

    private final FaultPatternRepository faultPatternRepository;

    @Transactional(readOnly = true)
    public List<FaultPatternDto> getActivePatterns() {
        return faultPatternRepository.findByIsActiveTrueOrderByRiskWeightDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FaultPatternDto> getPatternsByCategory(String category) {
        return faultPatternRepository.findActiveByCategory(category)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FaultPatternDto> getPatternsForDevice(String category, String brand) {
        return faultPatternRepository.findActiveByCategoryAndBrand(category, brand)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private FaultPatternDto toDto(FaultPattern fp) {
        List<String> actions = List.of();
        if (fp.getPreventiveActions() != null && !fp.getPreventiveActions().isBlank()) {
            try {
                actions = Arrays.asList(fp.getPreventiveActions().split("\\|"));
            } catch (Exception e) {
                log.warn("Could not parse preventive actions for pattern {}", fp.getId());
            }
        }
        return new FaultPatternDto(
                fp.getId(),
                fp.getDeviceCategory(),
                fp.getDeviceBrand(),
                fp.getFaultType(),
                fp.getDescription(),
                fp.getMinDeviceAgeYears() != null ? fp.getMinDeviceAgeYears() : 0,
                fp.getHealthScoreThreshold() != null ? fp.getHealthScoreThreshold() : 60,
                fp.getRiskWeight() != null ? fp.getRiskWeight() : 5,
                fp.getTypicalCostMin(),
                fp.getTypicalCostMax(),
                actions,
                fp.getIsActive() != null && fp.getIsActive()
        );
    }
}
