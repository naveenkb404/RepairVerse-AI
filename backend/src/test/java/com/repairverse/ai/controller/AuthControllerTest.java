package com.repairverse.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.repairverse.ai.dto.AuthRequest.LoginRequest;
import com.repairverse.ai.dto.AuthRequest.RegisterRequest;
import com.repairverse.ai.dto.AuthResponse.LoginResponse;
import com.repairverse.ai.dto.AuthResponse.MeResponse;
import com.repairverse.ai.dto.AuthResponse.RegisterResponse;
import com.repairverse.ai.dto.AuthResponse.TokenData;
import com.repairverse.ai.dto.AuthResponse.UserDto;
import com.repairverse.ai.entity.Role;
import com.repairverse.ai.exception.EmailAlreadyExistsException;
import com.repairverse.ai.exception.GlobalExceptionHandler;
import com.repairverse.ai.exception.UnauthorizedRoleException;
import com.repairverse.ai.security.CustomUserDetailsService;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private UserDto sampleUserDto;

    @BeforeEach
    void setUp() {
        sampleUserDto = new UserDto("usr-1", "Jane Doe", "jane@example.com", Role.USER);
    }

    @Test
    @DisplayName("POST /auth/register - 201 Created on valid registration")
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "Password@123", "USER");
        RegisterResponse response = new RegisterResponse(true, "Registration successful. You can now sign in.");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful. You can now sign in."));
    }

    @Test
    @DisplayName("POST /auth/register - 409 Conflict when email already registered")
    void testRegisterDuplicateEmailConflict() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "Password@123", "USER");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("jane@example.com"));

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("EMAIL_CONFLICT"));
    }

    @Test
    @DisplayName("POST /auth/register - 403 Forbidden when attempting ADMIN registration")
    void testRegisterAdminForbidden() throws Exception {
        RegisterRequest request = new RegisterRequest("Admin User", "admin@example.com", "Password@123", "ADMIN");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new UnauthorizedRoleException("Public registration as ADMIN is not permitted."));

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("ROLE_FORBIDDEN"));
    }

    @Test
    @DisplayName("POST /auth/register - 422 Unprocessable Entity on validation error")
    void testRegisterValidationFailure() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest("", "invalid-email", "short", "INVALID_ROLE");

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("POST /auth/login - 200 OK on valid credentials")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("jane@example.com", "Password@123");
        LoginResponse response = new LoginResponse(true, "Login successful", new TokenData("mock-jwt-token", sampleUserDto));

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.user.email").value("jane@example.com"));
    }

    @Test
    @DisplayName("POST /auth/login - 401 Unauthorized on bad credentials")
    void testLoginBadCredentials() throws Exception {
        LoginRequest request = new LoginRequest("jane@example.com", "WrongPassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /auth/me - 200 OK on authenticated user")
    void testGetMe() throws Exception {
        MeResponse response = new MeResponse(true, "User profile retrieved", sampleUserDto);
        when(authService.getCurrentUser(any())).thenReturn(response);

        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("jane@example.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /auth/logout - 200 OK")
    void testLogout() throws Exception {
        mockMvc.perform(post("/auth/logout").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
