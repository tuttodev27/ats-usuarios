package com.ats.user.infrastructure.in.web.mapper;

import com.ats.user.domain.model.User;
import com.ats.user.infrastructure.in.web.dto.request.UpdateUserRequest;
import com.ats.user.infrastructure.in.web.dto.request.UserRequest;
import com.ats.user.infrastructure.in.web.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.time.LocalDateTime;
import java.util.Set;

@Mapper(componentModel = "spring", imports= LocalDateTime.class)
public interface UserWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // se setea después (BCrypt)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    User toDomain(UserRequest request);

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "roles", expression = "java(mapRoleNames(user))")
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)        // no cambiar email acá
    @Mapping(target = "passwordHash", ignore = true) // no cambiar password acá
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    User toDomain(UpdateUserRequest update);

    default Set<String> mapRoleNames(User user) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return Collections.emptySet();
        }
        return user.getRoles().stream()
                .map(role -> role.getName())
                .filter(name -> name != null && !name.isBlank())
                .collect(java.util.stream.Collectors.toSet());
    }
}
