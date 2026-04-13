package com.ats.user.infrastructure.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateModuleRequest(
        @NotBlank
        @Size(max = 60)
        String code,

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String description,

        boolean active
) {
}
