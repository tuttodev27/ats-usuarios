package com.ats.user.infrastructure.adapter.out.persistence.mapper;

import com.ats.user.domain.model.Permission;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.model.User;
import com.ats.user.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import com.ats.user.infrastructure.adapter.out.persistence.entity.RoleEntity;
import com.ats.user.infrastructure.adapter.out.persistence.entity.RolePermissionEntity;
import com.ats.user.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(toRoleEntities(user.getRoles()))")
    UserEntity toEntity(User user);

    @Mapping(target = "roles", expression = "java(toDomainRoles(userEntity.getRoles()))")
    User toDomain(UserEntity userEntity);

    default Set<RoleEntity> toRoleEntities(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(role -> RoleEntity.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .active(role.isActive())
                        .build())
                .collect(Collectors.toSet());
    }

    default Set<Role> toDomainRoles(Set<RoleEntity> roleEntities) {
        if (roleEntities == null || roleEntities.isEmpty()) {
            return Collections.emptySet();
        }
        return roleEntities.stream()
                .map(roleEntity -> Role.builder()
                        .id(roleEntity.getId())
                        .name(roleEntity.getName())
                        .active(Boolean.TRUE.equals(roleEntity.getActive()))
                        .permissions(toDomainPermissions(roleEntity.getRolePermissions()))
                        .build())
                .collect(Collectors.toSet());
    }

    default Set<Permission> toDomainPermissions(Set<RolePermissionEntity> rolePermissions) {
        if (rolePermissions == null || rolePermissions.isEmpty()) {
            return Collections.emptySet();
        }
        return rolePermissions.stream()
                .filter(rp -> Boolean.TRUE.equals(rp.getActive()))
                .map(RolePermissionEntity::getPermission)
                .filter(p -> Boolean.TRUE.equals(p.getActive()))
                .map(this::toDomainPermission)
                .collect(Collectors.toSet());
    }

    default Permission toDomainPermission(PermissionEntity entity) {
        return Permission.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .resource(entity.getResource())
                .action(entity.getAction())
                .scope(entity.getScope())
                .description(entity.getDescription())
                .moduleId(entity.getModule() != null ? entity.getModule().getId() : null)
                .active(Boolean.TRUE.equals(entity.getActive()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
