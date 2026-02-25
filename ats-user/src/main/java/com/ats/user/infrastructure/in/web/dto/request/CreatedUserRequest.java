package com.ats.user.infrastructure.in.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatedUserRequest(
        @NotBlank
        @Size(max = 80)
        String name,

        @NotBlank
        @Size(max = 80)
        String lastName,

        @NotBlank
        @Email
        @Size(max=120)
        String email,

        @NotBlank
        @Pattern(regexp = "^\\+\\d{1,4}$", message = "Country code must start with '+' followed by 1 to 4 digits")
        String countryCode,

        @NotBlank
        @Pattern(regexp = "^[0-9]{6,20}$", message = "phone must be numeric 6-20 digits")
        String phone,

        @NotBlank
        @Size(min= 6, max = 100)
        String password

) {}
