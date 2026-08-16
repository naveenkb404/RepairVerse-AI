package com.repairverse.ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedRoleException extends RuntimeException {

    public UnauthorizedRoleException(String message) {
        super(message);
    }
}
