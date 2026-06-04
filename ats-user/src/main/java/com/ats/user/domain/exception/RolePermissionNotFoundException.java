package com.ats.user.domain.exception;

public class RolePermissionNotFoundException extends RuntimeException {
    public RolePermissionNotFoundException(String message) {
        super(message);
    }
}
