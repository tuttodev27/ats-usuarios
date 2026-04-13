package com.ats.user.infrastructure.in.web.dto.response;

import java.time.LocalDateTime;

public record PermissionResponse(
        Long id,
        String code,
        String resource,
        String action,
        String scope,
        String description,
        Long moduleId,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
