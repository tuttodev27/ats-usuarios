package com.ats.user.infrastructure.in.web.mapper;

import com.ats.user.domain.model.Permission;
import com.ats.user.infrastructure.in.web.dto.response.PermissionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionWebMapper {
    PermissionResponse toResponse(Permission permission);
}
