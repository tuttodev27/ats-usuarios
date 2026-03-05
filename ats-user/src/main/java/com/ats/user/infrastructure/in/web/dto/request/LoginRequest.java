package com.ats.user.infrastructure.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record LoginRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
) {

}
