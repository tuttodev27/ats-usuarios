package com.ats.user.infrastructure.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RoleDetailResponse(
        Long id,
        String name,
        String description,
        boolean active,
        List<PermissionResponse> permissions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
