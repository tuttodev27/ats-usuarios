package com.ats.user.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(example = """
        {
          "title": "Usuarios",
          "path": "/users",
          "icon": "people",
          "orderIndex": 1,
          "requiredPermissionCode": "USER_READ",
          "moduleId": 1
        }
        """)
public record UpdateMenuRequest(
        @NotBlank
        @Size(max = 120)
        @Schema(example = "Usuarios")
        String title,

        @NotBlank
        @Size(max = 200)
        @Schema(example = "/users")
        String path,

        @Size(max = 120)
        @Schema(example = "people")
        String icon,

        @Schema(example = "1")
        Integer orderIndex,

        @Size(max = 120)
        @Schema(example = "USER_READ")
        String requiredPermissionCode,

        @NotNull
        @Positive
        @Schema(example = "1")
        Long moduleId
) {
}
