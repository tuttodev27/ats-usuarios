package com.ats.user.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(example = """
        {
          "name": "Carlos",
          "lastName": "Garcia",
          "countryCode": "+57",
          "phone": "3001112233"
        }
        """)
public record UpdateUserRequest(
        @NotBlank
        @Size(max = 80)
        @Schema(example = "Carlos")
        String name,

        @NotBlank
        @Size(max = 80)
        @Schema(example = "Garcia")
        String lastName,

        @NotBlank
        @Pattern(regexp = "^\\+\\d{1,4}$", message = "Country code must start with '+' followed by 1 to 4 digits")
        @Schema(example = "+57")
        String countryCode,

        @NotBlank
        @Pattern(regexp = "^[0-9]{6,20}$", message = "phone must be numeric 6-20 digits")
        @Schema(example = "3001112233")
        String phone
) {
}
