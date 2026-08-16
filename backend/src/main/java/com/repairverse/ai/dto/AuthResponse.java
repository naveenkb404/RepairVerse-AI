package com.repairverse.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.repairverse.ai.entity.Role;

/**
 * Auth response DTOs — matches frontend AuthResponse contract.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    public record UserDto(
            String id,
            String fullName,
            String email,
            Role role
    ) {}

    public record TokenData(
            String token,
            UserDto user
    ) {}

    public record LoginResponse(
            boolean success,
            String message,
            TokenData data
    ) {}

    public record RegisterResponse(
            boolean success,
            String message
    ) {}

    public record MeResponse(
            boolean success,
            String message,
            UserDto data
    ) {}

    public record LogoutResponse(
            boolean success,
            String message
    ) {}
}
