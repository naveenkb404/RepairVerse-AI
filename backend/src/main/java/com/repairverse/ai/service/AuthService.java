package com.repairverse.ai.service;

import com.repairverse.ai.dto.AuthRequest.LoginRequest;
import com.repairverse.ai.dto.AuthRequest.RegisterRequest;
import com.repairverse.ai.dto.AuthResponse.*;
import com.repairverse.ai.entity.Role;
import com.repairverse.ai.entity.User;
import com.repairverse.ai.exception.EmailAlreadyExistsException;
import com.repairverse.ai.exception.UnauthorizedRoleException;
import com.repairverse.ai.repository.UserRepository;
import com.repairverse.ai.security.JwtTokenProvider;
import com.repairverse.ai.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user.
     * ADMIN role is never allowed through public registration.
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // Block ADMIN self-registration explicitly
        String roleStr = request.role() != null ? request.role().toUpperCase() : "USER";
        if (roleStr.equals("ADMIN")) {
            throw new UnauthorizedRoleException("Public registration as ADMIN is not permitted.");
        }

        // Check duplicate email
        if (userRepository.existsByEmail(request.email().toLowerCase())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        Role role;
        try {
            role = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Allowed values: USER, TECHNICIAN");
        }

        User user = User.builder()
                .fullName(request.fullName().trim())
                .email(request.email().toLowerCase().trim())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(role)
                .verified(false)
                .build();

        userRepository.save(user);
        log.info("New user registered: {} with role {}", user.getEmail(), role);

        return new RegisterResponse(true, "Registration successful. You can now sign in.");
    }

    /**
     * Authenticate an existing user and return a JWT.
     */
    public LoginResponse login(LoginRequest request) {
        // AuthenticationManager handles BCrypt verification; throws BadCredentialsException on failure
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().toLowerCase().trim(),
                        request.password()
                )
        );

        String jwt = jwtTokenProvider.generateToken(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Load role from the user entity for the response
        User user = userRepository.findByEmail(principal.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        UserDto userDto = new UserDto(
                principal.getId(),
                principal.getFullName(),
                principal.getEmail(),
                user.getRole()
        );

        return new LoginResponse(true, "Login successful", new TokenData(jwt, userDto));
    }

    /**
     * Return the current authenticated user's safe profile.
     * Never returns passwordHash or security credentials.
     */
    public MeResponse getCurrentUser(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found in database"));

        UserDto userDto = new UserDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );

        return new MeResponse(true, "User profile retrieved", userDto);
    }
}
