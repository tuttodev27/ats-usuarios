package com.ats.user.infrastructure.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(
        @NotBlank
        @Size(max = 60)
        String name,

        @Size(max = 255)
        String description,

        boolean active
) {
}
