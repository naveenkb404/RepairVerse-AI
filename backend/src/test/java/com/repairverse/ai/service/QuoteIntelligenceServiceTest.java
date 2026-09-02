package com.repairverse.ai.service;

import com.repairverse.ai.dto.RepairMatchingDto.QuoteIntelligenceResponse;
import com.repairverse.ai.entity.RepairQuote;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.RepairQuoteRepository;
import com.repairverse.ai.repository.RepairShopRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteIntelligenceServiceTest {

    @Mock
    private RepairQuoteRepository quoteRepository;

    @Mock
    private RepairShopRepository shopRepository;

    @InjectMocks
    private QuoteIntelligenceService quoteIntelligenceService;

    @Test
    @DisplayName("evaluateQuoteIntelligence — classifies fair price quote correctly")
    void testEvaluateQuoteFairPrice() {
        RepairQuote quote = RepairQuote.builder()
                .id("quote-1")
                .userId("usr-1")
                .repairShopId("shop-1")
                .repairTitle("Screen Replacement")
                .estimatedCost(90.0)
                .partsCost(50.0)
                .laborCost(40.0)
                .warrantyDays(90)
                .build();

        RepairShop shop = RepairShop.builder()
                .id("shop-1")
                .shopName("FixVerse Express")
                .build();

        when(quoteRepository.findById("quote-1")).thenReturn(Optional.of(quote));
        when(shopRepository.findById("shop-1")).thenReturn(Optional.of(shop));

        QuoteIntelligenceResponse response = quoteIntelligenceService.evaluateQuoteIntelligence("quote-1", "usr-1");

        assertNotNull(response);
        assertEquals("quote-1", response.quoteId());
        assertEquals("shop-1", response.repairShopId());
        assertEquals(90.0, response.estimatedCost());
        assertEquals("FAIR_PRICE", response.classification());
        assertTrue(response.priceFairnessScore() >= 80);
        assertFalse(response.insights().isEmpty());
    }

    @Test
    @DisplayName("evaluateQuoteIntelligence — detects suspiciously low price")
    void testEvaluateQuoteSuspiciouslyLow() {
        RepairQuote quote = RepairQuote.builder()
                .id("quote-2")
                .userId("usr-1")
                .repairShopId("shop-1")
                .repairTitle("Screen Replacement")
                .estimatedCost(20.0) // way below $95 benchmark
                .partsCost(10.0)
                .laborCost(10.0)
                .build();

        when(quoteRepository.findById("quote-2")).thenReturn(Optional.of(quote));
        when(shopRepository.findById("shop-1")).thenReturn(Optional.of(RepairShop.builder().id("shop-1").shopName("Suspicious Shop").build()));

        QuoteIntelligenceResponse response = quoteIntelligenceService.evaluateQuoteIntelligence("quote-2", "usr-1");

        assertNotNull(response);
        assertEquals("SUSPICIOUSLY_LOW", response.classification());
        assertFalse(response.warnings().isEmpty());
    }

    @Test
    @DisplayName("evaluateQuoteIntelligence — enforces ownership access security")
    void testEvaluateQuoteUnauthorizedAccess() {
        RepairQuote quote = RepairQuote.builder()
                .id("quote-3")
                .userId("usr-2") // Different user
                .repairShopId("shop-1")
                .build();

        when(quoteRepository.findById("quote-3")).thenReturn(Optional.of(quote));

        assertThrows(AccessDeniedException.class, () ->
                quoteIntelligenceService.evaluateQuoteIntelligence("quote-3", "usr-1"));
    }

    @Test
    @DisplayName("evaluateQuoteIntelligence — throws ResourceNotFoundException on unknown quote")
    void testEvaluateQuoteNotFound() {
        when(quoteRepository.findById("quote-unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                quoteIntelligenceService.evaluateQuoteIntelligence("quote-unknown", "usr-1"));
    }
}
