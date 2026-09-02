package com.repairverse.ai.controller;

import com.repairverse.ai.dto.MarketplaceDto.MarketplaceShopResponse;
import com.repairverse.ai.dto.MarketplaceDto.ShopRankingResponse;
import com.repairverse.ai.dto.MarketplaceDto.TrustScoreResponse;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.service.RepairMarketplaceService;
import com.repairverse.ai.service.RepairReputationService;
import com.repairverse.ai.service.RepairTrustService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RepairMarketplaceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairMarketplaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairMarketplaceService marketplaceService;

    @MockBean
    private RepairTrustService trustService;

    @MockBean
    private RepairReputationService reputationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/v1/marketplace/shops — returns ranked shops")
    void testDiscoverShops() throws Exception {
        MarketplaceShopResponse shop = new MarketplaceShopResponse(
                "shop-1", "FixVerse Hub", "123 Main St", 37.77, -122.41,
                "+1 555", "info@fix.com", "9-5", 4.9, 80, "TRUSTED", "PREMIUM",
                6, 200, 98.0, 20, true, 180, List.of("Apple"), List.of("ISO"), 92, "EXCEPTIONAL",
                List.of("Certified"), false
        );

        when(marketplaceService.discoverShops(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(shop));

        mockMvc.perform(get("/marketplace/shops"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].id").value("shop-1"));
    }

    @Test
    @DisplayName("GET /api/v1/marketplace/shops/{id}/ranking — returns ranking breakdown")
    void testGetShopRanking() throws Exception {
        ShopRankingResponse ranking = new ShopRankingResponse(
                "shop-1", "FixVerse Hub", 92, "EXCEPTIONAL", 25, 24, 18, 10, 10, 5,
                List.of("Reason"), List.of("Strength"), List.of(), false
        );

        when(marketplaceService.getShopRanking("shop-1", null, null)).thenReturn(ranking);

        mockMvc.perform(get("/marketplace/shops/shop-1/ranking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalScore").value(92));
    }

    @Test
    @DisplayName("GET /api/v1/marketplace/shops/{id}/trust — returns trust score")
    void testGetShopTrust() throws Exception {
        TrustScoreResponse trust = new TrustScoreResponse(
                "shop-1", 88, "EXCEPTIONAL", List.of("Factor"), List.of("Signal"), List.of(), false
        );

        when(trustService.evaluateTrust("shop-1")).thenReturn(trust);

        mockMvc.perform(get("/marketplace/shops/shop-1/trust"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.trustScore").value(88));
    }
}
