package com.repairverse.ai.service;

import com.repairverse.ai.entity.FederatedLearningBatch;
import com.repairverse.ai.entity.PrivacyAuditEvent;
import com.repairverse.ai.repository.PrivacyAuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Phase 35: Privacy Preservation Service.
 * Enforces zero-PII transmission, minimum group aggregation (N >= 5),
 * deterministic pseudo-anonymization, and audit event recording.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrivacyPreservationService {

    public static final int MIN_AGGREGATION_COUNT = 5;

    private final PrivacyAuditEventRepository privacyAuditRepository;

    public enum PrivacyLevel {
        STRICT,
        STANDARD,
        AGGREGATED
    }

    public enum PrivacyDecision {
        ACCEPTED,
        FILTERED,
        QUARANTINED
    }

    /**
     * Anonymize a raw device/user identifier into a one-way salt-hashed reference.
     */
    public String anonymizeIdentifier(String rawIdentifier, String salt) {
        if (rawIdentifier == null || rawIdentifier.isBlank()) {
            return "anon-null";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = (salt != null ? salt : "rv-salt-2026") + ":" + rawIdentifier;
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return "anon-" + hexString.substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            return "anon-" + Math.abs(rawIdentifier.hashCode());
        }
    }

    /**
     * Scrub sensitive personal identification fields (emails, phone numbers, exact serials).
     */
    public Map<String, Object> scrubSensitiveFields(Map<String, Object> record) {
        if (record == null) return Collections.emptyMap();
        Map<String, Object> sanitized = new HashMap<>(record);
        sanitized.remove("email");
        sanitized.remove("userEmail");
        sanitized.remove("phoneNumber");
        sanitized.remove("phone");
        sanitized.remove("address");
        sanitized.remove("serialNumber");
        sanitized.remove("imei");
        sanitized.remove("password");
        sanitized.remove("token");
        sanitized.remove("latitude");
        sanitized.remove("longitude");
        return sanitized;
    }

    /**
     * Evaluate aggregation threshold. Returns true if observation count >= MIN_AGGREGATION_COUNT.
     */
    public boolean isSafeToExposeSignal(int observationCount) {
        return observationCount >= MIN_AGGREGATION_COUNT;
    }

    /**
     * Filter a collection of raw outcomes and enforce privacy rules.
     */
    @Transactional
    public PrivacyBatchResult processBatchPrivacy(
            FederatedLearningBatch batch,
            List<Map<String, Object>> rawOutcomes) {

        if (rawOutcomes == null || rawOutcomes.isEmpty()) {
            recordAuditEvent(batch, "EMPTY_BATCH_EVALUATION", "MIN_THRESHOLD_CHECK", 0, 0, 0, 0);
            return new PrivacyBatchResult(Collections.emptyList(), 0, 0, 0, PrivacyDecision.ACCEPTED);
        }

        int totalProcessed = rawOutcomes.size();
        int sensitiveFieldsRemoved = 0;
        int filteredCount = 0;
        List<Map<String, Object>> sanitizedList = new ArrayList<>();

        for (Map<String, Object> raw : rawOutcomes) {
            Map<String, Object> scrubbed = scrubSensitiveFields(raw);
            sensitiveFieldsRemoved += (raw.size() - scrubbed.size());

            // Anonymize device/user ID
            if (scrubbed.containsKey("deviceId")) {
                scrubbed.put("anonymizedDeviceId", anonymizeIdentifier(String.valueOf(scrubbed.get("deviceId")), batch.getId()));
                scrubbed.remove("deviceId");
            }
            if (scrubbed.containsKey("userId")) {
                scrubbed.put("anonymizedUserId", anonymizeIdentifier(String.valueOf(scrubbed.get("userId")), batch.getId()));
                scrubbed.remove("userId");
            }

            sanitizedList.add(scrubbed);
        }

        int aggregatedCount = sanitizedList.size();
        PrivacyDecision decision = aggregatedCount >= MIN_AGGREGATION_COUNT ? PrivacyDecision.ACCEPTED : PrivacyDecision.QUARANTINED;

        recordAuditEvent(
                batch,
                "BATCH_PRIVACY_ENFORCEMENT",
                "PII_STRIPPING_AND_N5_THRESHOLD",
                totalProcessed,
                filteredCount,
                aggregatedCount,
                sensitiveFieldsRemoved
        );

        log.info("Privacy audit complete for batch '{}': {} records processed, {} fields stripped, decision '{}'",
                batch != null ? batch.getBatchReference() : "adhoc",
                totalProcessed, sensitiveFieldsRemoved, decision);

        return new PrivacyBatchResult(sanitizedList, totalProcessed, filteredCount, sensitiveFieldsRemoved, decision);
    }

    @Transactional
    public void recordAuditEvent(
            FederatedLearningBatch batch,
            String eventType,
            String privacyRule,
            int processed,
            int filtered,
            int aggregated,
            int sensitiveRemoved) {
        PrivacyAuditEvent event = PrivacyAuditEvent.builder()
                .batch(batch)
                .eventType(eventType)
                .privacyRule(privacyRule)
                .recordsProcessed(processed)
                .recordsFiltered(filtered)
                .recordsAggregated(aggregated)
                .sensitiveFieldsRemoved(sensitiveRemoved)
                .build();
        privacyAuditRepository.save(event);
    }

    public record PrivacyBatchResult(
            List<Map<String, Object>> sanitizedRecords,
            int recordsProcessed,
            int recordsFiltered,
            int sensitiveFieldsRemoved,
            PrivacyDecision decision
    ) {}
}
