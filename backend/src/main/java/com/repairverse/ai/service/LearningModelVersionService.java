package com.repairverse.ai.service;

import com.repairverse.ai.entity.IntelligenceModelVersion;
import com.repairverse.ai.repository.IntelligenceModelVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Phase 35: Learning Model Version Service.
 * Controls model version creation, lineage tracking, and atomic activation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LearningModelVersionService {

    private final IntelligenceModelVersionRepository modelRepository;

    /**
     * Get currently active intelligence model.
     */
    public IntelligenceModelVersion getActiveModel() {
        return modelRepository.findFirstByStatusOrderByActivatedAtDesc("ACTIVE")
                .orElseGet(this::createDefaultActiveModel);
    }

    /**
     * Create candidate model version incremented from current active.
     */
    @Transactional
    public IntelligenceModelVersion createCandidateVersion(int observationCount) {
        IntelligenceModelVersion active = getActiveModel();
        String candidateVersion = generateNextVersionString(active != null ? active.getVersion() : "R35.4");

        IntelligenceModelVersion candidate = IntelligenceModelVersion.builder()
                .modelName("RepairVerse Federated Core")
                .version(candidateVersion)
                .parentVersion(active != null ? active.getVersion() : "R35.3")
                .status("COLLECTING")
                .trainingObservations(observationCount)
                .validationScore(active != null ? active.getValidationScore() : 94.0)
                .trustScore(active != null ? active.getTrustScore() : 94)
                .improvementPercentage(0.0)
                .build();

        return modelRepository.save(candidate);
    }

    /**
     * Activate a validated candidate version, superseding prior active version.
     */
    @Transactional
    public IntelligenceModelVersion activateVersion(String versionString) {
        IntelligenceModelVersion candidate = modelRepository.findByVersion(versionString)
                .orElseThrow(() -> new IllegalArgumentException("Model version not found: " + versionString));

        if (!"APPROVED".equalsIgnoreCase(candidate.getStatus()) && !"VALIDATING".equalsIgnoreCase(candidate.getStatus())) {
            throw new IllegalStateException("Cannot activate unapproved model version in state: " + candidate.getStatus());
        }

        // Retire currently active models
        List<IntelligenceModelVersion> activeModels = modelRepository.findAllByStatus("ACTIVE");
        for (IntelligenceModelVersion oldActive : activeModels) {
            oldActive.setStatus("SUPERSEDED");
            oldActive.setRetiredAt(LocalDateTime.now());
            modelRepository.save(oldActive);
        }

        candidate.setStatus("ACTIVE");
        candidate.setActivatedAt(LocalDateTime.now());
        IntelligenceModelVersion saved = modelRepository.save(candidate);

        log.info("Model version '{}' successfully activated. Previous versions superseded.", versionString);
        return saved;
    }

    public List<IntelligenceModelVersion> getAllModelVersions() {
        return modelRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<IntelligenceModelVersion> findByVersion(String version) {
        return modelRepository.findByVersion(version);
    }

    private String generateNextVersionString(String current) {
        try {
            if (current != null && current.startsWith("R35.")) {
                int patch = Integer.parseInt(current.substring(4));
                return "R35." + (patch + 1);
            }
        } catch (Exception ignored) {}
        return "R35.5";
    }

    private IntelligenceModelVersion createDefaultActiveModel() {
        IntelligenceModelVersion defaultModel = IntelligenceModelVersion.builder()
                .modelName("RepairVerse Federated Core")
                .version("R35.4")
                .parentVersion("R35.3")
                .status("ACTIVE")
                .trainingObservations(1284)
                .validationScore(94.2)
                .trustScore(94)
                .improvementPercentage(8.7)
                .activatedAt(LocalDateTime.now())
                .build();
        return modelRepository.save(defaultModel);
    }
}
