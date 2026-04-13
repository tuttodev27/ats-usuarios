package com.ats.user.infrastructure.in.web.dto.response;

import java.time.LocalDateTime;

public record ModuleResponse(
        Long id,
        String code,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
