package com.ats.user.infrastructure.in.web.dto.response;

import java.time.LocalDateTime;

public record RoleResponse(
        Long id,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
