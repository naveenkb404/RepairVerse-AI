package com.repairverse.ai.controller;

import com.repairverse.ai.dto.MarketplaceDto.QuoteComparisonResponse;
import com.repairverse.ai.dto.MarketplaceDto.RepairQuoteResponse;
import com.repairverse.ai.dto.MarketplaceDto.RequestQuoteRequest;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.RepairQuoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RepairQuoteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RepairQuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairQuoteService quoteService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserPrincipal mockPrincipal() {
        return new UserPrincipal("usr-1", "Test User", "test@repairverse.ai", "pw",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    private RepairQuoteResponse mockQuoteResponse() {
        return new RepairQuoteResponse(
                "quote-1", "usr-1", "dev-1", "iPhone 14", "shop-1", "iFix Hub",
                null, null, "Screen Fix", "Broken digitizer", 110.0, 95.0, 130.0,
                2.0, 60.0, 50.0, 90, "REQUESTED", 84, "EXCELLENT",
                LocalDateTime.now(), LocalDateTime.now().plusDays(7), false
        );
    }

    @Test
    @DisplayName("POST /api/v1/repair-quotes/request — creates quote request")
    void testRequestQuote() throws Exception {
        when(quoteService.requestQuote(any(), eq("usr-1"))).thenReturn(mockQuoteResponse());

        mockMvc.perform(post("/repair-quotes/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"deviceId\":\"dev-1\",\"repairShopId\":\"shop-1\",\"userBudget\":110.0}")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value("quote-1"));
    }

    @Test
    @DisplayName("GET /api/v1/repair-quotes — returns user quotes")
    void testGetUserQuotes() throws Exception {
        when(quoteService.getUserQuotes("usr-1")).thenReturn(List.of(mockQuoteResponse()));

        mockMvc.perform(get("/repair-quotes")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    @DisplayName("PUT /api/v1/repair-quotes/{id}/accept — accepts quote")
    void testAcceptQuote() throws Exception {
        RepairQuoteResponse accepted = new RepairQuoteResponse(
                "quote-1", "usr-1", "dev-1", "iPhone 14", "shop-1", "iFix Hub",
                null, null, "Screen Fix", "Broken digitizer", 110.0, 95.0, 130.0,
                2.0, 60.0, 50.0, 90, "ACCEPTED", 84, "EXCELLENT",
                LocalDateTime.now(), LocalDateTime.now().plusDays(7), false
        );
        when(quoteService.acceptQuote("quote-1", "usr-1")).thenReturn(accepted);

        mockMvc.perform(put("/repair-quotes/quote-1/accept")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("GET /api/v1/repair-quotes/compare — returns comparison analysis")
    void testCompareQuotes() throws Exception {
        QuoteComparisonResponse comparison = new QuoteComparisonResponse(
                List.of(mockQuoteResponse()), "quote-1", "quote-1", "quote-1", "quote-1",
                List.of("Insight"), false
        );
        when(quoteService.compareQuotes(any(), eq("usr-1"))).thenReturn(comparison);

        mockMvc.perform(get("/repair-quotes/compare")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bestValueQuoteId").value("quote-1"));
    }

    @Test
    @DisplayName("Returns 404 for unowned quote")
    void testUnownedQuoteReturns404() throws Exception {
        when(quoteService.getQuoteDetails("q-unowned", "usr-1"))
                .thenThrow(new ResourceNotFoundException("Quote not found or not owned by user: q-unowned"));

        mockMvc.perform(get("/repair-quotes/q-unowned")
                        .with(authentication(new UsernamePasswordAuthenticationToken(mockPrincipal(), null, mockPrincipal().getAuthorities()))))
                .andExpect(status().isNotFound());
    }
}
