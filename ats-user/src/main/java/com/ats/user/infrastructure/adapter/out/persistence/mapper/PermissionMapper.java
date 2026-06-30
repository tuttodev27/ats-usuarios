package com.ats.user.infrastructure.adapter.out.persistence.mapper;

import com.ats.user.domain.model.Permission;
import com.ats.user.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "moduleId", source = "entity.module.id")
    @Mapping(target = "moduleCode", source = "entity.module.code")
    @Mapping(target = "moduleName", source = "entity.module.name")
    Permission toDomain(PermissionEntity entity);
}
