package com.ats.user.infrastructure.in.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record RolePermissionsResponse(
        @Schema(example = "1")
        Long roleId,
        @Schema(example = "ADMIN")
        String roleName,
        @Schema(example = "[1,2,3]")
        List<Long> permissionIds,
        @Schema(example = "[\"USER_READ\",\"USER_CREATE\",\"ROLE_UPDATE\"]")
        List<String> permissions
) {
}
