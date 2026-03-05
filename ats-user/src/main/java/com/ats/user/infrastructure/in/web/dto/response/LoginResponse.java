package com.ats.user.infrastructure.in.web.dto.response;

public record LoginResponse(
        String token,
        String tokenType,
        Long expiredInSeconds

) {
}
