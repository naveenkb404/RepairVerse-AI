package com.repairverse.ai.service;

import com.repairverse.ai.entity.IntelligenceModelVersion;
import com.repairverse.ai.entity.RepairLearningSignal;
import com.repairverse.ai.repository.LearningValidationResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LearningValidationServiceTest {

    @Mock
    private LearningValidationResultRepository validationRepository;

    private LearningValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new LearningValidationService(validationRepository);
    }

    @Test
    @DisplayName("Should approve candidate when validation accuracy and trust score increase")
    void testValidateCandidateSuccess() {
        IntelligenceModelVersion candidate = IntelligenceModelVersion.builder()
                .version("R35.5")
                .status("COLLECTING")
                .build();

        IntelligenceModelVersion baseline = IntelligenceModelVersion.builder()
                .version("R35.4")
                .validationScore(92.0)
                .trustScore(92)
                .build();

        RepairLearningSignal signal = RepairLearningSignal.builder()
                .successRate(0.96)
                .observationCount(20)
                .build();

        when(validationRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        var summary = validationService.validateCandidate(candidate, baseline, List.of(signal));

        assertTrue(summary.approved());
        assertEquals("APPROVED", summary.decision());
        assertTrue(candidate.getValidationScore() >= 92.0);
        assertTrue(summary.results().size() >= 4);
    }

    @Test
    @DisplayName("Should reject candidate when observations are below governance compliance threshold")
    void testValidateCandidateGovernanceReject() {
        IntelligenceModelVersion candidate = IntelligenceModelVersion.builder()
                .version("R35.5")
                .status("COLLECTING")
                .build();

        IntelligenceModelVersion baseline = IntelligenceModelVersion.builder()
                .version("R35.4")
                .validationScore(92.0)
                .trustScore(92)
                .build();

        // Signal with observation count < 5 fails governance check
        RepairLearningSignal signal = RepairLearningSignal.builder()
                .successRate(0.96)
                .observationCount(3)
                .build();

        when(validationRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        var summary = validationService.validateCandidate(candidate, baseline, List.of(signal));

        assertFalse(summary.approved());
        assertEquals("REJECTED", summary.decision());
    }
}
