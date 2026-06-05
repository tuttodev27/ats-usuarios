package com.ats.user.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(example = """
        {
          "name": "Carlos",
          "lastName": "Garcia",
          "email": "carlos@example.com",
          "countryCode": "+57",
          "phone": "3001112233",
          "password": "MiPassword123",
          "roleId": 1
        }
        """)
public record UserRequest(
        @NotBlank
        @Size(max = 80)
        @Schema(example = "Carlos")
        String name,

        @NotBlank
        @Size(max = 80)
        @Schema(example = "Garcia")
        String lastName,

        @NotBlank
        @Email
        @Size(max=120)
        @Schema(example = "carlos@example.com")
        String email,

        @NotBlank
        @Pattern(regexp = "^\\+\\d{1,4}$", message = "Country code must start with '+' followed by 1 to 4 digits")
        @Schema(example = "+57")
        String countryCode,

        @NotBlank
        @Pattern(regexp = "^[0-9]{6,20}$", message = "phone must be numeric 6-20 digits")
        @Schema(example = "3001112233")
        String phone,

        @NotBlank
        @Size(min= 8, max = 100)
        @Schema(example = "MiPassword123")
        String password,

        @NotNull
        @Schema(example = "1")
        Long roleId
) {
}
