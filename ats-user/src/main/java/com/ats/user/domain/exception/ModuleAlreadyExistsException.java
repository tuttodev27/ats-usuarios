package com.ats.user.domain.exception;

public class ModuleAlreadyExistsException extends RuntimeException {
    public ModuleAlreadyExistsException(String message) {
        super(message);
    }
}
