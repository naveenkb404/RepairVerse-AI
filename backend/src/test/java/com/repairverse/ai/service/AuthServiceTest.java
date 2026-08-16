package com.repairverse.ai.service;

import com.repairverse.ai.dto.AuthRequest.LoginRequest;
import com.repairverse.ai.dto.AuthRequest.RegisterRequest;
import com.repairverse.ai.dto.AuthResponse.LoginResponse;
import com.repairverse.ai.dto.AuthResponse.MeResponse;
import com.repairverse.ai.dto.AuthResponse.RegisterResponse;
import com.repairverse.ai.entity.Role;
import com.repairverse.ai.entity.User;
import com.repairverse.ai.exception.EmailAlreadyExistsException;
import com.repairverse.ai.exception.UnauthorizedRoleException;
import com.repairverse.ai.repository.UserRepository;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .id("usr-12345")
                .fullName("Jane Doe")
                .email("jane@example.com")
                .passwordHash("hashedPassword123")
                .role(Role.USER)
                .verified(false)
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new user with BCrypt hashed password")
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "Password@123", "USER");

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("Registration successful. You can now sign in.", response.message());
        verify(passwordEncoder, times(1)).encode("Password@123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should reject registration when email already exists")
    void testRegisterDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("Jane Doe", "jane@example.com", "Password@123", "USER");

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should block public registration as ADMIN")
    void testRegisterAdminBlocked() {
        RegisterRequest request = new RegisterRequest("Admin User", "admin@example.com", "Password@123", "ADMIN");

        assertThrows(UnauthorizedRoleException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should successfully authenticate and return signed JWT")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("jane@example.com", "Password@123");
        UserPrincipal userPrincipal = UserPrincipal.create(sampleUser);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mock-jwt-token-xyz");
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(sampleUser));

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("Login successful", response.message());
        assertNotNull(response.data());
        assertEquals("mock-jwt-token-xyz", response.data().token());
        assertEquals("jane@example.com", response.data().user().email());
        assertEquals(Role.USER, response.data().user().role());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid password")
    void testLoginInvalidPassword() {
        LoginRequest request = new LoginRequest("jane@example.com", "WrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    @DisplayName("Should retrieve safe user profile without passwordHash")
    void testGetCurrentUser() {
        UserPrincipal userPrincipal = UserPrincipal.create(sampleUser);
        when(userRepository.findById("usr-12345")).thenReturn(Optional.of(sampleUser));

        MeResponse response = authService.getCurrentUser(userPrincipal);

        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("usr-12345", response.data().id());
        assertEquals("jane@example.com", response.data().email());
        assertEquals("Jane Doe", response.data().fullName());
        assertEquals(Role.USER, response.data().role());
    }
}
