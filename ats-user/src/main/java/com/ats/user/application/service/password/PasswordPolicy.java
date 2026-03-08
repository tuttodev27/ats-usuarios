package com.ats.user.application.service.password;

public interface PasswordPolicy {
    void validate(String rawPassword);
}
