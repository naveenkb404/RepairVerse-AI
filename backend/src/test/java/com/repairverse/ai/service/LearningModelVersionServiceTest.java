package com.repairverse.ai.service;

import com.repairverse.ai.entity.IntelligenceModelVersion;
import com.repairverse.ai.repository.IntelligenceModelVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearningModelVersionServiceTest {

    @Mock
    private IntelligenceModelVersionRepository modelRepository;

    private LearningModelVersionService versionService;

    @BeforeEach
    void setUp() {
        versionService = new LearningModelVersionService(modelRepository);
    }

    @Test
    @DisplayName("Should create candidate version incremented from active baseline")
    void testCreateCandidateVersion() {
        IntelligenceModelVersion active = IntelligenceModelVersion.builder()
                .version("R35.4")
                .status("ACTIVE")
                .validationScore(94.0)
                .trustScore(94)
                .build();

        when(modelRepository.findFirstByStatusOrderByActivatedAtDesc("ACTIVE")).thenReturn(Optional.of(active));
        when(modelRepository.save(any(IntelligenceModelVersion.class))).thenAnswer(inv -> inv.getArgument(0));

        IntelligenceModelVersion candidate = versionService.createCandidateVersion(500);

        assertNotNull(candidate);
        assertEquals("R35.5", candidate.getVersion());
        assertEquals("R35.4", candidate.getParentVersion());
        assertEquals("COLLECTING", candidate.getStatus());
        assertEquals(500, candidate.getTrainingObservations());
    }

    @Test
    @DisplayName("Should activate approved model version and supersede previous active versions")
    void testActivateVersion() {
        IntelligenceModelVersion oldActive = IntelligenceModelVersion.builder()
                .version("R35.4")
                .status("ACTIVE")
                .build();

        IntelligenceModelVersion candidate = IntelligenceModelVersion.builder()
                .version("R35.5")
                .status("APPROVED")
                .build();

        when(modelRepository.findByVersion("R35.5")).thenReturn(Optional.of(candidate));
        when(modelRepository.findAllByStatus("ACTIVE")).thenReturn(List.of(oldActive));
        when(modelRepository.save(any(IntelligenceModelVersion.class))).thenAnswer(inv -> inv.getArgument(0));

        IntelligenceModelVersion activated = versionService.activateVersion("R35.5");

        assertEquals("ACTIVE", activated.getStatus());
        assertEquals("SUPERSEDED", oldActive.getStatus());
        assertNotNull(activated.getActivatedAt());
        assertNotNull(oldActive.getRetiredAt());
    }
}
