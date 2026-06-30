package com.ats.user.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(example = """
        {
          "roleId": 2
        }
        """)
public record UpdateUserRoleRequest(
        @NotNull
        @Schema(example = "2")
        Long roleId
) {
}
