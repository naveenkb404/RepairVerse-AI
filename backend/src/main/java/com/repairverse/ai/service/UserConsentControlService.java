package com.repairverse.ai.service;

import com.repairverse.ai.dto.TrustEngineDto.UpdateAutonomyPreferencesRequest;
import com.repairverse.ai.dto.TrustEngineDto.UserAutonomyPreferencesResponse;
import com.repairverse.ai.entity.UserAutonomyPreference;
import com.repairverse.ai.repository.UserAutonomyPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Phase 34: Manages per-user autonomy consent and notification preferences.
 * Provides a check method for the autonomous agent to verify if an action is allowed.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserConsentControlService {

    private final UserAutonomyPreferenceRepository preferenceRepository;

    /**
     * Get current user preferences (creates defaults if none exist).
     */
    public UserAutonomyPreferencesResponse getPreferences(String userId) {
        UserAutonomyPreference pref = getOrCreatePreference(userId);
        return toResponse(pref);
    }

    /**
     * Update user autonomy preferences.
     */
    @Transactional
    public UserAutonomyPreferencesResponse updatePreferences(String userId,
                                                              UpdateAutonomyPreferencesRequest request) {
        UserAutonomyPreference pref = getOrCreatePreference(userId);

        if (request.allowAutonomousInterventions() != null) {
            pref.setAllowAutonomousInterventions(request.allowAutonomousInterventions());
        }
        if (request.allowAutoScheduling() != null) {
            pref.setAllowAutoScheduling(request.allowAutoScheduling());
        }
        if (request.allowProactiveAlerts() != null) {
            pref.setAllowProactiveAlerts(request.allowProactiveAlerts());
        }
        if (request.minConfidenceThreshold() != null) {
            pref.setMinConfidenceThreshold(
                    Math.max(0, Math.min(100, request.minConfidenceThreshold())));
        }
        if (request.requireApprovalAboveCost() != null) {
            pref.setRequireApprovalAboveCost(
                    Math.max(0, request.requireApprovalAboveCost()));
        }
        if (request.notificationStyle() != null) {
            String style = request.notificationStyle().toUpperCase();
            if ("DETAILED".equals(style) || "SUMMARY".equals(style) || "SILENT".equals(style)) {
                pref.setNotificationStyle(style);
            }
        }

        pref = preferenceRepository.save(pref);
        log.info("Updated autonomy preferences for user '{}'", userId);
        return toResponse(pref);
    }

    /**
     * Check if an autonomous action is allowed based on user consent settings.
     *
     * @param userId       the user
     * @param estimatedCost  estimated cost of the action (INR)
     * @param confidence   AI confidence level (0–100)
     * @return true if the action can proceed autonomously
     */
    public boolean isActionAllowed(String userId, double estimatedCost, int confidence) {
        UserAutonomyPreference pref = getOrCreatePreference(userId);

        if (!pref.getAllowAutonomousInterventions()) {
            log.debug("User '{}' has disabled autonomous interventions", userId);
            return false;
        }
        if (confidence < pref.getMinConfidenceThreshold()) {
            log.debug("Confidence {}% below user threshold {}%", confidence, pref.getMinConfidenceThreshold());
            return false;
        }
        if (estimatedCost > pref.getRequireApprovalAboveCost()) {
            log.debug("Cost ₹{} exceeds user approval threshold ₹{}",
                    estimatedCost, pref.getRequireApprovalAboveCost());
            return false;
        }
        return true;
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    private UserAutonomyPreference getOrCreatePreference(String userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserAutonomyPreference newPref = UserAutonomyPreference.builder()
                            .userId(userId)
                            .build();
                    return preferenceRepository.save(newPref);
                });
    }

    private UserAutonomyPreferencesResponse toResponse(UserAutonomyPreference pref) {
        return new UserAutonomyPreferencesResponse(
                pref.getId(),
                pref.getUserId(),
                pref.getAllowAutonomousInterventions(),
                pref.getAllowAutoScheduling(),
                pref.getAllowProactiveAlerts(),
                pref.getMinConfidenceThreshold(),
                pref.getRequireApprovalAboveCost(),
                pref.getNotificationStyle()
        );
    }
}
