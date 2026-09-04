package com.repairverse.ai.service;

import com.repairverse.ai.entity.Device;
import com.repairverse.ai.service.PersonalizedDeviceAdvisorService.AdvisorNarrative;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalizedDeviceAdvisorServiceTest {

    private PersonalizedDeviceAdvisorService advisorService;
    private Device sampleDevice;

    @BeforeEach
    void setUp() {
        advisorService = new PersonalizedDeviceAdvisorService();
        sampleDevice = Device.builder()
                .id("dev-1")
                .userId("usr-1")
                .deviceName("MacBook Pro M2")
                .category("laptop")
                .brand("Apple")
                .model("A2779")
                .purchasePrice(1999.0)
                .build();
    }

    @Test
    @DisplayName("Generate narrative for CONTINUE_USING")
    void testContinueUsing() {
        AdvisorNarrative narrative = advisorService.generateNarrative(
                sampleDevice, "CONTINUE_USING", 88, "HEALTHY", 90, 15, 0.0, 1999.0, 36.0
        );

        assertThat(narrative.summary()).contains("MacBook Pro M2");
        assertThat(narrative.summary()).contains("exceptionally well");
        assertThat(narrative.smartDecision().recommendedAction()).isEqualTo("CONTINUE_USING");
        assertThat(narrative.smartDecision().priority()).isEqualTo("LOW");
    }

    @Test
    @DisplayName("Generate narrative for REPAIR_NOW")
    void testRepairNow() {
        AdvisorNarrative narrative = advisorService.generateNarrative(
                sampleDevice, "REPAIR_NOW", 65, "STABLE", 45, 65, 180.0, 1999.0, 36.0
        );

        assertThat(narrative.summary()).contains("economically and environmentally prime for repair");
        assertThat(narrative.smartDecision().recommendedAction()).isEqualTo("REPAIR_NOW");
        assertThat(narrative.smartDecision().priority()).isEqualTo("HIGH");
        assertThat(narrative.smartDecision().estimatedCost()).isEqualTo(180.0);
    }

    @Test
    @DisplayName("Generate narrative for PROFESSIONAL_SERVICE")
    void testProfessionalService() {
        AdvisorNarrative narrative = advisorService.generateNarrative(
                sampleDevice, "PROFESSIONAL_SERVICE", 35, "CRITICAL", 25, 85, 350.0, 1999.0, 36.0
        );

        assertThat(narrative.summary()).contains("urgent professional servicing");
        assertThat(narrative.smartDecision().priority()).isEqualTo("URGENT");
    }

    @Test
    @DisplayName("Generate narrative for RECYCLE")
    void testRecycle() {
        AdvisorNarrative narrative = advisorService.generateNarrative(
                sampleDevice, "RECYCLE", 25, "CRITICAL", 15, 90, 0.0, 1999.0, 36.0
        );

        assertThat(narrative.summary()).contains("beyond practical restoration");
        assertThat(narrative.smartDecision().recommendedAction()).isEqualTo("RECYCLE");
    }

    @Test
    @DisplayName("Handle null device gracefully")
    void testNullDevice() {
        AdvisorNarrative narrative = advisorService.generateNarrative(
                null, "CONTINUE_USING", 85, "HEALTHY", 85, 10, 0.0, 500.0, 15.0
        );

        assertThat(narrative.summary()).contains("Your device");
        assertThat(narrative.smartDecision()).isNotNull();
    }
}
