package com.ats.user.domain.model;

import java.util.List;

public record AuthResult(
        Long userId,
        String name,
        String lastName,
        String email,
        String phone,
        List<String> roles,
        List<String> permissions
) {}
