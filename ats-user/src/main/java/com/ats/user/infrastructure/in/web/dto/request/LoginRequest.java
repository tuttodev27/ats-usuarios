package com.ats.user.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Schema(example = """
        {
          "email": "admin@ats.local",
          "password": "Admin123"
        }
        """)
public record LoginRequest(
        @NotBlank
        @Email
        @Schema(example = "admin@ats.local")
        String email,
        @NotBlank
        @Schema(example = "Admin123")
        String password
) {

}
