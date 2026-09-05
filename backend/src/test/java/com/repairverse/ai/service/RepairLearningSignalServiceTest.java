package com.repairverse.ai.service;

import com.repairverse.ai.entity.FederatedLearningBatch;
import com.repairverse.ai.entity.RepairLearningSignal;
import com.repairverse.ai.repository.RepairLearningSignalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepairLearningSignalServiceTest {

    @Mock
    private RepairLearningSignalRepository signalRepository;

    @Mock
    private PrivacyPreservationService privacyService;

    private RepairLearningSignalService signalService;

    @BeforeEach
    void setUp() {
        signalService = new RepairLearningSignalService(signalRepository, privacyService);
    }

    @Test
    @DisplayName("Should calculate bounded success rate preventing wild oscillations")
    void testComputeBoundedRate() {
        double priorRate = 0.80;
        double observedPerfect = 1.0;
        double updated = signalService.computeBoundedRate(priorRate, observedPerfect);

        // weighted = 0.80 * 0.80 + 1.0 * 0.20 = 0.64 + 0.20 = 0.84 (delta = +0.04 <= 0.15)
        assertEquals(0.84, updated, 0.001);

        double observedZero = 0.0;
        double updatedDrop = signalService.computeBoundedRate(priorRate, observedZero);
        // weighted = 0.80 * 0.80 + 0 = 0.64 (delta = -0.16 -> constrained to -0.15 => 0.65)
        assertEquals(0.65, updatedDrop, 0.001);
    }

    @Test
    @DisplayName("Should extract signals only for groups satisfying privacy threshold")
    void testExtractSignalsThresholdEnforced() {
        FederatedLearningBatch batch = FederatedLearningBatch.builder().id("b-1").build();

        when(privacyService.isSafeToExposeSignal(anyInt())).thenAnswer(inv -> ((Integer) inv.getArgument(0)) >= 5);
        when(signalRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<Map<String, Object>> outcomes = new ArrayList<>();
        // 6 outcomes for Battery (Passes N>=5)
        for (int i = 0; i < 6; i++) {
            Map<String, Object> r = new HashMap<>();
            r.put("category", "SMARTPHONE");
            r.put("component", "BATTERY");
            r.put("failureMode", "DEGRADATION");
            r.put("action", "REPLACE_BATTERY");
            r.put("successful", true);
            outcomes.add(r);
        }
        // 2 outcomes for Screen (Fails N>=5)
        for (int i = 0; i < 2; i++) {
            Map<String, Object> r = new HashMap<>();
            r.put("category", "SMARTPHONE");
            r.put("component", "SCREEN");
            r.put("failureMode", "CRACK");
            r.put("action", "REPLACE_SCREEN");
            r.put("successful", true);
            outcomes.add(r);
        }

        List<RepairLearningSignal> signals = signalService.extractSignals(batch, outcomes);

        assertEquals(1, signals.size());
        assertEquals("BATTERY", signals.get(0).getComponentType());
        assertEquals(6, signals.get(0).getObservationCount());
    }
}
