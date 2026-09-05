package com.repairverse.ai.service;

import com.repairverse.ai.entity.FederatedLearningBatch;
import com.repairverse.ai.entity.PrivacyAuditEvent;
import com.repairverse.ai.repository.PrivacyAuditEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrivacyPreservationServiceTest {

    @Mock
    private PrivacyAuditEventRepository privacyAuditRepository;

    private PrivacyPreservationService privacyService;

    @BeforeEach
    void setUp() {
        privacyService = new PrivacyPreservationService(privacyAuditRepository);
    }

    @Test
    @DisplayName("Should scrub direct PII fields (email, phone, serials, coordinates)")
    void testScrubSensitiveFields() {
        Map<String, Object> raw = new HashMap<>();
        raw.put("deviceId", "dev-123");
        raw.put("email", "john@example.com");
        raw.put("phoneNumber", "+1-555-0199");
        raw.put("serialNumber", "SN-998877");
        raw.put("latitude", 37.7749);
        raw.put("category", "SMARTPHONE");

        Map<String, Object> scrubbed = privacyService.scrubSensitiveFields(raw);

        assertEquals("dev-123", scrubbed.get("deviceId"));
        assertEquals("SMARTPHONE", scrubbed.get("category"));
        assertFalse(scrubbed.containsKey("email"));
        assertFalse(scrubbed.containsKey("phoneNumber"));
        assertFalse(scrubbed.containsKey("serialNumber"));
        assertFalse(scrubbed.containsKey("latitude"));
    }

    @Test
    @DisplayName("Should anonymize identifier with deterministic hashing")
    void testAnonymizeIdentifier() {
        String raw = "device-uuid-999";
        String anon1 = privacyService.anonymizeIdentifier(raw, "salt-1");
        String anon2 = privacyService.anonymizeIdentifier(raw, "salt-1");
        String anonOther = privacyService.anonymizeIdentifier(raw, "salt-2");

        assertTrue(anon1.startsWith("anon-"));
        assertEquals(anon1, anon2, "Same raw ID and salt must produce identical anonymized string");
        assertNotEquals(anon1, anonOther, "Different salt must produce different anonymized string");
    }

    @Test
    @DisplayName("Should enforce minimum aggregation threshold N >= 5")
    void testAggregationThreshold() {
        assertFalse(privacyService.isSafeToExposeSignal(0));
        assertFalse(privacyService.isSafeToExposeSignal(4));
        assertTrue(privacyService.isSafeToExposeSignal(5));
        assertTrue(privacyService.isSafeToExposeSignal(100));
    }

    @Test
    @DisplayName("Should process batch privacy and record audit event")
    void testProcessBatchPrivacy() {
        FederatedLearningBatch batch = FederatedLearningBatch.builder()
                .id("batch-test-1")
                .batchReference("BATCH-TEST")
                .build();

        List<Map<String, Object>> records = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Map<String, Object> r = new HashMap<>();
            r.put("deviceId", "dev-" + i);
            r.put("email", "user" + i + "@mail.com");
            r.put("category", "LAPTOP");
            records.add(r);
        }

        var result = privacyService.processBatchPrivacy(batch, records);

        assertEquals(8, result.recordsProcessed());
        assertEquals(8, result.sanitizedRecords().size());
        assertEquals(PrivacyPreservationService.PrivacyDecision.ACCEPTED, result.decision());
        verify(privacyAuditRepository, times(1)).save(any(PrivacyAuditEvent.class));
    }
}
