package com.ats.user.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(example = """
        {
          "active": false
        }
        """)
public record UpdateUserStatusRequest(
        @NotNull
        @Schema(example = "false")
        Boolean active
) {
}
