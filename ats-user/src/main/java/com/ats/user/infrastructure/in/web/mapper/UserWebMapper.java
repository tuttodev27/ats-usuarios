package com.ats.user.infrastructure.in.web.mapper;

import com.ats.user.domain.model.User;
import com.ats.user.infrastructure.in.web.dto.request.CreatedUserRequest;
import com.ats.user.infrastructure.in.web.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports= LocalDateTime.class)
public interface UserWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // se setea después (BCrypt)
    @Mapping(target = "rePasswordHash", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    User toDomain(CreatedUserRequest request);

    @Mapping(target = "createAt", source = "createdAt")
    UserResponse toResponse(User user);
}
