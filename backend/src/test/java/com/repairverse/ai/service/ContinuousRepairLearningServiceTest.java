package com.repairverse.ai.service;

import com.repairverse.ai.dto.FederatedLearningDto.LearningRunResponse;
import com.repairverse.ai.entity.FederatedLearningBatch;
import com.repairverse.ai.entity.IntelligenceModelVersion;
import com.repairverse.ai.entity.RepairLearningSignal;
import com.repairverse.ai.repository.FederatedLearningBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContinuousRepairLearningServiceTest {

    @Mock
    private PrivacyPreservationService privacyService;

    @Mock
    private RepairLearningSignalService signalService;

    @Mock
    private LearningModelVersionService modelVersionService;

    @Mock
    private LearningValidationService validationService;

    @Mock
    private LearningKnowledgeGraphIntegrationService kgIntegrationService;

    @Mock
    private FederatedLearningBatchRepository batchRepository;

    private ContinuousRepairLearningService continuousLearningService;

    @BeforeEach
    void setUp() {
        continuousLearningService = new ContinuousRepairLearningService(
                privacyService,
                signalService,
                modelVersionService,
                validationService,
                kgIntegrationService,
                batchRepository
        );
    }

    @Test
    @DisplayName("Should run complete continuous learning cycle successfully")
    void testRunLearningCycleSuccess() {
        IntelligenceModelVersion activeModel = IntelligenceModelVersion.builder()
                .version("R35.4")
                .validationScore(94.0)
                .trustScore(94)
                .build();

        IntelligenceModelVersion candidateModel = IntelligenceModelVersion.builder()
                .version("R35.5")
                .validationScore(95.2)
                .trustScore(95)
                .build();

        when(modelVersionService.getActiveModel()).thenReturn(activeModel);
        when(batchRepository.save(any(FederatedLearningBatch.class))).thenAnswer(inv -> {
            FederatedLearningBatch b = inv.getArgument(0);
            b.setId("b-123");
            return b;
        });

        when(privacyService.processBatchPrivacy(any(), anyList()))
                .thenReturn(new PrivacyPreservationService.PrivacyBatchResult(List.of(Map.of("key", "val")), 10, 0, 5, PrivacyPreservationService.PrivacyDecision.ACCEPTED));

        List<RepairLearningSignal> signals = List.of(RepairLearningSignal.builder().observationCount(10).successRate(0.92).build());
        when(signalService.extractSignals(any(), anyList())).thenReturn(signals);

        when(modelVersionService.createCandidateVersion(anyInt())).thenReturn(candidateModel);

        when(validationService.validateCandidate(any(), any(), anyList()))
                .thenReturn(new LearningValidationService.CandidateValidationSummary(true, "APPROVED", 95.2, 1.2, 95, List.of()));

        LearningRunResponse response = continuousLearningService.runLearningCycle(null);

        assertTrue(response.success());
        assertTrue(response.validationPassed());
        assertEquals("R35.5", response.candidateVersion());
        verify(kgIntegrationService, times(1)).applyLearningToKnowledgeGraph(signals);
    }
}
