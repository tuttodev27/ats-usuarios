package com.ats.user.infrastructure.adapter.in.web.dto.response;

import java.time.LocalDateTime;

public record PermissionResponse(
        Long id,
        String code,
        String name,
        String resource,
        String action,
        String scope,
        String description,
        Long moduleId,
        String moduleCode,
        String moduleName,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
