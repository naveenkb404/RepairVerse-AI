package com.repairverse.ai.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Generic API error response — safe for client consumption.
 * Never exposes stack traces, SQL, passwords, or secrets.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        boolean success,
        String message,
        String errorCode
) {
    public static ErrorResponse of(String message, String errorCode) {
        return new ErrorResponse(false, message, errorCode);
    }
}
