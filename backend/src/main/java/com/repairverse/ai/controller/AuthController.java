package com.repairverse.ai.controller;

import com.repairverse.ai.dto.AuthRequest.LoginRequest;
import com.repairverse.ai.dto.AuthRequest.RegisterRequest;
import com.repairverse.ai.dto.AuthResponse.*;
import com.repairverse.ai.security.UserPrincipal;
import com.repairverse.ai.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Auth REST Controller
 * Base path: /api/v1/auth (applied via context-path + mapping)
 *
 * Public endpoints:
 *   POST /auth/register
 *   POST /auth/login
 *
 * Authenticated endpoints:
 *   GET  /auth/me
 *   POST /auth/logout
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/register
     * Creates a new USER or TECHNICIAN account.
     * ADMIN registration is blocked at the service layer.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/login
     * Authenticates credentials and returns a signed JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/auth/me
     * Returns the authenticated user's safe profile.
     * Requires a valid Bearer JWT in the Authorization header.
     * Never returns passwordHash or security secrets.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MeResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        MeResponse response = authService.getCurrentUser(userPrincipal);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/logout
     * JWT is stateless — the client is responsible for removing the token.
     * Server returns a clean acknowledgement without pretending the JWT is revoked.
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LogoutResponse> logout() {
        return ResponseEntity.ok(new LogoutResponse(true,
                "Logout acknowledged. Remove the token from your client to complete sign-out."));
    }
}
