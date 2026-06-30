package com.ats.user.infrastructure.adapter.in.web.dto.response;

import java.util.List;

public record LoginResponse(
        String token,
        String tokenType,
        Long expiredInSeconds,
        UserInfo user,
        List<String> roles,
        List<String> permissions
) {
    public record UserInfo(Long id, String name, String lastName, String email, String phone) {}
}
