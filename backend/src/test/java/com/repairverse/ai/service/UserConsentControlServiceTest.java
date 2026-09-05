package com.repairverse.ai.service;

import com.repairverse.ai.dto.TrustEngineDto.UpdateAutonomyPreferencesRequest;
import com.repairverse.ai.dto.TrustEngineDto.UserAutonomyPreferencesResponse;
import com.repairverse.ai.entity.UserAutonomyPreference;
import com.repairverse.ai.repository.UserAutonomyPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserConsentControlServiceTest {

    @Mock
    private UserAutonomyPreferenceRepository preferenceRepository;

    @InjectMocks
    private UserConsentControlService consentControlService;

    private UserAutonomyPreference testPref;

    @BeforeEach
    void setUp() {
        testPref = UserAutonomyPreference.builder()
                .id("pref-1")
                .userId("usr-1")
                .allowAutonomousInterventions(true)
                .allowAutoScheduling(false)
                .allowProactiveAlerts(true)
                .minConfidenceThreshold(80)
                .requireApprovalAboveCost(5000.0)
                .notificationStyle("DETAILED")
                .build();
    }

    @Test
    @DisplayName("getPreferences returns existing preference")
    void testGetPreferences_Existing() {
        when(preferenceRepository.findByUserId("usr-1")).thenReturn(Optional.of(testPref));

        UserAutonomyPreferencesResponse response = consentControlService.getPreferences("usr-1");

        assertThat(response.userId()).isEqualTo("usr-1");
        assertThat(response.allowAutonomousInterventions()).isTrue();
        assertThat(response.minConfidenceThreshold()).isEqualTo(80);
    }

    @Test
    @DisplayName("getPreferences creates default when none exists")
    void testGetPreferences_CreatesDefault() {
        when(preferenceRepository.findByUserId("usr-new")).thenReturn(Optional.empty());
        when(preferenceRepository.save(any())).thenAnswer(inv -> {
            UserAutonomyPreference p = inv.getArgument(0);
            p.setId("pref-new");
            return p;
        });

        UserAutonomyPreferencesResponse response = consentControlService.getPreferences("usr-new");

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo("usr-new");
    }

    @Test
    @DisplayName("updatePreferences merges only non-null fields")
    void testUpdatePreferences() {
        when(preferenceRepository.findByUserId("usr-1")).thenReturn(Optional.of(testPref));
        when(preferenceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UpdateAutonomyPreferencesRequest request = new UpdateAutonomyPreferencesRequest(
                false, null, null, 90, null, "SUMMARY"
        );

        UserAutonomyPreferencesResponse response = consentControlService
                .updatePreferences("usr-1", request);

        assertThat(response.allowAutonomousInterventions()).isFalse();
        assertThat(response.allowAutoScheduling()).isFalse(); // unchanged
        assertThat(response.minConfidenceThreshold()).isEqualTo(90);
        assertThat(response.notificationStyle()).isEqualTo("SUMMARY");
    }

    @Test
    @DisplayName("isActionAllowed returns false when interventions disabled")
    void testIsActionAllowed_Disabled() {
        testPref.setAllowAutonomousInterventions(false);
        when(preferenceRepository.findByUserId("usr-1")).thenReturn(Optional.of(testPref));

        boolean allowed = consentControlService.isActionAllowed("usr-1", 1000, 90);

        assertThat(allowed).isFalse();
    }

    @Test
    @DisplayName("isActionAllowed returns false when confidence below threshold")
    void testIsActionAllowed_LowConfidence() {
        when(preferenceRepository.findByUserId("usr-1")).thenReturn(Optional.of(testPref));

        boolean allowed = consentControlService.isActionAllowed("usr-1", 1000, 70);

        assertThat(allowed).isFalse();
    }

    @Test
    @DisplayName("isActionAllowed returns false when cost exceeds approval threshold")
    void testIsActionAllowed_HighCost() {
        when(preferenceRepository.findByUserId("usr-1")).thenReturn(Optional.of(testPref));

        boolean allowed = consentControlService.isActionAllowed("usr-1", 10000, 90);

        assertThat(allowed).isFalse();
    }

    @Test
    @DisplayName("isActionAllowed returns true when all conditions met")
    void testIsActionAllowed_Success() {
        when(preferenceRepository.findByUserId("usr-1")).thenReturn(Optional.of(testPref));

        boolean allowed = consentControlService.isActionAllowed("usr-1", 3000, 90);

        assertThat(allowed).isTrue();
    }
}
