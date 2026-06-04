package com.ats.user.infrastructure.in.web.mapper;

import com.ats.user.domain.model.Role;
import com.ats.user.infrastructure.in.web.dto.request.CreateRoleRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateRoleRequest;
import com.ats.user.infrastructure.in.web.dto.response.RolePermissionsResponse;
import com.ats.user.infrastructure.in.web.dto.response.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "active", source = "request.active")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role toDomain(CreateRoleRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "active", source = "request.active")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role toDomain(UpdateRoleRequest request);

    @Mapping(target = "permissionsCount", expression = "java(role.getPermissions() != null ? role.getPermissions().size() : 0)")
    RoleResponse toResponse(Role role);

    default RolePermissionsResponse toPermissionsResponse(Role role) {
        List<Long> permissionIds = role.getPermissions().stream()
                .map(p -> p.getId())
                .sorted()
                .toList();
        List<String> permissions = role.getPermissions().stream()
                .map(p -> p.getCode())
                .sorted(Comparator.naturalOrder())
                .toList();
        return new RolePermissionsResponse(role.getId(), role.getName(), permissionIds, permissions);
    }
}
