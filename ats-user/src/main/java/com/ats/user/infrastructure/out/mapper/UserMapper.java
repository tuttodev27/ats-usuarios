package com.ats.user.infrastructure.out.mapper;

import com.ats.user.domain.model.Role;
import com.ats.user.domain.model.User;
import com.ats.user.infrastructure.out.entity.RoleEntity;
import com.ats.user.infrastructure.out.entity.UserEntity;
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
                        .build())
                .collect(Collectors.toSet());
    }
}
