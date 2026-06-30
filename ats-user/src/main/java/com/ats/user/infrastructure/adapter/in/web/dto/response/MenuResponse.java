package com.ats.user.infrastructure.adapter.in.web.dto.response;

import java.time.LocalDateTime;

public record MenuResponse(
        Long id,
        String title,
        String path,
        String icon,
        Long moduleId,
        Integer orderIndex,
        String requiredPermissionCode,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
