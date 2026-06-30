package com.ats.user.domain.service;

import com.ats.user.domain.exception.InvalidPasswordException;
import org.springframework.stereotype.Component;

@Component
public class DefaultPasswordPolicy implements PasswordPolicy {

    @Override
    public void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new InvalidPasswordException("Password is required");
        }
        boolean hasLetter = rawPassword.chars().anyMatch(Character::isLetter);
        boolean hasDigit = rawPassword.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) {
            throw new InvalidPasswordException("Password must include at least one letter and one number");
        }
    }
}
