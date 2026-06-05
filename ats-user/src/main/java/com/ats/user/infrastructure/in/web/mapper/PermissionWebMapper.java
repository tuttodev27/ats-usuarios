package com.ats.user.infrastructure.in.web.mapper;

import com.ats.user.domain.model.Permission;
import com.ats.user.infrastructure.in.web.dto.response.PermissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionWebMapper {

    @Mapping(target = "moduleCode", source = "moduleCode")
    @Mapping(target = "moduleName", source = "moduleName")
    PermissionResponse toResponse(Permission permission);
}
