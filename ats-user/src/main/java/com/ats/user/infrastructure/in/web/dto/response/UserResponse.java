package com.ats.user.infrastructure.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String name,
        String lastName,
        String email,
        String countryCode,
        String phone,
        LocalDateTime createdAt,
        Set<String> roles
) {
}
