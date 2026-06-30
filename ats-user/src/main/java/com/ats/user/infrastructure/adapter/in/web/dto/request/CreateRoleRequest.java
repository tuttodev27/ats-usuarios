package com.ats.user.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(example = """
        {
          "name": "MANAGER",
          "description": "Rol gerencial",
          "active": true
        }
        """)
public record CreateRoleRequest(
        @NotBlank
        @Size(max = 60)
        @Schema(example = "MANAGER")
        String name,

        @Size(max = 255)
        @Schema(example = "Rol gerencial")
        String description,

        @Schema(example = "true")
        boolean active
) {
}
