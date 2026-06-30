package com.ats.user.infrastructure.adapter.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(example = """
        {
          "permissionIds": [1, 2, 3]
        }
        """)
public record AssignPermissionRequest(
        @ArraySchema(
                schema = @Schema(implementation = Long.class),
                arraySchema = @Schema(description = "IDs de permisos a asignar al rol")
        )
        @NotEmpty
        List<@NotNull Long> permissionIds
) {
}
