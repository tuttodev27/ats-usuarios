package com.ats.user.infrastructure.out.mapper;

import com.ats.user.domain.model.Module;
import com.ats.user.infrastructure.out.entity.ModuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModuleMapper {

    @Mapping(target = "menus", ignore = true)
    Module toDomain(ModuleEntity entity);
}
