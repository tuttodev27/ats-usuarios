package com.ats.user.domain.exception;

public class RoleNotAvailableException extends RuntimeException {
    public RoleNotAvailableException(String message) {
        super(message);
    }
}
