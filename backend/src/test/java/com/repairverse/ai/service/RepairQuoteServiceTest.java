package com.repairverse.ai.service;

import com.repairverse.ai.dto.MarketplaceDto.QuoteComparisonResponse;
import com.repairverse.ai.dto.MarketplaceDto.RepairQuoteResponse;
import com.repairverse.ai.dto.MarketplaceDto.RequestQuoteRequest;
import com.repairverse.ai.entity.Device;
import com.repairverse.ai.entity.RepairQuote;
import com.repairverse.ai.entity.RepairShop;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.repository.DeviceRepository;
import com.repairverse.ai.repository.RepairQuoteRepository;
import com.repairverse.ai.repository.RepairShopProfileRepository;
import com.repairverse.ai.repository.RepairShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepairQuoteServiceTest {

    @Mock
    private RepairQuoteRepository quoteRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private RepairShopRepository repairShopRepository;
    @Mock
    private RepairShopProfileRepository profileRepository;

    @InjectMocks
    private RepairQuoteService quoteService;

    private Device testDevice;
    private RepairShop testShop;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder().id("dev-1").userId("usr-1").deviceName("iPhone 14 Pro").build();
        testShop = RepairShop.builder().id("shop-1").shopName("iFix Hub").build();
    }

    @Test
    @DisplayName("User requests repair quote and receives calculated cost breakdown")
    void testRequestQuote() {
        when(deviceRepository.findByIdAndUserId("dev-1", "usr-1")).thenReturn(Optional.of(testDevice));
        when(repairShopRepository.findById("shop-1")).thenReturn(Optional.of(testShop));
        when(quoteRepository.save(any(RepairQuote.class))).thenAnswer(inv -> inv.getArgument(0));

        RequestQuoteRequest req = new RequestQuoteRequest("dev-1", "shop-1", null, null, "Display Replacement", "Cracked OLED", 120.0);
        RepairQuoteResponse res = quoteService.requestQuote(req, "usr-1");

        assertThat(res.estimatedCost()).isEqualTo(120.0);
        assertThat(res.partsCost()).isGreaterThan(0.0);
        assertThat(res.laborCost()).isGreaterThan(0.0);
        assertThat(res.status()).isEqualTo("REQUESTED");
    }

    @Test
    @DisplayName("Accepts quote and transitions status to ACCEPTED")
    void testAcceptQuote() {
        RepairQuote quote = RepairQuote.builder()
                .id("q-1").userId("usr-1").deviceId("dev-1").repairShopId("shop-1").status("REQUESTED").build();

        when(quoteRepository.findByIdAndUserId("q-1", "usr-1")).thenReturn(Optional.of(quote));
        when(quoteRepository.save(any(RepairQuote.class))).thenAnswer(inv -> inv.getArgument(0));
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(testDevice));
        when(repairShopRepository.findById("shop-1")).thenReturn(Optional.of(testShop));

        RepairQuoteResponse res = quoteService.acceptQuote("q-1", "usr-1");
        assertThat(res.status()).isEqualTo("ACCEPTED");
    }

    @Test
    @DisplayName("Compares multiple quotes and tags best value and lowest price")
    void testCompareQuotes() {
        RepairQuote q1 = RepairQuote.builder()
                .id("q-1").userId("usr-1").deviceId("dev-1").repairShopId("shop-1").estimatedCost(120.0).warrantyDays(90).estimatedDurationHours(2.0).build();
        RepairQuote q2 = RepairQuote.builder()
                .id("q-2").userId("usr-1").deviceId("dev-1").repairShopId("shop-1").estimatedCost(80.0).warrantyDays(180).estimatedDurationHours(1.5).build();

        when(quoteRepository.findByUserIdOrderByCreatedAtDesc("usr-1")).thenReturn(List.of(q1, q2));
        when(deviceRepository.findById("dev-1")).thenReturn(Optional.of(testDevice));
        when(repairShopRepository.findById("shop-1")).thenReturn(Optional.of(testShop));

        QuoteComparisonResponse res = quoteService.compareQuotes(null, "usr-1");

        assertThat(res.quotes()).hasSize(2);
        assertThat(res.lowestPriceQuoteId()).isEqualTo("q-2");
        assertThat(res.longestWarrantyQuoteId()).isEqualTo("q-2");
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException for unowned quote")
    void testUnownedQuoteThrowsException() {
        when(quoteRepository.findByIdAndUserId("q-unowned", "usr-1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quoteService.getQuoteDetails("q-unowned", "usr-1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
