package com.ats.user.infrastructure.adapter.in.web.dto.response;

import java.time.LocalDateTime;

public record RoleResponse(
        Long id,
        String name,
        String description,
        boolean active,
        int permissionsCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
