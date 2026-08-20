package com.repairverse.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.BookingDto.*;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.exception.ResourceNotFoundException;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.BookingService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/v1/bookings - Create booking successfully")
    void createBooking_Success() throws Exception {
        UserPrincipal principal = new UserPrincipal("usr-123", "User", "user@example.com", "pass", Collections.emptyList());
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        CreateBookingRequest request = new CreateBookingRequest("shop-001", "2026-09-01 10:00 AM", "Screen replacement");
        BookingResponse bookingResp = new BookingResponse("book-1", "usr-123", "shop-001", "TechCare Express", "2026-09-01 10:00 AM", "SCHEDULED", "SCHEDULED", "Screen replacement", "2026-08-20T10:00:00");
        BookingDetailResponse response = new BookingDetailResponse(true, "Booking confirmed", bookingResp);

        when(bookingService.createBooking(any(), eq("usr-123"))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bookingStatus").value("SCHEDULED"));
    }

    @Test
    @DisplayName("DELETE /api/v1/bookings/{id} - Return 403 Forbidden on cross-user cancellation attempt")
    void cancelBooking_Forbidden() throws Exception {
        UserPrincipal principal = new UserPrincipal("usr-hacker", "Hacker", "hacker@example.com", "pass", Collections.emptyList());
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(bookingService.cancelBooking(eq("book-1"), anyString()))
                .thenThrow(new AccessDeniedException("You are not authorized to cancel this booking"));


        mockMvc.perform(delete("/bookings/book-1").with(authentication(auth)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }
}
