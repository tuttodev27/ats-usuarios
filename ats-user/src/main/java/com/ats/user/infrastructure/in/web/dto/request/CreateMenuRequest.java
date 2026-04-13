package com.ats.user.infrastructure.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateMenuRequest(
        @NotBlank
        @Size(max = 120)
        String title,

        @NotBlank
        @Size(max = 200)
        String path,

        Integer orderIndex,

        @Size(max = 120)
        String requiredPermissionCode,

        boolean active,

        @NotNull
        @Positive
        Long moduleId
) {
}
