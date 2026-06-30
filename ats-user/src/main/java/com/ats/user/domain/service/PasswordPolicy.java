package com.ats.user.domain.service;

public interface PasswordPolicy {
    void validate(String rawPassword);
}
