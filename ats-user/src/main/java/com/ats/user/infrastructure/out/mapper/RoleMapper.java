package com.ats.user.infrastructure.out.mapper;

import com.ats.user.domain.model.Permission;
import com.ats.user.domain.model.Role;
import com.ats.user.infrastructure.out.entity.PermissionEntity;
import com.ats.user.infrastructure.out.entity.RoleEntity;
import com.ats.user.infrastructure.out.entity.RolePermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "permissions", expression = "java(toDomainPermissions(entity.getRolePermissions()))")
    Role toDomain(RoleEntity entity);

    default Set<Permission> toDomainPermissions(Set<RolePermissionEntity> rolePermissions) {
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return Collections.emptySet();
        }
        return rolePermissions.stream()
                .filter(rolePermission -> Boolean.TRUE.equals(rolePermission.getActive()))
                .map(RolePermissionEntity::getPermission)
                .filter(permission -> Boolean.TRUE.equals(permission.getActive()))
                .map(this::toDomainPermission)
                .collect(Collectors.toSet());
    }

    default Permission toDomainPermission(PermissionEntity permission) {
        return Permission.builder()
                .id(permission.getId())
                .code(permission.getCode())
                .resource(permission.getResource())
                .action(permission.getAction())
                .scope(permission.getScope())
                .description(permission.getDescription())
                .moduleId(permission.getModule() != null ? permission.getModule().getId() : null)
                .active(Boolean.TRUE.equals(permission.getActive()))
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .createdBy(permission.getCreatedBy())
                .updatedBy(permission.getUpdatedBy())
                .build();
    }
}
