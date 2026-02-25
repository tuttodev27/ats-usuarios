package com.ats.user.infrastructure.in.web.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String lastName,
        String email,
        String countryCode,
        String phone,
        LocalDateTime createdAt
) {
}
