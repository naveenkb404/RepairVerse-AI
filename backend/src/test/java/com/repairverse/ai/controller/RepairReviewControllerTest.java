package com.repairverse.ai.controller;

import com.repairverse.ai.dto.MarketplaceDto.CreateReviewRequest;
import com.repairverse.ai.dto.MarketplaceDto.RepairReviewResponse;
import com.repairverse.ai.dto.MarketplaceDto.ShopReputationResponse;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairReputationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RepairReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairReputationService reputationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal("usr-1", "Test User", "test@repairverse.ai", "pw",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    @DisplayName("GET /api/v1/marketplace/shops/{shopId}/reviews — returns reviews")
    void testGetShopReviews() throws Exception {
        RepairReviewResponse review = new RepairReviewResponse(
                "rev-1", "usr-1", "Alex Doe", "shop-1", "book-1", 5,
                "Superb repair", "Fixed quickly", 5, 5, 5, 5, true,
                LocalDateTime.now(), false
        );
        when(reputationService.getShopReviews("shop-1")).thenReturn(List.of(review));

        mockMvc.perform(get("/marketplace/shops/shop-1/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].id").value("rev-1"));
    }

    @Test
    @DisplayName("POST /api/v1/marketplace/shops/{shopId}/reviews — submits verified review")
    void testSubmitReview() throws Exception {
        RepairReviewResponse review = new RepairReviewResponse(
                "rev-1", "usr-1", "Alex Doe", "shop-1", "book-1", 5,
                "Superb repair", "Fixed quickly", 5, 5, 5, 5, true,
                LocalDateTime.now(), false
        );
        when(reputationService.submitReview(eq("shop-1"), any(), eq("usr-1"))).thenReturn(review);

        mockMvc.perform(post("/marketplace/shops/shop-1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":\"book-1\",\"rating\":5,\"title\":\"Great\",\"comment\":\"Fast\",\"repairQualityRating\":5,\"communicationRating\":5,\"valueRating\":5,\"timelinessRating\":5}")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(5));
    }

    @Test
    @DisplayName("Returns 403 Forbidden when user has no verified booking")
    void testSubmitReviewUnverifiedThrowsForbidden() throws Exception {
        when(reputationService.submitReview(eq("shop-1"), any(), eq("usr-1")))
                .thenThrow(new AccessDeniedException("Review rejected: Verified completed repair booking required"));

        mockMvc.perform(post("/marketplace/shops/shop-1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookingId\":\"book-none\",\"rating\":5,\"title\":\"Great\",\"comment\":\"Fast\",\"repairQualityRating\":5,\"communicationRating\":5,\"valueRating\":5,\"timelinessRating\":5}")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/v1/marketplace/shops/{shopId}/reputation — returns reputation breakdown")
    void testGetShopReputation() throws Exception {
        ShopReputationResponse rep = new ShopReputationResponse(
                "shop-1", 4.8, 42, 42, 4.9, 4.8, 4.7, 4.9,
                Map.of(5, 35L, 4, 7L, 3, 0L, 2, 0L, 1, 0L),
                List.of(), false
        );
        when(reputationService.getShopReputation("shop-1")).thenReturn(rep);

        mockMvc.perform(get("/marketplace/shops/shop-1/reputation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageRating").value(4.8));
    }
}
