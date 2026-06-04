package com.ats.user.infrastructure.in.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateMenuStatusRequest(
        @NotNull
        Boolean active
) {
}
