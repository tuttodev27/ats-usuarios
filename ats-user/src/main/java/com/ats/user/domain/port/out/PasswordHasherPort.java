package com.ats.user.domain.port.out;

public interface PasswordHasherPort {
    String encode(String rawPassword);
}
