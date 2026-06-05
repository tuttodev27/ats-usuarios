package com.ats.user.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(example = """
        {
          "name": "MANAGER",
          "description": "Rol gerencial"
        }
        """)
public record UpdateRoleRequest(
        @NotBlank
        @Size(max = 60)
        @Schema(example = "MANAGER")
        String name,

        @Size(max = 255)
        @Schema(example = "Rol gerencial")
        String description
) {
}
