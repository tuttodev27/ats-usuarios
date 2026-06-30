package com.ats.user.infrastructure.adapter.in.web.mapper;

import com.ats.user.domain.model.Module;
import com.ats.user.infrastructure.adapter.in.web.dto.request.CreateModuleRequest;
import com.ats.user.infrastructure.adapter.in.web.dto.request.UpdateModuleRequest;
import com.ats.user.infrastructure.adapter.in.web.dto.response.ModuleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModuleWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "menus", ignore = true)
    Module toDomain(CreateModuleRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "menus", ignore = true)
    Module toDomain(UpdateModuleRequest request);

    ModuleResponse toResponse(Module module);
}
