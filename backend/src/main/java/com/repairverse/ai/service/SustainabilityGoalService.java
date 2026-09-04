package com.repairverse.ai.service;

import com.repairverse.ai.dto.CircularEconomyDto.*;
import com.repairverse.ai.entity.CircularImpactEvent;
import com.repairverse.ai.entity.SustainabilityGoal;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.CircularImpactEventRepository;
import com.repairverse.ai.repository.SustainabilityGoalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Deterministic service for user sustainability goal management and progress tracking.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SustainabilityGoalService {

    private final SustainabilityGoalRepository goalRepository;
    private final CircularImpactEventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<SustainabilityGoalDto> getUserGoals(String userId) {
        List<SustainabilityGoal> goals = goalRepository.findByUserIdOrderByCreatedAtDesc(userId);

        if (goals.isEmpty()) {
            return getFallbackGoals(userId);
        }

        return goals.stream().map(this::toDto).toList();
    }

    @Transactional
    public SustainabilityGoalDto createGoal(String userId, CreateGoalRequest request) {
        SustainabilityGoal goal = SustainabilityGoal.builder()
            .userId(userId)
            .goalType(request.goalType().toUpperCase())
            .targetValue(request.targetValue())
            .currentValue(0.0)
            .startDate(LocalDateTime.now())
            .targetDate(request.targetDate() != null ? request.targetDate() : LocalDateTime.now().plusMonths(6))
            .status("ACTIVE")
            .build();

        SustainabilityGoal saved = goalRepository.save(goal);
        log.info("Created sustainability goal '{}' [{}] for user '{}'", saved.getId(), saved.getGoalType(), userId);

        // Recalculate based on existing history
        syncSingleGoalProgress(saved, userId);

        return toDto(saved);
    }

    @Transactional
    public SustainabilityGoalDto updateGoal(String userId, String goalId, UpdateGoalRequest request) {
        SustainabilityGoal goal = goalRepository.findByIdAndUserId(goalId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Sustainability goal not found with id: " + goalId));

        if (request.targetValue() != null && request.targetValue() > 0) {
            goal.setTargetValue(request.targetValue());
        }
        if (request.targetDate() != null) {
            goal.setTargetDate(request.targetDate());
        }
        if (request.status() != null && !request.status().isBlank()) {
            goal.setStatus(request.status().toUpperCase());
        }

        // Check if now completed
        if (goal.getCurrentValue() >= goal.getTargetValue()) {
            goal.setStatus("COMPLETED");
        }

        SustainabilityGoal updated = goalRepository.save(goal);
        log.info("Updated sustainability goal '{}' for user '{}'", updated.getId(), userId);

        return toDto(updated);
    }

    @Transactional
    public void deleteGoal(String userId, String goalId) {
        SustainabilityGoal goal = goalRepository.findByIdAndUserId(goalId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Sustainability goal not found with id: " + goalId));

        goalRepository.delete(goal);
        log.info("Deleted sustainability goal '{}' for user '{}'", goalId, userId);
    }

    @Transactional
    public void syncGoalProgress(String userId) {
        List<SustainabilityGoal> activeGoals = goalRepository.findByUserIdAndStatus(userId, "ACTIVE");
        for (SustainabilityGoal goal : activeGoals) {
            syncSingleGoalProgress(goal, userId);
        }
    }

    private void syncSingleGoalProgress(SustainabilityGoal goal, String userId) {
        List<CircularImpactEvent> events = eventRepository.findByUserId(userId);
        double total = 0.0;

        for (CircularImpactEvent e : events) {
            String type = goal.getGoalType().toUpperCase();
            switch (type) {
                case "CARBON_REDUCTION", "CARBON" -> total += (e.getCarbonSavedKg() != null ? e.getCarbonSavedKg() : 0.0);
                case "EWASTE_PREVENTION", "EWASTE" -> total += (e.getEwastePreventedKg() != null ? e.getEwastePreventedKg() : 0.0);
                case "DEVICE_LIFE_EXTENSION", "LIFE_EXTENSION" -> total += (e.getDeviceLifeExtensionDays() != null ? e.getDeviceLifeExtensionDays() : 0);
                case "REPAIR_COUNT", "REPAIR" -> {
                    if (e.getEventType() != null && e.getEventType().toUpperCase().contains("REPAIR")) {
                        total += 1.0;
                    }
                }
                case "MONEY_SAVED", "SAVINGS" -> total += (e.getMoneySaved() != null ? e.getMoneySaved() : 0.0);
                default -> total += 1.0;
            }
        }

        goal.setCurrentValue(Math.round(total * 100.0) / 100.0);
        if (goal.getCurrentValue() >= goal.getTargetValue()) {
            goal.setStatus("COMPLETED");
        }

        goalRepository.save(goal);
    }

    public SustainabilityGoalDto toDto(SustainabilityGoal goal) {
        double current = goal.getCurrentValue() != null ? goal.getCurrentValue() : 0.0;
        double target = goal.getTargetValue() != null ? goal.getTargetValue() : 1.0;
        int progress = (int) Math.min(100, Math.max(0, Math.round((current / target) * 100.0)));
        double remaining = Math.max(0.0, Math.round((target - current) * 100.0) / 100.0);
        boolean isCompleted = "COMPLETED".equalsIgnoreCase(goal.getStatus()) || current >= target;

        return new SustainabilityGoalDto(
            goal.getId(),
            goal.getUserId(),
            goal.getGoalType(),
            goal.getTargetValue(),
            current,
            progress,
            remaining,
            goal.getStartDate(),
            goal.getTargetDate(),
            goal.getStatus(),
            isCompleted
        );
    }

    public static List<SustainabilityGoalDto> getFallbackGoals(String userId) {
        return List.of(
            new SustainabilityGoalDto("sg-demo-1", userId, "CARBON_REDUCTION", 150.0, 114.5, 76, 35.5,
                LocalDateTime.now().minusMonths(2), LocalDateTime.now().plusMonths(4), "ACTIVE", false),
            new SustainabilityGoalDto("sg-demo-2", userId, "EWASTE_PREVENTION", 5.0, 4.2, 84, 0.8,
                LocalDateTime.now().minusMonths(3), LocalDateTime.now().plusMonths(3), "ACTIVE", false),
            new SustainabilityGoalDto("sg-demo-3", userId, "REPAIR_COUNT", 5.0, 5.0, 100, 0.0,
                LocalDateTime.now().minusMonths(5), LocalDateTime.now().minusDays(10), "COMPLETED", true)
        );
    }
}
